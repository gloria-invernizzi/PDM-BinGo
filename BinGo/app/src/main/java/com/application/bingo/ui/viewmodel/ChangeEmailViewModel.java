package com.application.bingo.ui.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.application.bingo.R;
import com.application.bingo.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
    public void changeEmail(String oldEmail, String oldPassword, String newEmail) {
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
                        messageLiveData.postValue(msg);
                    }
                },
                new UserRepository.LogoutCallback() {
                    @Override
                    public void onLogoutRequired() {
                        logoutLiveData.postValue(true);
                    }
                });
    }

    public void refreshFirebaseUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("ChangeEmailVM", "refreshFirebaseUser chiamato. currentUser = " + user);

        if (user != null) {
            user.reload().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    boolean verified = user.isEmailVerified();
                    if (verified) {
                        messageLiveData.postValue(getApplication().getString(R.string.email_verified));
                    } else {
                        messageLiveData.postValue(getApplication().getString(R.string.email_not_verified));
                    }
                } else {
                    messageLiveData.postValue(
                            getApplication().getString(R.string.user_reload_error)
                                    + ": " + task.getException().getMessage()
                    );
                }
            });

        } else {
            Log.e("ChangeEmailVM", "refreshFirebaseUser: utente non loggato");
            messageLiveData.postValue(getApplication().getString(R.string.user_not_logged_in));

        }
    }


}

