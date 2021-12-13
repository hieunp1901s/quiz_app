package com.example.quiz.adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quiz.databinding.ListChatroomRecyclerviewItemBinding;
import com.example.quiz.views.interfaces.ChatListDialogFragmentItemClicked;
import com.example.quiz.models.ChatRoom;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;

public class ListChatRoomAdapter extends ListAdapter<ChatRoom, ListChatRoomAdapter.ListChatRoomViewHolder> {
    ArrayList<String> notificationList;
    ChatListDialogFragmentItemClicked itemClicked;
    public ListChatRoomAdapter( ArrayList<String> notificationList, ChatListDialogFragmentItemClicked itemClicked) {
        super(DIFF_CALLBACK);
        this.notificationList = notificationList;
        this.itemClicked = itemClicked;
    }
    public static final DiffUtil.ItemCallback<ChatRoom> DIFF_CALLBACK = new DiffUtil.ItemCallback<ChatRoom>() {
        @Override
        public boolean areItemsTheSame(@NonNull @NotNull ChatRoom oldItem, @NonNull @NotNull ChatRoom newItem) {
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull @NotNull ChatRoom oldItem, @NonNull @NotNull ChatRoom newItem) {
            return false;
        }
    };


    public class ListChatRoomViewHolder extends RecyclerView.ViewHolder{
        ListChatroomRecyclerviewItemBinding binding;
        public ListChatRoomViewHolder(@NonNull @NotNull ListChatroomRecyclerviewItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindTo(ChatRoom chatRoom) {
            binding.tvRoomName.setText(chatRoom.getName());
            if (chatRoom.getLastMessage() != null) {
                String lastMessage = chatRoom.getLastMessage().getUser() + ": " + chatRoom.getLastMessage().getMessage();
                binding.tvLastMessage.setText(lastMessage);
            }
            //change view if have notification
            for (int i = 0; i < notificationList.size(); i++)
                if (chatRoom.getId().equals(notificationList.get(i))) {
                    binding.tvRoomName.setTextColor(Color.parseColor("#000000"));
                    binding.tvLastMessage.setTextColor(Color.parseColor("#000000"));
                    binding.tvRoomName.setTypeface(null, Typeface.BOLD);
                    binding.tvLastMessage.setTypeface(null, Typeface.BOLD);
                    break;
                }

            binding.getRoot().setOnClickListener(v -> itemClicked.onItemClicked(chatRoom));
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
        holder.bindTo(getItem(position));
    }
}
