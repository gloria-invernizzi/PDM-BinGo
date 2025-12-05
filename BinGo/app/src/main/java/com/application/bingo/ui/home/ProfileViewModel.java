package com.application.bingo.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.application.bingo.User;
import com.application.bingo.repository.SettingsRepository;
import com.application.bingo.repository.UserRepository;

//media tra Fragment e Repository
//mantiene lo stato del profilo utente
//gestisce impostazioni
//Semplifica il Fragment
//La UI si limita a chiamare metodi come:
//viewModel.setTheme("dark")
public class ProfileViewModel extends ViewModel {
    // Repository che gestisce i dati dell'utente (Room o API)
    private final UserRepository userRepo;

    // Repository che gestisce impostazioni app (tema, lingua, notifiche)
    private final SettingsRepository settingsRepo;

    // LiveData osservabile che contiene l'utente
    private final MutableLiveData<User> user = new MutableLiveData<>();

    // Costruttore
    public ProfileViewModel(UserRepository userRepo, SettingsRepository settingsRepo) {
        this.userRepo = userRepo;
        this.settingsRepo = settingsRepo;
    }
    // Espone la LiveData al Fragment
    public LiveData<User> getUser() {
        return user;
    }

    //carica un utente dal Repository, cercandolo via email
    //Quando il Repository lo trova, aggiorna la LiveData
     public void loadUser(String email){
        User u =userRepo.getUser(email);
        user.setValue(u);
     }

     //aggiorna i dati dell'utente nel database
     public void saveProfile(User u) {
         userRepo.updateUser(u);
     }

    // SEZIONE IMPOSTAZIONI

    //restituisce il tema salvato

    //Salva il tema selezionato

    public void setDarkTheme(){
         settingsRepo.setDarkTheme(true);
    }

    // Ritorna la lingua selezionata ("it", "en", "de", ecc.)
    public String getLanguage() {
        return settingsRepo.getLanguage();
    }

    // Salva la lingua scelta
    public void setLanguage(String lang) {
        settingsRepo.setLanguage(lang);
    }

    // Restituisce se le notifiche sono abilitate
    public boolean getNotificationsEnabled() {
        return settingsRepo.isNotificationsEnabled();
    }

    // Abilita o disabilita le notifiche
    public void setNotificationsEnabled(boolean en) {
        settingsRepo.setNotificationsEnabled(en);
    }







}
