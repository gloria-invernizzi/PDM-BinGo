package com.application.bingo.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.application.bingo.util.database.AppDatabase;
import com.application.bingo.util.database.User;
import com.application.bingo.util.database.UserDao;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * UserRepository:
 * - Si occupa di TUTTE le operazioni sui dati utente.
 * - Incapsula database locale (Room) e remoto (Firebase).
 * - Esegue sempre su thread in background.
 *
 * RESPONSABILITÀ:
 *   recupero dell’utente dal database
 *    aggiornamento dell’utente
 *    NON si preoccupa di come è fatto il DB
 *    NON decide quale DAO chiamare (lo fa internamente)
 *    NON gestisce UI
 */
public class UserRepository {

    // Codice di successo UNIVERSALE → il ViewModel capisce quando tutto è andato bene
    public static final String PASSWORD_OK = "PASSWORD_OK";

    private final UserDao userDao;
    private final FirebaseAuth mAuth;

    public UserRepository(Context context) {
        userDao = AppDatabase.getInstance(context).userDao();
        mAuth = FirebaseAuth.getInstance();
    }

    // ---------------------------------------------------------------------------------------------
    // CARICA UTENTE (ASYNC) – Ritorna su MAIN THREAD tramite callback
    // ---------------------------------------------------------------------------------------------
    public void getUser(String email, UserCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            User u = userDao.findByEmail(email);

            if (u != null) {
                postToMain(() -> callback.onUserLoaded(u));
            } else {
                // fallback su Firebase: NON salvare subito in Room
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if (firebaseUser != null && email.equals(firebaseUser.getEmail())) {
                    User fUser = new User(
                            firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "",
                            "", email, ""
                    );

                    // Non salvare ancora in Room per evitare record vuoti
                    postToMain(() -> callback.onUserLoaded(fUser));
                } else {
                    postToMain(() -> callback.onUserLoaded(null));
                }
            }
        });
    }

    /** Callback risultato caricamento utente */
    public interface UserCallback {
        void onUserLoaded(User user);
    }

    // ---------------------------------------------------------------------------------------------
    // AGGIORNA UTENTE COMPLETO – Async
    // ---------------------------------------------------------------------------------------------
    public void updateUser(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.update(user));
    }

    // ---------------------------------------------------------------------------------------------
    // AGGIORNA SOLO LA FOTO – Async
    // ---------------------------------------------------------------------------------------------
    public void updatePhotoUri(String email, String uri) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.updatePhotoUri(email, uri));
    }

    // ---------------------------------------------------------------------------------------------
    // CAMBIO PASSWORD (VALIDAZIONI + ROOM + FIREBASE)
    // ---------------------------------------------------------------------------------------------
    public void changePassword(
            String email,
            String oldPassword,
            String newPassword,
            String confirmPassword,
            Callback callback
    ) {

        // VALIDAZIONE BASE (prima ancora del thread)
        if (!newPassword.equals(confirmPassword)) {
            postToMain(() -> callback.onFailure("Le password non corrispondono"));
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                // -------------------------------------------
                // 1) AGGIORNAMENTO LOCALE (ROOM)
                // -------------------------------------------
                User localUser = userDao.findByEmail(email);

                if (localUser != null) {

                    // Controllo vecchia password nel database locale
                    if (!localUser.getPassword().equals(oldPassword)) {
                        postToMain(() -> callback.onFailure("Vecchia password errata"));
                        return;
                    }

                    // Aggiorna password locale
                    localUser.setPassword(newPassword);
                    userDao.update(localUser);
                }

                // -------------------------------------------
                // 2) AGGIORNAMENTO SU FIREBASE (se utente loggato)
                // -------------------------------------------
                FirebaseUser firebaseUser = mAuth.getCurrentUser();

                if (firebaseUser != null && email.equals(firebaseUser.getEmail())) {

                    // Prima serve la ri-autenticazione
                    com.google.firebase.auth.AuthCredential credential =
                            com.google.firebase.auth.EmailAuthProvider.getCredential(email, oldPassword);

                    firebaseUser.reauthenticate(credential)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {

                                    // Ora Firebase permette l'update password
                                    firebaseUser.updatePassword(newPassword)
                                            .addOnCompleteListener(updateTask -> {
                                                if (updateTask.isSuccessful()) {

                                                    // SUCCESSO TOTALE (locale + Firebase)
                                                    postToMain(() -> callback.onSuccess(PASSWORD_OK));

                                                } else {
                                                    postToMain(() -> callback.onFailure("Errore Firebase durante l'aggiornamento password"));
                                                }
                                            });

                                } else {
                                    postToMain(() -> callback.onFailure("Autenticazione Firebase fallita"));
                                }
                            });

                } else {
                    // Utente NON loggato su Firebase → ma locale aggiornata
                    postToMain(() -> callback.onSuccess(PASSWORD_OK));
                }

            } catch (Exception e) {
                postToMain(() -> callback.onFailure("Errore: " + e.getMessage()));
            }
        });
    }

    // Callback generica usata dal ViewModel
    public interface Callback {
        void onSuccess(String msg);
        void onFailure(String msg);
    }

    // ---------------------------------------------------------------------------------------------
    // POSTA SUL MAIN THREAD
    // ---------------------------------------------------------------------------------------------
    private void postToMain(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }
}
