package com.application.bingo.datasource.product;

import androidx.annotation.NonNull;

import com.application.bingo.R;
import com.application.bingo.model.dto.ProductWithPackagingWithTranslation;
import com.application.bingo.service.ServiceLocator;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductApiDataSource extends BaseProductRemoteDataSource {
    @Override
    public void getProduct(String barcode, String productType) {
        Call<ProductWithPackagingWithTranslation> responseCall = ServiceLocator.getInstance()
                .getProductAPIService(application)
                .getProduct(barcode, productType)
        ;

        responseCall.enqueue(new Callback<ProductWithPackagingWithTranslation>() {
            @Override
            public void onResponse(@NonNull Call<ProductWithPackagingWithTranslation> call,
                                   @NonNull Response<ProductWithPackagingWithTranslation> response) {

                if (response.body() != null && response.isSuccessful()) {
                    productResponseCallback.onSuccessFromRemote(response.body(), new Date().getTime());
                } else {
                    productResponseCallback.onFailureFromRemote(new Exception(application.getString(R.string.request_error)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProductWithPackagingWithTranslation> call, @NonNull Throwable t) {
            }
        });
    }
}
