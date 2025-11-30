package com.application.bingo.model;

public class Packaging {
    // ecoscore_data -> adjustment -> packaging
    private Boolean nonRecyclableAndNonBiodegradable;
    private String shape;
    private String material;
    private int environmentalScoreMaterialScore;

    public Boolean getNonRecyclableAndNonBiodegradable() {
        return nonRecyclableAndNonBiodegradable;
    }

    public Packaging setNonRecyclableAndNonBiodegradable(Boolean nonRecyclableAndNonBiodegradable)
    {
        this.nonRecyclableAndNonBiodegradable = nonRecyclableAndNonBiodegradable;

        return this;
    }

    public String getShape() {
        return shape;
    }

    public Packaging setShape(String shape) {
        this.shape = shape;

        return this;
    }

    public String getMaterial() {
        return material;
    }

    public Packaging setMaterial(String material) {
        this.material = material;

        return this;
    }

    public int getEnvironmentalScoreMaterialScore() {
        return environmentalScoreMaterialScore;
    }

    public Packaging setEnvironmentalScoreMaterialScore(int environmentalScoreMaterialScore) {
        this.environmentalScoreMaterialScore = environmentalScoreMaterialScore;

        return this;
    }
}
