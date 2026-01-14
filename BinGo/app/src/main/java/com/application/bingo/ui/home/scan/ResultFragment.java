package com.application.bingo.ui.home.scan;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
import com.application.bingo.model.dto.ProductDto;
import com.application.bingo.ui.adapter.PackagingRecyclerAdapter;
import com.application.bingo.ui.viewmodel.ProductViewModel;
import com.application.bingo.ui.viewmodel.ViewModelFactory;
import com.bumptech.glide.Glide;

public class ResultFragment extends Fragment {

    private TextView productName, productBrand, productBarcode, textResult, recyclingTitle;
    private CheckBox favoriteCheckbox;
    private ImageView productImage;
    private RecyclerView packagingRecyclerView;
    private ProgressBar loadingSpinner;
    private ProductViewModel productViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        productViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelFactory(requireActivity().getApplication())).get(ProductViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_result, container, false);

        productName = view.findViewById(R.id.product_name);
        productBrand = view.findViewById(R.id.product_brand);
        productBarcode = view.findViewById(R.id.product_barcode);
        textResult = view.findViewById(R.id.text_result);
        productImage = view.findViewById(R.id.product_image);
        favoriteCheckbox = view.findViewById(R.id.favorite_checkbox);
        recyclingTitle = view.findViewById(R.id.recycling_title);
        packagingRecyclerView = view.findViewById(R.id.packaging_material_container);

        packagingRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        String barcode = getArguments().getString("barcode");
        productBarcode.setText(getString(R.string.barcode, barcode));

        productViewModel.updateBarcode(barcode);

        loadingSpinner = view.findViewById(R.id.loading_spinner);
        loadingSpinner.setVisibility(View.VISIBLE);

        // TODO: work with last update ??
        productViewModel.getProductLiveData(barcode, System.currentTimeMillis()).observe(getViewLifecycleOwner(),
                result -> {
                    if (result.isSuccess()) {
                        ProductDto productDto = ((Result.Success<ProductDto>) result).getData();

                        this.productName.setText(productDto.getName());
                        this.productBrand.setText(getString(R.string.brand, productDto.getBrand()));
                        this.favoriteCheckbox.setChecked(productDto.isFavorite());

                        this.loadingSpinner.setVisibility(View.GONE);

                        this.packagingRecyclerView.setAdapter(new PackagingRecyclerAdapter(productDto.getPackagings()));

                        Glide.with(requireContext())
                            .load((null != productDto.getImageUrl() && !productDto.getImageUrl().isEmpty())
                                    ? productDto.getImageUrl()
                                    : R.drawable.product_not_found
                            )
                            .placeholder(R.drawable.product_not_found)
                            .into(productImage)
                        ;

                        favoriteCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                productDto.setFavorite(isChecked);

                                if (isChecked) {
                                    productViewModel.insertProduct(productDto);

                                    productViewModel.fetchFavoritesProducts();
                                } else {
                                    productViewModel.removeFromFavorites(productDto);
                                }
                            }
                        });

                        Toast.makeText(requireContext(), R.string.result_success, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.request_error, result.toString()), Toast.LENGTH_SHORT).show();

                        textResult.setText(R.string.result_failed);
                        recyclingTitle.setText(" ");

                        loadingSpinner.setVisibility(View.GONE);

                        Glide.with(requireContext())
                            .load(R.drawable.product_not_found)
                            .into(productImage)
                        ;
                    }
                });

        return view;
    }
}