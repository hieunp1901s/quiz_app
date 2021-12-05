package com.example.quiz.ui.dialog;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.quiz.R;
import com.example.quiz.databinding.FragmentMyTestInfoDialogBinding;
import com.example.quiz.models.Test;
import com.example.quiz.viewmodels.FirebaseViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MyTestInfoDialogFragment extends DialogFragment {
    Test test;
    FragmentMyTestInfoDialogBinding binding;
    public MyTestInfoDialogFragment(Test test) {
        this.test = test;
    }
    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        FirebaseViewModel firebaseViewModel = new ViewModelProvider(requireActivity()).get(FirebaseViewModel.class);
        binding = FragmentMyTestInfoDialogBinding.inflate(getLayoutInflater());
        Dialog builder = new Dialog(getActivity());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.setContentView(binding.getRoot());

        binding.tvTestID.setText(test.getTestID());
        binding.btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("test id", binding.tvTestID.getText().toString());
                clipboard.setPrimaryClip(clip);
            }
        });

        binding.btnResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseViewModel.getTestResult(test.getTestID());
                Navigation.findNavController(getActivity(), R.id.main_nav_host_fragment).navigate(R.id.action_homeFragment_to_testResultFragment);
            }
        });

        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseViewModel.deleteTest(test.getTestID());
                Objects.requireNonNull(getDialog()).dismiss();
            }
        });

        binding.btnManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getActivity(), R.id.main_nav_host_fragment).navigate(R.id.action_homeFragment_to_manageTestFragment);
            }
        });
        return builder;
    }
}
