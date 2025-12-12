package com.application.bingo.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.application.bingo.PrefsManager;
import com.application.bingo.database.AppDatabase;
import com.application.bingo.database.User;
import com.application.bingo.database.UserDao;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.EmailAuthProvider;

import java.util.concurrent.Executors;

/**
 * UserRepository:
 * - Si occupa di TUTTE le operazioni sui dati utente.
 * - Incapsula database locale (Room) e remoto (Firebase).
 * - Esegue sempre su thread in background.
 *
 * RESPONSABILITÀ:
 *   recupero dell’utente dal database
 *   aggiornamento dell’utente
 *   NON si preoccupa di come è fatto il DB
 *   NON decide quale DAO chiamare (lo fa internamente)
 *   NON gestisce UI
 */
public class UserRepository {

    public static final String PASSWORD_OK = "PASSWORD_OK";

    private final UserDao userDao;
    private final FirebaseAuth mAuth;
    private final Context context;

    public UserRepository(Context context) {
        this.context = context.getApplicationContext(); // salva il context
        userDao = AppDatabase.getInstance(context).userDao();
        mAuth = FirebaseAuth.getInstance();
    }

    // ---------------------------------------------------------------------------------------------
    // CARICA UTENTE (ASYNC) – Ritorna su MAIN THREAD tramite callback
    // ---------------------------------------------------------------------------------------------
    public void getUser(String email, UserCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            PrefsManager prefs = new PrefsManager(context);

            // 1) Provo a leggere da Room
            User u = userDao.findByEmail(email);

            if (u != null) {
                // merge dati Prefs se presenti
                if (u.getName() == null || u.getName().isEmpty()) {
                    String name = prefs.getSavedName();
                    if (!name.isEmpty()) u.setName(name);
                }
                if (u.getAddress() == null || u.getAddress().isEmpty()) {
                    String address = prefs.getSavedAddress();
                    if (!address.isEmpty()) u.setAddress(address);
                }
                if (u.getPhotoUri() == null || u.getPhotoUri().isEmpty()) {
                    u.setPhotoUri(prefs.getSavedPhotoUri());
                }
                Log.d("UserRepo", "User trovato in Room (merge Prefs/Firebase): " + u);
                postToMain(() -> callback.onUserLoaded(u));
                return;
            }

            // 2) PrefsManager
            String nameFromPrefs = prefs.getSavedName();
            String addressFromPrefs = prefs.getSavedAddress();
            String photoUri = prefs.getSavedPhotoUri();

            if (!nameFromPrefs.isEmpty() || !addressFromPrefs.isEmpty() || !photoUri.isEmpty()) {
                User prefsUser = new User(nameFromPrefs, addressFromPrefs, email, "");
                prefsUser.setPhotoUri(photoUri);
                Log.d("UserRepo", "User creato da Prefs: " + prefsUser);
                postToMain(() -> callback.onUserLoaded(prefsUser));
                return;
            }

            // 3) Fallback su FirebaseUser
            FirebaseUser firebaseUser = mAuth.getCurrentUser();
            if (firebaseUser != null && email.equals(firebaseUser.getEmail())) {
                User firebaseFallback = new User(
                        firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "",
                        "",  // address non disponibile in Firebase
                        email,
                        ""
                );
                firebaseFallback.setPhotoUri(""); // inizializza photoUri
                Log.d("UserRepo", "User creato da FirebaseUser: " + firebaseFallback);
                postToMain(() -> callback.onUserLoaded(firebaseFallback));
            } else {
                postToMain(() -> callback.onUserLoaded(null));
            }
        });
    }

    // ---------------------------------------------------------------------------------------------
    // SALVA SU PREFS
    // ---------------------------------------------------------------------------------------------
    public void saveToPrefs(User u) {
        PrefsManager prefs = new PrefsManager(context);
        prefs.saveUser(u.getName(), u.getAddress(), u.getEmail(), u.getPassword());
    }

    public void savePhotoToPrefs(String email, String uri) {
        PrefsManager prefs = new PrefsManager(context);
        prefs.savePhotoUri(email, uri);
    }

    // ---------------------------------------------------------------------------------------------
    // AGGIORNA UTENTE COMPLETO – Async
    // ---------------------------------------------------------------------------------------------
    public void updateUser(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.update(user));
        Log.d("UserRepo", "User aggiornato in Room: " + user);
    }

    // ---------------------------------------------------------------------------------------------
    // AGGIORNA SOLO LA FOTO – Async
    // ---------------------------------------------------------------------------------------------
    public void updatePhotoUri(String email, String uri) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.updatePhotoUri(email, uri));
        Log.d("UserRepo", "Foto aggiornata per " + email + ": " + uri);
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
        // -------------------------------
        // VALIDAZIONE BASE
        // -------------------------------
        if (!newPassword.equals(confirmPassword)) {
            postToMain(() -> callback.onFailure("Le password non corrispondono"));
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                // -------------------------------
                // 1) Recupero utente da Room
                // -------------------------------
                User localUser = userDao.findByEmail(email);
                PrefsManager prefs = new PrefsManager(context);

                if (localUser == null) {
                    String savedPassword = prefs.getSavedPassword();
                    localUser = new User(
                            prefs.getSavedName(),
                            prefs.getSavedAddress(),
                            email,
                            savedPassword != null ? savedPassword : ""
                    );
                    userDao.insert(localUser);
                    Log.d("UserRepo", "User creato in Room da Prefs: " + localUser);
                }

                // -------------------------------
                // LOG DEBUG
                // -------------------------------
                Log.d("UserRepo", "Vecchia password inserita: '" + oldPassword + "'");
                Log.d("UserRepo", "Vecchia password in Room: '" + localUser.getPassword() + "'");

                // -------------------------------
                // 2) Controllo vecchia password locale
                // -------------------------------
                if (!localUser.getPassword().equals(oldPassword)) {
                    postToMain(() -> callback.onFailure("Vecchia password errata"));
                    return;
                }

                // -------------------------------
                // 3) Aggiorna password locale in Room + Prefs
                // -------------------------------
                localUser.setPassword(newPassword);
                userDao.update(localUser);
                prefs.saveUser(localUser.getName(), localUser.getAddress(), email, newPassword);
                Log.d("UserRepo", "Password aggiornata in Room per " + email);

                // Notifica immediata di successo
                Log.d("UserRepo", "Notifico successo cambio password");
                postToMain(() -> callback.onSuccess(UserRepository.PASSWORD_OK));

                // -------------------------------
                // 4) Aggiornamento Firebase se utente loggato
                // -------------------------------
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if (firebaseUser != null && email.equals(firebaseUser.getEmail())) {
                    AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);
                    firebaseUser.reauthenticate(credential)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    firebaseUser.updatePassword(newPassword)
                                            .addOnCompleteListener(updateTask -> {
                                                if (updateTask.isSuccessful()) {
                                                    Log.d("UserRepo", "Password aggiornata anche su Firebase");
                                                } else {
                                                    Log.e("UserRepo", "Errore Firebase durante aggiornamento password", updateTask.getException());
                                                }
                                            });
                                } else {
                                    Log.e("UserRepo", "Autenticazione Firebase fallita", task.getException());
                                }
                            });
                }

            } catch (Exception e) {
                postToMain(() -> callback.onFailure("Errore: " + e.getMessage()));
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

    // ---------------------------------------------------------------------------------------------
    // POSTA SUL MAIN THREAD
    // ---------------------------------------------------------------------------------------------
    private void postToMain(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }
}
