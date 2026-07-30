package com.example.realprojectaistudymentor.repository;

import android.content.Context;

import com.example.realprojectaistudymentor.database.AppDatabase;
import com.example.realprojectaistudymentor.database.dao.BadgeDao;
import com.example.realprojectaistudymentor.database.dao.QuestionDao;
import com.example.realprojectaistudymentor.database.dao.QuizAttemptDao;
import com.example.realprojectaistudymentor.database.entity.BadgeEntity;
import com.example.realprojectaistudymentor.model.Badge;
import com.example.realprojectaistudymentor.model.Notification;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository cho badge — lưu, truy vấn, kiểm tra điều kiện mở khóa.
 */
public class BadgeRepository {

    private BadgeDao badgeDao;
    private QuestionDao questionDao;
    private QuizAttemptDao quizAttemptDao;

    public BadgeRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        badgeDao = db.badgeDao();
        questionDao = db.questionDao();
        quizAttemptDao = db.quizAttemptDao();
    }

    // Lưu badge mới
    public long save(BadgeEntity entity) {
        return badgeDao.insert(entity);
    }

    // Lấy tất cả badge của user
    public List<BadgeEntity> getByUser(int userId) {
        return badgeDao.getByUser(userId);
    }

    // Lấy badge theo badgeId
    public BadgeEntity getByUserAndBadge(int userId, String badgeId) {
        return badgeDao.getByUserAndBadge(userId, badgeId);
    }

    // Đánh dấu đã mở khóa
    public void markUnlocked(int userId, String badgeId) {
        badgeDao.markUnlocked(userId, badgeId, System.currentTimeMillis());
    }

    // Kiểm tra đã mở khóa chưa
    public boolean isUnlocked(int userId, String badgeId) {
        return badgeDao.isUnlocked(userId, badgeId);
    }

    // Đếm số badge đã mở khóa
    public int getUnlockedCount(int userId) {
        return badgeDao.getUnlockedCount(userId);
    }

    /**
     * Kiểm tra và award badge mới dựa trên hành động hiện tại.
     * Trả về danh sách notification mới (badge vừa unlock).
     */
    public List<Notification> checkAndAwardBadges(int userId) {
        List<Notification> newNotifications = new ArrayList<>();
        long now = System.currentTimeMillis();

        // Đảm bảo tất cả badge templates đều tồn tại trong DB
        ensureBadgeTemplatesExist(userId);

        // 1. First Question badge
        int questionCount = questionDao.getByUser(userId).size();
        if (questionCount >= 1 && !isUnlocked(userId, Badge.FIRST_QUESTION)) {
            markUnlocked(userId, Badge.FIRST_QUESTION);
            newNotifications.add(new Notification(
                    "🏆 Badge Earned: First Question!",
                    "You asked your first question. Keep going!",
                    "badge"
            ));
        }

        // 2. Question 10 badge
        if (questionCount >= 10 && !isUnlocked(userId, Badge.QUESTION_10)) {
            markUnlocked(userId, Badge.QUESTION_10);
            newNotifications.add(new Notification(
                    "🏆 Badge Earned: 10 Questions!",
                    "You asked 10 questions. You're studying hard!",
                    "badge"
            ));
        }

        // 3. Question 50 badge
        if (questionCount >= 50 && !isUnlocked(userId, Badge.QUESTION_50)) {
            markUnlocked(userId, Badge.QUESTION_50);
            newNotifications.add(new Notification(
                    "🏆 Badge Earned: 50 Questions!",
                    "You asked 50 questions. That's impressive!",
                    "badge"
            ));
        }

        // 4. Quiz Master badge
        int quizCount = quizAttemptDao.getTotalQuizzes(userId);
        int avgScore = quizAttemptDao.getAverageScore(userId);
        if (quizCount >= 5 && avgScore >= 70 && !isUnlocked(userId, Badge.QUIZ_MASTER)) {
            markUnlocked(userId, Badge.QUIZ_MASTER);
            newNotifications.add(new Notification(
                    "🏆 Badge Earned: Quiz Master!",
                    "You completed 5 quizzes with avg score ≥ 70%. Excellent!",
                    "badge"
            ));
        }

        return newNotifications;
    }

    /**
     * Tạo sẵn các badge template trong DB nếu chưa có.
     */
    private void ensureBadgeTemplatesExist(int userId) {
        String[][] templates = {
                {Badge.FIRST_QUESTION, "First Question", "Asked your first question", "❓"},
                {Badge.QUESTION_10, "Curious Mind", "Asked 10 questions", "🔟"},
                {Badge.QUESTION_50, "Knowledge Seeker", "Asked 50 questions", "📚"},
                {Badge.QUIZ_MASTER, "Quiz Master", "Avg score ≥ 70% in 5 quizzes", "🧠"},
                {Badge.STREAK_3, "On Fire!", "Studied 3 days in a row", "🔥"},
                {Badge.ALL_ROUNDER, "All-Rounder", "Studied ≥ 3 different subjects", "🌍"},
        };

        for (String[] t : templates) {
            if (badgeDao.getByUserAndBadge(userId, t[0]) == null) {
                BadgeEntity entity = new BadgeEntity();
                entity.userId = userId;
                entity.badgeId = t[0];
                entity.badgeName = t[1];
                entity.description = t[2];
                entity.iconEmoji = t[3];
                entity.unlocked = false;
                entity.earnedAt = System.currentTimeMillis();
                badgeDao.insert(entity);
            }
        }
    }
}
