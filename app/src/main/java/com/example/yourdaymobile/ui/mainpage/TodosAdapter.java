package com.example.yourdaymobile.ui.mainpage;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourdaymobile.R;
import com.example.yourdaymobile.ui.data.Todo;

import java.util.ArrayList;

public class TodosAdapter extends RecyclerView.Adapter<TodosAdapter.MyViewHolder> {
    Context context;
    private ArrayList<Todo> todos;

    @NonNull
    @Override
    public TodosAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.todo_row,parent,false);

            return new TodosAdapter.MyViewHolder(view);
    }

    public TodosAdapter(Context context, ArrayList<Todo> todos) {
        this.context = context;
        this.todos = todos;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Todo currentItem = todos.get(position);

        holder.todoTextView.setText(currentItem.getText());
        holder.isDone.setChecked(currentItem.getDone());

    }


    @Override
    public int getItemCount() {
        return todos.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView todoTextView;
        CheckBox isDone;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            todoTextView = itemView.findViewById(R.id.todoTextView);
            isDone = itemView.findViewById(R.id.idDoneCB);

        }
    }
}
