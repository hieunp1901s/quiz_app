package com.example.quiz.ui.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.Navigation;

import com.example.quiz.R;
import com.example.quiz.databinding.FragmentExitTestConfirmDialogBinding;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ExitTestConfirmDialogFragment extends DialogFragment {
    FragmentExitTestConfirmDialogBinding binding;
    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentExitTestConfirmDialogBinding.inflate(getLayoutInflater());

        Dialog builder = new Dialog(getActivity());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.setContentView(binding.getRoot());

        binding.btnCancelExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getDialog()).dismiss();
            }
        });

        binding.btnConfirmExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getDialog()).dismiss();
                Navigation.findNavController(getActivity(), R.id.main_nav_host_fragment).navigate(R.id.action_global_homeFragment);
            }
        });

        return builder;
    }

}
