package com.example.quiz.adapter;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quiz.databinding.MyListsRecylcerviewItemBinding;
import com.example.quiz.views.interfaces.TeacherTabFragmentItemClicked;
import com.example.quiz.models.Test;
import org.jetbrains.annotations.NotNull;

public class MyTestAdapter extends ListAdapter<Test, MyTestAdapter.MyTestsViewHolder> {
    TeacherTabFragmentItemClicked itemClicked;

    public MyTestAdapter(TeacherTabFragmentItemClicked itemClicked) {
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


    public class MyTestsViewHolder extends RecyclerView.ViewHolder {
        MyListsRecylcerviewItemBinding binding;

        public MyTestsViewHolder(@NonNull @NotNull MyListsRecylcerviewItemBinding binding) {
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
        }
    }
    @NonNull
    @NotNull
    @Override
    public MyTestAdapter.MyTestsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        MyListsRecylcerviewItemBinding binding = MyListsRecylcerviewItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyTestsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyTestAdapter.MyTestsViewHolder holder, int position) {
        holder.bindTo(getItem(position));
    }

}
