package com.example.realprojectaistudymentor.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realprojectaistudymentor.R;
import com.example.realprojectaistudymentor.database.entity.QuestionEntity;
import com.example.realprojectaistudymentor.utils.Constants;
import com.example.realprojectaistudymentor.utils.Helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * RecyclerView Adapter hiển thị danh sách câu hỏi cũ.
 * Hỗ trợ: click, long-click, bookmark (lưu trong SharedPreferences).
 */
public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    public interface OnQuestionClickListener {
        void onClick(QuestionEntity question);
    }

    private Context context;
    private List<QuestionEntity> questions = new ArrayList<>();
    private OnQuestionClickListener listener;
    private SharedPreferences prefs;
    private Set<String> bookmarkedIds;

    public QuestionAdapter(Context context, OnQuestionClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        this.bookmarkedIds = new HashSet<>(prefs.getStringSet("bookmarked_questions", new HashSet<>()));
    }

    public void setData(List<QuestionEntity> data) {
        this.questions = data;
        notifyDataSetChanged();
    }

    /**
     * Toggle bookmark cho câu hỏi tại position.
     */
    public void toggleBookmark(int position) {
        if (position < 0 || position >= questions.size()) return;
        QuestionEntity q = questions.get(position);
        String idStr = String.valueOf(q.id);

        if (bookmarkedIds.contains(idStr)) {
            bookmarkedIds.remove(idStr);
        } else {
            bookmarkedIds.add(idStr);
        }
        prefs.edit().putStringSet("bookmarked_questions", bookmarkedIds).apply();
        notifyItemChanged(position);
    }

    /**
     * Filter danh sách theo keyword.
     */
    public void filterByKeyword(String keyword) {
        // sẽ được gọi từ HistoryFragment
    }

    /**
     * Kiểm tra câu hỏi có được bookmark không.
     */
    public boolean isBookmarked(QuestionEntity question) {
        return bookmarkedIds.contains(String.valueOf(question.id));
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        QuestionEntity q = questions.get(position);
        holder.tvContent.setText(q.content);
        holder.tvSubject.setText(q.subject != null ? q.subject : "");
        holder.tvTime.setText(Helper.formatDate(q.timestamp));

        // Bookmark icon
        boolean bookmarked = bookmarkedIds.contains(String.valueOf(q.id));
        holder.btnBookmark.setImageResource(bookmarked
                ? android.R.drawable.btn_star_big_on
                : android.R.drawable.btn_star_big_off);
        holder.btnBookmark.setColorFilter(ContextCompat.getColor(context,
                bookmarked ? R.color.accent : R.color.text_hint));

        // Click → callback
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(q);
        });

        // Click bookmark icon → toggle
        holder.btnBookmark.setOnClickListener(v -> toggleBookmark(holder.getAdapterPosition()));

        // Long click → toggle bookmark
        holder.itemView.setOnLongClickListener(v -> {
            toggleBookmark(holder.getAdapterPosition());
            return true;
        });
    }

    @Override
    public int getItemCount() { return questions.size(); }

    static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvContent, tvSubject, tvTime;
        ImageButton btnBookmark;

        QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tv_question_content);
            tvSubject = itemView.findViewById(R.id.tv_question_subject);
            tvTime = itemView.findViewById(R.id.tv_question_time);
            btnBookmark = itemView.findViewById(R.id.btn_bookmark);
        }
    }
}
