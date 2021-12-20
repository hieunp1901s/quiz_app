package com.example.quiz.models;
import java.util.ArrayList;
import java.util.List;

public class StudentList {
    List<String> list;
    public StudentList(ArrayList<String> list) {
        this.list.addAll(list);
    }
}
