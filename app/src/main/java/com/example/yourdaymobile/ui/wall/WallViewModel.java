package com.example.yourdaymobile.ui.wall;

import android.content.Context;
import android.util.JsonReader;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.yourdaymobile.data.Post;
import com.example.yourdaymobile.data.Todo;
import com.example.yourdaymobile.utilities.OnHttpActionDone;
import com.example.yourdaymobile.utilities.Singleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WallViewModel extends ViewModel {
    private Context context;
    private RequestQueue mRequestQueue;
    private String authToken;


    public WallViewModel() {
        authToken = Singleton.authToken;
    }

    public void setContext(Context context) {
        this.context = context;
        volleyInit();
    }

    public void volleyInit(){
        // Instantiate the cache
        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024); // 1MB cap
        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);

        // Start the queue
        mRequestQueue.start();
    }

    public ArrayList<Post> getPosts(URL url) throws IOException {
        ArrayList<Post> posts = new ArrayList<>();

        HttpURLConnection myConnection =
                (HttpURLConnection) url.openConnection();
        myConnection.setRequestProperty("auth-token", Singleton.authToken);

        if (myConnection.getResponseCode() == 200) {

            InputStream responseBody = myConnection.getInputStream();

            posts = readJsonStream(responseBody);
        }
        return  posts;
    }

    private ArrayList<Post> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readTodosArray(reader);
        } finally {
            reader.close();
        }
    }

    private ArrayList<Post> readTodosArray(JsonReader reader) throws IOException {
        ArrayList<Post> posts = new ArrayList<Post>();

        reader.beginArray();
        while (reader.hasNext()) {
            posts.add(readMessage(reader));
        }
        reader.endArray();
        return posts;
    }

    private Post readMessage(JsonReader reader) throws IOException {
        String id = "";
        String author = "";
        String text = "";
        String date= "";
        String dateRaw = null;

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
            }
            else if(name.equals("date")) {
                dateRaw = reader.nextString();

            }else{
                reader.skipValue();

            }
        }

        if (dateRaw!=null){
            String[] tab= dateRaw.split("T");
            String dataDzien = tab[0];
            String[] tab2 = tab[1].split(":");
            String dataGodzina = tab2[0]+" "+tab2[1];
            date = dataDzien+"  "+dataGodzina;

        }


        reader.endObject();
        return new Post(id, text, author, date);
    }

    public void createPost(String postDescription, OnHttpActionDone done) {
        JSONObject jsonobject = new JSONObject();

        try {
            jsonobject.put("text", postDescription);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("http://192.168.0.12:3000/post", jsonobject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("Hello Response: ", String.valueOf(response));
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


}