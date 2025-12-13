package com.application.bingo.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.application.bingo.repository.UserRepository;

/**
 * ViewModel per il cambio password.
 * Espone LiveData per notificare la UI.
 * Gestisce logica minimale e delega tutto al repository.
 */
public class ChangePasswordViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<String> messageLiveData = new MutableLiveData<>();

    public ChangePasswordViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<String> getMessageLiveData() {
        return messageLiveData;
    }

    /**
     * Invia richiesta al repository per cambiare la password
     */
    public void changePassword(String email, String oldPassword, String newPassword, String confirmPassword) {
        userRepository.changePassword(email, oldPassword, newPassword, confirmPassword, new UserRepository.Callback() {
            @Override
            public void onSuccess(String msg) {
                messageLiveData.postValue("Password aggiornata con successo");
            }

            @Override
            public void onFailure(String msg) {
                messageLiveData.postValue(msg);
            }
        });
    }
}
