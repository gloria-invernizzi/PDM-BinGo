package com.application.bingo.ui.home.scan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.application.bingo.R;

public class ManualEntryActivity extends AppCompatActivity {

    EditText barcodeInput;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manual_entry);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        barcodeInput = findViewById(R.id.barcode_input);
        submitButton = findViewById(R.id.submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String barcode = barcodeInput.getText().toString().trim();

                if (!TextUtils.isEmpty(barcode)) {
                    Intent intent = new Intent(ManualEntryActivity.this, ResultActivity.class);
                    intent.putExtra("barcode", barcode);
                    startActivity(intent);
                } else {
                    Toast.makeText(ManualEntryActivity.this, R.string.insert_valid_barcode, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
