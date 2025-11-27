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
    private PrefsManager prefs; //manage shared preferences
    private final ExecutorService bg = Executors.newSingleThreadExecutor();

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //@Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button button = view.findViewById(R.id.login_button);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v){
                Log.w("loginFragment", "cliccato");
                Navigation.findNavController(v)
                        .navigate(R.id.action_loginFragment_to_homeFragment);
            }

        });
    }

    private void attemptLogin() {
        final String email = getText(etEmail);
        final String pass = getText(etPassword);

        if(email.isEmpty() || pass.isEmpty()){
            Toast.makeText(requireContext(), "Campi mancanti", Toast.LENGTH_SHORT).show();
            return;
        }

        //funzione lambda: eseguire il codice in un thread di background separato??
        bg.execute(() -> {
            User user = AppDatabase.getInstance(requireContext()).userDao().findByEmailAndPassword(email, pass);
            requireActivity().runOnUiThread(() -> {
                if (user == null) {
                    Toast.makeText(requireContext(), "Credenziali non valide", Toast.LENGTH_SHORT).show();
                } else {
                    prefs.saveUser(user.getName(), user.getEmail());
                    Toast.makeText(requireContext(), "Login effettuato con successo", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_loginFragment_to_homeFragment);
                }
            });
        });
    }

    private String getText(TextInputEditText et){
        return et.getText() != null ? et.getText().toString().trim() : "";
    }
}