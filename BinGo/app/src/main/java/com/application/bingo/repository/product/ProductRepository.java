package com.application.bingo.repository.product;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.application.bingo.datasource.product.BaseProductLocalDataSource;
import com.application.bingo.datasource.product.BaseProductRemoteDataSource;
import com.application.bingo.model.relation.ProductWithPackagings;
import com.application.bingo.model.Result;
import com.application.bingo.model.dto.ProductDto;

import java.util.List;

public class ProductRepository implements ProductResponseCallback {
    private static final String TAG = ProductRepository.class.getSimpleName();

    private final MutableLiveData<Result<ProductDto>> productMutableLiveData;
    private final MutableLiveData<Result<List<ProductDto>>> favoritesProductsMutableLiveData;

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

    public MutableLiveData<Result<ProductDto>> getProduct(String barcode, String productType) {
        // TODO maybe fetch from DB if the product was saved, maybe use a timestamp limit
        productRemoteDataSource.getProduct(barcode, productType);

        return productMutableLiveData;
    }

    public MutableLiveData<Result<List<ProductDto>>> getFavoriteProducts() {
        productLocalDataSource.getFavoriteProducts();

        return favoritesProductsMutableLiveData;
    }

    public void insertProduct(ProductDto product) {
        productLocalDataSource.insertProduct(product);
    }

    public void removeFromFavorites(ProductDto product) {
        productLocalDataSource.removeFromFavorites(product);
    }

    // TODO aggiornare i livedata durante le callback
    @Override
    public void onSuccessFromRemote(ProductDto product, long lastUpdate) {
        Result.Success<ProductDto> result = new Result.Success<>(product);

        productMutableLiveData.postValue(result);
    }

    @Override
    public void onFailureFromRemote(Exception exception) {

    }

    @Override
    public void onSuccessFromLocal(ProductWithPackagings product) {

    }

    @Override
    public void onFailureFromLocal(Exception exception) {

    }

    @Override
    public void onProductsFavoritesSuccessFromLocale(List<ProductDto> favorites) {
        Result.Success<List<ProductDto>> result = new Result.Success<List<ProductDto>>(favorites);

        favoritesProductsMutableLiveData.postValue(result);
    }
}
