package com.application.bingo.ui.home.profile;

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
    private ChangeEmailViewModel changeEmailVM;

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
        changeEmailVM = new ViewModelProvider(
                this,
                new ViewModelFactory(requireActivity().getApplication())
        ).get(ChangeEmailViewModel.class);

        // Messaggi
        changeEmailVM.getMessageLiveData().observe(getViewLifecycleOwner(), msg -> {
            btnChangeEmail.setEnabled(true);
            if (msg != null) {
                String message;
                switch (msg) {
                    case "fill_all_fields":
                        message = getString(R.string.fill_all_fields);
                        break;
                    case "emails_do_not_match":
                        message = getString(R.string.emails_do_not_match);
                        break;
                    case "enter_password":
                        message = getString(R.string.enter_password);
                        break;
                    default:
                        message = msg; // fallback
                }
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
            }
        });

        // Logout forzato
        changeEmailVM.getLogoutLiveData().observe(getViewLifecycleOwner(), logout -> {
            if (Boolean.TRUE.equals(logout)) {
                FirebaseAuth.getInstance().signOut();
                NavHostFragment.findNavController(this).navigate(R.id.welcomeFragment);
            }
        });

        // Bottone
        btnChangeEmail.setOnClickListener(v -> {
            String newEmail = nuovaEmailEditText.getText().toString().trim();
            String confirmEmail = confermaEmailEditText.getText().toString().trim();

            if (newEmail.isEmpty() || confirmEmail.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newEmail.equals(confirmEmail)) {
                Toast.makeText(requireContext(), getString(R.string.emails_do_not_match), Toast.LENGTH_SHORT).show();
                return;
            }

            ConfirmPasswordDialogFragment dialog =
                    new ConfirmPasswordDialogFragment(password -> {

                        if (password == null || password.isEmpty()) {
                            Toast.makeText(requireContext(), getString(R.string.enter_password), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        btnChangeEmail.setEnabled(false);

                        changeEmailVM.changeEmail(password, newEmail);
                    });

            dialog.show(getParentFragmentManager(), "ConfirmPasswordDialog");
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        changeEmailVM.refreshFirebaseUser();
    }
}
