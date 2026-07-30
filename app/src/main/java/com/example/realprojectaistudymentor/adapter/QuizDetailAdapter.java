package com.example.realprojectaistudymentor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realprojectaistudymentor.R;
import com.example.realprojectaistudymentor.database.entity.QuizQuestionEntity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class QuizDetailAdapter extends RecyclerView.Adapter<QuizDetailAdapter.DetailViewHolder> {

    private Context context;
    private List<QuizQuestionEntity> questions = new ArrayList<>();

    public QuizDetailAdapter(Context context) {
        this.context = context;
    }

    public void setQuestions(List<QuizQuestionEntity> questions) {
        this.questions = questions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_quiz_detail, parent, false);
        return new DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailViewHolder holder, int position) {
        QuizQuestionEntity question = questions.get(position);
        holder.bind(question, position);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    class DetailViewHolder extends RecyclerView.ViewHolder {

        TextView tvQuestionNumber, tvQuestionText, tvResultIcon;
        TextView tvUserAnswer, tvCorrectAnswer;
        LinearLayout layoutOptions;

        DetailViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionNumber = itemView.findViewById(R.id.tv_question_number);
            tvQuestionText = itemView.findViewById(R.id.tv_question_text);
            tvResultIcon = itemView.findViewById(R.id.tv_result_icon);
            tvUserAnswer = itemView.findViewById(R.id.tv_user_answer);
            tvCorrectAnswer = itemView.findViewById(R.id.tv_correct_answer);
            layoutOptions = itemView.findViewById(R.id.layout_options);
        }

        void bind(QuizQuestionEntity question, int position) {
            tvQuestionNumber.setText("Q " + (position + 1));
            tvQuestionText.setText(question.questionText);

            // Result icon
            if (question.isCorrect) {
                tvResultIcon.setText("✅");
            } else {
                tvResultIcon.setText("❌");
            }

            // Hiển thị options (MCQ)
            if (question.options != null && !question.options.isEmpty()) {
                layoutOptions.setVisibility(View.VISIBLE);
                layoutOptions.removeAllViews();

                try {
                    JSONArray optionsArray = new JSONArray(question.options);
                    for (int i = 0; i < optionsArray.length(); i++) {
                        RadioButton rb = new RadioButton(context);
                        rb.setText(optionsArray.getString(i));
                        rb.setTextSize(14);
                        rb.setPadding(8, 8, 8, 8);
                        rb.setEnabled(false); // Disable để chỉ xem

                        // Highlight đáp án đúng = xanh, đáp án sai = đỏ
                        if (i == question.correctIndex) {
                            rb.setTextColor(ContextCompat.getColor(context, R.color.success));
                        } else if (i == question.userAnswer && !question.isCorrect) {
                            rb.setTextColor(ContextCompat.getColor(context, R.color.error));
                        }

                        rb.setChecked(i == question.userAnswer);
                        layoutOptions.addView(rb);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // Hiển thị user answer
            if (question.userAnswer >= 0) {
                tvUserAnswer.setVisibility(View.VISIBLE);
                tvUserAnswer.setText("Your answer: Option " + (question.userAnswer + 1));
                tvUserAnswer.setTextColor(ContextCompat.getColor(context,
                        question.isCorrect ? R.color.success : R.color.error));
            } else {
                tvUserAnswer.setVisibility(View.VISIBLE);
                tvUserAnswer.setText("Your answer: Skipped");
                tvUserAnswer.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
            }

            // Hiển thị correct answer khi sai
            if (!question.isCorrect) {
                tvCorrectAnswer.setVisibility(View.VISIBLE);
                if (question.options != null) {
                    try {
                        JSONArray optionsArray = new JSONArray(question.options);
                        tvCorrectAnswer.setText("Correct answer: " + optionsArray.getString(question.correctIndex));
                    } catch (JSONException e) {
                        tvCorrectAnswer.setText("Correct answer: Option " + (question.correctIndex + 1));
                    }
                } else {
                    tvCorrectAnswer.setText("Correct answer: Option " + (question.correctIndex + 1));
                }
            } else {
                tvCorrectAnswer.setVisibility(View.GONE);
            }
        }
    }
}
