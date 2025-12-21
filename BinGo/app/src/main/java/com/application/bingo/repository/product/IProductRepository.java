package com.application.bingo.repository.product;

import android.app.Application;

import com.application.bingo.model.Product;
import com.application.bingo.model.dto.ProductDto;
import com.application.bingo.util.ResponseCallback;

public interface IProductRepository {
    // return callback, product searched by barcode
    void fetchProduct(String barcode, String productType);

    void updateProduct(ProductDto product);

    // return callback, list of user favorite products
    void getFavoriteProducts();

    void removeFromFavorites(Product product);
}
