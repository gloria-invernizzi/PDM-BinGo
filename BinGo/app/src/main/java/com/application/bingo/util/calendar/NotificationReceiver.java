package com.application.bingo.util.calendar;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import com.application.bingo.R;
import com.application.bingo.repository.SettingsRepository;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String waste = intent.getStringExtra("wasteType");

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "waste_channel";

        // Leggo le impostazioni dell'utente
        SettingsRepository settings = new SettingsRepository(context);
        boolean soundEnabled = settings.isSoundEnabled();
        boolean vibrationEnabled = settings.isVibrationEnabled(); // crea isVibrationEnabled se non esiste

        // Creazione canale notifiche (solo la prima volta)
        if (android.os.Build.VERSION.SDK_INT >= 26) {
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

        // Creo la notifica
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle("Preparare il bidone")
                .setContentText("Domani ritirano " + waste)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true)
                .setOnlyAlertOnce(false); // assicura che suoni e vibri ad ogni notifica

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    public static void updateNotificationChannel(Context context) {
        // Recupero le impostazioni dell'app tramite il repository
        SettingsRepository settings = new SettingsRepository(context);
        boolean soundEnabled = settings.isSoundEnabled();     // Controlla se l'utente vuole il suono
        boolean vibrationEnabled = settings.isVibrationEnabled(); // Controlla se l'utente vuole la vibrazione

        // Ottengo il NotificationManager di sistema
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "waste_channel"; // ID del canale notifiche già creato

        // Verifico che siamo su Android 8+ (API 26+) perché i canali esistono solo da qui
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            // Recupero il canale esistente tramite l'ID
            NotificationChannel channel = manager.getNotificationChannel(channelId);

            if (channel != null) {
                // Se l'utente ha abilitato il suono, imposto il suono di default del sistema
                if (soundEnabled) {
                    Uri defaultSound = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI;
                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build();
                    channel.setSound(defaultSound, audioAttributes); // Imposta il suono
                } else {
                    // Se il suono è disattivato, imposto null così la notifica non emette suono
                    channel.setSound(null, null);
                }

                // Abilita o disabilita la vibrazione in base alle impostazioni
                channel.enableVibration(vibrationEnabled);

                // Ri-crea il canale con le modifiche apportate (Android aggiorna il canale esistente)
                manager.createNotificationChannel(channel);
            }
        }
    }

}
