package com.application.bingo.ui.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.application.bingo.model.Result;
import com.application.bingo.model.dto.ProductDto;
import com.application.bingo.repository.product.ProductRepository;

import java.util.List;

public class ProductViewModel extends ViewModel {
    private static final String TAG = ProductViewModel.class.getSimpleName();

    private final ProductRepository productRepository;
    private MutableLiveData<Result<ProductDto>> productLiveData;
    private MutableLiveData<Result<List<ProductDto>>> favoriteProductsLiveData;
    private MutableLiveData<String> barcodeLiveData;

    public ProductViewModel(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public MutableLiveData<Result<ProductDto>> getProductLiveData(String barcode, long lastUpdate) {
        if (null == productLiveData) {
            fetchProduct(barcode, lastUpdate);
        }

        return productLiveData;
    }

    public MutableLiveData<Result<List<ProductDto>>> getFavoriteProductsLiveData() {
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

    public void insertProduct(ProductDto product) {
        productRepository.insertProduct(product);
    }

    public void removeFromFavorites(ProductDto product) {
        productRepository.removeFromFavorites(product);
    }
}
