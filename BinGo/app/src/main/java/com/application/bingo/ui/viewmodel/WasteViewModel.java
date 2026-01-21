package com.application.bingo.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.application.bingo.model.WasteItem;
import com.application.bingo.repository.WasteRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * ViewModel for WhereToThrowFragment.
 * Manages loading and filtering of waste disposal information.
 */
public class WasteViewModel extends ViewModel {

    private final WasteRepository repository;
    private final MutableLiveData<List<WasteItem>> wasteItems = new MutableLiveData<>();
    private final MutableLiveData<List<WasteItem>> filteredWasteItems = new MutableLiveData<>();
    private final MutableLiveData<List<String>> categories = new MutableLiveData<>();

    public WasteViewModel(WasteRepository wasteRepo) {
        this.repository = wasteRepo;
    }

    /**
     * Returns the observable list of filtered waste items.
     */
    public LiveData<List<WasteItem>> getFilteredWasteItems() {
        return filteredWasteItems;
    }

    /**
     * Returns the observable list of available categories.
     */
    public LiveData<List<String>> getCategories() {
        return categories;
    }

    /**
     * Loads waste data from the repository based on the specified language.
     * 
     * @param lang The language code (e.g., "en", "it") for localized content.
     */
    public void loadWasteData(String lang) {
        List<WasteItem> items = repository.loadWasteItems(lang);
        List<String> categoryKeys = repository.loadCategories(lang);

        wasteItems.setValue(items);
        filteredWasteItems.setValue(items);

        List<String> cats = new ArrayList<>();
        cats.add("ALL"); // Default option for showing all categories
        cats.addAll(categoryKeys);

        categories.setValue(cats);
    }

    /**
     * Filters the waste items based on search query and category.
     * 
     * @param query       The search text to match against item titles.
     * @param categoryKey The category to filter by (or "ALL").
     */
    public void filterWaste(String query, String categoryKey) {
        if (wasteItems.getValue() == null || categoryKey == null || query == null) return;

        String q = query.toLowerCase(Locale.getDefault());
        List<WasteItem> results = new ArrayList<>();

        for (WasteItem item : wasteItems.getValue()) {
            String title = item.getTitle();
            String category = item.getCategory();
            
            boolean matchesText = title != null && title.toLowerCase(Locale.getDefault()).contains(q);
            boolean matchesCategory = categoryKey.equalsIgnoreCase("ALL") ||
                    (category != null && category.equalsIgnoreCase(categoryKey));
            
            if (matchesText && matchesCategory) {
                results.add(item);
            }
        }
        filteredWasteItems.setValue(results);
    }
}
