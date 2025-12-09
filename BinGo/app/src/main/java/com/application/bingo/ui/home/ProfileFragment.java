// File: `BinGo/app/src/main/java/com/application/bingo/ui/home/ProfileFragment.java`
package com.application.bingo.ui.home;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.application.bingo.util.database.AppDatabase;
import com.application.bingo.PrefsManager;
import com.application.bingo.R;
import com.application.bingo.util.database.User;
import com.application.bingo.repository.UserRepository;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

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
    private static final int CAMERA_PERMISSION_REQUEST = 200;

    private boolean isEditing = false;
    private ProfileViewModel vm;

    public ProfileFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityResultContracts.StartActivityForResult contratto = new ActivityResultContracts.StartActivityForResult();
        ActivityResultCallback<ActivityResult> callback = result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri img = result.getData().getData();
                profileImage.setImageURI(img);
            }
        };
        galleryLauncher = registerForActivityResult(contratto, callback);

        ActivityResultContracts.StartActivityForResult contrattoFotocamera = new ActivityResultContracts.StartActivityForResult();
        ActivityResultCallback<ActivityResult> callbackFotocamera = result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                profileImage.setImageURI(cameraImageUri);
            }
        };
        cameraLauncher = registerForActivityResult(contrattoFotocamera, callbackFotocamera);
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

        vm.getUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    inputName.setText(user.getName());
                    inputEmail.setText(user.getEmail());
                    inputAddress.setText(user.getAddress());
                }
            }
        });

        // Pre-fill dai Prefs (fallback veloce)
        PrefsManager prefs = new PrefsManager(getContext());
        String savedName = prefs.getSavedName();
        String savedEmail = prefs.getSavedEmail();
        String savedAddress = prefs.getSavedAddress();
        if (!savedName.isEmpty() && inputName.getText().toString().isEmpty()) inputName.setText(savedName);
        if (!savedEmail.isEmpty() && inputEmail.getText().toString().isEmpty()) inputEmail.setText(savedEmail);
        if (!savedAddress.isEmpty() && inputAddress.getText().toString().isEmpty()) inputAddress.setText(savedAddress);

        // Carica dal ViewModel se esiste email salvata
        if (!savedEmail.isEmpty()) {
            Executors.newSingleThreadExecutor().execute(() -> vm.loadUser(savedEmail));
        }

        btnEditPhoto.setOnClickListener(v -> showPhotoDialog());

        btnEditSave.setOnClickListener(v -> {
            if (!isEditing) {
                isEditing = true;
                inputName.setEnabled(true);
                inputEmail.setEnabled(true);
                inputAddress.setEnabled(true);
                btnEditSave.setText("Salva");
            } else {
                isEditing = false;
                inputName.setEnabled(false);
                inputEmail.setEnabled(false);
                inputAddress.setEnabled(false);

                // Salva dati in Room e, se remember=true, aggiorna Prefs
                final String name = inputName.getText() == null ? "" : inputName.getText().toString().trim();
                final String email = inputEmail.getText() == null ? "" : inputEmail.getText().toString().trim();
                final String address = inputAddress.getText() == null ? "" : inputAddress.getText().toString().trim();
                final PrefsManager pm = new PrefsManager(requireContext());
                Executors.newSingleThreadExecutor().execute(() -> {
                    User existing = AppDatabase.getInstance(requireContext()).userDao().findByEmail(email);
                    if (existing != null) {
                        existing.setName(name);
                        existing.setAddress(address);
                        existing.setEmail(email);
                        AppDatabase.getInstance(requireContext()).userDao().update(existing);
                    } else {
                        // se non esiste, inseriscilo (password lasciata vuota se non nota)
                        User u = new User(name, address, email, pm.getSavedPassword());
                        AppDatabase.getInstance(requireContext()).userDao().insert(u);
                    }
                    // aggiorna prefs solo se l'utente ha scelto remember
                    if (pm.isRemember()) {
                        pm.saveUser(name, address, email, pm.getSavedPassword());
                        pm.setRemember(true);
                    }
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Dati salvati", Toast.LENGTH_SHORT).show();
                        btnEditSave.setText("Modifica");
                    });
                });
            }
        });

        loadProfileData();

        topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_settings) {
                NavController navController = NavHostFragment.findNavController(ProfileFragment.this);
                navController.navigate(R.id.action_profileFragment_to_settingsFragment);
                return true;
            }
            return false;
        });

        btnLogout.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Logout eseguito", Toast.LENGTH_SHORT).show();
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }

    private void loadProfileData() {
        PrefsManager prefs = new PrefsManager(getContext());
        String savedEmail = prefs.getSavedEmail();
        if (!savedEmail.isEmpty()) {
            Executors.newSingleThreadExecutor().execute(() -> vm.loadUser(savedEmail));
        }
    }

    private void showPhotoDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle("Cambia foto profilo");
        String[] opzioni = new String[]{"Scegli dalla galleria", "Scatta una foto"};
        DialogInterface.OnClickListener listener = (dialog, which) -> {
            if (which == 0) pickFromGallery();
            else if (which == 1) takePhoto();
        };
        builder.setItems(opzioni, listener);
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
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
            return;
        }
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "ProfilePhoto");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Foto profilo");
        cameraImageUri = requireActivity().getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
        cameraLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            } else {
                Toast.makeText(getContext(), "Permesso fotocamera negato", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Non tiene la foto profilo quando chiudo l'app
}
