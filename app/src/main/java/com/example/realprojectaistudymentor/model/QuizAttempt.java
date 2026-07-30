package com.example.realprojectaistudymentor.model;

/**
 * Lưu kết quả 1 lần làm quiz.
 */
public class QuizAttempt {

    private String attemptId;
    private String quizId;
    private int userId;
    private String quizTitle;
    private String subject;
    private int totalQuestions;
    private int correctAnswers;
    private int score;              // percentage 0-100
    private long timeTaken;         // milliseconds
    private long completedAt;

    public QuizAttempt() {
        this.attemptId = String.valueOf(System.currentTimeMillis());
        this.completedAt = System.currentTimeMillis();
    }

    public QuizAttempt(String quizId, int userId, String quizTitle, String subject,
                       int totalQuestions, int correctAnswers, long timeTaken) {
        this();
        this.quizId = quizId;
        this.userId = userId;
        this.quizTitle = quizTitle;
        this.subject = subject;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.timeTaken = timeTaken;
        this.score = totalQuestions > 0
                ? (int) ((correctAnswers * 100.0) / totalQuestions) : 0;
    }

    // Getters & Setters
    public String getAttemptId() { return attemptId; }
    public void setAttemptId(String attemptId) { this.attemptId = attemptId; }

    public String getQuizId() { return quizId; }
    public void setQuizId(String quizId) { this.quizId = quizId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getQuizTitle() { return quizTitle; }
    public void setQuizTitle(String quizTitle) { this.quizTitle = quizTitle; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int total) { this.totalQuestions = total; }

    public int getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(int correct) { this.correctAnswers = correct; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public long getTimeTaken() { return timeTaken; }
    public void setTimeTaken(long time) { this.timeTaken = time; }

    public long getCompletedAt() { return completedAt; }
    public void setCompletedAt(long time) { this.completedAt = time; }
}
