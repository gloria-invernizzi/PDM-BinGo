package com.application.bingo.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

public class SettingsRepository {

    private static final String PREFS_NAME = "app_settings";
    private static final String KEY_DARK_THEME = "dark_theme";
    private static final String KEY_NOTIFICATIONS = "notifications";
    private static final String KEY_SOUND = "sound";
    private static final String KEY_VIBRATION = "vibration";
    private static final String KEY_LANGUAGE = "language";
    private final SharedPreferences prefs;

    private final Context context;


    public SettingsRepository(Context context) {
        this.context=context;
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }


    // Tema Chiaro / Scuro
    public String getTheme() {
        String saved = prefs.getString(KEY_DARK_THEME, null);

        // if null -> nessun tema scelto -> uso tema del sistema
        if (saved == null) {
            int nightModeFlags = context.getResources().getConfiguration().uiMode
                    & Configuration.UI_MODE_NIGHT_MASK;

            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                return "dark";
            } else {
                return "light";
            }
        }


        return saved;
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
        return prefs.getBoolean(KEY_NOTIFICATIONS, false); // default: disattive
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
        // Usa il context dell'app per leggere la lingua corrente
        String defaultPhoneLang = context.getResources()
                .getConfiguration()
                .getLocales()
                .get(0)
                .getLanguage();

        return prefs.getString(KEY_LANGUAGE, defaultPhoneLang);
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