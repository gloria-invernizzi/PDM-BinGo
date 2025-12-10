package com.application.bingo.model;

public class PackagingDto {
    // ecoscore_data -> adjustment -> packaging
    private String shape;
    private String material;
    private String materialName;
    private String materialDescription;
    private int environmentalScoreMaterialScore;

    public String getShape() {
        return shape;
    }

    public PackagingDto setShape(String shape) {
        this.shape = shape;

        return this;
    }

    public String getMaterialName() {
        return materialName;
    }

    public PackagingDto setMaterialName(String materialName) {
        this.materialName = materialName;

        return this;
    }

    public int getEnvironmentalScoreMaterialScore() {
        return environmentalScoreMaterialScore;
    }

    public PackagingDto setEnvironmentalScoreMaterialScore(int environmentalScoreMaterialScore) {
        this.environmentalScoreMaterialScore = environmentalScoreMaterialScore;

        return this;
    }

    public String getMaterial() {
        return material;
    }

    public PackagingDto setMaterial(String material) {
        this.material = material;

        return this;
    }

    public String getMaterialDescription() {
        return materialDescription;
    }

    public PackagingDto setMaterialDescription(String materialDescription) {
        this.materialDescription = materialDescription;

        return this;
    }
}
