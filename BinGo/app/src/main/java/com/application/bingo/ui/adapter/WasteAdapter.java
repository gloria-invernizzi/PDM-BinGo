package com.application.bingo.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.bingo.R;
import com.application.bingo.model.WasteItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WasteAdapter extends RecyclerView.Adapter<WasteAdapter.ViewHolder> {

    private final Context context;
    private final Map<String, Integer> colorMap;
    private final Map<String, String> categoryTextMap;
    private final List<WasteItem> items;

    public WasteAdapter(Context context,
                        List<WasteItem> items,
                        Map<String, Integer> colorMap,
                        Map<String, String> categoryTextMap) {
        this.context = context;
        this.items = new ArrayList<>(items);
        this.colorMap = colorMap;
        this.categoryTextMap = categoryTextMap;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        View colorBar;
        TextView textTitle;
        TextView textCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            colorBar = itemView.findViewById(R.id.colorBar);
            textTitle = itemView.findViewById(R.id.textTitle);
            textCategory = itemView.findViewById(R.id.textCategory);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.waste_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WasteItem item = items.get(position);

        holder.textTitle.setText(item.getTitle());

        String categoryKey = item.getCategory();
        holder.textCategory.setText(categoryTextMap.getOrDefault(categoryKey, categoryKey));

        Integer color = colorMap.get(categoryKey);
        if (color != null) {
            holder.textCategory.setTextColor(color);
            holder.colorBar.setBackgroundColor(color);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // --- Metodo per aggiornare la lista ---
    public void updateList(List<WasteItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }
}
