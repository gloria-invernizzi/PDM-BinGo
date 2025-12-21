package com.application.bingo.model.dto;

import androidx.room.ColumnInfo;

import java.util.ArrayList;
import java.util.List;

public class ProductDto {
    private String barcode = "";

    private String imageUrl = "";
    private String name = "";
    private String brand = "";

    private List<PackagingDto> packagings;

    @ColumnInfo(name = "non_recyclable_and_non_biodegradable")
    private boolean nonRecyclableAndNonBiodegradable;

    @ColumnInfo(name = "is_favorite")
    private boolean isFavorite;

    public ProductDto() {
        this.packagings = new ArrayList<>();
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<PackagingDto> getPackagings() {
        return packagings;
    }

    public void setPackagings(List<PackagingDto> packagings) {
        this.packagings = packagings;
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

    public void setNonRecyclableAndNonBiodegradable(Boolean nonRecyclableAndNonBiodegradable)
    {
        this.nonRecyclableAndNonBiodegradable = nonRecyclableAndNonBiodegradable;
    }
}
