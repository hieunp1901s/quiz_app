package com.example.quiz.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.example.quiz.R;
import com.example.quiz.adapter.ChatRoomAdapter;
import com.example.quiz.databinding.FragmentChatRoomBinding;
import com.example.quiz.models.Message;
import com.example.quiz.viewmodels.FirebaseViewModel;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatRoomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatRoomFragment extends Fragment {
    FragmentChatRoomBinding binding;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    FirebaseViewModel firebaseViewModel;
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
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatRoomBinding.inflate(getLayoutInflater());
        sharedPref = requireContext().getSharedPreferences("com.example.quiz", Context.MODE_PRIVATE);

        firebaseViewModel = new ViewModelProvider(requireActivity()).get(FirebaseViewModel.class);
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        String chatroomID = Objects.requireNonNull(firebaseViewModel.getCurrentChatRoom().getValue()).getId();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.rvChat.setLayoutManager(layoutManager);
        ChatRoomAdapter chatRoomAdapter = new ChatRoomAdapter(firebaseViewModel.getMessages(), firebaseViewModel.getUser().getUid());
        binding.rvChat.setAdapter(chatRoomAdapter);
        binding.rvChat.scrollToPosition(firebaseViewModel.getMessages().size() - 1);
        binding.tvRoom.setText(firebaseViewModel.getCurrentChatRoom().getValue().getName());

        binding.btnBackChatroom.setOnClickListener(v -> {
            saveLastMessage();
            firebaseViewModel.removeCurrentChatRoomListener(chatroomID);
            firebaseViewModel.removeNewNotification(chatroomID);
            Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment).navigate(R.id.action_global_homeFragment);
        });


        firebaseViewModel.getNotifyChatRoomDataChanged().observe(getViewLifecycleOwner(), notify -> {
            chatRoomAdapter.notifyAdapter();
            binding.rvChat.scrollToPosition(firebaseViewModel.getMessages().size() - 1);
        });

        binding.btnSend.setOnClickListener(v -> {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd/MM ");
            String now = simpleDateFormat.format(new Date());
            firebaseViewModel.sendMessage(chatroomID, new Message(Objects.requireNonNull(firebaseViewModel.getUserInfo().getValue()).getName(), now, binding.etMessage.getText().toString(), firebaseViewModel.getUser().getUid()));
            binding.etMessage.setText("");
        });

        binding.getRoot().setFocusableInTouchMode(true);
        binding.getRoot().requestFocus();
        binding.getRoot().setOnKeyListener((v, keyCode, event) -> {
            if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
            {
                saveLastMessage();
                firebaseViewModel.removeCurrentChatRoomListener(chatroomID);
                firebaseViewModel.removeNewNotification(chatroomID);
                Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment).navigate(R.id.action_global_homeFragment);
                return true;
            }
            return false;
        });

        return binding.getRoot();
    }

    private void saveLastMessage() {
        String key = Objects.requireNonNull(firebaseViewModel.getCurrentChatRoom().getValue()).getId() + ".";
        int index = firebaseViewModel.getMessages().size() - 1;
        editor = sharedPref.edit();
        editor.putString(key, firebaseViewModel.getMessages().get(index).getMessage());
        editor.apply();
    }
}