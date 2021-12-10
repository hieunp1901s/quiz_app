package com.example.quiz.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quiz.R;
import com.example.quiz.databinding.JoinListsRecyclerviewItemBinding;
import com.example.quiz.models.TestDiffUtilCallback;
import com.example.quiz.views.interfaces.StudentTabFragmentItemClicked;
import com.example.quiz.models.Test;
import com.example.quiz.util.CalculatingTimerForTest;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;


public class JoinedTestAdapter extends RecyclerView.Adapter<JoinedTestAdapter.JoinTestsViewHolder> {
    private final ArrayList<Test> joinedTest;
    private final StudentTabFragmentItemClicked studentTabFragmentItemClicked;

    public JoinedTestAdapter(ArrayList<Test> joinTests, StudentTabFragmentItemClicked studentTabFragmentItemClicked) {
        this.joinedTest = joinTests;
        this.studentTabFragmentItemClicked = studentTabFragmentItemClicked;
    }

    public void updateListItem(ArrayList<Test> newList) {
        final TestDiffUtilCallback diffUtilCallback = new TestDiffUtilCallback(this.joinedTest, newList);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtilCallback);

        this.joinedTest.clear();
        this.joinedTest.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    public class JoinTestsViewHolder extends RecyclerView.ViewHolder {
        JoinListsRecyclerviewItemBinding binding;
        CalculatingTimerForTest calculatingTimerForTest;
        public JoinTestsViewHolder(@NonNull @NotNull JoinListsRecyclerviewItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
        public void setContent(int index) {
            String testName = "Test name: " + joinedTest.get(index).getTestName();
            String startTime = "Start time: " + joinedTest.get(index).getStartTime()  + " - " + joinedTest.get(index).getDate();
            String numberOfQuestions = joinedTest.get(index).getListQuestion().size() + " questions" + " (" + joinedTest.get(index).getDuration() + " minutes)";

            binding.tvTestName.setText(testName);
            binding.tvTime.setText(startTime);
            binding.tvNumberOfQuestions.setText(numberOfQuestions);

            calculatingTimerForTest = new CalculatingTimerForTest(joinedTest.get(index));
            if (calculatingTimerForTest.getResult() > 0) {
                binding.ivIcon.setImageResource(R.mipmap.test_icon_available);
            }

            binding.getRoot().setOnClickListener(v -> {
                if (calculatingTimerForTest.getResult() > 0) {
                    studentTabFragmentItemClicked.onItemClicked(joinedTest.get(index));
                }
                else
                    Toast.makeText(itemView.getContext(), "not time", Toast.LENGTH_SHORT).show();
            });
        }

    }
    @NonNull
    @NotNull
    @Override
    public JoinedTestAdapter.JoinTestsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        JoinListsRecyclerviewItemBinding binding = JoinListsRecyclerviewItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new JoinTestsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull JoinedTestAdapter.JoinTestsViewHolder holder, int position) {
        holder.setContent(position);
    }

    @Override
    public int getItemCount() {
        if (joinedTest == null)
            return 0;
        else
            return joinedTest.size();
    }
}
