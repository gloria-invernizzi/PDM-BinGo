package com.application.bingo.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.application.bingo.repository.SettingsRepository;
import com.application.bingo.repository.UserRepository;
import com.application.bingo.repository.NotificationRepository;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;
    private final UserRepository userRepository;
    private final SettingsRepository settingsRepository;
    private final NotificationRepository notificationRepository;

    public ViewModelFactory(Application application) {
        this.application = application;
        this.userRepository = new UserRepository(application);
        this.settingsRepository = new SettingsRepository(application);
        this.notificationRepository = new NotificationRepository(application);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SettingsViewModel.class)) {
            return (T) new SettingsViewModel(settingsRepository);
        } else if (modelClass.isAssignableFrom(ProfileViewModel.class)) {
            return (T) new ProfileViewModel(userRepository);
        } else if (modelClass.isAssignableFrom(ChangePasswordViewModel.class)) {
            return (T) new ChangePasswordViewModel(userRepository);
        } else if (modelClass.isAssignableFrom(ChangeEmailViewModel.class)) {
            return (T) new ChangeEmailViewModel(userRepository);
        } else if (modelClass.isAssignableFrom(NotificationViewModel.class)) {
            return (T) new NotificationViewModel(notificationRepository);
        } else if (modelClass.isAssignableFrom(FamilyViewModel.class)) {
            return (T) new FamilyViewModel(userRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
