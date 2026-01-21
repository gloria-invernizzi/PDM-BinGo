package com.application.bingo.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

/**
 * SettingsRepository:
 * Manages all application-level settings.
 * Handles persistence via SharedPreferences and provides default values.
 * Uses the application context to prevent memory leaks while accessing system settings.
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
        // Use application context to avoid memory leaks
        this.context = context.getApplicationContext();
        prefs = this.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // ---------------- Theme ----------------

    /**
     * Returns the current theme setting.
     * If no setting exists, it defaults to the system's current theme.
     *
     * @return "dark" or "light".
     */
    public String getTheme() {
        String saved = prefs.getString(KEY_THEME, null);

        if (saved == null) {
            // First run: detect system theme
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
        return "dark".equals(getTheme());
    }

    // ---------------- Language ----------------

    /**
     * Returns the current language setting.
     * If no setting exists, it defaults to the system's default language.
     *
     * @return ISO language code (e.g., "en", "it").
     */
    public String getLanguage() {
        String defaultPhoneLang = context.getResources()
                .getConfiguration()
                .getLocales()
                .get(0)
                .getLanguage();

        String saved = prefs.getString(KEY_LANGUAGE, null);
        return (saved == null) ? defaultPhoneLang : saved;
    }

    public void setLanguage(String language) {
        prefs.edit().putString(KEY_LANGUAGE, language).apply();
    }

    // ---------------- Notifications ----------------

    public boolean isNotificationsEnabled() {
        if (!prefs.contains(KEY_NOTIFICATIONS)) {
            return false; // Default to false on first open
        } else {
            return prefs.getBoolean(KEY_NOTIFICATIONS, false);
        }
    }

    public void setNotificationsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply();
    }

    // ---------------- Sound ----------------

    public boolean isSoundEnabled() {
        if (!prefs.contains(KEY_SOUND)) {
            return true; // Default enabled
        } else {
            return prefs.getBoolean(KEY_SOUND, true);
        }
    }

    public void setSoundEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_SOUND, enabled).apply();
    }

    // ---------------- Vibration ----------------

    public boolean isVibrationEnabled() {
        if (!prefs.contains(KEY_VIBRATION)) {
            return true; // Default enabled
        } else {
            return prefs.getBoolean(KEY_VIBRATION, true);
        }
    }

    public void setVibrationEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_VIBRATION, enabled).apply();
    }
}
