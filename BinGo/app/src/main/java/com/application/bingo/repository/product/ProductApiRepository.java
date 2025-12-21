package com.application.bingo.repository.product;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.application.bingo.R;
import com.application.bingo.database.ProductDao;
import com.application.bingo.model.Product;
import com.application.bingo.model.dto.ProductDto;
import com.application.bingo.service.ServiceLocator;
import com.application.bingo.util.ResponseCallback;

import java.util.Date;

public class ProductApiRepository implements IProductRepository
{
    private final Application application;
    private final ResponseCallback responseCallback;
    private final ProductDao productDao;

    public ProductApiRepository(Application application, ResponseCallback responseCallback) {
        this.application = application;
        this.responseCallback = responseCallback;
        this.productDao = ServiceLocator.getInstance().getAppDatabase(application).productDao();
    }

    @Override
    public void fetchProduct(String barcode, String productType) {
        Call<ProductDto> responseCall = ServiceLocator.getInstance()
                .getProductAPIService(application)
                .getProduct(barcode, productType)
        ;

        responseCall.enqueue(new Callback<ProductDto>() {
            @Override
            public void onResponse(@NonNull Call<ProductDto> call,
                                   @NonNull Response<ProductDto> response) {

                Log.e("ProductApiRepository", response + " ");

                if (response.body() != null && response.isSuccessful()) {

                    Log.e("ProductApiRepository", response + " ");

                    responseCallback.onSuccess(response.body(), new Date().getTime());
                } else {
                    responseCallback.onFailure(application.getString(R.string.request_error));
                }
            }

            @Override
            public void onFailure(Call<ProductDto> call, Throwable t) {
            }
        });

    }

    @Override
    public void updateProduct(ProductDto product) {
        ServiceLocator.getInstance().getAppDatabase(application).databaseWriteExecutor.execute(() -> {
            productDao.insertProductDto(product);
        });
    }

    @Override
    public void getFavoriteProducts() {
        // TODO
    }

    @Override
    public void removeFromFavorites(Product product) {
        // TODO
    }
}
