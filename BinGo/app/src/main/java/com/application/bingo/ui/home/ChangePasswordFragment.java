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

import com.application.bingo.R;
import com.application.bingo.repository.UserRepository;
import com.application.bingo.ui.viewmodel.ChangePasswordViewModel;
import com.application.bingo.ui.viewmodel.ViewModelFactory;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

        // -------------------------------
        // Recupero email utente loggato da Firebase
        // -------------------------------
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null || currentUser.getEmail() == null) {
            Toast.makeText(requireContext(), getString(R.string.user_not_logged_in), Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).popBackStack();
            return;
        }
        userEmail = currentUser.getEmail();
        Log.d(TAG, "Email utente: " + userEmail);

        // -------------------------------
        // Verifica connessione internet
        // -------------------------------
        UserRepository repo = new UserRepository(requireContext());
        if (!repo.isConnectedToInternet()) {
            Toast.makeText(requireContext(), getString(R.string.must_be_online), Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).popBackStack();
            return;
        }

        // -------------------------------
        // Creazione ViewModel
        // -------------------------------
        ViewModelFactory factory = new ViewModelFactory(requireActivity().getApplication());
        changePasswordViewModel = new ViewModelProvider(this, factory).get(ChangePasswordViewModel.class);

        // -------------------------------
        // Osserva LiveData messaggi
        // -------------------------------
        changePasswordViewModel.getMessageLiveData().observe(getViewLifecycleOwner(), message -> {
            Log.d(TAG, "Messaggio LiveData: " + message);
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();

            if (getString(R.string.password_updated_success).equals(message)) {
                if (vecchiaPasswordEditText != null) vecchiaPasswordEditText.setText("");
                if (nuovaPasswordEditText != null) nuovaPasswordEditText.setText("");
                if (confermaPasswordEditText != null) confermaPasswordEditText.setText("");
                NavHostFragment.findNavController(ChangePasswordFragment.this).popBackStack();
            }
        });

        // -------------------------------
        // Pulsante cambia password
        // -------------------------------
        btnChangePassword.setOnClickListener(v -> {
            String oldPass = vecchiaPasswordEditText != null ? vecchiaPasswordEditText.getText().toString().trim() : "";
            String newPass = nuovaPasswordEditText != null ? nuovaPasswordEditText.getText().toString().trim() : "";
            String confirmPass = confermaPasswordEditText != null ? confermaPasswordEditText.getText().toString().trim() : "";

            // Validazioni minime
            if (TextUtils.isEmpty(oldPass) || TextUtils.isEmpty(newPass) || TextUtils.isEmpty(confirmPass)) {
                Toast.makeText(requireContext(), getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirmPass)) {
                Toast.makeText(requireContext(), getString(R.string.passwords_do_not_match), Toast.LENGTH_SHORT).show();
                return;
            }

            // Invia richiesta al ViewModel
            changePasswordViewModel.changePassword(userEmail, oldPass, newPass, confirmPass);
        });
    }
}
