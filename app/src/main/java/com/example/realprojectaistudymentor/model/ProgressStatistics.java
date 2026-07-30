package com.example.realprojectaistudymentor.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Model thống kê học tập — tổng hợp từ DB để hiển thị trên ProgressFragment.
 */
public class ProgressStatistics {

    private int totalQuestionsAsked;
    private int totalQuizzesTaken;
    private int accuracyRate;               // percentage 0-100
    private Map<String, Integer> subjectBreakdown;  // subject -> số câu hỏi
    private int streakDays;                 // số ngày học liên tiếp
    private String aiInsights;              // nhận xét từ AI hoặc rule-based

    public ProgressStatistics() {
        this.subjectBreakdown = new HashMap<>();
    }

    // Getters & Setters
    public int getTotalQuestionsAsked() { return totalQuestionsAsked; }
    public void setTotalQuestionsAsked(int total) { this.totalQuestionsAsked = total; }

    public int getTotalQuizzesTaken() { return totalQuizzesTaken; }
    public void setTotalQuizzesTaken(int total) { this.totalQuizzesTaken = total; }

    public int getAccuracyRate() { return accuracyRate; }
    public void setAccuracyRate(int rate) { this.accuracyRate = rate; }

    public Map<String, Integer> getSubjectBreakdown() { return subjectBreakdown; }
    public void setSubjectBreakdown(Map<String, Integer> breakdown) { this.subjectBreakdown = breakdown; }

    public int getStreakDays() { return streakDays; }
    public void setStreakDays(int days) { this.streakDays = days; }

    public String getAiInsights() { return aiInsights; }
    public void setAiInsights(String insights) { this.aiInsights = insights; }

    /**
     * Tạo insights rule-based dựa trên dữ liệu thống kê.
     */
    public void generateInsights() {
        StringBuilder sb = new StringBuilder();

        if (totalQuestionsAsked == 0) {
            sb.append("📝 You haven't asked any questions yet. Start asking AI Mentor!");
        } else {
            // Đánh giá accuracy
            if (accuracyRate >= 80) {
                sb.append("🌟 Excellent! Accuracy rate: ").append(accuracyRate)
                  .append("% — you're making great progress!\n");
            } else if (accuracyRate >= 50) {
                sb.append("👍 Good job! Accuracy rate: ").append(accuracyRate)
                  .append("% — keep practicing!\n");
            } else {
                sb.append("💪 Don't give up! Accuracy rate: ").append(accuracyRate)
                  .append("% — review your weak topics.\n");
            }

            // Gợi ý môn cần cải thiện
            if (!subjectBreakdown.isEmpty()) {
                String weakestSubject = null;
                int minCount = Integer.MAX_VALUE;
                for (Map.Entry<String, Integer> entry : subjectBreakdown.entrySet()) {
                    if (entry.getValue() < minCount) {
                        minCount = entry.getValue();
                        weakestSubject = entry.getKey();
                    }
                }
                if (weakestSubject != null) {
                    sb.append("📚 You haven't asked much about \"")
                      .append(weakestSubject)
                      .append("\" — try exploring this topic!");
                }
            }
        }

        this.aiInsights = sb.toString();
    }
}
