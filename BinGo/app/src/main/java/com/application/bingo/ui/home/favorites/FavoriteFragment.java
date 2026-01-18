package com.application.bingo.ui.home.favorites;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;

import com.application.bingo.R;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.bingo.model.Result;
import com.application.bingo.model.relation.ProductWithPackagings;
import com.application.bingo.ui.SearchableListFragment;
import com.application.bingo.ui.adapter.ProductAdapter;
import com.application.bingo.ui.viewmodel.ProductViewModel;
import com.application.bingo.ui.viewmodel.ViewModelFactory;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;


public class FavoriteFragment extends SearchableListFragment<ProductWithPackagings> {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private ProductViewModel productViewModel;
    private CircularProgressIndicator circularProgressIndicator;
    private SearchView searchView;

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
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        this.circularProgressIndicator = view.findViewById(R.id.circularProgressIndicator);
        this.recyclerView = view.findViewById(R.id.recyclerView);
        this.searchView = view.findViewById(R.id.search_bar);

        this.recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        // use filtered list by query
        productAdapter = new ProductAdapter(this.filteredList, new ProductAdapter.OnItemClickListenerCallback() {
            @Override
            public void onItemClick(ProductWithPackagings product) {
                productViewModel.updateBarcode(product.getProduct().getBarcode());

                Navigation.findNavController(view).navigate(R.id.action_favoriteFragment_to_resultFragment);
            }
            @Override
            public void onFavoriteCheckboxClick(ProductWithPackagings product) {
                product.getProduct().setFavorite(!product.getProduct().isFavorite());

                productViewModel.updateProduct(product.getProduct());
            }
        });

        recyclerView.setAdapter(productAdapter);
        setupSearchView(searchView);

        productViewModel.getFavoriteProductsLiveData().observe(getViewLifecycleOwner(),
                result -> {
                    if (result.isSuccess()) {
                        List<ProductWithPackagings> favoritesFullList = ((Result.Success<List<ProductWithPackagings>>) result).getData();

                        this.fullList.clear();
                        this.fullList.addAll(favoritesFullList);

                        filterByQuery(searchView.getQuery().toString());

                        recyclerView.setVisibility(View.VISIBLE);

                        circularProgressIndicator.setVisibility(View.GONE);
                    } else {
                        Snackbar.make(view, getString(R.string.error_loading_favorites), Snackbar.LENGTH_SHORT).show();
                    }
                })
        ;

        return view;
    }

    @Override
    protected boolean filterItemCondition(ProductWithPackagings item, String query) {
        return item.getProduct().getName().toLowerCase().contains(query) ||
                item.getProduct().getBrand().toLowerCase().contains(query) ||
                item.getProduct().getBarcode().toLowerCase().contains(query)
        ;
    }

    @Override
    protected void notifyDataSetChanged() {
        if (productAdapter != null) {
            productAdapter.notifyDataSetChanged();
        }
    }
}