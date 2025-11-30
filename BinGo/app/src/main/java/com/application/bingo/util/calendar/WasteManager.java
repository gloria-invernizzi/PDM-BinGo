package com.application.bingo.util.calendar;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import androidx.annotation.RequiresPermission;

import java.util.Calendar;

public class WasteManager {

    private final Context context;

    public WasteManager(Context ctx) {
        this.context = ctx;
    }

    public void saveWasteForDay(long dateMillis, String wasteType, TextView infoText) {
        // Qui puoi salvare in Room
        // AppDatabase db = AppDatabase.getInstance(context);
        // db.userDao().saveWaste(...);

        String message = "Salvato: " + wasteType + " in data " + dateMillis;
        infoText.append("\n" + message);
    }

    public void scheduleNotification(long dateMillis, int hour, int minute, String wasteType) {
        long notificationTime = computeNotificationTime(dateMillis, hour, minute);

        Intent intent = new Intent(context, WasteNotificationReceiver.class);
        intent.putExtra("wasteType", wasteType);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (int) dateMillis,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        try {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent);
        } catch (SecurityException e) {
            System.out.println("Errore: permesso SCHEDULE_EXACT_ALARM mancante");
        }
    }
    private long computeNotificationTime(long dateMillis, int hour, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateMillis);

        // Imposta l’orario scelto
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
