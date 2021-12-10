package com.example.quiz.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quiz.databinding.QuestionNavRecyclerviewItemBinding;
import com.example.quiz.views.interfaces.QuestionFragmentItemClicked;
import org.jetbrains.annotations.NotNull;

public class QuestionNavAdapter extends RecyclerView.Adapter<QuestionNavAdapter.QuestionNavViewHolder> {
    private final Integer size;
    QuestionFragmentItemClicked questionFragmentItemClicked;
    public QuestionNavAdapter(Integer size, QuestionFragmentItemClicked questionFragmentItemClicked) {
        this.size = size;
        this.questionFragmentItemClicked = questionFragmentItemClicked;
    }

    public class QuestionNavViewHolder extends RecyclerView.ViewHolder {
        QuestionNavRecyclerviewItemBinding binding;
        public QuestionNavViewHolder(@NonNull @NotNull QuestionNavRecyclerviewItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setContent(int index) {
            String text = index + 1 + "";
            binding.btnNav.setText(text);
            binding.btnNav.setOnClickListener(v -> questionFragmentItemClicked.onItemCLicked(index));
        }
    }

    @NonNull
    @NotNull
    @Override
    public QuestionNavAdapter.QuestionNavViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        QuestionNavRecyclerviewItemBinding binding = QuestionNavRecyclerviewItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new QuestionNavViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull QuestionNavAdapter.QuestionNavViewHolder holder, int position) {
        holder.setContent(position);
    }

    @Override
    public int getItemCount() {
        return size;
    }
}
