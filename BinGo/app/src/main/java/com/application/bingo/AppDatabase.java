// file: app/src/main/java/com/application/bingo/AppDatabase.java
package com.application.bingo;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DB_NAME = "app-db";

    //Using volatile to ensure visibility of changes to instance across threads (singleton pattern)
    private static volatile AppDatabase instance;

    // Abstract method to get UserDao: a data access object (DAO) for User entity
    public abstract UserDao userDao();

    // Thread pool per operazioni di scrittura in background
    // Executor serve solo per scrittura (insert, update, delete)
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(4);

    //Context is variable that provides access to application-specific resources and classes
    public static AppDatabase getInstance(Context ctx) {
        if (instance == null) {
            //using synchronized block to ensure only one thread can access this block at a time
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
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