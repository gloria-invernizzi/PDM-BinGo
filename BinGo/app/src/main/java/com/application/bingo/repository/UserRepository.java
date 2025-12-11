package com.application.bingo.repository;

import android.content.Context;

import com.application.bingo.database.AppDatabase;
import com.application.bingo.database.User;
import com.application.bingo.database.UserDao;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserRepository {
    /*recupero dell’utente dal database
    aggiornamento dell’utente
    non si preoccupa di
     come è fatto il database
    quale DAO chiamare
    come è strutturata la query*/

    private final UserDao userDao;
    private final FirebaseAuth mAuth;
    public UserRepository(Context context){
        userDao = AppDatabase.getInstance(context).userDao();
        mAuth = FirebaseAuth.getInstance();
    }
    public User getUser(String email){
        return userDao.findByEmail(email);
    }

    public void updateUser(User user){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userDao.update(user);  // questa operazione va su un thread separato
        });

    }


    /**
     * Cambia la password di un utente.
     * Tutta la logica di validazione rimane qui nel repository.
     */
    public void changePassword(String email, String oldPassword, String newPassword, String confirmPassword, Callback callback) {
        if (!newPassword.equals(confirmPassword)) {
            postToMain(() -> callback.onFailure("Le password non corrispondono"));
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                // 1. Gestione Room locale
                User localUser = userDao.findByEmail(email);

                if (localUser != null) {
                    if (!localUser.getPassword().equals(oldPassword)) {
                        postToMain(() -> callback.onFailure("Vecchia password errata"));
                        return;
                    }
                    localUser.setPassword(newPassword);
                    userDao.update(localUser);
                    postToMain(() -> callback.onSuccess("Password aggiornata con successo (locale)"));
                }

                // 2. Gestione Firebase se l’utente è loggato
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if (firebaseUser != null && email.equals(firebaseUser.getEmail())) {
                    com.google.firebase.auth.AuthCredential credential =
                            com.google.firebase.auth.EmailAuthProvider.getCredential(email, oldPassword);

                    firebaseUser.reauthenticate(credential)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    firebaseUser.updatePassword(newPassword)
                                            .addOnCompleteListener(updateTask -> {
                                                if (updateTask.isSuccessful()) {
                                                    postToMain(() -> callback.onSuccess("Password aggiornata anche su Firebase"));
                                                } else {
                                                    String msg = updateTask.getException() != null ? updateTask.getException().getMessage() : "Errore aggiornamento password Firebase";
                                                    postToMain(() -> callback.onFailure(msg));
                                                }
                                            });
                                } else {
                                    String msg = task.getException() != null ? task.getException().getMessage() : "Autenticazione Firebase fallita";
                                    postToMain(() -> callback.onFailure(msg));
                                }
                            });
                } else {
                    // Utente Firebase non loggato, ma cambio locale riuscito
                    if (localUser != null) {
                        postToMain(() -> callback.onSuccess("Password aggiornata solo localmente"));
                    }
                }
            } catch (Exception e) {
                postToMain(() -> callback.onFailure("Errore imprevisto: " + e.getMessage()));
            }
        });
    }

    // Helper per chiamare il callback sul main thread
    private void postToMain(Runnable runnable) {
        new android.os.Handler(android.os.Looper.getMainLooper()).post(runnable);
    }

    /**
     * Callback per notificare successi o errori al ViewModel.
     */
    public interface Callback {
        void onSuccess(String message);
        void onFailure(String error);
    }
    // Aggiorna la URI della foto di un utente
    public void updatePhotoUri(String email, String uri) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userDao.updatePhotoUri(email, uri);
        });
    }
}