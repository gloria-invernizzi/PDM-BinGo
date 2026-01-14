package com.application.bingo.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.application.bingo.model.Material;

import java.util.List;

@Dao
public interface MaterialDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Material material);

    @Query("SELECT * FROM material WHERE packaging_material = :material")
    List<Material> findByPackageId(long material);
}
