package com.application.bingo.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.application.bingo.R;
import com.application.bingo.repository.SettingsRepository;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsFragment extends Fragment {

    private LinearLayout layoutTema;
    private SettingsRepository settingsRepo;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        settingsRepo = new SettingsRepository(requireContext());

        layoutTema = view.findViewById(R.id.layout_tema);
        layoutTema.setOnClickListener(v -> showThemeDialog());
    }

    private void showThemeDialog() {
        String[] temi = {"Chiaro", "Scuro"};
        int checkedItem = settingsRepo.isDarkTheme() ? 1 : 0; // recupera il tema salvato

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Scegli tema")
                .setSingleChoiceItems(temi, checkedItem, (dialog, which) -> {
                    if (which == 0) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        settingsRepo.setDarkTheme(false);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        settingsRepo.setDarkTheme(true);
                    }
                    dialog.dismiss();
                })
                .show();
    }
}
