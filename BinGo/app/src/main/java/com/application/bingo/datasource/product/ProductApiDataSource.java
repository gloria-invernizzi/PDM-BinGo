package com.application.bingo.datasource.product;

import androidx.annotation.NonNull;

import com.application.bingo.R;
import com.application.bingo.model.dto.ProductDto;
import com.application.bingo.service.ServiceLocator;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductApiDataSource extends BaseProductRemoteDataSource {
    @Override
    public void getProduct(String barcode, String productType) {
        Call<ProductDto> responseCall = ServiceLocator.getInstance()
                .getProductAPIService(application)
                .getProduct(barcode, productType)
        ;

        responseCall.enqueue(new Callback<ProductDto>() {
            @Override
            public void onResponse(@NonNull Call<ProductDto> call,
                                   @NonNull Response<ProductDto> response) {

                if (response.body() != null && response.isSuccessful()) {
                    productResponseCallback.onSuccessFromRemote(response.body(), new Date().getTime());
                } else {
                    productResponseCallback.onFailureFromRemote(new Exception(application.getString(R.string.request_error)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProductDto> call, @NonNull Throwable t) {
            }
        });
    }
}
