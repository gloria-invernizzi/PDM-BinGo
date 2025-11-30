package com.application.bingo.ui.home;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import com.application.bingo.R;

public class ProfileFragment extends Fragment {
    private MaterialToolbar topAppBar;
    private ImageView profileImage;       // Foto profilo
    private ImageView btnEditPhoto;       // Icona per cambiare foto
    private TextInputEditText inputName;
    private TextInputEditText inputEmail;
    private TextInputEditText inputAddress;
    private MaterialButton btnEditSave, btnLogout;

    private boolean isEditing = false; // Stato Modifica/Salva

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Trova Views
        topAppBar = view.findViewById(R.id.topAppBar);
        profileImage = view.findViewById(R.id.profile_image);
        btnEditPhoto = view.findViewById(R.id.btn_change_photo);
        inputName = view.findViewById(R.id.input_name);
        inputEmail = view.findViewById(R.id.input_email);
        inputAddress = view.findViewById(R.id.input_address);
        btnEditSave = view.findViewById(R.id.btn_edit_save);
        btnLogout = view.findViewById(R.id.btn_logout);

        //topAppBar
        topAppBar.setNavigationOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        // Gestione menu
        if (getActivity() != null) {
            getActivity().addMenuProvider(new MenuProvider() {
                                              @Override
                                              public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                                                  topAppBar.getMenu().clear(); //rimuove eventuali altri menu
                                                  topAppBar.inflateMenu(R.menu.profile_top_menu);
                                              }

                                              @Override
                                              public boolean onMenuItemSelected(@NonNull MenuItem item) {
                                                  int id = item.getItemId(); //id item selezionato
                                                  //se è la campanella delle notifiche
                                                  if (id == R.id.action_notifications) {
                                                      //mostra messaggio temporaneo
                                                      Toast.makeText(getContext(), "Apri  notifiche", Toast.LENGTH_SHORT).show();
                                                      return true;
                                                  } else if (id == R.id.action_settings) {
                                                      Toast.makeText(getContext(), "Apri impostazioni", Toast.LENGTH_SHORT).show();
                                                      return true;
                                                  }
                                                  return false;
                                              }
                                          },
                    //getViewLifecycleOwner() lega il menu al lifecycle del fragment, così viene attivo solo quando la view è visibile.
                    //Lifecycle.State.RESUMED indica che il menu sarà disponibile quando il fragment è visibile e in foreground.
                    getViewLifecycleOwner(),
                    Lifecycle.State.RESUMED);
        }

        // Cambia foto profilo

        btnEditPhoto.setOnClickListener(v ->
                new AlertDialog.Builder(getContext())

        );

        // Bottone Modifica/Salva

        // Bottone Logout
        btnLogout.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Logout eseguito", Toast.LENGTH_SHORT).show();
            if (getActivity() != null) {
                getActivity().finish(); // chiude l'Activity contenente il Fragment
            }
        });

    }
}