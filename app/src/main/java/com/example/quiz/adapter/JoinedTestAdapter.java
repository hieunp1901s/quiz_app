package com.example.quiz.adapter;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quiz.R;
import com.example.quiz.databinding.JoinListsRecyclerviewItemBinding;
import com.example.quiz.views.interfaces.StudentTabFragmentItemClicked;
import com.example.quiz.models.Test;
import com.example.quiz.util.CalculatingTimerForTest;
import org.jetbrains.annotations.NotNull;

public class JoinedTestAdapter extends ListAdapter<Test, JoinedTestAdapter.JoinTestsViewHolder> {
    private final StudentTabFragmentItemClicked itemClicked;

    public JoinedTestAdapter(StudentTabFragmentItemClicked itemClicked) {
        super(DIFF_CALLBACK);
        this.itemClicked = itemClicked;
    }

    public static final DiffUtil.ItemCallback<Test> DIFF_CALLBACK = new DiffUtil.ItemCallback<Test>() {
        @Override
        public boolean areItemsTheSame(@NonNull @NotNull Test oldItem, @NonNull @NotNull Test newItem) {
            return oldItem.getTestID().equals(newItem.getTestID());
        }

        @Override
        public boolean areContentsTheSame(@NonNull @NotNull Test oldItem, @NonNull @NotNull Test newItem) {
            return oldItem.equals(newItem);
        }
    };

    public class JoinTestsViewHolder extends RecyclerView.ViewHolder {
        JoinListsRecyclerviewItemBinding binding;
        CalculatingTimerForTest calculatingTimerForTest;
        public JoinTestsViewHolder(@NonNull @NotNull JoinListsRecyclerviewItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

        public void bindTo(Test test) {
            String testName = "Test name: " + test.getTestName();
            String startTime = "Start time: " + test.getStartTime()  + " - " + test.getDate();
            String numberOfQuestions = test.getListQuestion().size() + " questions" + " (" + test.getDuration() + " minutes)";

            binding.tvTestName.setText(testName);
            binding.tvTime.setText(startTime);
            binding.tvNumberOfQuestions.setText(numberOfQuestions);
            binding.getRoot().setOnClickListener(v -> itemClicked.onItemClicked(test));

            calculatingTimerForTest = new CalculatingTimerForTest(test);
            if (calculatingTimerForTest.getResult() > 0) {
                binding.ivIcon.setImageResource(R.mipmap.test_icon_available);
            }

            binding.getRoot().setOnClickListener(v -> {
                if (calculatingTimerForTest.getResult() > 0) {
                    itemClicked.onItemClicked(test);
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
        holder.bindTo(getItem(position));
    }

}
