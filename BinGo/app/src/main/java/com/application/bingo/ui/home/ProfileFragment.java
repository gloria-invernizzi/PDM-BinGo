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
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.application.bingo.database.AppDatabase;
import com.application.bingo.PrefsManager;
import com.application.bingo.R;
import com.application.bingo.database.User;
import com.application.bingo.repository.UserRepository;
import com.application.bingo.viewmodel.ProfileViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {

    private MaterialToolbar topAppBar;
    private ImageView profileImage;
    private ImageView btnEditPhoto;
    private TextInputEditText inputName;
    private TextInputEditText inputEmail;
    private TextInputEditText inputAddress;
    private MaterialButton btnEditSave, btnLogout;

    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private Uri cameraImageUri;

    private boolean isEditing = false;
    private ProfileViewModel vm;
    private ActivityResultLauncher<String> cameraPermissionLauncher;

    public ProfileFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // -------------------------------
        //  LANCIO GALLERIA
        // -------------------------------
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri img = result.getData().getData();

                        // Salva la URI in database + LiveData
                        String email = inputEmail.getText().toString().trim();
                        vm.savePhotoUri(email, img.toString());

                        profileImage.setImageURI(img);
                    }
                }
        );

        // -------------------------------
        //  LANCIO FOTOCAMERA
        // -------------------------------
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        String email = inputEmail.getText().toString().trim();
                        vm.savePhotoUri(email, cameraImageUri.toString());

                        profileImage.setImageURI(cameraImageUri);
                    }
                }
        );

        // -------------------------------
        //  PERMESSO FOTOCAMERA
        // -------------------------------
        cameraPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                    if (granted) {
                        takePhotoInternal();
                    } else {
                        Toast.makeText(getContext(), "Permesso fotocamera negato", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        topAppBar = view.findViewById(R.id.topAppBar);
        profileImage = view.findViewById(R.id.profile_image);
        btnEditPhoto = view.findViewById(R.id.btn_change_photo);
        inputName = view.findViewById(R.id.input_name);
        inputEmail = view.findViewById(R.id.input_email);
        inputAddress = view.findViewById(R.id.input_address);
        btnEditSave = view.findViewById(R.id.btn_edit_save);
        btnLogout = view.findViewById(R.id.btn_logout);

        inputEmail.setEnabled(false);

        // -------------------------------
        //  CREATE VIEWMODEL
        // -------------------------------
        vm = new ViewModelProvider(
                this,
                new ViewModelProvider.Factory() {
                    @NonNull
                    @Override
                    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                        UserRepository userRepo = new UserRepository(requireContext());
                        return (T) new ProfileViewModel(userRepo);
                    }
                }
        ).get(ProfileViewModel.class);

        // -------------------------------
        //  OBSERVER -> aggiorna UI quando l'utente cambia
        // -------------------------------
        vm.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                inputName.setText(user.getName());
                inputEmail.setText(user.getEmail());
                inputAddress.setText(user.getAddress());

                // Imposta la foto dall'URI salvata
                if (user.getPhotoUri() != null && !user.getPhotoUri().isEmpty()) {
                    profileImage.setImageURI(Uri.parse(user.getPhotoUri()));
                }
            }
        });

        // -------------------------------
        //  PREFS LOADING
        // -------------------------------
        PrefsManager prefs = new PrefsManager(getContext());
        String savedEmail = prefs.getSavedEmail();

        if (!savedEmail.isEmpty()) {
            vm.loadUser(savedEmail); // carica User da DB + LiveData
        }

        // -------------------------------
        //  FOTO PROFILO
        // -------------------------------
        btnEditPhoto.setOnClickListener(v -> showPhotoDialog());

        // -------------------------------
        //  MODIFICA -> SALVA PROFILO
        // -------------------------------
        btnEditSave.setOnClickListener(v -> handleEditSave());

        // -------------------------------
        //  LOGOUT
        // -------------------------------
        btnLogout.setOnClickListener(v -> performLogout());

        // -------------------------------
        //  SETTINGS NELLA TOPBAR
        // -------------------------------
        topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_settings) {
                NavHostFragment.findNavController(ProfileFragment.this)
                        .navigate(R.id.action_profileFragment_to_settingsFragment);
                return true;
            }
            return false;
        });
    }

    // ----------------------------------------------------
    //   GESTIONE BOTTONE MODIFICA/SALVA
    // ----------------------------------------------------
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

        final String name = inputName.getText() == null ? "" : inputName.getText().toString().trim();
        final String email = inputEmail.getText().toString().trim();
        final String address = inputAddress.getText() == null ? "" : inputAddress.getText().toString().trim();

        PrefsManager pm = new PrefsManager(requireContext());

        Executors.newSingleThreadExecutor().execute(() -> {

            User existing = AppDatabase.getInstance(requireContext()).userDao().findByEmail(email);

            if (existing != null) {
                existing.setName(name);
                existing.setAddress(address);
                AppDatabase.getInstance(requireContext()).userDao().update(existing);

                // Aggiorna LiveData nel ViewModel
                vm.loadUser(email);
            }

            if (pm.isRemember()) {
                pm.saveUser(name, address, email, pm.getSavedPassword());
            }

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "Dati salvati", Toast.LENGTH_SHORT).show();
                btnEditSave.setText("Modifica");
            });
        });
    }

    // ----------------------------------------------------
    //   LOGOUT
    // ----------------------------------------------------
    private void performLogout() {
        FirebaseAuth.getInstance().signOut();

        PrefsManager pm = new PrefsManager(requireContext());
        pm.clearSavedUser();
        pm.setRemember(false);

        NavHostFragment.findNavController(this)
                .navigate(R.id.action_profileFragment_to_loginFragment2);

        Toast.makeText(getContext(), "Logout eseguito", Toast.LENGTH_SHORT).show();
    }

    // ----------------------------------------------------
    //   FOTO PROFILO
    // ----------------------------------------------------
    private void showPhotoDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle("Cambia foto profilo");
        String[] opzioni = {"Scegli dalla galleria", "Scatta una foto"};

        builder.setItems(opzioni, (dialog, which) -> {
            if (which == 0) pickFromGallery();
            else takePhoto();
        });

        builder.show();
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
            return;
        }
        takePhotoInternal();
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
}
