package com.application.bingo.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.application.bingo.R;
import com.application.bingo.model.Notification;
import com.application.bingo.repository.SettingsRepository;
import com.application.bingo.util.calendar.NotificationManager;
import com.application.bingo.util.database.AppDatabase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.List;

public class CalendarFragment extends Fragment {
    private CalendarView calendarView;
    private long selectedDateMillis = 0;
    private TextView infoText;
    private NotificationManager notificationManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        notificationManager = new NotificationManager(getContext());
        selectedDateMillis = System.currentTimeMillis();

        calendarView = view.findViewById(R.id.calendarView);
        infoText = view.findViewById(R.id.info);
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAdd);

        calendarView.setOnDateChangeListener((calendarView, year, month, dayOfMonth) -> {
            selectedDateMillis = NotificationManager.convertToMillis(year, month, dayOfMonth);
            showWasteSummary(selectedDateMillis);
        });

        fabAdd.setOnClickListener(v -> {
            if (selectedDateMillis != 0) {
                openBinSelectionDialog(selectedDateMillis);
            } else {
                Toast.makeText(getContext(), "Seleziona prima un giorno", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showWasteSummary(long dateMillis) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // Esegui query in background
            List<Notification> list = notificationManager.getNotificationsForDay(dateMillis);

            // Poi aggiorna la UI sul main thread
            if (getActivity() == null) return;
            getActivity().runOnUiThread(() -> {
                if (list == null || list.isEmpty()) {
                    infoText.setText("Nessuna notifica impostata");
                    return;
                }

                StringBuilder sb = new StringBuilder("Notifiche impostate:\n\n");

                for (Notification n : list) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(n.getNotificationTime());
                    int hour = cal.get(Calendar.HOUR_OF_DAY);
                    int minute = cal.get(Calendar.MINUTE);

                    String formatted = String.format("• %s alle %02d:%02d (ogni %d settimane)",
                            n.getWasteType(), hour, minute, n.getRepeatWeeks());
                    sb.append(formatted).append("\n");
                }

                infoText.setText(sb.toString());
            });
        });
    }

    @SuppressLint("ScheduleExactAlarm")
    private void openBinSelectionDialog(long dateMillis) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_choose_bin, null);

        RadioGroup group = dialogView.findViewById(R.id.groupBins);
        // Seleziona il primo RadioButton di default
        if (group.getChildCount() > 0) {
            ((RadioButton) group.getChildAt(0)).setChecked(true);
        }

        TimePicker timePicker = dialogView.findViewById(R.id.timePicker);

        Spinner spinnerRepeat = dialogView.findViewById(R.id.spinnerRepeat);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"1 settimana", "2 settimane", "3 settimane", "4 settimane"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRepeat.setAdapter(adapter);

        new AlertDialog.Builder(getContext())
                .setTitle("Scegli il rifiuto")
                .setView(dialogView)
                .setPositiveButton("Salva", (dialog, which) -> {
                    int selectedId = group.getCheckedRadioButtonId();
                    if (selectedId == -1) return;

                    RadioButton rb = dialogView.findViewById(selectedId);
                    String wasteType = rb.getText().toString();

                    int hour = timePicker.getHour();
                    int minute = timePicker.getMinute();
                    int repeatWeeks = spinnerRepeat.getSelectedItemPosition() + 1;

                    //  Controllo in base alle impostazioni se le notifiche sono attive
                    SettingsRepository settingsRepo = new SettingsRepository(requireContext());
                    if (!settingsRepo.isNotificationsEnabled()) {
                        Toast.makeText(getContext(),
                                "Notifiche disattivate nelle impostazioni",
                                Toast.LENGTH_SHORT).show();
                        return; //  Blocca la creazione della notifica
                    }

// Salva e pianifica la notifica
                    notificationManager.saveNotification(dateMillis, hour, minute, wasteType, repeatWeeks);

                    showWasteSummary(dateMillis);
                })
                .setNegativeButton("Annulla", null)
                .show();
    }
}
