package com.application.bingo.service;

import android.app.Application;
import android.content.Context;

import com.application.bingo.database.AppDatabase;
import com.application.bingo.model.dto.ProductDto;
import com.application.bingo.util.normalizer.ProductDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceLocator {

    private static volatile ServiceLocator INSTANCE = null;
    private static final String OPEN_FOOD_FACTS_API_URL = "https://world.openfoodfacts.org";

    private ServiceLocator() {
    }

    public static ServiceLocator getInstance() {
        if (INSTANCE == null) {
            synchronized (ServiceLocator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServiceLocator();
                }
            }
        }
        return INSTANCE;
    }

    public ProductAPIService getProductAPIService(Context context) {
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(ProductDto.class, new ProductDeserializer(context))
            .create()
        ;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(OPEN_FOOD_FACTS_API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(ProductAPIService.class);
    }

    public AppDatabase getAppDatabase(Context context) {
        return AppDatabase.getInstance(context);
    }
}