// file: app/src/main/java/com/application/bingo/AppDatabase.java
package com.application.bingo.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.SQLite;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.application.bingo.model.Material;
import com.application.bingo.model.Notification;
import com.application.bingo.model.Packaging;
import com.application.bingo.model.Product;
import com.application.bingo.model.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class, Notification.class, Product.class, Packaging.class, Material.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DB_NAME = "app-db";

    // Using volatile to ensure visibility of changes to instance across threads (singleton pattern)
    private static volatile AppDatabase instance;

    // Abstract method to get UserDao: a data access object (DAO) for User entity
    public abstract UserDao userDao();
    public abstract NotificationDao notificationDao();
    public abstract ProductDao productDao();
    public abstract PackagingDao packagingDao();
    public abstract MaterialDao materialDao();

    // Thread pool per operazioni di scrittura in background
    // Executor serve solo per scrittura (insert, update, delete)
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(4);

    // Context is variable that provides access to application-specific resources and classes
    public static AppDatabase getInstance(Context ctx) {
        if (instance == null) {
            // using synchronized block to ensure only one thread can access this block at a time
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    ctx.getApplicationContext(),
                                    AppDatabase.class,
                                    DB_NAME)
                            .addMigrations(MIGRATION_1_2) // utilizza la migration invece di distruggere il DB
                            .build();
                }
            }
        }
        return instance;
    }

    // Migration dalla versione 1 alla 2: aggiunge la colonna photo_uri alla tabella users
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Aggiunge la colonna photo_uri di tipo TEXT
            database.execSQL("ALTER TABLE users ADD COLUMN photo_uri TEXT");
        }
    };
}
