// File: `BinGo/app/src/main/java/com/application/bingo/ui/RegisterFragment.java`
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RegisterFragment extends Fragment {
    private TextInputEditText etName, etAddress, etEmail, etPassword, etConfirm;
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
        MaterialButton btnRegister = view.findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener((View v) -> attemptRegister());
    }

    private void attemptRegister() {
        final String name = getText(etName);
        final String address = getText(etAddress);
        final String email = getText(etEmail);
        final String pass = getText(etPassword);
        final String confirm = getText(etConfirm);
        final boolean remember = cbRemember != null && cbRemember.isChecked();

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

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(requireActivity(), (Task<AuthResult> task) -> {
                    if (task.isSuccessful()) {
                        bg.execute(() -> {
                            User existing = AppDatabase.getInstance(requireContext()).userDao().findByEmail(email);
                            if (existing == null) {
                                User user = new User(name, address, email, pass);
                                long id = AppDatabase.getInstance(requireContext()).userDao().insert(user);

                                if (id > 0) {
                                    if (remember) {
                                        // salva nome, address, email, password e flag remember
                                        prefs.saveUser(name, address, email, pass);
                                        prefs.setRemember(true);
                                    } else {
                                        prefs.setRemember(false);
                                    }
                                    requireActivity().runOnUiThread(() -> {
                                        Toast.makeText(requireContext(), "Registrazione completata", Toast.LENGTH_SHORT).show();
                                        requireActivity().getOnBackPressedDispatcher().onBackPressed();
                                    });
                                }
                            } else {
                                // già presente in Room (caso raro)
                                requireActivity().runOnUiThread(() -> {
                                    Toast.makeText(requireContext(), "Utente già presente", Toast.LENGTH_SHORT).show();
                                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                                });
                            }
                        });
                    } else {
                        Exception exception = task.getException();
                        if (exception instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(requireContext(), "Email già registrata! Effettua il login.", Toast.LENGTH_LONG).show();
                            requireActivity().getOnBackPressedDispatcher().onBackPressed();
                        } else {
                            String msg = exception != null ? exception.getMessage() : "Errore Firebase";
                            Toast.makeText(requireContext(), "Registrazione fallita: " + msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private String getText(TextInputEditText et) {
        return et == null || et.getText() == null ? "" : et.getText().toString().trim();
    }
}
