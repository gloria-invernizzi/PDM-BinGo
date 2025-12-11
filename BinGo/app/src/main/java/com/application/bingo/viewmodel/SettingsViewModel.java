package com.application.bingo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.application.bingo.repository.SettingsRepository;

/**
 * ViewModel per SettingsFragment.
 * Espone metodi e LiveData per tema, lingua, notifiche, suono e vibrazione.
 */
public class SettingsViewModel extends ViewModel {

    private final SettingsRepository settingsRepo;

    // LiveData per lingua e notifiche
    private final MutableLiveData<String> languageLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> notificationsLiveData = new MutableLiveData<>();

    public SettingsViewModel(SettingsRepository settingsRepo) {
        this.settingsRepo = settingsRepo;
    }

    // ---------------- Tema ----------------
    public String getTheme() {
        return settingsRepo.getTheme();
    }

    public boolean isDarkTheme() {
        return settingsRepo.isDarkTheme();
    }

    public void setThemeLight() {
        settingsRepo.setTheme("light");
    }

    public void setThemeDark() {
        settingsRepo.setTheme("dark");
    }

    // ---------------- Lingua ----------------
    public LiveData<String> getLanguageLiveData() {
        return languageLiveData;
    }

    public void loadLanguage() {
        // Carica la lingua corrente e aggiorna la LiveData
        languageLiveData.setValue(settingsRepo.getLanguage());
    }

    public void setLanguage(String lang) {
        settingsRepo.setLanguage(lang);
        languageLiveData.setValue(lang); // aggiorna LiveData
    }

    public String getLanguage() {
        return settingsRepo.getLanguage();
    }

    // ---------------- Notifiche ----------------
    public LiveData<Boolean> getNotificationsLiveData() {
        return notificationsLiveData;
    }

    public void loadNotificationsState() {
        // Carica lo stato corrente delle notifiche
        notificationsLiveData.setValue(settingsRepo.isNotificationsEnabled());
    }

    public void setNotificationsEnabled(boolean enabled) {
        settingsRepo.setNotificationsEnabled(enabled);
        notificationsLiveData.setValue(enabled); // aggiorna LiveData
    }

    // ---------------- Suono ----------------
    public boolean isSoundEnabled() {
        return settingsRepo.isSoundEnabled();
    }

    public void setSoundEnabled(boolean enabled) {
        settingsRepo.setSoundEnabled(enabled);
    }

    // ---------------- Vibrazione ----------------
    public boolean isVibrationEnabled() {
        return settingsRepo.isVibrationEnabled();
    }

    public void setVibrationEnabled(boolean enabled) {
        settingsRepo.setVibrationEnabled(enabled);
    }
}

