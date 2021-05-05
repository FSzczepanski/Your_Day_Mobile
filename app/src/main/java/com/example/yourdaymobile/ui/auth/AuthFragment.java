package com.example.yourdaymobile.ui.auth;

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

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.yourdaymobile.MainActivity;
import com.example.yourdaymobile.R;
import com.example.yourdaymobile.Singleton;
import com.example.yourdaymobile.data.Todo;
import com.example.yourdaymobile.ui.TabLayoutDisabler;
import com.example.yourdaymobile.ui.mainpage.TodosAdapter;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class AuthFragment extends Fragment implements TabLayoutDisabler {

    private AuthViewModel mViewModel;
    private View root;
    private ProgressDialog progressDialog;

    public static AuthFragment newInstance() {
        return new AuthFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.auth_fragment, container, false);
        hideTabLayout();
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
       mViewModel.setContext(getActivity());
        login();
    }

    public void login(){
        Button loginButton = root.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialogInit();
                EditText emailEdit = root.findViewById(R.id.loginEmail);
                EditText passwordEdit = root.findViewById(R.id.loginPassword);
                String email = emailEdit.getText().toString();
                String password = passwordEdit.getText().toString();

                try {
                    signIn("sobczyk.m@outlook.com", "password123");
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

                Log.d("check",email+" "+password);


            }
        });
    }

    private void signIn(String email, String password) throws IOException, JSONException {
        mViewModel.login(email, password, new AuthTokenCallback() {
            @Override
            public void onCallback(String authToken) {
                //Todo sprawdzenie czy kod jest okej
                progressDialog.dismiss();
                if (authToken.equals("wrong")){
                    Toast.makeText(getContext(),"Podano błędne dane lub wystapił nieoczekiwany błąd, spróbuj ponownie",Toast.LENGTH_LONG).show();
                }else{
                    Singleton.authToken = authToken;

                    NavController navController = Navigation.findNavController(requireActivity(),
                            R.id.my_nav_host_fragment);
                    navController.navigate(R.id.action_authFragment_to_mainPageFragment);
                }
            }
        });
    }

    @Override
    public void hideTabLayout() {
        MainActivity.hideTabLayout();
    }

    @Override
    public void showTabLayout() {

    }

    private void progressDialogInit() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }


}