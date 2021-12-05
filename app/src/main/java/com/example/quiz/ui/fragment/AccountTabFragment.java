package com.example.quiz.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.quiz.ui.dialog.ConfirmLogoutDialogFragment;
import com.example.quiz.viewmodels.FirebaseViewModel;
import com.example.quiz.databinding.FragmentAccountTabBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountTabFragment extends Fragment {
    FragmentAccountTabBinding binding;
    private FirebaseViewModel firebaseViewModel;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AccountTabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountTabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountTabFragment newInstance(String param1, String param2) {
        AccountTabFragment fragment = new AccountTabFragment();
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
        binding = FragmentAccountTabBinding.inflate(inflater, container, false);

        binding.layoutLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new ConfirmLogoutDialogFragment();
                dialogFragment.setCancelable(false);
                dialogFragment.show(getParentFragmentManager(), "confirm logout");
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