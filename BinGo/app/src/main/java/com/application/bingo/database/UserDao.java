package com.application.bingo.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.application.bingo.model.User;

import java.util.List;

/**
 * Data Access Object (DAO) for the 'users' table.
 * Provides methods for retrieving, inserting, and updating user data in the database.
 */
@Dao
public interface UserDao {

    /**
     * Inserts a new user into the database.
     * Aborts the operation if a conflict occurs (e.g., duplicate email).
     *
     * @param user The user to insert.
     * @return The row ID of the newly inserted user.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(User user);

    /**
     * Finds a user by their email address (case-insensitive).
     *
     * @param email The user's email.
     * @return The User object or null if not found.
     */
    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    User findByEmail(String email);

    /**
     * Finds a user by email and password combination (email case-insensitive).
     * Used for local authentication.
     *
     * @param email    The user's email.
     * @param password The user's password.
     * @return The User object if credentials match, null otherwise.
     */
    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) AND password = :password LIMIT 1")
    User findByEmailAndPassword(String email, String password);

    /**
     * Retrieves a user by their unique database ID.
     */
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    User getById(long id);

    /**
     * Updates an existing user's details.
     */
    @Update
    void update(User user);

    /**
     * Updates only the profile photo URI for a specific user.
     */
    @Query("UPDATE users SET photo_uri = :uri WHERE LOWER(email) = LOWER(:email)")
    void updatePhotoUri(String email, String uri);

    /**
     * Retrieves all users associated with a specific family ID.
     */
    @Query("SELECT * FROM users WHERE family_id = :familyId")
    List<User> findByFamilyId(String familyId);

    /**
     * Updates the family ID for a specific user.
     */
    @Query("UPDATE users SET family_id = :familyId WHERE LOWER(email) = LOWER(:email)")
    void updateFamilyId(String email, String familyId);

    /**
     * Deletes a user record by email.
     */
    @Query("DELETE FROM users WHERE LOWER(email) = LOWER(:email)")
    void deleteByEmail(String email);

}
