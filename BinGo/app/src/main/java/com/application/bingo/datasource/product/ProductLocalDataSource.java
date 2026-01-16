package com.application.bingo.datasource.product;

import com.application.bingo.PrefsManager;
import com.application.bingo.database.AppDatabase;
import com.application.bingo.database.PackagingDao;
import com.application.bingo.database.ProductDao;
import com.application.bingo.model.Product;
import com.application.bingo.model.dto.ProductWithPackagingWithTranslation;
import com.application.bingo.model.relation.ProductWithPackagings;

import java.util.ArrayList;
import java.util.List;

public class ProductLocalDataSource extends BaseProductLocalDataSource {
    private final ProductDao productDAO;
    private final PackagingDao packagingDAO;
    private final PrefsManager prefsManager;

    public ProductLocalDataSource(AppDatabase appDatabase,
                                  PrefsManager prefsManager) {
        this.productDAO = appDatabase.productDao();
        this.packagingDAO = appDatabase.packagingDao();

        this.prefsManager = prefsManager;
    }

    @Override
    public void getProduct(String barcode) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            if (null == productDAO.findProduct(barcode)) {
                productCallback.onFailureFromLocal(new Exception("Il prodotto non esiste nel database locale"));
            } else {
                productCallback.onSuccessFromLocal(productDAO.findProduct(barcode));
            }
        });
    }

    @Override
    public void getFavoriteProducts() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            productCallback.onProductsFavoritesSuccessFromLocale(productDAO.findFavorites());
        });
    }

    @Override
    public void addToFavorites(ProductWithPackagingWithTranslation product) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            if (null == productDAO.findProduct(product.getProduct().getBarcode())) {
                productDAO.insertProductWithPackagingsWithTranslations(product);
            } else {
                productDAO.updateProductWithPackagingsWithTranslations(product);
            }

            productCallback.onProductStatusChanged(product, productDAO.findFavorites());
        });
    }

    @Override
    public void removeFromFavorites(ProductWithPackagingWithTranslation product) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            productDAO.updateProductWithPackagingsWithTranslations(product);

            productCallback.onProductStatusChanged(product, productDAO.findFavorites());
        });
    }

    @Override
    public void updateProduct(Product product) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            productDAO.updateProduct(product);

            ProductWithPackagingWithTranslation productWithPackagingWithTranslation = productDAO.findProduct(product.getBarcode());

            productCallback.onProductStatusChanged(productWithPackagingWithTranslation, productDAO.findFavorites());
        });
    }

    @Override
    public void updateProduct(ProductWithPackagingWithTranslation product) {
    }
}
