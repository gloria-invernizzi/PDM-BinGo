package com.application.bingo.repository;

import android.content.Context;

import com.application.bingo.AppDatabase;
import com.application.bingo.User;
import com.application.bingo.UserDao;

public class UserRepository {
    /*recupero dell’utente dal database
    aggiornamento dell’utente
    non si preoccupa di
     come è fatto il database
    quale DAO chiamare
    come è strutturata la query*/

    private final UserDao userDao;

    public UserRepository(Context context){
        userDao= AppDatabase.getInstance(context).userDao();
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
        AppDatabase.databaseWriteExecutor.execute(() -> {
            User user = userDao.findByEmail(email);

            if (user == null) {
                postToMain(() -> callback.onFailure("Utente non trovato"));
                return;
            }

            if (!user.getPassword().equals(oldPassword)) {
                postToMain(() -> callback.onFailure("Vecchia password errata"));
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                postToMain(() -> callback.onFailure("Le password non corrispondono"));
                return;
            }

            user.setPassword(newPassword);
            userDao.update(user);

            postToMain(() -> callback.onSuccess("Password aggiornata con successo"));
        });
    }

    // Helper per chiamare il callback sul main thread
    private void postToMain(Runnable runnable) {
        new android.os.Handler(android.os.Looper.getMainLooper()).post(runnable);
    }

    /**
     * Interfaccia callback per notificare successi o errori al ViewModel.
     */
    public interface Callback {
        void onSuccess(String message);
        void onFailure(String error);
    }
}
