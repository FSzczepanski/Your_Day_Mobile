package com.example.yourdaymobile.ui.mainpage;

import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yourdaymobile.R;
import com.example.yourdaymobile.ui.data.Todo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MainPageFragment extends Fragment {

    private MainPageViewModel mViewModel;
    private View root;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerTodos;
    private TodosAdapter todosAdapter;
    private ArrayList<String> list;


    public static MainPageFragment newInstance() {
        return new MainPageFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.main_page_fragment, container, false);
        progressDialogInit();
        //getDataFromApi();
        setUpTodosList();
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainPageViewModel.class);
        // TODO: Use the ViewModel
    }

    private void setUpTodosList(){
        recyclerTodos = root.findViewById(R.id.todosRV);

        ArrayList<Todo> todosList = new ArrayList<>();
        todosList.add(new Todo("duss",true));
        todosList.add(new Todo("dsadsa",false));
        todosList.add(new Todo("pancakes",true));

        todosAdapter = new TodosAdapter(getActivity(),todosList);
        recyclerTodos.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerTodos.setAdapter(todosAdapter);
        todosAdapter.notifyDataSetChanged();
        progressDialog.dismiss();
    }

    private void progressDialogInit() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);

        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void getDataFromApi(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // Create URL
                URL todosEndpoint = null;
                try {
                    todosEndpoint = new URL("https://competenciesdevelopmentfs.azurewebsites.net/api/Competencies/list");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                list = new ArrayList<>();
                // Create connection
                try {
                    HttpsURLConnection myConnection =
                            (HttpsURLConnection) todosEndpoint.openConnection();
                    myConnection.setRequestProperty("auth-token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2MDgxYmFlZWZhNjAxMzBiZjljZGMyNzQiLCJpYXQiOjE2MTkxNjU4OTR9.fEGJybX62agSG83lFqTNSkc7tm_W0bqXScSge6P5mR8");

                    if (myConnection.getResponseCode() == 200) {
                        // Success
                        // Further processing here
                    } else {
                        // Error handling code goes here
                    }
                    InputStream responseBody = myConnection.getInputStream();
                    InputStreamReader responseBodyReader =
                            new InputStreamReader(responseBody, "UTF-8");

                    JsonReader jsonReader = new JsonReader(responseBodyReader);

//                    jsonReader.beginArray(); // Start processing the JSON object
//                    while (jsonReader.hasNext()) { // Loop through all keys
//                      //  String key = jsonReader.nextName(); // Fetch the next key
//
////                            list.add(jsonReader.);
//
//                        }

                    jsonReader.close();

                    myConnection.disconnect();

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
    }

}