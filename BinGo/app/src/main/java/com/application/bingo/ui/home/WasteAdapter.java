package com.application.bingo.ui.home;

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

public class WasteAdapter extends ArrayAdapter<WasteItem> {

    public WasteAdapter(Context context, List<WasteItem> items) {
        super(context, 0, items);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_item_waste, parent, false);
        }

        WasteItem item = getItem(position);

        View colorBar = convertView.findViewById(R.id.colorBar);
        TextView textTitle = convertView.findViewById(R.id.textTitle);
        TextView textCategory = convertView.findViewById(R.id.textCategory);

        if (item != null) {
            textTitle.setText(item.title);
            textCategory.setText(item.category);
            textCategory.setTextColor(item.color);
            colorBar.setBackgroundColor(item.color);
        }

        return convertView;
    }
}
