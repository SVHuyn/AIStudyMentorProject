package com.example.realprojectaistudymentor.repository;

import android.content.Context;

import com.example.realprojectaistudymentor.database.AppDatabase;
import com.example.realprojectaistudymentor.database.dao.QuizQuestionDao;
import com.example.realprojectaistudymentor.database.entity.QuizQuestionEntity;

import java.util.List;

public class QuizQuestionRepository {

    private QuizQuestionDao quizQuestionDao;

    public QuizQuestionRepository(Context context) {
        quizQuestionDao = AppDatabase.getInstance(context).quizQuestionDao();
    }

    // Lưu 1 câu hỏi quiz
    public long save(QuizQuestionEntity question) {
        return quizQuestionDao.insert(question);
    }

    // Lưu nhiều câu hỏi quiz 1 lần
    public void saveAll(List<QuizQuestionEntity> questions) {
        quizQuestionDao.insertAll(questions);
    }

    // Lấy tất cả câu hỏi của 1 quiz
    public List<QuizQuestionEntity> getByQuizId(String quizId) {
        return quizQuestionDao.getByQuizId(quizId);
    }

    // Xóa câu hỏi theo quizId
    public void deleteByQuizId(String quizId) {
        quizQuestionDao.deleteByQuizId(quizId);
    }
}
