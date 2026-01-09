package com.application.bingo.datasource;

import android.content.Context;

import com.application.bingo.PrefsManager;
import com.application.bingo.database.AppDatabase;
import com.application.bingo.database.UserDao;
import com.application.bingo.model.User;
import com.application.bingo.repository.UserRepository;

import java.util.List;

/**
 * UserLocalSource:
 * Gestisce tutto ciò che è locale: Room + PrefsManager
 */
public class UserLocalSource {

    private final UserDao userDao;
    private final PrefsManager prefs;

    public UserLocalSource(Context context) {
        userDao = AppDatabase.getInstance(context).userDao();
        prefs = new PrefsManager(context);
    }

    public void getUser(String email, UserRepository.UserCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            User u = userDao.findByEmail(email);
            if (u != null) {
                callback.onUserLoaded(u);
                return;
            }
            // fallback Prefs
            String name = prefs.getSavedName();
            String address = prefs.getSavedAddress();
            String photoUri = prefs.getSavedPhotoUri();
            if (!name.isEmpty() || !address.isEmpty() || !photoUri.isEmpty()) {
                User prefsUser = new User(name, address, email, prefs.getSavedPassword());
                prefsUser.setPhotoUri(photoUri);
                callback.onUserLoaded(prefsUser);
            } else {
                callback.onUserLoaded(null);
            }
        });
    }

    public void updateUser(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userDao.update(user);
            saveToPrefs(user);
        });
    }

    public void updatePhotoUri(String email, String uri) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userDao.updatePhotoUri(email, uri);
            prefs.savePhotoUri(email, uri);
        });
    }

    public void saveToPrefs(User u) {
        prefs.saveUser(u.getName(), u.getAddress(), u.getEmail(), prefs.getSavedPassword());
        if (u.getPhotoUri() != null) prefs.savePhotoUri(u.getEmail(), u.getPhotoUri());
    }

    public void changePassword(String email,
                               String newPassword,
                               UserRepository.Callback callback) {

        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                User localUser = userDao.findByEmail(email);

                if (localUser == null) {
                    localUser = new User(
                            prefs.getSavedName(),
                            prefs.getSavedAddress(),
                            email,
                            newPassword
                    );
                    userDao.insert(localUser);
                } else {
                    localUser.setPassword(newPassword);
                    userDao.update(localUser);
                }

                // salva solo email + password
                prefs.saveUser(email, newPassword);

                callback.onSuccess(UserRepository.PASSWORD_OK);

            } catch (Exception e) {
                callback.onFailure("Errore locale: " + e.getMessage());
            }
        });
    }

    public void updateEmail(String oldEmail, String newEmail, UserRepository.Callback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                User localUser = userDao.findByEmail(oldEmail);
                if (localUser == null) {
                    localUser = new User(prefs.getSavedName(), prefs.getSavedAddress(),
                            oldEmail, prefs.getSavedPassword());
                    userDao.insert(localUser);
                }
                localUser.setEmail(newEmail);
                userDao.update(localUser);
                prefs.updateEmailOnly(newEmail); // solo email, non cancella altri dati

                callback.onSuccess("Email aggiornata localmente");
            } catch (Exception e) {
                callback.onFailure("Errore locale: " + e.getMessage());
            }
        });
    }

    public void updateFamilyId(String email, String familyId, UserRepository.Callback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                userDao.updateFamilyId(email, familyId);
                callback.onSuccess("Famiglia aggiornata con successo");
            } catch (Exception e) {
                callback.onFailure("Errore nell'aggiornamento della famiglia: " + e.getMessage());
            }
        });
    }

    public void getUsersByFamilyId(String familyId, UserRepository.FamilyMembersCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                List<User> members = userDao.findByFamilyId(familyId);
                callback.onMembersLoaded(members);
            } catch (Exception e) {
                callback.onMembersLoaded(null);
            }
        });
    }
}
