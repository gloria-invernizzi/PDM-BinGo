package com.application.bingo.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.application.bingo.repository.UserRepository;

/**
 * ViewModel for password change.
 * Handles:
 * - Minimum validations on ViewModel side
 * - Internet connection check
 * - Blocking Google users
 * - UI notifications via LiveData
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
     * Returns the current user's email through the repository.
     */
    public String getCurrentUserEmail() {
        return userRepository.getCurrentUserEmail();
    }

    /**
     * Changes a user's password.
     * - Checks if the user is a Google user (not allowed)
     * - Checks internet connection
     * - Sends request to repository
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

        // Check if Google user
        userRepository.isGoogleUser(email, new UserRepository.GoogleCheckCallback() {
            @Override
            public void onResult(boolean isGoogleUser) {
                if (isGoogleUser) {
                    messageLiveData.postValue("google_users_cannot_change_password");
                    return;
                }

                // Check internet connection (Password change requires online)
                if (!userRepository.isConnectedToInternet()) {
                    messageLiveData.postValue("cannot_change_offline");
                    return;
                }

                // Everything ok -> send request to repository
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
        });
    }
}
