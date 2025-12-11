package com.application.bingo.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

/**
 * Repository per tutte le impostazioni dell'app.
 * Gestisce la persistenza tramite SharedPreferences e
 * fornisce valori di default corretti alla prima apertura.
 * Il context dell'app viene usato per leggere impostazioni
 * di sistema (tema, lingua) senza rischi di memory leak.
 */
public class SettingsRepository {

    private static final String PREFS_NAME = "app_settings";

    private static final String KEY_THEME = "theme";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_NOTIFICATIONS = "notifications";
    private static final String KEY_SOUND = "sound";
    private static final String KEY_VIBRATION = "vibration";

    private final SharedPreferences prefs;
    private final Context context;

    public SettingsRepository(Context context) {
        // uso context dell'app per evitare memory leak
        this.context = context.getApplicationContext();
        prefs = this.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // ---------------- Tema ----------------
    /**
     * Restituisce il tema corrente.
     * Se non è mai stato scelto dall'utente, usa il tema di default del telefono.
     */
    public String getTheme() {
        String saved = prefs.getString(KEY_THEME, null);

        if (saved == null) {
            // prima apertura: uso tema di default del telefono
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

    public void setTheme(String theme) {
        prefs.edit().putString(KEY_THEME, theme).apply();
    }

    public boolean isDarkTheme() {
        if ("dark".equals(getTheme())) {
            return true;
        } else {
            return false;
        }
    }

    // ---------------- Lingua ----------------
    /**
     * Restituisce la lingua corrente.
     * Se non è mai stata scelta dall'utente, usa la lingua di default del telefono.
     */
    public String getLanguage() {
        String defaultPhoneLang = context.getResources()
                .getConfiguration()
                .getLocales()
                .get(0)
                .getLanguage();

        String saved = prefs.getString(KEY_LANGUAGE, null);
        if (saved == null) {
            return defaultPhoneLang;
        } else {
            return saved;
        }
    }

    public void setLanguage(String language) {
        prefs.edit().putString(KEY_LANGUAGE, language).apply();
    }

    // ---------------- Notifiche ----------------
    public boolean isNotificationsEnabled() {
        if (!prefs.contains(KEY_NOTIFICATIONS)) {
            return false; // default alla prima apertura
        } else {
            return prefs.getBoolean(KEY_NOTIFICATIONS, false);
        }
    }

    public void setNotificationsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply();
    }

    // ---------------- Suono ----------------
    public boolean isSoundEnabled() {
        if (!prefs.contains(KEY_SOUND)) {
            return true; // default attivo
        } else {
            return prefs.getBoolean(KEY_SOUND, true);
        }
    }

    public void setSoundEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_SOUND, enabled).apply();
    }

    // ---------------- Vibrazione ----------------
    public boolean isVibrationEnabled() {
        if (!prefs.contains(KEY_VIBRATION)) {
            return true; // default attivo
        } else {
            return prefs.getBoolean(KEY_VIBRATION, true);
        }
    }

    public void setVibrationEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_VIBRATION, enabled).apply();
    }
}

/*apply()  salva i dati in background, asincrono, senza bloccare il thread principale
non torna nulla, void
 commit() slava i dai sincrono, blocca il thread fino a quando la scrittura è completata
 ritorna boolean true/false se è andata/non andata a buon fine il salvataggio
 */