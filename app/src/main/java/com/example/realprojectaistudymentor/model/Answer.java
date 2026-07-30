package com.example.realprojectaistudymentor.model;

public class Answer {
    private String answerId;
    private String questionId;
    private String content;
    private String simplifiedExplanation;
    private String stepByStepExplanation;
    private String alternativeApproach;
    private String keyConceptsSummary;
    private String commonMistakes;
    private String suggestedFollowUpQuestions;  // added per requirements

    public Answer() {}

    public Answer(String questionId, String content) {
        this.answerId = String.valueOf(System.currentTimeMillis());
        this.questionId = questionId;
        this.content = content;
    }

    public String getContent() { return content; }
    public String getExplanation() { return stepByStepExplanation; }

    // Getters & Setters
    public String getAnswerId() { return answerId; }
    public void setAnswerId(String answerId) { this.answerId = answerId; }
    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }
    public void setContent(String content) { this.content = content; }
    public String getSimplifiedExplanation() { return simplifiedExplanation; }
    public void setSimplifiedExplanation(String simplifiedExplanation) { this.simplifiedExplanation = simplifiedExplanation; }
    public String getStepByStepExplanation() { return stepByStepExplanation; }
    public void setStepByStepExplanation(String v) { this.stepByStepExplanation = v; }
    public String getAlternativeApproach() { return alternativeApproach; }
    public void setAlternativeApproach(String v) { this.alternativeApproach = v; }
    public String getKeyConceptsSummary() { return keyConceptsSummary; }
    public void setKeyConceptsSummary(String v) { this.keyConceptsSummary = v; }
    public String getCommonMistakes() { return commonMistakes; }
    public void setCommonMistakes(String v) { this.commonMistakes = v; }
    public String getSuggestedFollowUpQuestions() { return suggestedFollowUpQuestions; }
    public void setSuggestedFollowUpQuestions(String v) { this.suggestedFollowUpQuestions = v; }
}
