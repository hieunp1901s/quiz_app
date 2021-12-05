package com.example.quiz.ui.fragment;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

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
import com.example.quiz.models.CacheData;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.Collections;
import java.util.Date;

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
    long duration = 0;
    CacheData cacheData;
    ArrayList<Integer> questionOrder;
    ArrayList<String> answerOrder;

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
        answer = new Answer();

        cacheData = readCache();

        if (cacheData != null) {
            if (cacheData.getTestID().equals(test.getTestID())) {
                questionOrder = cacheData.getQuestionOrder();
                answerOrder = cacheData.getAnswerOrder();
                listAnswer = cacheData.getListAnswer();
                Log.d("cache", "is not null and test id not null");
            }
            else {
                initRandom();
                cacheData = new CacheData();
                cacheData.setTestID(test.getTestID());
                cacheData.setAnswerOrder(answerOrder);
                cacheData.setQuestionOrder(questionOrder);
                Log.d("cache", "is not null and test id null");
            }
        }
        else {
            Log.d("cache", "is null");
            initRandom();
            cacheData = new CacheData();
            cacheData.setTestID(test.getTestID());
            cacheData.setAnswerOrder(answerOrder);
            cacheData.setQuestionOrder(questionOrder);
        }


        questionViewModel.getNavIndex().setValue(0);

        questionViewModel.getNavIndex().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer != null) {
                    binding.tvCurrentQuestion.setText("Question " + (integer + 1) + "/" + test.getListQuestion().size());
                    binding.tvQuestion.setText(test.getListQuestion().get(questionOrder.get(integer)).getQuestion());
                    binding.rbAnswer1.setText(test.getListQuestion().get(questionOrder.get(integer)).getAnswer(Character.getNumericValue(answerOrder.get(integer).charAt(0))));
                    binding.rbAnswer2.setText(test.getListQuestion().get(questionOrder.get(integer)).getAnswer(Character.getNumericValue(answerOrder.get(integer).charAt(1))));
                    binding.rbAnswer3.setText(test.getListQuestion().get(questionOrder.get(integer)).getAnswer(Character.getNumericValue(answerOrder.get(integer).charAt(2))));
                    binding.rbAnswer4.setText(test.getListQuestion().get(questionOrder.get(integer)).getAnswer(Character.getNumericValue(answerOrder.get(integer).charAt(3))));
                }

                if (!listAnswer.get(integer).equals(""))
                    switch (listAnswer.get(integer)) {
                        case "1":
                            binding.rbAnswer1.setChecked(true);
                            break;
                        case "2":
                            binding.rbAnswer2.setChecked(true);
                            break;
                        case "3":
                            binding.rbAnswer3.setChecked(true);
                            break;
                        case "4":
                            binding.rbAnswer4.setChecked(true);
                            break;
                    }
            }
        });

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (questionViewModel.getNavIndex().getValue() < test.getListQuestion().size() - 1) {
                    duration++;
                    binding.rgGroupAnswer.clearCheck();
                    binding.rgGroupAnswer.jumpDrawablesToCurrentState();
                    questionViewModel.getNavIndex().setValue(questionViewModel.getNavIndex().getValue() + 1);
                }

            }
        });

        binding.btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (questionViewModel.getNavIndex().getValue() > 0) {
                    binding.rgGroupAnswer.clearCheck();
                    binding.rgGroupAnswer.jumpDrawablesToCurrentState();
                    questionViewModel.getNavIndex().setValue(questionViewModel.getNavIndex().getValue() - 1);
                }

            }
        });

        //timer
        CountDownTimer countDownTimer = new CountDownTimer(questionViewModel.getTimer().getValue(), 1000) {

            public void onTick(long millisUntilFinished) {
                duration++;
                long minute = millisUntilFinished / (1000 * 60);
                long second = millisUntilFinished / 1000 - minute * 60;
                binding.tvTimer.setText("Time left " + minute + ":" + String.format("%02d", second));
            }

            public void onFinish() {
                int score = 0;
                ArrayList<String> correctAnswerOrder = mapAnswerToCorrectOrder();
                for (int i = 0; i < test.getListQuestion().size(); i++) {
                    if (correctAnswerOrder.get(i).equals(test.getListQuestion().get(i).getCorrectAnswer()))
                        score++;
                }
                answer.setScore(score + "");
                answer.setParticipant(questionViewModel.getParticipantName().getValue());
                answer.setListAnswer(correctAnswerOrder);

                long minute = duration / 60;
                long second = duration - minute * 60;
                answer.setDuration(minute+":"+ String.format("%02d", second));

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date now = Calendar.getInstance().getTime();
                String strDate = simpleDateFormat.format(now);
                answer.setTimeFinish(strDate);
                firebaseViewModel.submitAnswerToRepoFirebase(answer, test.getTestID());
                Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment).navigate(R.id.action_questionFragment_to_homeFragment);
            }
        }.start();


        //setup bottom sheet
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet);
        bottomSheetBehavior.setSkipCollapsed(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        //recyclerview with flex box for inside bottom sheet
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

                cacheData.setListAnswer(listAnswer);
                writeCache();
            }
        });

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int score = 0;
                ArrayList<String> correctAnswerOrder = mapAnswerToCorrectOrder();
                for (int i = 0; i < test.getListQuestion().size(); i++) {
                    if (correctAnswerOrder.get(i).equals(test.getListQuestion().get(i).getCorrectAnswer()))
                        score++;
                }
                answer.setScore(score + "");
                answer.setParticipant(questionViewModel.getParticipantName().getValue());
                answer.setListAnswer(correctAnswerOrder);

                long minute = duration / 60;
                long second = duration - minute * 60;
                answer.setDuration(minute+":"+ String.format("%02d", second));

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date now = Calendar.getInstance().getTime();
                String strDate = simpleDateFormat.format(now);
                answer.setTimeFinish(strDate);
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

        questionViewModel.getCancelTimer().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean)
                    countDownTimer.cancel();
            }
        });

        binding.tvTitle.setText(test.getTestName());

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

    private void initRandom() {
        listAnswer = new ArrayList<>();
        for (int i = 0; i < test.getListQuestion().size(); i++) {
            listAnswer.add("");
        }

        questionOrder = new ArrayList<>();
        ArrayList<String> temp = new ArrayList<>();
        answerOrder = new ArrayList<>();
        temp.add("1");
        temp.add("2");
        temp.add("3");
        temp.add("4");

        for (int i = 0; i < test.getListQuestion().size(); i++) {
            questionOrder.add(i);
        }

        if (test.getMix().equals("true")) {
            Collections.shuffle(questionOrder);
            for (int i = 0; i < test.getListQuestion().size(); i++) {
                Collections.shuffle(temp);
                String result = temp.get(0) + temp.get(1) + temp.get(2) + temp.get(3);
                answerOrder.add(result);
            }
        }
        else {
            for (int i = 0; i < test.getListQuestion().size(); i++) {
                answerOrder.add(temp.get(0) + temp.get(1) + temp.get(2) + temp.get(3));
            }
        }
    }

    private ArrayList<String> mapAnswerToCorrectOrder() {
        ArrayList<String> result = new ArrayList<>();
        result.addAll(listAnswer);
        for (int i = 0; i < result.size(); i++) {
            if (listAnswer.get(i).equals("1"))
                result.set(questionOrder.get(i), answerOrder.get(i).substring(0, 1));
            else if (listAnswer.get(i).equals("2"))
                result.set(questionOrder.get(i), answerOrder.get(i).substring(1, 2));
            else if (listAnswer.get(i).equals("3"))
                result.set(questionOrder.get(i), answerOrder.get(i).substring(2, 3));
            else if (listAnswer.get(i).equals("4"))
                result.set(questionOrder.get(i), answerOrder.get(i).substring(3));
        }
        return result;
    }

    private void writeCache() {
        String filename = "cache";
        try {
            File.createTempFile(filename, null, requireContext().getCacheDir());
            File cacheFile = new File(getContext().getCacheDir(), filename);
            FileOutputStream fos = new FileOutputStream(cacheFile);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(cacheData);
            os.close();
            fos.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private CacheData readCache() {
        String filename = "cache";
        File cacheFile = new File(requireContext().getCacheDir(), filename);
        try {
            FileInputStream fis = new FileInputStream(cacheFile);
            ObjectInputStream is = new ObjectInputStream(fis);
            CacheData cacheData = (CacheData) is.readObject();
            fis.close();
            is.close();
            return cacheData;
        } catch (IOException | ClassNotFoundException ignored) {

        }
        return null;
    }
}