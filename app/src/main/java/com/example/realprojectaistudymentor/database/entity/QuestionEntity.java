package com.example.realprojectaistudymentor.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "questions")
public class QuestionEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int userId;         // ai hỏi
    public String content;
    public String type;        // "text" hoặc "image"
    public String subject;
    public String difficultyLevel;
    public String imageUrl;    // null nếu là TextQuestion
    public long timestamp;
}
