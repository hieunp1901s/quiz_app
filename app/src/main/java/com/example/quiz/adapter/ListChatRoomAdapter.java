package com.example.quiz.adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quiz.databinding.ListChatroomRecyclerviewItemBinding;
import com.example.quiz.views.interfaces.ChatListDialogFragmentItemClicked;
import com.example.quiz.models.ChatRoom;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;

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
            if (list.get(index).getLastMessage() != null) {
                String lastMessage = list.get(index).getLastMessage().getUser() + ": " + list.get(index).getLastMessage().getMessage();
                binding.tvLastMessage.setText(lastMessage);
            }
            //change view if have notification
            for (int i = 0; i < notificationList.size(); i++)
                if (list.get(index).getId().equals(notificationList.get(i))) {
                    binding.tvRoomName.setTextColor(Color.parseColor("#000000"));
                    binding.tvLastMessage.setTextColor(Color.parseColor("#000000"));
                    binding.tvRoomName.setTypeface(null, Typeface.BOLD);
                    binding.tvLastMessage.setTypeface(null, Typeface.BOLD);
                    break;
                }

            binding.getRoot().setOnClickListener(v -> itemClicked.onItemClicked(list.get(index)));
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
