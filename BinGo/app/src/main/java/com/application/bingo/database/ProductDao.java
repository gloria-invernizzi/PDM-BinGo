package com.application.bingo.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.application.bingo.model.Material;
import com.application.bingo.model.Packaging;
import com.application.bingo.model.Product;
import com.application.bingo.model.relation.ProductWithPackagings;
import com.application.bingo.model.dto.MaterialDto;
import com.application.bingo.model.dto.PackagingDto;
import com.application.bingo.model.dto.ProductDto;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class ProductDao {
    @Insert
    public abstract void insert(Product product);

    @Transaction
    @Query("SELECT * FROM product")
    public abstract List<ProductWithPackagings> findAll();

    @Transaction
    @Query("SELECT * FROM product WHERE barcode = :barcode")
    public abstract ProductWithPackagings findByBarcode(String barcode);

    @Insert
    abstract void insertPackaging(List<Packaging> packaging);

    @Insert
    abstract void insertTranslations(List<Material> translations);

    @Update
    abstract void update(Product product);

    @Update
    public void removeFromFavorites(ProductDto dto) {
        //TODO
    }

    @Transaction
    public void insertProductDto(ProductDto dto) {
        Product product = new Product(dto);
        insert(product);

        List<Packaging> packagings = new ArrayList<>();
        List<Material> translations = new ArrayList<>();

        for (PackagingDto packageDto : dto.getPackagings()) {
            Packaging packaging = new Packaging(packageDto, product.getBarcode());
            packagings.add(packaging);

            for (MaterialDto materialDto : packageDto.getTranslations()) {
                translations.add(new Material(materialDto, packaging.getMaterial()));
            }
        }

        insertPackaging(packagings);
        insertTranslations(translations);
    }
}

