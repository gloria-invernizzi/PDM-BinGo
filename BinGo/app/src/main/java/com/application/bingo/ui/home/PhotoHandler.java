package com.application.bingo.ui.home;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;

import com.application.bingo.R;

/**
 * PhotoHandler:
 * Classe helper per gestire la selezione della foto profilo.
 * Si occupa di:
 * - Aprire la galleria
 * - Scattare foto con la fotocamera
 * - Gestire URI e risultati
 * Restituisce l'URI al Fragment tramite callback.
 */
public class PhotoHandler {

    private final Context context;
    private final ActivityResultLauncher<Intent> galleryLauncher;
    private final ActivityResultLauncher<Intent> cameraLauncher;
    private Uri cameraImageUri;

    public interface PhotoCallback {

        void onPhotoSelected(Uri uri);
    }

    private final PhotoCallback callback;

    public PhotoHandler(Context context,
                        ActivityResultLauncher<Intent> galleryLauncher,
                        ActivityResultLauncher<Intent> cameraLauncher,
                        PhotoCallback callback) {
        this.context = context;
        this.galleryLauncher = galleryLauncher;
        this.cameraLauncher = cameraLauncher;
        this.callback = callback;
    }

    /** Apri la galleria */
    public void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    /** Scatta foto con la fotocamera */
    public void takePhoto() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "ProfilePhoto");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Foto profilo");

        cameraImageUri = context.getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
        cameraLauncher.launch(intent);
    }

    /** Gestisce risultato galleria */
    public void handleGalleryResult(Intent data) {
        if (data != null && data.getData() != null) {
            callback.onPhotoSelected(data.getData());
        } else {
            Toast.makeText(context, context.getString(R.string.no_photo_selected), Toast.LENGTH_SHORT).show();
        }
    }

    /** Gestisce risultato fotocamera */
    public void handleCameraResult() {
        if (cameraImageUri != null) {
            callback.onPhotoSelected(cameraImageUri);
        } else {
            Toast.makeText(context, context.getString(R.string.camera_error), Toast.LENGTH_SHORT).show();
        }
    }
}
