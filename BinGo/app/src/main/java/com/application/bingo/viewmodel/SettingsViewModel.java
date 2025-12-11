package com.application.bingo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.application.bingo.repository.SettingsRepository;
//La UI si limita a chiamare metodi come:
//viewModel.setTheme("dark")
public class SettingsViewModel extends ViewModel {

    private final SettingsRepository settingsRepo;

    public SettingsViewModel(SettingsRepository settingsRepo) {
        this.settingsRepo = settingsRepo;
    }

    // TEMA
    public String getTheme() {
        return settingsRepo.getTheme();
    }

    public boolean isDarkTheme() {
        return "dark".equals(settingsRepo.getTheme());
    }

    public void setThemeLight() {
        settingsRepo.setTheme("light");
    }

    public void setThemeDark() {
        settingsRepo.setTheme("dark");
    }

    // LINGUA -> LiveData per osservare la lingua in tempo reale
    //Così la UI può osservare i cambiamenti di lingua senza leggere direttamente il repository
    private final MutableLiveData<String> languageLiveData = new MutableLiveData<>();

    public LiveData<String> getLanguageLiveData() {
        return languageLiveData;
    }

    public void loadLanguage() {
        languageLiveData.setValue(settingsRepo.getLanguage());
    }

    public void setLanguage(String lang) {
        settingsRepo.setLanguage(lang);
        languageLiveData.setValue(lang); // aggiorna la LiveData
    }
    public String getLanguage() {
        return settingsRepo.getLanguage();
    }



    // NOTIFICHE
    private final MutableLiveData<Boolean> notificationsLiveData = new MutableLiveData<>();

    public LiveData<Boolean> getNotificationsLiveData() {
        return notificationsLiveData;
    }

    public void loadNotificationsState() {
        notificationsLiveData.setValue(settingsRepo.isNotificationsEnabled());
    }

    public void setNotificationsEnabled(boolean enabled) {
        settingsRepo.setNotificationsEnabled(enabled);
        notificationsLiveData.setValue(enabled); // aggiorna la LiveData
    }

    public boolean isSoundEnabled() {
        return settingsRepo.isSoundEnabled();
    }

    public boolean isVibrationEnabled() {
        return settingsRepo.isVibrationEnabled();
    }

    public void setSoundEnabled(boolean isChecked) {
        settingsRepo.setSoundEnabled(isChecked);
    }

    public void setVibrationEnabled(boolean isChecked) {
   settingsRepo.setVibrationEnabled(isChecked);
    }
}
