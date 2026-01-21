package com.application.bingo.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.application.bingo.model.Packaging;
import com.application.bingo.model.relation.PackagingWithTranslations;

import java.util.List;

@Dao
public interface PackagingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Packaging packaging);

    @Query("SELECT * FROM packaging WHERE product_barcode = :barcode")
    List<Packaging> findByProductId(String barcode);

    @Transaction
    @Query("SELECT * FROM packaging WHERE product_barcode = :barcode")
    List<PackagingWithTranslations> findAll(String barcode);

    @Transaction
    @Query("SELECT * FROM packaging WHERE material = :material")
    PackagingWithTranslations findByMaterial(String material);
}
