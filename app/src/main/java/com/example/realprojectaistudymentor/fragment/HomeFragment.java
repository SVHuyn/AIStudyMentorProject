package com.example.realprojectaistudymentor.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realprojectaistudymentor.R;
import com.example.realprojectaistudymentor.adapter.QuestionAdapter;
import com.example.realprojectaistudymentor.database.entity.QuestionEntity;
import com.example.realprojectaistudymentor.repository.QuestionRepository;
import com.example.realprojectaistudymentor.utils.SessionManager;

import java.util.List;

public class HomeFragment extends Fragment {

    private TextView tvWelcome, tvQuestionCount;
    private Button btnQuickAsk;
    private RecyclerView rvRecentQuestions;
    private QuestionAdapter questionAdapter;
    private QuestionRepository questionRepo;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvWelcome       = view.findViewById(R.id.tv_welcome);
        tvQuestionCount = view.findViewById(R.id.tv_question_count);
        btnQuickAsk     = view.findViewById(R.id.btn_quick_ask);
        rvRecentQuestions = view.findViewById(R.id.rv_recent_questions);

        sessionManager = new SessionManager(requireContext());
        questionRepo   = new QuestionRepository(requireContext());

        // Hiện tên user
        tvWelcome.setText("Hi, " + sessionManager.getUserName() + " 👋");

        // Setup RecyclerView cho câu hỏi gần đây
        questionAdapter = new QuestionAdapter(requireContext(), question -> {
            // Bấm vào câu hỏi → chuyển sang AskQuestionFragment (hoặc mở detail)
        });
        rvRecentQuestions.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvRecentQuestions.setAdapter(questionAdapter);

        // Nút Ask AI Mentor → chuyển sang tab Ask Question
        btnQuickAsk.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new AskQuestionFragment())
                    .addToBackStack(null)
                    .commit();
        });

        loadRecentQuestions();
    }

    private void loadRecentQuestions() {
        int userId = sessionManager.getUserId();
        List<QuestionEntity> questions = questionRepo.getHistory(userId);

        // Hiện số câu hỏi đã hỏi
        tvQuestionCount.setText("You have asked " + questions.size() + " questions");

        // Chỉ hiện 5 câu gần nhất trên Home
        List<QuestionEntity> recent = questions.size() > 5 ? questions.subList(0, 5) : questions;
        questionAdapter.setData(recent);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload khi quay lại tab Home
        loadRecentQuestions();
    }
}
