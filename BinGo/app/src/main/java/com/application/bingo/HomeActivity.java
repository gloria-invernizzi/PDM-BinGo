package com.application.bingo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;


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
