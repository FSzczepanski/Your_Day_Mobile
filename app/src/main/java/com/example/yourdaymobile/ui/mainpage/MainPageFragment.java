package com.example.yourdaymobile.ui.mainpage;

import androidx.lifecycle.ViewModelProvider;

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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yourdaymobile.ui.MainActivity;
import com.example.yourdaymobile.R;
import com.example.yourdaymobile.utilities.OnHttpActionDone;
import com.example.yourdaymobile.utilities.Singleton;
import com.example.yourdaymobile.data.Todo;
import com.example.yourdaymobile.utilities.TabLayoutDisabler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
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
    private AddNewTodoDialog dialog;
    private AddCityDialog addCityDialog;


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
        addTodo();
        setUpList();
        weatherWidget();
    }

    public void setUpList() {

        DownloadTask task = new DownloadTask();
        task.execute("http://192.168.0.12:3000/note");

    }

    private void addTodo(){
        FloatingActionButton addButton = root.findViewById(R.id.addNewTodo);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new AddNewTodoDialog(mViewModel, new OnHttpActionDone() {
                    @Override
                    public void onDone() {
                        refreshList();
                    }
                });
                dialog.show(getParentFragmentManager(), "DialogFragment");
            }
        });
    }


    private void progressDialogInit() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);

        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void refreshList(){
        recyclerTodos = root.findViewById(R.id.todosRV);
        recyclerTodos.setLayoutManager(new LinearLayoutManager(getActivity()));
        todosAdapter = new TodosAdapter(getActivity(), new ArrayList<Todo>(), mViewModel,getParentFragmentManager(), new OnHttpActionDone() {
            @Override
            public void onDone() {
                refreshList();
            }
        });
        recyclerTodos.setAdapter(todosAdapter);
        todosAdapter.notifyDataSetChanged();

        setUpList();
    }

    public void weatherWidget(){
        mViewModel.getWeather("Gdansk", new WeatherCallback() {
            @Override
            public void onCallback(String city,String temp, String weatherDescription) {
                TextView cityTV = root.findViewById(R.id.cityTV);
                TextView tempTV = root.findViewById(R.id.tempTv);
                TextView descTV = root.findViewById(R.id.weatherDescTV);
                cityTV.setText(city);
                String[] tTab = temp.split("\\.");
                tempTV.setText(tTab[0]+"°C");
                descTV.setText(weatherDescription);
            }
        });

        View weatherView = root.findViewById(R.id.weatherView);
        weatherView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCityDialog = new AddCityDialog(mViewModel, new WeatherCallback() {
                    @Override
                    public void onCallback(String city,String temp, String weatherDescription) {
                        TextView cityTV = root.findViewById(R.id.cityTV);
                        TextView tempTV = root.findViewById(R.id.tempTv);
                        TextView descTV = root.findViewById(R.id.weatherDescTV);
                        cityTV.setText(city);
                        tempTV.setText(temp+"°C");
                        descTV.setText(weatherDescription);

                    }
                });

                addCityDialog.show(getParentFragmentManager(),"Dialog");
            }
        });
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
                    todosAdapter = new TodosAdapter(getActivity(), todos, mViewModel,getParentFragmentManager(), new OnHttpActionDone() {
                        @Override
                        public void onDone() {
                            refreshList();
                        }
                    });
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