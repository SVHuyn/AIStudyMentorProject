package com.example.realprojectaistudymentor.model;

import java.util.List;

public class Student extends User {

    private String studentId;
    private String educationLevel;          // "middle_school", "high_school", "university"
    private List<String> preferredSubjects; // ["Math", "Physics", "Programming"]
    private String explanationStyle;        // "short", "detailed", "step_by_step"
    private int xpPoints;
    private int level;

    public Student() {}

    public Student(String email, String password, String fullName,
                   String educationLevel, List<String> preferredSubjects, String explanationStyle) {
        super(email, password, fullName);
        this.studentId        = String.valueOf(System.currentTimeMillis());
        this.educationLevel   = educationLevel;
        this.preferredSubjects = preferredSubjects;
        this.explanationStyle = explanationStyle;
        this.xpPoints         = 0;
        this.level            = 1;
    }

    @Override
    public void register() {
        // handled in RegisterActivity
    }

    @Override
    public void login() {
        // handled in LoginActivity
    }

    public void setPreferences(String educationLevel, List<String> subjects, String explanationStyle) {
        this.educationLevel    = educationLevel;
        this.preferredSubjects = subjects;
        this.explanationStyle  = explanationStyle;
    }

    // Getters & Setters
    public String getStudentId()                       { return studentId; }
    public String getEducationLevel()                  { return educationLevel; }
    public void setEducationLevel(String v)            { this.educationLevel = v; }
    public List<String> getPreferredSubjects()         { return preferredSubjects; }
    public void setPreferredSubjects(List<String> v)   { this.preferredSubjects = v; }
    public String getExplanationStyle()                { return explanationStyle; }
    public void setExplanationStyle(String v)          { this.explanationStyle = v; }
    public int getXpPoints()                           { return xpPoints; }
    public void setXpPoints(int v)                     { this.xpPoints = v; }
    public int getLevel()                              { return level; }
    public void setLevel(int v)                        { this.level = v; }
}
