package com.example.realprojectaistudymentor.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realprojectaistudymentor.R;
import com.example.realprojectaistudymentor.adapter.AnswerAdapter;
import com.example.realprojectaistudymentor.api.AIMentorService;
import com.example.realprojectaistudymentor.model.Answer;
import com.example.realprojectaistudymentor.repository.AnswerRepository;
import com.example.realprojectaistudymentor.repository.QuestionRepository;
import com.example.realprojectaistudymentor.repository.UserRepository;
import com.example.realprojectaistudymentor.utils.Helper;
import com.example.realprojectaistudymentor.utils.OcrHelper;
import com.example.realprojectaistudymentor.utils.SessionManager;

public class AskQuestionFragment extends Fragment {

    private static final int PICK_IMAGE = 100;

    // Views
    private EditText etQuestion, etSubject;
    private Button btnAsk, btnUploadImage;
    private ImageView ivPreview;
    private ProgressBar progressBar;
    private LinearLayout layoutAnswer;
    private RecyclerView rvAnswer;
    private TextView tvError;
    private TextView tvOcrResult;

    // Logic
    private AnswerAdapter answerAdapter;
    private AIMentorService aiService;
    private QuestionRepository questionRepo;
    private AnswerRepository answerRepo;
    private UserRepository userRepo;
    private SessionManager sessionManager;
    private Uri selectedImageUri = null;
    private String extractedTextFromImage = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ask_question, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init views
        etQuestion     = view.findViewById(R.id.et_question);
        etSubject      = view.findViewById(R.id.et_subject);
        btnAsk         = view.findViewById(R.id.btn_ask);
        btnUploadImage = view.findViewById(R.id.btn_upload_image);
        ivPreview      = view.findViewById(R.id.iv_image_preview);
        progressBar    = view.findViewById(R.id.progress_bar);
        layoutAnswer   = view.findViewById(R.id.layout_answer);
        rvAnswer       = view.findViewById(R.id.rv_answer);
        tvError        = view.findViewById(R.id.tv_error);
        tvOcrResult    = view.findViewById(R.id.tv_ocr_result);

        // Init logic
        aiService      = new AIMentorService(requireContext());
        questionRepo   = new QuestionRepository(requireContext());
        answerRepo     = new AnswerRepository(requireContext());
        userRepo       = new UserRepository(requireContext());
        sessionManager = new SessionManager(requireContext());

        // Setup RecyclerView
        answerAdapter = new AnswerAdapter(requireContext());
        rvAnswer.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvAnswer.setAdapter(answerAdapter);

        // Bấm Ask → gọi AI
        btnAsk.setOnClickListener(v -> submitQuestion());

        // Bấm upload ảnh
        btnUploadImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });
    }

    private void submitQuestion() {
        String questionText = etQuestion.getText().toString().trim();
        String subject      = etSubject.getText().toString().trim();

        // Validate
        if (questionText.isEmpty()) {
            etQuestion.setError("Please enter your question");
            return;
        }
        if (subject.isEmpty()) subject = "General";

        if (!Helper.isNetworkAvailable(requireContext())) {
            tvError.setText("No internet connection");
            tvError.setVisibility(View.VISIBLE);
            return;
        }

        // Hiện loading
        progressBar.setVisibility(View.VISIBLE);
        layoutAnswer.setVisibility(View.GONE);
        tvError.setVisibility(View.GONE);
        btnAsk.setEnabled(false);

        // Lưu câu hỏi vào DB
        String type = selectedImageUri != null ? "image" : "text";
        String imageUrl = selectedImageUri != null ? selectedImageUri.toString() : null;
        int userId = sessionManager.getUserId();
        long questionId = questionRepo.save(questionText, subject, type, imageUrl, userId);

        // Gọi AI — nếu có ảnh với extracted text thì dùng askAIWithImage
        String finalSubject = subject;
        AIMentorService.AiCallback aiCallback = new AIMentorService.AiCallback() {
            @Override
            public void onSuccess(Answer answer) {
                // Lưu câu trả lời vào DB
                answerRepo.save(answer, (int) questionId);

                // Cộng XP cho mỗi câu hỏi (5 XP)
                userRepo.addXp(userId, 5);

                // Cập nhật UI trên main thread — kiểm tra fragment còn attached
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (!isAdded()) return;
                    progressBar.setVisibility(View.GONE);
                    layoutAnswer.setVisibility(View.VISIBLE);
                    btnAsk.setEnabled(true);
                    answerAdapter.setAnswer(answer);
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (!isAdded()) return;
                    progressBar.setVisibility(View.GONE);
                    btnAsk.setEnabled(true);
                    tvError.setText(errorMessage);
                    tvError.setVisibility(View.VISIBLE);
                });
            }
        };

        // Gọi AI — nếu có extracted text từ OCR thì dùng askAIWithImage
        if (extractedTextFromImage != null && !extractedTextFromImage.isEmpty()) {
            aiService.askAIWithImage(questionText, finalSubject, extractedTextFromImage, aiCallback);
        } else {
            aiService.askAI(questionText, finalSubject, aiCallback);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            ivPreview.setImageURI(selectedImageUri);
            ivPreview.setVisibility(View.VISIBLE);

            // Trích xuất text từ ảnh bằng OCR
            extractTextFromImage(selectedImageUri);
        }
    }

    private void extractTextFromImage(Uri imageUri) {
        tvOcrResult.setVisibility(View.VISIBLE);
        tvOcrResult.setText("OCR: Đang trích xuất text từ ảnh...");

        OcrHelper.extractText(requireContext(), imageUri, new OcrHelper.OcrCallback() {
            @Override
            public void onSuccess(String extractedText) {
                extractedTextFromImage = extractedText;
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (!isAdded()) return;
                    tvOcrResult.setText("OCR: " + extractedText);
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                extractedTextFromImage = null;
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (!isAdded()) return;
                    tvOcrResult.setText("OCR: Không thể trích xuất text (" + errorMessage + ")");
                });
            }
        });
    }
}
