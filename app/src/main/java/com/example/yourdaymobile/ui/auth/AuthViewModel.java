package com.example.yourdaymobile.ui.auth;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class AuthViewModel extends ViewModel {

    public Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public void login(String email, String password, AuthTokenCallback callback) {
        String authToken = "";

        JSONObject jsonobject = new JSONObject();

        try {
            jsonobject.put("email", email);
            jsonobject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(twojeIp"/auth/login", jsonobject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("wprked", response.toString());
                callback.onCallback(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{

                    if (error.getMessage()==null){
                        Log.d("error AuthViewModel","error1");
                        callback.onCallback("wrong");
                    }else{
                        String[] getCode1 = error.getMessage().split("Value ");
                        String[] getCode2 = getCode1[1].split(" of type");
                        String resp = getCode2[0];
                        callback.onCallback(resp);
                    }

                    } catch (Exception e2) {
                    Log.d("error AuthViewModel","error3");
                      callback.onCallback("wrong");
                        e2.printStackTrace();
                    }
                }


        });
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(jsonObjectRequest);

    }
}


