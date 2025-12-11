package com.application.bingo.repository;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Classe helper per leggere e scrivere le impostazioni principali dell'app
 * (tema, lingua, notifiche, suono, vibrazione) usando SharedPreferences.
 */
public class SettingsPrefs {

    private static final String PREFS_NAME = "settings_prefs";

    private static final String KEY_THEME = "theme";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_NOTIFICATIONS = "notifications";
    private static final String KEY_SOUND = "sound";
    private static final String KEY_VIBRATION = "vibration";

    private final SharedPreferences prefs;

    public SettingsPrefs(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // ---------------- Tema ----------------
    public void setTheme(String theme) {
        prefs.edit().putString(KEY_THEME, theme).apply();
    }

    public String getTheme() {
        return prefs.getString(KEY_THEME, null); // null indica che non è stato mai scelto
    }

    // ---------------- Lingua ----------------
    public void setLanguage(String lang) {
        prefs.edit().putString(KEY_LANGUAGE, lang).apply();
    }

    public String getLanguage() {
        return prefs.getString(KEY_LANGUAGE, "it"); // default italiano
    }

    // ---------------- Notifiche ----------------
    public void setNotificationsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply();
    }

    public boolean isNotificationsEnabled() {
        return prefs.getBoolean(KEY_NOTIFICATIONS, false); // default disattive
    }

    // ---------------- Suono ----------------
    public void setSoundEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_SOUND, enabled).apply();
    }

    public boolean isSoundEnabled() {
        return prefs.getBoolean(KEY_SOUND, true); // default attivo
    }

    // ---------------- Vibrazione ----------------
    public void setVibrationEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_VIBRATION, enabled).apply();
    }

    public boolean isVibrationEnabled() {
        return prefs.getBoolean(KEY_VIBRATION, true); // default attiva
    }
}




