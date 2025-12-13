package com.application.bingo.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.application.bingo.repository.NotificationRepository;
import com.application.bingo.repository.SettingsRepository;
import com.application.bingo.repository.UserRepository;
import com.application.bingo.repository.WasteRepository;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;

    public ViewModelFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        if (modelClass.isAssignableFrom(ChangePasswordViewModel.class)) {
            return (T) new ChangePasswordViewModel(
                    new UserRepository(application)
            );
        }

        if (modelClass.isAssignableFrom(NotificationViewModel.class)) {
            return (T) new NotificationViewModel(
                    new NotificationRepository(application)
            );
        }

        if (modelClass.isAssignableFrom(ProfileViewModel.class)) {
            return (T) new ProfileViewModel(
                    new UserRepository(application)
            );
        }

        if (modelClass.isAssignableFrom(SettingsViewModel.class)) {
            return (T) new SettingsViewModel(
                    new SettingsRepository(application)
            );
        }

        if (modelClass.isAssignableFrom(WasteViewModel.class)) {
            return (T) new WasteViewModel(
                    new WasteRepository(application)
            );
        }


        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
