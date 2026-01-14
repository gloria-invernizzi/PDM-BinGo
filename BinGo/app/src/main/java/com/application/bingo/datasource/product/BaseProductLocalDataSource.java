package com.application.bingo.datasource.product;

import com.application.bingo.model.dto.ProductDto;
import com.application.bingo.repository.product.ProductResponseCallback;

public abstract class BaseProductLocalDataSource {
    protected ProductResponseCallback productCallback;

    public void setProductCallback(ProductResponseCallback productCallback) {
        this.productCallback = productCallback;
    }

    public abstract void getProduct(String barcode);

    public abstract void getFavoriteProducts();

    public abstract void insertProduct(ProductDto product);

    public abstract void removeFromFavorites(ProductDto productDto);

    // public abstract void insertProduct(ProductWithPackagings product);
}
