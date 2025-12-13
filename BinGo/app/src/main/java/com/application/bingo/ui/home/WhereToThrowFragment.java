package com.application.bingo.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import com.application.bingo.R;
import com.application.bingo.ui.adapter.WasteAdapter;
import com.application.bingo.ui.viewmodel.SettingsViewModel;
import com.application.bingo.ui.viewmodel.ViewModelFactory;
import com.application.bingo.ui.viewmodel.WasteViewModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WhereToThrowFragment extends Fragment {
    private EditText inputSearch;
    private Spinner spinnerContainers;
    private Map<String, Integer> colorMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_where_to_throw, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inputSearch = view.findViewById(R.id.inputSearch);
        spinnerContainers = view.findViewById(R.id.spinnerContainers);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerWaste);

        setupAppearanceMaps();

        ViewModelFactory factory = new ViewModelFactory(requireActivity().getApplication());
        WasteViewModel wasteVM = new ViewModelProvider(this, factory).get(WasteViewModel.class);
        SettingsViewModel settingsVM = new ViewModelProvider(this, factory).get(SettingsViewModel.class);
        settingsVM.loadLanguage();

        // Osserva la lingua e ricarica i dati
        settingsVM.getLanguageLiveData().observe(getViewLifecycleOwner(), lang -> {
            wasteVM.loadWasteData(lang, requireContext());
        });

        wasteVM.initCategoryMaps(requireContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        WasteAdapter adapter = new WasteAdapter(getContext(), new ArrayList<>(), colorMap, wasteVM.getCategoryTextMap(getContext()));
        recyclerView.setAdapter(adapter);

        // Osserva items filtrati
        wasteVM.getFilteredWasteItems().observe(getViewLifecycleOwner(), adapter::updateList);

        // Osserva categorie per lo spinner
        wasteVM.getCategories().observe(getViewLifecycleOwner(), cats -> {
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                    getContext(),
                    android.R.layout.simple_spinner_item,
                    cats
            );
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerContainers.setAdapter(spinnerAdapter);
        });

        setupSearch(wasteVM);
    }

    private void setupAppearanceMaps() {
        colorMap = new HashMap<>();
        colorMap.put("ORGANIC", 0xFF8D6E63);
        colorMap.put("PLASTIC", 0xFFAB47BC);
        colorMap.put("PAPER", 0xFFECC300);
        colorMap.put("GLASS", 0xFF26A69A);
        colorMap.put("RESIDUAL", 0xFFE53935);
        colorMap.put("ECOMOBILE", 0xFF81D4FA);
        colorMap.put("CENTER", 0xFFFFB74D);
    }

    private void setupSearch(WasteViewModel wasteVM) {
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                Object selected = spinnerContainers.getSelectedItem();
                String category = selected != null ? selected.toString() : "ALL";
                wasteVM.filterWaste(s.toString(), category);
            }
        });

        spinnerContainers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                wasteVM.filterWaste(inputSearch.getText().toString(), parent.getItemAtPosition(position).toString());
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}
