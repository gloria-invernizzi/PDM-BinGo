package com.application.bingo.util;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import com.application.bingo.R;
import com.application.bingo.repository.SettingsRepository;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NotificationWorker extends Worker {

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());

        String waste = getInputData().getString("wasteType");

        String channelId = "waste_channel";

        // Leggo le impostazioni dell'utente
        SettingsRepository settings = new SettingsRepository(getApplicationContext());
        boolean soundEnabled = settings.isSoundEnabled();
        boolean vibrationEnabled = settings.isVibrationEnabled(); // crea isVibrationEnabled se non esiste

        // Creazione canale notifiche (solo la prima volta)
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            if (getApplicationContext().checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                return Result.failure();
            }

            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Rifiuti",
                    NotificationManager.IMPORTANCE_HIGH
            );

            // Configuro il suono
            if (soundEnabled) {
                Uri defaultSound = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI;
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();
                channel.setSound(defaultSound, audioAttributes);
            } else {
                channel.setSound(null, null);
            }

            // Configuro la vibrazione
            channel.enableVibration(vibrationEnabled);

            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "waste_channel")
                .setContentTitle("Preparare il bidone")
                .setContentText("Domani ritirano " + waste)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        manager.notify((int) System.currentTimeMillis(), builder.build());

        return Result.success();
    }
}
