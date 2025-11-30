package com.application.bingo.ui.home.calendar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.application.bingo.R;
import com.application.bingo.util.calendar.WasteManager;

public class CalendarFragment extends Fragment {
    private CalendarView calendarView;
    private TextView txtSelectedDayInfo;
    private TextView infoText;
    private long selectedDateMillis = 0;

    private WasteManager wasteManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        wasteManager = new WasteManager(getContext());

        calendarView = view.findViewById(R.id.calendarView);
        txtSelectedDayInfo = view.findViewById(R.id.txtSelectedDayInfo);
        infoText = view.findViewById(R.id.infoText);


        calendarView.setOnDateChangeListener((calendarView, year, month, dayOfMonth) -> {
            selectedDateMillis = WasteManager.convertToMillis(year, month, dayOfMonth);
            txtSelectedDayInfo.setText("Giorno selezionato: " + dayOfMonth + "/" + (month + 1));

            openBinSelectionDialog(selectedDateMillis);
        });

    }
    @SuppressLint("ScheduleExactAlarm")
    private void openBinSelectionDialog(long dateMillis) {

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_choose_bin, null);

        RadioGroup group = dialogView.findViewById(R.id.groupBins);
        TimePicker timePicker = dialogView.findViewById(R.id.timePicker);


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

                    wasteManager.saveWasteForDay(dateMillis, wasteType, infoText);
                    wasteManager.scheduleNotification(dateMillis, hour, minute, wasteType);

                })
                .setNegativeButton("Annulla", null)
                .show();
    }

}