package com.application.bingo.ui.home.scan;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.bingo.R;
import com.application.bingo.ui.viewmodel.ProductViewModel;
import com.application.bingo.ui.viewmodel.ViewModelFactory;
import com.application.bingo.util.scanner.AppCustomScanFragment;
import com.application.bingo.util.scanner.CustomImageAnalyzer;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

/**
 * Fragment responsible for handling barcode scanning using CameraX.
 */
public class ScanFragment extends AppCustomScanFragment {

    private PreviewView previewView;
    private String lastBarcodeScanned;
    private ProductViewModel productViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        productViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelFactory(requireActivity().getApplication())).get(ProductViewModel.class);

        return inflater.inflate(R.layout.fragment_scan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        previewView = view.findViewById(R.id.camera_preview);

        // Check for camera permissions
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
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

    /**
     * Initializes and starts the CameraX camera.
     */
    private void startCamera() {
        // Camera preview asynchronous computation result
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(requireContext());

        // Listener that executes once the camera is initialized
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                ImageAnalysis imageAnalysis =
                        new ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build();

                imageAnalysis.setAnalyzer(
                        ContextCompat.getMainExecutor(requireContext()),
                        new CustomImageAnalyzer(this)
                );

                cameraProvider.unbindAll();

                // Initialize camera and define lifecycle
                // https://developer.android.com/media/camera/camerax/architecture
                cameraProvider.bindToLifecycle(
                        this,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis
                );

            } catch (ExecutionException | InterruptedException e) {
                Log.e("ScanFragment", "Error starting camera", e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    /**
     * Returns the last barcode scanned by this fragment.
     * @return The last scanned barcode string.
     */
    public String getLastScanned() {
        return this.lastBarcodeScanned;
    }

    /**
     * Sets the last scanned barcode string.
     * @param lastScanned The barcode string to set.
     * @return This ScanFragment instance.
     */
    public ScanFragment setLastScanned(String lastScanned) {
        this.lastBarcodeScanned = lastScanned;
        return this;
    }

    /**
     * Returns the ProductViewModel associated with this fragment.
     * @return The ProductViewModel instance.
     */
    public ProductViewModel getProductViewModel() {
        return this.productViewModel;
    }
}
