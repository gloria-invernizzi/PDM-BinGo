package com.application.bingo.ui.home.profile;

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

import com.application.bingo.R;
import com.application.bingo.ui.viewmodel.ChangePasswordViewModel;
import com.application.bingo.ui.viewmodel.ViewModelFactory;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Fragment per cambiare la password utente.
 * - Online-first
 * - Blocca utenti Google
 * - Usa MVVM con LiveData per notifiche
 */
public class ChangePasswordFragment extends Fragment {

    private TextInputEditText vecchiaPasswordEditText;
    private TextInputEditText nuovaPasswordEditText;
    private TextInputEditText confermaPasswordEditText;
    private MaterialButton btnChangePassword;

    private ChangePasswordViewModel changePasswordViewModel;
    private String userEmail;

    private static final String TAG = "ChangePasswordFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated chiamato");

        // -------------------------------
        // Recupero campi dal layout
        // -------------------------------
        vecchiaPasswordEditText = (TextInputEditText) ((TextInputLayout)view.findViewById(R.id.vecchia_password)).getEditText();
        nuovaPasswordEditText = (TextInputEditText) ((TextInputLayout)view.findViewById(R.id.nuova_password)).getEditText();
        confermaPasswordEditText = (TextInputEditText) ((TextInputLayout)view.findViewById(R.id.conferma_password)).getEditText();
        btnChangePassword = view.findViewById(R.id.btn_change_password);

        // Creazione ViewModel tramite Factory
        ViewModelFactory factory = new ViewModelFactory(requireActivity().getApplication());
        changePasswordViewModel = new ViewModelProvider(this, factory).get(ChangePasswordViewModel.class);

        //  Recupero Email tramite ViewModel
        userEmail = changePasswordViewModel.getCurrentUserEmail();
        if (userEmail == null) {
            Toast.makeText(requireContext(), getString(R.string.user_not_logged_in), Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).popBackStack();
            return;
        }

        //  Osserva LiveData messaggi
        setupObservers();

        //  Pulsante cambia password
        btnChangePassword.setOnClickListener(v -> attemptChangePassword());
    }

    private void setupObservers() {
        changePasswordViewModel.getMessageLiveData().observe(getViewLifecycleOwner(), messageKey -> {
            Log.d(TAG, "Messaggio Ricevuto: " + messageKey);
            
            // Traduzione della chiave messaggio in stringa utente (gestione centralizzata errori)
            String displayMessage;
            switch (messageKey) {
                case "password_updated_success":
                    displayMessage = getString(R.string.password_updated_success);
                    clearFields();
                    NavHostFragment.findNavController(this).popBackStack();
                    break;
                case "cannot_change_offline":
                    displayMessage = getString(R.string.must_be_online);
                    break;
                case "google_users_cannot_change_password":
                    displayMessage = getString(R.string.google_users_cannot_change_password);
                    break;
                case "passwords_do_not_match":
                    displayMessage = getString(R.string.passwords_do_not_match);
                    break;
                case "fill_all_fields":
                    displayMessage = getString(R.string.error_missing_fields);
                    break;
                default:
                    displayMessage = messageKey; // Messaggio d'errore diretto dal repository
                    break;
            }
            Toast.makeText(requireContext(), displayMessage, Toast.LENGTH_SHORT).show();
        });
    }

    private void attemptChangePassword() {
        String oldPass = vecchiaPasswordEditText != null ? vecchiaPasswordEditText.getText().toString().trim() : "";
        String newPass = nuovaPasswordEditText != null ? nuovaPasswordEditText.getText().toString().trim() : "";
        String confirmPass = confermaPasswordEditText != null ? confermaPasswordEditText.getText().toString().trim() : "";

        // Il ViewModel gestirà le validazioni e il controllo connessione
        changePasswordViewModel.changePassword(userEmail, oldPass, newPass, confirmPass);
    }

    private void clearFields() {
        if (vecchiaPasswordEditText != null) vecchiaPasswordEditText.setText("");
        if (nuovaPasswordEditText != null) nuovaPasswordEditText.setText("");
        if (confermaPasswordEditText != null) confermaPasswordEditText.setText("");
    }
}
