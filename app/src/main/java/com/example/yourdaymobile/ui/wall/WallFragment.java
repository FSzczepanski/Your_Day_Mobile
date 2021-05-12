package com.example.yourdaymobile.ui.wall;

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

import com.example.yourdaymobile.R;
import com.example.yourdaymobile.data.Post;
import com.example.yourdaymobile.data.Todo;
import com.example.yourdaymobile.ui.mainpage.AddNewTodoDialog;
import com.example.yourdaymobile.ui.mainpage.MainPageFragment;
import com.example.yourdaymobile.ui.mainpage.TodosAdapter;
import com.example.yourdaymobile.utilities.OnHttpActionDone;
import com.example.yourdaymobile.utilities.Singleton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class WallFragment extends Fragment {

    private WallViewModel mViewModel;
    private View root;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerWall;
    private WallAdapter wallAdapter;
    private ArrayList<String> list;
    private AddNewPostDialog dialog;

    public static WallFragment newInstance() {
        return new WallFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.wall_fragment, container, false);
        progressDialogInit();

        addPost();
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(WallViewModel.class);
        mViewModel.setContext(getContext());
        setUpWall();
    }

    private void setUpWall(){
        WallFragment.DownloadTask task = new WallFragment.DownloadTask();
        task.execute("http://192.168.0.12:3000/post");

    }

    private void addPost(){
        FloatingActionButton addButton = root.findViewById(R.id.addNewPost);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new AddNewPostDialog(mViewModel, new OnHttpActionDone() {
                    @Override
                    public void onDone() {
                        refreshList();
                    }
                });
                dialog.show(getParentFragmentManager(), "DialogFragment");
            }
        });
    }

    private void refreshList(){

        recyclerWall = root.findViewById(R.id.wallRV);
        recyclerWall.setLayoutManager(new LinearLayoutManager(getActivity()));
        wallAdapter = new WallAdapter(getActivity(), new ArrayList<Post>());
        recyclerWall.setAdapter(wallAdapter);
        wallAdapter.notifyDataSetChanged();

        setUpWall();
    }

    private void progressDialogInit() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);

        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public class DownloadTask extends AsyncTask<String, Void, ArrayList<Post>> {

        @Override
        protected ArrayList<Post> doInBackground(String... urls) {

            //getting todos list
            ArrayList<Post> posts = new ArrayList<>();
            URL url;
            try {
                url = new URL(urls[0]);
                posts=  mViewModel.getPosts(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //posting todos list

            return posts;
        }


        @Override
        protected void onPostExecute(ArrayList<Post> posts) {
            super.onPostExecute(posts);


            try {
                if (posts.get(0).getId().equals("xd404")){
                    progressDialog.dismiss();
                    NavController navController = Navigation.findNavController(requireActivity(),
                            R.id.my_nav_host_fragment);
                    navController.navigate(R.id.action_mainPageFragment_to_authFragment);
                }else {
                    recyclerWall = root.findViewById(R.id.wallRV);

                    wallAdapter = new WallAdapter(getActivity(), posts);
                    recyclerWall.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerWall.setAdapter(wallAdapter);
                    wallAdapter.notifyDataSetChanged();

                    progressDialog.dismiss();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}