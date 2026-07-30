package com.example.realprojectaistudymentor.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.realprojectaistudymentor.R;
import com.example.realprojectaistudymentor.model.ProgressStatistics;
import com.example.realprojectaistudymentor.repository.QuestionRepository;
import com.example.realprojectaistudymentor.repository.QuizRepository;
import com.example.realprojectaistudymentor.utils.SessionManager;

import java.util.Map;

/**
 * Dashboard thống kê học tập.
 * Hiển thị: tổng câu hỏi, % chính xác, môn hay học, AI insights.
 */
public class ProgressFragment extends Fragment {

    private TextView tvTotalQuestions, tvTotalQuizzes, tvAccuracyRate;
    private LinearLayout layoutSubjectBreakdown;
    private TextView tvNoSubjectData, tvAiInsights;
    private Button btnLeaderboard;

    private QuizRepository quizRepo;
    private QuestionRepository questionRepo;
    private SessionManager session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_progress, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        session = new SessionManager(requireContext());
        quizRepo = new QuizRepository(requireContext());
        questionRepo = new QuestionRepository(requireContext());

        // Bind views
        tvTotalQuestions = view.findViewById(R.id.tv_total_questions);
        tvTotalQuizzes = view.findViewById(R.id.tv_total_quizzes);
        tvAccuracyRate = view.findViewById(R.id.tv_accuracy_rate);
        layoutSubjectBreakdown = view.findViewById(R.id.layout_subject_breakdown);
        tvNoSubjectData = view.findViewById(R.id.tv_no_subject_data);
        tvAiInsights = view.findViewById(R.id.tv_ai_insights);
        btnLeaderboard = view.findViewById(R.id.btn_leaderboard);

        // Nút Leaderboard
        btnLeaderboard.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new LeaderboardFragment())
                    .addToBackStack(null)
                    .commit();
        });

        loadStatistics();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload mỗi khi fragment hiển thị lại (sau khi làm quiz xong)
        loadStatistics();
    }

    private void loadStatistics() {
        int userId = session.getUserId();
        ProgressStatistics stats = quizRepo.getStatistics(userId, questionRepo);

        // Tổng câu hỏi
        tvTotalQuestions.setText(String.valueOf(stats.getTotalQuestionsAsked()));

        // Tổng quiz đã làm
        tvTotalQuizzes.setText(String.valueOf(stats.getTotalQuizzesTaken()));

        // % Chính xác
        tvAccuracyRate.setText(stats.getAccuracyRate() + "%");

        // Subject breakdown
        layoutSubjectBreakdown.removeAllViews();
        Map<String, Integer> breakdown = stats.getSubjectBreakdown();
        if (breakdown.isEmpty()) {
            tvNoSubjectData.setVisibility(View.VISIBLE);
            // tvNoSubjectData đã bị removeAllViews xóa, thêm lại an toàn
            if (tvNoSubjectData.getParent() == null) {
                layoutSubjectBreakdown.addView(tvNoSubjectData);
            }
        } else {
            tvNoSubjectData.setVisibility(View.GONE);
            for (Map.Entry<String, Integer> entry : breakdown.entrySet()) {
                addSubjectRow(entry.getKey(), entry.getValue());
            }
        }

        // AI Insights
        tvAiInsights.setText(stats.getAiInsights());
    }

    /**
     * Thêm 1 dòng môn học vào subject breakdown layout.
     */
    private void addSubjectRow(String subject, int count) {
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 8, 0, 8);

        // Tên môn
        TextView tvSubject = new TextView(requireContext());
        tvSubject.setText(subject);
        tvSubject.setTextSize(14);
        tvSubject.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));
        LinearLayout.LayoutParams paramsSubject = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        tvSubject.setLayoutParams(paramsSubject);

        // Số lượng
        TextView tvCount = new TextView(requireContext());
        tvCount.setText(count + " lần");
        tvCount.setTextSize(13);
        tvCount.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));

        row.addView(tvSubject);
        row.addView(tvCount);

        // Divider
        View divider = new View(requireContext());
        divider.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        divider.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.text_hint));

        layoutSubjectBreakdown.addView(row);
        layoutSubjectBreakdown.addView(divider);
    }
}
