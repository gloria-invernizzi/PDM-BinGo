package com.application.bingo.util;

import android.content.Context;
import android.util.Log;

import com.application.bingo.model.PackagingDto;
import com.application.bingo.model.ProductApiResponse;

import org.json.JSONObject;

import java.io.InputStream;

// 5449000214911
public class MaterialParserUtils {
    private Context context;
    private ProductApiResponse product;
    public MaterialParserUtils(Context context, ProductApiResponse product) {
        this.context = context;
        this.product = product;
    }
    public ProductApiResponse hydratePackagings()
    {
        if (product.getPackagings() == null) {
            return product;
        }

        for (PackagingDto packaging:
             product.getPackagings()) {
            parseMaterial(packaging);
        }

        return product;
    }

    private void parseMaterial(PackagingDto packaging) {
        try {
            InputStream input = context.getAssets().open("open_food_facts_packaging_materials.json");

            int size = input.available();

            byte[] buffer = new byte[size];

            input.read(buffer);

            input.close();

            String json = new String(buffer, "UTF-8");
            JSONObject obj = new JSONObject(json);

            JSONObject material = obj.getJSONObject(packaging.getMaterial());

            if (material.has("description")) {
                JSONObject description = material.getJSONObject("description");

                if (description.has("it")) {
                    packaging.setMaterialDescription(description.getString("it"));
                } else {
                    packaging.setMaterialDescription(description.getString("en"));
                }
            }

            JSONObject name = material.getJSONObject("name");

            if (name.has("it")) {
                packaging.setMaterialName(name.getString("it"));
            } else {
                packaging.setMaterialName(name.getString("en"));
            }
        } catch (Exception e) {
            Log.e("MaterialParser", e.getMessage() + " ");
        }
    }
}
