package com.application.bingo;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManager {
    private static final String PREFS_NAME = "bingo_prefs";
    private static final String KEY_NAME = "user_name";
    private static final String KEY_SURNAME = "user_surname";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_ADDRESS = "user_address";
    private static final String KEY_PASS = "user_pass";
    private static final String KEY_PHOTO_URI = "user_photo_uri";
    private static final String KEY_REMEMBER = "remember";

    private final SharedPreferences prefs;

    public PrefsManager(Context ctx) {
        prefs = ctx.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveUser(String email, String pass) {
        prefs.edit()
                .putString(KEY_EMAIL, email)
                .putString(KEY_PASS, pass)
                .putBoolean(KEY_REMEMBER, true)
                .apply();
    }

    public void saveUser(String name, String surname, String address, String email, String pass) {
        prefs.edit()
                .putString(KEY_NAME, name)
                .putString(KEY_SURNAME, surname)
                .putString(KEY_ADDRESS, address)
                .putString(KEY_EMAIL, email)
                .putString(KEY_PASS, pass)
                .putBoolean(KEY_REMEMBER, true)
                .apply();
    }

    // Salva SOLO l'email per la sessione corrente (senza password)
    public void saveSessionEmail(String email) {
        prefs.edit().putString(KEY_EMAIL, email).apply();
    }

    public void clearSavedUser() {
        prefs.edit()
                .remove(KEY_NAME)
                .remove(KEY_SURNAME)
                .remove(KEY_ADDRESS)
                .remove(KEY_EMAIL)
                .remove(KEY_PASS)
                .putBoolean(KEY_REMEMBER, false)
                .apply();
    }

    public String getSavedName() {
        return prefs.getString(KEY_NAME, "");
    }

    public String getSavedSurname() {
        return prefs.getString(KEY_SURNAME, "");
    }

    public String getSavedAddress() {
        return prefs.getString(KEY_ADDRESS, "");
    }

    public String getSavedPassword() {
        return prefs.getString(KEY_PASS, "");
    }

    public String getSavedEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    public void savePhotoUri(String email, String uri) {
        prefs.edit()
                .putString(KEY_PHOTO_URI, uri)
                .apply();
    }

    public String getSavedPhotoUri() {
        return prefs.getString(KEY_PHOTO_URI, "");
    }

    public void setRemember(boolean remember) {
        prefs.edit().putBoolean(KEY_REMEMBER, remember).apply();
    }

    public boolean isRemember() {
        return prefs.getBoolean(KEY_REMEMBER, false);
    }

    public void clearLoginOnly() {
        prefs.edit()
                .remove(KEY_PASS)
                .remove(KEY_REMEMBER)
                .apply();
    }

    public void updateEmailOnly(String newEmail) {
        prefs.edit().putString(KEY_EMAIL, newEmail).apply();
    }

    public void clearAll() {
        prefs.edit().clear().apply();
    }

}