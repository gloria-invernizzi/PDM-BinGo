package com.application.bingo.ui.viewmodel;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.application.bingo.R;
import com.application.bingo.model.WasteItem;
import com.application.bingo.repository.WasteRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WasteViewModel extends ViewModel {

    private final WasteRepository repository;
    private final MutableLiveData<List<WasteItem>> wasteItems = new MutableLiveData<>();
    private final MutableLiveData<List<WasteItem>> filteredWasteItems = new MutableLiveData<>();
    private final MutableLiveData<List<String>> categories = new MutableLiveData<>();
    private Map<String, String> textToCategoryKey;


    public WasteViewModel(WasteRepository wasteRepo) {
        this.repository = wasteRepo;
    }

    public LiveData<List<WasteItem>> getWasteItems() {
        return wasteItems;
    }

    public LiveData<List<WasteItem>> getFilteredWasteItems() {
        return filteredWasteItems;
    }


    public LiveData<List<String>> getCategories() {
        return categories;
    }

    public Map<String, String> getTextToCategoryKey() {
        return textToCategoryKey;
    }

    public void loadWasteData(String lang, Context context) {
        List<WasteItem> items = repository.loadWasteItems(lang);
        List<String> catsKeys = repository.loadCategories(lang);

        wasteItems.setValue(items);
        filteredWasteItems.setValue(items);

        // Converto le chiavi in testi
        Map<String, String> catTextMap = getCategoryTextMap(context);
        List<String> catsLocalized = new ArrayList<>();

        // Aggiungo "ALL" come prima voce
        catsLocalized.add(catTextMap.get("ALL"));

        for (String key : catsKeys) {
            String localized = catTextMap.getOrDefault(key, key);
            catsLocalized.add(localized);
        }

        categories.setValue(catsLocalized);
    }

    public void filterWaste(String query, String categoryFilter) {
        if (wasteItems.getValue() == null) return;

        // Converto il testo selezionato nello spinner nella chiave logica
        String categoryKey = textToCategoryKey.getOrDefault(categoryFilter, "ALL");

        String q = query.toLowerCase(Locale.getDefault());
        List<WasteItem> results = new ArrayList<>();

        for (WasteItem item : wasteItems.getValue()) {
            boolean matchesText = item.getTitle().toLowerCase(Locale.getDefault()).contains(q);
            boolean matchesCategory = categoryKey.equalsIgnoreCase("ALL")
                    || item.getCategory().equalsIgnoreCase(categoryKey);

            if (matchesText && matchesCategory) {
                results.add(item);
            }
        }

        filteredWasteItems.setValue(results);
    }

    public void initCategoryMaps(Context context) {
        Map<String, String> categoryTextMap = getCategoryTextMap(context);
        textToCategoryKey = new HashMap<>();
        for (Map.Entry<String, String> entry : categoryTextMap.entrySet()) {
            textToCategoryKey.put(entry.getValue(), entry.getKey());
        }
    }

    public Map<String, String> getCategoryTextMap(Context context) {
        Map<String, String> map = new HashMap<>();
        map.put("ALL", context.getString(R.string.all));
        map.put("ORGANIC", context.getString(R.string.organic));
        map.put("PLASTIC", context.getString(R.string.plastic));
        map.put("PAPER", context.getString(R.string.paper));
        map.put("GLASS", context.getString(R.string.glass));
        map.put("RESIDUAL", context.getString(R.string.residual));
        map.put("ECOMOBILE", context.getString(R.string.ecomobile));
        map.put("CENTER", context.getString(R.string.center));
        return map;
    }


}
