package com.application.bingo.datasource;

import android.content.Context;
import android.util.Log;

import com.application.bingo.R;
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

    private final Context context;
    public UserRemoteSource(Context context) {
        this.context = context;
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
            callback.onFailure(context.getString(R.string.user_not_authenticated));
            return;
        }

        if (!user.isEmailVerified()) {
            user.sendEmailVerification();
            callback.onFailure(context.getString(R.string.verify_email_first));
            return;
        }

        user.verifyBeforeUpdateEmail(newEmail)
                .addOnSuccessListener(v -> callback.onSuccess(context.getString(R.string.verification_email_sent)))
                .addOnFailureListener(e -> callback.onFailure(context.getString(R.string.generic_error, e.getMessage())));
    }

    /**
     * Aggiorna la password con callback opzionale
     */
    public void updatePassword(String email, String oldPassword, String newPassword, UserRepository.Callback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            if (callback != null) callback.onFailure(context.getString(R.string.user_not_authenticated));

            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);

        user.reauthenticate(credential)
                .addOnSuccessListener(unused -> user.updatePassword(newPassword)
                        .addOnSuccessListener(v -> {
                            Log.d(TAG, "Password aggiornata su Firebase");
                            if (callback != null) callback.onSuccess(context.getString(R.string.password_updated));
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Errore update password", e);
                            if (callback != null) callback.onFailure(context.getString(R.string.password_update_error, e.getMessage()));
                        }))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Re-auth fallita", e);
                    if (callback != null) callback.onFailure(context.getString(R.string.reauth_failed, e.getMessage()));
                });
    }
    /**
     * Elimina account
     */
    public void deleteAccount(UserRepository.Callback callback) {
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            callback.onFailure(context.getString(R.string.user_not_authenticated));
            return;
        }

        user.delete()
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "Account Firebase eliminato");
                    callback.onSuccess(context.getString(R.string.account_deleted));
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Errore eliminazione account", e);
                    callback.onFailure(
                            context.getString(R.string.generic_error, e.getMessage())
                    );
                });
    }



}
