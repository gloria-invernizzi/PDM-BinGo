package com.application.bingo.util.calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WasteManager {

    private final Context context;
    private final Map<String, List<String>> wasteMap = new HashMap<>();
    public WasteManager(Context ctx) {
        this.context = ctx;
    }

    public void saveNotification(long dateMillis, int hour, int minute, String wasteType, int repeatWeeks) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateMillis);

        Calendar endOfYear = Calendar.getInstance();
        endOfYear.set(Calendar.MONTH, Calendar.DECEMBER);
        endOfYear.set(Calendar.DAY_OF_MONTH, 31);
        endOfYear.set(Calendar.HOUR_OF_DAY, 23);
        endOfYear.set(Calendar.MINUTE, 59);
        endOfYear.set(Calendar.SECOND, 59);
        endOfYear.set(Calendar.MILLISECOND, 999);

        while (cal.getTimeInMillis() <= endOfYear.getTimeInMillis()) {
            long currentDayMillis = cal.getTimeInMillis();
            String key = getDayKey(currentDayMillis);

            wasteMap.putIfAbsent(key, new ArrayList<>());

            String entry = String.format("%02d:%02d - %s", hour, minute, wasteType);
            wasteMap.get(key).add(entry);

            scheduleNotification(currentDayMillis, hour, minute, wasteType);

            cal.add(Calendar.DAY_OF_YEAR, 7 * repeatWeeks);
        }

        System.out.println(wasteMap.toString());
    }

    public List<String> getWaste(String day) {
        return wasteMap.getOrDefault(day, new ArrayList<>());
    }

    private void scheduleNotification(long dateMillis, int hour, int minute, String wasteType) {
        long notificationTime = computeNotificationTime(dateMillis, hour, minute);

        Intent intent = new Intent(context, WasteNotificationReceiver.class);
        intent.putExtra("wasteType", wasteType);
        int requestCode = (int) (System.currentTimeMillis() & 0xfffffff);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
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
    public String getDayKey(long dateMillis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateMillis);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return String.format("%04d-%02d-%02d", year, month, day);
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
