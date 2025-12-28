package com.application.bingo.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.application.bingo.model.User;
import com.application.bingo.datasource.UserLocalSource;
import com.application.bingo.datasource.UserRemoteSource;

import java.util.List;

/**
 * UserRepository:
 * Espone gli stessi metodi del repository originale,
 * ma ora delega le operazioni ai datasource separati.
 */
public class UserRepository {

    public static final String PASSWORD_OK = "PASSWORD_OK";

    private final UserLocalSource local;
    private final UserRemoteSource remote;
    private final Handler mainHandler;

    public UserRepository(Context context) {
        local = new UserLocalSource(context);
        remote = new UserRemoteSource();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    // Recupera utente (merge dati locali e fallback Firebase)
    public void getUser(String email, UserCallback callback) {
        local.getUser(email, user -> {
            if (user != null) {
                postToMain(() -> callback.onUserLoaded(user));
            } else {
                // fallback su FirebaseUser
                if (remote.getFirebaseUser() != null &&
                        email.equals(remote.getFirebaseUser().getEmail())) {
                    User firebaseFallback = new User(
                            remote.getFirebaseUser().getDisplayName() != null
                                    ? remote.getFirebaseUser().getDisplayName() : "",
                            "",
                            email,
                            ""
                    );
                    firebaseFallback.setPhotoUri("");
                    postToMain(() -> callback.onUserLoaded(firebaseFallback));
                } else {
                    postToMain(() -> callback.onUserLoaded(null));
                }
            }
        });
    }

    public void updateUser(User user) {
        local.updateUser(user);
    }

    public void updatePhotoUri(String email, String uri) {
        local.updatePhotoUri(email, uri);
    }

    public void saveToPrefs(User u) {
        local.saveToPrefs(u);
    }

    // Cambio password locale + remoto
    public void changePassword(String email, String oldPassword, String newPassword,
                               String confirmPassword, Callback callback) {
        local.changePassword(email, oldPassword, newPassword, confirmPassword, new Callback() {
            @Override
            public void onSuccess(String msg) {
                postToMain(() -> callback.onSuccess(msg));
                remote.updatePassword(email, oldPassword, newPassword);
            }

            @Override
            public void onFailure(String msg) {
                postToMain(() -> callback.onFailure(msg));
            }
        });
    }

    // ---------------------------------------------------------------------------------------------
    // CALLBACKS
    // ---------------------------------------------------------------------------------------------
    public interface UserCallback {
        void onUserLoaded(User user);
    }

    public interface Callback {
        void onSuccess(String msg);
        void onFailure(String msg);
    }

    public interface FamilyMembersCallback {
        void onMembersLoaded(List<User> members);
    }

    private void postToMain(Runnable runnable) {
        mainHandler.post(runnable);
    }

    public void changeEmail(String oldEmail, String newEmail, Callback callback) {
        // Aggiornamento locale
        local.updateEmail(oldEmail, newEmail, new Callback() {
            @Override
            public void onSuccess(String msgLocal) {
                // Aggiornamento remoto
                remote.updateEmail(newEmail, new Callback() {
                    @Override
                    public void onSuccess(String msgRemote) {
                        // Successo completo
                        postToMain(() -> callback.onSuccess("Email aggiornata con successo"));
                    }

                    @Override
                    public void onFailure(String msg) {
                        postToMain(() -> callback.onFailure("Errore remoto: " + msg));
                    }
                });
            }

            @Override
            public void onFailure(String msg) {
                postToMain(() -> callback.onFailure("Errore locale: " + msg));
            }
        });
    }

    public void updateFamilyId(String email, String familyId, Callback callback) {
        local.updateFamilyId(email, familyId, new Callback() {
            @Override
            public void onSuccess(String msg) {
                postToMain(() -> callback.onSuccess(msg));
            }

            @Override
            public void onFailure(String msg) {
                postToMain(() -> callback.onFailure(msg));
            }
        });
    }

    public void getUsersByFamilyId(String familyId, FamilyMembersCallback callback) {
        local.getUsersByFamilyId(familyId, members -> postToMain(() -> callback.onMembersLoaded(members)));
    }
}
