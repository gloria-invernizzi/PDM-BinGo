package com.application.bingo.repository;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsPrefs {
    private static final String PREFS_NAME = "settings_prefs";   // Nome del file delle preferenze

    private static final String KEY_THEME = "theme";             // Chiave per salvare il tema
    private static final String KEY_LANGUAGE = "language";       // Chiave per salvare la lingua
    private static final String KEY_NOTIFICATIONS = "notifications"; // Chiave per le notifiche
    private SharedPreferences prefs;                        // Oggetto SharedPreferences usato dalla classe

    public  SettingsPrefs(Context context) {                       // Costruttore che riceve un Context
        prefs = context.getSharedPreferences(                     // Ottiene/crea il file delle preferenze
                PREFS_NAME,
                Context.MODE_PRIVATE                              // Modalità privata (visibile solo all'app)
        );
    }

    //  THEME
    public void setTheme(String theme) {                          // Metodo per salvare il tema
        prefs.edit()                                              // Apre l'editor
                .putString(KEY_THEME, theme)                         // Inserisce una stringa con la chiave "theme"
                .apply();                                            // Applica le modifiche in modo asincrono
    }

    public String getTheme() {                                    // Metodo per leggere il tema salvato
        return prefs.getString(KEY_THEME, "system");              // Ritorna il valore o "system" se non esiste
    }

    // LANGUAGE
    public void setLanguage(String lang) {                        // Salva la lingua
        prefs.edit()                                              // Apre l'editor
                .putString(KEY_LANGUAGE, lang)                       // Inserisce la stringa sotto la chiave "language"
                .apply();                                            // Salva
    }

    public String getLanguage() {                                 // Legge la lingua
        return prefs.getString(KEY_LANGUAGE, "it");               // Default: italiano
    }

    // NOTIFICATIONS
    public void setNotificationsEnabled(boolean enabled) {         // Salva lo stato delle notifiche
        prefs.edit()                                               // Apre l'editor
                .putBoolean(KEY_NOTIFICATIONS, enabled)              // Inserisce un boolean
                .apply();                                             // Salva
    }

    public boolean getNotificationsEnabled() {                     // Legge lo stato notifiche
        return prefs.getBoolean(KEY_NOTIFICATIONS, false);          // Default true (notifiche attive)
    }
}






