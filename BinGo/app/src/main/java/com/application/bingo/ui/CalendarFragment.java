package com.application.bingo.ui;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.application.bingo.R;
import com.application.bingo.model.Notification;
import com.application.bingo.ui.adapter.NotificationAdapter;
import com.application.bingo.repository.NotificationRepository;
import com.application.bingo.viewmodel.NotificationViewModel;
import com.application.bingo.repository.SettingsRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarFragment extends Fragment {

    private CalendarView calendarView;
    private long selectedDateMillis = 0;
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;

    private NotificationViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(NotificationViewModel.class);

        calendarView = view.findViewById(R.id.calendarView);
        recyclerView = view.findViewById(R.id.recyclerNotifications);
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAdd);

        adapter = new NotificationAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Giorno iniziale
        selectedDateMillis = System.currentTimeMillis();
        viewModel.getNotificationsForDay(selectedDateMillis)
                .observe(getViewLifecycleOwner(), adapter::updateList);

        // Cambio giorno
        calendarView.setOnDateChangeListener((cv, year, month, dayOfMonth) -> {
            selectedDateMillis = NotificationRepository.convertToMillis(year, month, dayOfMonth);
            viewModel.getNotificationsForDay(selectedDateMillis)
                    .observe(getViewLifecycleOwner(), adapter::updateList);
        });

        // Listener delete
        adapter.setOnItemDeleteListener(new NotificationAdapter.OnItemDeleteListener() {
            @Override
            public void onItemDelete(Notification notification) {
                viewModel.deleteNotification(notification); // singola
            }

            @Override
            public void onItemDeleteRepeating(Notification notification) {
                viewModel.deleteRepeatingNotification(notification); // tutte le ripetizioni
            }
        });

        // Nuova notifica
        fabAdd.setOnClickListener(v ->
                openBinSelectionDialog(selectedDateMillis, null)
        );
    }


    private void openBinSelectionDialog(long dateMillis, @Nullable Notification existingNotification) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_choose_bin, null);

        RadioGroup group = dialogView.findViewById(R.id.groupBins);
        TimePicker timePicker = dialogView.findViewById(R.id.timePicker);

        Spinner spinnerRepeat = dialogView.findViewById(R.id.spinnerRepeat);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"1 settimana", "2 settimane", "3 settimane", "4 settimane"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRepeat.setAdapter(adapter);

        // Se stiamo modificando una notifica esistente, preimpostiamo i valori
        if (existingNotification != null) {
            String type = existingNotification.getWasteType();
            for (int i = 0; i < group.getChildCount(); i++) {
                RadioButton rb = (RadioButton) group.getChildAt(i);
                if (rb.getText().toString().equals(type)) {
                    rb.setChecked(true);
                    break;
                }
            }

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(existingNotification.getNotificationTime());
            timePicker.setHour(cal.get(Calendar.HOUR_OF_DAY));
            timePicker.setMinute(cal.get(Calendar.MINUTE));
            spinnerRepeat.setSelection(existingNotification.getRepeatWeeks() - 1);
        } else {
            // Seleziona il primo RadioButton di default
            if (group.getChildCount() > 0) ((RadioButton) group.getChildAt(0)).setChecked(true);
        }

        new AlertDialog.Builder(getContext())
                .setTitle(existingNotification == null ? "Aggiungi notifica" : "Modifica notifica")
                .setView(dialogView)
                .setPositiveButton("Salva", (dialog, which) -> {
                    int selectedId = group.getCheckedRadioButtonId();
                    if (selectedId == -1) return;

                    RadioButton rb = dialogView.findViewById(selectedId);
                    String wasteType = rb.getText().toString();
                    int hour = timePicker.getHour();
                    int minute = timePicker.getMinute();
                    int repeatWeeks = spinnerRepeat.getSelectedItemPosition() + 1;

                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(dateMillis);
                    cal.set(Calendar.HOUR_OF_DAY, hour);
                    cal.set(Calendar.MINUTE, minute);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);

                    //  Controllo in base alle impostazioni se le notifiche sono attive
                    SettingsRepository settingsRepo = new SettingsRepository(requireContext());
                    if (!settingsRepo.isNotificationsEnabled()) {
                        Toast.makeText(getContext(),
                                "Notifiche disattivate nelle impostazioni",
                                Toast.LENGTH_SHORT).show();
                        return; //  Blocca la creazione della notifica
                    }

                    Notification notification;
                    if (existingNotification != null) {
                        // Modifica
                        existingNotification.setWasteType(wasteType);
                        existingNotification.setNotificationTime(cal.getTimeInMillis());
                        existingNotification.setRepeatWeeks(repeatWeeks);
                        notification = existingNotification;
                    } else {
                        // Nuova
                        notification = new Notification(cal.getTimeInMillis(), wasteType, repeatWeeks);
                    }

                    // Salva nel DB tramite ViewModel
                    viewModel.saveNotification(notification);
                })
                .setNegativeButton("Annulla", null)
                .show();
    }
}
