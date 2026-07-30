package com.example.realprojectaistudymentor.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.realprojectaistudymentor.model.LeaderboardEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Repository quản lý Leaderboard trên Firebase Realtime Database.
 * Path: /leaderboard/{email}
 */
public class LeaderboardRepository {

    private static final String TAG = "LeaderboardRepo";
    private static final String DB_PATH = "leaderboard";

    private final DatabaseReference leaderboardRef;

    public LeaderboardRepository() {
        leaderboardRef = FirebaseDatabase.getInstance().getReference(DB_PATH);
    }

    /**
     * Callback cho việc lấy leaderboard.
     */
    public interface LeaderboardCallback {
        void onSuccess(List<LeaderboardEntry> entries);
        void onFailure(String errorMessage);
    }

    /**
     * Đồng步 thông tin user lên Firebase.
     * Gọi khi đăng nhập, đăng ký, hoặc khi XP thay đổi.
     */
    public void syncUser(String email, String fullName, int xpPoints, int level) {
        if (email == null || email.isEmpty()) return;

        // Dùng email làm key (thay ký tự đặc biệt)
        String key = email.replace(".", "_").replace("@", "_at_");
        LeaderboardEntry entry = new LeaderboardEntry(fullName, xpPoints, level);

        leaderboardRef.child(key)
                .setValue(entry)
                .addOnSuccessListener(aVoid ->
                        Log.d(TAG, "Synced user: " + email))
                .addOnFailureListener(e ->
                        Log.e(TAG, "Failed to sync user: " + e.getMessage()));
    }

    /**
     * Lấy leaderboard từ Firebase, sắp xếp theo XP giảm dần.
     */
    public void getLeaderboard(LeaderboardCallback callback) {
        leaderboardRef.orderByChild("xpPoints")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<LeaderboardEntry> entries = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            LeaderboardEntry entry = child.getValue(LeaderboardEntry.class);
                            if (entry != null) {
                                entries.add(entry);
                            }
                        }
                        // Firebase orderByChild sắp xếp ASC, cần reverse để DESC
                        Collections.reverse(entries);
                        callback.onSuccess(entries);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Failed to read leaderboard: " + error.getMessage());
                        callback.onFailure(error.getMessage());
                    }
                });
    }

    /**
     * Lắng nghe thay đổi real-time trên leaderboard.
     */
    public void listenForChanges(LeaderboardCallback callback) {
        leaderboardRef.orderByChild("xpPoints")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<LeaderboardEntry> entries = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            LeaderboardEntry entry = child.getValue(LeaderboardEntry.class);
                            if (entry != null) {
                                entries.add(entry);
                            }
                        }
                        Collections.reverse(entries);
                        callback.onSuccess(entries);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Failed to listen leaderboard: " + error.getMessage());
                        callback.onFailure(error.getMessage());
                    }
                });
    }

    /**
     * Dừng lắng nghe (gọi trong onDestroy).
     */
    public void stopListening() {
        leaderboardRef.removeEventListener((ValueEventListener) null);
    }
}
