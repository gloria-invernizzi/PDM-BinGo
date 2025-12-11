package com.application.bingo.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.application.bingo.R;
import com.application.bingo.model.WasteItem;
import com.application.bingo.repository.SettingsRepository;
import com.application.bingo.ui.adapter.WasteAdapter;
import com.application.bingo.viewmodel.SettingsViewModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WhereToThrowFragment extends Fragment {
    private Map<String, String> wasteMap;
    private EditText inputSearch;
    private Spinner spinnerContainers;
    private ListView listResults;
    private Map<String, Integer> colorMap;
    private Map<String, String> categoryToTextMap;
    private Map<String, String> textToCategoryKey;

    public static final String CATEGORY_ALL = "ALL";
    public static final String CATEGORY_ORGANIC = "ORGANIC";
    public static final String CATEGORY_PLASTIC = "PLASTIC";
    public static final String CATEGORY_PAPER = "PAPER";
    public static final String CATEGORY_GLASS = "GLASS";
    public static final String CATEGORY_RESIDUAL = "RESIDUAL";
    public static final String CATEGORY_CENTER = "CENTER";
    public static final String CATEGORY_ECOMOBILE = "ECOMOBILE";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_where_to_throw, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inputSearch = view.findViewById(R.id.inputSearch);
        spinnerContainers = view.findViewById(R.id.spinnerContainers);
        listResults = view.findViewById(R.id.listResults);

        setupCategoryTextMap();
        setupAppearanceMaps();
        loadWasteData();
        setupSpinner();
        setupSearch();

        textToCategoryKey = new HashMap<>();
        for (Map.Entry<String, String> entry : categoryToTextMap.entrySet()) {
            textToCategoryKey.put(entry.getValue(), entry.getKey());
        }

    }
    private void setupCategoryTextMap() {
        categoryToTextMap = new HashMap<>();
        categoryToTextMap.put(CATEGORY_ALL, getString(R.string.all));
        categoryToTextMap.put(CATEGORY_ORGANIC, getString(R.string.organic));
        categoryToTextMap.put(CATEGORY_PLASTIC, getString(R.string.plastic));
        categoryToTextMap.put(CATEGORY_PAPER, getString(R.string.paper));
        categoryToTextMap.put(CATEGORY_GLASS, getString(R.string.glass));
        categoryToTextMap.put(CATEGORY_RESIDUAL, getString(R.string.residual));
        categoryToTextMap.put(CATEGORY_ECOMOBILE, getString(R.string.ecomobile));
        categoryToTextMap.put(CATEGORY_CENTER, getString(R.string.center));
    }
    private void setupAppearanceMaps() {
        colorMap = new HashMap<String, Integer>();

        colorMap.put(CATEGORY_ORGANIC, 0xFF8D6E63);
        colorMap.put(CATEGORY_PLASTIC, 0xFFAB47BC);
        colorMap.put(CATEGORY_PAPER, 0xFFECC300);
        colorMap.put(CATEGORY_GLASS, 0xFF26A69A);
        colorMap.put(CATEGORY_RESIDUAL, 0xFFE53935);
        colorMap.put(CATEGORY_ECOMOBILE, 0xFF81D4FA);
        colorMap.put(CATEGORY_CENTER, 0xFFFFB74D);
    }

    private void loadWasteData() {
        SettingsRepository settingsRepo = new SettingsRepository(requireContext());
        SettingsViewModel settingsVM = new ViewModelProvider(
                this,
                new ViewModelProvider.Factory() {
                    @NonNull
                    @Override
                    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                        if (modelClass.isAssignableFrom(SettingsViewModel.class)) {
                            return (T) new SettingsViewModel(settingsRepo);
                        }
                        throw new IllegalArgumentException("unknown viewmodel class");
                    }
                }
        ).get(SettingsViewModel.class);

        String lang = settingsVM.getLanguage();
        String jsonFile = "waste_database_it.json";

        if (!lang.equals("italian")) {
            jsonFile = "waste_database_en.json";
        }

        wasteMap = new HashMap<>();

        try {
            InputStream is = getContext().getAssets().open(jsonFile);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, StandardCharsets.UTF_8);

            JSONObject root = new JSONObject(jsonString);
            Iterator<String> categories = root.keys();

            while (categories.hasNext()) {
                String categoryKey = categories.next();
                JSONObject catObj = root.getJSONObject(categoryKey);

                wasteMap.put(categoryKey, categoryKey);

                if (catObj.has("cosa_mettere")) {
                    JSONArray rifiuti = catObj.getJSONArray("cosa_mettere");
                    for (int i = 0; i < rifiuti.length(); i++) {
                        wasteMap.put(rifiuti.getString(i).toLowerCase(), categoryKey);
                    }
                }

                if (catObj.has("rifiuti")) {
                    JSONObject rifiuti = catObj.getJSONObject("rifiuti");
                    Iterator<String> keys = rifiuti.keys();
                    while (keys.hasNext()) {
                        String key = keys.next().toLowerCase();
                        wasteMap.put(key, categoryKey);
                        JSONArray synonyms = rifiuti.getJSONArray(key);
                        for (int i = 0; i < synonyms.length(); i++) {
                            wasteMap.put(synonyms.getString(i).toLowerCase(), categoryKey);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Errore caricamento rifiuti", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            getContext(),
            android.R.layout.simple_spinner_item,
            List.of(
                categoryToTextMap.get(CATEGORY_ALL),
                categoryToTextMap.get(CATEGORY_ORGANIC),
                categoryToTextMap.get(CATEGORY_PLASTIC),
                categoryToTextMap.get(CATEGORY_PAPER),
                categoryToTextMap.get(CATEGORY_GLASS),
                categoryToTextMap.get(CATEGORY_RESIDUAL),
                categoryToTextMap.get(CATEGORY_ECOMOBILE),
                categoryToTextMap.get(CATEGORY_CENTER)
            )
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerContainers.setAdapter(adapter);
    }

    private void setupSearch() {
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { filterResults(); }
        });

        spinnerContainers.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                filterResults();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    private void filterResults() {
        String query = inputSearch.getText().toString().toLowerCase();
        String categoryFilter = spinnerContainers.getSelectedItem().toString();

        List<WasteItem> results = new ArrayList<>();

        for (Map.Entry<String, String> entry : wasteMap.entrySet()) {
            String waste = entry.getKey();
            String category = entry.getValue();

            boolean matchesText = waste.toLowerCase().contains(query);
            boolean matchesCategory = CATEGORY_ALL.equalsIgnoreCase(categoryFilter)
                    || categoryToTextMap.getOrDefault(category, category).equals(categoryFilter);

            if (matchesText && matchesCategory) {
                results.add(new WasteItem(
                        waste.substring(0,1).toUpperCase() + waste.substring(1),
                        categoryToTextMap.getOrDefault(category, category)
                ));
            }
        }

        listResults.setAdapter(new WasteAdapter(getContext(), results, colorMap, textToCategoryKey));
    }

}