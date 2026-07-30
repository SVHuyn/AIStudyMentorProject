package com.example.realprojectaistudymentor.repository;

import android.content.Context;

import com.example.realprojectaistudymentor.database.AppDatabase;
import com.example.realprojectaistudymentor.database.dao.QuestionDao;
import com.example.realprojectaistudymentor.database.entity.QuestionEntity;
import com.example.realprojectaistudymentor.utils.Constants;

import java.util.List;

public class QuestionRepository {

    private QuestionDao questionDao;

    public QuestionRepository(Context context) {
        questionDao = AppDatabase.getInstance(context).questionDao();
    }

    // Lưu câu hỏi mới vào DB, trả về id
    public long save(String content, String subject, String type, String imageUrl, int userId) {
        QuestionEntity entity = new QuestionEntity();
        entity.content = content;
        entity.subject = subject;
        entity.type = type;
        entity.imageUrl = imageUrl;
        entity.userId = userId;
        entity.timestamp = System.currentTimeMillis();
        return questionDao.insert(entity);
    }

    // Lấy lịch sử câu hỏi của user
    public List<QuestionEntity> getHistory(int userId) {
        return questionDao.getByUser(userId);
    }

    // Tìm kiếm
    public List<QuestionEntity> search(int userId, String keyword) {
        return questionDao.search(userId, keyword);
    }

    // Lấy theo môn học
    public List<QuestionEntity> getBySubject(int userId, String subject) {
        return questionDao.getBySubject(userId, subject);
    }

    // Lấy 1 câu hỏi
    public QuestionEntity getById(int id) {
        return questionDao.getById(id);
    }

    // Xoá 1 câu hỏi
    public void delete(QuestionEntity question) {
        questionDao.delete(question);
    }
}
