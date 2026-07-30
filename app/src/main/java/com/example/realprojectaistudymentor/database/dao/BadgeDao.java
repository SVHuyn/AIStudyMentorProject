package com.example.realprojectaistudymentor.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.realprojectaistudymentor.database.entity.BadgeEntity;

import java.util.List;

@Dao
public interface BadgeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(BadgeEntity badge);

    @Update
    void update(BadgeEntity badge);

    @Delete
    void delete(BadgeEntity badge);

    // Lấy tất cả badge của 1 user
    @Query("SELECT * FROM badges WHERE userId = :userId ORDER BY earnedAt DESC")
    List<BadgeEntity> getByUser(int userId);

    // Lấy 1 badge theo badgeId của user
    @Query("SELECT * FROM badges WHERE userId = :userId AND badgeId = :badgeId LIMIT 1")
    BadgeEntity getByUserAndBadge(int userId, String badgeId);

    // Đánh dấu đã mở khóa
    @Query("UPDATE badges SET unlocked = 1, unlockedAt = :time WHERE userId = :userId AND badgeId = :badgeId")
    void markUnlocked(int userId, String badgeId, long time);

    // Kiểm tra badge đã mở khóa chưa
    @Query("SELECT EXISTS(SELECT 1 FROM badges WHERE userId = :userId AND badgeId = :badgeId AND unlocked = 1)")
    boolean isUnlocked(int userId, String badgeId);

    // Đếm số badge đã mở khóa
    @Query("SELECT COUNT(*) FROM badges WHERE userId = :userId AND unlocked = 1")
    int getUnlockedCount(int userId);

    // Xoá tất cả badge của user
    @Query("DELETE FROM badges WHERE userId = :userId")
    void deleteAllByUser(int userId);
}
