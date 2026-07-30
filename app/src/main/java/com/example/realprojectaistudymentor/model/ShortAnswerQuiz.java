package com.example.realprojectaistudymentor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Quiz tự luận ngắn — user gõ đáp án text, so sánh case-insensitive.
 */
public class ShortAnswerQuiz extends PracticeQuiz {

    private List<String> correctAnswers;     // đáp án đúng cho mỗi câu
    private List<String> sampleAnswers;      // mẫu trả lời (hiển thị sau khi trả lời)

    public ShortAnswerQuiz() {
        super();
        this.correctAnswers = new ArrayList<>();
        this.sampleAnswers = new ArrayList<>();
    }

    public ShortAnswerQuiz(String title, String subject, String difficultyLevel,
                           int timeLimit, List<String> questions,
                           List<String> correctAnswers, List<String> sampleAnswers) {
        super(title, subject, difficultyLevel, timeLimit);
        this.questions = questions;
        this.correctAnswers = correctAnswers;
        this.sampleAnswers = sampleAnswers;
    }

    @Override
    public boolean checkAnswer(int questionIndex, String userAnswer) {
        if (questionIndex < 0 || questionIndex >= correctAnswers.size()) return false;
        String expected = correctAnswers.get(questionIndex).trim().toLowerCase();
        String actual = userAnswer.trim().toLowerCase();
        return expected.equals(actual);
    }

    @Override
    public String getCorrectAnswer(int questionIndex) {
        if (questionIndex < 0 || questionIndex >= correctAnswers.size()) return "";
        return correctAnswers.get(questionIndex);
    }

    // Getters & Setters
    public List<String> getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(List<String> answers) { this.correctAnswers = answers; }

    public List<String> getSampleAnswers() { return sampleAnswers; }
    public void setSampleAnswers(List<String> samples) { this.sampleAnswers = samples; }

    public String getSampleAnswer(int questionIndex) {
        if (questionIndex >= 0 && questionIndex < sampleAnswers.size()) {
            return sampleAnswers.get(questionIndex);
        }
        return "";
    }
}
