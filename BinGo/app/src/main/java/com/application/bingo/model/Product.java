package com.application.bingo.model;

import static com.application.bingo.constants.Language.ENG;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.application.bingo.constants.Language;

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
    private boolean isFavorite;
    @ColumnInfo(name = "name_it")
    private String nameIt = "";
    @ColumnInfo(name = "name_en")
    private String nameEn = "";
    @ColumnInfo(name = "name_es")
    private String nameEs = "";

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
        this.isFavorite = favorite;
    }

    public boolean getNonRecyclableAndNonBiodegradable() {
        return nonRecyclableAndNonBiodegradable;
    }

    public void setNonRecyclableAndNonBiodegradable(boolean nonRecyclableAndNonBiodegradable)
    {
        this.nonRecyclableAndNonBiodegradable = nonRecyclableAndNonBiodegradable;
    }

    public String getNameIt() {
        return nameIt;
    }

    public void setNameIt(String nameIt) {
        this.nameIt = nameIt;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameEs() {
        return nameEs;
    }

    public void setNameEs(String nameEs) {
        this.nameEs = nameEs;
    }

    public String getNameFromLanguage(String language) {
        switch (language) {
            case "it":
                return !nameIt.isEmpty() ? nameIt : name;
            case "en":
                return !nameEn.isEmpty() ? nameEn : name;
            case "es":
                return !nameEs.isEmpty() ? nameEs : name;
            default:
                return name;
        }
    }

    public void setNameByLanguage(Language language, String name) {
        switch (language) {
            case ITA:
                 this.nameIt = name;
                 break;
            case ENG:
                 this.nameEn = name;
                 break;
            case ES:
                 this.nameEs = name;
            default:
                 this.name = name;
        }
    }
}
