package com.application.bingo.model;

public class WasteItem {
    private final String title;
    private final String category;

    public WasteItem(String title, String category) {
        this.title = title;
        this.category = category;
    }

    public String getTitle() { return title; }
    public String getCategory() { return category; }

}
