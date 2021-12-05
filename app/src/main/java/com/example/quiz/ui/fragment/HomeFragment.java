package com.example.quiz.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;


import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.example.quiz.models.Test;
import com.example.quiz.models.User;
import com.example.quiz.ui.MainActivity;
import com.example.quiz.ui.dialog.ChatListDialogFragment;
import com.example.quiz.ui.dialog.FindTestDialogFragment;
import com.example.quiz.ui.dialog.SessionExpiredDialogFragment;
import com.example.quiz.viewmodels.FirebaseViewModel;
import com.example.quiz.R;
import com.example.quiz.databinding.FragmentHomeBinding;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;
    FirebaseViewModel firebaseViewModel;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        firebaseViewModel = new ViewModelProvider(requireActivity()).get(FirebaseViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        NavHostFragment navHostFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.home_nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        BottomNavigationView bottomNav = binding.bottomNavigationView;
        NavigationUI.setupWithNavController(bottomNav, navController);
        BadgeDrawable badgeDrawable = BadgeDrawable.create(getContext());

        firebaseViewModel.getUserInfo().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    binding.tvUser.setText(user.getName());
                }
            }
        });

        firebaseViewModel.getLogInState().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == 0) {
                    DialogFragment dialogFragment = new SessionExpiredDialogFragment();
                    dialogFragment.setCancelable(false);
                    dialogFragment.show(getParentFragmentManager(), "session expired");
                }
            }
        });

        binding.btnFindTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.etFindTest.getText().toString().equals("")) {
                    firebaseViewModel.findTestFromFirebase(binding.etFindTest.getText().toString());
                }
            }
        });

        firebaseViewModel.getFindTestResult().observe(getViewLifecycleOwner(), new Observer<Test>() {
            @Override
            public void onChanged(Test test) {
                if (test != null) {
                    binding.etFindTest.setText("");
                    DialogFragment dialogFragment = new FindTestDialogFragment();
                    dialogFragment.setCancelable(false);
                    dialogFragment.show(getParentFragmentManager(), "find test");
                }
            }
        });

        firebaseViewModel.getIsFindTestResultNull().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean == null) {

                }
                else if (aBoolean) {
                    binding.tvAlert.setText("Can't find test!");
                    firebaseViewModel.getIsFindTestResultNull().setValue(null);
                }

                else if (!aBoolean) {
                    binding.tvAlert.setText("Network error!");
                    firebaseViewModel.getIsFindTestResultNull().setValue(null);
                }

            }
        });

        binding.etFindTest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tvAlert.setText("Enter test ID");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.fabMessage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressLint("UnsafeExperimentalUsageError")
            @Override
            public void onGlobalLayout() {
                badgeDrawable.setHorizontalOffset(50);
                badgeDrawable.setVerticalOffset(50);
                BadgeUtils.attachBadgeDrawable(badgeDrawable, binding.fabMessage, null);
                binding.fabMessage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        binding.fabMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new ChatListDialogFragment();
//                dialogFragment.setCancelable(false);
                dialogFragment.show(getParentFragmentManager(), "list chatroom");
            }
        });

        firebaseViewModel.getNotification().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer != null)
                    badgeDrawable.setNumber(integer);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}