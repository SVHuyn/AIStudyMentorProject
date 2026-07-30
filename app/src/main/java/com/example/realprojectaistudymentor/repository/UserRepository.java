package com.example.realprojectaistudymentor.repository;

import android.content.Context;

import com.example.realprojectaistudymentor.database.AppDatabase;
import com.example.realprojectaistudymentor.database.dao.UserDao;
import com.example.realprojectaistudymentor.database.entity.UserEntity;
import com.example.realprojectaistudymentor.utils.Helper;
import com.example.realprojectaistudymentor.repository.LeaderboardRepository;

import java.util.List;

public class UserRepository {

    private UserDao userDao;

    public UserRepository(Context context) {
        userDao = AppDatabase.getInstance(context).userDao();
    }

    // Đăng ký — trả về id mới, -1 nếu email đã tồn tại
    public long register(String email, String password, String fullName) {
        if (userDao.getByEmail(email) != null) return -1; // email trùng
        UserEntity entity = new UserEntity();
        entity.email     = email;
        entity.password  = Helper.hashPassword(password); // hash trước khi lưu
        entity.fullName  = fullName;
        entity.xpPoints  = 0;
        entity.level     = 1;
        entity.createdAt = System.currentTimeMillis();
        return userDao.insert(entity);
    }

    // Đăng nhập — trả về UserEntity nếu đúng, null nếu sai
    public UserEntity login(String email, String password) {
        return userDao.login(email, Helper.hashPassword(password));
    }

    // Lưu preferences sau onboarding
    public void savePreferences(int userId, String level, List<String> subjects, String style) {
        String subjectsStr = String.join(",", subjects); // "Math,Physics"
        userDao.updatePreferences(userId, level, subjectsStr, style);
    }

    // Lấy thông tin user
    public UserEntity getById(int userId) {
        return userDao.getById(userId);
    }

    // Cộng XP — cập nhật Room + sync lên Firebase Leaderboard
    public void addXp(int userId, int xp) {
        userDao.addXp(userId, xp);

        // Sync lên Firebase
        UserEntity user = userDao.getById(userId);
        if (user != null) {
            // Tính level mới từ XP (mỗi 100 XP = 1 level)
            int newLevel = Math.max(1, (user.xpPoints + xp) / 100 + 1);
            LeaderboardRepository leaderboardRepo = new LeaderboardRepository();
            leaderboardRepo.syncUser(user.email, user.fullName, user.xpPoints + xp, newLevel);
        }
    }

    // Lưu đường dẫn avatar
    public void saveAvatar(int userId, String avatarPath) {
        userDao.updateAvatar(userId, avatarPath);
    }
}
