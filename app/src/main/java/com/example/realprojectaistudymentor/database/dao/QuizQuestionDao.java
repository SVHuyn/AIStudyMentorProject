package com.example.realprojectaistudymentor.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.realprojectaistudymentor.database.entity.QuizQuestionEntity;

import java.util.List;

@Dao
public interface QuizQuestionDao {

    @Insert
    long insert(QuizQuestionEntity question);

    @Insert
    void insertAll(List<QuizQuestionEntity> questions);

    @Delete
    void delete(QuizQuestionEntity question);

    // Lấy tất cả câu hỏi của 1 quiz theo quizId
    @Query("SELECT * FROM quiz_questions WHERE quizId = :quizId ORDER BY id ASC")
    List<QuizQuestionEntity> getByQuizId(String quizId);

    // Xóa tất cả câu hỏi của 1 quiz
    @Query("DELETE FROM quiz_questions WHERE quizId = :quizId")
    void deleteByQuizId(String quizId);

    // Xóa tất cả câu hỏi của 1 user (qua quiz_attempts)
    @Query("DELETE FROM quiz_questions WHERE quizId IN (SELECT quizId FROM quiz_attempts WHERE userId = :userId)")
    void deleteAllByUser(int userId);
}
