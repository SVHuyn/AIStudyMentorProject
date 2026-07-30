package com.example.realprojectaistudymentor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realprojectaistudymentor.R;
import com.example.realprojectaistudymentor.database.entity.QuizAttemptEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QuizHistoryAdapter extends RecyclerView.Adapter<QuizHistoryAdapter.HistoryViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(QuizAttemptEntity attempt);
    }

    private Context context;
    private List<QuizAttemptEntity> attempts = new ArrayList<>();
    private OnItemClickListener listener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());

    public QuizHistoryAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setAttempts(List<QuizAttemptEntity> attempts) {
        this.attempts = attempts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_quiz_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        QuizAttemptEntity attempt = attempts.get(position);
        holder.bind(attempt);
    }

    @Override
    public int getItemCount() {
        return attempts.size();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvSubject, tvScore, tvDate, tvTimeTaken;

        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_quiz_title);
            tvSubject = itemView.findViewById(R.id.tv_subject);
            tvScore = itemView.findViewById(R.id.tv_score);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTimeTaken = itemView.findViewById(R.id.tv_time_taken);
        }

        void bind(QuizAttemptEntity attempt) {
            tvTitle.setText(attempt.quizTitle != null ? attempt.quizTitle : "Quiz");
            tvSubject.setText(attempt.subject != null ? attempt.subject : "General");
            tvScore.setText(attempt.score + "%");
            tvDate.setText(dateFormat.format(new Date(attempt.completedAt)));

            // Format time taken
            long seconds = attempt.timeTaken / 1000;
            long minutes = seconds / 60;
            seconds = seconds % 60;
            tvTimeTaken.setText(String.format(Locale.getDefault(), "%dm %ds", minutes, seconds));

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(attempt);
                }
            });
        }
    }
}
