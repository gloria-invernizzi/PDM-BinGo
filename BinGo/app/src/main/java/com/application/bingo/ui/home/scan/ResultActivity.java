package com.application.bingo.ui.home.scan;

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
import com.application.bingo.R;
import com.application.bingo.model.ProductApiResponse;
import com.application.bingo.util.MaterialTranslator;
import com.application.bingo.util.normalizer.ProductDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.stream.Collectors;

public class ResultActivity extends AppCompatActivity {

    TextView productName, productBrand, productBarcode, recyclingInfo, textResult;
    ImageView productImage;
    String barcode;

    ProgressBar loadingSpinner;

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
                       String jsonString = response.toString();
                       Gson gson = new GsonBuilder()
                               .registerTypeAdapter(ProductApiResponse.class, new ProductDeserializer())
                               .create();

                        ProductApiResponse product = gson.fromJson(jsonString, ProductApiResponse.class);

                        MaterialTranslator materialTranslator = new MaterialTranslator();

                        productName.setText(product.getName());
                        productBrand.setText(product.getBrand());
                        recyclingInfo.setText(product.getPackagings().stream().map(packaging -> materialTranslator.translateMaterial(this, packaging.getMaterial())).collect(Collectors.joining(", ")));
                        textResult.setText(R.string.result_success);
                        loadingSpinner.setVisibility(View.GONE);

                        if (!product.getImageUrl().isEmpty()) {
                            Picasso.get().load(product.getImageUrl()).into(productImage);
                        }
                    } catch (Exception e) {
                        textResult.setText(R.string.parsing_error);
                        loadingSpinner.setVisibility(View.GONE);

                        Log.e("ResultActivity",e.getMessage()+" ");
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
