package com.example.yourdaymobile.ui.wall;

import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yourdaymobile.R;
import com.example.yourdaymobile.ui.data.Todo;
import com.example.yourdaymobile.ui.mainpage.TodosAdapter;

import java.util.ArrayList;

public class TodosFragment extends Fragment {

    private TodosViewModel mViewModel;
    private View root;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerWall;
    private WallAdapter wallAdapter;
    private ArrayList<String> list;

    public static TodosFragment newInstance() {
        return new TodosFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.todos_fragment, container, false);
        progressDialogInit();
        setUpWall();
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TodosViewModel.class);
        // TODO: Use the ViewModel
    }

    private void setUpWall(){
        recyclerWall = root.findViewById(R.id.wallRV);

        ArrayList<String> list = new ArrayList<>();
        list.add("post1");
        list.add("post132");
        list.add("hehe");
        list.add("sd");
        list.add("post1");


        wallAdapter = new WallAdapter(getActivity(),list);
        recyclerWall.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerWall.setAdapter(wallAdapter);
        wallAdapter.notifyDataSetChanged();
        progressDialog.dismiss();
    }

    private void progressDialogInit() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);

        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

}