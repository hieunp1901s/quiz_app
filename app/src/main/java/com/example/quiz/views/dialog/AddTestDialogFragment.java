package com.example.quiz.views.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.quiz.databinding.FragmentAddTestDialogBinding;
import com.example.quiz.models.Test;
import com.example.quiz.viewmodels.FirebaseViewModel;
import org.jetbrains.annotations.NotNull;
import java.util.Calendar;
import java.util.Objects;

public class AddTestDialogFragment extends DialogFragment {
    FragmentAddTestDialogBinding binding;
    String fileName;
    Test test;
    public AddTestDialogFragment(String fileName, Test test) {
        this.test = test;
        this.fileName = fileName;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentAddTestDialogBinding.inflate(getLayoutInflater());

        Dialog builder = new Dialog(getActivity());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.setContentView(binding.getRoot());

        binding.tvFile.setText(fileName);
        binding.etDate.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private final Calendar cal = Calendar.getInstance();
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("DefaultLocale")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]", "");
                    String cleanC = current.replaceAll("[^\\d.]", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        String ddmmyyyy = "DDMMYYYY";
                        clean = clean + ddmmyyyy.substring(clean.length());
                    }else{
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day  = Integer.parseInt(clean.substring(0,2));
                        int mon  = Integer.parseInt(clean.substring(2,4));
                        int year = Integer.parseInt(clean.substring(4,8));

                        if(mon > 12) mon = 12;
                        cal.set(Calendar.MONTH, mon-1);

//                        year = (year<1900)?1900:(year>2100)?2100:year;
                        year = (year<1900)?1900: Math.min(year, 2100);
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

//                        day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                        day = Math.min(day, cal.getActualMaximum(Calendar.DATE));
                        clean = String.format("%02d%02d%02d",day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

//                    sel = sel < 0 ? 0 : sel;
                    sel = Math.max(sel, 0);
                    current = clean;
                    binding.etDate.setText(current);
//                    binding.etDate.setSelection(sel < current.length() ? sel : current.length());
                    binding.etDate.setSelection(Math.min(sel, current.length()));

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.tvTimeStart.setOnClickListener(v -> {
            @SuppressLint("DefaultLocale") TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), (timePicker, hourOfDay, minutes) -> {
                String timeStart = hourOfDay + ":" + String.format("%02d", minutes);
                binding.tvTimeStart.setText(timeStart);}, 0, 0, false);
            timePickerDialog.show();
        });


        binding.btnConfirmAddTest.setOnClickListener(v -> {
            if (binding.etTestName.getText().toString().length() > 0
                    && binding.tvTimeStart.getText().toString().length() > 0
                    && binding.etDuration.getText().toString().length() > 0
                    && binding.etDate.getText().toString().length() > 0) {

                test.setTestName(binding.etTestName.getText().toString());
                test.setStartTime(binding.tvTimeStart.getText().toString());
                test.setDuration(binding.etDuration.getText().toString());
                test.setDate(binding.etDate.getText().toString());
                if (binding.cbMix.isChecked())
                    test.setMix("true");
                else
                    test.setMix("false");
                FirebaseViewModel firebaseViewModel = new ViewModelProvider(requireActivity()).get(FirebaseViewModel.class);
                firebaseViewModel.addTestFirebase(test);
                Objects.requireNonNull(getDialog()).dismiss();
            }

            else
                Toast.makeText(getContext(), "All Field Must Be Entered", Toast.LENGTH_SHORT).show();

        });

        binding.btnCancelAddTest.setOnClickListener(v -> Objects.requireNonNull(getDialog()).dismiss());

        return builder;
    }

}
