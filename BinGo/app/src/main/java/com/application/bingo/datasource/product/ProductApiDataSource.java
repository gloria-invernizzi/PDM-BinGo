package com.application.bingo.datasource.product;

import androidx.annotation.NonNull;

import com.application.bingo.R;
import com.application.bingo.model.dto.ProductWithPackagingWithTranslation;
import com.application.bingo.service.ServiceLocator;
import com.application.bingo.util.MaterialParserUtils;
import com.application.bingo.util.normalizer.ProductDeserializer;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductApiDataSource extends BaseProductRemoteDataSource {
    private ProductDeserializer deserializer;
    public ProductApiDataSource (ProductDeserializer deserializer) {
        this.deserializer = deserializer;
    }

    @Override
    public void getProduct(String barcode, String productType) {
        Call<ProductWithPackagingWithTranslation> responseCall = ServiceLocator.getInstance()
                .getProductAPIService(deserializer)
                .getProduct(barcode, productType)
        ;

        responseCall.enqueue(new Callback<ProductWithPackagingWithTranslation>() {
            @Override
            public void onResponse(@NonNull Call<ProductWithPackagingWithTranslation> call,
                                   @NonNull Response<ProductWithPackagingWithTranslation> response) {

                if (response.body() != null && response.isSuccessful()) {
                    productResponseCallback.onSuccessFromRemote(response.body(), new Date().getTime());
                } else {
                    productResponseCallback.onFailureFromRemote(new Exception("Api error"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProductWithPackagingWithTranslation> call, @NonNull Throwable t) {
            }
        });
    }
}
