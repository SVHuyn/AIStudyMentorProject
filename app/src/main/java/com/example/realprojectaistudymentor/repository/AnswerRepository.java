package com.example.realprojectaistudymentor.repository;

import android.content.Context;

import com.example.realprojectaistudymentor.database.AppDatabase;
import com.example.realprojectaistudymentor.database.dao.AnswerDao;
import com.example.realprojectaistudymentor.database.entity.AnswerEntity;
import com.example.realprojectaistudymentor.model.Answer;

public class AnswerRepository {

    private AnswerDao answerDao;

    public AnswerRepository(Context context) {
        answerDao = AppDatabase.getInstance(context).answerDao();
    }

    // Lưu câu trả lời từ AI vào DB
    public void save(Answer answer, int questionId) {
        AnswerEntity entity = new AnswerEntity();
        entity.questionId = questionId;
        entity.content = answer.getContent();
        entity.stepByStepExplanation = answer.getStepByStepExplanation();
        entity.alternativeApproach = answer.getAlternativeApproach();
        entity.keyConceptsSummary = answer.getKeyConceptsSummary();
        entity.commonMistakes = answer.getCommonMistakes();
        entity.timestamp = System.currentTimeMillis();
        answerDao.insert(entity);
    }

    // Lấy câu trả lời theo questionId
    public AnswerEntity getByQuestionId(int questionId) {
        return answerDao.getByQuestionId(questionId);
    }

    // Xoá câu trả lời
    public void deleteByQuestionId(int questionId) {
        answerDao.deleteByQuestionId(questionId);
    }
}
