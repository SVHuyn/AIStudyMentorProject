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
import com.example.realprojectaistudymentor.adapter.QuizDetailAdapter;
import com.example.realprojectaistudymentor.database.entity.QuizQuestionEntity;
import com.example.realprojectaistudymentor.repository.QuizQuestionRepository;

import java.util.List;

public class QuizDetailFragment extends Fragment {

    private static final String ARG_QUIZ_ID = "quizId";
    private static final String ARG_QUIZ_TITLE = "quizTitle";

    private TextView tvDetailTitle, tvDetailScore, tvDetailInfo;
    private Button btnBack;
    private RecyclerView rvQuizDetail;

    private QuizDetailAdapter adapter;
    private QuizQuestionRepository quizQuestionRepo;
    private String quizId;
    private String quizTitle;

    public static QuizDetailFragment newInstance(String quizId, String quizTitle) {
        QuizDetailFragment fragment = new QuizDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUIZ_ID, quizId);
        args.putString(ARG_QUIZ_TITLE, quizTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quiz_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            quizId = getArguments().getString(ARG_QUIZ_ID);
            quizTitle = getArguments().getString(ARG_QUIZ_TITLE);
        }

        quizQuestionRepo = new QuizQuestionRepository(requireContext());

        tvDetailTitle = view.findViewById(R.id.tv_detail_title);
        tvDetailScore = view.findViewById(R.id.tv_detail_score);
        tvDetailInfo = view.findViewById(R.id.tv_detail_info);
        btnBack = view.findViewById(R.id.btn_back);
        rvQuizDetail = view.findViewById(R.id.rv_quiz_detail);

        tvDetailTitle.setText(quizTitle != null ? quizTitle : "Quiz Detail");

        setupRecyclerView();
        loadQuizDetail();

        btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }

    private void setupRecyclerView() {
        rvQuizDetail.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new QuizDetailAdapter(requireContext());
        rvQuizDetail.setAdapter(adapter);
    }

    private void loadQuizDetail() {
        List<QuizQuestionEntity> questions = quizQuestionRepo.getByQuizId(quizId);
        adapter.setQuestions(questions);

        // Tính điểm
        int total = questions.size();
        int correct = 0;
        for (QuizQuestionEntity q : questions) {
            if (q.isCorrect) correct++;
        }
        int score = total > 0 ? (correct * 100 / total) : 0;

        tvDetailScore.setText(score + "%");
        tvDetailInfo.setText(correct + "/" + total + " correct answers");
    }
}
