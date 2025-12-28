package com.application.bingo.model;

public class ProductApiResponse {
    public int status;

    public ProductWithPackagings product;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ProductWithPackagings getProduct() {
        return product;
    }

    public void setProduct(ProductWithPackagings product) {
        this.product = product;
    }
}
