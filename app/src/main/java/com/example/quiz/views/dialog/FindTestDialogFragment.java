package com.example.quiz.views.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.quiz.databinding.FragmentFindTestDialogBinding;
import com.example.quiz.models.Notification;
import com.example.quiz.viewmodels.FirebaseViewModel;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;


public class FindTestDialogFragment extends DialogFragment {
    FragmentFindTestDialogBinding binding;
    FirebaseViewModel firebaseViewModel;
    ArrayList<String> emailList;
    String userEmail;
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

        firebaseViewModel.getFindTestResult().observe(this, test -> {
            if (test!= null) {
                userEmail = firebaseViewModel.getUserInfo().getValue().getEmail();
                emailList = test.getStudentEmailList();
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
            boolean check = false;
            for (int i = 0; i < emailList.size(); i++) {
                if (userEmail.equals(emailList.get(i))) {
                    firebaseViewModel.addTestToRepoFirebase(Objects.requireNonNull(firebaseViewModel.getFindTestResult().getValue()).getTestID(), "joinTests");
                    Date now = Calendar.getInstance().getTime();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    String strDate = simpleDateFormat.format(now);
                    firebaseViewModel.saveNormalLog(new Notification("[" + firebaseViewModel.getFindTestResult().getValue().getTestName() + "]" + "You joined test successfully", strDate));
                    check = true;
                    break;
                }
            }

            if (check) {
                firebaseViewModel.getFindTestResult().setValue(null);

                Objects.requireNonNull(getDialog()).dismiss();
            }
            else {
                Toast.makeText(getContext(), "You can't add this test, tell the test owner!", Toast.LENGTH_SHORT).show();
            }
        });

        return builder;
    }

}
