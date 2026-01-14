package com.application.bingo.datasource.product;

import com.application.bingo.PrefsManager;
import com.application.bingo.database.AppDatabase;
import com.application.bingo.database.PackagingDao;
import com.application.bingo.database.ProductDao;
import com.application.bingo.model.Product;
import com.application.bingo.model.relation.ProductWithPackagings;
import com.application.bingo.model.dto.ProductDto;

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
            productCallback.onSuccessFromLocal(productDAO.findByBarcode(barcode));
        });
    }

    @Override
    public void getFavoriteProducts() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<ProductWithPackagings> favorites = productDAO.findAll();
            List<ProductDto> products = new ArrayList<>();

            for (ProductWithPackagings productWithPackagings : favorites) {
                Product productEntity = productWithPackagings.getProduct();
                ProductDto productDto = new ProductDto(productEntity);

                /*
                * List<PackagingDto> packagingDtos = new ArrayList<>();

                for (PackagingWithTranslations productWithTranslations : productWithPackagings.getPackagings()) {
                    Packaging packagingEntity = productWithTranslations.getPackaging();
                    PackagingDto packagingDto = new PackagingDto(packagingEntity);

                    List<MaterialDto> materialDtos = new ArrayList<>();

                    for (Material materialEntity : productWithTranslations.getMaterials()) {
                        materialDtos.add(new MaterialDto(materialEntity));
                    }

                    packagingDto.setTranslations(materialDtos);
                    packagingDtos.add(packagingDto);
                }

                productDto.setPackagings(packagingDtos);
                 * */
                products.add(productDto);
            }

            productCallback.onProductsFavoritesSuccessFromLocale(products);
        });
    }

    @Override
    public void insertProduct(ProductDto product) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            productDAO.insertProductDto(product);
        });
    }

    //@Override
    //public void insertProduct(ProductWithPackagings productWithPackagings) {
    //    AppDatabase.databaseWriteExecutor.execute(() -> {
    //        productDAO.insert(productWithPackagings.getProduct());
    //    });
    //}

    @Override
    public void removeFromFavorites(ProductDto product) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            productDAO.removeFromFavorites(product);
        });
    }
}
