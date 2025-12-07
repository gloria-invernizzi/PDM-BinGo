package com.application.bingo.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.application.bingo.R;
import com.application.bingo.repository.UserRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ChangePasswordFragment extends Fragment {

    private TextInputEditText vecchiaPasswordEditText, nuovaPasswordEditText, confermaPasswordEditText;
    private MaterialButton btnChangePassword;
    private ChangePasswordViewModel changePasswordViewModel;

    private String userEmail;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Trova i TextInputEditText all'interno dei TextInputLayout
        vecchiaPasswordEditText = (TextInputEditText) ((TextInputLayout) view.findViewById(R.id.vecchia_password)).getEditText();
        nuovaPasswordEditText = (TextInputEditText) ((TextInputLayout) view.findViewById(R.id.nuova_password)).getEditText();
        confermaPasswordEditText = (TextInputEditText) ((TextInputLayout) view.findViewById(R.id.conferma_password)).getEditText();

        btnChangePassword = view.findViewById(R.id.btn_change_password);
        Log.d("ChangePasswordFragment", "btnChangePassword: " + btnChangePassword);

        // Recupera l'email dell'utente loggato da SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        userEmail = prefs.getString("user_email", null);

        Log.d("ChangePasswordFragment", "Email utente: " + userEmail);

        if (userEmail == null) {
            Toast.makeText(requireContext(), "Utente non loggato", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crea repository e ViewModel
        UserRepository repository = new UserRepository(requireContext());
        changePasswordViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                if (modelClass.isAssignableFrom(ChangePasswordViewModel.class)) {
                    return (T) new ChangePasswordViewModel(repository);
                }
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        }).get(ChangePasswordViewModel.class);

        // Osserva i messaggi dal ViewModel
        changePasswordViewModel.getMessageLiveData().observe(getViewLifecycleOwner(), message -> {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();

            // Se cambio avvenuto con successo, resetta i campi
            if ("Password aggiornata con successo".equals(message)) {
                if (vecchiaPasswordEditText != null) vecchiaPasswordEditText.setText("");
                if (nuovaPasswordEditText != null) nuovaPasswordEditText.setText("");
                if (confermaPasswordEditText != null) confermaPasswordEditText.setText("");
            }
        });

        // Click listener del pulsante
        btnChangePassword.setOnClickListener(v -> {
            Log.d("ChangePasswordFragment", "Pulsante cliccato!");
            String vecchiaPassword = vecchiaPasswordEditText != null ? vecchiaPasswordEditText.getText().toString().trim() : "";
            String nuovaPassword = nuovaPasswordEditText != null ? nuovaPasswordEditText.getText().toString().trim() : "";
            String confermaPassword = confermaPasswordEditText != null ? confermaPasswordEditText.getText().toString().trim() : "";

            if (TextUtils.isEmpty(vecchiaPassword) || TextUtils.isEmpty(nuovaPassword) || TextUtils.isEmpty(confermaPassword)) {
                Log.d("ChangePasswordFragment", "Compila tutti i campi");
                Toast.makeText(requireContext(), "Compila tutti i campi", Toast.LENGTH_SHORT).show();
                return;
            }

            // Chiamata al ViewModel per cambiare la password
            changePasswordViewModel.changePassword(userEmail, vecchiaPassword, nuovaPassword, confermaPassword);
        });
    }
    //NON VA SE PREMO AGGIORNA NON FUNZIONA
}
