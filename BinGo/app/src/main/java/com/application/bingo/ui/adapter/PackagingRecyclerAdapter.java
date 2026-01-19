package com.application.bingo.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.bingo.R;
import com.application.bingo.constants.Language;
import com.application.bingo.model.Material;
import com.application.bingo.model.relation.PackagingWithTranslations;

import java.util.List;

public class PackagingRecyclerAdapter extends RecyclerView.Adapter<PackagingRecyclerAdapter.PackagingViewHolder> {

    private static final int VIEW_TYPE_PACKAGING = 0;
    private static final int VIEW_TYPE_EMPTY_PACKAGING = 1;

    private List<PackagingWithTranslations> packagings;
    private String language;

    public PackagingRecyclerAdapter(List<PackagingWithTranslations> packagings, String language) {
        this.packagings = packagings;
        this.language = language;
    }

    public static class PackagingViewHolder extends RecyclerView.ViewHolder {
        TextView name, description;
        public PackagingViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.material_name);
            description = itemView.findViewById(R.id.material_description);
        }
    }

    @NonNull
    @Override
    public PackagingViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(VIEW_TYPE_PACKAGING == viewType ? R.layout.packaging_item : R.layout.empty_packaging_item, viewGroup, false);

        return new PackagingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PackagingViewHolder holder, int position) {
        if (VIEW_TYPE_EMPTY_PACKAGING == getItemViewType(position)) {
            return;
        }

        PackagingWithTranslations packaging = packagings.get(position);

        Material translation = packaging.getTranslations().stream()
                .filter(trans -> trans.getLanguage().equalsIgnoreCase(language))
                .findFirst()
                .orElse(null);

        if (null != translation) {
            holder.name.setText(translation.getName());
            holder.description.setText(translation.getDescription());
        } else {
            holder.name.setText(packaging.getPackaging().getMaterial());
            holder.description.setText(R.string.no_material_translation);
        }
    }

    @Override
    public int getItemCount() {
        // Always return at least 1 element, for displaying error
        return packagings.isEmpty() ? 1 : packagings.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (packagings == null || packagings.isEmpty()) {
            return VIEW_TYPE_EMPTY_PACKAGING;
        }
        return VIEW_TYPE_PACKAGING;
    }
}
