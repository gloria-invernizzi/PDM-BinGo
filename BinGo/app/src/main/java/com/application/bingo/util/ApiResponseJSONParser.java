package com.application.bingo.util;

import android.content.Context;
import com.google.gson.Gson;
import com.application.bingo.model.ProductApiResponse;

public class ApiResponseJSONParser {
    public Context context;

    public ApiResponseJSONParser(Context context) {
        this.context = context;
    }

    public ProductApiResponse gsonParse(String jsonRaw) {
        return new Gson().fromJson(jsonRaw, ProductApiResponse.class);
    }
}
