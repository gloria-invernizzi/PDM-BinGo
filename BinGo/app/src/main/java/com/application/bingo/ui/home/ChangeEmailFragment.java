package com.application.bingo.ui.home;

import android.os.Bundle;
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
import com.application.bingo.ui.ConfirmPasswordDialogFragment;
import com.application.bingo.ui.viewmodel.ChangeEmailViewModel;
import com.application.bingo.ui.viewmodel.ViewModelFactory;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ChangeEmailFragment extends Fragment {

    private TextInputEditText nuovaEmailEditText;
    private TextInputEditText confermaEmailEditText;
    private MaterialButton btnChangeEmail;

    private ChangeEmailViewModel changeEmailViewModel;
    private String currentEmail;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_email, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Recupero email corrente da PrefsManager ---
        PrefsManager prefsManager = new PrefsManager(requireContext());
        currentEmail = prefsManager.getSavedEmail();

        btnChangeEmail = view.findViewById(R.id.btn_change_email);

        TextInputLayout nuovaEmailLayout = view.findViewById(R.id.nuova_email);
        TextInputLayout confermaEmailLayout = view.findViewById(R.id.conferma_email);

        nuovaEmailEditText = (TextInputEditText) nuovaEmailLayout.getEditText();
        confermaEmailEditText = (TextInputEditText) confermaEmailLayout.getEditText();

        // --- ViewModel setup ---
        changeEmailViewModel = new ViewModelProvider(
                this,
                new ViewModelFactory(requireActivity().getApplication())
        ).get(ChangeEmailViewModel.class);

        // --- Observer messaggi ---
        changeEmailViewModel.getMessageLiveData()
                .observe(getViewLifecycleOwner(), message -> {
                    btnChangeEmail.setEnabled(true);
                    if (message != null) {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                        // Se il messaggio indica successo, puoi opzionalmente tornare indietro
                        if (message.toLowerCase().contains("successo")) {
                            // Pop back stack o navigazione
                            // NavHostFragment.findNavController(this).popBackStack();
                        }
                    }
                });

        // --- Observer logout dopo cambio email ---
        changeEmailViewModel.getLogoutLiveData()
                .observe(getViewLifecycleOwner(), logout -> {
                    if (logout != null && logout) {
                        // --- Logout forzato ---
                        FirebaseAuth.getInstance().signOut();

                        // Torna al welcomeFragment del login graph
                        NavHostFragment.findNavController(this)
                                .popBackStack(R.id.welcomeFragment, false);
                    }
                });

        // --- Bottone cambio email ---
        btnChangeEmail.setOnClickListener(v -> {

            String newEmail = nuovaEmailEditText.getText().toString().trim();
            String confirmEmail = confermaEmailEditText.getText().toString().trim();

            if (newEmail.isEmpty() || confirmEmail.isEmpty()) {
                Toast.makeText(requireContext(), "Compila tutti i campi", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newEmail.equals(confirmEmail)) {
                Toast.makeText(requireContext(), "Le email non corrispondono", Toast.LENGTH_SHORT).show();
                return;
            }

            // --- Dialog password obbligatoria ---
            ConfirmPasswordDialogFragment dialog =
                    new ConfirmPasswordDialogFragment(password -> {
                        if (password == null || password.isEmpty()) {
                            Toast.makeText(requireContext(),
                                    "Inserisci la password", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        btnChangeEmail.setEnabled(false);

                        // --- Chiamata al ViewModel ---
                        changeEmailViewModel.changeEmail(
                                currentEmail,
                                password,
                                newEmail
                        );
                    });

            dialog.show(getParentFragmentManager(), "ConfirmPasswordDialog");
        });
    }
}
