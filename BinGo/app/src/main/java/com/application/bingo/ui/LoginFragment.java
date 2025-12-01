// file: app/src/main/java/com/application/bingo/ui/LoginFragment.java
package com.application.bingo.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.application.bingo.AppDatabase;
import com.application.bingo.PrefsManager;
import com.application.bingo.R;
import com.application.bingo.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginFragment extends Fragment {

    private TextInputEditText etEmail, etPassword;
    private Button btnlogin;
    private Button btnRegister;
    private CheckBox cbRemember;
    private PrefsManager prefs;
    private final ExecutorService bg = Executors.newSingleThreadExecutor();
    private FirebaseAuth mAuth;

    private static final String TAG = LoginFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        prefs = new PrefsManager(requireContext());
        etEmail = view.findViewById(R.id.textInputEmail);
        etPassword = view.findViewById(R.id.textInputPassword);
        btnlogin = view.findViewById(R.id.login_button);
        btnRegister = view.findViewById(R.id.register_button);
        cbRemember = view.findViewById(R.id.cbRemember);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Log.i(TAG, "Firebase user: " + user.getEmail());
            // Naviga direttamente se vuoi
        }

        final String savedEmail = prefs.getSavedEmail();
        final String savedPass = prefs.getSavedPassword();
        if (!savedEmail.isEmpty() && !savedPass.isEmpty()) {
            bg.execute(() -> {
                User u = AppDatabase.getInstance(requireContext())
                        .userDao()
                        .findByEmailAndPassword(savedEmail, savedPass);
                if (u != null) {
                    requireActivity().runOnUiThread(() -> {
                        etEmail.setText(savedEmail);
                        etPassword.setText(savedPass);
                        cbRemember.setChecked(true);
                    });
                } else {
                    prefs.clearSavedUser();
                }
            });
        }

        btnlogin.setOnClickListener(v -> attemptLogin());
        btnRegister.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_registerFragment));
    }

    private void attemptLogin() {
        final String email = getText(etEmail);
        final String pass = getText(etPassword);

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(requireContext(), "Campi mancanti", Toast.LENGTH_SHORT).show();
            return;
        }

        //CONTROLLARE PRIMA SE ESISTE IN ROOM (CACHE LOCALE) PER EVITARE CHIAMATE FIREBASE INUTILI??
        // Effettua Firebase sign in
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(requireActivity(), task -> {
            if (task.isSuccessful()) {
                // Signed in
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                String name = firebaseUser != null && firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "";
                bg.execute(() -> {
                    User local = AppDatabase.getInstance(requireContext()).userDao().findByEmail(email);
                    if (local == null) {
                        // Controlla se l'utente esiste in Room, altrimenti crealo (sincronizzazione dei due database per garantire persistenza offline)
                        // crea record locale con campi minimi
                        User newUser = new User(firebaseUser != null ? firebaseUser.getDisplayName() : "", "", email, pass);
                        AppDatabase.getInstance(requireContext()).userDao().insert(newUser);
                    }
                    requireActivity().runOnUiThread(() -> {
                        // salva preferenze di sessione
                        prefs.saveUser(name,email); // adatta a tua implementazione PrefsManager
                        if (cbRemember != null && cbRemember.isChecked()) {
                            prefs.saveUser(email, pass); // o metodo corretto per salvare la password
                        }
                        Toast.makeText(requireContext(), "Login effettuato", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_mainActivity2);
                    });
                });
            } else {
                String msg = task.getException() != null ? task.getException().getMessage() : "Credenziali non valide";
                Toast.makeText(requireContext(), "Login fallito: " + msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getText(TextInputEditText et){
        return et == null || et.getText() == null ? "" : et.getText().toString().trim();
    }
}