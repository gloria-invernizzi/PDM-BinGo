package com.application.bingo.datasource;

import android.util.Log;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * UserRemoteSource:
 * Gestisce tutto ciò che è remoto: FirebaseAuth
 */
public class UserRemoteSource {

    private final FirebaseAuth mAuth;

    public UserRemoteSource() {
        mAuth = FirebaseAuth.getInstance();
    }

    // Recupera l'utente Firebase corrente
    public FirebaseUser getFirebaseUser() {
        return mAuth.getCurrentUser();
    }

    // Aggiorna password su Firebase
    public void updatePassword(String email, String oldPassword, String newPassword) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null && email.equals(firebaseUser.getEmail())) {
            AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);
            firebaseUser.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            firebaseUser.updatePassword(newPassword)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            Log.d("UserRemoteSource", "Password aggiornata anche su Firebase");
                                        } else {
                                            Log.e("UserRemoteSource", "Errore Firebase update password", updateTask.getException());
                                        }
                                    });
                        } else {
                            Log.e("UserRemoteSource", "Autenticazione Firebase fallita", task.getException());
                        }
                    });
        }
    }
}
