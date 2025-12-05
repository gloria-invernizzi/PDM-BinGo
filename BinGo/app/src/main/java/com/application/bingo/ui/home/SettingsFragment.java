package com.application.bingo.ui.home;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.application.bingo.R;
import com.application.bingo.repository.SettingsRepository;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    // layout per tema e lingua
    private LinearLayout layoutTema;
    private LinearLayout layoutLingua;

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

        // click listener per il tema
        layoutTema.setOnClickListener(v -> showThemeDialog());

        // click listener per la lingua
        layoutLingua.setOnClickListener(v -> showLanguageDialog());
    }

    // mostra il dialog per scegliere il tema
    private void showThemeDialog() {
        String[] temi = {"chiaro", "scuro"};
        int checkedItem = settingsVM.isDarkTheme() ? 1 : 0;

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("scegli tema")
                .setSingleChoiceItems(temi, checkedItem, (dialog, which) -> {
                    if (which == 0) {
                        settingsVM.setThemeLight();
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    } else {
                        settingsVM.setThemeDark();
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }
                })
                .show();
    }

    // mostra il dialog per scegliere la lingua
    private void showLanguageDialog() {
        // array di lingue supportate
        String[] lingue = {"italiano", "inglese", "spagnolo"};

        // recupera la lingua salvata e trova l'indice corrispondente
        String savedLang = settingsVM.getLanguage();
        if (savedLang == null || savedLang.isEmpty()) {
            // lingua del telefono
            savedLang = getResources().getConfiguration().getLocales().get(0).getLanguage();
        }
        int checkedItem = 0; // default
        for (int i = 0; i < lingue.length; i++) {
            if (lingue[i].equalsIgnoreCase(savedLang)) {
                checkedItem = i;

            }
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("scegli lingua")
                .setSingleChoiceItems(lingue, checkedItem, (dialog, which) -> {
                    // salva la lingua selezionata
                    String selectedLang;
                    switch (which) {
                        case 0:
                            selectedLang = "it";
                            break;
                        case 1:
                            selectedLang = "en";
                            break;
                        case 2:
                            selectedLang = "es";
                            break;
                        default:
                            selectedLang = "it";
                    }
                    settingsVM.setLanguage(selectedLang);
                    updateLocale(selectedLang);  // metodo helper

                    dialog.dismiss();
                })
                .show();
    }

    /* metodo privato del fragment, prende in ingresso un codice lingua ("it", "en", "es")
   serve a cambiare la lingua dell’app al volo */
    private void updateLocale(String langCode) {
        // crea un nuovo locale
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        // ottiene le risorse e la configurazione attuale
        Resources res = requireContext().getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale);

        // aggiorna le risorse dell'app
        res.updateConfiguration(config, res.getDisplayMetrics());

        // ricrea l'activity per applicare i nuovi testi
        requireActivity().recreate();
    }
//NON VA, NON CAMBIA LA LINGUA


}

