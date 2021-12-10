package com.example.quiz.views.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.quiz.databinding.FragmentSimpleAlertDialogBinding;
import com.example.quiz.viewmodels.FirebaseViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SimpleAlertDialogFragment extends DialogFragment {
    private final String message;

    public SimpleAlertDialogFragment(String message) {
        this.message = message;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        FragmentSimpleAlertDialogBinding binding = FragmentSimpleAlertDialogBinding.inflate(getLayoutInflater());

        FirebaseViewModel firebaseViewModel = new ViewModelProvider(requireActivity()).get(FirebaseViewModel.class);

        Dialog builder = new Dialog(getActivity());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.setContentView(binding.getRoot());

        binding.tvMessage.setText(message);

        binding.btnExit.setOnClickListener(v -> {
            firebaseViewModel.getCheckAnswerSubmitted().setValue(3);
            Objects.requireNonNull(getDialog()).dismiss();
        });

        return builder;
    }
}
