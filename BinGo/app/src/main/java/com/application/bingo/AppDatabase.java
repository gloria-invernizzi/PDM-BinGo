package com.application.bingo;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DB_NAME = "app-db";

    //Using volatile to ensure visibility of changes to instance across threads (singleton pattern)
    private static volatile AppDatabase instance;

    // Abstract method to get UserDao: a data access object (DAO) for User entity
    public abstract UserDao userDao();

    //Context is variable that provides access to application-specific resources and classes
    public static AppDatabase getInstance(Context ctx) {
        if (instance == null) {
            //using synchronized block to ensure only one thread can access this block at a time
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = androidx.room.Room.databaseBuilder(
                                    ctx.getApplicationContext(),
                                    AppDatabase.class,
                                    DB_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}