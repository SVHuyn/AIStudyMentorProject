package com.example.realprojectaistudymentor.model;

public abstract class Question {
    protected String questionId;
    protected String content;
    protected String subject;
    protected String difficultyLevel;
    protected long timestamp;

    public Question() {}

    public Question(String content, String subject, String difficultyLevel) {
        this.questionId = String.valueOf(System.currentTimeMillis());
        this.content = content;
        this.subject = subject;
        this.difficultyLevel = difficultyLevel;
        this.timestamp = System.currentTimeMillis();
    }

    public abstract String getContent();
    public abstract String getSubject();

    // Getters & Setters
    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }
    public void setContent(String content) { this.content = content; }
    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setSubject(String subject) { this.subject = subject; }
}
