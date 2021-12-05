package com.example.quiz.ui.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz.R;
import com.example.quiz.adapter.JoinTestsAdapter;
import com.example.quiz.adapter.ListChatRoomAdapter;
import com.example.quiz.databinding.FragmentChatListDialogBinding;
import com.example.quiz.models.ChatListDialogFragmentItemClicked;
import com.example.quiz.viewmodels.FirebaseViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ChatListDialogFragment extends DialogFragment implements ChatListDialogFragmentItemClicked {
    FragmentChatListDialogBinding binding;
    ArrayList<String> testID;
    ArrayList<String> chatroomName;
    FirebaseViewModel firebaseViewModel;

    public ChatListDialogFragment() {

    }
    public ChatListDialogFragment(ArrayList<String> testID, ArrayList<String> chatroomName) {
        this.testID = testID;
        this.chatroomName = chatroomName;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentChatListDialogBinding.inflate(getLayoutInflater());
        Dialog builder = new Dialog(getActivity());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.setContentView(binding.getRoot());

        FirebaseViewModel firebaseViewModel = new ViewModelProvider(requireActivity()).get(FirebaseViewModel.class);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.rvListChatroom.setLayoutManager(layoutManager);
        ListChatRoomAdapter listChatRoomAdapter = new ListChatRoomAdapter(firebaseViewModel.getListChatRoom(), firebaseViewModel.getNewNotification(), this::onItemClicked);
        binding.rvListChatroom.setAdapter(listChatRoomAdapter);

        return builder;
    }

    @Override
    public void onItemClicked(String testID) {
        getDialog().dismiss();
        FirebaseViewModel firebaseViewModel = new ViewModelProvider(requireActivity()).get(FirebaseViewModel.class);
        firebaseViewModel.initCurrentChatRoom(testID);
        Navigation.findNavController(getActivity(), R.id.main_nav_host_fragment).navigate(R.id.action_global_chatRoomFragment);
    }
}
