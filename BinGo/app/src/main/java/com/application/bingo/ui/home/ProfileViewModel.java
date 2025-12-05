package com.application.bingo.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.application.bingo.User;
import com.application.bingo.repository.SettingsRepository;
import com.application.bingo.repository.UserRepository;

//media tra Fragment e Repository
//mantiene lo stato del profilo utente
//Semplifica il Fragment
public class ProfileViewModel extends ViewModel {

    private final UserRepository userRepo;

    // LiveData osservabile che contiene l'utente
    private final MutableLiveData<User> user = new MutableLiveData<>();

    // Costruttore
    public ProfileViewModel(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // Espone la LiveData al Fragment
    public LiveData<User> getUser() {
        return user;
    }

    // Carica un utente dal repository tramite email
    public void loadUser(String email) {
        User u = userRepo.getUser(email);
        user.setValue(u);
    }

    // Aggiorna i dati dell'utente nel repository
    public void saveProfile(User u) {
        userRepo.updateUser(u);
    }
}