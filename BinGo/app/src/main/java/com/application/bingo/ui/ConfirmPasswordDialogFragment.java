package com.application.bingo.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.application.bingo.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

public class ConfirmPasswordDialogFragment extends DialogFragment {

    public interface PasswordListener {
        void onPasswordConfirmed(String password);
    }

    private final PasswordListener listener;
    private TextInputEditText passwordEditText;

    public ConfirmPasswordDialogFragment(PasswordListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_confirm_password, null);

        passwordEditText = view.findViewById(R.id.password_edit_text);

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Conferma identità")
                .setView(view)
                .setPositiveButton("Conferma", null) //  listener dopo
                .setNegativeButton("Annulla", (dialog, which) -> dismiss())
                .create();
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog == null) return;

        dialog.findViewById(android.R.id.button1)
                .setOnClickListener(v -> {

                    String password = passwordEditText.getText() != null
                            ? passwordEditText.getText().toString().trim()
                            : "";

                    if (password.isEmpty()) {
                        passwordEditText.setError("Inserisci la password");
                        return; //  NON chiude il dialog
                    }

                    if (listener != null) {
                        listener.onPasswordConfirmed(password);
                    }

                    dismiss(); //  chiude SOLO se valido
                });
    }
}

