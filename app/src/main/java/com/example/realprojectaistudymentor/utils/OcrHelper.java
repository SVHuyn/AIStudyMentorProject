package com.example.realprojectaistudymentor.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

/**
 * Helper dùng ML Kit Text Recognition để trích xuất text từ ảnh.
 * Chạy on-device, miễn phí, không cần API key.
 */
public class OcrHelper {

    private static final String TAG = "OcrHelper";

    public interface OcrCallback {
        void onSuccess(String extractedText);
        void onFailure(String errorMessage);
    }

    /**
     * Trích xuất text từ ảnh tại Uri.
     */
    public static void extractText(Context context, Uri imageUri, OcrCallback callback) {
        try {
            InputImage image = InputImage.fromFilePath(context, imageUri);

            // Dùng Latin recognizer (hỗ trợ tiếng Anh, tiếng Việt cơ bản)
            TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

            recognizer.process(image)
                    .addOnSuccessListener(result -> {
                        String text = result.getText();
                        if (text.isEmpty()) {
                            callback.onSuccess("(Không nhận diện được text từ ảnh)");
                        } else {
                            Log.d(TAG, "OCR result: " + text);
                            callback.onSuccess(text);
                        }
                        recognizer.close();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "OCR failed: " + e.getMessage());
                        callback.onFailure("Không thể nhận diện text: " + e.getMessage());
                        recognizer.close();
                    });
        } catch (Exception e) {
            Log.e(TAG, "Failed to load image: " + e.getMessage());
            callback.onFailure("Không thể đọc ảnh: " + e.getMessage());
        }
    }
}
