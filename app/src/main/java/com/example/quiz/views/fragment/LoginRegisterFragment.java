package com.example.quiz.views.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.quiz.R;
import com.example.quiz.views.dialog.RegisterDialogFragment;
import com.example.quiz.viewmodels.FirebaseViewModel;
import com.example.quiz.databinding.FragmentLoginRegisterBinding;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginRegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginRegisterFragment extends Fragment{
    private FirebaseViewModel firebaseViewModel;
    FragmentLoginRegisterBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginRegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginRegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginRegisterFragment newInstance(String param1, String param2) {
        LoginRegisterFragment fragment = new LoginRegisterFragment();
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
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        firebaseViewModel = new ViewModelProvider(requireActivity()).get(FirebaseViewModel.class);
        binding = FragmentLoginRegisterBinding.inflate(getLayoutInflater());

        requireActivity().getWindow().getDecorView().setSystemUiVisibility(0);
        requireActivity().getWindow().setStatusBarColor(Color.parseColor("#101112"));

        firebaseViewModel.getLogInState().setValue(0);

        binding.btnRegister.setOnClickListener(v -> {
            DialogFragment dialog = new RegisterDialogFragment();
            dialog.setCancelable(false);
            dialog.show(getParentFragmentManager(), "register dialog");
        });

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etUsername.getText().toString();
            String password = binding.etLoginPassword.getText().toString();

            if (email.length() > 0 && password.length() > 0) {
                firebaseViewModel.getProgressing().setValue(true);
                firebaseViewModel.login(email, password);

            }
            else {
                Toast.makeText(getContext(), "Email Address and Password Must Be Entered", Toast.LENGTH_SHORT).show();
            }
        });

        firebaseViewModel.getLogInState().observe(getViewLifecycleOwner(), integer -> {
            if (integer == 1)
                Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment).navigate(R.id.action_global_homeFragment);
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}