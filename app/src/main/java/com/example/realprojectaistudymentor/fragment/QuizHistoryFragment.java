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
import com.example.realprojectaistudymentor.adapter.QuizHistoryAdapter;
import com.example.realprojectaistudymentor.database.entity.QuizAttemptEntity;
import com.example.realprojectaistudymentor.repository.QuizRepository;
import com.example.realprojectaistudymentor.utils.SessionManager;

import java.util.List;

public class QuizHistoryFragment extends Fragment {

    private RecyclerView rvQuizHistory;
    private TextView tvEmpty;
    private Button btnBack;

    private QuizHistoryAdapter adapter;
    private QuizRepository quizRepo;
    private SessionManager session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quiz_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        session = new SessionManager(requireContext());
        quizRepo = new QuizRepository(requireContext());

        rvQuizHistory = view.findViewById(R.id.rv_quiz_history);
        tvEmpty = view.findViewById(R.id.tv_empty);
        btnBack = view.findViewById(R.id.btn_back);

        setupRecyclerView();
        loadQuizHistory();

        btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }

    private void setupRecyclerView() {
        rvQuizHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new QuizHistoryAdapter(requireContext(), attempt -> {
            // Mở chi tiết quiz
            QuizDetailFragment detailFragment = QuizDetailFragment.newInstance(attempt.quizId, attempt.quizTitle);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        rvQuizHistory.setAdapter(adapter);
    }

    private void loadQuizHistory() {
        int userId = session.getUserId();
        List<QuizAttemptEntity> attempts = quizRepo.getAttempts(userId);

        if (attempts.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvQuizHistory.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvQuizHistory.setVisibility(View.VISIBLE);
            adapter.setAttempts(attempts);
        }
    }
}
