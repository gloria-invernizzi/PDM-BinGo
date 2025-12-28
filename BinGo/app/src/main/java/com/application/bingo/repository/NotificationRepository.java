package com.application.bingo.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.application.bingo.model.Notification;
import com.application.bingo.util.NotificationWorker;
import com.application.bingo.database.AppDatabase;
import com.application.bingo.database.NotificationDao;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NotificationRepository {

    private final Context context;
    private final NotificationDao dao;
    public NotificationRepository(Context ctx) {
        this.context = ctx;
        AppDatabase db = AppDatabase.getInstance(ctx);
        dao = db.notificationDao();
    }

    // --- SALVATAGGIO ---
    public void saveNotification(Notification notification) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(notification.getNotificationTime());
            long repeatWeeks = notification.getRepeatWeeks();

            while (cal.getTimeInMillis() <= getEndOfYear()) {
                Notification n = new Notification(cal.getTimeInMillis(), notification.getWasteType(), notification.getRepeatWeeks());
                n.setFamilyId(notification.getFamilyId()); // Copia familyId
                dao.insert(n);
                scheduleNotification(n);
                cal.add(Calendar.WEEK_OF_YEAR, (int) repeatWeeks);
            }
        });
    }

    // --- WORKMANAGER ---
    private void scheduleNotification(Notification notification) {
        long delay = notification.getNotificationTime() - System.currentTimeMillis();

        Data data = new Data.Builder()
                .putString("wasteType", notification.getWasteType())
                .build();

        if (delay > 0){
            OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .setInputData(data)
                    .build();

            WorkManager.getInstance(context).enqueue(request);
        }
    }

    // --- ELIMINAZIONE DI UNA NOTIFICA ---
    public void deleteNotification(Notification notification) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.delete(notification);
        });
    }

    // --- ELIMINAZIONE ANCHE DELLE SUCCESSIVE ---
    public void deleteRepeatingNotification(Notification notification) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.deleteNotifications(
                    notification.getWasteType(),
                    notification.getNotificationTime(),
                    getEndOfYear()
            );
        });
    }

    // --- CARICAMENTO ---
    public LiveData<List<Notification>> getNotificationsForDay(long selectedDateMillis, String familyId) {
        long startOfDay = getStartOfDay(selectedDateMillis);
        long endOfDay = getEndOfDay(selectedDateMillis);
        return dao.getNotificationsForDay(startOfDay, endOfDay, familyId);
    }

    private long getStartOfDay(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    private long getEndOfDay(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTimeInMillis();
    }

    private long getEndOfYear(){
        Calendar endOfYear = Calendar.getInstance();
        endOfYear.set(Calendar.MONTH, Calendar.DECEMBER);
        endOfYear.set(Calendar.DAY_OF_MONTH, 31);
        endOfYear.set(Calendar.HOUR_OF_DAY, 23);
        endOfYear.set(Calendar.MINUTE, 59);
        endOfYear.set(Calendar.SECOND, 59);
        endOfYear.set(Calendar.MILLISECOND, 999);
        return endOfYear.getTimeInMillis();
    }

}
