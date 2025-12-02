package com.application.bingo.ui.home;

import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.bingo.R;

public class HomeFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate il layout del fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    @Override
    //metodo chiamato dopo che la view del Fragment esiste
    //contiene dati salvati se il fragment viene ricreato dopo un cambio di configurazione (ad esempio rotazione dello schermo)
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CardView scanCard = view.findViewById(R.id.card_scan);
        scanCard.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_scanFragment));
        CardView manualCard = view.findViewById(R.id.card_manual);
        manualCard.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_manualEntryFragment));
        CardView whereToThrow = view.findViewById(R.id.where_to_throw);
        whereToThrow.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_whereToThrowFragment));

    }
}