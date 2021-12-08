package com.example.quiz.ui.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.quiz.databinding.FragmentFindTestDialogBinding;
import com.example.quiz.viewmodels.FirebaseViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class FindTestDialogFragment extends DialogFragment {
    FragmentFindTestDialogBinding binding;
    FirebaseViewModel firebaseViewModel;
    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentFindTestDialogBinding.inflate(getLayoutInflater());
        Dialog builder = new Dialog(getActivity());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.setContentView(binding.getRoot());

        firebaseViewModel = new ViewModelProvider(requireActivity()).get(FirebaseViewModel.class);

        firebaseViewModel.getFindTestResult().observe(getViewLifecycleOwner(), test -> {
            if (test!= null) {
                String testName = "Test name:" + test.getTestName();
                String time = "Duration:" + test.getDuration() + " minutes";
                String numberOfQuestions = test.getListQuestion().size() + " questions";
                binding.tvTestName.setText(testName);
                binding.tvTime.setText(time);
                binding.tvNumberOfQuestions.setText(numberOfQuestions);
            }

        });

        binding.btnCancel.setOnClickListener(v -> {
            firebaseViewModel.getFindTestResult().setValue(null);
            Objects.requireNonNull(getDialog()).dismiss();
        });

        binding.btnAdd.setOnClickListener(v -> {
            firebaseViewModel.addTestToRepoFirebase(Objects.requireNonNull(firebaseViewModel.getFindTestResult().getValue()).getTestID(), "joinTests");
            firebaseViewModel.getFindTestResult().setValue(null);
            Objects.requireNonNull(getDialog()).dismiss();
        });

        return builder;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentFindTestDialogBinding.inflate(inflater, container, false);


        return binding.getRoot();
    }
}
