package com.application.bingo.datasource.product;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.application.bingo.R;
import com.application.bingo.model.dto.ProductWithPackagingWithTranslation;
import com.application.bingo.model.relation.ProductWithPackagings;
import com.application.bingo.service.ServiceLocator;
import com.application.bingo.util.normalizer.ProductDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductMockDataSource extends BaseProductRemoteDataSource {
    private Context context;
    private final String MOCK_EFF_PRODUCT_FILENAME = "open_food_facts_product_8053259800282.json";
    public ProductMockDataSource (Context context) {
        this.context = context;
    }

    @Override
    public void getProduct(String barcode, String productType) {
        ProductWithPackagingWithTranslation productWithPackagingWithTranslation = null;

        try {
            InputStream inputStream = context.getAssets().open(MOCK_EFF_PRODUCT_FILENAME);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(ProductWithPackagingWithTranslation.class, new ProductDeserializer(context))
                    .create()
                    ;

            productWithPackagingWithTranslation = gson.fromJson(bufferedReader, ProductWithPackagingWithTranslation.class);
        } catch (IOException e) {
            Log.e("ProductMockDataSource", e.getMessage() + " ");
        }

        if (null != productWithPackagingWithTranslation) {
            productResponseCallback.onSuccessFromRemote(productWithPackagingWithTranslation, System.currentTimeMillis());
        } else {
            productResponseCallback.onFailureFromRemote(new Exception("Mock error"));
        }

    }
}
