package com.example.realprojectaistudymentor.repository;

import android.content.Context;

import com.example.realprojectaistudymentor.database.AppDatabase;
import com.example.realprojectaistudymentor.database.dao.QuizAttemptDao;
import com.example.realprojectaistudymentor.database.entity.QuizAttemptEntity;
import com.example.realprojectaistudymentor.model.ProgressStatistics;
import com.example.realprojectaistudymentor.model.QuizAttempt;
import com.example.realprojectaistudymentor.utils.Helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizRepository {

    private QuizAttemptDao quizAttemptDao;

    public QuizRepository(Context context) {
        quizAttemptDao = AppDatabase.getInstance(context).quizAttemptDao();
    }

    // Lưu 1 lần làm quiz
    public long saveAttempt(QuizAttempt attempt) {
        QuizAttemptEntity entity = new QuizAttemptEntity();
        entity.quizId = attempt.getQuizId();
        entity.userId = attempt.getUserId();
        entity.quizTitle = attempt.getQuizTitle();
        entity.subject = attempt.getSubject();
        entity.totalQuestions = attempt.getTotalQuestions();
        entity.correctAnswers = attempt.getCorrectAnswers();
        entity.score = attempt.getScore();
        entity.timeTaken = attempt.getTimeTaken();
        entity.completedAt = attempt.getCompletedAt();
        return quizAttemptDao.insert(entity);
    }

    // Lấy lịch sử làm quiz của user
    public List<QuizAttemptEntity> getAttempts(int userId) {
        return quizAttemptDao.getByUser(userId);
    }

    // Lấy 1 lần làm quiz
    public QuizAttemptEntity getAttemptById(int id) {
        return quizAttemptDao.getById(id);
    }

    // Tính tỷ lệ chính xác trung bình
    public int getAccuracyRate(int userId) {
        return quizAttemptDao.getAverageScore(userId);
    }

    // Tổng số lần làm quiz
    public int getTotalQuizzes(int userId) {
        return quizAttemptDao.getTotalQuizzes(userId);
    }

    /**
     * Tổng hợp ProgressStatistics cho user.
     * Dùng QuestionDao (qua QuestionRepository) cho totalQuestionsAsked,
     * và QuizAttemptDao cho quiz stats.
     */
    public ProgressStatistics getStatistics(int userId, QuestionRepository questionRepo) {
        ProgressStatistics stats = new ProgressStatistics();

        // Tổng số câu hỏi đã hỏi
        int totalQuestions = questionRepo.getHistory(userId).size();
        stats.setTotalQuestionsAsked(totalQuestions);

        // Quiz stats
        int totalQuizzes = quizAttemptDao.getTotalQuizzes(userId);
        stats.setTotalQuizzesTaken(totalQuizzes);
        stats.setAccuracyRate(quizAttemptDao.getAverageScore(userId));

        // Subject breakdown — đếm câu hỏi theo môn
        Map<String, Integer> subjectMap = new HashMap<>();
        List<QuizAttemptEntity> attempts = quizAttemptDao.getByUser(userId);
        for (QuizAttemptEntity attempt : attempts) {
            String subject = attempt.subject;
            if (subject != null && !subject.isEmpty()) {
                subjectMap.put(subject, subjectMap.getOrDefault(subject, 0) + 1);
            }
        }
        // Thêm data từ câu hỏi đã hỏi
        List<com.example.realprojectaistudymentor.database.entity.QuestionEntity> questions =
                questionRepo.getHistory(userId);
        for (com.example.realprojectaistudymentor.database.entity.QuestionEntity q : questions) {
            String subject = q.subject;
            if (subject != null && !subject.isEmpty()) {
                subjectMap.put(subject, subjectMap.getOrDefault(subject, 0) + 1);
            }
        }
        stats.setSubjectBreakdown(subjectMap);

        // Tạo insights
        stats.generateInsights();

        return stats;
    }

    // Xoá 1 lần làm quiz
    public void deleteAttempt(QuizAttemptEntity attempt) {
        quizAttemptDao.delete(attempt);
    }
}
