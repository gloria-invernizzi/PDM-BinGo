package com.application.bingo.model;

import java.util.List;

public class ProductApiResponse {
    private String barcode;
    private String imageUrl;
    private String name;
    private String brand;
    private List<Packaging> packagings;
    private boolean isFavorite;

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

    public List<Packaging> getPackagings() {
        return packagings;
    }

    public void setPackagings(List<Packaging> packagings) {
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

}
