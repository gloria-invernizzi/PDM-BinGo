package com.application.bingo.ui.home;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.application.bingo.PrefsManager;
import com.application.bingo.R;
import com.application.bingo.repository.UserRepository;
import com.application.bingo.viewmodel.ChangePasswordViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Fragment per modificare la password utente.
 * Gestisce l'interazione UI:
 * - Recupero campi
 * - Validazione base
 * - Invio richiesta al ViewModel
 * - Visualizzazione notifiche
 */
public class ChangePasswordFragment extends Fragment {

    private TextInputEditText vecchiaPasswordEditText, nuovaPasswordEditText, confermaPasswordEditText;
    private MaterialButton btnChangePassword;
    private ChangePasswordViewModel changePasswordViewModel;

    private String userEmail;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Collega il layout al fragment
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // -------------------------------
        // Recupero dei campi dal layout
        // -------------------------------
        vecchiaPasswordEditText = (TextInputEditText) ((TextInputLayout) view.findViewById(R.id.vecchia_password)).getEditText();
        nuovaPasswordEditText = (TextInputEditText) ((TextInputLayout) view.findViewById(R.id.nuova_password)).getEditText();
        confermaPasswordEditText = (TextInputEditText) ((TextInputLayout) view.findViewById(R.id.conferma_password)).getEditText();

        btnChangePassword = view.findViewById(R.id.btn_change_password);

        // -------------------------------
        // Recupero email utente loggato
        // -------------------------------
        PrefsManager prefsManager = new PrefsManager(requireContext());
        userEmail = prefsManager.getSavedEmail();
        Log.d("ChangePasswordFragment", "Email letta : " + userEmail);

        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(requireContext(), "Utente non loggato", Toast.LENGTH_SHORT).show();
            return;
        }

        // -------------------------------
        // Creazione Repository + ViewModel
        // -------------------------------
        UserRepository repository = new UserRepository(requireContext());

        changePasswordViewModel = new ViewModelProvider(
                this,
                new ViewModelProvider.Factory() {
                    @NonNull
                    @Override
                    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                        if (modelClass.isAssignableFrom(ChangePasswordViewModel.class)) {
                            return (T) new ChangePasswordViewModel(repository);
                        }
                        throw new IllegalArgumentException("Unknown ViewModel class");
                    }
                }
        ).get(ChangePasswordViewModel.class);

        // -------------------------------
        // Osserva i messaggi dal ViewModel
        // -------------------------------
        changePasswordViewModel.getMessageLiveData().observe(getViewLifecycleOwner(), message -> {

            // Mostra toast con esito
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();

            // Se password cambiata correttamente → reset campi
            if (message.equals("Password aggiornata con successo")) {
                vecchiaPasswordEditText.setText("");
                nuovaPasswordEditText.setText("");
                confermaPasswordEditText.setText("");
            }
        });

        // -------------------------------
        // Pulsante "Cambia password"
        // -------------------------------
        btnChangePassword.setOnClickListener(v -> {
            Log.d("ChangePasswordFragment", "Pulsante cliccato!");

            String oldPass = vecchiaPasswordEditText.getText() != null ? vecchiaPasswordEditText.getText().toString().trim() : "";
            String newPass = nuovaPasswordEditText.getText() != null ? nuovaPasswordEditText.getText().toString().trim() : "";
            String confirmPass = confermaPasswordEditText.getText() != null ? confermaPasswordEditText.getText().toString().trim() : "";

            // -------------------------------
            // Verifica se l’utente ha effettivamente compilato vecchia password, nuova password e conferma password
            // -------------------------------
            if (TextUtils.isEmpty(oldPass) || TextUtils.isEmpty(newPass) || TextUtils.isEmpty(confirmPass)) {
                Toast.makeText(requireContext(), "Compila tutti i campi", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirmPass)) {
                Toast.makeText(requireContext(), "Le password non corrispondono", Toast.LENGTH_SHORT).show();
                return;
            }

            // -------------------------------
            // Richiesta di modifica al ViewModel
            // -------------------------------
            changePasswordViewModel.changePassword(userEmail, oldPass, newPass, confirmPass);
        });
    }
}

