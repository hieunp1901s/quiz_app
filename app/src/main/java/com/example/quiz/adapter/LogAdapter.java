package com.example.quiz.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz.databinding.NotificationRecyclerviewItemBinding;
import com.example.quiz.models.Notification;

import org.jetbrains.annotations.NotNull;

public class LogAdapter extends ListAdapter<Notification, LogAdapter.ViewHolder> {
    public LogAdapter() {
        super(DIFF_CALLBACK);
    }

    public static final DiffUtil.ItemCallback<Notification> DIFF_CALLBACK = new DiffUtil.ItemCallback<Notification>() {
        @Override
        public boolean areItemsTheSame(@NonNull @NotNull Notification oldItem, @NonNull @NotNull Notification newItem) {
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull @NotNull Notification oldItem, @NonNull @NotNull Notification newItem) {
            return false;
        }
    };

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        NotificationRecyclerviewItemBinding binding = NotificationRecyclerviewItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bindTo(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        NotificationRecyclerviewItemBinding binding;
        public ViewHolder(@NonNull @NotNull NotificationRecyclerviewItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindTo(Notification notification) {
            binding.tvMessage.setText(notification.getMessage());
            binding.tvTime.setText(notification.getTime());
        }
    }

}
