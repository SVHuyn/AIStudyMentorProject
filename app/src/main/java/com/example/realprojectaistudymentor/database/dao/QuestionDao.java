package com.example.realprojectaistudymentor.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.realprojectaistudymentor.database.entity.QuestionEntity;

import java.util.List;

@Dao
public interface QuestionDao {

    @Insert
    long insert(QuestionEntity question);   // trả về id vừa insert

    @Update
    void update(QuestionEntity question);

    @Delete
    void delete(QuestionEntity question);

    // Lấy tất cả câu hỏi của 1 user, mới nhất lên đầu
    @Query("SELECT * FROM questions WHERE userId = :userId ORDER BY timestamp DESC")
    List<QuestionEntity> getByUser(int userId);

    // Lấy 1 câu hỏi theo id
    @Query("SELECT * FROM questions WHERE id = :id")
    QuestionEntity getById(int id);

    // Tìm kiếm theo từ khoá
    @Query("SELECT * FROM questions WHERE userId = :userId AND content LIKE '%' || :keyword || '%' ORDER BY timestamp DESC")
    List<QuestionEntity> search(int userId, String keyword);

    // Lấy theo môn học
    @Query("SELECT * FROM questions WHERE userId = :userId AND subject = :subject ORDER BY timestamp DESC")
    List<QuestionEntity> getBySubject(int userId, String subject);

    // Xoá toàn bộ câu hỏi của 1 user
    @Query("DELETE FROM questions WHERE userId = :userId")
    void deleteAllByUser(int userId);
}
