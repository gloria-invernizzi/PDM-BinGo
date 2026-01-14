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

import com.application.bingo.R;
import com.application.bingo.ui.ConfirmPasswordDialogFragment;
import com.application.bingo.ui.viewmodel.ChangeEmailViewModel;
import com.application.bingo.ui.viewmodel.ViewModelFactory;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangeEmailFragment extends Fragment {

    private TextInputEditText nuovaEmailEditText;
    private TextInputEditText confermaEmailEditText;
    private MaterialButton btnChangeEmail;

    private ChangeEmailViewModel viewModel;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_change_email, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        btnChangeEmail = view.findViewById(R.id.btn_change_email);

        TextInputLayout nuovaEmailLayout = view.findViewById(R.id.nuova_email);
        TextInputLayout confermaEmailLayout = view.findViewById(R.id.conferma_email);

        nuovaEmailEditText = (TextInputEditText) nuovaEmailLayout.getEditText();
        confermaEmailEditText = (TextInputEditText) confermaEmailLayout.getEditText();

        // ViewModel
        viewModel = new ViewModelProvider(
                this,
                new ViewModelFactory(requireActivity().getApplication())
        ).get(ChangeEmailViewModel.class);

        // Messaggi
        viewModel.getMessageLiveData().observe(getViewLifecycleOwner(), msg -> {
            btnChangeEmail.setEnabled(true);
            if (msg != null) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
            }
        });

        // Logout forzato
        viewModel.getLogoutLiveData().observe(getViewLifecycleOwner(), logout -> {
            if (Boolean.TRUE.equals(logout)) {
                FirebaseAuth.getInstance().signOut();
                NavHostFragment.findNavController(this).navigate(R.id.welcomeFragment);
            }
        });

        // Bottone
        btnChangeEmail.setOnClickListener(v -> {

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            if (firebaseUser == null) {
                Toast.makeText(
                        requireContext(),
                        "Sessione scaduta. Effettua di nuovo il login.",
                        Toast.LENGTH_LONG
                ).show();

            }

            String oldEmail = firebaseUser.getEmail();
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

            ConfirmPasswordDialogFragment dialog =
                    new ConfirmPasswordDialogFragment(password -> {

                        if (password == null || password.isEmpty()) {
                            Toast.makeText(
                                    requireContext(),
                                    "Inserisci la password",
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }

                        btnChangeEmail.setEnabled(false);

                        viewModel.changeEmail(
                                oldEmail,
                                password,
                                newEmail
                        );
                    });

            dialog.show(getParentFragmentManager(), "ConfirmPasswordDialog");
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.refreshFirebaseUser();
    }
}
