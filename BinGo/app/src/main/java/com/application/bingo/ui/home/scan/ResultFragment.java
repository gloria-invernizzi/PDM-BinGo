package com.application.bingo.ui.home.scan;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.application.bingo.R;
import com.application.bingo.model.Result;
import com.application.bingo.model.dto.ProductWithPackagingWithTranslation;
import com.application.bingo.ui.adapter.PackagingRecyclerAdapter;
import com.application.bingo.ui.viewmodel.ProductViewModel;
import com.application.bingo.ui.viewmodel.SettingsViewModel;
import com.application.bingo.ui.viewmodel.ViewModelFactory;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ResultFragment extends Fragment {

    private TextView productName, productBrand, productBarcode, recyclingTitle;
    private CheckBox favoriteCheckbox;
    private ImageView productImage;
    private RecyclerView packagingRecyclerView;
    private ProgressBar loadingSpinner;
    private ProductViewModel productViewModel;
    private SettingsViewModel settingsViewModel;
    private ProductWithPackagingWithTranslation productWithPackagingWithTranslations;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewModelFactory viewModelFactory = new ViewModelFactory(requireActivity().getApplication());

        productViewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(ProductViewModel.class);
        settingsViewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(SettingsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_result, container, false);

        String barcode = productViewModel.getBarcodeLiveData().getValue();

        productName = view.findViewById(R.id.product_name);
        productBrand = view.findViewById(R.id.product_brand);
        productBarcode = view.findViewById(R.id.product_barcode);
        productImage = view.findViewById(R.id.product_image);
        favoriteCheckbox = view.findViewById(R.id.favorite_checkbox);
        recyclingTitle = view.findViewById(R.id.recycling_title);
        packagingRecyclerView = view.findViewById(R.id.packaging_material_container);

        productBarcode.setText(getString(R.string.barcode, barcode));

        packagingRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        loadingSpinner = view.findViewById(R.id.loading_spinner);
        loadingSpinner.setVisibility(View.VISIBLE);

        productViewModel.getProductLiveData(barcode).observe(getViewLifecycleOwner(),
                result -> {
                    if (result.isSuccess()) {
                        this.productWithPackagingWithTranslations = ((Result.Success<ProductWithPackagingWithTranslation>) result).getData();

                        this.productName.setText(productWithPackagingWithTranslations.getProduct().getName());
                        this.productBrand.setText(getString(R.string.brand, productWithPackagingWithTranslations.getProduct().getBrand()));
                        this.favoriteCheckbox.setChecked(productWithPackagingWithTranslations.getProduct().isFavorite());

                        this.loadingSpinner.setVisibility(View.GONE);
                        this.favoriteCheckbox.setVisibility(View.VISIBLE);

                        this.packagingRecyclerView.setAdapter(new PackagingRecyclerAdapter(productWithPackagingWithTranslations.getPackagings(), settingsViewModel.getLanguage()));

                        Glide.with(requireContext())
                            .load((null != productWithPackagingWithTranslations.getProduct().getImageUrl() && !productWithPackagingWithTranslations.getProduct().getImageUrl().isEmpty())
                                    ? productWithPackagingWithTranslations.getProduct().getImageUrl()
                                    : R.drawable.product_not_found
                            )
                            .placeholder(R.drawable.product_not_found)
                            .into(productImage)
                        ;

                        favoriteCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                                productWithPackagingWithTranslations.getProduct().setFavorite(isChecked);

                                if (isChecked) {
                                    productViewModel.addToFavorites(productWithPackagingWithTranslations);
                                } else {
                                    productViewModel.removeFromFavorites(productWithPackagingWithTranslations);
                                }
                            }
                        });

                        // Toast.makeText(requireContext(), R.string.result_success, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.request_error, result.toString()), Toast.LENGTH_SHORT).show();

                        this.productName.setText(R.string.result_failed);
                        this.productBrand.setText(" ");
                        this.recyclingTitle.setText(" ");

                        this.packagingRecyclerView.setAdapter(new PackagingRecyclerAdapter(new ArrayList<>(), settingsViewModel.getLanguage()));

                        this.favoriteCheckbox.setVisibility(View.INVISIBLE);
                        this.loadingSpinner.setVisibility(View.GONE);

                        Glide.with(requireContext())
                            .load(R.drawable.product_not_found)
                            .into(productImage)
                        ;
                    }
                });

        return view;
    }
}