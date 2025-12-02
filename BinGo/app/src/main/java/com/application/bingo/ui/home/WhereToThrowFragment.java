package com.application.bingo.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
    private List<String> categoriesList;

    private ListView listResults;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_where_to_throw, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inputSearch = view.findViewById(R.id.inputSearch);
        spinnerContainers = view.findViewById(R.id.spinnerContainers);
        listResults = view.findViewById(R.id.listResults);

        loadWasteData();
        setupSpinner();
        setupSearch();

    }

    private void loadWasteData() {
        wasteMap = new HashMap<>();
        categoriesList = new ArrayList<>();
        categoriesList.add("Tutti");

        try {
            InputStream is = getContext().getAssets().open("waste_database.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, StandardCharsets.UTF_8);

            JSONObject root = new JSONObject(jsonString);
            Iterator<String> categories = root.keys();

            while (categories.hasNext()) {
                String categoria = categories.next();
                categoriesList.add(categoria);

                JSONObject catObj = root.getJSONObject(categoria);

                // Mappa la categoria stessa
                wasteMap.put(categoria.toLowerCase(), categoria);

                // Mappa gli elementi di "cosa_mettere"
                if (catObj.has("cosa_mettere")) {
                    JSONArray metti = catObj.getJSONArray("cosa_mettere");
                    for (int i = 0; i < metti.length(); i++) {
                        String item = metti.getString(i).toLowerCase();
                        wasteMap.put(item, categoria);
                    }
                }

                // Mappa tutti i rifiuti
                if (catObj.has("rifiuti")) {
                    JSONObject rifiuti = catObj.getJSONObject("rifiuti");
                    Iterator<String> keys = rifiuti.keys();
                    while (keys.hasNext()) {
                        String key = keys.next().toLowerCase();
                        wasteMap.put(key, categoria);
                        JSONArray synonyms = rifiuti.getJSONArray(key);
                        for (int i = 0; i < synonyms.length(); i++) {
                            wasteMap.put(synonyms.getString(i).toLowerCase(), categoria);
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
                categoriesList
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerContainers.setAdapter(adapter);
    }

    private void setupSearch() {
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                filterResults();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    private void filterResults() {
        String query = inputSearch.getText().toString().toLowerCase();
        String containerFilter = spinnerContainers.getSelectedItem().toString();

        List<String> results = new ArrayList<>();

        for (Map.Entry<String, String> entry : wasteMap.entrySet()) {
            String waste = entry.getKey();
            String category = entry.getValue();

            boolean matchesSearch = waste.contains(query);
            boolean matchesContainer = containerFilter.equals("Tutti") || containerFilter.equals(category);

            if (matchesSearch && matchesContainer) {
                results.add(capitalize(waste) + " → " + category);
            }
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, results);

        listResults.setAdapter(adapter);
    }

    private String capitalize(String text) {
        if (text.length() == 0) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}