package com.application.bingo.datasource;

import android.util.Log;

import com.application.bingo.repository.UserRepository;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * UserRemoteSource:
 * Gestisce le chiamate Firebase
 */
public class UserRemoteSource {

    private static final String TAG = "UserRemoteSource";
    private final FirebaseAuth auth;

    public UserRemoteSource() {
        auth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getFirebaseUser() {
        return auth.getCurrentUser();
    }

    /**
     * Aggiorna l'email in Firebase con re-auth
     */
    public void updateEmail(String newEmail, String password, UserRepository.Callback callback) {
        FirebaseUser user = auth.getCurrentUser();
        Log.d(TAG, "updateEmail chiamato. currentUser = " + user);

        if (user != null) {
            Log.d(TAG, "Email corrente: " + user.getEmail());
        } else {
            Log.e(TAG, "updateEmail: utente non autenticato!");
            callback.onFailure("Utente non autenticato");
            return;
        }

        user.reload().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Errore reload utente", task.getException());
                callback.onFailure("Errore reload utente: " + task.getException().getMessage());
                return;
            }

            if (!user.isEmailVerified()) {
                Log.d(TAG, "Email non verificata. Invio mail di verifica...");
                user.sendEmailVerification()
                        .addOnSuccessListener(v -> Log.d(TAG, "Mail di verifica inviata correttamente"))
                        .addOnFailureListener(e -> Log.e(TAG, "Errore invio mail di verifica", e));
                callback.onFailure("Devi prima verificare la tua email corrente. Controlla la tua casella di posta.");
                return;
            }

            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
            user.reauthenticate(credential)
                    .addOnSuccessListener(v -> {
                        Log.d(TAG, "Re-auth completata. Aggiornamento email...");
                        user.updateEmail(newEmail)
                                .addOnSuccessListener(unused -> {
                                    Log.d(TAG, "Email aggiornata su Firebase: " + newEmail);
                                    user.sendEmailVerification();
                                    callback.onSuccess("Email aggiornata con successo. Verifica la nuova email!");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Errore updateEmail", e);
                                    callback.onFailure("Errore updateEmail: " + e.getMessage());
                                });
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Re-auth fallita", e);
                        callback.onFailure("Re-auth fallita: " + e.getMessage());
                    });
        });
    }

    /**
     * Aggiorna la password con callback opzionale
     */
    public void updatePassword(String email, String oldPassword, String newPassword, UserRepository.Callback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            if (callback != null) callback.onFailure("Utente non autenticato");
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);

        user.reauthenticate(credential)
                .addOnSuccessListener(unused -> user.updatePassword(newPassword)
                        .addOnSuccessListener(v -> {
                            Log.d(TAG, "Password aggiornata su Firebase");
                            if (callback != null) callback.onSuccess("Password aggiornata su Firebase");
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Errore update password", e);
                            if (callback != null) callback.onFailure("Errore update password: " + e.getMessage());
                        }))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Re-auth fallita", e);
                    if (callback != null) callback.onFailure("Re-auth fallita: " + e.getMessage());
                });
    }
}
