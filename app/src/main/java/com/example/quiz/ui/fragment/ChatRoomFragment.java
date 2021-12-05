package com.example.quiz.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.quiz.R;
import com.example.quiz.adapter.ChatRoomAdapter;
import com.example.quiz.databinding.FragmentChatRoomBinding;
import com.example.quiz.models.Message;
import com.example.quiz.viewmodels.FirebaseViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatRoomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatRoomFragment extends Fragment {
    FragmentChatRoomBinding binding;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatRoomFragment() {
        // Required empty public constructor
    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatRoomFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatRoomFragment newInstance(String param1, String param2) {
        ChatRoomFragment fragment = new ChatRoomFragment();
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
        binding = FragmentChatRoomBinding.inflate(getLayoutInflater());
        FirebaseViewModel firebaseViewModel = new ViewModelProvider(requireActivity()).get(FirebaseViewModel.class);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.rvChat.setLayoutManager(layoutManager);
        ChatRoomAdapter chatRoomAdapter = new ChatRoomAdapter(firebaseViewModel.getMessages(), firebaseViewModel.getUser().getUid());
        binding.rvChat.setAdapter(chatRoomAdapter);

        firebaseViewModel.getUpdateChatRoom().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer > 0)
                    chatRoomAdapter.notifyAdapter();
            }
        });

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd/MM ");
                String now = simpleDateFormat.format(new Date());
                firebaseViewModel.sendMessage(firebaseViewModel.getCurrentChatRoomID().getValue(), new Message(firebaseViewModel.getUserInfo().getValue().getName(), now, binding.etMessage.getText().toString(), firebaseViewModel.getUser().getUid()));
                binding.etMessage.setText("");
            }
        });

        binding.getRoot().setFocusableInTouchMode(true);
        binding.getRoot().requestFocus();
        binding.getRoot().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
                {
                    firebaseViewModel.removeCurrentChatRoomListener(firebaseViewModel.getCurrentChatRoomID().getValue());
                    Navigation.findNavController(getActivity(), R.id.main_nav_host_fragment).navigate(R.id.action_global_homeFragment);
                    return true;
                }
                return false;
            }
        });

        return binding.getRoot();
    }
}