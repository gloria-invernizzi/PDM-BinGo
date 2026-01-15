package com.application.bingo.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "material",
        foreignKeys = @ForeignKey(
                entity = Packaging.class,
                parentColumns = "uid",
                childColumns = "packaging_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("packaging_id")}
)
public class Material {
    @PrimaryKey(autoGenerate = true)
    private long uid;
    private String name;
    private String description;
    private String language;
    private boolean empty;

    @ColumnInfo(name = "packaging_id")
    private String packagingId;

    public Material() {
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

    public String getPackagingId() {
        return this.packagingId;
    }

    public void setPackagingId(String packagingId) {
        this.packagingId = packagingId;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }
}
