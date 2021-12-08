package com.example.quiz.adapter;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quiz.R;
import com.example.quiz.databinding.ChatRoomRecyclerviewItemBinding;
import com.example.quiz.models.Message;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder> {
    private final ArrayList<Message> messages;
    private final String userID;
    public ChatRoomAdapter(ArrayList<Message> messages, String userID) {
        this.messages = messages;
        this.userID = userID;
    }

    public void notifyAdapter() {
        notifyDataSetChanged();
    }

    public class ChatRoomViewHolder extends RecyclerView.ViewHolder{
        ChatRoomRecyclerviewItemBinding binding;
        public ChatRoomViewHolder(@NonNull @NotNull ChatRoomRecyclerviewItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setContent(int index) {

            if (messages.get(index).getUserId().equals(userID)) {
                binding.getRoot().setGravity(Gravity.END);
                binding.tvMessageContent.setBackgroundResource(R.drawable.my_message_background);
                binding.tvMessageContent.setTextColor(Color.parseColor("#FFFFFF"));
            }

            binding.tvName.setText(messages.get(index).getUser());
            binding.tvMessageContent.setText(messages.get(index).getMessage());
            binding.tvTimeSend.setText(messages.get(index).getTime());
        }
    }

    @NonNull
    @NotNull
    @Override
    public ChatRoomAdapter.ChatRoomViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ChatRoomRecyclerviewItemBinding binding = ChatRoomRecyclerviewItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ChatRoomViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ChatRoomAdapter.ChatRoomViewHolder holder, int position) {
        holder.setContent(position);
    }

    @Override
    public int getItemCount() {
        if (messages == null)
            return 0;
        else
            return messages.size();
    }
}
