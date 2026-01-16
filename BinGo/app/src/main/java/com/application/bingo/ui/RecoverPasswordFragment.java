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

public class RecoverPasswordFragment extends Fragment {

    private TextInputEditText etEmail;
    private Button btnSendRecovery;
    private TextView tvBackToLogin;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inizializza Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Collega il layout al fragment
        return inflater.inflate(R.layout.fragment_recover_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Associa le viste dal layout
        etEmail = view.findViewById(R.id.textInputEmail_recover);
        btnSendRecovery = view.findViewById(R.id.send_recovery_button);
        tvBackToLogin = view.findViewById(R.id.back_to_login_button);

        // Imposta i listener per i click
        btnSendRecovery.setOnClickListener(v -> sendPasswordResetEmail());

        tvBackToLogin.setOnClickListener(v -> {
            // Torna al fragment di login
            Navigation.findNavController(v).popBackStack();
        });
    }

    private void sendPasswordResetEmail() {
        String email = etEmail.getText().toString().trim();

        // Validazione dell'email in input
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

        // Mostrare un feedback all'utente (es. ProgressBar)??

        // Chiama il metodo di Firebase per inviare l'email
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    // Nascondi il feedback nel caso ci fosse (DA RIVEDERE)
                    if (task.isSuccessful()) {
                        // Email inviata con successo
                        Toast.makeText(getContext(), getString(R.string.recovery_email_sent), Toast.LENGTH_LONG).show();
                        // Torna automaticamente alla schermata di login:
                        if(getView() != null) {
                            Navigation.findNavController(getView()).popBackStack();
                        }
                    } else {
                        // Errore durante l'invio
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : getString(R.string.error_unknown);
                        Toast.makeText(getContext(), getString(R.string.error_generic) + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
