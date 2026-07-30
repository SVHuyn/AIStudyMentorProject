package com.example.realprojectaistudymentor.model;

/**
 * Model badge thành tích — user kiếm được khi đạt điều kiện.
 */
public class Badge {

    private String badgeId;
    private String name;
    private String description;
    private String iconEmoji;       // Emojis are displayed on the badge.
    private boolean unlocked;
    private long unlockedAt;

    // Common badge types (predefined)
    public static final String FIRST_QUESTION = "first_question";
    public static final String QUESTION_10 = "question_10";
    public static final String QUESTION_50 = "question_50";
    public static final String QUIZ_MASTER = "quiz_master";
    public static final String STREAK_3 = "streak_3";
    public static final String ALL_ROUNDER = "all_rounder";

    public Badge() {}

    public Badge(String badgeId, String name, String description, String iconEmoji) {
        this.badgeId = badgeId;
        this.name = name;
        this.description = description;
        this.iconEmoji = iconEmoji;
        this.unlocked = false;
    }

    // Getters & Setters
    public String getBadgeId() { return badgeId; }
    public void setBadgeId(String badgeId) { this.badgeId = badgeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIconEmoji() { return iconEmoji; }
    public void setIconEmoji(String iconEmoji) { this.iconEmoji = iconEmoji; }

    public boolean isUnlocked() { return unlocked; }
    public void setUnlocked(boolean unlocked) { this.unlocked = unlocked; }

    public long getUnlockedAt() { return unlockedAt; }
    public void setUnlockedAt(long unlockedAt) { this.unlockedAt = unlockedAt; }
}
