package com.example.realprojectaistudymentor.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "answers")
public class AnswerEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int questionId;
    public String content;
    public String stepByStepExplanation;
    public String alternativeApproach;
    public String keyConceptsSummary;
    public String commonMistakes;
    public long timestamp;
}
