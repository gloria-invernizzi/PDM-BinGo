package com.application.bingo.service;

import com.application.bingo.model.dto.ProductWithPackagingWithTranslation;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductAPIService {
    @GET("/api/v2/product/{barcode}")
    Call<ProductWithPackagingWithTranslation> getProduct(
            @Path("barcode") String barcode,
            @Query("product_type") String productType
    );
}
