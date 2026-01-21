package com.application.bingo.repository;

import android.content.Context;

import com.application.bingo.R;
import com.application.bingo.database.AppDatabase;
import com.application.bingo.database.UserDao;
import com.application.bingo.datasource.UserLocalSource;
import com.application.bingo.datasource.UserRemoteSource;
import com.application.bingo.model.User;
import com.application.bingo.util.NetworkUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * UserRepository:
 * Single entry point for user data management, coordinating local (Room/Prefs) 
 * and remote (Firebase) sources.
 */
public class UserRepository {
    private final UserLocalSource localSource;
    private final UserRemoteSource remoteSource;
    private final UserDao userDao;
    private final FirebaseAuth mAuth;
    private final ExecutorService executor;
    private final Context context;

    public static final String PASSWORD_OK = "PASSWORD_OK";

    /**
     * Standard callback for operations that can succeed or fail with a message.
     */
    public interface Callback {
        void onSuccess(String message);
        void onFailure(String error);
    }

    /**
     * Interface for user loading results.
     */
    public interface UserCallback {
        void onUserLoaded(User user);
        void onFailure(String error);
    }

    /**
     * Callback for Google authentication verification.
     */
    @FunctionalInterface
    public interface GoogleCheckCallback {
        void onResult(boolean isGoogleUser);
    }

    /**
     * Callback for family members list retrieval.
     */
    @FunctionalInterface
    public interface FamilyMembersCallback {
        void onMembersLoaded(List<User> members);
    }

    /**
     * Callback for signaling that a logout is required (e.g., after email change).
     */
    @FunctionalInterface
    public interface LogoutCallback {
        void onLogoutRequired();
    }

    /**
     * Generic callback for repository operations.
     */
    @FunctionalInterface
    public interface RepositoryCallback<T> {
        void onComplete(T result);
    }

    public UserRepository(Context context) {
        this.context = context.getApplicationContext();
        this.localSource = new UserLocalSource(this.context);
        this.remoteSource = new UserRemoteSource(this.context);
        this.userDao = AppDatabase.getInstance(this.context).userDao();
        this.mAuth = FirebaseAuth.getInstance();
        this.executor = Executors.newFixedThreadPool(4);
    }

    /**
     * Returns the email of the currently logged-in user or null if not authenticated.
     */
    public String getCurrentUserEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        return (user != null) ? user.getEmail() : null;
    }

    /**
     * Checks if internet connectivity is available.
     */
    public boolean isInternetAvailable() {
        return NetworkUtil.isInternetAvailable(context);
    }

    /**
     * Utility method for ViewModels to check internet connectivity.
     */
    public boolean isConnectedToInternet() {
        return isInternetAvailable();
    }

    /**
     * Checks if the current user is authenticated via Google.
     * 
     * @param email    The email to check (maintained for API compatibility).
     * @param callback Result callback.
     */
    public void isGoogleUser(String email, GoogleCheckCallback callback) {
        FirebaseUser user = mAuth.getCurrentUser();
        boolean isGoogle = false;
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                if (GoogleAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                    isGoogle = true;
                    break;
                }
            }
        }
        callback.onResult(isGoogle);
    }

    /**
     * Updates the user's password both in Firebase and locally.
     */
    public void changePassword(String email, String oldPwd, String newPwd, String confirmPwd, Callback callback) {
        if (!newPwd.equals(confirmPwd)) {
            callback.onFailure(context.getString(R.string.passwords_do_not_match));
            return;
        }

        remoteSource.updatePassword(email, oldPwd, newPwd, new Callback() {
            @Override
            public void onSuccess(String message) {
                localSource.changePassword(email, newPwd, callback);
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    /**
     * Updates the user's email address in Firebase and locally.
     * Triggers a logout if the remote update is successful.
     */
    public void changeEmail(String oldEmail, String oldPassword, String newEmail, Callback callback, LogoutCallback logoutCallback) {
        remoteSource.updateEmail(newEmail, oldPassword, new Callback() {
            @Override
            public void onSuccess(String message) {
                localSource.updateEmail(oldEmail, newEmail, new Callback() {
                    @Override
                    public void onSuccess(String msg) {
                        if (callback != null) callback.onSuccess(message);
                        if (logoutCallback != null) logoutCallback.onLogoutRequired();
                    }

                    @Override
                    public void onFailure(String msg) {
                        if (callback != null) callback.onFailure(
                                context.getString(R.string.email_updated_firebase_not_local, msg));
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                if (callback != null) callback.onFailure(error);
            }
        });
    }

    /**
     * Searches for a user in the local database by email and password.
     */
    public void findLocalUser(String email, String password, RepositoryCallback<User> callback) {
        executor.execute(() -> {
            User user = userDao.findByEmailAndPassword(email, password);
            callback.onComplete(user);
        });
    }

    /**
     * Saves a user to the local database if they do not already exist.
     */
    public void saveLocalUser(User user, Runnable onComplete) {
        executor.execute(() -> {
            User existing = userDao.findByEmail(user.getEmail());
            if (existing == null) {
                userDao.insert(user);
            }
            if (onComplete != null) onComplete.run();
        });
    }

    /**
     * Retrieves user details from the local data source.
     */
    public void getUser(String email, UserCallback callback) {
        localSource.getUser(email, new UserCallback() {
            @Override
            public void onUserLoaded(User user) {
                callback.onUserLoaded(user);
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    /**
     * Updates user details in the local data source.
     */
    public void updateUser(User user) {
        localSource.updateUser(user);
    }

    /**
     * Updates the user's profile photo URI in the local source.
     */
    public void updatePhotoUri(String email, String uri) {
        localSource.updatePhotoUri(email, uri);
    }

    /**
     * Saves user data to SharedPreferences via the local source.
     */
    public void saveToPrefs(User user) {
        localSource.saveToPrefs(user);
    }

    /**
     * Updates the family ID for a given user locally.
     */
    public void updateFamilyId(String email, String familyId, Callback callback) {
        localSource.updateFamilyId(email, familyId, callback);
    }

    /**
     * Retrieves all users belonging to a specific family ID.
     */
    public void getUsersByFamilyId(String familyId, FamilyMembersCallback callback) {
        localSource.getUsersByFamilyId(familyId, callback);
    }

    /**
     * Authenticates a user using Firebase with email and password.
     */
    public Task<AuthResult> firebaseSignIn(String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password);
    }

    /**
     * Authenticates a user using Firebase with provided credentials (e.g., Google).
     */
    public Task<AuthResult> firebaseSignInWithCredential(AuthCredential credential) {
        return mAuth.signInWithCredential(credential);
    }

    /**
     * Deletes the user's account from both Firebase and the local database.
     */
    public void deleteAccount(Callback callback) {
        if (!isInternetAvailable()) {
            callback.onFailure(context.getString(R.string.delete_account_offline_error));
            return;
        }

        remoteSource.deleteAccount(new Callback() {
            @Override
            public void onSuccess(String msg) {
                // Remove user data locally upon remote success
                localSource.deleteLocalUser(remoteSource.getFirebaseUser().getEmail());
                callback.onSuccess(context.getString(R.string.delete_account_confirmation_message));
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }
}
