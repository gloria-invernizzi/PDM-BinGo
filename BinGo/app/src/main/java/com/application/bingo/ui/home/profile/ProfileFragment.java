package com.application.bingo.ui.home.profile;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.application.bingo.R;
import com.application.bingo.PrefsManager;
import com.application.bingo.ui.viewmodel.ProfileViewModel;
import com.application.bingo.ui.viewmodel.ViewModelFactory;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private ImageView profileImage, btnEditPhoto;
    private TextInputEditText inputName, inputEmail, inputAddress;
    private MaterialButton btnEditSave, btnLogout;
    private ImageButton btnSettings;
    private ProfileViewModel vm;
    private PrefsManager prefs;
    private PhotoHandler photoHandler;

    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;

    private boolean isEditing = false;

    public ProfileFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = new PrefsManager(requireContext());

        // -----------------------------------------------------------------------------------------
        // LANCIO GALLERIA
        // -----------------------------------------------------------------------------------------
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        photoHandler.handleGalleryResult(result.getData());
                    }
                }
        );

        // -----------------------------------------------------------------------------------------
        // LANCIO FOTOCAMERA
        // -----------------------------------------------------------------------------------------
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        photoHandler.handleCameraResult();
                    }
                }
        );

        // -----------------------------------------------------------------------------------------
        // PERMESSO FOTOCAMERA
        // -----------------------------------------------------------------------------------------
        cameraPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                    if (granted) photoHandler.takePhoto();
                    else Toast.makeText(getContext(), getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                });

        // -----------------------------------------------------------------------------------------
        // PHOTO HANDLER
        // -----------------------------------------------------------------------------------------
        photoHandler = new PhotoHandler(requireContext(), galleryLauncher, cameraLauncher, uri -> {
            String email = inputEmail.getText().toString().trim();
            vm.savePhotoUri(email, uri.toString());
            profileImage.setImageURI(uri);
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        setupViewModel();
        setupObservers();
        loadUserFromPrefs();
        setupButtons();
    }

    // ---------------------------------------------------------------------------------------------
    // INIZIALIZZAZIONE VIEWMODEL
    // ---------------------------------------------------------------------------------------------
    private void setupViewModel() {
        ViewModelFactory factory = new ViewModelFactory(requireActivity().getApplication());
        vm = new ViewModelProvider(this, factory).get(ProfileViewModel.class);
    }

    // ---------------------------------------------------------------------------------------------
    // OSSERVA LIVE DATA UTENTE
    // ---------------------------------------------------------------------------------------------
    private void setupObservers() {

        // Observer dei dati dell'utente
        vm.getUser().observe(getViewLifecycleOwner(), user -> {
            Log.d("ProfileFragment", "Observer ProfileViewModel: user=" + user);
            if (user != null) {
                Log.d("ProfileFragment", "Imposto UI: "
                        + user.getName() + ", "
                        + user.getEmail() + ", "
                        + user.getAddress());
                inputName.setText(user.getName());
                inputEmail.setText(user.getEmail());
                inputAddress.setText(user.getAddress());

                if (user.getPhotoUri() != null && !user.getPhotoUri().isEmpty()) {
                    profileImage.setImageURI(Uri.parse(user.getPhotoUri()));
                }
            } else {
                Log.d("ProfileFragment", "VM non ha restituito nessun utente");
                // Mostra messaggio internazionalizzato
                Toast.makeText(requireContext(), getString(R.string.user_not_found), Toast.LENGTH_SHORT).show();
            }
        });

        // Observer dei messaggi di errore (chiavi logiche dal ViewModel)
        vm.getError().observe(getViewLifecycleOwner(), msgKey -> {
            if (msgKey != null) {
                String message;
                switch (msgKey) {
                    case "user_not_found":
                        message = getString(R.string.user_not_found);
                        break;
                    case "user_not_loaded":
                        message = getString(R.string.user_not_loaded);
                        break;
                    default:
                        message = msgKey; // fallback per eventuali messaggi dinamici dal repo
                }
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    // ---------------------------------------------------------------------------------------------
    // CARICA UTENTE DAI PREFS
    // ---------------------------------------------------------------------------------------------
    private void loadUserFromPrefs() {
        String savedEmail = prefs.getSavedEmail();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("ProfileFragment", "loadUserFromPrefs chiamato");
        Log.d("ProfileFragment", "prefs savedEmail = " + savedEmail);
        Log.d("ProfileFragment", "firebaseUser = " + (firebaseUser != null ? firebaseUser.getEmail() : "null"));

        String emailToLoad = null;

        if (savedEmail != null && !savedEmail.isEmpty()) {
            emailToLoad = savedEmail;
            // Popolamento rapido per migliorare la UX
            Log.d("ProfileFragment", "Carico da prefs: name=" + prefs.getSavedName()
                    + ", address=" + prefs.getSavedAddress());
            inputName.setText(prefs.getSavedName());
            inputAddress.setText(prefs.getSavedAddress());
            Log.d("ProfileFragment", "Setto email temporanea: " + savedEmail);
            inputEmail.setText(savedEmail);
        } else if (firebaseUser != null && firebaseUser.getEmail() != null) {
            emailToLoad = firebaseUser.getEmail();
        }

        if (emailToLoad != null) {
            // Usiamo la tua VM per caricare i dati reali dal DB/Network
            Log.d("ProfileFragment", "Chiamo vm.loadUser con email = " + emailToLoad);
            vm.loadUser(emailToLoad);
        } else {
            // Solo se non c'è NESSUNA traccia dell'utente torniamo alla Welcome
            Log.d("ProfileFragment", "Utente non identificato, ritorno a Welcome");
            try {
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.welcomeFragment);
            } catch (Exception e) {
                // Se non trovi il NavController o la destinazione, usa il back
                if (isAdded()) requireActivity().onBackPressed();
            }
        }
        Log.d("ProfileFragment", "savedEmail = " + savedEmail);
        Log.d("ProfileFragment", "firebaseEmail = " +
                (firebaseUser != null ? firebaseUser.getEmail() : "null"));

        // L'aggiornamento della UI avverrà tramite l'osservatore già definito in setupObservers().
    }


    // ---------------------------------------------------------------------------------------------
    // BOTTONI
    // ---------------------------------------------------------------------------------------------
    private void setupButtons() {
        btnSettings.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.settingsFragment);
        });
        btnEditPhoto.setOnClickListener(v -> showPhotoDialog());
        btnEditSave.setOnClickListener(v -> handleEditSave());
        btnLogout.setOnClickListener(v -> performLogout());
    }

    // ---------------------------------------------------------------------------------------------
    // MODIFICA / SALVA
    // ---------------------------------------------------------------------------------------------
    private void handleEditSave() {
        if (!isEditing) {
            isEditing = true;
            inputName.setEnabled(true);
            inputAddress.setEnabled(true);
            btnEditSave.setText(getString(R.string.save));
            return;
        }

        isEditing = false;
        inputName.setEnabled(false);
        inputAddress.setEnabled(false);
        // Passa tutto al ViewModel
        vm.updateProfile(
                inputName.getText().toString().trim(),
                inputAddress.getText().toString().trim()
        );

        Toast.makeText(getContext(), getString(R.string.data_saved), Toast.LENGTH_SHORT).show();
        btnEditSave.setText(getString(R.string.edit));
    }

    // ---------------------------------------------------------------------------------------------
    // LOGOUT
    // ---------------------------------------------------------------------------------------------
    private void performLogout() {
        FirebaseAuth.getInstance().signOut();

        // NON cancello tutto
        prefs.clearLoginOnly();

        NavController navController = Navigation.findNavController(requireActivity(),
                R.id.fragmentContainerView);
        navController.navigate(R.id.welcomeFragment);
    }

    // ---------------------------------------------------------------------------------------------
    // DIALOG FOTO PROFILO
    // ---------------------------------------------------------------------------------------------
    private void showPhotoDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.change_photo)
                .setItems(
                        new String[]{
                            getString(R.string.photo_from_gallery),
                            getString(R.string.photo_from_camera)},
                        (dialog, which) -> {
                            if (which == 0) photoHandler.pickFromGallery();
                            else takePhotoWithPermission();
                        }).show();
    }

    private void takePhotoWithPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        } else {
            photoHandler.takePhoto();
        }
    }

    // ---------------------------------------------------------------------------------------------
    // SETUP VIEW
    // ---------------------------------------------------------------------------------------------
    private void setupViews(View view) {
        btnSettings = view.findViewById(R.id.btn_settings);
        profileImage = view.findViewById(R.id.profile_image);
        btnEditPhoto = view.findViewById(R.id.btn_change_photo);
        inputName = view.findViewById(R.id.input_name);
        inputEmail = view.findViewById(R.id.input_email);
        inputAddress = view.findViewById(R.id.input_address);
        btnEditSave = view.findViewById(R.id.btn_edit_save);
        btnLogout = view.findViewById(R.id.btn_logout);

        inputEmail.setEnabled(false);
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d("ProfileFragment", "onResume chiamato");

        loadUserFromPrefs();
    }

}
