package com.application.bingo.service;

import android.app.Application;
import android.content.Context;

import com.application.bingo.database.AppDatabase;
import com.application.bingo.datasource.product.BaseProductLocalDataSource;
import com.application.bingo.datasource.product.BaseProductRemoteDataSource;
import com.application.bingo.datasource.product.ProductApiDataSource;
import com.application.bingo.datasource.product.ProductLocalDataSource;
import com.application.bingo.datasource.product.ProductMockDataSource;
import com.application.bingo.model.dto.ProductWithPackagingWithTranslation;
import com.application.bingo.repository.product.ProductRepository;
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

    public ProductAPIService getProductAPIService(ProductDeserializer deserializer) {
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(ProductWithPackagingWithTranslation.class, deserializer)
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

    public ProductRepository getProductRepository(Application application, boolean debugMode) {
        BaseProductRemoteDataSource productRemoteDataSource;
        BaseProductLocalDataSource productLocalDataSource;

        ProductDeserializer deserializer = new ProductDeserializer(application);
        if (debugMode) {
            productRemoteDataSource = new ProductMockDataSource(application);
        } else {
            productRemoteDataSource = new ProductApiDataSource(deserializer);
        }

        productLocalDataSource = new ProductLocalDataSource(getAppDatabase(application));

        // Repository ha sorgente dati sia remota che locale
        return new ProductRepository(productRemoteDataSource, productLocalDataSource);
    }
}