package com.application.bingo.util.normalizer;

import android.content.Context;

import com.application.bingo.model.dto.PackagingDto;
import com.application.bingo.model.dto.ProductDto;
import com.application.bingo.service.ServiceLocator;
import com.application.bingo.util.MaterialParserUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ProductDeserializer implements JsonDeserializer<ProductDto>
{
    private static final String PACKAGING_MISSING_MATERIAL = "packaging_data_missing";

    private Context appContext;
    public ProductDeserializer(Context appContext) {
        this.appContext = appContext;
    }

    @Override
    public ProductDto deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject obj = json.getAsJsonObject();

        ProductDto product = new ProductDto();

        JsonObject jsonProduct = obj.getAsJsonObject("product");
        JsonObject jsonPackage = jsonProduct
                .getAsJsonObject("ecoscore_data")
                .getAsJsonObject("adjustments")
                .getAsJsonObject("packaging")
        ;

        if (!(jsonPackage.has("warning") && jsonPackage.get("warning").getAsString().equalsIgnoreCase(PACKAGING_MISSING_MATERIAL))) {
            JsonArray jsonPackagings = jsonPackage.getAsJsonArray("packagings");

            Type listType = new TypeToken<List<PackagingDto>>() {}.getType();
            List<PackagingDto> packagingList = context.deserialize(jsonPackagings, listType);

            MaterialParserUtils materialParser = new MaterialParserUtils(appContext);
            for (PackagingDto packaging:
                 packagingList) {
                materialParser.parseMaterial(packaging);
            }

            product.setNonRecyclableAndNonBiodegradable(jsonPackage.get("non_recyclable_and_non_biodegradable_materials").getAsBoolean());
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

        product.setBarcode(obj.get("code").getAsString());
        product.setBrand(jsonProduct.get("brands").getAsString());

        if (ServiceLocator.getInstance().getAppDatabase(appContext).productDao().findByBarcode(product.getBarcode()) != null) {
            product.setFavorite(true);
        }

        return product;
    }
}
