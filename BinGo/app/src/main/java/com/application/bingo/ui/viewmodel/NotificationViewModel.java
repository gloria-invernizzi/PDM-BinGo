package com.application.bingo.ui.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.application.bingo.R;
import com.application.bingo.model.Notification;
import com.application.bingo.repository.NotificationRepository;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NotificationViewModel extends ViewModel {
    private final NotificationRepository repository;
    private final MutableLiveData<Long> selectedDate = new MutableLiveData<>();
    private final MutableLiveData<String> familyId = new MutableLiveData<>();

    public NotificationViewModel(NotificationRepository notificationRepo) {
        this.repository = notificationRepo;
        // Inizializza familyId a null di default
        familyId.setValue(null);
    }
    
    // Imposta la famiglia corrente (chiamato dal Fragment/Activity)
    public void setFamilyId(String id) {
        familyId.setValue(id);
    }

    // LiveData combinato: reagisce sia al cambio data che al cambio famiglia
    public LiveData<List<Notification>> getNotificationsForSelectedDay() {
        return Transformations.switchMap(selectedDate, date -> 
            Transformations.switchMap(familyId, fId -> 
                repository.getNotificationsForDay(date, fId)
            )
        );
    }

    public void setSelectedDate(long millis) {
        selectedDate.setValue(millis);
    }

    public void saveNotification(Notification notification) {
        // Assicura che la notifica abbia il familyId corrente
        notification.setFamilyId(familyId.getValue());
        repository.saveNotification(notification);
    }

    public void deleteNotification(Notification notification) {
        repository.deleteNotification(notification);
    }

    public void deleteRepeatingNotification(Notification notification) {
        repository.deleteRepeatingNotification(notification);
    }

    public String formatDateTime(Notification n, Context context) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(n.getNotificationTime());

        String date = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
                .format(cal.getTime());

        String time = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault())
                .format(cal.getTime());

        return date + " " + time;
    }

    public String formatRepeat(Notification n, Context context) {
        int weeks = n.getRepeatWeeks();
        return context.getResources().getQuantityString(
                R.plurals.repeat_weeks,
                weeks,
                weeks
        );
    }
}
