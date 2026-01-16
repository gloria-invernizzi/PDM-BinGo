package com.application.bingo.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.bingo.R;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.bingo.model.Result;
import com.application.bingo.model.relation.ProductWithPackagings;
import com.application.bingo.ui.adapter.ProductAdapter;
import com.application.bingo.ui.viewmodel.ProductViewModel;
import com.application.bingo.ui.viewmodel.ViewModelFactory;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;


public class FavoriteFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<ProductWithPackagings> favoriteProducts;
    private ProductAdapter productAdapter;
    private ProductViewModel productViewModel;
    private CircularProgressIndicator circularProgressIndicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        productViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelFactory(requireActivity().getApplication())).get(ProductViewModel.class);

        favoriteProducts = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        circularProgressIndicator = view.findViewById(R.id.circularProgressIndicator);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        productAdapter = new ProductAdapter(favoriteProducts, true);

        recyclerView.setAdapter(productAdapter);

        productViewModel.getFavoriteProductsLiveData().observe(getViewLifecycleOwner(),
                result -> {
                    if (result.isSuccess()) {
                        this.favoriteProducts.clear();
                        this.favoriteProducts.addAll(((Result.Success<List<ProductWithPackagings>>) result).getData());

                        recyclerView.setVisibility(View.VISIBLE);

                        circularProgressIndicator.setVisibility(View.GONE);
                    } else {
                        Snackbar.make(view, getString(R.string.error_loading_favorites), Snackbar.LENGTH_SHORT).show();

                    }
                })
        ;

        return view;
    }
}