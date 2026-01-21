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

/**
 * Data Access Object (DAO) for Product-related entities.
 * Manages complex relationships between Products, Packagings, and Material translations.
 */
@Dao
public abstract class ProductDao {

    /**
     * Retrieves all products along with their associated packagings.
     */
    @Transaction
    @Query("SELECT * FROM product")
    public abstract List<ProductWithPackagings> findAll();

    /**
     * Retrieves all products marked as favorites.
     */
    @Transaction
    @Query("SELECT * FROM product WHERE is_favorite = 1")
    public abstract List<ProductWithPackagings> findFavorites();

    /**
     * Retrieves a single product by its barcode.
     */
    @Transaction
    @Query("SELECT * FROM product WHERE barcode = :barcode")
    public abstract ProductWithPackagings findProductWithPackagingsByBarcode(String barcode);

    /**
     * Retrieves packagings and their material translations for a specific product barcode.
     */
    @Transaction
    @Query("SELECT * FROM packaging WHERE product_barcode = :barcode")
    public abstract List<PackagingWithTranslations> findPackagingWithTranslationByBarcode(String barcode);

    /**
     * Helper method to reconstruct a full ProductWithPackagingWithTranslation object from the database.
     */
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

    @Query("SELECT is_favorite FROM product WHERE barcode = :barcode LIMIT 1")
    public abstract Boolean isProductFavorite(String barcode);

    // --- Insert Methods ---

    @Insert
    public abstract void insert(Product product);

    @Insert
    abstract void insertPackaging(Packaging packaging);

    @Insert
    abstract void insertTranslations(List<Material> translations);

    /**
     * Inserts a full product hierarchy into the database within a transaction.
     */
    @Transaction
    public void insertProductWithPackagingsWithTranslations(@NonNull ProductWithPackagingWithTranslation productWithPackagingWithTranslation) {
        insert(productWithPackagingWithTranslation.getProduct());

        for (PackagingWithTranslations packaging:
                productWithPackagingWithTranslation.getPackagings()) {

            insertPackaging(packaging.getPackaging());
            insertTranslations(packaging.getTranslations());
        }
    }

    // --- Update Methods ---

    @Update
    public abstract void updateProduct(Product product);

    @Update
    public abstract void updatePackaging(Packaging packagings);

    @Update
    public abstract void updateTranslations(List<Material> translations);

    /**
     * Updates a full product hierarchy in the database within a transaction.
     */
    @Transaction
    public void updateProductWithPackagingsWithTranslations(@NonNull ProductWithPackagingWithTranslation productWithPackagingWithTranslation) {
        updateProduct(productWithPackagingWithTranslation.getProduct());

        for (PackagingWithTranslations packaging:
                productWithPackagingWithTranslation.getPackagings()) {

            updatePackaging(packaging.getPackaging());
            updateTranslations(packaging.getTranslations());
        }
    }
}
