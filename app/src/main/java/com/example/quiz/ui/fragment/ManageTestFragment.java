package com.example.quiz.ui.fragment;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.quiz.R;
import com.example.quiz.databinding.FragmentManageTestBinding;
import com.example.quiz.models.Test;
import com.example.quiz.viewmodels.FirebaseViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ManageTestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManageTestFragment extends Fragment {
    FirebaseViewModel firebaseViewModel;
    FragmentManageTestBinding binding;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ManageTestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ManageTestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ManageTestFragment newInstance(String param1, String param2) {
        ManageTestFragment fragment = new ManageTestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        firebaseViewModel = new ViewModelProvider(requireActivity()).get(FirebaseViewModel.class);
        Test test = firebaseViewModel.getSelectedMyTest().getValue();

        binding = FragmentManageTestBinding.inflate(getLayoutInflater());
        String testName = Objects.requireNonNull(test).getTestName();
        String timeStart = test.getStartTime();
        String duration = test.getDuration();
        String mix = test.getMix();
        String date = test.getDate();


        binding.etTestName.setText(testName);
        binding.tvTimeStart.setText(timeStart);
        binding.etDuration.setText(duration);
        binding.cbMix.setChecked(mix.equals("true"));

        binding.etDate.setText(date);

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
                String time_Start = hourOfDay + ":" + String.format("%02d", minutes);
                binding.tvTimeStart.setText(time_Start);}, 0, 0, false);
            timePickerDialog.show();
        });

        binding.btnCancelAddTest.setOnClickListener(v -> Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment).popBackStack());

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
                firebaseViewModel.manageTest(test);
                Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment).popBackStack();
            }

            else
                Toast.makeText(getContext(), "All Field Must Be Entered", Toast.LENGTH_SHORT).show();

        });


        return binding.getRoot();
    }
}