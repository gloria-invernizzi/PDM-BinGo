package com.application.bingo.ui.home;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.application.bingo.R;
import com.application.bingo.PrefsManager;
import com.application.bingo.database.User;
import com.application.bingo.repository.UserRepository;
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

    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private Uri cameraImageUri;

    private boolean isEditing = false;

    private ProfileViewModel vm;
    private PrefsManager prefs;

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
                        Uri img = result.getData().getData();
                        String email = inputEmail.getText().toString().trim();

                        vm.savePhotoUri(email, img.toString()); // aggiorna Room + prefs
                        profileImage.setImageURI(img);
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
                        String email = inputEmail.getText().toString().trim();
                        vm.savePhotoUri(email, cameraImageUri.toString()); // aggiorna Room + prefs
                        profileImage.setImageURI(cameraImageUri);
                    }
                }
        );

        // -----------------------------------------------------------------------------------------
        // PERMESSO FOTOCAMERA
        // -----------------------------------------------------------------------------------------
        cameraPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                    if (granted) takePhotoInternal();
                    else Toast.makeText(getContext(), "Permesso negato", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("ProfileFragment", "onViewCreated called"); // <- LOG DI TEST
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
        vm.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                inputName.setText(user.getName());
                inputEmail.setText(user.getEmail());
                inputAddress.setText(user.getAddress());
                if (user.getPhotoUri() != null && !user.getPhotoUri().isEmpty()) {
                    profileImage.setImageURI(Uri.parse(user.getPhotoUri()));
                }
            }
        });
    }

    // ---------------------------------------------------------------------------------------------
    // CARICA UTENTE DAI PREFS
    // ---------------------------------------------------------------------------------------------
    private void loadUserFromPrefs() {
        String savedEmail = prefs.getSavedEmail();

        if (!savedEmail.isEmpty()) {
            // Aggiorna UI subito da prefs (se disponibili)
            inputName.setText(prefs.getSavedName());
            inputAddress.setText(prefs.getSavedAddress());
            String photo = prefs.getSavedPhotoUri();
            if (!photo.isEmpty()) profileImage.setImageURI(Uri.parse(photo));

            // Carica anche l'utente dal repository (Room/Firebase) per LiveData
            vm.loadUser(savedEmail);
        } else {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null && firebaseUser.getEmail() != null) {
                vm.loadUser(firebaseUser.getEmail());
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // BOTTONI
    // ---------------------------------------------------------------------------------------------
    private void setupButtons() {
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
            btnEditSave.setText("Salva");
            return;
        }

        isEditing = false;
        inputName.setEnabled(false);
        inputAddress.setEnabled(false);

        String name = inputName.getText().toString().trim();
        String address = inputAddress.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();

        vm.updateProfile(name, address); // aggiorna Room + prefs
        Toast.makeText(getContext(), "Dati salvati", Toast.LENGTH_SHORT).show();
        btnEditSave.setText("Modifica");
    }

    // ---------------------------------------------------------------------------------------------
    // LOGOUT
    // ---------------------------------------------------------------------------------------------
    private void performLogout() {
        FirebaseAuth.getInstance().signOut();
        prefs.clearSavedUser();
        prefs.setRemember(false);

        NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
        navController.navigate(R.id.welcomeFragment);
    }

    // ---------------------------------------------------------------------------------------------
    // FOTO PROFILO
    // ---------------------------------------------------------------------------------------------
    private void showPhotoDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Cambia foto")
                .setItems(new String[]{"Galleria", "Fotocamera"}, (dialog, which) -> {
                    if (which == 0) pickFromGallery();
                    else takePhoto();
                })
                .show();
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void takePhoto() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        } else {
            takePhotoInternal();
        }
    }

    private void takePhotoInternal() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "ProfilePhoto");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Foto profilo");

        cameraImageUri = requireActivity().getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
        cameraLauncher.launch(intent);
    }

    // ---------------------------------------------------------------------------------------------
    // SETUP VIEW
    // ---------------------------------------------------------------------------------------------
    private void setupViews(View view) {
        profileImage = view.findViewById(R.id.profile_image);
        btnEditPhoto = view.findViewById(R.id.btn_change_photo);
        inputName = view.findViewById(R.id.input_name);
        inputEmail = view.findViewById(R.id.input_email);
        inputAddress = view.findViewById(R.id.input_address);
        btnEditSave = view.findViewById(R.id.btn_edit_save);
        btnLogout = view.findViewById(R.id.btn_logout);

        inputEmail.setEnabled(false);
    }
}
