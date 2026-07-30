package com.example.realprojectaistudymentor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realprojectaistudymentor.R;
import com.example.realprojectaistudymentor.model.Answer;

import java.util.ArrayList;
import java.util.List;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder> {

    public static class AnswerSection {
        public String title;
        public String content;
        public AnswerSection(String title, String content) {
            this.title = title;
            this.content = content;
        }
    }

    private Context context;
    private List<AnswerSection> sections = new ArrayList<>();

    public AnswerAdapter(Context context) {
        this.context = context;
    }

    public void setAnswer(Answer answer) {
        sections.clear();
        if (answer.getContent() != null)
            sections.add(new AnswerSection("📖 Answer", answer.getContent()));
        if (answer.getSimplifiedExplanation() != null)
            sections.add(new AnswerSection("🧩 Simplified", answer.getSimplifiedExplanation()));
        if (answer.getStepByStepExplanation() != null)
            sections.add(new AnswerSection("🔢 Step-by-Step", answer.getStepByStepExplanation()));
        if (answer.getAlternativeApproach() != null)
            sections.add(new AnswerSection("🔄 Alternative Approach", answer.getAlternativeApproach()));
        if (answer.getKeyConceptsSummary() != null)
            sections.add(new AnswerSection("💡 Key Concepts", answer.getKeyConceptsSummary()));
        if (answer.getCommonMistakes() != null)
            sections.add(new AnswerSection("⚠️ Common Mistakes", answer.getCommonMistakes()));
        if (answer.getSuggestedFollowUpQuestions() != null)
            sections.add(new AnswerSection("🎯 Try These Next", answer.getSuggestedFollowUpQuestions()));
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_answer, parent, false);
        return new AnswerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerViewHolder holder, int position) {
        AnswerSection section = sections.get(position);
        holder.tvTitle.setText(section.title);
        holder.tvContent.setText(section.content);
    }

    @Override
    public int getItemCount() { return sections.size(); }

    static class AnswerViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent;
        AnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle   = itemView.findViewById(R.id.tv_answer_title);
            tvContent = itemView.findViewById(R.id.tv_answer_content);
        }
    }
}
