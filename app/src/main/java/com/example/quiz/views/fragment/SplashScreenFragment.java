package com.example.quiz.views.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.quiz.R;
import com.example.quiz.viewmodels.FirebaseViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SplashScreenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SplashScreenFragment extends Fragment {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SplashScreenFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SplashScreenFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SplashScreenFragment newInstance(String param1, String param2) {
        SplashScreenFragment fragment = new SplashScreenFragment();
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
        FirebaseViewModel firebaseViewModel = new ViewModelProvider(requireActivity()).get(FirebaseViewModel.class);

        new CountDownTimer(2000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment).navigate(R.id.action_splashScreenFragment_to_loginFragment);
//                firebaseViewModel.getLogInState().observe(getViewLifecycleOwner(), integer -> {
//                    if (integer == 1) {
//                        Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment).navigate(R.id.action_splashScreenFragment_to_homeFragment);
//                    }
//                    else if (integer == 0) {
//                        Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment).navigate(R.id.action_splashScreenFragment_to_loginFragment);
//                    }
//                });
            }
        }.start();


        return inflater.inflate(R.layout.fragment_splash_screen, container, false);
    }
}