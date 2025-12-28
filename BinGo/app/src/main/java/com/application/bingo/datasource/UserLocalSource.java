package com.application.bingo.datasource;

import android.content.Context;
import android.util.Log;

import com.application.bingo.PrefsManager;
import com.application.bingo.database.AppDatabase;
import com.application.bingo.model.User;
import com.application.bingo.database.UserDao;
import com.application.bingo.repository.UserRepository;

import java.util.concurrent.ExecutorService;
import java.util.List;

/**
 * UserLocalSource:
 * Gestisce tutto ciò che è locale: Room + PrefsManager
 */
public class UserLocalSource {

    private final UserDao userDao;
    private final PrefsManager prefs;
    private final ExecutorService executor;

    public UserLocalSource(Context context) {
        userDao = AppDatabase.getInstance(context).userDao();
        prefs = new PrefsManager(context);
        executor = AppDatabase.databaseWriteExecutor;
    }

    // Recupera utente da Room e Prefs
    public void getUser(String email, UserRepository.UserCallback callback) {
        executor.execute(() -> {
            User u = userDao.findByEmail(email);

            if (u != null) {
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
                Log.d("UserLocalSource", "User trovato in Room (merge Prefs): " + u);
                callback.onUserLoaded(u);
                return;
            }

            // fallback da Prefs
            String nameFromPrefs = prefs.getSavedName();
            String addressFromPrefs = prefs.getSavedAddress();
            String photoUri = prefs.getSavedPhotoUri();
            if (!nameFromPrefs.isEmpty() || !addressFromPrefs.isEmpty() || !photoUri.isEmpty()) {
                User prefsUser = new User(nameFromPrefs, addressFromPrefs, email, "");
                prefsUser.setPhotoUri(photoUri);
                Log.d("UserLocalSource", "User creato da Prefs: " + prefsUser);
                callback.onUserLoaded(prefsUser);
            } else {
                callback.onUserLoaded(null);
            }
        });
    }

    // Aggiorna utente in Room
    public void updateUser(User user) {
        executor.execute(() -> {
            userDao.update(user);
            Log.d("UserLocalSource", "User aggiornato in Room: " + user);
        });
    }

    // Aggiorna solo la foto
    public void updatePhotoUri(String email, String uri) {
        executor.execute(() -> {
            userDao.updatePhotoUri(email, uri);// aggiorna Room
            prefs.savePhotoUri(email, uri);
            Log.d("UserLocalSource", "Foto aggiornata in Room e Prefs per " + email);
        });
    }

    // Salva utente nei Prefs
    public void saveToPrefs(User u) {
        prefs.saveUser(u.getName(), u.getAddress(), u.getEmail(), u.getPassword());
    }

    // Cambio password locale
    public void changePassword(String email, String oldPassword, String newPassword,
                               String confirmPassword, UserRepository.Callback callback) {
        executor.execute(() -> {
            try {
                if (!newPassword.equals(confirmPassword)) {
                    callback.onFailure("Le password non corrispondono");
                    return;
                }

                User localUser = userDao.findByEmail(email);
                if (localUser == null) {
                    localUser = new User(prefs.getSavedName(), prefs.getSavedAddress(),
                            email, prefs.getSavedPassword());
                    userDao.insert(localUser);
                    Log.d("UserLocalSource", "User creato in Room da Prefs: " + localUser);
                }

                if (!localUser.getPassword().equals(oldPassword)) {
                    callback.onFailure("Vecchia password errata");
                    return;
                }

                localUser.setPassword(newPassword);
                userDao.update(localUser);
                prefs.saveUser(localUser.getName(), localUser.getAddress(), email, newPassword);
                Log.d("UserLocalSource", "Password aggiornata in Room per " + email);

                callback.onSuccess(UserRepository.PASSWORD_OK);

            } catch (Exception e) {
                callback.onFailure("Errore: " + e.getMessage());
            }
        });
    }
    // In UserLocalSource
    public void updateEmail(String oldEmail, String newEmail, UserRepository.Callback callback) {
        executor.execute(() -> {
            try {
                User localUser = userDao.findByEmail(oldEmail);
                if (localUser != null) {
                    localUser.setEmail(newEmail);
                    userDao.update(localUser);

                    // Aggiorna SharedPreferences con la nuova email
                    prefs.saveUser(
                            localUser.getName(),
                            localUser.getAddress(),
                            newEmail,// nuova email
                            localUser.getPassword()
                    );

                    callback.onSuccess(UserRepository.PASSWORD_OK);
                } else {
                    callback.onFailure("Utente locale non trovato");
                }
            } catch (Exception e) {
                callback.onFailure("Errore: " + e.getMessage());
            }
        });
    }

    // In UserLocalSource
    public void updateFamilyId(String email, String familyId, UserRepository.Callback callback) {
        executor.execute(() -> {
            try {
                userDao.updateFamilyId(email, familyId);
                callback.onSuccess("Famiglia aggiornata con successo");
            } catch (Exception e) {
                callback.onFailure("Errore nell'aggiornamento della famiglia: " + e.getMessage());
            }
        });
    }

    public void getUsersByFamilyId(String familyId, UserRepository.FamilyMembersCallback callback) {
        executor.execute(() -> {
            try {
                List<User> members = userDao.findByFamilyId(familyId);
                callback.onMembersLoaded(members);
            } catch (Exception e) {
                // Gestione errore o lista vuota
                callback.onMembersLoaded(null);
            }
        });
    }
}
