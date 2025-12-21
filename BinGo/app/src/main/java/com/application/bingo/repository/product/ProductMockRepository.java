package com.application.bingo.repository.product;

import android.app.Application;

import com.application.bingo.database.ProductDao;
import com.application.bingo.model.Product;
import com.application.bingo.model.dto.ProductDto;
import com.application.bingo.service.ServiceLocator;
import com.application.bingo.util.ResponseCallback;

public class ProductMockRepository implements IProductRepository{
    private final Application application;
    private final ResponseCallback responseCallback;
    private final ProductDao productDao;

    public ProductMockRepository(Application application, ResponseCallback responseCallback) {
        this.application = application;
        this.responseCallback = responseCallback;
        this.productDao = ServiceLocator.getInstance().getAppDatabase(application).productDao();
    }

    @Override
    public void fetchProduct(String barcode, String productType) {
        // TODO
    }

    @Override
    public void updateProduct(ProductDto product) {
        // TODO
    }

    @Override
    public void getFavoriteProducts() {
        // TODO
    }

    @Override
    public void removeFromFavorites(Product product) {
        // TODO
    }
}
