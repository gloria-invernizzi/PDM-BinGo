package com.application.bingo.util.scanner;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.navigation.Navigation;

import com.application.bingo.R;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

public class CustomImageAnalyzer implements ImageAnalysis.Analyzer {

    private final AppCustomScanFragment scanFragment;
    public CustomImageAnalyzer(AppCustomScanFragment scanFragment)
    {
        this.scanFragment = scanFragment;
    }
    
    @Override
    @OptIn(markerClass = ExperimentalGetImage.class)
    public void analyze(@NonNull ImageProxy image) {
        if (image.getImage() != null) {
            InputImage inputImage = InputImage.fromMediaImage(
                    image.getImage(),
                    image.getImageInfo().getRotationDegrees()
            );

            BarcodeScanning.getClient().process(inputImage)
                    .addOnSuccessListener(barcodes -> {
                        for (Barcode barcode : barcodes) {
                            String value = barcode.getRawValue();

                            if (value != null && !value.equalsIgnoreCase(scanFragment.getLastScanned())) {
                                scanFragment.setLastScanned(value);

                                scanFragment.getProductViewModel().updateBarcode(value);

                                Navigation.findNavController(scanFragment.requireView()).navigate(R.id.action_scanFragment_to_resultFragment);

                                break;
                            }
                        }
                    })
                    .addOnCompleteListener(task -> image.close());
        } else {
            image.close();
        }
    }
}
