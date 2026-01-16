package com.application.bingo.ui.home.scan;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.application.bingo.R;
import com.application.bingo.ui.viewmodel.ProductViewModel;
import com.application.bingo.ui.viewmodel.ViewModelFactory;

public class ManualEntryFragment extends Fragment {
    public interface OnManualEntryListener {
        void onGoToResult(Bundle bundle);
    }

    private OnManualEntryListener listener;

    private ProductViewModel productViewModel;

    public void setOnManualEntryListener(OnManualEntryListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        productViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelFactory(requireActivity().getApplication())).get(ProductViewModel.class);

        return inflater.inflate(R.layout.fragment_manual_entry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText barcodeInput = view.findViewById(R.id.barcode_input);
        Button submitButton = view.findViewById(R.id.submit_button);

        submitButton.setOnClickListener(v -> {
                String barcode = barcodeInput.getText().toString().trim();

                if (!TextUtils.isEmpty(barcode)) {
                    productViewModel.updateBarcode(barcode);

                    Bundle bundle = new Bundle();
                    listener.onGoToResult(bundle);
                } else {
                    Toast.makeText(requireContext(), R.string.insert_valid_barcode, Toast.LENGTH_SHORT).show();
                }
            });
    }

}