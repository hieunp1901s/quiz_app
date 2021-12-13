package com.example.quiz.views.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quiz.R;
import com.example.quiz.adapter.ListChatRoomAdapter;
import com.example.quiz.databinding.FragmentChatListDialogBinding;
import com.example.quiz.models.Message;
import com.example.quiz.views.interfaces.ChatListDialogFragmentItemClicked;
import com.example.quiz.models.ChatRoom;
import com.example.quiz.viewmodels.FirebaseViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;


public class ChatListDialogFragment extends DialogFragment implements ChatListDialogFragmentItemClicked {
    FragmentChatListDialogBinding binding;
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

        if (firebaseViewModel.getCurrentChatRoom().getValue() != null) {
            firebaseViewModel.removeCurrentChatRoomListener(firebaseViewModel.getCurrentChatRoom().getValue().getId());
        }


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.rvListChatroom.setLayoutManager(layoutManager);
        ListChatRoomAdapter listChatRoomAdapter = new ListChatRoomAdapter(firebaseViewModel.getNewNotification().getValue(), this);
        binding.rvListChatroom.setAdapter(listChatRoomAdapter);

        firebaseViewModel.getChatRoomList().observe(requireActivity(), list -> listChatRoomAdapter.submitList(list));

        return builder;
    }

    @Override
    public void onItemClicked(ChatRoom chatRoom) {
        Objects.requireNonNull(getDialog()).dismiss();
        FirebaseViewModel firebaseViewModel = new ViewModelProvider(requireActivity()).get(FirebaseViewModel.class);
        firebaseViewModel.initCurrentChatRoom(chatRoom.getId());
        firebaseViewModel.getCurrentChatRoom().setValue(chatRoom);
        Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment).navigate(R.id.action_global_chatRoomFragment);
    }
}
