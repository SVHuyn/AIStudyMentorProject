package com.example.realprojectaistudymentor.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realprojectaistudymentor.R;
import com.example.realprojectaistudymentor.model.LeaderboardEntry;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {

    private Context context;
    private List<LeaderboardEntry> entries = new ArrayList<>();
    private String currentUserEmail = "";

    // Medal emojis cho top 3
    private static final String[] MEDALS = {"🥇", "🥈", "🥉"};

    public LeaderboardAdapter(Context context) {
        this.context = context;
    }

    public void setEntries(List<LeaderboardEntry> entries) {
        this.entries = entries;
        notifyDataSetChanged();
    }

    public void setCurrentUserEmail(String email) {
        this.currentUserEmail = email;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_leaderboard, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        LeaderboardEntry entry = entries.get(position);
        holder.bind(entry, position);
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    class LeaderboardViewHolder extends RecyclerView.ViewHolder {

        TextView tvRank, tvAvatar, tvName, tvLevel, tvXp;

        LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tv_rank);
            tvAvatar = itemView.findViewById(R.id.tv_avatar);
            tvName = itemView.findViewById(R.id.tv_name);
            tvLevel = itemView.findViewById(R.id.tv_level);
            tvXp = itemView.findViewById(R.id.tv_xp);
        }

        void bind(LeaderboardEntry entry, int position) {
            int rank = position + 1;

            // Rank — medal cho top 3, số cho phần còn lại
            if (rank <= 3) {
                tvRank.setText(MEDALS[rank - 1]);
            } else {
                tvRank.setText(String.valueOf(rank));
            }

            // Name
            tvName.setText(entry.getFullName() != null ? entry.getFullName() : "Unknown");

            // Level
            tvLevel.setText("Level " + entry.getLevel());

            // XP
            tvXp.setText(String.valueOf(entry.getXpPoints()));

            // Highlight user hiện tại
            boolean isCurrentUser = currentUserEmail != null &&
                    currentUserEmail.equals(entry.getFullName());

            // Hoặc so sánh bằng email nếu có trong entry
            // Ở đây dùng fullName làm fallback, nhưng nên so sánh email chính xác hơn

            if (rank <= 3) {
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                tvName.setTypeface(null, Typeface.BOLD);
            } else {
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                tvName.setTypeface(null, Typeface.NORMAL);
            }
        }
    }
}
