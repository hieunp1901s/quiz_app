package com.example.quiz.ui.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.quiz.R;
import com.example.quiz.databinding.FragmentSubmitAnswerDialogBinding;
import com.example.quiz.models.Answer;
import com.example.quiz.viewmodels.FirebaseViewModel;
import com.example.quiz.viewmodels.QuestionViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SubmitAnswerDialogFragment extends DialogFragment {
    FragmentSubmitAnswerDialogBinding binding;
    Answer answer;

    public SubmitAnswerDialogFragment(Answer answer) {
        this.answer = answer;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentSubmitAnswerDialogBinding.inflate(getLayoutInflater());
        QuestionViewModel questionViewModel = new ViewModelProvider(requireActivity()).get(QuestionViewModel.class);
        FirebaseViewModel firebaseViewModel = new ViewModelProvider(requireActivity()).get(FirebaseViewModel.class);

        Dialog builder = new Dialog(getActivity());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.setContentView(binding.getRoot());

        binding.btnCancelSubmit.setOnClickListener(v -> Objects.requireNonNull(getDialog()).dismiss());

        binding.btnConfirmSubmit.setOnClickListener(v -> {
            firebaseViewModel.submitAnswerToRepoFirebase(answer, Objects.requireNonNull(questionViewModel.getTest().getValue()).getTestID());
            questionViewModel.getCancelTimer().setValue(true);
            Objects.requireNonNull(getDialog()).dismiss();
            Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment).navigate(R.id.action_global_homeFragment);
        });
        return builder;
    }
}
