package com.application.bingo.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.application.bingo.model.User;
import com.application.bingo.repository.UserRepository;

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
    private final MutableLiveData<String> error = new MutableLiveData<>();


    public ProfileViewModel(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public LiveData<User> getUser() {
        return user;
    }

    public LiveData<String> getError() {
        return error;
    }

    private final MutableLiveData<String> deleteAccountResult = new MutableLiveData<>();
    // ---------------------------------------------------------------------------------------------
    // CARICA UTENTE DAL REPOSITORY
    // ---------------------------------------------------------------------------------------------
    public void loadUser(String email) {
        Log.d("ProfileViewModel", "loadUser chiamato con email = " + email);
        userRepo.getUser(email, new UserRepository.UserCallback() {
            @Override
            public void onUserLoaded(User u) {
                if (u != null) {
                    Log.d("ProfileViewModel", "User ricevuto da UserLocalSource: " + user);
                    user.postValue(u);
                } else {
                    error.postValue("Utente non trovato");
                }
            }

            public void onFailure(String msg) {
                // Questo è il metodo che mancava e causava l'errore!
                error.postValue(msg);
            }
        });
    }

    // ---------------------------------------------------------------------------------------------
    // SALVA MODIFICHE NOME/INDIRIZZO
    // ---------------------------------------------------------------------------------------------
    public void updateProfile(String name, String address) {
        User u = user.getValue();
        if (u == null) {
            error.postValue("user_not_loaded");
            return;
        }
        u.setName(name);
        u.setAddress(address);
        userRepo.updateUser(u);   // Room / remoto
        userRepo.saveToPrefs(u);  // aggiorna anche PrefsManager
        user.postValue(u);
    }

    // ---------------------------------------------------------------------------------------------
    // SALVA FOTO PROFILO
    // ---------------------------------------------------------------------------------------------
    public void savePhotoUri(String email, String uri) {
        User u = user.getValue();
        if (u == null) {
            error.postValue("user_not_found");
            return;
        }
        u.setPhotoUri(uri);
        userRepo.updatePhotoUri(email, uri);
        userRepo.saveToPrefs(u);  // aggiorna PrefsManager
        user.postValue(u);
    }

    // ---------------------------------------------------------------------------------------------
    // ELIMINA PROFILO
    // ---------------------------------------------------------------------------------------------
    public LiveData<String> getDeleteAccountResult() {
        return deleteAccountResult;
    }

    // Metodo per eliminare account
    public void deleteAccount() {
        User u = user.getValue();
        if (u == null) {
            deleteAccountResult.postValue("user_not_loaded");
            return;
        }

        if (!userRepo.isInternetAvailable()) {
            deleteAccountResult.postValue("offline_error");
            return;
        }

        userRepo.deleteAccount(new UserRepository.Callback() {
            @Override
            public void onSuccess(String message) {
                deleteAccountResult.postValue("account_deleted_success");
            }

            @Override
            public void onFailure(String error) {
                deleteAccountResult.postValue(error); // qui può essere "reauth_required" o altri errori Firebase
            }
        });
    }

}


