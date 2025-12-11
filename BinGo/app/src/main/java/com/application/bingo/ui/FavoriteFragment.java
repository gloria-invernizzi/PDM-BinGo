package com.application.bingo.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.bingo.R;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.bingo.model.ProductApiResponse;
import com.application.bingo.util.adapter.ProductAdapter;
import java.util.ArrayList;
import java.util.List;


public class FavoriteFragment extends Fragment {

    private List<ProductApiResponse> productList;
    private ProductAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        productList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        adapter =
                new ProductAdapter(productList, false,
                        new ProductAdapter.OnItemClickListener() {
                            @Override
                            public void onProductClick(ProductApiResponse product) {

                            }

                            @Override
                            public void onFavoriteButtonPressed(int position) {}
                        });

        recyclerView.setAdapter(adapter);
        return view;
    }

}