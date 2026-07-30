package com.example.realprojectaistudymentor.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realprojectaistudymentor.R;
import com.example.realprojectaistudymentor.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView Adapter hiển thị danh sách câu hỏi quiz.
 * Hỗ trợ 3 loại: Multiple Choice, Short Answer, Fill-in-the-Blank.
 * Hiển thị instant feedback (đúng/sai) sau khi user trả lời.
 */
public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {

    // ——— Data model cho 1 câu quiz ———
    public static class QuizItem {
        public String questionText;
        public String quizType;          // QUIZ_TYPE_MCQ / QUIZ_TYPE_SAQ / QUIZ_TYPE_FILL_BLANK
        public String[] options;         // MCQ only
        public int correctIndex;         // MCQ only — đáp án đúng (index)
        public String correctAnswer;     // SAQ / FillBlank — đáp án đúng (text)
        public String sampleAnswer;      // SAQ — mẫu trả lời (hiển thị sau khi trả lời)
        public boolean answered;
        public boolean isCorrect;
        public String userAnswer;
    }

    // ——— Callback interface ———
    public interface OnQuizAnswerListener {
        void onAnswerSelected(int position, String answer, boolean isCorrect);
    }

    private Context context;
    private List<QuizItem> items = new ArrayList<>();
    private OnQuizAnswerListener listener;

    public QuizAdapter(Context context, OnQuizAnswerListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setItems(List<QuizItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_quiz, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        QuizItem item = items.get(position);
        holder.bind(item, position);
    }

    @Override
    public int getItemCount() { return items.size(); }

    // ——— ViewHolder ———
    class QuizViewHolder extends RecyclerView.ViewHolder {

        TextView tvQuestionNumber, tvQuestionText, tvFeedback;
        LinearLayout layoutMCQ;
        RadioGroup radioGroupOptions;
        LinearLayout layoutTextAnswer;
        EditText etAnswer;
        Button btnSubmit;
        View feedbackDivider;

        QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionNumber = itemView.findViewById(R.id.tv_question_number);
            tvQuestionText = itemView.findViewById(R.id.tv_question_text);
            tvFeedback = itemView.findViewById(R.id.tv_feedback);
            layoutMCQ = itemView.findViewById(R.id.layout_mcq);
            radioGroupOptions = itemView.findViewById(R.id.radio_group_options);
            layoutTextAnswer = itemView.findViewById(R.id.layout_text_answer);
            etAnswer = itemView.findViewById(R.id.et_answer);
            btnSubmit = itemView.findViewById(R.id.btn_submit_answer);
            feedbackDivider = itemView.findViewById(R.id.feedback_divider);
        }

        void bind(QuizItem item, int position) {
            // Số câu
            tvQuestionNumber.setText("Q " + (position + 1));
            tvQuestionText.setText(item.questionText);

            // Ẩn feedback mặc định
            tvFeedback.setVisibility(View.GONE);
            feedbackDivider.setVisibility(View.GONE);

            if (item.answered) {
                showAnsweredState(item);
                return;
            }

            // Chưa trả lời — hiển thị input theo loại quiz
            if (Constants.QUIZ_TYPE_MCQ.equals(item.quizType)) {
                showMCQInput(item, position);
            } else {
                showTextInput(item, position);
            }
        }

        /**
         * Hiển thị input cho MCQ (RadioGroup).
         */
        private void showMCQInput(QuizItem item, int position) {
            layoutMCQ.setVisibility(View.VISIBLE);
            layoutTextAnswer.setVisibility(View.GONE);
            radioGroupOptions.removeAllViews();

            if (item.options != null) {
                for (int i = 0; i < item.options.length; i++) {
                    RadioButton rb = new RadioButton(context);
                    rb.setText(item.options[i]);
                    rb.setId(View.generateViewId());
                    rb.setTextSize(14);
                    rb.setPadding(8, 12, 8, 12);
                    radioGroupOptions.addView(rb);
                }
            }

            radioGroupOptions.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == -1) return;
                int selectedIndex = group.indexOfChild(group.findViewById(checkedId));
                boolean correct = (selectedIndex == item.correctIndex);
                item.answered = true;
                item.isCorrect = correct;
                item.userAnswer = String.valueOf(selectedIndex);
                notifyDataSetChanged();
                if (listener != null) {
                    listener.onAnswerSelected(position, item.userAnswer, correct);
                }
            });
        }

        /**
         * Hiển thị input cho Short Answer / Fill Blank (EditText + Submit).
         */
        private void showTextInput(QuizItem item, int position) {
            layoutMCQ.setVisibility(View.GONE);
            layoutTextAnswer.setVisibility(View.VISIBLE);
            etAnswer.setText("");
            etAnswer.setEnabled(true);
            etAnswer.setHint(Constants.QUIZ_TYPE_FILL_BLANK.equals(item.quizType)
                    ? "Fill in the blank..."
                    : "Enter your answer...");
            btnSubmit.setVisibility(View.VISIBLE);

            btnSubmit.setOnClickListener(v -> {
                String answer = etAnswer.getText().toString().trim();
                if (answer.isEmpty()) {
                    etAnswer.setError("Please enter an answer");
                    return;
                }
                boolean correct = answer.equalsIgnoreCase(item.correctAnswer);
                item.answered = true;
                item.isCorrect = correct;
                item.userAnswer = answer;
                notifyDataSetChanged();
                if (listener != null) {
                    listener.onAnswerSelected(position, answer, correct);
                }
            });
        }

        /**
         * Hiển thị trạng thái đã trả lời (feedback).
         */
        private void showAnsweredState(QuizItem item) {
            // Disable input
            layoutMCQ.setVisibility(View.GONE);
            layoutTextAnswer.setVisibility(View.GONE);

            // Feedback
            tvFeedback.setVisibility(View.VISIBLE);
            feedbackDivider.setVisibility(View.VISIBLE);

            if (item.isCorrect) {
                tvFeedback.setText("✅ Correct!");
                tvFeedback.setTextColor(ContextCompat.getColor(context, R.color.success));
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            } else {
                String correctText;
                if (Constants.QUIZ_TYPE_MCQ.equals(item.quizType)) {
                    correctText = "Correct answer: " + item.options[item.correctIndex];
                } else if (Constants.QUIZ_TYPE_FILL_BLANK.equals(item.quizType)) {
                    correctText = "Correct answer: " + item.correctAnswer;
                } else {
                    correctText = "Correct answer: " + item.correctAnswer;
                    if (item.sampleAnswer != null && !item.sampleAnswer.isEmpty()) {
                        correctText += "\nSample answer: " + item.sampleAnswer;
                    }
                }
                tvFeedback.setText("❌ Wrong!\n" + correctText);
                tvFeedback.setTextColor(ContextCompat.getColor(context, R.color.error));
            }
        }
    }
}
