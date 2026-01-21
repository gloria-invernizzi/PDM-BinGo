package com.application.bingo.ui.home.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.application.bingo.PrefsManager;
import com.application.bingo.ui.viewmodel.ProfileViewModel;
import com.google.android.material.switchmaterial.SwitchMaterial;
import android.widget.Toast;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Fragment per gestire le impostazioni dell'app:
 * tema, lingua, notifiche, suono e vibrazione.
 */
public class SettingsFragment extends Fragment {

    private LinearLayout layoutTema, layoutLingua, layoutCambiaPassword, layoutCambiaEmail,layoutEliminaAccount ;

    private SwitchMaterial switchNotifiche, switchSuono, switchVibrazione;

    private SettingsViewModel settingsVM;
    private ProfileViewModel profileVM;
    private PrefsManager prefs;


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
        profileVM = new ViewModelProvider(this, factory).get(ProfileViewModel.class);

        prefs = new PrefsManager(requireContext());
        String savedEmail = prefs.getSavedEmail();
        if (savedEmail != null) {
            profileVM.loadUser(savedEmail);
        } else {
            NavHostFragment.findNavController(this).navigate(R.id.loginFragment);
        }

        // Trova la view
        layoutTema = view.findViewById(R.id.layout_theme);
        layoutLingua = view.findViewById(R.id.layout_language);
        layoutCambiaPassword = view.findViewById(R.id.btn_change_password);
        layoutCambiaEmail = view.findViewById(R.id.btn_change_email);

        switchNotifiche = view.findViewById(R.id.switch_notifications);
        switchSuono = view.findViewById(R.id.switch_suono);
        switchVibrazione = view.findViewById(R.id.switch_vibrazione);

        // Cambio password
        layoutCambiaPassword.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.changePasswordFragment));

        // Cambio email
        layoutCambiaEmail.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.changeEmailFragment));

        // Elimina Profilo
        layoutEliminaAccount = view.findViewById(R.id.btn_delete_account);

        LinearLayout btnDelete = view.findViewById(R.id.btn_delete_account);
        btnDelete.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.delete_account_confirmation_title)
                    .setMessage(R.string.delete_account_confirmation_message)
                    .setNegativeButton(R.string.delete_account_cancel_button, (dialog, which) -> dialog.dismiss())
                    .setPositiveButton(R.string.delete_account_confirm_button, (dialog, which) -> {
                        dialog.dismiss();
                        profileVM.deleteAccount();
                    })
                    .show();
        });


        // Osserva il risultato
        profileVM.getDeleteAccountResult().observe(getViewLifecycleOwner(), result -> {
            if ("offline_error".equals(result)) {
                Toast.makeText(getContext(),
                        "Non puoi eliminare il profilo offline",
                        Toast.LENGTH_SHORT).show();
                return;
            }


            if ("account_deleted_success".equals(result)) {
                Toast.makeText(getContext(), "Profilo eliminato con successo", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_settingsFragment_to_WelcomeFragment); // crea l'azione nel nav graph
                return;
            }

            // eventuali altri errori
            Toast.makeText(getContext(), R.string.error_generic + result, Toast.LENGTH_SHORT).show();
        });

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
                        Toast.makeText(getContext(),
                                getString(R.string.error_save_notifications),
                                Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(getContext(),
                                getString(R.string.error_save_sound),
                                Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(getContext(),
                                getString(R.string.error_save_vibration),
                                Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(),
                        getString(R.string.error_load_settings),
                        Toast.LENGTH_SHORT).show();
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
                                android.widget.  Toast.makeText(getContext(),
                                        getString(R.string.error_save_theme),
                                        Toast.LENGTH_SHORT).show();


                        }
                    })
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
            if (getContext() != null)
                android.widget. Toast.makeText(getContext(),
                        getString(R.string.error_load_themes),
                        Toast.LENGTH_SHORT).show();
        }
    }

    // Dialog per scegliere lingua
    private void showLanguageDialog() {
        try {
            String[] lingueLabel = getResources().getStringArray(R.array.languages);
            String[] lingueCode = {"it", "en", "es"};
            //Se non esiste lingua salvata, usa la lingua di default del telefono
            String savedLang = settingsVM.getLanguage();
            if (savedLang == null || savedLang.isEmpty()) {
                savedLang = getResources().getConfiguration()
                        .getLocales()
                        .get(0)
                        .getLanguage();
            }

            int checkedItem = 0; //ligua selezionata di default nel dialog
            for (int i = 0; i < lingueCode.length; i++) {
                if (lingueCode[i].equalsIgnoreCase(savedLang)) {
                    checkedItem = i;
                    break;
                }
            }
            //creo Dialog
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.choose_language)
                    .setSingleChoiceItems(lingueLabel, checkedItem, (dialog, which) -> {
                        try {
                            String selectedCode = lingueCode[which]; //which: posizione lingua scelta
                            settingsVM.setLanguage(selectedCode);//salva lingua scelta
                            dialog.dismiss(); //chiude dialog
                            requireActivity().recreate(); //ricrea activity
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (getContext() != null)
                                android.widget.Toast.makeText(getContext(),
                                        getString(R.string.error_save_language),
                                        Toast.LENGTH_SHORT).show();

                        }
                    })
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
            if (getContext() != null)
                android.widget.Toast.makeText(getContext(),
                        getString(R.string.error_load_languages),
                        Toast.LENGTH_SHORT).show();
        }
    }


}
