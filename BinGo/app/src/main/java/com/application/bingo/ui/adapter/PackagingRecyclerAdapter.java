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

    private List<PackagingWithTranslations> packagings;

    public PackagingRecyclerAdapter(List<PackagingWithTranslations> packagings) {
        this.packagings = packagings;
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
                .inflate(R.layout.packaging_item, viewGroup, false);

        return new PackagingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PackagingViewHolder holder, int position) {
        if (packagings.isEmpty()) {
            holder.description.setText(holder.itemView.getContext().getString(R.string.no_packaging_details));

            return;
        }

        PackagingWithTranslations packaging = packagings.get(position);

        Material translation = packaging.getTranslations().stream()
                //TODO: filter by the language defined on the application
                .filter(trans -> trans.getLanguage().equalsIgnoreCase(Language.ITA.languageAsString()))
                .findFirst()
                .orElse(null);

        if (null != translation) {
            holder.name.setText(translation.getName());
            holder.description.setText(translation.getDescription());
        } else {
            holder.name.setText('1');
            holder.description.setText('2');
        }
    }

    @Override
    public int getItemCount() {
        // Always return at least 1 element, for displaying error
        return packagings.isEmpty() ? 1 : packagings.size();
    }
}
