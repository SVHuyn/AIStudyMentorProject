package com.example.realprojectaistudymentor.model;

/**
 * Model cho 1 entry trong Leaderboard (Firebase).
 */
public class LeaderboardEntry {
    private String fullName;
    private int xpPoints;
    private int level;
    private long lastUpdated;

    // Constructor mặc định cần cho Firebase
    public LeaderboardEntry() {}

    public LeaderboardEntry(String fullName, int xpPoints, int level) {
        this.fullName = fullName;
        this.xpPoints = xpPoints;
        this.level = level;
        this.lastUpdated = System.currentTimeMillis();
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public int getXpPoints() { return xpPoints; }
    public void setXpPoints(int xpPoints) { this.xpPoints = xpPoints; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public long getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }
}
