package com.application.bingo.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.application.bingo.model.Notification;

import java.util.List;

/**
 * Data Access Object (DAO) for the 'notification' table.
 */
@Dao
public interface NotificationDao {

    @Insert
    long insert(Notification notification);

    @Delete
    void delete(Notification notification);

    @Query("SELECT * FROM notification")
    List<Notification> getAll();

    /**
     * Retrieves notifications for a specific time range and family ID.
     * Includes notifications with no family ID or those matching the provided one.
     */
    @Query("SELECT * FROM notification WHERE notificationTime BETWEEN :start AND :end AND (family_id IS NULL OR family_id = :familyId)")
    LiveData<List<Notification>> getNotificationsForDay(long start, long end, String familyId);
    
    /**
     * Legacy query for retrieving notifications without a family ID.
     */
    @Query("SELECT * FROM notification WHERE notificationTime BETWEEN :start AND :end AND family_id IS NULL")
    LiveData<List<Notification>> getNotificationsForDayLegacy(long start, long end);

    /**
     * Deletes multiple notifications based on waste type and time range.
     */
    @Query("DELETE FROM notification WHERE wasteType = :wasteType " +
            "AND notificationTime >= :start AND notificationTime <= :end")
    void deleteNotifications(String wasteType, long start, long end);
}
