package com.application.bingo.datasource.product;

import com.application.bingo.PrefsManager;
import com.application.bingo.database.AppDatabase;
import com.application.bingo.database.PackagingDao;
import com.application.bingo.database.ProductDao;
import com.application.bingo.model.Product;
import com.application.bingo.model.dto.ProductWithPackagingWithTranslation;
import com.application.bingo.model.relation.ProductWithPackagings;
import com.application.bingo.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ProductLocalDataSource extends BaseProductLocalDataSource {
    private final ProductDao productDAO;

    public ProductLocalDataSource(AppDatabase appDatabase) {
        this.productDAO = appDatabase.productDao();
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

            productCallback.onProductStatusChanged(productDAO.findFavorites());
        });
    }

    @Override
    public void removeFromFavorites(ProductWithPackagingWithTranslation product) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            productDAO.updateProductWithPackagingsWithTranslations(product);

            productCallback.onProductStatusChanged(productDAO.findFavorites());
        });
    }

    @Override
    public void updateProduct(Product product) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            productDAO.updateProduct(product);

            productCallback.onProductStatusChanged(productDAO.findFavorites());
        });
    }

    @Override
    public void isProductFavorite(String barcode,
                                  Consumer<Boolean> callback) {

        AppDatabase.databaseWriteExecutor.execute(() -> {
            Boolean isFavorite = productDAO.isProductFavorite(barcode);
            callback.accept(isFavorite);
        });
    }
}
