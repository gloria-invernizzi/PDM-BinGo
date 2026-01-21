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
 * Manages Firebase authentication and remote user data operations.
 */
public class UserRemoteSource {

    private static final String TAG = "UserRemoteSource";
    private final FirebaseAuth auth;
    private final Context context;

    public UserRemoteSource(Context context) {
        this.context = context;
        auth = FirebaseAuth.getInstance();
    }

    /**
     * Returns the currently authenticated Firebase user.
     */
    public FirebaseUser getFirebaseUser() {
        return auth.getCurrentUser();
    }

    /**
     * Updates the user's email in Firebase.
     * Requires the user to be verified and sends a verification email before updating.
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
     * Updates the user's password in Firebase after re-authentication.
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
                            Log.d(TAG, "Password updated on Firebase");
                            if (callback != null) callback.onSuccess(context.getString(R.string.password_updated));
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error updating password", e);
                            if (callback != null) callback.onFailure(context.getString(R.string.password_update_error, e.getMessage()));
                        }))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Re-authentication failed", e);
                    if (callback != null) callback.onFailure(context.getString(R.string.reauth_failed, e.getMessage()));
                });
    }

    /**
     * Deletes the user's account from Firebase.
     */
    public void deleteAccount(UserRepository.Callback callback) {
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            callback.onFailure(context.getString(R.string.user_not_authenticated));
            return;
        }

        user.delete()
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "Firebase account deleted");
                    callback.onSuccess(context.getString(R.string.account_deleted));
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting account", e);
                    callback.onFailure(
                            context.getString(R.string.generic_error, e.getMessage())
                    );
                });
    }
}
