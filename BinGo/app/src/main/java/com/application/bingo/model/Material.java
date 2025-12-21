package com.application.bingo.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.application.bingo.model.dto.MaterialDto;

@Entity(
        tableName = "material",
        foreignKeys = @ForeignKey(
                entity = Packaging.class,
                parentColumns = "material",
                childColumns = "packaging_material",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("packaging_material")}
)
public class Material {
    @PrimaryKey(autoGenerate = true)
    private long uid;

    private String name;
    private String description;
    private String language;
    private boolean empty;

    @ColumnInfo(name = "packaging_material")
    private String packagingMaterial;

    public Material() {
    }
    public Material(MaterialDto dto, String packagingMaterial) {
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.language = dto.getLanguage().languageAsString();
        this.empty = dto.isEmpty();

        this.packagingMaterial = packagingMaterial;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPackagingMaterial() {
        return this.packagingMaterial;
    }

    public void setPackagingMaterial(String packagingMaterial) {
        this.packagingMaterial = packagingMaterial;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }
}
