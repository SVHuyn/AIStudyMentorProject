package com.example.realprojectaistudymentor.model;

public class TextQuestion extends Question {
    private String format; // "paragraph", "bullet", "latex"

    public TextQuestion() {}

    public TextQuestion(String content, String subject, String difficultyLevel, String format) {
        super(content, subject, difficultyLevel);
        this.format = format;
    }

    @Override
    public String getContent() { return content; }

    @Override
    public String getSubject() { return subject; }

    public String getText() { return content; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
}
