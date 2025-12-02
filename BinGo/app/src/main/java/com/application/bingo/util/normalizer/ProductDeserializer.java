package com.application.bingo.util.normalizer;

import com.application.bingo.model.Packaging;
import com.application.bingo.model.ProductApiResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import android.util.Log;

import java.lang.reflect.Type;
import java.util.List;

public class ProductDeserializer implements JsonDeserializer<ProductApiResponse>
{
    @Override
    public ProductApiResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject obj = json.getAsJsonObject();

        ProductApiResponse product = new ProductApiResponse();

        JsonObject jsonProduct = obj.getAsJsonObject("product");
        JsonArray jsonPackagings = jsonProduct
                .getAsJsonObject("ecoscore_data")
                .getAsJsonObject("adjustments")
                .getAsJsonObject("packaging")
                .getAsJsonArray("packagings")
        ;

        Type listType = new TypeToken<List<Packaging>>() {}.getType();
        List<Packaging> packagingList = context.deserialize(jsonPackagings, listType);

        product.setBarcode(obj.get("code").getAsString());

        product.setName(jsonProduct.get("product_name_it").getAsString());
        product.setImageUrl(jsonProduct.get("image_front_url").getAsString());
        product.setBrand(jsonProduct.get("brands").getAsString());

        product.setPackagings(packagingList);

        return product;
    }
}
