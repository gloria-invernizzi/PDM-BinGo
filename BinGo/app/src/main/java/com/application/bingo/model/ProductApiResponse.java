package com.application.bingo.model;

import java.util.List;

public class ProductApiResponse {
    private String barcode;
    private String imageUrl;
    private String name;
    private String brand;
    private List<Packaging> packagings;

    public String getImageUrl() {
        return imageUrl;
    }

    public ProductApiResponse setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;

        return this;
    }

    public String getBarcode() {
        return barcode;
    }

    public ProductApiResponse setBarcode(String barcode) {
        this.barcode = barcode;

        return this;
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

    public ProductApiResponse setPackagings(List<Packaging> packagings) {
        this.packagings = packagings;

        return this;
    }

    public String getBrand() {
        return brand;
    }

    public ProductApiResponse setBrand(String brand) {
        this.brand = brand;

        return this;
    }
}
