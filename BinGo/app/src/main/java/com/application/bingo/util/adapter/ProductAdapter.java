package com.application.bingo.util.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.bingo.R;
import com.application.bingo.model.ProductApiResponse;

import java.util.List;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onProductClick(ProductApiResponse product);
        void onFavoriteButtonPressed(int position);
    }

    private int layout;
    private List<ProductApiResponse> productList;
    private boolean heartVisible;
    private Context context;
    private final OnItemClickListener onItemClickListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView textViewName;
        private final TextView textViewBrand;
        private final CheckBox favoriteCheckbox;
        private final ImageView imageView;

        public ViewHolder(View view) {
            super(view);

            textViewName = view.findViewById(R.id.product_name);
            textViewBrand =  view.findViewById(R.id.product_brand);
            favoriteCheckbox = view.findViewById(R.id.favoriteButton);
            imageView = view.findViewById(R.id.imageView);

            favoriteCheckbox.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        public TextView getTextViewName() {
            return textViewName;
        }

        public TextView getTextViewBrand() {
            return textViewBrand;
        }

        public CheckBox getFavoriteCheckbox() {
            return favoriteCheckbox;
        }

        public ImageView getImageView() { return  imageView; }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.favoriteButton) {
                onItemClickListener.onFavoriteButtonPressed(getAdapterPosition());
            } else {
                onItemClickListener.onProductClick(productList.get(getAdapterPosition()));
            }
        }

    }

    public ProductAdapter(List<ProductApiResponse> productList, boolean heartVisible, OnItemClickListener onItemClickListener) {
        this.productList = productList;
        this.heartVisible = heartVisible;
        this.onItemClickListener = onItemClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_product, viewGroup, false);

        if (this.context == null) this.context = viewGroup.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getTextViewName().setText(productList.get(position).getName());
        viewHolder.getTextViewBrand().setText(productList.get(position).getBrand());
        viewHolder.getFavoriteCheckbox().setChecked(productList.get(position).isFavorite());

        if (!heartVisible) {
            viewHolder.getFavoriteCheckbox().setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }
}
