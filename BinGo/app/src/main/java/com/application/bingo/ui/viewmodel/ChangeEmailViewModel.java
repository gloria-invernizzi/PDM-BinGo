package com.application.bingo.ui.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.application.bingo.repository.UserRepository;

/**
 * ChangeEmailViewModel:
 * Gestisce la logica per cambiare l'email
 * - Controlli input
 * - Chiama UserRepository per aggiornamento locale + remoto
 * - Posta messaggi al fragment tramite LiveData
 * - MVVM puro: la VM non chiama Firebase direttamente
 */
public class ChangeEmailViewModel extends AndroidViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<String> messageLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> logoutLiveData = new MutableLiveData<>();

    public ChangeEmailViewModel(Application application) {
        super(application);
        userRepository = new UserRepository(application.getApplicationContext());
    }

    public LiveData<String> getMessageLiveData() { return messageLiveData; }
    public LiveData<Boolean> getLogoutLiveData() { return logoutLiveData; }

    /**
     * Cambia l'email: flusso corretto
     * - online-first
     * - locale solo se remoto ok
     * - logout forzato dopo successo
     */
    public void changeEmail(String oldEmail, String oldPassword, String newEmail) {
        if (oldPassword == null || oldPassword.isEmpty()) {
            messageLiveData.setValue("Password non valida");
            return;
        }

        userRepository.changeEmail(oldEmail, oldPassword, newEmail,
                new UserRepository.Callback() {
                    @Override
                    public void onSuccess(String msg) {
                        messageLiveData.postValue("Email aggiornata con successo");
                    }

                    @Override
                    public void onFailure(String msg) {
                        messageLiveData.postValue("Aggiornamento fallito: " + msg);
                    }
                },
                new UserRepository.LogoutCallback() {
                    @Override
                    public void onLogoutRequired() {
                        logoutLiveData.postValue(true);
                    }
                });
    }
}

