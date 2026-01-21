package com.application.bingo.datasource.product;

import com.application.bingo.model.Product;
import com.application.bingo.model.dto.ProductWithPackagingWithTranslation;
import com.application.bingo.repository.product.ProductResponseCallback;

import java.util.function.Consumer;

public abstract class BaseProductLocalDataSource {
    protected ProductResponseCallback productCallback;

    public void setProductCallback(ProductResponseCallback productCallback) {
        this.productCallback = productCallback;
    }

    public abstract void getProduct(String barcode);

    public abstract void getFavoriteProducts();

    public abstract void addToFavorites(ProductWithPackagingWithTranslation product);

    public abstract void removeFromFavorites(ProductWithPackagingWithTranslation productDto);

    public abstract void updateProduct(Product product);

    public abstract void isProductFavorite(String barcode, Consumer<Boolean> callback);
}