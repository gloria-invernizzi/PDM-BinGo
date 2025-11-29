package com.application.bingo.ui.home.scan;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.application.bingo.R;
import com.application.bingo.util.scanner.CustomImageAnalyzer;
import com.application.bingo.util.scanner.AppCustomScanActivity;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class ScanActivity extends AppCustomScanActivity {

    private PreviewView previewView;
    private String lastBarcodeScanned;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        previewView = findViewById(R.id.camera_preview);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 100);
        } else {
            startCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        }
    }

    private void startCamera() {
        // Camera preview asynchronous computation result
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        // Listener that execute once the camera is initialized
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                ImageAnalysis imageAnalysis =
                        new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                ;

                imageAnalysis.setAnalyzer(
                        ContextCompat.getMainExecutor(this),
                        new CustomImageAnalyzer(this)
                );

                cameraProvider.unbindAll();

                // Initialize camera and define lifecycle
                // https://developer.android.com/media/camera/camerax/architecture?hl=it#java
                cameraProvider.bindToLifecycle(
                        this,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis
                );

            } catch (ExecutionException | InterruptedException e) {
                Log.e("ScanActivity", e.getMessage() + " ");
            }
        }, ContextCompat.getMainExecutor(this));
    }

    public String getLastScanned() {
        return this.lastBarcodeScanned;
    }

    public ScanActivity setLastScanned(String lastScanned) {
        this.lastBarcodeScanned = lastScanned;

        return this;
    }
}