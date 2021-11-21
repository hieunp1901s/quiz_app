package com.example.quiz.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.example.quiz.R;
import com.example.quiz.adapter.QuestionNavAdapter;
import com.example.quiz.databinding.FragmentQuestionBinding;
import com.example.quiz.models.Answer;
import com.example.quiz.models.QuestionFragmentItemClicked;
import com.example.quiz.models.Test;
import com.example.quiz.ui.dialog.ExitTestConfirmDialogFragment;
import com.example.quiz.ui.dialog.SubmitAnswerDialogFragment;
import com.example.quiz.viewmodels.FirebaseViewModel;
import com.example.quiz.viewmodels.QuestionViewModel;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuestionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuestionFragment extends Fragment implements QuestionFragmentItemClicked {
    FragmentQuestionBinding binding;
    FirebaseViewModel firebaseViewModel;
    QuestionViewModel questionViewModel;
    static BottomSheetBehavior bottomSheetBehavior;
    Test test;
    ArrayList<String> listAnswer;
    Answer answer;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public QuestionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QuestionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QuestionFragment newInstance(String param1, String param2) {
        QuestionFragment fragment = new QuestionFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentQuestionBinding.inflate(inflater, container, false);
        firebaseViewModel = new ViewModelProvider(requireActivity()).get(FirebaseViewModel.class);
        questionViewModel = new ViewModelProvider(requireActivity()).get(QuestionViewModel.class);
        test = questionViewModel.getTest().getValue();
        listAnswer = new ArrayList<>();
        //setup for list answer

        for (int i = 0; i < test.getListQuestion().size(); i++) {
            listAnswer.add("1");
        }

        answer = new Answer();

        questionViewModel.getNavIndex().postValue(0);

        questionViewModel.getNavIndex().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer != null) {
                    binding.tvCurrentQuestion.setText("Question " + (integer + 1) + "/" + test.getListQuestion().size());
                    binding.tvQuestion.setText(test.getListQuestion().get(integer).getQuestion());
                    binding.rbAnswer1.setText(test.getListQuestion().get(integer).getAnswer_1());
                    binding.rbAnswer2.setText(test.getListQuestion().get(integer).getAnswer_2());
                    binding.rbAnswer3.setText(test.getListQuestion().get(integer).getAnswer_3());
                    binding.rbAnswer4.setText(test.getListQuestion().get(integer).getAnswer_4());
                }
            }
        });

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (questionViewModel.getNavIndex().getValue() < test.getListQuestion().size() - 1) {
                    binding.rgGroupAnswer.clearCheck();
                    binding.rgGroupAnswer.jumpDrawablesToCurrentState();
                    questionViewModel.getNavIndex().postValue(questionViewModel.getNavIndex().getValue() + 1);
                }

            }
        });

        binding.btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (questionViewModel.getNavIndex().getValue() > 0) {
                    binding.rgGroupAnswer.clearCheck();
                    binding.rgGroupAnswer.jumpDrawablesToCurrentState();
                    questionViewModel.getNavIndex().postValue(questionViewModel.getNavIndex().getValue() - 1);
                }

            }
        });

        //timer
        new CountDownTimer(questionViewModel.getTimer().getValue(), 1000) {

            public void onTick(long millisUntilFinished) {
                long minute = millisUntilFinished / (1000 * 60);
                long second = millisUntilFinished / 1000 - minute * 60;
                binding.tvTimer.setText("Time left " + minute + ":" + String.format("%02d", second));
            }

            public void onFinish() {
//                firebaseViewModel.submitAnswerToRepoFirebase(answer, questionViewModel.getTest().getValue().getTestID());
            }
        }.start();

        //setup bottomsheet
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet);
        bottomSheetBehavior.setSkipCollapsed(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        //recyclerview with flex box for inside bottomsheet
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        binding.rvNavQuestion.setLayoutManager(layoutManager);
        QuestionNavAdapter adapter = new QuestionNavAdapter(questionViewModel.getTest().getValue().getListQuestion().size(), this);
        binding.rvNavQuestion.setAdapter(adapter);

        binding.btnListQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
            }
        });

        binding.rgGroupAnswer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbAnswer1:
                        listAnswer.set(questionViewModel.getNavIndex().getValue(), "1");
                        break;
                    case R.id.rbAnswer2:
                        listAnswer.set(questionViewModel.getNavIndex().getValue(), "2");
                        break;
                    case R.id.rbAnswer3:
                        listAnswer.set(questionViewModel.getNavIndex().getValue(), "3");
                        break;
                    case R.id.rbAnswer4:
                        listAnswer.set(questionViewModel.getNavIndex().getValue(), "4");
                        break;
                }
            }
        });

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int score = 0;
                for (int i = 0; i < test.getListQuestion().size(); i++) {
                    if (listAnswer.get(i).equals(test.getListQuestion().get(i).getCorrectAnswer()))
                        score++;
                }
                answer.setScore(score + "");
                answer.setParticipant(questionViewModel.getParticipantName().getValue());
                DialogFragment dialogFragment = new SubmitAnswerDialogFragment(answer);
                dialogFragment.show(getParentFragmentManager(), "submit answer");

            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitTest();
            }
        });


        //override back press
        binding.getRoot().setFocusableInTouchMode(true);
        binding.getRoot().requestFocus();
        binding.getRoot().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
                {
                    exitTest();
                    return true;
                }
                return false;
            }
        });

        return binding.getRoot();
    }

    private void exitTest() {
        DialogFragment dialogFragment = new ExitTestConfirmDialogFragment();
        dialogFragment.show(getParentFragmentManager(), "exit question fragment");
    }

    @Override
    public void onItemCLicked(int index) {
        questionViewModel.getNavIndex().postValue(index);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}