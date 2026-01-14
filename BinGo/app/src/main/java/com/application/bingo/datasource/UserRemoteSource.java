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

        if (user == null) {
            callback.onFailure("Utente non autenticato");
            return;
        }

        if (!user.isEmailVerified()) {
            user.sendEmailVerification();
            callback.onFailure("Devi prima verificare la tua email attuale.");
            return;
        }

        user.verifyBeforeUpdateEmail(newEmail)
                .addOnSuccessListener(v -> callback.onSuccess("Email di verifica inviata alla nuova casella."))
                .addOnFailureListener(e -> callback.onFailure("Errore: " + e.getMessage()));
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
