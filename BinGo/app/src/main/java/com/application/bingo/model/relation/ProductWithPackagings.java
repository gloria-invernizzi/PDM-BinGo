package com.application.bingo.model.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.application.bingo.model.Packaging;
import com.application.bingo.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductWithPackagings {
    @Embedded
    private Product product;

    @Relation(
            parentColumn = "barcode",
            entityColumn = "product_barcode"
    )
    private List<Packaging> packagings = new ArrayList<>();

    public List<Packaging> getPackagings() {
        return packagings;
    }

    public void setPackagings(List<Packaging> packagings) {
        this.packagings = packagings;
    }

    public void addPackaging(Packaging packaging)
    {
        if (packagings.contains(packaging)) {
            return;
        }

        packagings.add(packaging);
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
