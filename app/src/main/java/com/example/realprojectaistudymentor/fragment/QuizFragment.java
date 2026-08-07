package com.example.realprojectaistudymentor.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realprojectaistudymentor.R;
import com.example.realprojectaistudymentor.adapter.QuizAdapter;
import com.example.realprojectaistudymentor.api.AIQuizService;
import com.example.realprojectaistudymentor.model.MultipleChoiceQuiz;
import com.example.realprojectaistudymentor.model.PracticeQuiz;
import com.example.realprojectaistudymentor.model.QuizAttempt;
import com.example.realprojectaistudymentor.database.entity.QuizQuestionEntity;
import com.example.realprojectaistudymentor.repository.QuizQuestionRepository;
import com.example.realprojectaistudymentor.repository.QuizRepository;
import com.example.realprojectaistudymentor.repository.UserRepository;
import com.example.realprojectaistudymentor.utils.Constants;
import com.example.realprojectaistudymentor.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

/**
 * Màn hình làm quiz — chọn môn + độ khó, AI tạo câu hỏi,
 * hiển thị quiz, lưu kết quả khi hoàn thành.
 */
public class QuizFragment extends Fragment {

    // Setup panel
    private ScrollView layoutQuizSetup;
    private Spinner spinnerSubject;
    private Spinner spinnerDifficulty;
    private Button btnGenerate;
    private TextView tvError;

    // Loading
    private LinearLayout layoutLoading;
    private TextView tvLoadingStatus;

    // Quiz
    private RecyclerView rvQuiz;
    private TextView tvProgress, tvQuizScore;
    private ProgressBar progressBar;
    private LinearLayout layoutResult;
    private Button btnFinish;
    private Button btnViewHistory;
    private Button btnViewHistorySetup;

    private QuizAdapter adapter;
    private QuizRepository quizRepo;
    private QuizQuestionRepository quizQuestionRepo;
    private UserRepository userRepo;
    private SessionManager session;
    private AIQuizService aiQuizService;

    private PracticeQuiz currentQuiz;
    private List<QuizAdapter.QuizItem> quizItems = new ArrayList<>();
    private int answeredCount = 0;
    private int correctCount = 0;
    private long startTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        session = new SessionManager(requireContext());
        quizRepo = new QuizRepository(requireContext());
        quizQuestionRepo = new QuizQuestionRepository(requireContext());
        userRepo = new UserRepository(requireContext());
        aiQuizService = new AIQuizService(requireContext());

        // Bind setup panel
        layoutQuizSetup = view.findViewById(R.id.layout_quiz_setup);
        spinnerSubject = view.findViewById(R.id.spinner_quiz_subject);
        spinnerDifficulty = view.findViewById(R.id.spinner_difficulty);
        btnGenerate = view.findViewById(R.id.btn_generate_quiz);
        tvError = view.findViewById(R.id.tv_quiz_error);

        // Bind loading
        layoutLoading = view.findViewById(R.id.layout_quiz_loading);
        tvLoadingStatus = view.findViewById(R.id.tv_loading_status);

        // Bind quiz
        rvQuiz = view.findViewById(R.id.rv_quiz_questions);
        tvProgress = view.findViewById(R.id.tv_quiz_progress);
        tvQuizScore = view.findViewById(R.id.tv_quiz_score);
        progressBar = view.findViewById(R.id.progress_bar_quiz);
        layoutResult = view.findViewById(R.id.layout_quiz_result);
        btnFinish = view.findViewById(R.id.btn_finish_quiz);
        btnViewHistory = view.findViewById(R.id.btn_view_history);
        btnViewHistorySetup = view.findViewById(R.id.btn_view_history_setup);

        setupDifficultySpinner();
        setupSubjectSpinner();
        setupRecyclerView();

        // Nút Generate Quiz
        btnGenerate.setOnClickListener(v -> generateQuiz());

        // Nút New Quiz → quay lại setup
        btnFinish.setOnClickListener(v -> showSetupPanel());

        // Nút View History → mở màn hình lịch sử quiz
        btnViewHistory.setOnClickListener(v -> openQuizHistory());

        // Nút View History trong setup panel
        btnViewHistorySetup.setOnClickListener(v -> openQuizHistory());
    }

    /**
     * Setup spinner chọn độ khó.
     */
    private void setupDifficultySpinner() {
        String[] difficulties = {
                Constants.DIFFICULTY_EASY,
                Constants.DIFFICULTY_MEDIUM,
                Constants.DIFFICULTY_HARD
        };
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                difficulties
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(spinnerAdapter);
        spinnerDifficulty.setSelection(1); // Default: Medium
    }

    /**
     * Setup spinner chọn môn học.
     */
    private void setupSubjectSpinner() {
        String[] subjects = {
                "General",
                "Math",
                "Physics",
                "Chemistry",
                "Biology",
                "History",
                "Geography",
                "English",
                "Programming"
        };
        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                subjects
        );
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubject.setAdapter(subjectAdapter);
    }

    /**
     * Setup RecyclerView + QuizAdapter.
     */
    private void setupRecyclerView() {
        rvQuiz.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new QuizAdapter(requireContext(), (position, answer, isCorrect) -> {
            answeredCount++;
            if (isCorrect) correctCount++;
            updateProgress();
            if (answeredCount == quizItems.size()) {
                finishQuiz();
            }
        });
        rvQuiz.setAdapter(adapter);
    }

    /**
     * Validate input và gọi AI tạo quiz.
     */
    private void generateQuiz() {
        String subject = spinnerSubject.getSelectedItem().toString();
        String difficulty = spinnerDifficulty.getSelectedItem().toString();
        tvError.setVisibility(View.GONE);

        // Hiển thị loading, ẩn setup
        showLoading();

        aiQuizService.generateQuiz(subject, difficulty, Constants.DEFAULT_QUIZ_COUNT,
                new AIQuizService.QuizCallback() {
                    @Override
                    public void onSuccess(PracticeQuiz quiz) {
                        if (getActivity() == null) return;
                        getActivity().runOnUiThread(() -> {
                            currentQuiz = quiz;
                            loadQuiz(quiz);
                            showQuizPanel();
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        if (getActivity() == null) return;
                        getActivity().runOnUiThread(() -> {
                            showSetupPanel();
                            tvError.setText(errorMessage + "\nTap Generate to retry");
                            tvError.setVisibility(View.VISIBLE);
                        });
                    }
                },
                (attempt, maxRetries) -> {
                    if (getActivity() == null) return;
                    getActivity().runOnUiThread(() ->
                            tvLoadingStatus.setText("Rate limited, retrying... (" + attempt + "/" + maxRetries + ")")
                    );
                });
    }

    /**
     * Load quiz lên RecyclerView.
     */
    private void loadQuiz(PracticeQuiz quiz) {
        quizItems.clear();
        answeredCount = 0;
        correctCount = 0;

        if (quiz instanceof MultipleChoiceQuiz) {
            MultipleChoiceQuiz mcq = (MultipleChoiceQuiz) quiz;
            for (int i = 0; i < mcq.getQuestionCount(); i++) {
                QuizAdapter.QuizItem item = new QuizAdapter.QuizItem();
                item.questionText = mcq.getQuestions().get(i);
                item.quizType = Constants.QUIZ_TYPE_MCQ;
                item.options = mcq.getOptions(i);
                item.correctIndex = mcq.getCorrectIndex(i);
                item.answered = false;
                quizItems.add(item);
            }
        }

        adapter.setItems(quizItems);
        updateProgress();
        startTime = System.currentTimeMillis();
    }

    /**
     * Cập nhật progress bar + text.
     */
    private void updateProgress() {
        int total = quizItems.size();
        tvProgress.setText("Q " + answeredCount + "/" + total);
        int percent = total > 0 ? (answeredCount * 100 / total) : 0;
        progressBar.setProgress(percent);
    }

    /**
     * Hoàn thành quiz — hiển thị điểm, lưu vào DB, và cộng XP.
     */
    private void finishQuiz() {
        long timeTaken = System.currentTimeMillis() - startTime;
        int total = quizItems.size();
        int score = total > 0 ? (correctCount * 100 / total) : 0;

        // Hiển thị kết quả
        tvQuizScore.setText("Result: " + correctCount + "/" + total + " (" + score + "%)");
        layoutResult.setVisibility(View.VISIBLE);

        // Lưu QuizAttempt vào DB
        QuizAttempt attempt = new QuizAttempt(
                currentQuiz.getQuizId(),
                session.getUserId(),
                currentQuiz.getTitle(),
                currentQuiz.getSubject(),
                total,
                correctCount,
                timeTaken
        );
        quizRepo.saveAttempt(attempt);

        // Lưu chi tiết từng câu hỏi vào DB
        saveQuizQuestions();

        // Tính XP: 10 điểm cơ bản + bonus mỗi câu đúng
        int xpEarned = 10 + (correctCount * 5);
        userRepo.addXp(session.getUserId(), xpEarned);

        Toast.makeText(requireContext(),
                "Result saved! +" + xpEarned + " XP",
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Lưu chi tiết từng câu hỏi của quiz vào bảng quiz_questions.
     */
    private void saveQuizQuestions() {
        List<QuizQuestionEntity> entities = new ArrayList<>();
        String quizId = currentQuiz.getQuizId();

        for (QuizAdapter.QuizItem item : quizItems) {
            QuizQuestionEntity entity = new QuizQuestionEntity();
            entity.quizId = quizId;
            entity.questionText = item.questionText;

            // Lưu options dưới dạng JSON array
            if (item.options != null) {
                JSONArray optionsArray = new JSONArray();
                for (String opt : item.options) {
                    optionsArray.put(opt);
                }
                entity.options = optionsArray.toString();
            }

            entity.correctIndex = item.correctIndex;

            // Lưu user answer (-1 nếu bỏ qua)
            if (item.userAnswer != null) {
                try {
                    entity.userAnswer = Integer.parseInt(item.userAnswer);
                } catch (NumberFormatException e) {
                    entity.userAnswer = -1;
                }
            } else {
                entity.userAnswer = -1;
            }

            entity.isCorrect = item.isCorrect;
            entities.add(entity);
        }

        quizQuestionRepo.saveAll(entities);
    }

    // --- Panel visibility helpers ---

    private void openQuizHistory() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new QuizHistoryFragment())
                .addToBackStack(null)
                .commit();
    }

    private void showSetupPanel() {
        layoutQuizSetup.setVisibility(View.VISIBLE);
        layoutLoading.setVisibility(View.GONE);
        rvQuiz.setVisibility(View.GONE);
        layoutResult.setVisibility(View.GONE);
        tvProgress.setText("Q 0/0");
        progressBar.setProgress(0);
    }

    private void showLoading() {
        layoutQuizSetup.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.VISIBLE);
        rvQuiz.setVisibility(View.GONE);
        layoutResult.setVisibility(View.GONE);
    }

    private void showQuizPanel() {
        layoutQuizSetup.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.GONE);
        rvQuiz.setVisibility(View.VISIBLE);
        layoutResult.setVisibility(View.GONE);
    }
}
