package com.application.bingo.ui.home;

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
    public boolean getNotificationsEnabled() {
        return settingsRepo.isNotificationsEnabled();
    }

    public void setNotificationsEnabled(boolean enabled) {
        settingsRepo.setNotificationsEnabled(enabled);
    }
}
