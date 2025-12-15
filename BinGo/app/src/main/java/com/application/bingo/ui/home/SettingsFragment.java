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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.application.bingo.R;
import com.application.bingo.ui.viewmodel.SettingsViewModel;
import com.application.bingo.ui.viewmodel.ViewModelFactory;
import com.application.bingo.util.calendar.NotificationProcessor;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Locale;

/**
 * Fragment per gestire le impostazioni dell'app:
 * tema, lingua, notifiche, suono e vibrazione.
 */
public class SettingsFragment extends Fragment {

    private LinearLayout layoutTema, layoutLingua, layoutCambiaPassword, layoutCambiaEmail;

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

        // Creo ViewModel
        ViewModelFactory factory =
                new ViewModelFactory(requireActivity().getApplication());

        settingsVM = new ViewModelProvider(this, factory).get(SettingsViewModel.class);

        // Trovo le view
        layoutTema = view.findViewById(R.id.layout_tema);
        layoutLingua = view.findViewById(R.id.layout_lingua);
        layoutCambiaPassword = view.findViewById(R.id.layout_cambia_password);

        switchNotifiche = view.findViewById(R.id.switch_notifiche);
        switchSuono = view.findViewById(R.id.switch_suono);
        switchVibrazione = view.findViewById(R.id.switch_vibrazione);

        // Cambio password
        layoutCambiaPassword.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.changePasswordFragment));


        //Cambio email
        layoutCambiaEmail = view.findViewById(R.id.layout_cambia_email);
        layoutCambiaEmail.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.changeEmailFragment));

        // ---------------- Notifiche ----------------
        settingsVM.loadNotificationsState();
        settingsVM.getNotificationsLiveData().observe(getViewLifecycleOwner(), enabled -> {
            if (switchNotifiche != null) switchNotifiche.setChecked(enabled);
        });

        if (switchNotifiche != null) {
            switchNotifiche.setOnCheckedChangeListener((buttonView, isChecked) -> {
                try {
                    settingsVM.setNotificationsEnabled(isChecked);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getContext() != null) {
                        android.widget.Toast.makeText(getContext(),
                                "Errore nel salvare impostazioni notifiche",
                                android.widget.Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        // ---------------- Suono e Vibrazione ----------------
        if (switchSuono != null) switchSuono.setChecked(settingsVM.isSoundEnabled());
        if (switchVibrazione != null) switchVibrazione.setChecked(settingsVM.isVibrationEnabled());

        if (switchSuono != null) {
            switchSuono.setOnCheckedChangeListener((buttonView, isChecked) -> {
                try {
                    settingsVM.setSoundEnabled(isChecked);
                    if (getContext() != null) NotificationProcessor.updateNotificationChannel(getContext());
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getContext() != null) {
                        android.widget.Toast.makeText(getContext(),
                                "Errore nel salvare impostazioni suono",
                                android.widget.Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        // Listener vibrazione: salva e aggiorna canale notifiche
        if (switchVibrazione != null) {
            switchVibrazione.setOnCheckedChangeListener((buttonView, isChecked) -> {
                try {
                    settingsVM.setVibrationEnabled(isChecked);
                    if (getContext() != null) NotificationProcessor.updateNotificationChannel(getContext());
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getContext() != null) {
                        android.widget.Toast.makeText(getContext(),
                                "Errore nel salvare impostazioni vibrazione",
                                android.widget.Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        // ---------------- Tema e Lingua ----------------
        try {
            layoutTema.setOnClickListener(v -> showThemeDialog());
            layoutLingua.setOnClickListener(v -> showLanguageDialog());
        } catch (Exception e) {
            e.printStackTrace();
            if (getContext() != null) {
                android.widget.Toast.makeText(getContext(),
                        "Errore nel caricare le impostazioni",
                        android.widget.Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Dialog per scegliere tema
    private void showThemeDialog() {
        try {
            String[] temi = getResources().getStringArray(R.array.themes);
            int checkedItem = settingsVM.isDarkTheme() ? 1 : 0;

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.choose_theme)
                    .setSingleChoiceItems(temi, checkedItem, (dialog, which) -> {
                        try {
                            if (which == 0) {
                                settingsVM.setThemeLight();
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            } else {
                                settingsVM.setThemeDark();
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                            }
                            dialog.dismiss();
                            if (isAdded()) requireActivity().recreate();
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (getContext() != null)
                                android.widget.Toast.makeText(getContext(), "Errore nel salvare tema",
                                        android.widget.Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
            if (getContext() != null)
                android.widget.Toast.makeText(getContext(), "Errore nel caricare temi",
                        android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    // Dialog per scegliere lingua
    private void showLanguageDialog() {
        try {
            String[] lingueLabel = getResources().getStringArray(R.array.languages);
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
                    .setTitle(R.string.choose_language)
                    .setSingleChoiceItems(lingueLabel, checkedItem, (dialog, which) -> {
                        try {
                            String selectedCode = lingueCode[which];
                            settingsVM.setLanguage(selectedCode);
                            updateLocale(selectedCode);
                            dialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (getContext() != null)
                                android.widget.Toast.makeText(getContext(), "Errore nel salvare lingua",
                                        android.widget.Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
            if (getContext() != null)
                android.widget.Toast.makeText(getContext(), "Errore nel caricare lingue",
                        android.widget.Toast.LENGTH_SHORT).show();
        }
    }
    private void updateLocale(String langCode) {
        try {
            Locale locale = new Locale(langCode);
            Locale.setDefault(locale);

            Resources res = requireActivity().getResources();
            Configuration config = new Configuration(res.getConfiguration());
            config.setLocale(locale);
            res.updateConfiguration(config, res.getDisplayMetrics());

            if (isAdded()) requireActivity().recreate();
        } catch (Exception e) {
            e.printStackTrace();
            if (getContext() != null)
                android.widget.Toast.makeText(getContext(), "Errore nell'aggiornare la lingua",
                        android.widget.Toast.LENGTH_SHORT).show();
        }
    }
}
