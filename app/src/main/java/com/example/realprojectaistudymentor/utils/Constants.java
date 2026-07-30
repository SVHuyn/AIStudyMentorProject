package com.example.realprojectaistudymentor.utils;

import com.example.realprojectaistudymentor.BuildConfig;

public class Constants {

    // API
    public static final String BASE_URL = "https://api.example.com/"; // thay bằng URL thật

    // Groq API - Đọc từ BuildConfig (lưu trong local.properties, KHÔNG push lên git)
    public static final String GROQ_API_KEY = BuildConfig.GROQ_API_KEY;
    public static final String GROQ_MODEL = "llama-3.1-8b-instant";
    public static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";

    // Gemini API (deprecated)
    public static final String GEMINI_API_KEY = "";
    public static final String GEMINI_MODEL = "gemini-2.0-flash";
    public static final String GEMINI_API_URL = "";

    // HF API (deprecated)
    public static final String HF_API_KEY = "";
    public static final String HF_API_URL = "";

    // SharedPreferences
    public static final String PREF_NAME = "AIStudyMentorPrefs";
    public static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_USER_EMAIL = "userEmail";
    public static final String KEY_USER_NAME = "userName";

    // Database
    public static final String DB_NAME = "ai_study_mentor_db";
    public static final int DB_VERSION = 4;

    // Intent Keys
    public static final String KEY_QUESTION_ID = "questionId";
    public static final String KEY_QUIZ_ID = "quizId";

    // Quiz types
    public static final String QUIZ_TYPE_MCQ = "multiple_choice";
    public static final String QUIZ_TYPE_SAQ = "short_answer";
    public static final String QUIZ_TYPE_FILL_BLANK = "fill_blank";

    // Question types
    public static final String QUESTION_TYPE_TEXT = "text";
    public static final String QUESTION_TYPE_IMAGE = "image";

    // Quiz generation
    public static final int DEFAULT_QUIZ_COUNT = 5;
    public static final String DIFFICULTY_EASY = "Easy";
    public static final String DIFFICULTY_MEDIUM = "Medium";
    public static final String DIFFICULTY_HARD = "Hard";
}
