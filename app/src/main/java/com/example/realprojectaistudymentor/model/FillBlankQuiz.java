package com.example.realprojectaistudymentor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Quiz điền vào chỗ trống — câu chứa placeholder "___", user điền từ missing.
 * Theo tài liệu: dựa trên tài liệu học tập đã upload.
 */
public class FillBlankQuiz extends PracticeQuiz {

    private List<String> fullTexts;      // câu đầy đủ (chứa ___)
    private List<String> correctWords;   // từ đúng cần điền

    public FillBlankQuiz() {
        super();
        this.fullTexts = new ArrayList<>();
        this.correctWords = new ArrayList<>();
    }

    public FillBlankQuiz(String title, String subject, String difficultyLevel,
                         int timeLimit, List<String> fullTexts, List<String> correctWords) {
        super(title, subject, difficultyLevel, timeLimit);
        this.fullTexts = fullTexts;
        this.correctWords = correctWords;
        // questions = fullTexts (hiển thị câu có chỗ trống)
        this.questions = new ArrayList<>(fullTexts);
    }

    @Override
    public boolean checkAnswer(int questionIndex, String userAnswer) {
        if (questionIndex < 0 || questionIndex >= correctWords.size()) return false;
        String expected = correctWords.get(questionIndex).trim().toLowerCase();
        String actual = userAnswer.trim().toLowerCase();
        return expected.equals(actual);
    }

    @Override
    public String getCorrectAnswer(int questionIndex) {
        if (questionIndex < 0 || questionIndex >= correctWords.size()) return "";
        return correctWords.get(questionIndex);
    }

    /**
     * Trả về full text với đáp án đúng được điền vào (để hiển thị sau khi trả lời).
     */
    public String getFilledText(int questionIndex) {
        if (questionIndex < 0 || questionIndex >= fullTexts.size()) return "";
        String text = fullTexts.get(questionIndex);
        String word = correctWords.get(questionIndex);
        return text.replace("___", word);
    }

    // Getters & Setters
    public List<String> getFullTexts() { return fullTexts; }
    public void setFullTexts(List<String> texts) { this.fullTexts = texts; }

    public List<String> getCorrectWords() { return correctWords; }
    public void setCorrectWords(List<String> words) { this.correctWords = words; }
}
