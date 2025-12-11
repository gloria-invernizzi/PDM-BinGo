package com.application.bingo.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.application.bingo.R;
import com.application.bingo.model.WasteItem;

import java.util.List;
import java.util.Map;

public class WasteAdapter extends ArrayAdapter<WasteItem> {

    private final Map<String, Integer> colorMap;
    private final Map<String, String> textToCategoryKey;

    public WasteAdapter(Context context, List<WasteItem> items,
                        Map<String, Integer> colorMap,
                        Map<String, String> textToCategoryKey) {
        super(context, 0, items);
        this.colorMap = colorMap;
        this.textToCategoryKey = textToCategoryKey;
    }


    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(getContext())
                    .inflate(R.layout.waste_item, parent, false);
        }

        WasteItem item = getItem(position);
        View colorBar = view.findViewById(R.id.colorBar);
        TextView textTitle = view.findViewById(R.id.textTitle);
        TextView textCategory = view.findViewById(R.id.textCategory);

        textTitle.setText(item.getTitle());

        String categoryText = item.getCategory();
        Integer color = colorMap.get(textToCategoryKey.get(item.getCategory()));

        textCategory.setText(categoryText);
        if (color != null) {
            textCategory.setTextColor(color);
            colorBar.setBackgroundColor(color);
        }

        return view;
    }
}
