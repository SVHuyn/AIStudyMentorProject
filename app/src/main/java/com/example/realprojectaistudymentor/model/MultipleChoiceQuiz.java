package com.example.realprojectaistudymentor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Quiz trắc nghiệm — mỗi câu có nhiều lựa chọn, chọn 1 đáp án đúng.
 */
public class MultipleChoiceQuiz extends PracticeQuiz {

    // Mỗi câu có list options riêng
    private List<String[]> optionsPerQuestion;   // String[] = {"A. ...", "B. ...", ...}
    private int[] correctAnswerIndices;          // index trong options

    public MultipleChoiceQuiz() {
        super();
        this.optionsPerQuestion = new ArrayList<>();
    }

    public MultipleChoiceQuiz(String title, String subject, String difficultyLevel,
                              int timeLimit, List<String> questions,
                              List<String[]> options, int[] correctIndices) {
        super(title, subject, difficultyLevel, timeLimit);
        this.questions = questions;
        this.optionsPerQuestion = options;
        this.correctAnswerIndices = correctIndices;
    }

    @Override
    public boolean checkAnswer(int questionIndex, String userAnswer) {
        if (questionIndex < 0 || questionIndex >= correctAnswerIndices.length) return false;
        try {
            int selected = Integer.parseInt(userAnswer);
            return selected == correctAnswerIndices[questionIndex];
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String getCorrectAnswer(int questionIndex) {
        if (questionIndex < 0 || questionIndex >= correctAnswerIndices.length) return "";
        return String.valueOf(correctAnswerIndices[questionIndex]);
    }

    // Getters & Setters
    public List<String[]> getOptionsPerQuestion() { return optionsPerQuestion; }
    public void setOptionsPerQuestion(List<String[]> options) { this.optionsPerQuestion = options; }

    public int[] getCorrectAnswerIndices() { return correctAnswerIndices; }
    public void setCorrectAnswerIndices(int[] indices) { this.correctAnswerIndices = indices; }

    public String[] getOptions(int questionIndex) {
        if (questionIndex >= 0 && questionIndex < optionsPerQuestion.size()) {
            return optionsPerQuestion.get(questionIndex);
        }
        return new String[0];
    }

    public int getCorrectIndex(int questionIndex) {
        if (questionIndex >= 0 && questionIndex < correctAnswerIndices.length) {
            return correctAnswerIndices[questionIndex];
        }
        return -1;
    }
}
