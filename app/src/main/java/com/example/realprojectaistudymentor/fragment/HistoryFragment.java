package com.example.realprojectaistudymentor.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Fragment lịch sử câu hỏi — search, bookmark, lọc theo môn.
 */
public class HistoryFragment extends Fragment {

    private EditText etSearch;
    private ChipGroup chipGroupFilter, chipGroupSubjects;
    private Chip chipAll, chipBookmarked;
    private RecyclerView rvHistory;
    private TextView tvEmpty;

    private QuestionAdapter adapter;
    private QuestionRepository questionRepo;
    private SessionManager session;

    // Store all questions for filtering
    private List<QuestionEntity> allQuestions = new ArrayList<>();
    private Set<String> subjects = new HashSet<>();
    private String currentFilter = "all";      // "all", "bookmarked", hoặc tên môn
    private String searchQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        session = new SessionManager(requireContext());
        questionRepo = new QuestionRepository(requireContext());

        // Bind views
        etSearch = view.findViewById(R.id.et_search);
        chipGroupFilter = view.findViewById(R.id.chip_group_filter);
        chipGroupSubjects = view.findViewById(R.id.chip_group_subjects);
        chipAll = view.findViewById(R.id.chip_all);
        chipBookmarked = view.findViewById(R.id.chip_bookmarked);
        rvHistory = view.findViewById(R.id.rv_history);
        tvEmpty = view.findViewById(R.id.tv_empty_history);

        // Setup RecyclerView
        rvHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new QuestionAdapter(requireContext(), question -> {
            // Click vào câu hỏi → có thể mở detail (future feature)
        });
        rvHistory.setAdapter(adapter);

        // Search — debounce 300ms
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                searchQuery = s.toString().trim();
                applyFilters();
            }
        });

        // Filter chips — All / Bookmarked
        chipGroupFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                currentFilter = "all";
                chipAll.setChecked(true);
            } else {
                int checkedId = checkedIds.get(0);
                if (checkedId == R.id.chip_all) {
                    currentFilter = "all";
                } else if (checkedId == R.id.chip_bookmarked) {
                    currentFilter = "bookmarked";
                }
            }
            applyFilters();
        });

        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    /**
     * Tải dữ liệu từ DB, tạo filter chips theo môn.
     */
    private void loadData() {
        int userId = session.getUserId();
        allQuestions = questionRepo.getHistory(userId);

        // Thu thập danh sách môn học
        subjects.clear();
        for (QuestionEntity q : allQuestions) {
            if (q.subject != null && !q.subject.isEmpty()) {
                subjects.add(q.subject);
            }
        }

        // Tạo subject filter chips
        createSubjectChips();

        // Áp dụng filter
        applyFilters();
    }

    /**
     * Tạo dynamic chips cho từng môn học.
     */
    private void createSubjectChips() {
        chipGroupSubjects.removeAllViews();

        if (subjects.isEmpty()) {
            chipGroupSubjects.setVisibility(View.GONE);
            return;
        }

        chipGroupSubjects.setVisibility(View.VISIBLE);

        for (String subject : subjects) {
            Chip chip = new Chip(requireContext());
            chip.setText(subject);
            chip.setCheckable(true);
            chip.setChecked(false);
            chip.setOnClickListener(v -> {
                currentFilter = subject;
                // Uncheck group filter chips
                chipGroupFilter.clearCheck();
                applyFilters();
            });
            chipGroupSubjects.addView(chip);
        }
    }

    /**
     * Áp dụng search + filter, cập nhật adapter.
     */
    private void applyFilters() {
        List<QuestionEntity> filtered = new ArrayList<>();

        for (QuestionEntity q : allQuestions) {
            // Filter by search
            boolean matchSearch = searchQuery.isEmpty()
                    || (q.content != null && q.content.toLowerCase().contains(searchQuery.toLowerCase()))
                    || (q.subject != null && q.subject.toLowerCase().contains(searchQuery.toLowerCase()));

            // Filter by type
            boolean matchFilter = true;
            if ("bookmarked".equals(currentFilter)) {
                matchFilter = adapter.isBookmarked(q);
            } else if (!"all".equals(currentFilter)) {
                // Filter by subject
                matchFilter = currentFilter.equals(q.subject);
            }

            if (matchSearch && matchFilter) {
                filtered.add(q);
            }
        }

        // Hiển thị/ẩn empty state
        if (filtered.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvHistory.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvHistory.setVisibility(View.VISIBLE);
        }

        adapter.setData(filtered);
    }
}
