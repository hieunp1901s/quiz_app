package com.example.quiz.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz.R;
import com.example.quiz.databinding.JoinListsRecyclerviewItemBinding;
import com.example.quiz.models.StudentTabFragmentItemClicked;
import com.example.quiz.models.Test;
import com.example.quiz.util.CalculatingTimerForTest;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;


public class JoinTestsAdapter extends RecyclerView.Adapter<JoinTestsAdapter.JoinTestsViewHolder> {
    private final ArrayList<Test> joinTests;
    private final StudentTabFragmentItemClicked studentTabFragmentItemClicked;

    public JoinTestsAdapter(ArrayList<Test> joinTests, StudentTabFragmentItemClicked studentTabFragmentItemClicked) {
        this.joinTests = joinTests;
        this.studentTabFragmentItemClicked = studentTabFragmentItemClicked;
    }
    public void notifyAdapter() {
        notifyDataSetChanged();
    }

    public class JoinTestsViewHolder extends RecyclerView.ViewHolder {
        JoinListsRecyclerviewItemBinding binding;
        CalculatingTimerForTest calculatingTimerForTest;
        public JoinTestsViewHolder(@NonNull @NotNull JoinListsRecyclerviewItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
        public void setContent(int index) {
            String testName = "Test name: " + joinTests.get(index).getTestName();
            String startTime = "Start time: " + joinTests.get(index).getStartTime()  + " - " + joinTests.get(index).getDate();
            String numberOfQuestions = joinTests.get(index).getListQuestion().size() + " questions" + " (" + joinTests.get(index).getDuration() + " minutes)";

            binding.tvTestName.setText(testName);
            binding.tvTime.setText(startTime);
            binding.tvNumberOfQuestions.setText(numberOfQuestions);

            calculatingTimerForTest = new CalculatingTimerForTest(joinTests.get(index));
            if (calculatingTimerForTest.getResult() > 0) {
                binding.ivIcon.setImageResource(R.mipmap.test_icon_available);
            }

            binding.getRoot().setOnClickListener(v -> {
                if (calculatingTimerForTest.getResult() > 0) {
                    studentTabFragmentItemClicked.onItemClicked(joinTests.get(index));
                }
                else
                    Toast.makeText(itemView.getContext(), "not time", Toast.LENGTH_SHORT).show();
            });
        }

    }
    @NonNull
    @NotNull
    @Override
    public JoinTestsAdapter.JoinTestsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        JoinListsRecyclerviewItemBinding binding = JoinListsRecyclerviewItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new JoinTestsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull JoinTestsAdapter.JoinTestsViewHolder holder, int position) {
        holder.setContent(position);
    }

    @Override
    public int getItemCount() {
        if (joinTests == null)
            return 0;
        else
            return joinTests.size();
    }
}
