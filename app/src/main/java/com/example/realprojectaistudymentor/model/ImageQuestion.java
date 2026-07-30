package com.example.realprojectaistudymentor.model;

public class ImageQuestion extends Question {
    private String imageUrl;
    private String extractedText;

    public ImageQuestion() {}

    public ImageQuestion(String content, String subject, String difficultyLevel, String imageUrl) {
        super(content, subject, difficultyLevel);
        this.imageUrl = imageUrl;
    }

    @Override
    public String getContent() { return content; }

    @Override
    public String getSubject() { return subject; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getExtractedText() { return extractedText; }
    public void setExtractedText(String extractedText) { this.extractedText = extractedText; }

    /**
     * Trả về text đã trích xuất từ ảnh qua OCR.
     * Text được set từ bên ngoài qua setExtractedText() sau khi ML Kit xử lý xong.
     */
    public String extractTextFromImage() {
        return extractedText != null ? extractedText : "";
    }
}
