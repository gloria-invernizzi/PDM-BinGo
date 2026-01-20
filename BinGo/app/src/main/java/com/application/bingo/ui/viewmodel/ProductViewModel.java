package com.application.bingo.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.application.bingo.model.Product;
import com.application.bingo.model.Result;
import com.application.bingo.model.dto.ProductWithPackagingWithTranslation;
import com.application.bingo.model.relation.ProductWithPackagings;
import com.application.bingo.repository.product.ProductRepository;

import java.util.ArrayList;
import java.util.List;

public class ProductViewModel extends ViewModel {

    private final ProductRepository productRepository;
    private MutableLiveData<Result> productLiveData;
    private MutableLiveData<Result> favoriteProductsLiveData;
    private MutableLiveData<String> barcodeLiveData = new MutableLiveData<>("");
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");

    // Osserva e combina i cambiamenti di più live data
    private final MediatorLiveData<Result> filteredFavorites = new MediatorLiveData<>();

    public ProductViewModel(ProductRepository productRepository) {
        this.productRepository = productRepository;

        favoriteProductsLiveData = productRepository.getFavoriteProducts();
        filteredFavorites.addSource(searchQuery, q -> performFilter());
        filteredFavorites.addSource(favoriteProductsLiveData, r -> performFilter());
    }

    public MutableLiveData<Result> getProductLiveData(String barcode) {
        if (null == productLiveData) {
            fetchProduct(barcode);
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

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public String getSearchQuery() {
        return searchQuery.getValue();
    }

    public MutableLiveData<Result> getFilteredFavorites() {
        return filteredFavorites;
    }

    private void fetchProduct(String barcode) {
        productLiveData = productRepository.getProduct(barcode, "all");
    }

    private void getFavoriteProducts() {
        favoriteProductsLiveData = productRepository.getFavoriteProducts();
    }

    public void updateBarcode(@NonNull String barcode) {
        if (!barcode.equals(barcodeLiveData.getValue())) {
            barcodeLiveData.postValue(barcode);

            fetchProduct(barcode);
        }
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

    private void performFilter() {
        Result result = favoriteProductsLiveData.getValue();
        String query = searchQuery.getValue();

        if (!result.isSuccess()) {
            filteredFavorites.setValue(result);

            return;
        }

        List<ProductWithPackagings> fullList = ((Result.Success<List<ProductWithPackagings>>) result).getData();

        if (query == null || query.isEmpty()) {
            filteredFavorites.setValue(result);
        } else {
            String lowerQuery = query.toLowerCase().trim();

            List<ProductWithPackagings> filtered = new ArrayList<>();

            for (ProductWithPackagings item : fullList) {
                if (item.getProduct().getName().toLowerCase().contains(lowerQuery) ||
                        item.getProduct().getBrand().toLowerCase().contains(lowerQuery) ||
                        item.getProduct().getBarcode().toLowerCase().contains(lowerQuery)) {
                    filtered.add(item);
                }
            }
            filteredFavorites.setValue(new Result.Success<>(filtered));
        }
    }
}
