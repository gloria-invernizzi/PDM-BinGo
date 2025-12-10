package com.application.bingo.model;

import java.util.ArrayList;
import java.util.List;

public class ProductApiResponse {
    private String barcode;
    private String imageUrl = "";
    private String name = "";
    private String brand = "";
    private List<PackagingDto> packagings;
    private boolean nonRecyclableAndNonBiodegradable;
    private boolean isFavorite;
    public ProductApiResponse() {
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

    public ProductApiResponse setName(String name) {
        this.name = name;

        return this;
    }

    public List<PackagingDto> getPackagings() {
        return packagings;
    }

    public ProductApiResponse setPackagings(List<PackagingDto> packagings) {
        this.packagings = packagings;

        return this;
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

    public ProductApiResponse setFavorite(boolean favorite) {
        isFavorite = favorite;

        return this;
    }

    public boolean getNonRecyclableAndNonBiodegradable() {
        return nonRecyclableAndNonBiodegradable;
    }

    public ProductApiResponse setNonRecyclableAndNonBiodegradable(Boolean nonRecyclableAndNonBiodegradable)
    {
        this.nonRecyclableAndNonBiodegradable = nonRecyclableAndNonBiodegradable;

        return this;
    }
}
