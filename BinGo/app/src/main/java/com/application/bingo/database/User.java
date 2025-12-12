// file: app/src/main/java/com/application/bingo/User.java
package com.application.bingo.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity; // Define the class as a Room entity
import androidx.room.PrimaryKey; // Specify the primary key for each entity
import androidx.room.Index; // Create indices for faster queries

@Entity(tableName = "users", indices = {@Index(value = {"email"}, unique = true)})
public class User {
    //The primary key field 'id' is auto-generated??
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private String address;
    private String email;
    private String password;
    // Nuovo campo per la foto profilo
    @ColumnInfo(name = "photo_uri")
    private String photoUri;

    // User constructor
    public User(String name, String address, String email, String password) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.password = password;
        this.photoUri = ""; // inizializza sempre

    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    // Getter e setter per la foto
    public String getPhotoUri() { return photoUri; }
    public void setPhotoUri(String photoUri) { this.photoUri = photoUri; }
}