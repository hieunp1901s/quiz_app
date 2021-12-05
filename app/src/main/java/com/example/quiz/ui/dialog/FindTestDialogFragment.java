package com.example.quiz.ui.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.quiz.R;
import com.example.quiz.databinding.FragmentFindTestDialogBinding;
import com.example.quiz.models.Test;
import com.example.quiz.viewmodels.FirebaseViewModel;

import org.jetbrains.annotations.NotNull;

public class FindTestDialogFragment extends DialogFragment {
    FragmentFindTestDialogBinding binding;
    FirebaseViewModel firebaseViewModel;
    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_find_test_dialog, new LinearLayout(getActivity()), false);
        Dialog builder = new Dialog(getActivity());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.setContentView(view);
        return builder;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentFindTestDialogBinding.inflate(inflater, container, false);
        firebaseViewModel = new ViewModelProvider(requireActivity()).get(FirebaseViewModel.class);

        firebaseViewModel.getFindTestResult().observe(getViewLifecycleOwner(), new Observer<Test>() {
            @Override
            public void onChanged(Test test) {
                if (test!= null) {
                    binding.tvTestName.setText("Test name:" + test.getTestName());
                    binding.tvTime.setText("Duration:" + test.getDuration() + " minutes");
                    binding.tvNumberOfQuestions.setText(test.getListQuestion().size() + " questions");
                }

            }
        });

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseViewModel.getFindTestResult().setValue(null);
                getDialog().dismiss();
            }
        });

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseViewModel.addTestToRepoFirebase(firebaseViewModel.getFindTestResult().getValue().getTestID(), "joinTests");
                firebaseViewModel.getFindTestResult().setValue(null);
                getDialog().dismiss();
            }
        });

        return binding.getRoot();
    }
}
