package com.example.quiz.views.fragment;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.quiz.adapter.TestResultAdapter;
import com.example.quiz.databinding.FragmentTestResultBinding;
import com.example.quiz.models.Answer;
import com.example.quiz.views.interfaces.TestResultFragmentItemClicked;
import com.example.quiz.views.dialog.StudentAnswerInfoDialogFragment;
import com.example.quiz.viewmodels.FirebaseViewModel;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TestResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestResultFragment extends Fragment implements TestResultFragmentItemClicked {
    FragmentTestResultBinding binding;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TestResultFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TestResultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TestResultFragment newInstance(String param1, String param2) {
        TestResultFragment fragment = new TestResultFragment();
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
        FirebaseViewModel firebaseViewModel = new ViewModelProvider(requireActivity()).get(FirebaseViewModel.class);

        binding = FragmentTestResultBinding.inflate(getLayoutInflater());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.rvTestResult.setLayoutManager(layoutManager);

        firebaseViewModel.getFinishGetResult().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                TestResultAdapter testResultAdapter = new TestResultAdapter(firebaseViewModel.getAnswerList(), TestResultFragment.this);
                binding.rvTestResult.setAdapter(testResultAdapter);
                firebaseViewModel.getFinishGetResult().setValue(false);
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
    public void onItemClicked(Answer answer) {
        DialogFragment dialogFragment = new StudentAnswerInfoDialogFragment(answer);
        dialogFragment.show(getParentFragmentManager(), "student answer");
    }
}