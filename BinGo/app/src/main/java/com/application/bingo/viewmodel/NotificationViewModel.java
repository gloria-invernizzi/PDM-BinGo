package com.application.bingo.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.application.bingo.model.Notification;
import com.application.bingo.repository.NotificationRepository;
import java.util.List;
public class NotificationViewModel extends AndroidViewModel {
    private final NotificationRepository repository;

    public NotificationViewModel(@NonNull Application application) {
        super(application);
        repository = new NotificationRepository(application);
    }

    public LiveData<List<Notification>> getNotificationsForDay(long selectedDateMillis) {
        return repository.getNotificationsForDay(selectedDateMillis);
    }

    public void saveNotification(Notification notification) {
        repository.saveNotification(notification);
    }

    public void deleteNotification(Notification notification) {
        repository.deleteNotification(notification);
    }

    public void deleteRepeatingNotification(Notification notification) {
        repository.deleteRepeatingNotification(notification);
    }



}
