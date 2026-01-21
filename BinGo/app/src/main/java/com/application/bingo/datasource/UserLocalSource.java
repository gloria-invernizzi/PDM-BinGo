package com.application.bingo.datasource;

import android.content.Context;
import android.util.Log;

import com.application.bingo.PrefsManager;
import com.application.bingo.R;
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
    private final Context context;

    private final UserDao userDao;
    private final PrefsManager prefs;

    public UserLocalSource(Context context) {
        this.context = context;
        userDao = AppDatabase.getInstance(context).userDao();
        prefs = new PrefsManager(context);
    }

    public void getUser(String email, UserRepository.UserCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Log.d("UserLocalSource", "getUser chiamato per email = " + email);
            User u = userDao.findByEmail(email);
            if (u != null) {
                Log.d("UserLocalSource", "Trovato in Room: " + u);
                callback.onUserLoaded(u);
                return;
            }
            // fallback Prefs
            Log.d("UserLocalSource", "Non trovato in Room, provo prefs");
            String name = prefs.getSavedName();
            String surname = prefs.getSavedSurname();
            String address = prefs.getSavedAddress();
            String photoUri = prefs.getSavedPhotoUri();
            Log.d("UserLocalSource", "Prefs read: name=" + name + ", surname=" + surname
                    + ", address=" + address + ", photoUri=" + photoUri);
            if (!name.isEmpty() || !address.isEmpty() || !photoUri.isEmpty()) {
                User prefsUser = new User(name, surname, address, email, prefs.getSavedPassword());
                prefsUser.setPhotoUri(photoUri);
                Log.d("UserLocalSource", "Creo utente dai prefs: " + prefsUser);
                callback.onUserLoaded(prefsUser);
            } else {
                Log.d("UserLocalSource", "Prefs vuoti, ritorno null");
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
        prefs.saveUser(u.getName(), u.getSurname(), u.getAddress(), u.getEmail(), prefs.getSavedPassword());
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
                            prefs.getSavedSurname(),
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
                callback.onFailure(context.getString(R.string.local_error, e.getMessage()));

            }
        });
    }

    public void updateEmail(String oldEmail, String newEmail, UserRepository.Callback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                User localUser = userDao.findByEmail(oldEmail);
                if (localUser == null) {
                    localUser = new User(prefs.getSavedName(), prefs.getSavedSurname(), prefs.getSavedAddress(),
                            oldEmail, prefs.getSavedPassword());
                    userDao.insert(localUser);
                }
                localUser.setEmail(newEmail);
                userDao.update(localUser);
                prefs.updateEmailOnly(newEmail); // solo email, non cancella altri dati

                callback.onSuccess(context.getString(R.string.email_updated));

            } catch (Exception e) {
                callback.onFailure(context.getString(R.string.local_error, e.getMessage()));


            }
        });
    }

    public void updateFamilyId(String email, String familyId, UserRepository.Callback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                userDao.updateFamilyId(email, familyId);
                callback.onSuccess(context.getString(R.string.family_updated_success));

            } catch (Exception e) {
                callback.onFailure(context.getString(R.string.family_update_error, e.getMessage()));

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
    public void deleteLocalUser(String email) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                userDao.deleteByEmail(email);
            } catch (Exception ignored) {}

            prefs.clearAll();// pulizia totale prefs
        });
    }

}
