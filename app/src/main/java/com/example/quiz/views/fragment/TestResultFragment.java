package com.example.quiz.views.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.quiz.adapter.TestResultAdapter;
import com.example.quiz.databinding.FragmentTestResultBinding;
import com.example.quiz.models.Answer;
import com.example.quiz.views.interfaces.TestResultFragmentItemClicked;
import com.example.quiz.views.dialog.StudentAnswerInfoDialogFragment;
import com.example.quiz.viewmodels.FirebaseViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.apache.poi.ss.formula.functions.Column;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(0);
        requireActivity().getWindow().setStatusBarColor(Color.parseColor("#151718"));
        binding = FragmentTestResultBinding.inflate(getLayoutInflater());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.rvTestResult.setLayoutManager(layoutManager);

        firebaseViewModel.getFinishGetResult().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                float min = 10;
                float max = 0;
                ArrayList<Answer> list = firebaseViewModel.getAnswerList();
                float countA = 0f, countB = 0f, countC = 0f, countD = 0f, countF = 0f;
                for (int i = 0; i < list.size(); i++) {
                    String string = list.get(i).getScore();
                    String[] parts = string.split("/", 2);
                    float part1 = Float.parseFloat(parts[0]);
                    float part2 = Float.parseFloat(parts[1]);
                    float score = 10f * (part1 / part2) ;

                    if (score > max)
                        max = score;
                    if (score < min)
                        min = score;

                    if (score >= 8.5f)
                        countA++;
                    else if (score >= 7.0f)
                        countB++;
                    else if (score >= 5.5f)
                        countC++;
                    else if (score >= 4.0f)
                        countD++;
                    else
                        countF++;
                }

                //Barchart
                ArrayList numberOfGrade = new ArrayList();
                numberOfGrade.add(new BarEntry(countA, 0));
                numberOfGrade.add(new BarEntry(countB, 1));
                numberOfGrade.add(new BarEntry(countC, 2));
                numberOfGrade.add(new BarEntry(countD, 3));
                numberOfGrade.add(new BarEntry(countF, 4));



                ArrayList barLabel = new ArrayList();
                barLabel.add("A");
                barLabel.add("B");
                barLabel.add("C");
                barLabel.add("D");
                barLabel.add("F");

                List<Integer> barColor = new ArrayList<>();
                barColor.add(Color.RED);
                barColor.add(Color.GREEN);
                barColor.add(Color.BLUE);
                barColor.add(Color.MAGENTA);
                barColor.add(Color.GRAY);

                BarDataSet dataSet = new BarDataSet(numberOfGrade, "Grade");
                binding.barChart.animateY(5000);
                BarData data = new BarData(barLabel, dataSet);
                dataSet.setColors(barColor);
                binding.barChart.setData(data);
                binding.barChart.getXAxis().setTextColor(Color.WHITE);
                binding.barChart.getAxisLeft().setTextColor(Color.WHITE);
                binding.barChart.getAxisRight().setTextColor(Color.WHITE);// left y-axis
                binding.barChart.getXAxis().setTextColor(Color.WHITE);
                binding.barChart.getLegend().setTextColor(Color.WHITE);
                binding.barChart.getBarData().setValueTextColor(Color.WHITE);

                binding.barChart.getLegend().setComputedColors(barColor);
                dataSet.setValueTextSize(12f);


                //Piechart
                ArrayList pie = new ArrayList();
                ArrayList pieLabel = new ArrayList();
                List<Integer> colors = new ArrayList<>();
                List<String> legendLabel = new ArrayList<>();
                int index = 0;
                float total = countA + countB + countC + countD;
                if (countA > 0) {
                    pieLabel.add("A");
                    pie.add(new Entry(countA / total * 100, index));
                    colors.add(Color.RED);
                    index++;
                }
                if (countB > 0) {
                    pieLabel.add("B");
                    pie.add(new Entry(countB/ total * 100, index));
                    colors.add(Color.GREEN);
                    index++;
                }
                if (countC > 0) {
                    pieLabel.add("C");
                    pie.add(new Entry(countC/ total * 100, index));
                    colors.add(Color.BLUE);
                    index++;
                }

                if (countD > 0) {
                    pieLabel.add("D");
                    pie.add(new Entry(countD/ total *100, index));
                    colors.add(Color.MAGENTA);
                    index++;
                }

                if (countF > 0) {
                    pieLabel.add("F");
                    pie.add(new Entry(countF/ total * 100, index));
                    colors.add(Color.GRAY);
                    index++;
                }

                PieDataSet pieDataSet  = new PieDataSet(pie, "Grade");
                binding.pieChart.animateY(5000);
                PieData pieData = new PieData(pieLabel, pieDataSet);;
                pieData.setValueFormatter(new PercentFormatter());
                binding.pieChart.setData(pieData);
                binding.pieChart.getLegend().setTextColor(Color.WHITE);
                binding.pieChart.getData().setValueTextColor(Color.WHITE);
                binding.pieChart.getLegend().setEnabled(false);
                pieDataSet.setColors(colors);
                pieDataSet.setValueTextSize(12f);
                binding.tvHighestScore.setText("Highest Score: " + max);
                binding.tvLowestScore.setText("Lowest Score: " + min);

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



