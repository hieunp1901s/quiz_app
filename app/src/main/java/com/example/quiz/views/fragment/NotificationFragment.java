package com.example.quiz.views.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.quiz.adapter.LogAdapter;
import com.example.quiz.adapter.TestScoreHistoryAdapter;
import com.example.quiz.databinding.FragmentNotificationBinding;
import com.example.quiz.models.Notification;
import com.example.quiz.viewmodels.FirebaseViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationFragment extends Fragment {
    FragmentNotificationBinding binding;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NotificationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
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

        FirebaseViewModel firebaseViewModel = new ViewModelProvider(requireActivity()).get(FirebaseViewModel.class);
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(0);
        requireActivity().getWindow().setStatusBarColor(Color.parseColor("#2d87ff"));
        binding = FragmentNotificationBinding.inflate(getLayoutInflater());
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Test score"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Log"));

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext());
        layoutManager1.setReverseLayout(true);
        layoutManager1.setStackFromEnd(true);
        binding.rvTestScoreHistory.setLayoutManager(layoutManager1);
        TestScoreHistoryAdapter testScoreHistoryAdapter = new TestScoreHistoryAdapter();
        binding.rvTestScoreHistory.setAdapter(testScoreHistoryAdapter);


        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext());
        layoutManager2.setReverseLayout(true);
        layoutManager2.setStackFromEnd(true);
        binding.rvLog.setLayoutManager(layoutManager2);
        LogAdapter logAdapter = new LogAdapter();
        binding.rvLog.setAdapter(logAdapter);

        firebaseViewModel.getScoreLog().observe(getViewLifecycleOwner(), new Observer<ArrayList<Notification>>() {
            @Override public void onChanged(ArrayList<Notification> notifications) {
                testScoreHistoryAdapter.submitList(notifications);
            }
        });

        firebaseViewModel.getNormalLog().observe(getViewLifecycleOwner(), new Observer<ArrayList<Notification>>() {
            @Override
            public void onChanged(ArrayList<Notification> notifications) {
                logAdapter.submitList(notifications);
            }
        });



        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    binding.rvTestScoreHistory.setVisibility(View.VISIBLE);
                    binding.rvLog.setVisibility(View.INVISIBLE);
                }
                else {
                    binding.rvTestScoreHistory.setVisibility(View.INVISIBLE);
                    binding.rvLog.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return binding.getRoot();
    }
}