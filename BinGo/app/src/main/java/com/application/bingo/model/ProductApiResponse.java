package com.application.bingo.model;

import java.util.ArrayList;
import java.util.List;

public class ProductApiResponse {
    private String barcode;
    private String imageUrl = "";
    private String name = "";
    private String brand = "";
    private List<PackagingDto> packagings;
    private Boolean nonRecyclableAndNonBiodegradable;

    public ProductApiResponse() {
        this.packagings = new ArrayList<>();
    }
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

    public ProductApiResponse setBrand(String brand) {
        this.brand = brand;

        return this;
    }

    public Boolean getNonRecyclableAndNonBiodegradable() {
        return nonRecyclableAndNonBiodegradable;
    }

    public ProductApiResponse setNonRecyclableAndNonBiodegradable(Boolean nonRecyclableAndNonBiodegradable)
    {
        this.nonRecyclableAndNonBiodegradable = nonRecyclableAndNonBiodegradable;

        return this;
    }
}
