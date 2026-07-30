package com.example.realprojectaistudymentor.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "quiz_attempts")
public class QuizAttemptEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String quizId;
    public int userId;
    public String quizTitle;
    public String subject;
    public int totalQuestions;
    public int correctAnswers;
    public int score;               // percentage 0-100
    public long timeTaken;          // milliseconds
    public long completedAt;
}
