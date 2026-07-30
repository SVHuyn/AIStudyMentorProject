package com.example.realprojectaistudymentor.api;

import com.example.realprojectaistudymentor.model.MultipleChoiceQuiz;
import com.example.realprojectaistudymentor.model.PracticeQuiz;
import com.example.realprojectaistudymentor.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Service tạo quiz bằng AI — dùng Groq API (Llama 3).
 */
public class AIQuizService {

    private static final String TAG = "AIQuiz";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final int MAX_RETRIES = 5;
    private static final long RETRY_DELAY_MS = 5000;

    private final Context context;
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();

    public AIQuizService(Context context) {
        this.context = context.getApplicationContext();
    }

    public interface QuizCallback {
        void onSuccess(PracticeQuiz quiz);
        void onFailure(String errorMessage);
    }

    public interface ProgressListener {
        void onRetry(int attempt, int maxRetries);
    }

    /**
     * Gọi AI để tạo quiz, tự động retry khi bị rate limit (429).
     */
    public void generateQuiz(String subject, String difficulty, int questionCount,
                             QuizCallback callback, ProgressListener progressListener) {
        // Kiểm tra mạng trước khi gọi API
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo == null || !netInfo.isConnected()) {
            callback.onFailure("No internet connection. Please check your network..");
            return;
        }

        new Thread(() -> {
            int attempt = 0;
            while (attempt < MAX_RETRIES) {
                attempt++;
                try {
                    String prompt = buildPrompt(subject, difficulty, questionCount);

                    // Groq API format (OpenAI-compatible)
                    JSONObject body = new JSONObject();
                    body.put("model", Constants.GROQ_MODEL);
                    body.put("max_tokens", 1500);
                    body.put("temperature", 0.7);

                    // Tạo messages array
                    JSONArray messagesArray = new JSONArray();
                    JSONObject message = new JSONObject();
                    message.put("role", "user");
                    message.put("content", prompt);
                    messagesArray.put(message);
                    body.put("messages", messagesArray);

                    RequestBody requestBody = RequestBody.create(JSON, body.toString());
                    Log.d(TAG, "Final request body: " + body.toString());

                    Request.Builder requestBuilder = new Request.Builder()
                            .url(Constants.GROQ_API_URL)
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authorization", "Bearer " + Constants.GROQ_API_KEY)
                            .post(requestBody);

                    Log.d(TAG, "Request URL: " + Constants.GROQ_API_URL);
                    Log.d(TAG, "Request body: " + body.toString());

                    Response response = client.newCall(requestBuilder.build()).execute();
                    String responseBody = response.body() != null ? response.body().string() : "";
                    Log.d(TAG, "Response code: " + response.code());
                    Log.d(TAG, "Response body: " + responseBody);

                    if (response.isSuccessful() && !responseBody.isEmpty()) {

                        // Groq API response format (OpenAI-compatible)
                        String aiText;
                        try {
                            JSONObject respObj = new JSONObject(responseBody);
                            JSONArray choices = respObj.getJSONArray("choices");
                            JSONObject choice = choices.getJSONObject(0);
                            JSONObject messageObj = choice.getJSONObject("message");
                            aiText = messageObj.getString("content");
                        } catch (JSONException e) {
                            Log.e(TAG, "Parse error: " + e.getMessage());
                            aiText = responseBody;
                        }

                        PracticeQuiz quiz = parseQuizResponse(aiText, subject, difficulty);
                        callback.onSuccess(quiz);
                        return;

                    } else if (response.code() == 429) {
                        // Rate limit - wait and retry
                        if (attempt < MAX_RETRIES) {
                            if (progressListener != null) {
                                progressListener.onRetry(attempt, MAX_RETRIES);
                            }
                            Thread.sleep(RETRY_DELAY_MS);
                            continue;
                        }
                        callback.onFailure("Too many requests. Please wait a moment and try again.");
                        return;

                    } else {
                        // Log chi tiết lỗi
                        Log.e(TAG, "API Error: " + response.code());
                        Log.e(TAG, "Error body: " + responseBody);
                        callback.onFailure("AI error (" + response.code() + "): " + responseBody);
                        return;
                    }

                } catch (IOException | JSONException e) {
                    Log.e(TAG, "Exception: " + e.getClass().getSimpleName() + ": " + e.getMessage());
                    e.printStackTrace();
                    if (attempt < MAX_RETRIES) {
                        try { Thread.sleep(RETRY_DELAY_MS); } catch (InterruptedException ignored) {}
                        continue;
                    }
                    callback.onFailure("Error: " + e.getClass().getSimpleName() + ": " + e.getMessage());
                    return;
                } catch (InterruptedException e) {
                    callback.onFailure("Request cancelled.");
                    return;
                }
            }
        }).start();
    }

    /**
     * Xây dựng prompt cho AI.
     */
    private String buildPrompt(String subject, String difficulty, int questionCount) {
        return "Generate a multiple choice quiz with the following settings:\n"
                + "- Subject: " + subject + "\n"
                + "- Difficulty: " + difficulty + "\n"
                + "- Number of questions: " + questionCount + "\n\n"
                + "Respond ONLY in this JSON format (no extra text, no markdown):\n"
                + "{\n"
                + "  \"title\": \"Quiz title for this " + subject + " quiz\",\n"
                + "  \"questions\": [\n"
                + "    {\n"
                + "      \"question\": \"question text here\",\n"
                + "      \"options\": [\"A. option1\", \"B. option2\", \"C. option3\", \"D. option4\"],\n"
                + "      \"correctIndex\": 0\n"
                + "    }\n"
                + "  ]\n"
                + "}\n\n"
                + "Rules:\n"
                + "- Each question must have exactly 4 options labeled A. B. C. D.\n"
                + "- correctIndex is 0-based (0=A, 1=B, 2=C, 3=D)\n"
                + "- Return exactly " + questionCount + " questions";
    }

    /**
     * Parse JSON response từ AI thành PracticeQuiz.
     */
    private PracticeQuiz parseQuizResponse(String aiText, String subject, String difficulty) {
        try {
            int start = aiText.indexOf("{");
            int end = aiText.lastIndexOf("}") + 1;
            if (start < 0 || end <= start) {
                return createFallbackQuiz(subject, difficulty);
            }

            JSONObject json = new JSONObject(aiText.substring(start, end));

            String title = json.optString("title", subject + " Quiz");
            JSONArray questionsArray = json.getJSONArray("questions");

            List<String> questions = new ArrayList<>();
            List<String[]> optionsList = new ArrayList<>();
            List<Integer> correctIndices = new ArrayList<>();

            for (int i = 0; i < questionsArray.length(); i++) {
                JSONObject q = questionsArray.getJSONObject(i);
                questions.add(q.getString("question"));

                JSONArray opts = q.getJSONArray("options");
                String[] options = new String[opts.length()];
                for (int j = 0; j < opts.length(); j++) {
                    options[j] = opts.getString(j);
                }
                optionsList.add(options);

                correctIndices.add(q.getInt("correctIndex"));
            }

            int[] correctArray = new int[correctIndices.size()];
            for (int i = 0; i < correctIndices.size(); i++) {
                correctArray[i] = correctIndices.get(i);
            }

            return new MultipleChoiceQuiz(
                    title, subject, difficulty, 0,
                    questions, optionsList, correctArray
            );

        } catch (JSONException e) {
            return createFallbackQuiz(subject, difficulty);
        }
    }

    /**
     * Fallback quiz khi AI trả về kết quả không hợp lệ.
     */
    private PracticeQuiz createFallbackQuiz(String subject, String difficulty) {
        List<String> questions = new ArrayList<>();
        List<String[]> options = new ArrayList<>();
        int[] correct = {1, 0, 2, 3, 1};

        questions.add("What is 2 + 2?");
        options.add(new String[]{"A. 3", "B. 4", "C. 5", "D. 6"});

        questions.add("What is 10 - 3?");
        options.add(new String[]{"A. 7", "B. 8", "C. 6", "D. 9"});

        questions.add("What is 3 × 4?");
        options.add(new String[]{"A. 7", "B. 11", "C. 12", "D. 14"});

        questions.add("What is 15 ÷ 3?");
        options.add(new String[]{"A. 3", "B. 4", "C. 5", "D. 6"});

        questions.add("What is 9 + 6?");
        options.add(new String[]{"A. 13", "B. 14", "C. 15", "D. 16"});

        return new MultipleChoiceQuiz(
                subject + " Quiz (Fallback)", subject, difficulty, 0,
                questions, options, correct
        );
    }
}
