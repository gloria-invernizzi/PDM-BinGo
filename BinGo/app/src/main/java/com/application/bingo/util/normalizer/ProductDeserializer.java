package com.application.bingo.util.normalizer;

import android.content.Context;
import android.util.Log;

import com.application.bingo.model.Packaging;
import com.application.bingo.model.Product;
import com.application.bingo.model.dto.ProductWithPackagingWithTranslation;
import com.application.bingo.model.relation.PackagingWithTranslations;
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
import java.util.ArrayList;
import java.util.List;

public class ProductDeserializer implements JsonDeserializer<ProductWithPackagingWithTranslation>
{
    private static final String PACKAGING_MISSING_MATERIAL = "packaging_data_missing";

    private Context appContext;
    public ProductDeserializer(Context appContext) {
        this.appContext = appContext;
    }

    @Override
    public ProductWithPackagingWithTranslation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject obj = json.getAsJsonObject();

        Product product = new Product();

        ProductWithPackagingWithTranslation productWithPackagingWithTranslation = new ProductWithPackagingWithTranslation();
        productWithPackagingWithTranslation.setProduct(product);

        JsonObject jsonProduct = obj.getAsJsonObject("product");
        JsonObject jsonPackage = jsonProduct
                .getAsJsonObject("ecoscore_data")
                .getAsJsonObject("adjustments")
                .getAsJsonObject("packaging")
        ;

        if (!(jsonPackage.has("warning") && jsonPackage.get("warning").getAsString().equalsIgnoreCase(PACKAGING_MISSING_MATERIAL))) {
            JsonArray jsonPackagings = jsonPackage.getAsJsonArray("packagings");

            Type listType = new TypeToken<List<Packaging>>() {}.getType();
            List<Packaging> packagingList = context.deserialize(jsonPackagings, listType);

            List<PackagingWithTranslations> packagingWithTranslationsList = new ArrayList<>();

            MaterialParserUtils materialParser = new MaterialParserUtils(appContext);
            for (Packaging packaging:
                 packagingList) {
                PackagingWithTranslations packagingWithTranslations = new PackagingWithTranslations();
                packagingWithTranslations.setPackaging(packaging);

                materialParser.parseMaterial(packagingWithTranslations);

                packagingWithTranslationsList.add(packagingWithTranslations);
            }

            product.setNonRecyclableAndNonBiodegradable(jsonPackage.get("non_recyclable_and_non_biodegradable_materials").getAsBoolean());

            productWithPackagingWithTranslation.setPackagings(packagingWithTranslationsList);
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

        ProductWithPackagingWithTranslation localProduct = ServiceLocator.getInstance().getAppDatabase(appContext).productDao().findProduct(product.getBarcode());

        if (null != localProduct && localProduct.getProduct().isFavorite()) {
            product.setFavorite(localProduct.getProduct().isFavorite());
        }

        return productWithPackagingWithTranslation;
    }
}
