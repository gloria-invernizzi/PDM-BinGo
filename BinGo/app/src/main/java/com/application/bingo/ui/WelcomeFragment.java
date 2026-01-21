package com.application.bingo.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.google.firebase.auth.FirebaseAuth;

import com.application.bingo.R;

/**
 * WelcomeFragment:
 * The entry point of the app where the user can choose to log in or register.
 * Also handles automatic navigation to the home screen if a session is already active.
 */
public class WelcomeFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = NavHostFragment.findNavController(this);

        // Check for an existing session (offline/online)
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            navController.navigate(R.id.action_welcomeFragment_to_home);
            return;
        }

        // Navigate to Login screen
        view.findViewById(R.id.btnGoToLogin).setOnClickListener(v ->
                navController.navigate(R.id.action_welcomeFragment_to_loginFragment)
        );

        // Navigate to Registration screen
        view.findViewById(R.id.btnGoToRegister).setOnClickListener(v ->
                navController.navigate(R.id.action_welcomeFragment_to_registerFragment)
        );
    }
}
