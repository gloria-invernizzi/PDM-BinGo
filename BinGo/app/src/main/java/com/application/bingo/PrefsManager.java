package com.application.bingo;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManager {
    // Shared Preferences file name and keys
    private static final String PREFS_NAME = "bingo_prefs";
    private static final String KEY_NAME = "user_name";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_LOGGED = "is_logged";

    private final SharedPreferences prefs;

    // Constructor to initialize SharedPreferences
    public PrefsManager(Context ctx) {
        prefs = ctx.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Save user information and login status
    public void saveUser(String name, String email) {
        prefs.edit()
                .putString(KEY_NAME, name)
                .putString(KEY_EMAIL, email)
                .putBoolean(KEY_LOGGED, true)
                .apply();
    }

    public void clear() {
        prefs.edit().clear().apply();
    }

    public boolean isLogged() {
        return prefs.getBoolean(KEY_LOGGED, false);
    }

    public String getUserName() {
        return prefs.getString(KEY_NAME, "");
    }

    public String getUserEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    public void saveUser(String name, String s, String email, String pass) {
        prefs.edit()
                .putString(KEY_NAME, name)
                .putString(KEY_EMAIL, email)
                .putBoolean(KEY_LOGGED, true)
                .apply();
    }

    //SALVARE I LOGIN??
}
