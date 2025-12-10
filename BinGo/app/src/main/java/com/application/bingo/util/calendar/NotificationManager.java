package com.application.bingo.util.calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.application.bingo.model.Notification;
import com.application.bingo.util.database.AppDatabase;
import com.application.bingo.util.database.NotificationDao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationManager {

    private final Context context;
    private final NotificationDao dao;
    public NotificationManager(Context ctx) {
        this.context = ctx;
        AppDatabase db = AppDatabase.getInstance(ctx);
        dao = db.notificationDao();
    }

    public void saveNotification(long dateMillis, int hour, int minute, String wasteType, int repeatWeeks) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            long notificationTime = computeNotificationTime(dateMillis, hour, minute);
            Calendar endOfYear = Calendar.getInstance();
            endOfYear.set(Calendar.MONTH, Calendar.DECEMBER);
            endOfYear.set(Calendar.DAY_OF_MONTH, 31);
            endOfYear.set(Calendar.HOUR_OF_DAY, 23);
            endOfYear.set(Calendar.MINUTE, 59);
            endOfYear.set(Calendar.SECOND, 59);
            endOfYear.set(Calendar.MILLISECOND, 999);

            while (notificationTime <= endOfYear.getTimeInMillis()) {
                Notification notification = new Notification(notificationTime, wasteType, repeatWeeks);
                long uid = dao.insert(notification);
                scheduleNotification(uid, notification);
                notificationTime = notificationTime + (long) repeatWeeks * 7 * 24 * 60 * 60 * 1000;
            }

        });

    }

    private void scheduleNotification(long uid, Notification notification) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("wasteType", notification.getWasteType());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (int) uid,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        try {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, notification.getNotificationTime(), pendingIntent);
        } catch (SecurityException e) {
            System.out.println("Errore: permesso SCHEDULE_EXACT_ALARM mancante");
        }
    }

    public List<Notification> getNotificationsForDay(long selectedDateMillis) {
        List<Notification> all = dao.getAll();
        Calendar calSelected = Calendar.getInstance();
        calSelected.setTimeInMillis(selectedDateMillis);

        int selYear = calSelected.get(Calendar.YEAR);
        int selMonth = calSelected.get(Calendar.MONTH);
        int selDay = calSelected.get(Calendar.DAY_OF_MONTH);

        List<Notification> result = new ArrayList<>();
        for (Notification n : all) {
            Calendar calNotif = Calendar.getInstance();
            calNotif.setTimeInMillis(n.getNotificationTime());

            if (calNotif.get(Calendar.YEAR) == selYear &&
                    calNotif.get(Calendar.MONTH) == selMonth &&
                    calNotif.get(Calendar.DAY_OF_MONTH) == selDay) {
                result.add(n);
            }
        }
        return result;
    }


    private long computeNotificationTime(long dateMillis, int hour, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateMillis);

        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTimeInMillis();
    }

    public static long convertToMillis(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, 0, 0, 0);
        return cal.getTimeInMillis();
    }

}
