package com.example.yourdaymobile.ui.mainpage;

import android.content.Context;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.yourdaymobile.R;
import com.example.yourdaymobile.data.Todo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MainPageViewModel extends ViewModel {

    private ArrayList<Todo> todosList;
    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

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
        return new Todo(id, text, false);
    }
}