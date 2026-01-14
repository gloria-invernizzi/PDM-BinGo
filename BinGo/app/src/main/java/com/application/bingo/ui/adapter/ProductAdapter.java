package com.application.bingo.ui.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.bingo.R;
import com.application.bingo.model.relation.ProductWithPackagings;
import com.application.bingo.model.dto.ProductDto;
import com.bumptech.glide.Glide;

import java.util.List;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onProductClick(ProductWithPackagings product);
        void onFavoriteButtonPressed(int position);
    }

    private List<ProductDto> products;
    private boolean heartVisible;
    private Context context;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewName;
        private final TextView textViewBrand;
        private final CheckBox favoriteCheckbox;
        private final ImageView imageView;

        public ViewHolder(View view) {
            super(view);

            textViewName = view.findViewById(R.id.product_name);
            textViewBrand =  view.findViewById(R.id.product_brand);
            favoriteCheckbox = view.findViewById(R.id.is_favorite);
            imageView = view.findViewById(R.id.product_image);

            /* favoriteCheckbox.setOnClickListener(this);
            view.setOnClickListener(this);*/
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

        /* @Override
        public void onClick(View v) {
            if (v.getId() == R.id.favoriteButton) {
                onItemClickListener.onFavoriteButtonPressed(getAdapterPosition());
            } else {
                onItemClickListener.onProductClick(products.get(getAdapterPosition()));
            }
        }*/

    }

    public ProductAdapter(List<ProductDto> products, boolean heartVisible) {
        this.products = products;
        this.heartVisible = heartVisible;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.product_item, viewGroup, false);

        if (this.context == null) this.context = viewGroup.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        ProductDto product = products.get(position);

        viewHolder.getTextViewName().setText(product.getName());
        viewHolder.getTextViewBrand().setText(product.getBrand());
        viewHolder.getFavoriteCheckbox().setChecked(product.isFavorite());

        if (!product.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(product.getImageUrl())
                    .placeholder(new ColorDrawable(R.drawable.product_not_found))
                    .into(viewHolder.getImageView())
            ;
        }

        if (!heartVisible) {
            viewHolder.getFavoriteCheckbox().setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return products.size();
    }
}
