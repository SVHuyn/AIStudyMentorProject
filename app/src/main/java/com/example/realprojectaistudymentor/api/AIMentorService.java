package com.example.realprojectaistudymentor.api;

import com.example.realprojectaistudymentor.model.Answer;
import com.example.realprojectaistudymentor.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Service gọi AI trả lời câu hỏi — dùng Groq API (Llama 3).
 */
public class AIMentorService {

    private static final String TAG = "AIMentor";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final int MAX_RETRIES = 5;
    private static final long RETRY_DELAY_MS = 5000;

    private final Context context;
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();

    public AIMentorService(Context context) {
        this.context = context.getApplicationContext();
    }

    public interface AiCallback {
        void onSuccess(Answer answer);
        void onFailure(String errorMessage);
    }

    public void askAI(String questionContent, String subject, AiCallback callback) {
        askAIInternal(questionContent, subject, null, callback);
    }

    /**
     * Gọi AI với câu hỏi kèm text trích xuất từ ảnh OCR.
     */
    public void askAIWithImage(String questionContent, String subject, String extractedImageText, AiCallback callback) {
        askAIInternal(questionContent, subject, extractedImageText, callback);
    }

    private void askAIInternal(String questionContent, String subject, String extractedImageText, AiCallback callback) {
        // Kiểm tra mạng trước khi gọi API
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo == null || !netInfo.isConnected()) {
            callback.onFailure("Không có kết nối Internet. Vui lòng kiểm tra mạng.");
            return;
        }

        new Thread(() -> {
            int attempt = 0;
            while (attempt < MAX_RETRIES) {
                attempt++;
                try {
                    // Xây prompt — nếu có extractedImageText thì thêm vào
                    StringBuilder promptBuilder = new StringBuilder();
                    promptBuilder.append("You are an AI Study Mentor for students. ");
                    promptBuilder.append("Answer this ").append(subject).append(" question.\n\n");

                    if (extractedImageText != null && !extractedImageText.isEmpty()) {
                        promptBuilder.append("[Text extracted from user's image via OCR:\n");
                        promptBuilder.append(extractedImageText).append("]\n\n");
                    }

                    promptBuilder.append("Question: ").append(questionContent).append("\n\n");
                    promptBuilder.append("Respond ONLY in this JSON format (no extra text):\n");
                    promptBuilder.append("{\n");
                    promptBuilder.append("  \"content\": \"main answer here\",\n");
                    promptBuilder.append("  \"simplifiedExplanation\": \"explain simply for beginners\",\n");
                    promptBuilder.append("  \"stepByStepExplanation\": \"step 1... step 2...\",\n");
                    promptBuilder.append("  \"alternativeApproach\": \"another way to solve this\",\n");
                    promptBuilder.append("  \"keyConceptsSummary\": \"key concepts used\",\n");
                    promptBuilder.append("  \"commonMistakes\": \"mistakes students often make\",\n");
                    promptBuilder.append("  \"suggestedFollowUpQuestions\": \"1. ... 2. ... 3. ...\"\n");
                    promptBuilder.append("}");

                    String prompt = promptBuilder.toString();

                    // Groq API format (OpenAI-compatible)
                    JSONObject body = new JSONObject();
                    body.put("model", Constants.GROQ_MODEL);
                    body.put("max_tokens", 800);
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

                        Answer answer = parseAIResponse(aiText);
                        callback.onSuccess(answer);
                        return;

                    } else if (response.code() == 429) {
                        // Rate limit - wait and retry
                        if (attempt < MAX_RETRIES) {
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

    private Answer parseAIResponse(String aiText) {
        Answer answer = new Answer();
        try {
            int start = aiText.indexOf("{");
            int end   = aiText.lastIndexOf("}") + 1;
            if (start >= 0 && end > start) {
                JSONObject json = new JSONObject(aiText.substring(start, end));
                answer.setContent(json.optString("content", aiText));
                answer.setSimplifiedExplanation(json.optString("simplifiedExplanation", null));
                answer.setStepByStepExplanation(json.optString("stepByStepExplanation", null));
                answer.setAlternativeApproach(json.optString("alternativeApproach", null));
                answer.setKeyConceptsSummary(json.optString("keyConceptsSummary", null));
                answer.setCommonMistakes(json.optString("commonMistakes", null));
                answer.setSuggestedFollowUpQuestions(json.optString("suggestedFollowUpQuestions", null));
            } else {
                answer.setContent(aiText);
            }
        } catch (JSONException e) {
            answer.setContent(aiText);
        }
        return answer;
    }
}
