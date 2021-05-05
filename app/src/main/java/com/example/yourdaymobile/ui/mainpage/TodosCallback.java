package com.example.yourdaymobile.ui.mainpage;

import com.example.yourdaymobile.data.Todo;

import java.util.ArrayList;

public interface TodosCallback {
    void onCallback(ArrayList<Todo> todos);
}