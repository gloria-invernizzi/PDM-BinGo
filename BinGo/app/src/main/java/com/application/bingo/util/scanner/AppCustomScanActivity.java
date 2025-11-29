package com.application.bingo.util.scanner;

import androidx.appcompat.app.AppCompatActivity;

public abstract class AppCustomScanActivity extends AppCompatActivity {
    public abstract String getLastScanned();
    public abstract AppCustomScanActivity setLastScanned(String last);
}
