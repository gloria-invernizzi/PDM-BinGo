package com.application.bingo.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.application.bingo.repository.UserRepository;
import com.google.firebase.auth.FirebaseUser;

/**
 * ViewModel per gestire la logica di cambio password.
 * Comunica con il repository e espone messaggi al fragment tramite LiveData.
 */
public class ChangePasswordViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<String> messageLiveData = new MutableLiveData<>();

    public ChangePasswordViewModel(UserRepository repository) {
        this.userRepository = repository; // repository inietta la logica di cambio password
    }

    public LiveData<String> getMessageLiveData() {
        return messageLiveData;
    }

    /**
     * Richiesta di cambio password.
     * userEmail: email dell'utente (recuperata da SharedPreferences)
     * oldPassword: vecchia password inserita
     * newPassword: nuova password
     * confirmPassword: conferma nuova password
     */
    public void changePassword(String userEmail, String oldPassword, String newPassword, String confirmPassword) {
        userRepository.changePassword(userEmail, oldPassword, newPassword, confirmPassword, new UserRepository.Callback() {
            @Override
            public void onSuccess(String message) {
                messageLiveData.postValue(message); // invia messaggio al fragment
            }

            @Override
            public void onFailure(String error) {
                messageLiveData.postValue(error); // invia messaggio di errore al fragment
            }
        });
    }
}


