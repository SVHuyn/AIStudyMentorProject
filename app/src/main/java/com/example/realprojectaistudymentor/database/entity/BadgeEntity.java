package com.example.realprojectaistudymentor.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "badges")
public class BadgeEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int userId;
    public String badgeId;
    public String badgeName;
    public String description;
    public String iconEmoji;
    public boolean unlocked;
    public long unlockedAt;
    public long earnedAt;
}
