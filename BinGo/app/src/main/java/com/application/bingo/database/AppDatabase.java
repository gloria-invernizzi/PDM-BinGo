package com.application.bingo.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.application.bingo.model.Material;
import com.application.bingo.model.Notification;
import com.application.bingo.model.Packaging;
import com.application.bingo.model.Product;
import com.application.bingo.model.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AppDatabase:
 * The main database for the application, managing entities for User, Notification, Product, Packaging, and Material.
 */
@Database(entities = {User.class, Notification.class, Product.class, Packaging.class, Material.class}, version = 5, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DB_NAME = "app-db";

    // Singleton instance to ensure a single connection across the app
    private static volatile AppDatabase instance;

    // Abstract methods to retrieve DAOs
    public abstract UserDao userDao();
    public abstract NotificationDao notificationDao();
    public abstract ProductDao productDao();
    public abstract PackagingDao packagingDao();
    public abstract MaterialDao materialDao();

    /**
     * Thread pool for background write operations.
     * Used for asynchronous inserts, updates, and deletes.
     */
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(4);

    /**
     * Returns the singleton database instance, creating it if necessary.
     */
    public static AppDatabase getInstance(Context ctx) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    ctx.getApplicationContext(),
                                    AppDatabase.class,
                                    DB_NAME)
                            .addMigrations(MIGRATION_1_2, MIGRATION_3_4, MIGRATION_4_5) // Use migrations to preserve data
                            .build();
                }
            }
        }
        return instance;
    }

    /**
     * Migration from version 1 to 2: Adds 'photo_uri' column to the users table.
     */
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE users ADD COLUMN photo_uri TEXT");
        }
    };

    /**
     * Migration from version 3 to 4: Adds 'family_id' column to the users table.
     */
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE users ADD COLUMN family_id TEXT");
        }
    };

    /**
     * Migration from version 4 to 5: Adds 'family_id' column to the Notification table.
     */
    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Notification ADD COLUMN family_id TEXT");
        }
    };
}
