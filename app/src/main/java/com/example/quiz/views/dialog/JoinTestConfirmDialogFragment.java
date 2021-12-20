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
import com.example.quiz.databinding.FragmentJoinConfirmDialogBinding;
import com.example.quiz.models.Notification;
import com.example.quiz.models.Test;
import com.example.quiz.util.CalculatingTimerForTest;
import com.example.quiz.viewmodels.FirebaseViewModel;
import com.example.quiz.viewmodels.QuestionViewModel;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class JoinTestConfirmDialogFragment extends DialogFragment {
    FragmentJoinConfirmDialogBinding binding;
    CalculatingTimerForTest calculatingTimerForTest;
    Test test;

    public JoinTestConfirmDialogFragment(Test test) {
        this.test = test;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentJoinConfirmDialogBinding.inflate(getLayoutInflater());
        FirebaseViewModel firebaseViewModel = new ViewModelProvider(requireActivity()).get(FirebaseViewModel.class);
        QuestionViewModel questionViewModel = new ViewModelProvider(requireActivity()).get(QuestionViewModel.class);
        calculatingTimerForTest = new CalculatingTimerForTest(test);

        Dialog builder = new Dialog(getActivity());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.setContentView(binding.getRoot());

        binding.etName.setText(Objects.requireNonNull(firebaseViewModel.getUserInfo().getValue()).getName());

        binding.btnConfirmJoin.setOnClickListener(v -> {
            if (calculatingTimerForTest.getResult() > 0) {
                //prepare data for test
                questionViewModel.getTest().setValue(test);
                questionViewModel.getTimer().setValue(calculatingTimerForTest.getResult());
                questionViewModel.getParticipantName().setValue(binding.etName.getText().toString());
                questionViewModel.getCancelTimer().setValue(false);
                Objects.requireNonNull(getDialog()).dismiss();
                Date now = Calendar.getInstance().getTime();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String strDate = simpleDateFormat.format(now);
                firebaseViewModel.saveNormalLog(new Notification("[" + test.getTestName() + "]" + " You started a test", strDate));

            }
            else {
                Objects.requireNonNull(getDialog()).dismiss();
                Toast.makeText(getContext(), "there's not time for test", Toast.LENGTH_SHORT).show();
            }

        });

        return builder;
    }
}
