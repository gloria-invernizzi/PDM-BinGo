package com.application.bingo.ui.home;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.application.bingo.PrefsManager;
import com.application.bingo.R;
import com.application.bingo.ui.viewmodel.ChangeEmailViewModel;
import com.application.bingo.ui.viewmodel.ViewModelFactory;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ChangeEmailFragment extends Fragment {

    private TextInputEditText vecchiaEmailEditText, nuovaEmailEditText, confermaEmailEditText;
    private MaterialButton btnChangeEmail;
    private ChangeEmailViewModel changeEmailViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_email, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("ChangeEmailFragment", "onViewCreated chiamato");

        vecchiaEmailEditText = ((TextInputEditText) ((TextInputLayout) view.findViewById(R.id.vecchia_email)).getEditText());
        nuovaEmailEditText = ((TextInputEditText) ((TextInputLayout) view.findViewById(R.id.nuova_email)).getEditText());
        confermaEmailEditText = ((TextInputEditText) ((TextInputLayout) view.findViewById(R.id.conferma_email)).getEditText());
        btnChangeEmail = view.findViewById(R.id.btn_change_email);

        PrefsManager prefsManager = new PrefsManager(requireContext());
        String savedEmail = prefsManager.getSavedEmail();
        if (vecchiaEmailEditText != null) vecchiaEmailEditText.setText(savedEmail);

        // ViewModel
        changeEmailViewModel = new ViewModelProvider(this,
                new ViewModelFactory(requireActivity().getApplication())
        ).get(ChangeEmailViewModel.class);

        // Observer messaggi
        changeEmailViewModel.getMessageLiveData().observe(getViewLifecycleOwner(), message -> {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            btnChangeEmail.setEnabled(true);
            if ("Email aggiornata con successo".equals(message)) {
                if (vecchiaEmailEditText != null) vecchiaEmailEditText.setText("");
                if (nuovaEmailEditText != null) nuovaEmailEditText.setText("");
                if (confermaEmailEditText != null) confermaEmailEditText.setText("");
                NavHostFragment.findNavController(ChangeEmailFragment.this).popBackStack();
            }
        });

        // Click button
        btnChangeEmail.setOnClickListener(v -> {
            String oldEmail = vecchiaEmailEditText != null && vecchiaEmailEditText.getText() != null
                    ? vecchiaEmailEditText.getText().toString().trim() : "";
            String newEmail = nuovaEmailEditText != null && nuovaEmailEditText.getText() != null
                    ? nuovaEmailEditText.getText().toString().trim() : "";
            String confirmEmail = confermaEmailEditText != null && confermaEmailEditText.getText() != null
                    ? confermaEmailEditText.getText().toString().trim() : "";

            if (TextUtils.isEmpty(oldEmail) || TextUtils.isEmpty(newEmail) || TextUtils.isEmpty(confirmEmail)) {
                Toast.makeText(requireContext(), "Compila tutti i campi", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newEmail.equals(confirmEmail)) {
                Toast.makeText(requireContext(), "Le email non corrispondono", Toast.LENGTH_SHORT).show();
                return;
            }

            btnChangeEmail.setEnabled(false);
            Log.d("ChangeEmailFragment", "Chiamo ViewModel con vecchia='" + oldEmail + "' nuova='" + newEmail + "'");
            changeEmailViewModel.changeEmail(oldEmail, newEmail, confirmEmail);
        });
    }
}
