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
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.application.bingo.R;
import com.application.bingo.model.Notification;
import com.application.bingo.ui.adapter.NotificationAdapter;
import com.application.bingo.ui.viewmodel.NotificationViewModel;
import com.application.bingo.ui.viewmodel.SettingsViewModel;
import com.application.bingo.ui.viewmodel.ViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarFragment extends Fragment {

    private CalendarView calendarView;
    private long selectedDateMillis = 0;
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;

    private NotificationViewModel notificationViewModel;
    private SettingsViewModel settingsViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewModelFactory factory =
                new ViewModelFactory(requireActivity().getApplication());

        notificationViewModel = new ViewModelProvider(this, factory).get(NotificationViewModel.class);
        settingsViewModel =
                new ViewModelProvider(this, factory).get(SettingsViewModel.class);

        settingsViewModel.loadNotificationsState();

        calendarView = view.findViewById(R.id.calendarView);
        recyclerView = view.findViewById(R.id.recyclerNotifications);
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAdd);

        adapter = new NotificationAdapter(new ArrayList<>(), notificationViewModel);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Giorno iniziale
        selectedDateMillis = System.currentTimeMillis();
        notificationViewModel.setSelectedDate(selectedDateMillis);

        notificationViewModel.getNotificationsForSelectedDay()
                .observe(getViewLifecycleOwner(), adapter::updateList);

        // Cambio giorno
        calendarView.setOnDateChangeListener((cv, year, month, dayOfMonth) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, dayOfMonth, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
            selectedDateMillis = cal.getTimeInMillis();
            notificationViewModel.setSelectedDate(selectedDateMillis);
        });

        // Listener delete
        adapter.setOnItemDeleteListener(new NotificationAdapter.OnItemDeleteListener() {
            @Override
            public void onItemDelete(Notification notification) {
                // Cancella una singola notifica
                notificationViewModel.deleteNotification(notification);
            }

            @Override
            public void onItemDeleteRepeating(Notification notification) {
                // Cancella tutte le ripetizioni fino a fine anno
                notificationViewModel.deleteRepeatingNotification(notification);
            }
        });

        // Nuova notifica
        fabAdd.setOnClickListener(v ->
                settingsViewModel.getNotificationsLiveData().observe(getViewLifecycleOwner(), enabled -> {
                    if (!enabled) {
                        Toast.makeText(getContext(), R.string.notifications_disabled, Toast.LENGTH_SHORT).show();
                    } else {
                        openBinSelectionDialog(selectedDateMillis);
                    }
                })
        );
    }

    private void openBinSelectionDialog(long dateMillis) {
        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_choose_bin, null);

        RadioGroup group = dialogView.findViewById(R.id.groupBins);
        TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
        Spinner spinnerRepeat = dialogView.findViewById(R.id.spinnerRepeat);
        Button buttonSave = dialogView.findViewById(R.id.buttonSave);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);

        // Seleziona il primo RadioButton di default
        if (group.getChildCount() > 0) ((RadioButton) group.getChildAt(0)).setChecked(true);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        buttonSave.setOnClickListener(v -> {
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

            Notification notification = new Notification(cal.getTimeInMillis(), wasteType, repeatWeeks);
            notificationViewModel.saveNotification(notification);
            dialog.dismiss();
        });

        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
