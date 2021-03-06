package com.example.quiz.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quiz.databinding.TestResultReyclerviewItemBinding;
import com.example.quiz.models.Answer;
import com.example.quiz.views.interfaces.TestResultFragmentItemClicked;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;

public class TestResultAdapter extends RecyclerView.Adapter<TestResultAdapter.TestResultViewHolder> {
    ArrayList<Answer> list;
    TestResultFragmentItemClicked itemClicked;

    public TestResultAdapter(ArrayList<Answer> list, TestResultFragmentItemClicked itemClicked) {
        this.list = list;
        this.itemClicked = itemClicked;
    }

    public class TestResultViewHolder extends RecyclerView.ViewHolder {
        TestResultReyclerviewItemBinding binding;

        public TestResultViewHolder(@NonNull @NotNull TestResultReyclerviewItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        
        public void setContent(int index) {
            String id = index + 1 + "";
            binding.tvID.setText(id);
            binding.tvStudentName.setText(list.get(index).getParticipantName());
            binding.tvTimeFinish.setText(list.get(index).getTimeFinish());
            binding.tvResult.setText(list.get(index).getScore());
            binding.tvTimeDone.setText(list.get(index).getDuration());

            binding.getRoot().setOnClickListener(v -> itemClicked.onItemClicked(list.get(index)));
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
