package com.example.realprojectaistudymentor.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "quiz_questions")
public class QuizQuestionEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String quizId;           // link tới quiz_attempts.quizId
    public String questionText;     // nội dung câu hỏi
    public String options;          // JSON array: ["A. ...","B. ...","C. ...","D. ..."]
    public int correctIndex;       // đáp án đúng (0-based)
    public int userAnswer;         // đáp án user chọn (-1 nếu bỏ qua)
    public boolean isCorrect;      // user trả lời đúng không
}
