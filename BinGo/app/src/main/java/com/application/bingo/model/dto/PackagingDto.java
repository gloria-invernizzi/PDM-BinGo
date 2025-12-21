package com.application.bingo.model.dto;

import java.util.ArrayList;
import java.util.List;

public class PackagingDto {
    private String shape;
    private String material;

    private int environmentalScoreMaterialScore;

    private List<MaterialDto> translations;

    public PackagingDto() {
        this.translations = new ArrayList<>();
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public int getEnvironmentalScoreMaterialScore() {
        return environmentalScoreMaterialScore;
    }

    public void setEnvironmentalScoreMaterialScore(int environmentalScoreMaterialScore) {
        this.environmentalScoreMaterialScore = environmentalScoreMaterialScore;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }
    public List<MaterialDto> getTranslations() {
        return translations;
    }

    public void setTranslations(List<MaterialDto> translations) {
        this.translations = translations;
    }

    public void addTranslation(MaterialDto translation) {
        if (!this.translations.contains(translation)) {
            this.translations.add(translation);
        }
    }
}
