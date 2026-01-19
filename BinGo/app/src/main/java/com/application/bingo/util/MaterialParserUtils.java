package com.application.bingo.util;

import android.content.Context;
import android.util.Log;

import com.application.bingo.constants.Language;
import com.application.bingo.model.Material;
import com.application.bingo.model.relation.PackagingWithTranslations;

import org.json.JSONObject;

import java.io.InputStream;

// 5449000214911
public class MaterialParserUtils {
    private JSONObject rawPackagingMaterials;
    public MaterialParserUtils(Context context) {
        try {
            InputStream input = context.getAssets().open("open_food_facts_packaging_materials.json");

            int size = input.available();

            byte[] buffer = new byte[size];

            input.read(buffer);

            input.close();

            String json = new String(buffer, "UTF-8");
            this.rawPackagingMaterials = new JSONObject(json);
        } catch (Exception e) {
            Log.e("MaterialParser", e.getMessage() + " ");
        }
    }

    public PackagingWithTranslations parseMaterial(PackagingWithTranslations packaging) {
        try {
            JSONObject materialObj = this.rawPackagingMaterials.getJSONObject(packaging.getPackaging().getMaterial());

            for (Language language:
                 Language.cases()) {
                boolean hasDescription = false;
                boolean hasName = false;

                Material material = new Material();
                material.setLanguage(language.languageAsString());

                if (materialObj.has("description")) {
                    JSONObject description = materialObj.getJSONObject("description");

                    if (description.has(language.languageAsString())) {
                        material.setDescription(description.getString(language.languageAsString()));

                        hasDescription = true;
                    }
                }

                JSONObject name = materialObj.getJSONObject("name");

                if (name.has(language.languageAsString())) {
                    material.setName(name.getString(language.languageAsString()));

                    hasName = true;
                }

                if (hasDescription || hasName) {
                    packaging.addTranslation(material);
                }
            }

            return packaging;
        } catch (Exception e) {
            Log.e("MaterialParser", e.getMessage() + " ");
        }

        return null;
    }
}
