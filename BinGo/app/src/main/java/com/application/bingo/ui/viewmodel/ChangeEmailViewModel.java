package com.application.bingo.ui.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.application.bingo.R;
import com.application.bingo.repository.UserRepository;

/**
 * ChangeEmailViewModel:
 * Gestisce la logica per cambiare l'email
 * - Controlli input
 * - Chiama UserRepository per aggiornamento locale + remoto
 * - Posta messaggi al fragment tramite LiveData
 * - MVVM  la VM non chiama Firebase direttamente
 */
public class ChangeEmailViewModel extends AndroidViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<String> messageLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> logoutLiveData = new MutableLiveData<>();

    public ChangeEmailViewModel(Application application) {
        super(application);
        userRepository = new UserRepository(application.getApplicationContext());
    }

    public LiveData<String> getMessageLiveData() {
        return messageLiveData;
    }

    public LiveData<Boolean> getLogoutLiveData() {
        return logoutLiveData;
    }

    /**
     * Cambia l'email: flusso corretto
     * - solo online
     * - locale solo se remoto ok
     * - logout forzato dopo successo
     */
    public void changeEmail(String oldPassword, String newEmail) {
        String oldEmail = userRepository.getCurrentUserEmail();
        if (oldPassword == null || oldPassword.isEmpty()) {
            messageLiveData.setValue(getApplication().getString(R.string.invalid_password));
            return;
        }

        userRepository.changeEmail(oldEmail, oldPassword, newEmail,
                new UserRepository.Callback() {
                    @Override
                    public void onSuccess(String msg) {
                        messageLiveData.postValue(msg);
                    }

                    @Override
                    public void onFailure(String msg) {
                        // Traduci l’errore per la UI
                        if ("USER_NOT_FOUND".equals(msg)) {
                            messageLiveData.postValue(getApplication().getString(R.string.session_expired));
                        } else {
                            messageLiveData.postValue(msg);
                        }                    }
                },
                new UserRepository.LogoutCallback() {
                    @Override
                    public void onLogoutRequired() {
                        logoutLiveData.postValue(true);
                    }
                });
    }

    public void refreshFirebaseUser() {
        userRepository.refreshFirebaseUser(
                new UserRepository.Callback() {
                    @Override
                    public void onSuccess(String msg) {
                        messageLiveData.postValue(msg);
                    }

                    @Override
                    public void onFailure(String msg) {
                        messageLiveData.postValue(msg);
                    }
                });
    }

}

