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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    // layout per tema, lingua, notifiche, suono, vibrazione
    private LinearLayout layoutTema;
    private LinearLayout layoutLingua;
    private LinearLayout layoutNotifiche;
    private LinearLayout layoutSuono;
    private LinearLayout layoutVibrazione;
    private LinearLayout layoutCambiaPassword;
    private Switch switchNotifiche;
    private Switch switchSuono;
    private Switch switchVibrazione;

    // viewmodel
    private SettingsViewModel settingsVM;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflating del layout del fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // creo il repository
        SettingsRepository settingsRepo = new SettingsRepository(requireContext());

        // creo la factory e il viewmodel
        settingsVM = new ViewModelProvider(
                this,
                new ViewModelProvider.Factory() {
                    @NonNull
                    @Override
                    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                        if (modelClass.isAssignableFrom(SettingsViewModel.class)) {
                            return (T) new SettingsViewModel(settingsRepo);
                        }
                        throw new IllegalArgumentException("unknown viewmodel class");
                    }
                }
        ).get(SettingsViewModel.class);

        // trovo le views nel layout
        layoutTema = view.findViewById(R.id.layout_tema);
        layoutLingua = view.findViewById(R.id.layout_lingua);
        layoutNotifiche = view.findViewById(R.id.layout_notifiche);
        layoutSuono = view.findViewById(R.id.layout_suono);
        layoutVibrazione = view.findViewById(R.id.layout_vibrazione);
        switchNotifiche = view.findViewById(R.id.switch_notifiche);
        switchSuono = view.findViewById(R.id.switch_suono);
        switchVibrazione = view.findViewById(R.id.switch_vibrazione);
        layoutCambiaPassword = view.findViewById(R.id.layout_cambia_password);

        // cambio password
        layoutCambiaPassword.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.changePasswordFragment);
        });

        // -----------------------------
        // NOTIFICHE: carico stato e osservo cambiamenti
        // -----------------------------
        settingsVM.loadNotificationsState(); // carica lo stato salvato
        settingsVM.getNotificationsLiveData().observe(getViewLifecycleOwner(), enabled -> {
            switchNotifiche.setChecked(enabled); // aggiorna lo switch quando cambia
        });

        // cambio stato quando l'utente tocca lo switch
        switchNotifiche.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settingsVM.setNotificationsEnabled(isChecked);
        });

        // -----------------------------
        // SUONO E VIBRAZIONE: carico stato iniziale
        // -----------------------------
        switchSuono.setChecked(settingsVM.isSoundEnabled());
        switchVibrazione.setChecked(settingsVM.isVibrationEnabled());

        switchSuono.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settingsVM.setSoundEnabled(isChecked);
        });

        switchVibrazione.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settingsVM.setVibrationEnabled(isChecked);
        });

        // -----------------------------
        // CLICK LISTENER
        // -----------------------------
        layoutTema.setOnClickListener(v -> showThemeDialog());
        layoutLingua.setOnClickListener(v -> showLanguageDialog());
    }

    // -----------------------------
    // DIALOG PER SCEGLIERE IL TEMA
    // -----------------------------
    private void showThemeDialog() {
        String[] temi = {"chiaro", "scuro"}; // nomi da mostrare nel dialog
        int checkedItem = settingsVM.isDarkTheme() ? 1 : 0; // selezione iniziale

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("scegli tema")
                .setSingleChoiceItems(temi, checkedItem, (dialog, which) -> {
                    if (which == 0) {
                        settingsVM.setThemeLight(); // salva tema chiaro
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // applica tema chiaro
                    } else {
                        settingsVM.setThemeDark(); // salva tema scuro
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); // applica tema scuro
                    }
                    dialog.dismiss();

                    // ⚡ ricrea l'activity per applicare subito il tema
                    requireActivity().recreate();
                })
                .show();
    }

    // -----------------------------
    // DIALOG PER SCEGLIERE LA LINGUA
    // -----------------------------
    private void showLanguageDialog() {

        // nomi visualizzati nel dialog
        String[] lingueLabel = {"italiano", "inglese", "spagnolo"};

        // codici reali salvati
        String[] lingueCode = {"it", "en", "es"};

        // lingua salvata
        String savedLang = settingsVM.getLanguage();

        if (savedLang == null || savedLang.isEmpty()) {
            savedLang = getResources().getConfiguration()
                    .getLocales()
                    .get(0)
                    .getLanguage();
        }

        // trovo l’indice giusto da selezionare
        int checkedItem = 0;
        for (int i = 0; i < lingueCode.length; i++) {
            if (lingueCode[i].equalsIgnoreCase(savedLang)) {
                checkedItem = i;
                break;
            }
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("scegli lingua")
                .setSingleChoiceItems(lingueLabel, checkedItem, (dialog, which) -> {

                    String selectedCode = lingueCode[which];

                    // salva la lingua scelta
                    settingsVM.setLanguage(selectedCode);

                    // applica subito la lingua nell'app
                    updateLocale(selectedCode);

                    dialog.dismiss();
                })
                .show();
    }

    // -----------------------------
    // METODO PRIVATO PER AGGIORNARE LA LINGUA DELL'APP
    // -----------------------------
    private void updateLocale(String langCode) {
        // crea un oggetto Locale con la lingua scelta
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale); // imposta il locale di default nell'app

        // aggiorna la configurazione delle risorse
        Resources res = requireActivity().getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale); // imposta la nuova lingua

        res.updateConfiguration(config, res.getDisplayMetrics());

        // ricrea l'activity per applicare subito i nuovi testi
        requireActivity().recreate();
    }
}
