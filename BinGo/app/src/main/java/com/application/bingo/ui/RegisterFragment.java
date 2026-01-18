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

import com.application.bingo.PrefsManager;
import com.application.bingo.R;
import com.application.bingo.model.User;
import com.application.bingo.repository.UserRepository;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class RegisterFragment extends Fragment {
    private TextInputEditText etName, etSurname, etAddress, etEmail, etPassword, etConfirm;
    private CheckBox cbRemember;
    private PrefsManager prefs;
    private FirebaseAuth mAuth;
    private UserRepository userRepository;

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
        userRepository = new UserRepository(requireContext());
        prefs = new PrefsManager(requireContext());
        etName = view.findViewById(R.id.etName);
        etSurname = view.findViewById(R.id.etSurname);
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
        final String surname = getText(etSurname);
        final String address = getText(etAddress);
        final String email = getText(etEmail);
        final String pass = getText(etPassword);
        final String confirm = getText(etConfirm);
        final boolean remember = cbRemember != null && cbRemember.isChecked();

        if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
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
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        User user = new User(name, surname, address, email, pass);
                        user.setPhotoUri(""); // foto vuota iniziale
                        userRepository.saveLocalUser(user, () -> requireActivity().runOnUiThread(() -> {
                            if (remember) {
                                prefs.saveUser(name, surname, address, email, pass);
                                prefs.setRemember(true);
                            } else {
                                prefs.setRemember(false);
                                prefs.clearSavedUser();
                            }
                            Toast.makeText(requireContext(), "Registrazione completata!", Toast.LENGTH_SHORT).show();
                            requireActivity().getOnBackPressedDispatcher().onBackPressed();
                        }));
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
