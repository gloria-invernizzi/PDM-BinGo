package com.application.bingo.database;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.application.bingo.model.Material;
import com.application.bingo.model.Packaging;
import com.application.bingo.model.Product;
import com.application.bingo.model.dto.ProductWithPackagingWithTranslation;
import com.application.bingo.model.relation.PackagingWithTranslations;
import com.application.bingo.model.relation.ProductWithPackagings;

import java.util.List;

@Dao
public abstract class ProductDao {
    @Transaction
    @Query("SELECT * FROM product")
    public abstract List<ProductWithPackagings> findAll();
    @Transaction
    @Query("SELECT * FROM product WHERE is_favorite = 1")
    public abstract List<ProductWithPackagings> findFavorites();

    // Select
    @Transaction
    @Query("SELECT * FROM product WHERE barcode = :barcode")
    public abstract ProductWithPackagings findProductWithPackagingsByBarcode(String barcode);
    @Transaction
    @Query("SELECT * FROM packaging WHERE product_barcode = :barcode")
    public abstract List<PackagingWithTranslations> findPackagingWithTranslationByBarcode(String barcode);
    public ProductWithPackagingWithTranslation findProduct(String barcode) {
        ProductWithPackagings localProduct = findProductWithPackagingsByBarcode(barcode);

        if (null == localProduct) {
            return null;
        }

        ProductWithPackagingWithTranslation productWithPackagingWithTranslation = new ProductWithPackagingWithTranslation();

        productWithPackagingWithTranslation.setProduct(localProduct.getProduct());
        productWithPackagingWithTranslation.setPackagings(findPackagingWithTranslationByBarcode(barcode));

        return productWithPackagingWithTranslation;
    }

    // Insert
    @Insert
    public abstract void insert(Product product);
    @Insert
    abstract void insertPackaging(Packaging packaging);
    @Insert
    abstract void insertTranslations(List<Material> translations);
    public void insertProductWithPackagingsWithTranslations(@NonNull ProductWithPackagingWithTranslation productWithPackagingWithTranslation) {
        insert(productWithPackagingWithTranslation.getProduct());

        for (PackagingWithTranslations packaging:
                productWithPackagingWithTranslation.getPackagings()) {

            insertPackaging(packaging.getPackaging());
            insertTranslations(packaging.getTranslations());
        }
    }

    // Update
    @Update
    public abstract void updateProduct(Product product);
    @Update
    public abstract void updatePackaging(Packaging packagings);
    @Update
    public abstract void updateTranslations(List<Material> translations);
    public void updateProductWithPackagingsWithTranslations(@NonNull ProductWithPackagingWithTranslation productWithPackagingWithTranslation) {
        updateProduct(productWithPackagingWithTranslation.getProduct());

        for (PackagingWithTranslations packaging:
                productWithPackagingWithTranslation.getPackagings()) {

            updatePackaging(packaging.getPackaging());
            updateTranslations(packaging.getTranslations());
        }
    }
}

