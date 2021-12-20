package com.example.quiz.views.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.quiz.adapter.JoinedTestAdapter;
import com.example.quiz.databinding.FragmentStudentTabBinding;
import com.example.quiz.models.User;
import com.example.quiz.views.dialog.FindTestDialogFragment;
import com.example.quiz.views.interfaces.StudentTabFragmentItemClicked;
import com.example.quiz.models.Test;
import com.example.quiz.views.dialog.JoinTestConfirmDialogFragment;
import com.example.quiz.views.dialog.SimpleAlertDialogFragment;
import com.example.quiz.viewmodels.FirebaseViewModel;
import com.example.quiz.viewmodels.QuestionViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StudentTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudentTabFragment extends Fragment implements StudentTabFragmentItemClicked {
    FirebaseViewModel firebaseViewModel;
    QuestionViewModel questionViewModel;
    FragmentStudentTabBinding binding;
    Test test;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StudentTabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StudentTabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StudentTabFragment newInstance(String param1, String param2) {
        StudentTabFragment fragment = new StudentTabFragment();
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
        // Inflate the layout for this fragment
        binding = FragmentStudentTabBinding.inflate(inflater, container, false);
        firebaseViewModel = new ViewModelProvider(requireActivity()).get(FirebaseViewModel.class);
        questionViewModel = new ViewModelProvider(requireActivity()).get(QuestionViewModel.class);



        firebaseViewModel.getCheckAdmin().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == 0) {
                    requireActivity().getWindow().getDecorView().setSystemUiVisibility(0);
                    requireActivity().getWindow().setStatusBarColor(Color.parseColor("#8B80B6"));
                    binding.studentLayoutTrue.setVisibility(View.VISIBLE);
                    init();
                }
                else if (integer == 1){
                    requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    requireActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
                    binding.studentLayoutFalse.setVisibility(View.VISIBLE);
                }
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClicked(Test test) {
        this.test = test;
        firebaseViewModel.getProgressing().setValue(true);
        firebaseViewModel.checkIfAnswerSubmitted(test.getTestID());
    }

    public void init() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.rvJoinTests.setLayoutManager(layoutManager);
        JoinedTestAdapter joinedTestAdapter = new JoinedTestAdapter(this);
        binding.rvJoinTests.setAdapter(joinedTestAdapter);

        firebaseViewModel.getJoinedTestList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Test>>() {
            @Override
            public void onChanged(ArrayList<Test> tests) {
                joinedTestAdapter.submitList(tests);
            }
        });

        firebaseViewModel.getUserInfo().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user!=null) {
                    binding.tvUser.setText(user.getName());
                }
            }
        });


        firebaseViewModel.getCheckAnswerSubmitted().observe(getViewLifecycleOwner(), integer -> {
            if (integer == 1) {
                firebaseViewModel.getCheckAnswerSubmitted().setValue(3);
                firebaseViewModel.getProgressing().setValue(false);
                DialogFragment dialogFragment = new SimpleAlertDialogFragment("You already submitted!");
                dialogFragment.show(getParentFragmentManager(), "already submitted");
            }
            else if (integer == 0) {
                firebaseViewModel.getCheckAnswerSubmitted().setValue(3);
                firebaseViewModel.getProgressing().setValue(false);
                DialogFragment dialogFragment = new JoinTestConfirmDialogFragment(test);
                dialogFragment.show(getParentFragmentManager(), "join confirm");
            }
        });

        binding.btnFindTest.setOnClickListener(v -> {
            if (!binding.etFindTest.getText().toString().equals("")) {
                firebaseViewModel.findTestFromFirebase(binding.etFindTest.getText().toString());
            }
        });

        firebaseViewModel.getFindTestResult().observe(getViewLifecycleOwner(), test -> {
            if (test != null) {
                binding.etFindTest.setText("");
                DialogFragment dialogFragment = new FindTestDialogFragment();
                dialogFragment.setCancelable(false);
                dialogFragment.show(getParentFragmentManager(), "find test");
            }
        });

        firebaseViewModel.getIsFindTestResultNull().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean == null) {

            }
            else if (aBoolean) {
                binding.tvAlert.setText("Can't find test!");
                firebaseViewModel.getIsFindTestResultNull().setValue(null);
            }

            else {
                binding.tvAlert.setText("Network error!");
                firebaseViewModel.getIsFindTestResultNull().setValue(null);
            }

        });

        binding.etFindTest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tvAlert.setText("Enter test ID");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}