package com.application.bingo;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RegisterFragment extends Fragment {
    private TextInputEditText etName, etAddress, etEmail, etPassword, etConfirm;
    private MaterialButton btnRegister;
    private PrefsManager prefs; // manage shared preferences
    private final Executor bg = Executors.newSingleThreadExecutor(); // manage background tasks

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment: create the view hierarchy from the XML layout
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = new PrefsManager(requireContext());
        etName = view.findViewById(R.id.etName);
        etAddress = view.findViewById(R.id.etAddress);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        etConfirm = view.findViewById(R.id.etConfirm);
        btnRegister = view.findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> attemptRegister());
    }

    private void attemptRegister() {
        final String name = getText(etName);
        final String email = getText(etEmail);
        final String pass = getText(etPassword);
        final String confirm = getText(etConfirm);

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

        registerWithRoom(name, email, pass);
    }

    private void registerWithRoom(final String name, final String email, final String pass) {
        bg.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());
            // check if the email is already registered
            User existing = db.userDao().findByEmail(email);
            if (existing != null) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Account già registrato con questa email", Toast.LENGTH_SHORT).show());
                return;
            }

            // address initially empty
            User user = new User(name, "", email, pass);
            long id = db.userDao().insert(user);

            if (id > 0) {
                // save user in shared preferences
                // Error? onBackPressed non funziona più dopo la registrazione??
                prefs.saveUser(name, "", email, pass);
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Registrazione completata", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                });
            } else {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Errore durante la registrazione", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private String getText(TextInputEditText et) {
        // retrieve and trim text from TextInputEditText
        return et == null || et.getText() == null ? "" : et.getText().toString().trim();
    }
}
