package com.example.realprojectaistudymentor.database.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "users",
        indices = {@Index(value = "email", unique = true)} // email không được trùng
)
public class UserEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String email;
    public String password;         // nên hash bằng BCrypt trong thực tế
    public String fullName;
    public String educationLevel;
    public String preferredSubjects; // lưu dạng "Math,Physics,Programming"
    public String explanationStyle;
    public int xpPoints;
    public int level;
    public boolean isTwoFactorEnabled;
    public long createdAt;
    public String avatarPath;       // đường dẫn file avatar cục bộ
}
