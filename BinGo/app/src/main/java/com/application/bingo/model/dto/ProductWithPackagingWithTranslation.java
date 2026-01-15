package com.application.bingo.model.dto;

import com.application.bingo.model.Product;
import com.application.bingo.model.relation.PackagingWithTranslations;

import java.util.ArrayList;
import java.util.List;

public class ProductWithPackagingWithTranslation {
    private Product product;
    private List<PackagingWithTranslations> packagings;

    public ProductWithPackagingWithTranslation() {
        packagings = new ArrayList<>();
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public List<PackagingWithTranslations> getPackagings() {
        return packagings;
    }

    public void setPackagings(List<PackagingWithTranslations> packagings) {
        this.packagings = packagings;
    }
}
