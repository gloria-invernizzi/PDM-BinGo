package com.application.bingo.util.normalizer;

import android.util.Log;

import com.application.bingo.model.PackagingDto;
import com.application.bingo.model.ProductApiResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ProductDeserializer implements JsonDeserializer<ProductApiResponse>
{
    private String packagingMissingWarning = "packaging_data_missing";

    @Override
    public ProductApiResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject obj = json.getAsJsonObject();

        ProductApiResponse product = new ProductApiResponse();

        JsonObject jsonProduct = obj.getAsJsonObject("product");
        JsonObject jsonPackage = jsonProduct
                .getAsJsonObject("ecoscore_data")
                .getAsJsonObject("adjustments")
                .getAsJsonObject("packaging")
        ;

        if (!(jsonPackage.has("warning") && jsonPackage.get("warning").getAsString().equalsIgnoreCase(this.packagingMissingWarning))) {
            JsonArray jsonPackagings = jsonPackage.getAsJsonArray("packagings");

            Type listType = new TypeToken<List<PackagingDto>>() {}.getType();
            List<PackagingDto> packagingList = context.deserialize(jsonPackagings, listType);

            product.setBarcode(obj.get("code").getAsString());

            product.setPackagings(packagingList);
        }

        if (jsonProduct.has("product_name_it")) {
            product.setName(jsonProduct.get("product_name_it").getAsString());
        } else {
            product.setName(jsonProduct.get("product_name").getAsString());
        }

        if (jsonProduct.has("image_front_url")) {
            product.setImageUrl(jsonProduct.get("image_front_url").getAsString());
        }
        product.setBrand(jsonProduct.get("brands").getAsString());

        product.setNonRecyclableAndNonBiodegradable(jsonPackage.get("non_recyclable_and_non_biodegradable_materials").getAsBoolean());

        return product;
    }
}
