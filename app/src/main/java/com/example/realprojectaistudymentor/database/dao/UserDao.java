package com.example.realprojectaistudymentor.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.realprojectaistudymentor.database.entity.UserEntity;

@Dao
public interface UserDao {

    @Insert
    long insert(UserEntity user);   // trả về id vừa tạo

    @Update
    void update(UserEntity user);

    @Delete
    void delete(UserEntity user);

    // Đăng nhập — kiểm tra email + password
    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    UserEntity login(String email, String password);

    // Kiểm tra email đã tồn tại chưa (tránh đăng ký trùng)
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    UserEntity getByEmail(String email);

    // Lấy user theo id
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    UserEntity getById(int id);

    // Cập nhật preferences sau onboarding
    @Query("UPDATE users SET educationLevel = :level, preferredSubjects = :subjects, explanationStyle = :style WHERE id = :userId")
    void updatePreferences(int userId, String level, String subjects, String style);

    // Cập nhật XP
    @Query("UPDATE users SET xpPoints = xpPoints + :xp WHERE id = :userId")
    void addXp(int userId, int xp);

    // Cập nhật avatar
    @Query("UPDATE users SET avatarPath = :path WHERE id = :userId")
    void updateAvatar(int userId, String path);

    // Cập nhật trạng thái 2FA
    @Query("UPDATE users SET isTwoFactorEnabled = :enabled WHERE id = :userId")
    void updateTwoFactor(int userId, boolean enabled);
}
