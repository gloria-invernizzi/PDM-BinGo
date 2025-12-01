package com.application.bingo;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import android.provider.MediaStore;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileFragment extends Fragment {

    // COMPONENTI UI
    private MaterialToolbar topAppBar;
    private ImageView profileImage;       // Foto profilo
    private ImageView btnEditPhoto;       // Icona per cambiare foto
    private TextInputEditText inputName;
    private TextInputEditText inputEmail;
    private TextInputEditText inputAddress;
    private MaterialButton btnEditSave, btnLogout;

    // LAUNCHER PER GALLERIA E FOTOCAMERA
    private ActivityResultLauncher<Intent> galleryLauncher;   // <-- Launcher per aprire la galleria
    private ActivityResultLauncher<Intent> cameraLauncher;    // <-- Launcher per aprire la fotocamera
    private Uri cameraImageUri;                               // <-- Dove verrà salvata la foto scattata

    // STATO MODIFICA/ SALVA
    private boolean isEditing = false;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // REGISTRAZIONE DEL LAUNCHER PER LA GALLERIA

        // 1. Creo l’oggetto che descrive il tipo di operazione da fare:
        //    in questo caso: “avvia un’Activity e aspettati un risultato”.
        ActivityResultContracts.StartActivityForResult contratto =
                new ActivityResultContracts.StartActivityForResult();

        // 2. Creo il callback, cioè cosa deve succedere quando la galleria
        //    restituisce un risultato.
        ActivityResultCallback<ActivityResult> callback =
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        // Controllo se l'utente ha selezionato un'immagine correttamente
                        if (result.getResultCode() == Activity.RESULT_OK &&
                                result.getData() != null) {

                            // Ottengo l'URI dell'immagine scelta dalla galleria
                            Uri img = result.getData().getData();

                            // Mostro l'immagine nella ImageView
                            profileImage.setImageURI(img);
                        }
                    }
                };

        // 3. Registro il launcher usando il contratto e il callback creati sopra
        galleryLauncher = registerForActivityResult(contratto, callback);


        // REGISTRAZIONE DEL LAUNCHER PER LA FOTOCAMERA

        ActivityResultContracts.StartActivityForResult contrattoFotocamera = new ActivityResultContracts.StartActivityForResult();

        ActivityResultCallback<ActivityResult> callbackFotocamera =
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // La foto è già salvata nell'URI cameraImageUri
                            // Mostro l'immagine direttamente nella ImageView
                            profileImage.setImageURI(cameraImageUri);
                        }
                    }
                };

        // Registro il launcher usando il contratto e il callback creati sopra
        cameraLauncher = registerForActivityResult(contrattoFotocamera, callbackFotocamera);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // TROVA LE VIEWS NEL LAYOUT

        topAppBar = view.findViewById(R.id.topAppBar);
        profileImage = view.findViewById(R.id.profile_image);
        btnEditPhoto = view.findViewById(R.id.btn_change_photo);
        inputName = view.findViewById(R.id.input_name);
        inputEmail = view.findViewById(R.id.input_email);
        inputAddress = view.findViewById(R.id.input_address);
        btnEditSave = view.findViewById(R.id.btn_edit_save);
        btnLogout = view.findViewById(R.id.btn_logout);


        // CLICK LISTENER PER CAMBIARE FOTO → apre un menu con 2 scelte

        btnEditPhoto.setOnClickListener(v -> showPhotoDialog());

        // CLICK LISTENER BOTTONE MODIFICA/SALVA

        btnEditSave.setOnClickListener(v -> {
            if (!isEditing) {
                // Passo a modalità Modifica: rendi i campi editabili
                isEditing = true;
                inputName.setEnabled(true);
                inputEmail.setEnabled(true);
                inputAddress.setEnabled(true);
                btnEditSave.setText("Salva");
            } else {
                // Passo a modalità Salva: disabilita campi e salva dati
                isEditing = false;
                inputName.setEnabled(false);
                inputEmail.setEnabled(false);
                inputAddress.setEnabled(false);

                // aggiungere la logica reale di salvataggio dati
                Toast.makeText(getContext(), "Dati salvati", Toast.LENGTH_SHORT).show();

                btnEditSave.setText("Modifica");
            }
        });


        // NAVIGAZIONE TOP APP BAR

        topAppBar.setNavigationOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        // GESTIONE MENU TOP APP BAR

        if (getActivity() != null) {
            getActivity().addMenuProvider(new MenuProvider() {
                @Override
                public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                    topAppBar.getMenu().clear(); // rimuove eventuali altri menu
                    topAppBar.inflateMenu(R.menu.profile_top_menu);
                }

                @Override
                public boolean onMenuItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId(); // id item selezionato

                    if (id == R.id.action_notifications) {
                        Toast.makeText(getContext(), "Apri notifiche", Toast.LENGTH_SHORT).show();
                        return true;
                    } else if (id == R.id.action_settings) {
                        Toast.makeText(getContext(), "Apri impostazioni", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    return false;
                }
            }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
        }


        // BOTTONE LOGOUT

        btnLogout.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Logout eseguito", Toast.LENGTH_SHORT).show();
            if (getActivity() != null) {
                getActivity().finish(); // chiude l'Activity contenente il Fragment
            }
        });
    }

    // METODO PER MOSTRARE IL DIALOG PER CAMBIARE FOTO

    private void showPhotoDialog() {

        // Creo il builder del dialog in stile Material
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());

        // Imposto il titolo del popup
        builder.setTitle("Cambia foto profilo");

        // Definisco le opzioni che compariranno nella lista del dialog
        String[] opzioni = new String[]{"Scegli dalla galleria", "Scatta una foto"};

        // Definisco il listener per gestire il click sulle opzioni
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0) {
                    // L’utente ha scelto la prima opzione: "Galleria"
                    pickFromGallery();
                } else if (which == 1) {
                    // L’utente ha scelto la seconda opzione: "Fotocamera"
                    takePhoto();
                }
            }
        };

        // Imposto le opzioni e il listener nel dialog
        builder.setItems(opzioni, listener);

        // Mostro il dialog
        builder.show();
    }


    // METODO PER APRIRE LA GALLERIA

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK); // <-- Intent per scegliere un file multimediale
        intent.setType("image/*");                      // <-- Filtra solo immagini
        galleryLauncher.launch(intent);                 // <-- Avvia la galleria
    }


    // METODO PER APRIRE LA FOTOCAMERA

    private void takePhoto() {

        // CREAZIONE METADATI DELLA FOTO (titolo, descrizione)
        ContentValues values = new ContentValues();              // <-- Crea contenitore parametri foto
        values.put(MediaStore.Images.Media.TITLE, "ProfilePhoto");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Foto profilo");

        // CREA URI DOVE LA FOTOCAMERA SALVERÀ LA FOTO
        cameraImageUri = requireActivity().getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        // INTENT PER LA FOTOCAMERA
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // <-- Avvia app fotocamera
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);    // <-- Indichiamo dove salvare l'immagine

        // AVVIA LA FOTOCAMERA
        cameraLauncher.launch(intent);
    }









}
