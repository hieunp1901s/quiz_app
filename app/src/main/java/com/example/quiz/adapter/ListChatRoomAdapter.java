package com.example.quiz.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz.databinding.ChatRoomRecyclerviewItemBinding;
import com.example.quiz.databinding.JoinListsRecyclerviewItemBinding;
import com.example.quiz.databinding.ListChatroomRecyclerviewItemBinding;
import com.example.quiz.models.ChatListDialogFragmentItemClicked;
import com.example.quiz.models.ChatRoom;
import com.example.quiz.models.Test;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;


public class ListChatRoomAdapter extends RecyclerView.Adapter<ListChatRoomAdapter.ListChatRoomViewHolder> {
    ArrayList<ChatRoom> list;
    ArrayList<String> notificationList;
    ChatListDialogFragmentItemClicked itemClicked;
    public ListChatRoomAdapter(ArrayList<ChatRoom> list, ArrayList<String> notificationList, ChatListDialogFragmentItemClicked itemClicked) {
        this.list = list;
        this.notificationList = notificationList;
        this.itemClicked = itemClicked;
        clearDupChatRoom();
    }

    public void notifyAdapter() {
        clearDupChatRoom();
        notifyDataSetChanged();
    }

    private void clearDupChatRoom() {
        for (int i = 0; i < list.size() - 1; i++) {
            Log.d("i", i + "");
            Log.d("list size", list.size() + "");
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(i).getId().equals(list.get(j).getId())) {
                    list.remove(j);
                    j--;
                }
            }
        }
    }

    public class ListChatRoomViewHolder extends RecyclerView.ViewHolder{
        ListChatroomRecyclerviewItemBinding binding;
        public ListChatRoomViewHolder(@NonNull @NotNull ListChatroomRecyclerviewItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setContent(int index) {
            binding.tvRoomName.setText(list.get(index).getName());

            //check
            for (int i = 0; i < notificationList.size(); i++)
                if (list.get(index).getId().equals(notificationList.get(i)))
                    binding.tvRoomName.setText(list.get(index).getName() + "....");



            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String id = list.get(index).getId();
                    Log.d("id", id);
                    itemClicked.onItemClicked(id);
                }
            });
        }


    }
    @NonNull
    @NotNull
    @Override
    public ListChatRoomAdapter.ListChatRoomViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ListChatroomRecyclerviewItemBinding binding = ListChatroomRecyclerviewItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ListChatRoomViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ListChatRoomAdapter.ListChatRoomViewHolder holder, int position) {
        holder.setContent(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
