package com.application.bingo.repository.product;

import com.application.bingo.model.relation.ProductWithPackagings;
import com.application.bingo.model.dto.ProductDto;

import java.util.List;

// interfaccia implementabile dai fragment
public interface ProductResponseCallback {
    // Remote
    void onSuccessFromRemote(ProductDto product, long lastUpdate);
    void onFailureFromRemote(Exception exception);

    // Locale
    void onSuccessFromLocal(ProductWithPackagings product);
    void onFailureFromLocal(Exception exception);
    void onProductsFavoritesSuccessFromLocale(List<ProductDto> favorites);

    /*
    void onFavoriteStatusChanged(Article news, List<Article> favorite);
    void onFavoriteStatusChanged(List<Article> news);
    void onDeleteFavoriteSuccess(List<Article> favorite);
    */
}
