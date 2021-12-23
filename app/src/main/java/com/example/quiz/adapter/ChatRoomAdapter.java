package com.example.quiz.adapter;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quiz.R;
import com.example.quiz.databinding.ChatRoomRecyclerviewItemBinding;
import com.example.quiz.models.Message;

import org.jetbrains.annotations.NotNull;

public class ChatRoomAdapter extends ListAdapter<Message, ChatRoomAdapter.ChatRoomViewHolder> {
    private final String userID;
    public ChatRoomAdapter(String userID) {
        super(DIFF_CALLBACK);
        this.userID = userID;
    }

    public static final DiffUtil.ItemCallback<Message> DIFF_CALLBACK = new DiffUtil.ItemCallback<Message>() {
        @Override
        public boolean areItemsTheSame(@NonNull @NotNull Message oldItem, @NonNull @NotNull Message newItem) {
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull @NotNull Message oldItem, @NonNull @NotNull Message newItem) {
            return false;
        }
    };


    public class ChatRoomViewHolder extends RecyclerView.ViewHolder{
        ChatRoomRecyclerviewItemBinding binding;
        public ChatRoomViewHolder(@NonNull @NotNull ChatRoomRecyclerviewItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void init(Message message) {
            binding.getRoot().setGravity(Gravity.START);
            binding.tvMessageContent.setBackgroundResource(R.drawable.message_background);
            binding.tvMessageContent.setTextColor(Color.parseColor("#ececec"));
            binding.tvName.setText(message.getUser());
            binding.tvMessageContent.setText(message.getMessage());
            binding.tvTimeSend.setText(message.getTime());
            binding.tvName.setVisibility(View.VISIBLE);
            binding.tvTimeSend.setVisibility(View.VISIBLE);
        }

        public void bindTo(Message message, int index) {
            init(message);
            if (message.getUserId().equals(userID)) {
                binding.getRoot().setGravity(Gravity.END);
                binding.tvMessageContent.setBackgroundResource(R.drawable.my_message_background);
                binding.tvMessageContent.setTextColor(Color.parseColor("#ececec"));
                binding.tvName.setVisibility(View.GONE);
            }

            if (index > 0) {
                if (getItem(index -1).getTime().equals(message.getTime()))
                    binding.tvTimeSend.setVisibility(View.GONE);
                if (getItem(index -1).getUserId().equals(message.getUserId()))
                    binding.tvName.setVisibility(View.GONE);
            }

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
        holder.bindTo(getItem(position), position);
    }

}
