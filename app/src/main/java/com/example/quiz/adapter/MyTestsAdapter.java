package com.example.quiz.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz.databinding.MyListsRecylcerviewItemBinding;
import com.example.quiz.models.TeacherTabFragmentItemClicked;
import com.example.quiz.models.Test;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MyTestsAdapter extends RecyclerView.Adapter<MyTestsAdapter.MyTestsViewHolder> {
    private final ArrayList<Test> myTests;
    TeacherTabFragmentItemClicked itemClicked;

    public MyTestsAdapter(ArrayList<Test> myTests, TeacherTabFragmentItemClicked itemClicked) {
        this.myTests = myTests;
        this.itemClicked = itemClicked;
    }
    public void notifyAdapter() {
        notifyDataSetChanged();
    }

    public class MyTestsViewHolder extends RecyclerView.ViewHolder {
        MyListsRecylcerviewItemBinding binding;

        public MyTestsViewHolder(@NonNull @NotNull MyListsRecylcerviewItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

        public void setContent(int index) {
            String testName = "Test name: " + myTests.get(index).getTestName();
            String startTime = "Start time: " + myTests.get(index).getStartTime()  + " - " + myTests.get(index).getDate();
            String numberOfQuestions = myTests.get(index).getListQuestion().size() + " questions" + " (" + myTests.get(index).getDuration() + " minutes)";

            binding.tvTestName.setText(testName);
            binding.tvTime.setText(startTime);
            binding.tvNumberOfQuestions.setText(numberOfQuestions);
            binding.getRoot().setOnClickListener(v -> itemClicked.onItemClicked(myTests.get(index)));
        }
    }
    @NonNull
    @NotNull
    @Override
    public MyTestsAdapter.MyTestsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        MyListsRecylcerviewItemBinding binding = MyListsRecylcerviewItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyTestsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyTestsAdapter.MyTestsViewHolder holder, int position) {
        holder.setContent(position);
    }

    @Override
    public int getItemCount() {
        if (myTests == null)
            return 0;
        else
            return myTests.size();
    }
}
