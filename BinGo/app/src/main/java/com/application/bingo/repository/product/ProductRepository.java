package com.application.bingo.repository.product;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.application.bingo.datasource.product.BaseProductLocalDataSource;
import com.application.bingo.datasource.product.BaseProductRemoteDataSource;
import com.application.bingo.model.Product;
import com.application.bingo.model.dto.ProductWithPackagingWithTranslation;
import com.application.bingo.model.relation.ProductWithPackagings;
import com.application.bingo.model.Result;
import com.application.bingo.util.NetworkUtil;

import java.util.List;

public class ProductRepository implements ProductResponseCallback {
    private final MutableLiveData<Result> productMutableLiveData;
    private final MutableLiveData<Result> favoritesProductsMutableLiveData;

    private final BaseProductRemoteDataSource productRemoteDataSource;
    private final BaseProductLocalDataSource productLocalDataSource;

    public ProductRepository(BaseProductRemoteDataSource productRemoteDataSource,
                             BaseProductLocalDataSource productLocalDataSource) {

        productMutableLiveData = new MutableLiveData<>();
        favoritesProductsMutableLiveData = new MutableLiveData<>();

        this.productRemoteDataSource = productRemoteDataSource;
        this.productLocalDataSource = productLocalDataSource;
        this.productRemoteDataSource.setProductCallback(this);
        this.productLocalDataSource.setProductCallback(this);
    }

    public MutableLiveData<Result> getProduct(String barcode, String productType, boolean isInternetAvailable) {
        if (!isInternetAvailable) {
            productLocalDataSource.getProduct(barcode);
        } else {
            productRemoteDataSource.getProduct(barcode, productType);
        }

        return productMutableLiveData;
    }

    public MutableLiveData<Result> getFavoriteProducts() {
        productLocalDataSource.getFavoriteProducts();

        return favoritesProductsMutableLiveData;
    }

    public void addToFavorites(ProductWithPackagingWithTranslation product) {
        productLocalDataSource.addToFavorites(product);
    }

    public void removeFromFavorites(ProductWithPackagingWithTranslation product) {
        productLocalDataSource.removeFromFavorites(product);
    }

    public void updateProduct(Product product) {
        productLocalDataSource.updateProduct(product);
    }

    @Override
    public void onSuccessFromRemote(ProductWithPackagingWithTranslation product, long lastUpdate) {
        productLocalDataSource.isProductFavorite(
                product.getProduct().getBarcode(),
                isFavorite -> {

                    if (Boolean.TRUE.equals(isFavorite)) {
                        product.getProduct().setFavorite(true);
                    }

                    productMutableLiveData.postValue(new Result.Success<>(product));
                }
        );
    }

    @Override
    public void onFailureFromRemote(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());

        productMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromLocal(ProductWithPackagingWithTranslation product) {
        Result.Success<ProductWithPackagingWithTranslation> result = new Result.Success<>(product);

        productMutableLiveData.postValue(result);
    }

    @Override
    public void onFailureFromLocal(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());

        productMutableLiveData.postValue(result);
    }

    @Override
    public void onProductsFavoritesSuccessFromLocale(List<ProductWithPackagings> favorites) {
        Result.Success<List<ProductWithPackagings>> result = new Result.Success<>(favorites);

        favoritesProductsMutableLiveData.postValue(result);
    }

    @Override
    public void onProductStatusChanged(List<ProductWithPackagings> favorites) {
        favoritesProductsMutableLiveData.postValue(new Result.Success<>(favorites));
    }
}
