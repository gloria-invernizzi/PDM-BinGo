package com.application.bingo.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Notification {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    private long notificationTime;
    private String wasteType;
    private int repeatWeeks;

    public Notification(long notificationTime, String wasteType, int repeatWeeks) {
        this.notificationTime = notificationTime;
        this.wasteType = wasteType;
        this.repeatWeeks = repeatWeeks;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public long getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(long notificationTime) {
        this.notificationTime = notificationTime;
    }
    public String getWasteType() {
        return wasteType;
    }

    public void setWasteType(String wasteType) {
        this.wasteType = wasteType;
    }

    public int getRepeatWeeks() {
        return repeatWeeks;
    }

    public void setRepeatWeeks(int repeatWeeks) {
        this.repeatWeeks = repeatWeeks;
    }
}
