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
 * ViewModel per WhereToThrowFragment
 */
public class WasteViewModel extends ViewModel {

    private final WasteRepository repository;
    private final MutableLiveData<List<WasteItem>> wasteItems = new MutableLiveData<>();
    private final MutableLiveData<List<WasteItem>> filteredWasteItems = new MutableLiveData<>();
    private final MutableLiveData<List<String>> categories = new MutableLiveData<>();


    public WasteViewModel(WasteRepository wasteRepo) {
        this.repository = wasteRepo;
    }

    public LiveData<List<WasteItem>> getFilteredWasteItems() {
        return filteredWasteItems;
    }

    public LiveData<List<String>> getCategories() {
        return categories;
    }

    public void loadWasteData(String lang) {
        List<WasteItem> items = repository.loadWasteItems(lang);
        List<String> categoryKeys = repository.loadCategories(lang);

        wasteItems.setValue(items);
        filteredWasteItems.setValue(items);

        List<String> cats = new ArrayList<>();
        cats.add("ALL");
        cats.addAll(categoryKeys);

        categories.setValue(cats);
    }

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
