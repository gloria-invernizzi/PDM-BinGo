package com.application.bingo.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.application.bingo.repository.UserRepository;

public class ChangePasswordViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<String> messageLiveData = new MutableLiveData<>();

    public ChangePasswordViewModel(UserRepository repository) {
        this.userRepository = repository;
    }

    public LiveData<String> getMessageLiveData() {
        return messageLiveData;
    }

    /**
     * Chiede al repository di cambiare la password.
     */
    public void changePassword(String email, String oldPassword, String newPassword, String confirmPassword) {
        userRepository.changePassword(email, oldPassword, newPassword, confirmPassword, new UserRepository.Callback() {
            @Override
            public void onSuccess(String message) {
                messageLiveData.postValue(message);
            }

            @Override
            public void onFailure(String error) {
                messageLiveData.postValue(error);
            }
        });
    }
}


