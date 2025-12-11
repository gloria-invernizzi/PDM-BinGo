// file : app/src/main/java/com/application/bingo/UserDao.java
package com.application.bingo.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

//Queries for retrieving and inserting User data in the database
@Dao
public interface UserDao {
    //onConflictStrategy.ABORT: e.g. duplicate email
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(User user);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User findByEmail(String email);

    /* LIMIT 1: ensures that only one record is returned, even if multiple records match the criteria
        --> we expect email-password combination to be unique*/
    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    User findByEmailAndPassword(String email, String password);

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    User getById(long id);

    @Update
    void update(User user);
    // Nuovo metodo per aggiornare la foto del profilo
    @Query("UPDATE users SET photo_uri = :uri WHERE email = :email")
    void updatePhotoUri(String email, String uri);

}

