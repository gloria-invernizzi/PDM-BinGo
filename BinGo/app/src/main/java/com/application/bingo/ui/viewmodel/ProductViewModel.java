package com.application.bingo.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.application.bingo.model.Product;
import com.application.bingo.model.Result;
import com.application.bingo.model.dto.ProductWithPackagingWithTranslation;
import com.application.bingo.repository.product.ProductRepository;

import java.util.List;

public class ProductViewModel extends ViewModel {
    private static final String TAG = ProductViewModel.class.getSimpleName();

    private final ProductRepository productRepository;
    private MutableLiveData<Result> productLiveData;
    private MutableLiveData<Result> favoriteProductsLiveData;
    private MutableLiveData<String> barcodeLiveData;

    public ProductViewModel(ProductRepository productRepository) {
        this.productRepository = productRepository;
        this.barcodeLiveData = new MutableLiveData<>("");
    }

    public MutableLiveData<Result> getProductLiveData(String barcode, long lastUpdate) {
        if (null == productLiveData) {
            fetchProduct(barcode, lastUpdate);
        }

        return productLiveData;
    }

    public MutableLiveData<Result> getFavoriteProductsLiveData() {
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

    public void updateBarcode(@NonNull String barcode) {
        if (!barcode.equals(barcodeLiveData.getValue())) {
            barcodeLiveData.postValue(barcode);

            fetchProduct(barcode, System.currentTimeMillis());
        }
    }

    public void fetchFavoritesProducts() {
        getFavoriteProducts();
    }

    public void addToFavorites(ProductWithPackagingWithTranslation product) {
        productRepository.addToFavorites(product);
    }

    public void removeFromFavorites(ProductWithPackagingWithTranslation product) {
        productRepository.removeFromFavorites(product);
    }

    public void updateProduct(Product product) {
        productRepository.updateProduct(product);
    }

    public void updateProduct(ProductWithPackagingWithTranslation product) {
        productRepository.updateProduct(product);
    }
}
