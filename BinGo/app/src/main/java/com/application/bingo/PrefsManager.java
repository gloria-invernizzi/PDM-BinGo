package com.application.bingo;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManager {
    // Shared Preferences file name and keys
    private static final String PREFS_NAME = "bingo_prefs";
    private static final String KEY_NAME = "user_name";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_ADDRESS = "user_address";
    private static final String KEY_PASS = "user_pass";
    private static final String KEY_REMEMBER = "remember";

    private final SharedPreferences prefs;

    // Constructor to initialize SharedPreferences
    // SharedPreferences works in a background thread
    public PrefsManager(Context ctx) {
        prefs = ctx.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Save user information and login status
    public void saveUser(String name, String email) {
        prefs.edit()
                .putString(KEY_NAME, name)
                .putString(KEY_EMAIL, email)
                .putBoolean(KEY_REMEMBER, true)
                .apply();
    }
    public void saveUser(String name, String address, String email, String pass) {
        prefs.edit()
                .putString(KEY_NAME, name)
                .putString(KEY_EMAIL, email)
                .putBoolean(KEY_REMEMBER, true)
                .apply();
    }
    public void clearSavedUser() {
        prefs.edit().clear().apply();
    }

    public String getSavedName() {
        return prefs.getString(KEY_NAME, "");
    }

    public String getUserAddress() {
        return prefs.getString(KEY_ADDRESS, "");
    }

    public String getSavedPassword() {
        return prefs.getString(KEY_PASS, "");
    }
    public String getSavedEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    //SALVARE I LOGIN??
}
