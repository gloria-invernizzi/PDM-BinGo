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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.application.bingo.PrefsManager;
import com.application.bingo.R;
import com.application.bingo.ui.viewmodel.ChangePasswordViewModel;
import com.application.bingo.ui.viewmodel.ViewModelFactory;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Fragment per modificare la password utente.
 * Gestisce:
 * - Recupero campi dalla UI
 * - Validazione base (non vuoti, conferma password)
 * - Invio richiesta al ViewModel
 * - Visualizzazione notifiche
 * - Reset campi e navigazione se successo
 */
public class ChangePasswordFragment extends Fragment {

    private TextInputEditText vecchiaPasswordEditText;
    private TextInputEditText nuovaPasswordEditText;
    private TextInputEditText confermaPasswordEditText;
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
        Log.d("ChangePasswordFragment", "onViewCreated chiamato");

        // -------------------------------
        // Recupero dei campi dal layout
        // -------------------------------
        vecchiaPasswordEditText = null;
        nuovaPasswordEditText = null;
        confermaPasswordEditText = null;

        TextInputLayout vecchiaLayout = view.findViewById(R.id.vecchia_password);
        if (vecchiaLayout != null) {
            vecchiaPasswordEditText = (TextInputEditText) vecchiaLayout.getEditText();
        }

        TextInputLayout nuovaLayout = view.findViewById(R.id.nuova_password);
        if (nuovaLayout != null) {
            nuovaPasswordEditText = (TextInputEditText) nuovaLayout.getEditText();
        }

        TextInputLayout confermaLayout = view.findViewById(R.id.conferma_password);
        if (confermaLayout != null) {
            confermaPasswordEditText = (TextInputEditText) confermaLayout.getEditText();
        }

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
        // Creazione ViewModel con Factory
        // -------------------------------
        ViewModelFactory factory = new ViewModelFactory(requireActivity().getApplication());
        changePasswordViewModel = new ViewModelProvider(this, factory).get(ChangePasswordViewModel.class);

        // -------------------------------
        // Osserva i messaggi dal ViewModel
        // -------------------------------
        changePasswordViewModel.getMessageLiveData().observe(getViewLifecycleOwner(), message -> {
            Log.d("ChangePasswordFragment", "Message LiveData: " + message);
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();

            // Se password cambiata correttamente → reset campi e torna indietro
            if ("Password aggiornata con successo".equals(message)) {
                if (vecchiaPasswordEditText != null) vecchiaPasswordEditText.setText("");
                if (nuovaPasswordEditText != null) nuovaPasswordEditText.setText("");
                if (confermaPasswordEditText != null) confermaPasswordEditText.setText("");
                NavHostFragment.findNavController(ChangePasswordFragment.this).popBackStack();
            }
        });

        // -------------------------------
        // Pulsante "Cambia password"
        // -------------------------------
        btnChangePassword.setOnClickListener(v -> {
            Log.d("ChangePasswordFragment", "Pulsante cliccato!");

            // Recupero testo dai campi con if/else invece di ?:
            String oldPass;
            if (vecchiaPasswordEditText != null && vecchiaPasswordEditText.getText() != null) {
                oldPass = vecchiaPasswordEditText.getText().toString().trim();
            } else {
                oldPass = "";
            }

            String newPass;
            if (nuovaPasswordEditText != null && nuovaPasswordEditText.getText() != null) {
                newPass = nuovaPasswordEditText.getText().toString().trim();
            } else {
                newPass = "";
            }

            String confirmPass;
            if (confermaPasswordEditText != null && confermaPasswordEditText.getText() != null) {
                confirmPass = confermaPasswordEditText.getText().toString().trim();
            } else {
                confirmPass = "";
            }

            // -------------------------------
            // Verifica campi compilati
            // -------------------------------
            if (TextUtils.isEmpty(oldPass) || TextUtils.isEmpty(newPass) || TextUtils.isEmpty(confirmPass)) {
                Toast.makeText(requireContext(), "Compila tutti i campi", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verifica corrispondenza nuova password e conferma
            if (!newPass.equals(confirmPass)) {
                Toast.makeText(requireContext(), "Le password non corrispondono", Toast.LENGTH_SHORT).show();
                return;
            }

            // -------------------------------
            // Invio richiesta cambio password al ViewModel
            // -------------------------------
            changePasswordViewModel.changePassword(userEmail, oldPass, newPass, confirmPass);
        });
    }
}

