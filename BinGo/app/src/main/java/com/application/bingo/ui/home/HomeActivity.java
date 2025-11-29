package com.application.bingo.ui.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.application.bingo.R;
import com.application.bingo.ui.home.scan.ManualEntryActivity;
import com.application.bingo.ui.home.scan.ScanActivity;


public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        CardView scanCard = findViewById(R.id.card_scan);
        CardView manualCard = findViewById(R.id.card_manual);

        scanCard.setOnClickListener(v -> startActivity(new Intent(this, ScanActivity.class)));
        manualCard.setOnClickListener(v -> startActivity(new Intent(this, ManualEntryActivity.class)));
    }
}
