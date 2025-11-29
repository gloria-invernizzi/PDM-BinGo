package com.application.bingo.util.scanner;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.application.bingo.ui.home.scan.ResultActivity;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

public class CustomImageAnalyzer implements ImageAnalysis.Analyzer {

    private final AppCustomScanActivity scanActivity;
    public CustomImageAnalyzer(AppCustomScanActivity scanActivity)
    {
        this.scanActivity = scanActivity;
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

                            if (value != null && !value.equalsIgnoreCase(scanActivity.getLastScanned())) {
                                scanActivity.setLastScanned(value);

                                scanActivity.startActivity(
                                        new Intent(scanActivity, ResultActivity.class).putExtra("barcode", value)
                                );

                                scanActivity.finish();

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
