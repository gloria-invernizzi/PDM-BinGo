package com.application.bingo;

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
import com.google.android.material.textfield.TextInputEditText;

// This import statement is for managing background tasks using Executor framework
// Is allowed to run tasks asynchronously without blocking the main UI thread???
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    private TextInputEditText etEmail, etPassword;
    private Button btnlogin;
    private CheckBox cbRemember;
    private PrefsManager prefs; // manage shared preferences
    private final ExecutorService bg = Executors.newSingleThreadExecutor();

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = new PrefsManager(requireContext());
        // Lo inserisco qui perchè al di fuori della create view il view non esiste--> darebbe errore!
        etEmail = view.findViewById(R.id.textInputEmail);
        etPassword = view.findViewById(R.id.textInputPassword);
        btnlogin = view.findViewById(R.id.login_button);
        cbRemember = view.findViewById(R.id.cbRemember);

        // Se ci sono credenziali salvate, verifica in DB e autocompila
        final String savedEmail = prefs.getSavedEmail();
        final String savedPass = prefs.getSavedPassword();
        if (!savedEmail.isEmpty() && !savedPass.isEmpty()) {
            bg.execute(() -> {
                User u = AppDatabase.getInstance(requireContext())
                        .userDao()
                        .findByEmailAndPassword(savedEmail, savedPass);
                if (u != null) {
                    requireActivity().runOnUiThread(() -> {
                        // credenziali salvate valide: autocompilazione campi
                        etEmail.setText(savedEmail);
                        etPassword.setText(savedPass);
                        cbRemember.setChecked(true);
                    });
                } else {
                    // credenziali salvate non valide: pulizia --> opzionale!
                    prefs.clearSavedUser();
                    // Corretto?
                }
            });
        }

        // credeziali salvate non valide o assenti: normale procedura di login
        btnlogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        final String email = getText(etEmail);
        final String pass = getText(etPassword);

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(requireContext(), "Campi mancanti", Toast.LENGTH_SHORT).show();
            return;
        }

        bg.execute(() -> {
            User user = AppDatabase.getInstance(requireContext()).userDao().findByEmailAndPassword(email, pass);
            requireActivity().runOnUiThread(() -> {
                if (user == null) {
                    Toast.makeText(requireContext(), "Credenziali non valide", Toast.LENGTH_SHORT).show();
                } else {
                    // salva nome/email per sessione (sovrascrive la password?)
                    prefs.saveUser(user.getName(), user.getEmail());
                    if (cbRemember != null && cbRemember.isChecked()) {
                        prefs.saveUser(user.getName(), user.getEmail(), user.getAddress(), user.getPassword());
                    } else {
                        prefs.clearSavedUser();
                    }

                    Toast.makeText(requireContext(), "Login effettuato con successo", Toast.LENGTH_SHORT).show();

                    //Navigation Controller per spostarsi al Fragment Home
                    Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_homeFragment);
                }
            });
        });
    }

    private String getText(TextInputEditText et){
        return et == null || et.getText() == null ? "" : et.getText().toString().trim();
    }
}