// file: app/src/main/java/com/application/bingo/ui/RegisterFragment.java
package com.application.bingo.ui;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.application.bingo.AppDatabase;
import com.application.bingo.PrefsManager;
import com.application.bingo.R;
import com.application.bingo.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
//per gestire task asincroni??
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RegisterFragment extends Fragment {
    private TextInputEditText etName, etAddress, etEmail, etPassword, etConfirm;
    private MaterialButton btnRegister;
    private CheckBox cbRemember;
    private PrefsManager prefs;
    private final Executor bg = Executors.newSingleThreadExecutor();
    private FirebaseAuth mAuth;

    public RegisterFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        prefs = new PrefsManager(requireContext());
        etName = view.findViewById(R.id.etName);
        etAddress = view.findViewById(R.id.etAddress);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        etConfirm = view.findViewById(R.id.etConfirm);
        cbRemember = view.findViewById(R.id.cbRemember);
        btnRegister = view.findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener((View v) -> attemptRegister());
    }

    private void attemptRegister() {
        final String name = getText(etName);
        final String email = getText(etEmail);
        final String pass = getText(etPassword);
        final String confirm = getText(etConfirm);
        final boolean remember = cbRemember!=null && cbRemember.isChecked();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(requireContext(), "Campi mancanti", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireContext(), "Email non valida", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass.length() < 6) {
            Toast.makeText(requireContext(), "La password deve contenere almeno 6 caratteri", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pass.equals(confirm)){
            Toast.makeText(requireContext(), "Le password non corrispondono", Toast.LENGTH_SHORT).show();
            return;
        }

        // Registrazione su Firebase e su Room per garantire persistenza
        mAuth.createUserWithEmailAndPassword(email, pass)
                /* Gestione esito task registrazione Firebase in modo asincrono con listener k per
                * evitare blocchi dell'interfaccia utente
                */
                .addOnCompleteListener(requireActivity(), (Task<AuthResult> task) -> {
                    if (task.isSuccessful()) {
                        // Firebase user created -> salva anche in Room
                        bg.execute(() -> {
                            User user = new User(name, "", email, pass);
                            long id = AppDatabase.getInstance(requireContext()).userDao().insert(user);
                            if (id > 0) {
                                if (remember) {
                                    prefs.saveUser(name, "", email, pass);
                                }
                                requireActivity().runOnUiThread(() -> {
                                    Toast.makeText(requireContext(), "Registrazione completata", Toast.LENGTH_SHORT).show();
                                    requireActivity().onBackPressed();
                                });
                            } else {
                                requireActivity().runOnUiThread(() ->
                                        Toast.makeText(requireContext(), "Errore durante la registrazione locale", Toast.LENGTH_SHORT).show());
                            }
                        });
                    } else {
                        // Firebase failed
                        String msg = task.getException() != null ? task.getException().getMessage() : "Errore Firebase";
                        Toast.makeText(requireContext(), "Registrazione fallita: " + msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getText(TextInputEditText et) {
        return et == null || et.getText() == null ? "" : et.getText().toString().trim();
    }
}