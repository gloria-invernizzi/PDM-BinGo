package com.application.bingo.datasource.product;

import android.app.Application;

import com.application.bingo.repository.product.ProductResponseCallback;

public abstract class BaseProductRemoteDataSource {
    protected ProductResponseCallback productResponseCallback;
    protected Application application;

    public void setProductCallback(ProductResponseCallback productResponseCallback, Application application) {
        this.productResponseCallback = productResponseCallback;
        this.application = application;
    }

    public abstract void getProduct(String barcode, String productType);
}