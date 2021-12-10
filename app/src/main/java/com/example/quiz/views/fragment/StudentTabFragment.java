package com.example.quiz.views.fragment;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.quiz.adapter.JoinedTestAdapter;
import com.example.quiz.databinding.FragmentStudentTabBinding;
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

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.rvJoinTests.setLayoutManager(layoutManager);
        JoinedTestAdapter joinedTestAdapter = new JoinedTestAdapter(firebaseViewModel.getJoinedTestList(), this);
        binding.rvJoinTests.setAdapter(joinedTestAdapter);

        firebaseViewModel.getNewJoinedTestList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Test>>() {
            @Override
            public void onChanged(ArrayList<Test> tests) {
                if (tests != null)
                    joinedTestAdapter.updateListItem(tests);
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
}