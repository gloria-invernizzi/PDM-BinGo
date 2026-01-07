package com.application.bingo.repository;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Handler;
import android.os.Looper;

import com.application.bingo.datasource.UserLocalSource;
import com.application.bingo.datasource.UserRemoteSource;
import com.application.bingo.model.User;

import java.util.List;

/**
 * UserRepository:
 * Gestisce tutto il flusso di dati utenti
 * - Offline-first per tutti i dati eccetto email/password
 * - Cambio email online-first + logout
 */
public class UserRepository {

    public static final String PASSWORD_OK = "PASSWORD_OK";
    private final Context context;
    private final UserLocalSource local;
    private final UserRemoteSource remote;
    private final Handler mainHandler;

    public UserRepository(Context context) {
        this.context = context;
        local = new UserLocalSource(context);
        remote = new UserRemoteSource();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    // ---------------------------------------------------------------------------------------------
    // EMAIL (aggiornamento robusto: locale solo DOPO successo remoto)
    // ---------------------------------------------------------------------------------------------
    public void changeEmail(String oldEmail,
                            String oldPassword,
                            String newEmail,
                            Callback callback,
                            LogoutCallback logoutCallback) {

        if (!isConnectedToInternet()) {
            postToMain(() -> callback.onFailure("Per cambiare email devi essere connesso a Internet"));
            return;
        }

        // --- Aggiornamento remoto Firebase ---
        remote.updateEmail(newEmail, oldPassword, new Callback() {
            @Override
            public void onSuccess(String msgRemote) {
                // Aggiornamento locale SOLO se Firebase ha aggiornato correttamente
                local.updateEmail(oldEmail, newEmail, new Callback() {
                    @Override
                    public void onSuccess(String msgLocal) {
                        postToMain(() -> {
                            callback.onSuccess(msgRemote);
                            logoutCallback.onLogoutRequired(); // trigger logout soft
                        });
                    }

                    @Override
                    public void onFailure(String msg) {
                        postToMain(() -> callback.onFailure("Aggiornamento locale fallito: " + msg));
                    }
                });
            }

            @Override
            public void onFailure(String msgRemote) {
                postToMain(() -> callback.onFailure("Aggiornamento remoto fallito: " + msgRemote));
            }
        });
    }

    // ---------------------------------------------------------------------------------------------
    // PASSWORD
    // ---------------------------------------------------------------------------------------------
    public void changePassword(String email,
                               String oldPassword,
                               String newPassword,
                               String confirmPassword,
                               Callback callback) {

        if (!newPassword.equals(confirmPassword)) {
            postToMain(() -> callback.onFailure("Le password non corrispondono"));
            return;
        }

        // 1️⃣ Aggiorno remoto prima
        remote.updatePassword(email, oldPassword, newPassword, new Callback() {
            @Override
            public void onSuccess(String msgRemote) {
                // 2️⃣ Aggiorno locale solo se remoto ok
                local.changePassword(email, oldPassword, newPassword, confirmPassword, new Callback() {
                    @Override
                    public void onSuccess(String msgLocal) {
                        postToMain(() -> callback.onSuccess("Password aggiornata correttamente"));
                    }

                    @Override
                    public void onFailure(String msgLocal) {
                        postToMain(() -> callback.onFailure("Errore locale: " + msgLocal));
                    }
                });
            }

            @Override
            public void onFailure(String msgRemote) {
                postToMain(() -> callback.onFailure("Errore remoto: " + msgRemote));
            }
        });
    }

    // ---------------------------------------------------------------------------------------------
    // UTENTI
    // ---------------------------------------------------------------------------------------------
    public void getUser(String email, UserCallback callback) {
        local.getUser(email, user -> postToMain(() -> callback.onUserLoaded(user)));
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

    // ---------------------------------------------------------------------------------------------
    // CALLBACKS
    // ---------------------------------------------------------------------------------------------
    public interface UserCallback { void onUserLoaded(User user); }
    public interface Callback { void onSuccess(String msg); void onFailure(String msg); }
    public interface LogoutCallback { void onLogoutRequired(); }
    public interface FamilyMembersCallback { void onMembersLoaded(List<User> members); }

    // ---------------------------------------------------------------------------------------------
    // UTILS
    // ---------------------------------------------------------------------------------------------
    private void postToMain(Runnable runnable) { mainHandler.post(runnable); }

    public boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
        return nc != null && nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }
}
