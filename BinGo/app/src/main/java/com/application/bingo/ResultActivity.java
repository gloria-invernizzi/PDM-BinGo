package com.application.bingo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

public class ResultActivity extends AppCompatActivity {

    TextView productName, productBrand, productBarcode, recyclingInfo, textResult;
    ImageView productImage;
    String barcode;

    ProgressBar loadingSpinner;

    String translateMaterial(String raw) {
        switch (raw) {
            case "metal": return getString(R.string.material_metal);
            case "plastic":
            case "o-7-other-plastics":
                return getString(R.string.material_plastic);
            case "glass": return getString(R.string.material_glass);
            case "paper": return getString(R.string.material_paper);
            case "cardboard": return getString(R.string.material_cardboard);
            default: return raw;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        productName = findViewById(R.id.product_name);
        productBrand = findViewById(R.id.product_brand);
        productBarcode = findViewById(R.id.product_barcode);
        recyclingInfo = findViewById(R.id.recycling_info);
        textResult = findViewById(R.id.text_result);
        productImage = findViewById(R.id.product_image);

        barcode = getIntent().getStringExtra("barcode");
        productBarcode.setText(getString(R.string.barcode,barcode));

        loadingSpinner = findViewById(R.id.loading_spinner);
        loadingSpinner.setVisibility(View.VISIBLE);

        String apiUrl = "https://world.openfoodfacts.org/api/v2/product/" + barcode + ".json";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                apiUrl,
                null,
                response -> {
                    try {
                        JSONObject product = response.getJSONObject("product");

                        String name = product.optString("product_name", "Nome non disponibile");
                        String brand = product.optString("brands", "Marca non disponibile");
                        String imageUrl = product.optString("image_front_url", "");
                        String category = product.optString("categories", "Categoria non disponibile");

                        // Packaging info
                        StringBuilder packagingInfo = new StringBuilder();
                        packagingInfo.append(getString(R.string.material)).append("\n");
                        JSONObject ecoscoreData = product.getJSONObject("ecoscore_data");
                        JSONObject adjustments = ecoscoreData.getJSONObject("adjustments");
                        JSONObject packaging = adjustments.getJSONObject("packaging");
                        JSONArray packagings = packaging.getJSONArray("packagings");

                        for (int i = 0; i < packagings.length(); i++) {
                            JSONObject item = packagings.getJSONObject(i);
                            String rawMaterial = item.optString("material", "").replace("en:", "");
                            String material = translateMaterial(rawMaterial);
                            packagingInfo.append(material).append("\n");
                        }
                        if (packagingInfo.toString().isEmpty()){
                            packagingInfo.append(getString(R.string.no_packaging_info));
                        }

                        productName.setText(name);
                        productBrand.setText(getString(R.string.brand, brand));
                        recyclingInfo.setText(packagingInfo.toString());
                        textResult.setText(R.string.result_success);
                        loadingSpinner.setVisibility(View.GONE);

                        if (!imageUrl.isEmpty()) {
                            Picasso.get().load(imageUrl).into(productImage);
                        }

                    } catch (Exception e) {
                        textResult.setText(R.string.parsing_error);
                        loadingSpinner.setVisibility(View.GONE);
                    }
                },
                error -> {
                    Toast.makeText(this, R.string.request_error + error.getMessage(), Toast.LENGTH_LONG).show();
                    textResult.setText(R.string.result_failed);
                    loadingSpinner.setVisibility(View.GONE);
                }
        );

        queue.add(request);
    }
}
