package com.example.quiz.views.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.quiz.databinding.FragmentRegisterDialogBinding;
import com.example.quiz.viewmodels.FirebaseViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RegisterDialogFragment extends DialogFragment {
    FragmentRegisterDialogBinding binding;
    public RegisterDialogFragment() {}

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterDialogBinding.inflate(getLayoutInflater());
        Dialog builder = new Dialog(getActivity());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.setContentView(binding.getRoot());

        FirebaseViewModel firebaseViewModel = new ViewModelProvider(requireActivity()).get(FirebaseViewModel.class);



        binding.btnCancel.setOnClickListener(v -> Objects.requireNonNull(getDialog()).dismiss());

        binding.btnConfirm.setOnClickListener(v -> {
            String userName = binding.etUsername.getText().toString();
            String email = binding.etEmail.getText().toString();
            String password = binding.etPassword.getText().toString();

            if (userName.length() > 0 && password.length() > 0 && email.length() > 0) {
                firebaseViewModel.getProgressing().setValue(true);
                firebaseViewModel.register(userName, email, password);
                Objects.requireNonNull(getDialog()).dismiss();
            }
            else
                Toast.makeText(getContext(), "Email Address and Password Must Be Entered", Toast.LENGTH_SHORT).show();
        });

        return builder;
    }

}
