package com.example.realprojectaistudymentor.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.realprojectaistudymentor.database.entity.AnswerEntity;

@Dao
public interface AnswerDao {

    @Insert
    long insert(AnswerEntity answer);

    @Delete
    void delete(AnswerEntity answer);

    // Lấy câu trả lời theo questionId
    @Query("SELECT * FROM answers WHERE questionId = :questionId LIMIT 1")
    AnswerEntity getByQuestionId(int questionId);

    // Xoá câu trả lời theo questionId
    @Query("DELETE FROM answers WHERE questionId = :questionId")
    void deleteByQuestionId(int questionId);
}
