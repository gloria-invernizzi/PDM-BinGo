package com.application.bingo.ui.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.application.bingo.model.Result;
import com.application.bingo.model.dto.ProductWithPackagingWithTranslation;
import com.application.bingo.model.relation.ProductWithPackagings;
import com.application.bingo.repository.product.ProductRepository;

import java.util.List;

public class ProductViewModel extends ViewModel {
    private static final String TAG = ProductViewModel.class.getSimpleName();

    private final ProductRepository productRepository;
    private MutableLiveData<Result<ProductWithPackagingWithTranslation>> productLiveData;
    private MutableLiveData<Result<List<ProductWithPackagings>>> favoriteProductsLiveData;
    private MutableLiveData<String> barcodeLiveData;

    public ProductViewModel(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public MutableLiveData<Result<ProductWithPackagingWithTranslation>> getProductLiveData(String barcode, long lastUpdate) {
        if (null == productLiveData) {
            fetchProduct(barcode, lastUpdate);
        }

        return productLiveData;
    }

    public MutableLiveData<Result<List<ProductWithPackagings>>> getFavoriteProductsLiveData() {
        if (null == favoriteProductsLiveData) {
            getFavoriteProducts();
        }
        return favoriteProductsLiveData;
    }

    public MutableLiveData<String> getBarcodeLiveData() {
        return barcodeLiveData;
    }

    private void fetchProduct(String barcode, long lastUpdate) {
        productLiveData = productRepository.getProduct(barcode, "all");
    }

    private void getFavoriteProducts() {
        favoriteProductsLiveData = productRepository.getFavoriteProducts();
    }

    public void updateBarcode(String barcode) {
        if (null == barcodeLiveData) {
            barcodeLiveData = new MutableLiveData<>();

            barcodeLiveData.postValue(barcode);
        } else if (!barcode.equals(barcodeLiveData.getValue())) {
            barcodeLiveData.postValue(barcode);

            fetchProduct(barcode, System.currentTimeMillis());
        }
    }

    public void fetchFavoritesProducts() {
        getFavoriteProducts();
    }

    public void insertProduct(ProductWithPackagingWithTranslation product) {
        productRepository.insertProduct(product);
    }

    public void removeFromFavorites(ProductWithPackagingWithTranslation product) {
        productRepository.removeFromFavorites(product);
    }
}
