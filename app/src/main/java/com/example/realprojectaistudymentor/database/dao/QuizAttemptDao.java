package com.example.realprojectaistudymentor.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.realprojectaistudymentor.database.entity.QuizAttemptEntity;

import java.util.List;

@Dao
public interface QuizAttemptDao {

    @Insert
    long insert(QuizAttemptEntity attempt);

    @Delete
    void delete(QuizAttemptEntity attempt);

    // Lấy tất cả lần làm quiz của 1 user, mới nhất lên đầu
    @Query("SELECT * FROM quiz_attempts WHERE userId = :userId ORDER BY completedAt DESC")
    List<QuizAttemptEntity> getByUser(int userId);

    // Lấy 1 lần làm quiz theo id
    @Query("SELECT * FROM quiz_attempts WHERE id = :id")
    QuizAttemptEntity getById(int id);

    // Tính tỷ lệ chính xác trung bình của 1 user
    @Query("SELECT COALESCE(AVG(score), 0) FROM quiz_attempts WHERE userId = :userId")
    int getAverageScore(int userId);

    // Tổng số lần làm quiz
    @Query("SELECT COUNT(*) FROM quiz_attempts WHERE userId = :userId")
    int getTotalQuizzes(int userId);

    // Tổng số câu đúng
    @Query("SELECT COALESCE(SUM(correctAnswers), 0) FROM quiz_attempts WHERE userId = :userId")
    int getTotalCorrectAnswers(int userId);

    // Tổng số câu đã làm
    @Query("SELECT COALESCE(SUM(totalQuestions), 0) FROM quiz_attempts WHERE userId = :userId")
    int getTotalQuestionsAnswered(int userId);

    // Lấy các lần làm quiz theo môn học
    @Query("SELECT * FROM quiz_attempts WHERE userId = :userId AND subject = :subject ORDER BY completedAt DESC")
    List<QuizAttemptEntity> getBySubject(int userId, String subject);

    // Đếm số lần làm quiz theo môn
    @Query("SELECT COUNT(*) FROM quiz_attempts WHERE userId = :userId AND subject = :subject")
    int getCountBySubject(int userId, String subject);
}
