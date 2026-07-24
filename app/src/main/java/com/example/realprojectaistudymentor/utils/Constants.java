package com.example.realprojectaistudymentor.utils;

public class Constants {

    // API
    public static final String BASE_URL = "https://api.example.com/"; // thay bằng URL thật
    public static final String AI_API_KEY = "your_ai_api_key_here";

    // SharedPreferences
    public static final String PREF_NAME = "AIStudyMentorPrefs";
    public static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_USER_EMAIL = "userEmail";
    public static final String KEY_USER_NAME = "userName";

    // Database
    public static final String DB_NAME = "ai_study_mentor_db";
    public static final int DB_VERSION = 1;

    // Intent Keys
    public static final String KEY_QUESTION_ID = "questionId";
    public static final String KEY_QUIZ_ID = "quizId";

    // Quiz types
    public static final String QUIZ_TYPE_MCQ = "multiple_choice";
    public static final String QUIZ_TYPE_SAQ = "short_answer";

    // Question types
    public static final String QUESTION_TYPE_TEXT = "text";
    public static final String QUESTION_TYPE_IMAGE = "image";
}
