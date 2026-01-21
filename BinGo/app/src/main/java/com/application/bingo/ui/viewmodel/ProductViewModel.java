package com.application.bingo.ui.viewmodel;

import androidx.annotation.NonNull;
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

/**
 * ViewModel for Product-related UI.
 * Handles product lookup, favorites management, and filtering of favorite products.
 */
public class ProductViewModel extends ViewModel {

    private final ProductRepository productRepository;
    private MutableLiveData<Result> productLiveData;
    private MutableLiveData<Result> favoriteProductsLiveData;
    private final MutableLiveData<String> barcodeLiveData = new MutableLiveData<>("");
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");

    /**
     * Observes and combines changes from multiple LiveData sources to filter favorites.
     */
    private final MediatorLiveData<Result> filteredFavorites = new MediatorLiveData<>();

    /**
     * Constructor for ProductViewModel.
     * Initializes favorite products and sets up the filtered favorites mediator.
     *
     * @param productRepository The repository for product-related data operations.
     */
    public ProductViewModel(ProductRepository productRepository) {
        this.productRepository = productRepository;

        favoriteProductsLiveData = productRepository.getFavoriteProducts();
        
        // Add sources to MediatorLiveData for combined updates
        filteredFavorites.addSource(searchQuery, q -> performFilter());
        filteredFavorites.addSource(favoriteProductsLiveData, r -> performFilter());
    }

    /**
     * Returns the LiveData for a specific product by its barcode.
     *
     * @param barcode The barcode of the product to fetch.
     * @return A MutableLiveData object containing the result of the product lookup.
     */
    public MutableLiveData<Result> getProductLiveData(String barcode, boolean isNetworkAvailable) {
        if (null == productLiveData) {
            fetchProduct(barcode, isNetworkAvailable);
        }
        return productLiveData;
    }

    /**
     * Returns the list of favorite products as LiveData.
     *
     * @return A MutableLiveData object containing the result of the favorites fetch.
     */
    public MutableLiveData<Result> getFavoriteProductsLiveData() {
        if (null == favoriteProductsLiveData) {
            getFavoriteProducts();
        }
        return favoriteProductsLiveData;
    }

    /**
     * Returns the current barcode LiveData.
     *
     * @return A MutableLiveData object containing the current barcode string.
     */
    public MutableLiveData<String> getBarcodeLiveData() {
        return barcodeLiveData;
    }

    /**
     * Sets the search query for filtering favorite products.
     *
     * @param query The search string used for filtering.
     */
    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    /**
     * Gets the current search query string.
     *
     * @return The current search query.
     */
    public String getSearchQuery() {
        return searchQuery.getValue();
    }

    /**
     * Returns the observable list of filtered favorite products.
     *
     * @return A MediatorLiveData object containing the filtered list of favorites.
     */
    public MutableLiveData<Result> getFilteredFavorites() {
        return filteredFavorites;
    }

    /**
     * Triggers a repository call to fetch product details by barcode.
     *
     * @param barcode The barcode of the product to fetch.
     */
    private void fetchProduct(String barcode, boolean isNetworkAvailable) {
        productLiveData = productRepository.getProduct(barcode, "all", isNetworkAvailable);
    }

    /**
     * Triggers a repository call to fetch the list of favorite products.
     */
    private void getFavoriteProducts() {
        favoriteProductsLiveData = productRepository.getFavoriteProducts();
    }

    /**
     * Updates the current barcode and triggers a fetch if it has changed.
     *
     * @param barcode The new barcode string.
     */
    public void updateBarcode(@NonNull String barcode, boolean isNetworkAvailable) {
        if (!barcode.equals(barcodeLiveData.getValue())) {
            barcodeLiveData.postValue(barcode);
            fetchProduct(barcode, isNetworkAvailable);
        }
    }

    /**
     * Adds a product to the user's favorites via the repository.
     *
     * @param product The product to be added.
     */
    public void addToFavorites(ProductWithPackagingWithTranslation product) {
        productRepository.addToFavorites(product);
    }

    /**
     * Removes a product from the user's favorites via the repository.
     *
     * @param product The product to be removed.
     */
    public void removeFromFavorites(ProductWithPackagingWithTranslation product) {
        productRepository.removeFromFavorites(product);
    }

    /**
     * Updates an existing product's information in the repository.
     *
     * @param product The product with updated information.
     */
    public void updateProduct(Product product) {
        productRepository.updateProduct(product);
    }

    /**
     * Combines search query and favorites list to produce a filtered result.
     * Filters by name, brand, or barcode based on the current search query.
     */
    private void performFilter() {
        Result result = favoriteProductsLiveData.getValue();
        String query = searchQuery.getValue();

        if (result == null || !result.isSuccess()) {
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
                // Check if name, brand, or barcode contains the search query
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
