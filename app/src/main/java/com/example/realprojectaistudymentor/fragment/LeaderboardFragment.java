package com.example.realprojectaistudymentor.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realprojectaistudymentor.R;
import com.example.realprojectaistudymentor.adapter.LeaderboardAdapter;
import com.example.realprojectaistudymentor.model.LeaderboardEntry;
import com.example.realprojectaistudymentor.repository.LeaderboardRepository;
import com.example.realprojectaistudymentor.utils.SessionManager;

import java.util.List;

public class LeaderboardFragment extends Fragment {

    private RecyclerView rvLeaderboard;
    private LinearLayout layoutLoading;
    private TextView tvEmpty;
    private Button btnBack;

    private LeaderboardAdapter adapter;
    private LeaderboardRepository leaderboardRepo;
    private SessionManager session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leaderboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        session = new SessionManager(requireContext());
        leaderboardRepo = new LeaderboardRepository();

        rvLeaderboard = view.findViewById(R.id.rv_leaderboard);
        layoutLoading = view.findViewById(R.id.layout_loading);
        tvEmpty = view.findViewById(R.id.tv_empty);
        btnBack = view.findViewById(R.id.btn_back);

        setupRecyclerView();
        showLoading();
        loadLeaderboard();

        btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }

    private void setupRecyclerView() {
        rvLeaderboard.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new LeaderboardAdapter(requireContext());
        rvLeaderboard.setAdapter(adapter);
    }

    private void showLoading() {
        layoutLoading.setVisibility(View.VISIBLE);
        rvLeaderboard.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.GONE);
    }

    private void showLeaderboard() {
        layoutLoading.setVisibility(View.GONE);
        rvLeaderboard.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
    }

    private void showEmpty() {
        layoutLoading.setVisibility(View.GONE);
        rvLeaderboard.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.VISIBLE);
    }

    private void loadLeaderboard() {
        leaderboardRepo.getLeaderboard(new LeaderboardRepository.LeaderboardCallback() {
            @Override
            public void onSuccess(List<LeaderboardEntry> entries) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (!isAdded()) return;
                    if (entries.isEmpty()) {
                        showEmpty();
                    } else {
                        adapter.setEntries(entries);
                        showLeaderboard();
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (!isAdded()) return;
                    showEmpty();
                    tvEmpty.setText("Failed to load leaderboard.\n" + errorMessage);
                    tvEmpty.setVisibility(View.VISIBLE);
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Không cần stopListening vì dùng addListenerForSingleValueEvent
    }
}
