package com.example.quiz.models;

import androidx.recyclerview.widget.DiffUtil;

import java.util.ArrayList;

public class MessageDiffUtilCallback extends DiffUtil.Callback {
    ArrayList<Message> oldList;
    ArrayList<Message> newList;

    public MessageDiffUtilCallback(ArrayList<Message> oldList, ArrayList<Message> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        if (oldList != null)
            return oldList.size();
        return 0;
    }

    @Override
    public int getNewListSize() {
        if (newList != null)
            return  newList.size();
        return 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return false;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return false;
    }

}
