package com.application.bingo.ui.home.scan;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.application.bingo.adapter.PackagingRecyclerAdapter;
import com.application.bingo.model.ProductApiResponse;
import com.application.bingo.util.MaterialParserUtils;
import com.application.bingo.util.normalizer.ProductDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

public class ResultFragment extends Fragment {

    TextView productName, productBrand, productBarcode, textResult, recyclingTitle;
    ImageView productImage;
    String barcode;
    RecyclerView packagingRecyclerView;
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
        textResult = view.findViewById(R.id.text_result);
        productImage = view.findViewById(R.id.product_image);
        favoriteCheckbox = view.findViewById(R.id.favorite_checkbox);
        recyclingTitle = view.findViewById(R.id.recycling_title);

        packagingRecyclerView = view.findViewById(R.id.packaging_material_container);
        packagingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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

                        MaterialParserUtils materialParser = new MaterialParserUtils(getContext(), product);
                        materialParser.hydratePackagings();

                        productName.setText(product.getName());
                        productBrand.setText(getString(R.string.brand,product.getBrand()));

                        loadingSpinner.setVisibility(View.GONE);

                        packagingRecyclerView.setAdapter(new PackagingRecyclerAdapter(product.getPackagings()));

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

                        Toast.makeText(requireContext(), R.string.result_success, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(requireContext(), R.string.request_error + e.getMessage(), Toast.LENGTH_LONG).show();

                        textResult.setText(R.string.parsing_error);
                        recyclingTitle.setText(" ");

                        loadingSpinner.setVisibility(View.GONE);
                        Picasso.get().load(R.drawable.product_not_found).into(productImage);

                        Log.e("ResultActivity",e.getMessage()+" ");
                    }
                },
                error -> {
                    Toast.makeText(requireContext(), R.string.request_error + error.getMessage(), Toast.LENGTH_LONG).show();

                    textResult.setText(R.string.result_failed);
                    recyclingTitle.setText(" ");

                    loadingSpinner.setVisibility(View.GONE);

                    Picasso.get().load(R.drawable.product_not_found).into(productImage);
                }
        );

        queue.add(request);
    }

}