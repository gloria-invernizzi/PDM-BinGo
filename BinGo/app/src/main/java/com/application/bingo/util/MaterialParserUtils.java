package com.application.bingo.util;

import android.content.Context;
import android.util.Log;

import com.application.bingo.constants.Language;
import com.application.bingo.model.dto.MaterialDto;
import com.application.bingo.model.dto.PackagingDto;

import org.json.JSONObject;

import java.io.InputStream;

// 5449000214911
public class MaterialParserUtils {
    private Context context;
    public MaterialParserUtils(Context context) {
        this.context = context;
    }

    public PackagingDto parseMaterial(PackagingDto packaging) {
        try {
            // TODO: move json loading on object creation, constructor, instead of reading all every time
            InputStream input = context.getAssets().open("open_food_facts_packaging_materials.json");

            int size = input.available();

            byte[] buffer = new byte[size];

            input.read(buffer);

            input.close();

            String json = new String(buffer, "UTF-8");
            JSONObject obj = new JSONObject(json);

            JSONObject materialObj = obj.getJSONObject(packaging.getMaterial());

            for (Language language:
                 Language.cases()) {
                boolean hasDescription = false;
                boolean hasName = false;

                MaterialDto material = new MaterialDto();
                material.setLanguage(language);

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

                material.setEmpty(!(hasDescription || hasName));

                packaging.addTranslation(material);
            }

            return packaging;
        } catch (Exception e) {
            Log.e("MaterialParser", e.getMessage() + " ");
        }

        return null;
    }
}
