package com.example.realprojectaistudymentor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class cho các loại quiz practice.
 * Mỗi subclass (MultipleChoiceQuiz, ShortAnswerQuiz, FillBlankQuiz)
 * sẽ override方法 checkAnswer() và提供 UI-specific data.
 */
public abstract class PracticeQuiz {

    protected String quizId;
    protected String title;
    protected String subject;
    protected String difficultyLevel;
    protected List<String> questions;
    protected int timeLimit;        // giây, 0 = không giới hạn
    protected long timestamp;

    public PracticeQuiz() {
        this.questions = new ArrayList<>();
        this.timestamp = System.currentTimeMillis();
    }

    public PracticeQuiz(String title, String subject, String difficultyLevel, int timeLimit) {
        this();
        this.quizId = String.valueOf(System.currentTimeMillis());
        this.title = title;
        this.subject = subject;
        this.difficultyLevel = difficultyLevel;
        this.timeLimit = timeLimit;
    }

    // Abstract — mỗi loại quiz kiểm tra đáp án theo cách riêng
    public abstract boolean checkAnswer(int questionIndex, String userAnswer);

    // Abstract — trả về đáp án đúng để hiển thị feedback
    public abstract String getCorrectAnswer(int questionIndex);

    // Getters & Setters
    public String getQuizId() { return quizId; }
    public void setQuizId(String quizId) { this.quizId = quizId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public List<String> getQuestions() { return questions; }
    public void setQuestions(List<String> questions) { this.questions = questions; }

    public int getQuestionCount() { return questions.size(); }

    public int getTimeLimit() { return timeLimit; }
    public void setTimeLimit(int timeLimit) { this.timeLimit = timeLimit; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
