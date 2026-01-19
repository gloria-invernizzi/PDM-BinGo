package com.application.bingo.repository.product;

import com.application.bingo.model.dto.ProductWithPackagingWithTranslation;
import com.application.bingo.model.relation.ProductWithPackagings;

import java.util.List;

public interface ProductResponseCallback {
    // Remote
    void onSuccessFromRemote(ProductWithPackagingWithTranslation product, long lastUpdate);
    void onFailureFromRemote(Exception exception);

    // Locale
    void onSuccessFromLocal(ProductWithPackagingWithTranslation product);
    void onFailureFromLocal(Exception exception);
    void onProductsFavoritesSuccessFromLocale(List<ProductWithPackagings> favorites);
    void onProductStatusChanged(List<ProductWithPackagings> favorites);
}
