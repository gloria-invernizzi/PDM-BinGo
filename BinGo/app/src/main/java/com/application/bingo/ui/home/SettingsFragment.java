package com.application.bingo.ui.home;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.application.bingo.R;
import com.application.bingo.repository.SettingsRepository;
import com.application.bingo.viewmodel.SettingsViewModel;
import com.application.bingo.util.calendar.NotificationProcessor;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Locale;

/**
 * Fragment per gestire le impostazioni dell'app:
 * tema, lingua, notifiche, suono e vibrazione.
 */
public class SettingsFragment extends Fragment {

    private LinearLayout layoutTema, layoutLingua, layoutNotifiche,
            layoutSuono, layoutVibrazione, layoutCambiaPassword;

    private Switch switchNotifiche, switchSuono, switchVibrazione;

    private SettingsViewModel settingsVM;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Creo repository e ViewModel
        SettingsRepository settingsRepo = new SettingsRepository(requireContext());
        settingsVM = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                if (modelClass.isAssignableFrom(SettingsViewModel.class)) {
                    return (T) new SettingsViewModel(settingsRepo);
                }
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        }).get(SettingsViewModel.class);

        // Trovo le view
        layoutTema = view.findViewById(R.id.layout_tema);
        layoutLingua = view.findViewById(R.id.layout_lingua);
        layoutNotifiche = view.findViewById(R.id.layout_notifiche);
        layoutSuono = view.findViewById(R.id.layout_suono);
        layoutVibrazione = view.findViewById(R.id.layout_vibrazione);
        layoutCambiaPassword = view.findViewById(R.id.layout_cambia_password);

        switchNotifiche = view.findViewById(R.id.switch_notifiche);
        switchSuono = view.findViewById(R.id.switch_suono);
        switchVibrazione = view.findViewById(R.id.switch_vibrazione);

        // Cambio password
        layoutCambiaPassword.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.changePasswordFragment));

        // ---------------- Notifiche ----------------
        settingsVM.loadNotificationsState();
        settingsVM.getNotificationsLiveData().observe(getViewLifecycleOwner(), enabled -> {
            switchNotifiche.setChecked(enabled);
        });

        switchNotifiche.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settingsVM.setNotificationsEnabled(isChecked);
        });

        // ---------------- Suono e Vibrazione ----------------
        switchSuono.setChecked(settingsVM.isSoundEnabled());
        switchVibrazione.setChecked(settingsVM.isVibrationEnabled());

        // Listener suono: salva e aggiorna canale notifiche
        switchSuono.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settingsVM.setSoundEnabled(isChecked);
            NotificationProcessor.updateNotificationChannel(requireContext());
        });

        // Listener vibrazione: salva e aggiorna canale notifiche
        switchVibrazione.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settingsVM.setVibrationEnabled(isChecked);
            NotificationProcessor.updateNotificationChannel(requireContext());
        });

        // ---------------- Tema e Lingua ----------------
        layoutTema.setOnClickListener(v -> showThemeDialog());
        layoutLingua.setOnClickListener(v -> showLanguageDialog());
    }

    // Dialog per scegliere tema
    private void showThemeDialog() {
        String[] temi = {"Chiaro", "Scuro"};
        int checkedItem = settingsVM.isDarkTheme() ? 1 : 0;

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Scegli tema")
                .setSingleChoiceItems(temi, checkedItem, (dialog, which) -> {
                    if (which == 0) {
                        settingsVM.setThemeLight();
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    } else {
                        settingsVM.setThemeDark();
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }
                    dialog.dismiss();
                    requireActivity().recreate();
                })
                .show();
    }

    // Dialog per scegliere lingua
    private void showLanguageDialog() {
        String[] lingueLabel = {"italiano", "inglese", "spagnolo"};
        String[] lingueCode = {"it", "en", "es"};

        String savedLang = settingsVM.getLanguage();
        if (savedLang == null || savedLang.isEmpty()) {
            savedLang = getResources().getConfiguration()
                    .getLocales()
                    .get(0)
                    .getLanguage();
        }

        int checkedItem = 0;
        for (int i = 0; i < lingueCode.length; i++) {
            if (lingueCode[i].equalsIgnoreCase(savedLang)) {
                checkedItem = i;
                break;
            }
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Scegli lingua")
                .setSingleChoiceItems(lingueLabel, checkedItem, (dialog, which) -> {
                    String selectedCode = lingueCode[which];
                    settingsVM.setLanguage(selectedCode);
                    updateLocale(selectedCode);
                    dialog.dismiss();
                })
                .show();
    }

    private void updateLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Resources res = requireActivity().getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale);
        res.updateConfiguration(config, res.getDisplayMetrics());

        requireActivity().recreate();
    }
}
