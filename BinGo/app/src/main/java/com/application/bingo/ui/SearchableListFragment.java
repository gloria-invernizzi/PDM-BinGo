package com.application.bingo.ui;

import android.util.Log;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class SearchableListFragment<T> extends Fragment {

    protected List<T> fullList = new ArrayList<>();
    protected List<T> filteredList = new ArrayList<>();

    protected abstract boolean filterItemCondition(T item, String query);

    protected abstract void notifyDataSetChanged();

    protected void setupSearchView(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterByQuery(newText);

                return true;
            }
        });
    }

    public void filterByQuery(String text) {
        this.filteredList.clear();
        Log.e("SEARCH", text);

        if (text.isEmpty()) {
            this.filteredList.addAll(fullList);
        } else {
            String query = text.toLowerCase().trim();

            for (T item:
                 this.fullList) {
                if (filterItemCondition(item, query)) {
                    this.filteredList.add(item);
                }
            }
        }

        notifyDataSetChanged();
    }
}
