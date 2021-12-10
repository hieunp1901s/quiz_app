package com.example.quiz.models;
import androidx.recyclerview.widget.DiffUtil;

import java.util.ArrayList;

public class TestDiffUtilCallback extends DiffUtil.Callback {
    ArrayList<Test> oldList;
    ArrayList<Test> newList;

    public TestDiffUtilCallback(ArrayList<Test> oldList, ArrayList<Test> newList) {
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
        return newList.get(newItemPosition).getTestID().equals(oldList.get(oldItemPosition).getTestID());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return newList.get(newItemPosition).equals(oldList.get(oldItemPosition));
    }

}
