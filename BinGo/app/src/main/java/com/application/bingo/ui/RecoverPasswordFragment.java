package com.application.bingo.ui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.application.bingo.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

/**
 * RecoverPasswordFragment:
 * Handles the UI for requesting a password reset email via Firebase Authentication.
 */
public class RecoverPasswordFragment extends Fragment {

    private TextInputEditText etEmail;
    private Button btnSendRecovery;
    private TextView tvBackToLogin;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recover_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind views from layout
        etEmail = view.findViewById(R.id.textInputEmail_recover);
        btnSendRecovery = view.findViewById(R.id.send_recovery_button);
        tvBackToLogin = view.findViewById(R.id.back_to_login_button);

        // Set click listeners
        btnSendRecovery.setOnClickListener(v -> sendPasswordResetEmail());

        tvBackToLogin.setOnClickListener(v -> {
            // Return to the login fragment
            Navigation.findNavController(v).popBackStack();
        });
    }

    /**
     * Validates input and triggers Firebase to send a password reset email.
     */
    private void sendPasswordResetEmail() {
        String email = etEmail.getText().toString().trim();

        // Validate email input
        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.error_email_required));
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError(getString(R.string.error_email_invalid));
            etEmail.requestFocus();
            return;
        }

        // Call Firebase method to send reset email
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Email sent successfully
                        Toast.makeText(getContext(), getString(R.string.recovery_email_sent), Toast.LENGTH_LONG).show();
                        // Automatically return to the login screen
                        if(getView() != null) {
                            Navigation.findNavController(getView()).popBackStack();
                        }
                    } else {
                        // Handle failure
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : getString(R.string.error_unknown);
                        Toast.makeText(getContext(), getString(R.string.error_generic) + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
