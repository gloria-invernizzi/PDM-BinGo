package com.application.bingo.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.application.bingo.repository.UserRepository;

public class ChangeEmailViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<String> messageLiveData = new MutableLiveData<>();

    public ChangeEmailViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<String> getMessageLiveData() {
        return messageLiveData;
    }

    public void changeEmail(String oldEmail, String newEmail, String confirmEmail) {
        if (!newEmail.equals(confirmEmail)) {
            messageLiveData.setValue("Le email non corrispondono");
            return;
        }

        userRepository.changeEmail(oldEmail, newEmail, new UserRepository.Callback() {
            @Override
            public void onSuccess(String msg) {
                Log.d("ChangeEmailViewModel", "onSuccess callback: " + msg);
                messageLiveData.postValue("Email aggiornata con successo");
            }

            @Override
            public void onFailure(String msg) {
                Log.d("ChangeEmailViewModel", "onFailure callback: " + msg);
                messageLiveData.postValue(msg);
            }
        });
    }
}

