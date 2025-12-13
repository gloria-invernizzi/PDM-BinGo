package com.application.bingo.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.application.bingo.database.User;
import com.application.bingo.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * ProfileViewModel:
 * - Mantiene lo stato dell'utente (nome, foto, email...)
 * - Chiama il repository per qualsiasi operazione sui dati
 * - Non conosce database, né Firebase
 * - Espone LiveData al Fragment
 */
public class ProfileViewModel extends ViewModel {

    private final UserRepository userRepo;

    // LiveData osservata dal Fragment
    private final MutableLiveData<User> user = new MutableLiveData<>();

    public ProfileViewModel(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public LiveData<User> getUser() {
        return user;
    }

    // ---------------------------------------------------------------------------------------------
    // CARICA UTENTE DAL REPOSITORY
    // ---------------------------------------------------------------------------------------------
    public void loadUser(String email) {
        Log.d("ProfileViewModel", "loadUser called con email: " + email);
        userRepo.getUser(email, u -> {
            Log.d("ProfileViewModel", "Utente ricevuto dal repository: " + u);
            if (u != null) {
                user.setValue(u);
            }
        });
    }

    // ---------------------------------------------------------------------------------------------
    // SALVA MODIFICHE NOME/INDIRIZZO
    // ---------------------------------------------------------------------------------------------
    public void updateProfile(String name, String address) {
        User u = user.getValue();
        if (u == null) return;
        u.setName(name);
        u.setAddress(address);
        userRepo.updateUser(u);   // Room
        userRepo.saveToPrefs(u);  // PrefsManager
        user.setValue(u);         // LiveData
    }

    // ---------------------------------------------------------------------------------------------
    // SALVA FOTO PROFILO
    // ---------------------------------------------------------------------------------------------
    public void savePhotoUri(String email, String uri) {
        userRepo.updatePhotoUri(email, uri); // Room
        userRepo.savePhotoToPrefs(email, uri); // PrefsManager
        User u = user.getValue();
        if (u != null) {
            u.setPhotoUri(uri);
            user.setValue(u);
        }
    }
}
