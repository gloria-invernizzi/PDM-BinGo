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
import android.widget.Toast;

import com.application.bingo.R;
import com.application.bingo.util.calendar.WasteManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class CalendarFragment extends Fragment {
    private CalendarView calendarView;
    private long selectedDateMillis = 0;
    private TextView infoText;
    private WasteManager wasteManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        wasteManager = new WasteManager(getContext());

        calendarView = view.findViewById(R.id.calendarView);
        infoText = view.findViewById(R.id.info);
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAdd);

        calendarView.setOnDateChangeListener((calendarView, year, month, dayOfMonth) -> {
            selectedDateMillis = WasteManager.convertToMillis(year, month, dayOfMonth);
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
        String key = wasteManager.getDayKey(dateMillis);
        List<String> wastes = wasteManager.getWaste(key);
        if (wastes.isEmpty()) {
            infoText.setText("Nessuna notifica impostata");
        } else {
            StringBuilder sb = new StringBuilder("Notifiche impostate:\n");
            for (String w : wastes) {
                sb.append("• ").append(w).append("\n");
            }
            infoText.setText(sb.toString());
        }
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

                    wasteManager.saveWaste(dateMillis, hour, minute, wasteType);
                    wasteManager.scheduleNotification(dateMillis, hour, minute, wasteType);

                    showWasteSummary(dateMillis);
                })
                .setNegativeButton("Annulla", null)
                .show();
    }
}
