package com.application.bingo.repository;

import android.app.Application;
import android.content.res.AssetManager;

import com.application.bingo.model.WasteItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WasteRepository {

    private final Application application;

    public WasteRepository(Application application) {
        this.application = application;
    }

    public List<WasteItem> loadWasteItems(String lang) {
        String jsonFile = lang.equals("italian") ? "waste_database_it.json" : "waste_database_en.json";
        List<WasteItem> items = new ArrayList<>();

        try {
            AssetManager am = application.getAssets();
            InputStream is = am.open(jsonFile);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, StandardCharsets.UTF_8);

            JSONObject root = new JSONObject(jsonString);
            Iterator<String> categories = root.keys();

            while (categories.hasNext()) {
                String categoryKey = categories.next();
                JSONObject catObj = root.getJSONObject(categoryKey);

                if (catObj.has("cosa_mettere")) {
                    JSONArray rifiuti = catObj.getJSONArray("cosa_mettere");
                    for (int i = 0; i < rifiuti.length(); i++) {
                        items.add(new WasteItem(rifiuti.getString(i), categoryKey));
                    }
                }

                if (catObj.has("rifiuti")) {
                    JSONObject rifiuti = catObj.getJSONObject("rifiuti");
                    Iterator<String> keys = rifiuti.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        items.add(new WasteItem(key, categoryKey));
                        JSONArray synonyms = rifiuti.getJSONArray(key);
                        for (int i = 0; i < synonyms.length(); i++) {
                            items.add(new WasteItem(synonyms.getString(i), categoryKey));
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    public List<String> loadCategories(String lang) {
        String jsonFile = lang.equals("italian") ? "waste_database_it.json" : "waste_database_en.json";
        List<String> cats = new ArrayList<>();

        try {
            AssetManager am = application.getAssets();
            InputStream is = am.open(jsonFile);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, StandardCharsets.UTF_8);

            JSONObject root = new JSONObject(jsonString);
            Iterator<String> categories = root.keys();

            while (categories.hasNext()) {
                cats.add(categories.next());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cats;
    }
}
