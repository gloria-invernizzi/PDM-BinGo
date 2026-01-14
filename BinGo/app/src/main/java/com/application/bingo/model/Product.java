package com.application.bingo.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.application.bingo.model.dto.ProductDto;

@Entity(tableName = "product")
public class Product {
    @PrimaryKey
    @NonNull
    public String barcode = "";
    @ColumnInfo(name = "image_url")
    private String imageUrl = "";
    private String name = "";
    private String brand = "";
    @ColumnInfo(name = "non_recyclable_and_non_biodegradable")
    private boolean nonRecyclableAndNonBiodegradable;
    @ColumnInfo(name = "is_favorite")
    private boolean isFavorite; // TODO: think about, maybe we need relation n - n, https://developer.android.com/training/data-storage/room/relationships/many-to-many?hl=it

    public Product() {
    }

    public Product(ProductDto productDto) {
        this.barcode = productDto.getBarcode();
        this.imageUrl = productDto.getImageUrl();
        this.name = productDto.getName();
        this.brand = productDto.getBrand();
        this.nonRecyclableAndNonBiodegradable = productDto.getNonRecyclableAndNonBiodegradable();
        this.isFavorite = productDto.isFavorite();
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @NonNull
    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(@NonNull String barcode) {
        this.barcode = barcode;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public boolean getNonRecyclableAndNonBiodegradable() {
        return nonRecyclableAndNonBiodegradable;
    }

    public void setNonRecyclableAndNonBiodegradable(boolean nonRecyclableAndNonBiodegradable)
    {
        this.nonRecyclableAndNonBiodegradable = nonRecyclableAndNonBiodegradable;
    }
}
