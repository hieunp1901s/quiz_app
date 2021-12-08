package com.example.quiz.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz.databinding.StudentAnswerRecyclerviewItemBinding;
import com.example.quiz.models.Answer;
import com.example.quiz.models.Test;

import org.jetbrains.annotations.NotNull;

public class StudentAnswerAdapter extends RecyclerView.Adapter<StudentAnswerAdapter.StudentAnswerViewHolder> {
    Test test;
    Answer answer;

    public StudentAnswerAdapter(Test test, Answer answer) {
        this.test = test;
        this.answer = answer;
    }

    public class StudentAnswerViewHolder extends RecyclerView.ViewHolder {
        StudentAnswerRecyclerviewItemBinding binding;
        public StudentAnswerViewHolder(@NonNull @NotNull StudentAnswerRecyclerviewItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void setContent(int index) {
            String question = "Question " + (index + 1) + ": " + test.getListQuestion().get(index).getQuestion();
            String rightAnswer;

            switch (test.getListQuestion().get(index).getCorrectAnswer()) {
                case "1":
                    rightAnswer = "Correct Answer: " + test.getListQuestion().get(index).getAnswer_1();
                    break;
                case "2":
                    rightAnswer = "Correct Answer: " + test.getListQuestion().get(index).getAnswer_2();
                    break;
                case "3":
                    rightAnswer = "Correct Answer: " + test.getListQuestion().get(index).getAnswer_3();
                    break;
                case "4":
                    rightAnswer = "Correct Answer: " + test.getListQuestion().get(index).getAnswer_4();
                    break;
                default:
                    rightAnswer = "Correct Answer: ";
            }

            String studentAnswer;
            switch (answer.getListAnswer().get(index)) {
                case "1":
                    studentAnswer = "Student's Answer: " + test.getListQuestion().get(index).getAnswer_1();
                    break;
                case "2":
                    studentAnswer = "Student's Answer: " + test.getListQuestion().get(index).getAnswer_2();
                    break;
                case "3":
                    studentAnswer = "Student's Answer: " + test.getListQuestion().get(index).getAnswer_3();
                    break;
                case "4":
                    studentAnswer = "Student's Answer: " + test.getListQuestion().get(index).getAnswer_4();
                    break;
                default:
                    studentAnswer = "Student's Answer :";
            }

            binding.tvTestQuestion.setText(question);
            binding.tvRightAnswer.setText(rightAnswer);
            binding.tvStudentAnswer.setText(studentAnswer);
        }
    }

    @NonNull
    @NotNull
    @Override
    public StudentAnswerAdapter.StudentAnswerViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        StudentAnswerRecyclerviewItemBinding binding = StudentAnswerRecyclerviewItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new StudentAnswerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull StudentAnswerAdapter.StudentAnswerViewHolder holder, int position) {
        holder.setContent(position);
    }

    @Override
    public int getItemCount() {
        return answer.getListAnswer().size();
    }
}
