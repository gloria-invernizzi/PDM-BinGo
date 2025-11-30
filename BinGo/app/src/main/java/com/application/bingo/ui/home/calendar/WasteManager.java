package com.application.bingo.ui.home.calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class WasteManager {

    private final Context context;

    public WasteManager(Context ctx) {
        this.context = ctx;
    }

    public void saveWasteForDay(long dateMillis, String wasteType) {
        // Qui puoi salvare in Room
        // AppDatabase db = AppDatabase.getInstance(context);
        // db.userDao().saveWaste(...);

        // Per ora stampa
        System.out.println("Salvato: " + wasteType + " in data " + dateMillis);
    }

    public void scheduleNotification(long dateMillis, int hour, int minute, String wasteType) {

        long notificationTime = computePreviousDayNotification(dateMillis, hour, minute);

        Intent intent = new Intent(context, WasteNotificationReceiver.class);
        intent.putExtra("wasteType", wasteType);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (int) dateMillis,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent);
    }

    private long computePreviousDayNotification(long dateMillis, int hour, int minute) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateMillis);

        // Vai al giorno prima
        cal.add(Calendar.DAY_OF_MONTH, -1);

        // Imposta orario per la notifica
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);

        return cal.getTimeInMillis();
    }

    public static long convertToMillis(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, 0, 0, 0);
        return cal.getTimeInMillis();
    }
}
