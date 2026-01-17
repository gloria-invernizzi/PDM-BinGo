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
    private static final String TAG = ProductRepository.class.getSimpleName();

    private final MutableLiveData<Result> productMutableLiveData;
    private final MutableLiveData<Result> favoritesProductsMutableLiveData;

    private final BaseProductRemoteDataSource productRemoteDataSource;
    private final BaseProductLocalDataSource productLocalDataSource;
    private final Application application;

    public ProductRepository(Application application,
                             BaseProductRemoteDataSource productRemoteDataSource,
                             BaseProductLocalDataSource productLocalDataSource) {

        productMutableLiveData = new MutableLiveData<>();
        favoritesProductsMutableLiveData = new MutableLiveData<>();

        this.productRemoteDataSource = productRemoteDataSource;
        this.productLocalDataSource = productLocalDataSource;
        this.productRemoteDataSource.setProductCallback(this, application);
        this.productLocalDataSource.setProductCallback(this);
        this.application = application;
    }

    public MutableLiveData<Result> getProduct(String barcode, String productType) {
        if (!NetworkUtil.isInternetAvailable(application)) {
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

    public void updateProduct(ProductWithPackagingWithTranslation product) {
        productLocalDataSource.updateProduct(product);
    }


    @Override
    public void onSuccessFromRemote(ProductWithPackagingWithTranslation product, long lastUpdate) {
        Result.Success<ProductWithPackagingWithTranslation> result = new Result.Success<>(product);

        productMutableLiveData.postValue(result);
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
    public void onProductStatusChanged(ProductWithPackagingWithTranslation product, List<ProductWithPackagings> favorites) {
        //productMutableLiveData.postValue(new Result.Success<>(product));

        favoritesProductsMutableLiveData.postValue(new Result.Success<>(favorites));
    }
}
