package com.example.yourdaymobile.ui.mainpage;

import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.yourdaymobile.MainActivity;
import com.example.yourdaymobile.R;
import com.example.yourdaymobile.Singleton;
import com.example.yourdaymobile.data.Todo;
import com.example.yourdaymobile.ui.TabLayoutDisabler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainPageFragment extends Fragment implements TabLayoutDisabler {

    private MainPageViewModel mViewModel;
    private View root;
    private String authToken;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerTodos;
    private TodosAdapter todosAdapter;
    private ArrayList<Todo> todosList;


    public static MainPageFragment newInstance() {
        return new MainPageFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.main_page_fragment, container, false);
        progressDialogInit();

        showTabLayout();
        authToken=Singleton.authToken;


        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainPageViewModel.class);
        mViewModel.setContext(getContext());
        setUpList();




    }

    private void setUpList() {

        DownloadTask task = new DownloadTask();
        task.execute(twojeip"/notes");

    }


    private void progressDialogInit() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);

        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    @Override
    public void hideTabLayout() {

    }

    @Override
    public void showTabLayout() {
        MainActivity.showTabLayout();
    }



    public class DownloadTask extends AsyncTask<String, Void, ArrayList<Todo>> {

        @Override
        protected ArrayList<Todo> doInBackground(String... urls) {

            //getting todos list
            ArrayList<Todo> todos = new ArrayList<>();
            URL url;
            try {
                url = new URL(urls[0]);
                todos=  mViewModel.getTodos(url, authToken);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //posting todos list

            return todos;
        }


        @Override
        protected void onPostExecute(ArrayList<Todo> todos) {
            super.onPostExecute(todos);


            try {
                if (todos.get(0).getId().equals("xd404")){
                    progressDialog.dismiss();
                    NavController navController = Navigation.findNavController(requireActivity(),
                            R.id.my_nav_host_fragment);
                    navController.navigate(R.id.action_mainPageFragment_to_authFragment);
                }else {
                    recyclerTodos = root.findViewById(R.id.todosRV);
                    recyclerTodos.setLayoutManager(new LinearLayoutManager(getActivity()));
                    todosAdapter = new TodosAdapter(getActivity(), todos);
                    recyclerTodos.setAdapter(todosAdapter);
                    todosAdapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}