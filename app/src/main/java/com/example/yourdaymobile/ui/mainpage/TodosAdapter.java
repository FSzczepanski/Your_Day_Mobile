package com.example.yourdaymobile.ui.mainpage;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourdaymobile.R;
import com.example.yourdaymobile.data.Todo;
import com.example.yourdaymobile.utilities.OnHttpActionDone;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class TodosAdapter extends RecyclerView.Adapter<TodosAdapter.MyViewHolder> {
    Context context;
    private ArrayList<Todo> todos;
    private MainPageViewModel mViewModel;
    private OnHttpActionDone onAction;
    private EditTodoDialog dialog;
    private FragmentManager fragmentManager;

    @NonNull
    @Override
    public TodosAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.todo_row, parent, false);

        return new TodosAdapter.MyViewHolder(view);
    }

    public TodosAdapter(Context context, ArrayList<Todo> todos, MainPageViewModel mViewModel, FragmentManager fm, OnHttpActionDone onAction) {
        this.context = context;
        this.todos = todos;
        this.mViewModel =mViewModel;
        this.onAction = onAction;
        this.fragmentManager = fm;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Todo currentItem = todos.get(position);

        holder.todoTextView.setText(currentItem.getText());
        holder.isDone.setChecked(currentItem.getDone());
        String id = currentItem.getId();
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new EditTodoDialog(mViewModel, id, new OnHttpActionDone() {
                    @Override
                    public void onDone() {
                        onAction.onDone();
                    }
                });
                dialog.show(fragmentManager, "DialogFragment");
            }
        });

    }


    @Override
    public int getItemCount() {
        return todos.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView todoTextView;
        CheckBox isDone;
        AppCompatImageButton editButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            todoTextView = itemView.findViewById(R.id.todoTextView);
            isDone = itemView.findViewById(R.id.idDoneCB);
            editButton = itemView.findViewById(R.id.editTodoButton);

        }
    }
}