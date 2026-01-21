package com.application.bingo.ui.home.favorites;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;

import com.application.bingo.R;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.bingo.model.Result;
import com.application.bingo.model.relation.ProductWithPackagings;
import com.application.bingo.ui.adapter.ProductAdapter;
import com.application.bingo.ui.viewmodel.ProductViewModel;
import com.application.bingo.ui.viewmodel.ViewModelFactory;
import com.application.bingo.util.NetworkUtil;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;


public class FavoriteFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private ProductViewModel productViewModel;
    private CircularProgressIndicator circularProgressIndicator;
    private SearchView searchView;
    private List<ProductWithPackagings> filteredFavoritesProducts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        productViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelFactory(requireActivity().getApplication())).get(ProductViewModel.class);

        filteredFavoritesProducts = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        this.circularProgressIndicator = view.findViewById(R.id.circularProgressIndicator);
        this.recyclerView = view.findViewById(R.id.recyclerView);
        this.searchView = view.findViewById(R.id.search_bar);

        this.recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        // use filtered list by query
        productAdapter = new ProductAdapter(filteredFavoritesProducts, new ProductAdapter.OnItemClickListenerCallback() {
            @Override
            public void onItemClick(ProductWithPackagings product) {
                productViewModel.updateBarcode(product.getProduct().getBarcode(), NetworkUtil.isInternetAvailable(requireContext()));

                Navigation.findNavController(view).navigate(R.id.action_favoriteFragment_to_resultFragment);
            }
            @Override
            public void onFavoriteCheckboxClick(ProductWithPackagings product) {
                product.getProduct().setFavorite(!product.getProduct().isFavorite());

                productViewModel.updateProduct(product.getProduct());
            }
        });
        recyclerView.setAdapter(productAdapter);

        this.setupSearchView(searchView, productViewModel.getSearchQuery());

        productViewModel.getFilteredFavorites().observe(getViewLifecycleOwner(),
                result -> {
                    if (result.isSuccess()) {
                        List<ProductWithPackagings> newFilteredFavorites = ((Result.Success<List<ProductWithPackagings>>) result).getData();

                        this.filteredFavoritesProducts.clear();
                        this.filteredFavoritesProducts.addAll(newFilteredFavorites);

                        productAdapter.notifyDataSetChanged();

                        recyclerView.setVisibility(View.VISIBLE);

                        circularProgressIndicator.setVisibility(View.GONE);
                    } else {
                        Snackbar.make(view, getString(R.string.error_loading_favorites), Snackbar.LENGTH_SHORT).show();
                    }
                })
        ;

        return view;
    }

    private void setupSearchView(SearchView searchView, String initialQuery) {
        searchView.setQuery(initialQuery, false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                productViewModel.setSearchQuery(newText);

                return true;
            }
        });
    }
}