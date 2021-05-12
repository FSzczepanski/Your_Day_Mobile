package com.example.yourdaymobile.ui.mainpage;

import android.content.Context;
import android.util.JsonReader;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.yourdaymobile.utilities.OnHttpActionDone;
import com.example.yourdaymobile.utilities.Singleton;
import com.example.yourdaymobile.data.Todo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainPageViewModel extends ViewModel {

    private ArrayList<Todo> todosList;
    private Context context;
    private RequestQueue mRequestQueue;
    private String authToken;
    private String notesUrl;

    public MainPageViewModel() {
        authToken = Singleton.authToken;
    }

    public void setContext(Context context) {
        this.context = context;
        volleyInit();
    }


    public void volleyInit(){
            notesUrl = "http://192.168.0.12:3000/note/";

        // Instantiate the cache
        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024); // 1MB cap
        // Set up the network to use HttpURLConnection as the HTTP client.
                Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
                mRequestQueue = new RequestQueue(cache, network);

        // Start the queue
                mRequestQueue.start();
    }



    //get - podejście z async taskiem, inne operacje http są już wykonywane za pomocą volleya ze względu
    // na problematyke async taska z bardziej skomplikowanymi zapytaniami
    public ArrayList<Todo> getTodos(URL url, String authToken) throws IOException {
        ArrayList<Todo> todos = new ArrayList<>();

        HttpURLConnection myConnection =
                (HttpURLConnection) url.openConnection();
        myConnection.setRequestProperty("auth-token", authToken);

        if (myConnection.getResponseCode() == 200) {

            InputStream responseBody = myConnection.getInputStream();

            todos = readJsonStream(responseBody);
        }
        return  todos;
    }

    private ArrayList<Todo> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readTodosArray(reader);
        } finally {
            reader.close();
        }
    }

    private ArrayList<Todo> readTodosArray(JsonReader reader) throws IOException {
        ArrayList<Todo> todos = new ArrayList<Todo>();

        reader.beginArray();
        while (reader.hasNext()) {
            todos.add(readMessage(reader));
        }
        reader.endArray();
        return todos;
    }

    private Todo readMessage(JsonReader reader) throws IOException {
        String id = "";
        String author = "";
        String text = "";

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("_id")) {
                id = reader.nextString();
            }
            else if(name.equals("author")) {
                author = reader.nextString();
            }
            else if(name.equals("text")) {
                text = reader.nextString();
            }else{
                reader.skipValue();

            }
        }
        reader.endObject();

        Log.e("id",id);
        return new Todo(id, text, false);
    }




    public void createTodo(String todoDescription, OnHttpActionDone done) {
        JSONObject jsonobject = new JSONObject();

        try {
            jsonobject.put("text", todoDescription);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(notesUrl, jsonobject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
               // Log.e("Hello Response: ", String.valueOf(response));
                done.onDone();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Hello Response Error: ",error.toString());
            }
        }){
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("auth-token", authToken);
                return headers;
            }
        };

        mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(jsonObjectRequest);

    }

    public void deleteTodo(String id, OnHttpActionDone deleted) {
        String url = notesUrl + id;
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE,url , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.e("mainVMdelete",response.toString());
                deleted.onDone();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("mainVMdelete", Objects.requireNonNull(error.getMessage()));
                deleted.onDone();
            }
        }){
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("auth-token", authToken);
                return headers;
            }
        };

        mRequestQueue.add(stringRequest);
    }


    public void updateTodo(String todoDescription,String id, OnHttpActionDone done) {
        JSONObject jsonobject = new JSONObject();

        try {
            jsonobject.put("text", todoDescription);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = notesUrl+id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PATCH,url, jsonobject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
               // Log.e("Hello Response: ", String.valueOf(response));
                done.onDone();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Hello Response Error: ",error.toString());
            }
        }){
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("auth-token", authToken);
                return headers;
            }
        };

        mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(jsonObjectRequest);

    }

    public void getWeather(String city,WeatherCallback weatherCallback){
        String temperature = "";
        JSONObject jsonobject = new JSONObject();



        String url = "http://192.168.0.12:3000/weather/"+city;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,jsonobject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Log.e("Hello Response: ", String.valueOf(response));

                try {
                    String temperature = response.getString("temperature");
                    String desc = response.getString("weather");
                    weatherCallback.onCallback(city,temperature,desc);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Hello Response Error: ",error.toString());
            }
        }){
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("auth-token", authToken);
                return headers;
            }
        };

        mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(jsonObjectRequest);



    }
}