package com.application.bingo.database;

import androidx.lifecycle.LiveData;
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

    // Query modificata per includere familyId
    @Query("SELECT * FROM notification WHERE notificationTime BETWEEN :start AND :end AND (family_id IS NULL OR family_id = :familyId)")
    LiveData<List<Notification>> getNotificationsForDay(long start, long end, String familyId);
    
    // Vecchia query mantenuta per retrocompatibilità (se familyId è null)
    @Query("SELECT * FROM notification WHERE notificationTime BETWEEN :start AND :end AND family_id IS NULL")
    LiveData<List<Notification>> getNotificationsForDayLegacy(long start, long end);


    @Query("DELETE FROM notification WHERE wasteType = :wasteType " +
            "AND notificationTime >= :start AND notificationTime <= :end")
    void deleteNotifications(String wasteType, long start, long end);
}
