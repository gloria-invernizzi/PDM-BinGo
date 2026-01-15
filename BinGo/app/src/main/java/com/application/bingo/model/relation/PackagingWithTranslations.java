package com.application.bingo.model.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.application.bingo.model.Material;
import com.application.bingo.model.Packaging;

import java.util.ArrayList;
import java.util.List;

public class PackagingWithTranslations {
    @Embedded
    private Packaging packaging;

    @Relation(
            parentColumn = "material",
            entityColumn = "packaging_id",
            entity = Material.class
    )
    private List<Material> translations = new ArrayList<>();

    public Packaging getPackaging() {
        return packaging;
    }

    public void setPackaging(Packaging packaging) {
        this.packaging = packaging;
    }

    public List<Material> getTranslations() {
        return translations;
    }

    public void setTranslations(List<Material> translations) {
        this.translations = translations;
    }

    public void addTranslation(Material packaging)
    {
        if (translations.contains(packaging)) {
            return;
        }

        translations.add(packaging);
    }
}
