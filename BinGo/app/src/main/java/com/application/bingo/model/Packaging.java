package com.application.bingo.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.application.bingo.model.dto.PackagingDto;

@Entity(
        tableName = "packaging",
        foreignKeys = @ForeignKey(
                entity = Product.class,
                parentColumns = "barcode",
                childColumns = "product_barcode",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("product_barcode")}
)
public class Packaging {
    // ecoscore_data -> adjustment -> packaging
    private String shape;
    @PrimaryKey
    @NonNull
    private String material = "";

    @ColumnInfo(name = "environmental_score_material_score")
    private int environmentalScoreMaterialScore;

    @ColumnInfo(name = "product_barcode")
    private String productBarcode;

    public Packaging() {
    }

    public Packaging (PackagingDto dto, String productBarcode) {
        this.shape = dto.getShape();
        this.material = dto.getMaterial();
        this.environmentalScoreMaterialScore = dto.getEnvironmentalScoreMaterialScore();

        this.productBarcode = productBarcode;

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

    public String getProductBarcode() {
        return productBarcode;
    }

    public void setProductBarcode(String productBarcode) {
        this.productBarcode = productBarcode;
    }
}
