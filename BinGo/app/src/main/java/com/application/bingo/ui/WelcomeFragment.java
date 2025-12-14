package com.application.bingo.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.application.bingo.R;

public class WelcomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = NavHostFragment.findNavController(this);

        // Vai al Login
        view.findViewById(R.id.btnGoToLogin).setOnClickListener(v ->
                navController.navigate(R.id.action_welcomeFragment_to_loginFragment)
        );

        // Vai alla Registrazione
        view.findViewById(R.id.btnGoToRegister).setOnClickListener(v ->
                navController.navigate(R.id.action_welcomeFragment_to_registerFragment)
        );
    }
}
