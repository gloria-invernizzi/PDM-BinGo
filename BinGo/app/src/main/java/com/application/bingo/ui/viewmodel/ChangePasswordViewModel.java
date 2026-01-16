package com.application.bingo.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.application.bingo.repository.UserRepository;

/**
 * ViewModel per il cambio password.
 * Gestisce:
 * - Validazioni minime lato ViewModel
 * - Controllo connessione internet
 * - Blocco utenti Google
 * - Notifiche alla UI tramite LiveData
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
     * Cambia la password di un utente.
     * - Controlla se l'utente è Google (non consentito)
     * - Controlla connessione internet
     * - Invia richiesta al repository
     */
    public void changePassword(String email, String oldPassword, String newPassword, String confirmPassword) {
        if (email == null || email.isEmpty()) {
            messageLiveData.postValue("invalid_email");
            return;
        }

        if (oldPassword == null || oldPassword.isEmpty() ||
                newPassword == null || newPassword.isEmpty() ||
                confirmPassword == null || confirmPassword.isEmpty()) {
            messageLiveData.postValue("fill_all_fields");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            messageLiveData.postValue("passwords_do_not_match");
            return;
        }

        // -------------------------------
        // Controllo se utente Google
        // -------------------------------
        userRepository.isGoogleUser(email, new UserRepository.GoogleCheckCallback() {
            @Override
            public void onResult(boolean isGoogleUser) {
                if (isGoogleUser) {
                    messageLiveData.postValue("google_users_cannot_change_password");
                    return;
                }

                // -------------------------------
                // Controllo connessione internet
                // -------------------------------
                if (!userRepository.isConnectedToInternet()) {
                    messageLiveData.postValue("cannot_change_offline");
                    return;
                }

                // -------------------------------
                // Tutto ok → invia richiesta al repository
                // -------------------------------
                userRepository.changePassword(email, oldPassword, newPassword, confirmPassword, new UserRepository.Callback() {
                    @Override
                    public void onSuccess(String msg) {
                        messageLiveData.postValue("password_updated_success");
                    }

                    @Override
                    public void onFailure(String msg) {
                        messageLiveData.postValue(msg);
                    }
                });
            }

            public void onFailure(String msg) {
                messageLiveData.postValue(msg);
            }
        });
    }
}
