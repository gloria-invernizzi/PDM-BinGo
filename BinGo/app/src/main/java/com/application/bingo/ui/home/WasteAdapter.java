package com.application.bingo.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.application.bingo.R;

import java.util.List;
import java.util.Map;

public class WasteAdapter extends ArrayAdapter<String> {

    private Map<String, Integer> iconMap;
    private Map<String, Integer> colorMap;

    public WasteAdapter(Context context, List<String> items,
                        Map<String, Integer> iconMap,
                        Map<String, Integer> colorMap) {
        super(context, 0, items);
        this.iconMap = iconMap;
        this.colorMap = colorMap;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_item_waste, parent, false);
        }

        String item = getItem(position);
        String category = item.split(" → ")[1];

        ImageView icon = convertView.findViewById(R.id.icon);
        TextView text = convertView.findViewById(R.id.text);

        text.setText(item);

        if (iconMap.containsKey(category))
            icon.setImageResource(iconMap.get(category));

        if (colorMap.containsKey(category))
            convertView.setBackgroundColor(colorMap.get(category));

        return convertView;
    }
}
