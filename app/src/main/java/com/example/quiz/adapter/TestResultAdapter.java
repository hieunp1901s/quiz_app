package com.example.quiz.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz.databinding.FragmentTestResultBinding;
import com.example.quiz.databinding.TestResultReyclerviewItemBinding;
import com.example.quiz.models.Answer;
import com.example.quiz.models.Test;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;

public class TestResultAdapter extends RecyclerView.Adapter<TestResultAdapter.TestResultViewHolder> {
    Test test;
    ArrayList<Answer> list;

    public TestResultAdapter(ArrayList<Answer> list) {
        this.list = list;
    }

    public void notifyAdapter() {
        notifyDataSetChanged();
    }

    public class TestResultViewHolder extends RecyclerView.ViewHolder {
        TestResultReyclerviewItemBinding binding;

        public TestResultViewHolder(@NonNull @NotNull TestResultReyclerviewItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        
        public void setContent(int index) {
            binding.tvID.setText(index + 1 + "");
            binding.tvStudentName.setText(list.get(index).getParticipantName());
            binding.tvTimeFinish.setText(list.get(index).getTimeFinish());
            binding.tvResult.setText(list.get(index).getScore());
            binding.tvTimeDone.setText(list.get(index).getDuration());
        }
    }

    @NonNull
    @NotNull
    @Override
    public TestResultAdapter.TestResultViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        TestResultReyclerviewItemBinding binding = TestResultReyclerviewItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TestResultViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TestResultAdapter.TestResultViewHolder holder, int position) {
        holder.setContent(position);
    }

    @Override
    public int getItemCount() {
        if (list != null)
            return list.size();
        else 
            return 0;
    }
}
