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

import com.example.quiz.databinding.FragmentMyTestInfoDialogBinding;
import com.example.quiz.models.Test;

import org.jetbrains.annotations.NotNull;

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
        return builder;
    }
}
