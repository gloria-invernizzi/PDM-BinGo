package com.application.bingo.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.bingo.R;
import com.application.bingo.model.Product;
import com.application.bingo.model.relation.ProductWithPackagings;
import com.bumptech.glide.Glide;

import java.util.List;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private static final int VIEW_TYPE_PRODUCT = 0;
    private static final int VIEW_TYPE_EMPTY = 1;

    public interface OnItemClickListenerCallback {
        void onItemClick(ProductWithPackagings product);
        void onFavoriteCheckboxClick(ProductWithPackagings product);
    }

    private List<ProductWithPackagings> products;
    private Context context;
    private OnItemClickListenerCallback onItemClickListenerCallback;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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

            if (null != favoriteCheckbox) {
                favoriteCheckbox.setOnClickListener(this);
                view.setOnClickListener(this);
            }
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
            if (v.getId() == R.id.is_favorite) {
                onItemClickListenerCallback.onFavoriteCheckboxClick(products.get(getBindingAdapterPosition()));
            } else {
                onItemClickListenerCallback.onItemClick(products.get(getBindingAdapterPosition()));
            }
        }
    }

    public ProductAdapter(List<ProductWithPackagings> products, OnItemClickListenerCallback onItemClickListenerCallback) {
        this.products = products;
        this.onItemClickListenerCallback = onItemClickListenerCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(VIEW_TYPE_EMPTY == viewType ? R.layout.empty_product_item : R.layout.product_item, viewGroup, false);

        if (this.context == null) this.context = viewGroup.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        if (VIEW_TYPE_EMPTY == getItemViewType(position)) {
            return;
        }

        Product product = this.products.get(position).getProduct();

        viewHolder.getTextViewName().setText(product.getName());
        viewHolder.getTextViewBrand().setText(product.getBrand());
        viewHolder.getFavoriteCheckbox().setChecked(product.isFavorite());

        if (!product.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(product.getImageUrl())
                    .into(viewHolder.getImageView())
            ;
        }
    }


    @Override
    public int getItemCount() {
        return products.isEmpty() ? 1 : products.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (products == null || products.isEmpty()) {
            return VIEW_TYPE_EMPTY;
        }
        return VIEW_TYPE_PRODUCT;
    }
}
