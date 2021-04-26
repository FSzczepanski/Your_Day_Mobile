package com.example.yourdaymobile.ui.wall;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourdaymobile.R;
import com.example.yourdaymobile.ui.data.Todo;
import com.example.yourdaymobile.ui.mainpage.TodosAdapter;

import java.util.ArrayList;

public class WallAdapter extends RecyclerView.Adapter<WallAdapter.MyViewHolder> {
    Context context;
    private ArrayList<String> posts;

    @NonNull
    @Override
    public WallAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.post_row,parent,false);

        return new WallAdapter.MyViewHolder(view);
    }

    public WallAdapter(Context context, ArrayList<String> posts) {
        this.context = context;
        this.posts = posts;
    }

    @Override
    public void onBindViewHolder(@NonNull WallAdapter.MyViewHolder holder, int position) {
        String currentItem = posts.get(position);

        holder.postTextView.setText(currentItem);

    }


    @Override
    public int getItemCount() {
        return posts.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView postTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            postTextView = itemView.findViewById(R.id.todoTextView);

        }
    }
}
