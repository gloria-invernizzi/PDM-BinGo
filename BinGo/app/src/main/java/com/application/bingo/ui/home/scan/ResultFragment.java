package com.application.bingo.ui.home.scan;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.stream.Collectors;

public class ResultFragment extends Fragment {

    TextView productName, productBrand, productBarcode, recyclingInfo, textResult;
    ImageView productImage;
    String barcode;
    ProgressBar loadingSpinner;

    CheckBox favoriteCheckbox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productName = view.findViewById(R.id.product_name);
        productBrand = view.findViewById(R.id.product_brand);
        productBarcode = view.findViewById(R.id.product_barcode);
        recyclingInfo = view.findViewById(R.id.recycling_info);
        textResult = view.findViewById(R.id.text_result);
        productImage = view.findViewById(R.id.product_image);
        favoriteCheckbox = view.findViewById(R.id.favorite_checkbox);

        barcode = getArguments().getString("barcode");
        productBarcode.setText(getString(R.string.barcode,barcode));

        loadingSpinner = view.findViewById(R.id.loading_spinner);
        loadingSpinner.setVisibility(View.VISIBLE);

        String apiUrl = "https://world.openfoodfacts.org/api/v2/product/" + barcode + ".json";

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                apiUrl,
                null,
                response -> {
                    try {
                       String jsonString = response.toString();
                       Gson gson = new GsonBuilder()
                               .registerTypeAdapter(ProductApiResponse.class, new ProductDeserializer())
                               .create()
                       ;

                        ProductApiResponse product = gson.fromJson(jsonString, ProductApiResponse.class);

                        MaterialTranslator materialTranslator = new MaterialTranslator();

                        productName.setText(product.getName());
                        productBrand.setText(product.getBrand());

                        recyclingInfo.setText(product.getPackagings().stream().map(packaging -> materialTranslator.translateMaterial(requireContext(), packaging.getMaterial())).collect(Collectors.joining(", ")));
                        textResult.setText(R.string.result_success);

                        loadingSpinner.setVisibility(View.GONE);

                        if (!product.getImageUrl().isEmpty()) {
                            Picasso.get().load(product.getImageUrl()).into(productImage);
                        }

                        favoriteCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    // cuore selezionato
                                } else {
                                    // cuore deselezionato
                                }
                            }
                        });


                    } catch (Exception e) {
                        textResult.setText(R.string.parsing_error);
                        loadingSpinner.setVisibility(View.GONE);
                        Log.e("ResultActivity",e.getMessage()+" ");
                    }
                },
                error -> {
                    Toast.makeText(requireContext(), R.string.request_error + error.getMessage(), Toast.LENGTH_LONG).show();
                    textResult.setText(R.string.result_failed);
                    loadingSpinner.setVisibility(View.GONE);
                }
        );

        queue.add(request);
    }

}