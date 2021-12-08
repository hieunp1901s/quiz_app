package com.example.quiz.ui.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz.adapter.StudentAnswerAdapter;
import com.example.quiz.databinding.FragmentStudentAnswerInfoDialogBinding;
import com.example.quiz.models.Answer;
import com.example.quiz.viewmodels.FirebaseViewModel;

import org.jetbrains.annotations.NotNull;

public class StudentAnswerInfoDialogFragment extends DialogFragment {
    Answer answer;
    FragmentStudentAnswerInfoDialogBinding binding;
    public StudentAnswerInfoDialogFragment(Answer answer) {
        this.answer = answer;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentStudentAnswerInfoDialogBinding.inflate(getLayoutInflater());
        FirebaseViewModel firebaseViewModel = new ViewModelProvider(requireActivity()).get(FirebaseViewModel.class);


        Dialog builder = new Dialog(getActivity());
//        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.setContentView(binding.getRoot());

        String studentNameInfo = "Student: " + answer.getParticipantName();
        binding.tvStudentNameInfo.setText(studentNameInfo);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.rvAnswer.setLayoutManager(layoutManager);
        StudentAnswerAdapter adapter = new StudentAnswerAdapter(firebaseViewModel.getSelectedMyTest().getValue(), answer);
        binding.rvAnswer.setAdapter(adapter);

        return builder;
    }
}
