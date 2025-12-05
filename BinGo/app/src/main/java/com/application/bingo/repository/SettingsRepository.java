package com.application.bingo.repository;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsRepository {

    private static final String PREFS_NAME = "app_settings";
    private static final String KEY_DARK_THEME = "dark_theme";
    private static final String KEY_NOTIFICATIONS = "notifications";
    private static final String KEY_SOUND = "sound";
    private static final String KEY_VIBRATION = "vibration";
    private static final String KEY_LANGUAGE = "italian";
    private final SharedPreferences prefs;


    public SettingsRepository(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }


    // Tema Chiaro / Scuro
    public String getTheme() {
        return prefs.getString(KEY_DARK_THEME, "light"); // default: chiaro
    }

    public boolean isDarkTheme() {
        return getTheme().equals("dark");
    }
    public void setTheme(String theme) {

        // theme deve essere "light" o "dark"
        prefs.edit()
                .putString(KEY_DARK_THEME, theme)
                .apply();
    }



    // Notifiche

    public boolean isNotificationsEnabled() {
        return prefs.getBoolean(KEY_NOTIFICATIONS, true); // default: attive
    }

    public void setNotificationsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply();
    }


    // Suono

    public boolean isSoundEnabled() {
        return prefs.getBoolean(KEY_SOUND, true); // default: attivo
    }

    public void setSoundEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_SOUND, enabled).apply();
    }


    // Vibrazione

    public boolean isVibrationEnabled() {
        return prefs.getBoolean(KEY_VIBRATION, true); // default: attiva
    }

    public void setVibrationEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_VIBRATION, enabled).apply();
    }

    // Lingua
    public String getLanguage() {

        return prefs.getString(KEY_LANGUAGE, KEY_LANGUAGE);
    }
    public void setLanguage(String language) {
        prefs.edit().putString(KEY_LANGUAGE, language).apply();
    }



}
/*apply()  salva i dati in background, asincrono, senza bloccare il thread principale
non torna nulla, void
 commit() slava i dai sincrono, blocca il thread fino a quando la scrittura è completata
 ritorna boolean true/false se è andata/non andata a buon fine il salvataggio
 */