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
 * Manages all local data operations using Room and PrefsManager.
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

    /**
     * Retrieves a user by email, checking Room first and falling back to SharedPreferences.
     *
     * @param email    The user's email.
     * @param callback Result callback.
     */
    public void getUser(String email, UserRepository.UserCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Log.d("UserLocalSource", "getUser called for email = " + email);
            User u = userDao.findByEmail(email);
            if (u != null) {
                Log.d("UserLocalSource", "Found in Room: " + u);
                callback.onUserLoaded(u);
                return;
            }
            
            // Fallback to Prefs if not in Room
            Log.d("UserLocalSource", "Not found in Room, checking prefs");
            String name = prefs.getSavedName();
            String surname = prefs.getSavedSurname();
            String address = prefs.getSavedAddress();
            String photoUri = prefs.getSavedPhotoUri();
            Log.d("UserLocalSource", "Prefs read: name=" + name + ", surname=" + surname
                    + ", address=" + address + ", photoUri=" + photoUri);
            
            if (!name.isEmpty() || !address.isEmpty() || !photoUri.isEmpty()) {
                User prefsUser = new User(name, surname, address, email, prefs.getSavedPassword());
                prefsUser.setPhotoUri(photoUri);
                Log.d("UserLocalSource", "Creating user from prefs: " + prefsUser);
                callback.onUserLoaded(prefsUser);
            } else {
                Log.d("UserLocalSource", "Prefs empty, returning null");
                callback.onUserLoaded(null);
            }
        });
    }

    /**
     * Updates user data in both Room and SharedPreferences.
     */
    public void updateUser(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userDao.update(user);
            saveToPrefs(user);
        });
    }

    /**
     * Updates only the user's photo URI in both Room and SharedPreferences.
     */
    public void updatePhotoUri(String email, String uri) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userDao.updatePhotoUri(email, uri);
            prefs.savePhotoUri(email, uri);
        });
    }

    /**
     * Saves user data to SharedPreferences.
     */
    public void saveToPrefs(User u) {
        prefs.saveUser(u.getName(), u.getSurname(), u.getAddress(), u.getEmail(), prefs.getSavedPassword());
        if (u.getPhotoUri() != null) prefs.savePhotoUri(u.getEmail(), u.getPhotoUri());
    }

    /**
     * Updates the password in the local database and SharedPreferences.
     */
    public void changePassword(String email, String newPassword, UserRepository.Callback callback) {
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

                // Save only email and password to prefs for security/simplicity
                prefs.saveUser(email, newPassword);
                callback.onSuccess(UserRepository.PASSWORD_OK);

            } catch (Exception e) {
                callback.onFailure(context.getString(R.string.local_error, e.getMessage()));
            }
        });
    }

    /**
     * Updates the user's email address in the local database and SharedPreferences.
     */
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
                prefs.updateEmailOnly(newEmail); // Updates email only, preserves other data

                callback.onSuccess(context.getString(R.string.email_updated));

            } catch (Exception e) {
                callback.onFailure(context.getString(R.string.local_error, e.getMessage()));
            }
        });
    }

    /**
     * Updates the family ID for a user in the local database.
     */
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

    /**
     * Retrieves all family members associated with a specific family ID.
     */
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

    /**
     * Deletes user data from the local database and clears all SharedPreferences.
     */
    public void deleteLocalUser(String email) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                userDao.deleteByEmail(email);
            } catch (Exception ignored) {}

            prefs.clearAll(); // Total cleanup of local preferences
        });
    }
}
