package com.application.bingo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.bingo.R;
import com.application.bingo.model.PackagingDto;

import java.util.List;

public class PackagingRecyclerAdapter extends RecyclerView.Adapter<PackagingRecyclerAdapter.PackagingViewHolder> {

    private List<PackagingDto> packagings;

    public PackagingRecyclerAdapter(List<PackagingDto> packagings) {
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
                .inflate(R.layout.packaging_card, viewGroup, false);

        return new PackagingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PackagingViewHolder holder, int position) {
        if (packagings.isEmpty()) {
            holder.description.setText(holder.itemView.getContext().getString(R.string.no_packaging_details));

            return;
        }

        PackagingDto packaging = packagings.get(position);

        holder.name.setText(packaging.getMaterialName());
        holder.description.setText(packaging.getMaterialDescription());
    }

    @Override
    public int getItemCount() {
        // Always return at least 1 element, for displaying error
        return packagings.isEmpty() ? 1 : packagings.size();
    }
}
