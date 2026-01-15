package com.application.bingo.repository.product;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.application.bingo.datasource.product.BaseProductLocalDataSource;
import com.application.bingo.datasource.product.BaseProductRemoteDataSource;
import com.application.bingo.model.dto.ProductWithPackagingWithTranslation;
import com.application.bingo.model.relation.ProductWithPackagings;
import com.application.bingo.model.Result;

import java.util.List;

public class ProductRepository implements ProductResponseCallback {
    private static final String TAG = ProductRepository.class.getSimpleName();

    private final MutableLiveData<Result<ProductWithPackagingWithTranslation>> productMutableLiveData;
    private final MutableLiveData<Result<List<ProductWithPackagings>>> favoritesProductsMutableLiveData;

    private final BaseProductRemoteDataSource productRemoteDataSource;
    private final BaseProductLocalDataSource productLocalDataSource;

    public ProductRepository(Application application,
                             BaseProductRemoteDataSource productRemoteDataSource,
                             BaseProductLocalDataSource productLocalDataSource) {

        productMutableLiveData = new MutableLiveData<>();
        favoritesProductsMutableLiveData = new MutableLiveData<>();

        this.productRemoteDataSource = productRemoteDataSource;
        this.productLocalDataSource = productLocalDataSource;
        this.productRemoteDataSource.setProductCallback(this, application);
        this.productLocalDataSource.setProductCallback(this);
    }

    public MutableLiveData<Result<ProductWithPackagingWithTranslation>> getProduct(String barcode, String productType) {
        // TODO maybe fetch from DB if the product was saved, maybe use a timestamp limit
        productRemoteDataSource.getProduct(barcode, productType);

        return productMutableLiveData;
    }

    public MutableLiveData<Result<List<ProductWithPackagings>>> getFavoriteProducts() {
        productLocalDataSource.getFavoriteProducts();

        return favoritesProductsMutableLiveData;
    }

    public void insertProduct(ProductWithPackagingWithTranslation product) {
        productLocalDataSource.insertProduct(product);
    }

    public void removeFromFavorites(ProductWithPackagingWithTranslation product) {
        productLocalDataSource.removeFromFavorites(product);
    }

    // TODO aggiornare i livedata durante le callback
    @Override
    public void onSuccessFromRemote(ProductWithPackagingWithTranslation product, long lastUpdate) {
        Result.Success<ProductWithPackagingWithTranslation> result = new Result.Success<>(product);

        productMutableLiveData.postValue(result);
    }

    @Override
    public void onFailureFromRemote(Exception exception) {

    }

    @Override
    public void onSuccessFromLocal(ProductWithPackagingWithTranslation product) {

    }

    @Override
    public void onFailureFromLocal(Exception exception) {

    }

    @Override
    public void onProductsFavoritesSuccessFromLocale(List<ProductWithPackagings> favorites) {
        Result.Success<List<ProductWithPackagings>> result = new Result.Success<>(favorites);

        favoritesProductsMutableLiveData.postValue(result);
    }
}
