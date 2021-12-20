package com.example.quiz.views.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.quiz.databinding.FragmentSendMailDialogBinding;
import com.example.quiz.viewmodels.FirebaseViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SendMailDialogFragment extends DialogFragment {
    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        FirebaseViewModel firebaseViewModel = new ViewModelProvider(requireActivity()).get(FirebaseViewModel.class);
        FragmentSendMailDialogBinding binding = FragmentSendMailDialogBinding.inflate(getLayoutInflater());
        Dialog builder = new Dialog(getActivity());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        firebaseViewModel.getMyTestList().observe(this, tests -> {
            if (tests != null) {
                ArrayList<String> list = new ArrayList<>();
                for (int i = 0; i < tests.size(); i++) {
                    list.add(tests.get(i).getTestName());
                }
                ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, list);
                binding.spinner.setAdapter(stringArrayAdapter);
            }
        });

        binding.btnSendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemSelected = binding.spinner.getSelectedItem().toString();
                getDialog().dismiss();
                final Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                ArrayList<String> list = new ArrayList<>();
                for (int i = 0; i < firebaseViewModel.getMyTestList().getValue().size(); i++) {
                    if (firebaseViewModel.getMyTestList().getValue().get(i).getTestName().equals(itemSelected)) {
                        list = firebaseViewModel.getMyTestList().getValue().get(i).getStudentEmailList();
                    }
                }
                String uri = "mailto:";
                for (int i = 0; i < list.size(); i++)
                    uri += (list.get(i) + ",");
                emailIntent.setType("message/rfc822");
                emailIntent.setData(Uri.parse(uri.substring(0, uri.length() -1)));
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });
        builder.setContentView(binding.getRoot());


        return builder;
    }
}
