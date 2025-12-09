package com.application.bingo.util.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.application.bingo.model.Notification;

import java.util.List;
@Dao
public interface NotificationDao {

    @Insert
    long insert(Notification notification);

    @Delete
    void delete(Notification notification);

    @Query("SELECT * FROM notification")
    List<Notification> getAll();
}

