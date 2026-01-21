package com.application.bingo.datasource.product;

import android.app.Application;

import com.application.bingo.repository.product.ProductResponseCallback;

public abstract class BaseProductRemoteDataSource {
    protected ProductResponseCallback productResponseCallback;

    public void setProductCallback(ProductResponseCallback productResponseCallback) {
        this.productResponseCallback = productResponseCallback;
    }

    public abstract void getProduct(String barcode, String productType);
}