package com.application.bingo.util;

import com.application.bingo.model.Product;
import com.application.bingo.model.ProductWithPackagings;
import com.application.bingo.model.dto.ProductDto;

// interfaccia implementabile dai fragment
public interface ResponseCallback {
    void onSuccess(ProductDto product, long lastUpdate);
    void onFailure(String error);
}
