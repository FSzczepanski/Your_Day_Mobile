# Your_Day_Mobile

# Dokumentacja na zajęcia:

# Aplikacja mobilna, Android Studio + Java, Api - Async Task/Volley - OpenWeatherMap - wykorzystanie w API restowym 

# Identyfikacja raportu

**Nazwa** **przedmiotu**: 	
Przetwarzanie danych w chmurze obliczeniowej

**Grupa**: S2232

**Rok**: II (4 semestr)

**Osoby** **tworzące** **projekt**:

- Filip Szczepański  
- Mateusz Sobczyk  


# Cel projektu

Your Day Mobile jest aplikacją mobilną dla serwisu Your Day. Aplikacja korzysta z api, które jest obsługiwane za pomocą async tasków i biblioteki Volley. Your Day Mobile pozwala na dodawanie, edycje oraz usuwanie własnych zadań. oraz na dodawanie publicznych dla całej organizacji postów. Poza tym aplikacja wyświetla aktualną pogodę.

# Lista funkcjonalności  (zdjęcia wraz z istotnymi elementami kodu)

### Logowanie

![1](https://user-images.githubusercontent.com/61236736/117999525-3946f900-b345-11eb-9e83-5b5b93541d5e.jpg)

W aplikacji mobilnej ze względu na założenia bezpieczeństwa możliwe jest jedynie logowanie, rejestracja jest dostępna w serwisie internetowym i odbywa się ona się po specjalnym linku z wygnerowanym tokenem.

Po zalogowaniu z APi zwracany jest token autentykujący, później przy każdej operacji wymagającej zalogowanego użytkownika jest on podawany w nagłówku zapytania. Zmienna Tokena przechowywana jest w Singletonie

AuthFragment.java

```java
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
                    signIn(email, password);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void signIn(String email, String password) throws IOException, JSONException {
        mViewModel.login(email, password, new AuthTokenCallback() {
            @Override
            public void onCallback(String authToken) {
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
````

```java
AuthViewModel.java

public void login(String email, String password, AuthTokenCallback callback) {
        String authToken = "";

        JSONObject jsonobject = new JSONObject();

        try {
            jsonobject.put("email", email);
            jsonobject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("http://192.168.0.12:3000/auth/login", jsonobject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
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
 ````
 Singleton.java
 
 ```java
 
public class Singleton {
    private static Singleton instance = null;

    public static String authToken;

    //signleton pozwala na przechowywanie zmiennej authTokena przez całość działania aplikacji
    public Singleton() {
    }
    public static Singleton getInstance() {
        if(instance == null) {
            instance = new Singleton();
            authToken = new String();

        }
        return instance;
    }

}
````
 
## Główny ekran

Na głównym ekranie znajdują się prywatne zadania użytkownika oraz aktualna pogoda

![2](https://user-images.githubusercontent.com/61236736/117999528-39df8f80-b345-11eb-82fc-05bda693e5d8.jpg)

### Pogoda

Po naciśnięciu na pogodę możliwa jest zmiana miasta

![3](https://user-images.githubusercontent.com/61236736/117999529-39df8f80-b345-11eb-8c7b-777f6036fe4d.jpg)

![4](https://user-images.githubusercontent.com/61236736/117999535-3a782600-b345-11eb-8d36-96b797b76932.jpg)


MainPageFragment.java
```java
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
````

MainPageViewModel
```java
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

````

### Zadania (Todos)
CRUD prywatnych zadań użytkownika

* Lista zadań

![4](https://user-images.githubusercontent.com/61236736/117999535-3a782600-b345-11eb-8d36-96b797b76932.jpg)

* Edycja zadania

![5](https://user-images.githubusercontent.com/61236736/117999537-3a782600-b345-11eb-8e7c-b5f0047f2c92.jpg)

![6](https://user-images.githubusercontent.com/61236736/117999538-3b10bc80-b345-11eb-8314-684f874eb389.jpg)

![7](https://user-images.githubusercontent.com/61236736/117999559-419f3400-b345-11eb-89f3-222b97a9e36a.jpg)

* Usuwanie zadania

![8](https://user-images.githubusercontent.com/61236736/117999563-4237ca80-b345-11eb-992a-cbe1460d0393.jpg)

![9](https://user-images.githubusercontent.com/61236736/117999564-42d06100-b345-11eb-84aa-4cb670f26b02.jpg)

* Dodawanie zadania

![10](https://user-images.githubusercontent.com/61236736/117999567-42d06100-b345-11eb-8cf1-b8bea45cf86f.jpg)

![11](https://user-images.githubusercontent.com/61236736/117999568-4368f780-b345-11eb-87e2-5c368d0c64b5.jpg)

Kod:
Część MainPageFragment.java która dotyczy zadań
 Dopóki zadania nie zostaną zwrócone wyświetlany jest progress dialog.

```java
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
````

MainPageViewModel.java

```java
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

````

Jeden z dialogów (dodawanie zadania), jest ich dość sporo więc w readme umieszczam tylko jeden.

```java
public class AddNewTodoDialog extends DialogFragment {
    private MainPageViewModel mViewModel;
    private String todoDescription="";
    private DialogFragment dialog;
    private View view;
    private OnHttpActionDone onTodoAdded;

    public AddNewTodoDialog(MainPageViewModel mViewModel, OnHttpActionDone onTodoAdded) {
        this.mViewModel = mViewModel;
        this.onTodoAdded = onTodoAdded;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Dodaj nowe zadanie");
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_todo, null);
        builder.setView(view);

        EditText editTextTodoDesc = view.findViewById(R.id.etTodo);
        TextView textViewAdd = view.findViewById(R.id.tvAdd);
        TextView textViewCancel = view.findViewById(R.id.tvCancel);


        textViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                todoDescription = editTextTodoDesc.getText().toString();

                if ((!todoDescription.matches(""))) {

                    //interfejs informuje nas kiedy doda sie nowe zadanie i przekazujemy to do fragmentu żeby zaaktualizować liste
                    mViewModel.createTodo(todoDescription, new OnHttpActionDone() {
                        @Override
                        public void onDone() {
                            onTodoAdded.onDone();
                        }
                    });

                    AddNewTodoDialog.this.getDialog().cancel();
                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(), "Wprowadź prawidłowe dane ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTodoDialog.this.getDialog().cancel();
            }
        });



        return builder.create();
    }






}
````




## Wall Fragment - Ekran publicznych dla całej organizacji Postów

* lista postów

![12](https://user-images.githubusercontent.com/61236736/117999569-4368f780-b345-11eb-8f7a-2972fefc2d39.jpg)

* dodawanie posta

![13](https://user-images.githubusercontent.com/61236736/117999570-4368f780-b345-11eb-85a0-8a4881c6b707.jpg)

![14](https://user-images.githubusercontent.com/61236736/117999571-44018e00-b345-11eb-815e-ac91a5d47d43.jpg)

WallFragment.java
```java
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
````

WallFragmentViewModel.java
```java

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
````

WallAdapter.java - obsługa listy
```java
public class WallAdapter extends RecyclerView.Adapter<WallAdapter.MyViewHolder> {
    Context context;
    private ArrayList<Post> posts;

    @NonNull
    @Override
    public WallAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.post_row,parent,false);

        return new WallAdapter.MyViewHolder(view);
    }

    public WallAdapter(Context context, ArrayList<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @Override
    public void onBindViewHolder(@NonNull WallAdapter.MyViewHolder holder, int position) {
        Post currentItem = posts.get(position);

        holder.postTextView.setText(currentItem.getText());
        holder.timeTextView.setText(currentItem.getDate());
        holder.authorTextView.setText(currentItem.getAuthor());

    }


    @Override
    public int getItemCount() {
        return posts.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView postTextView, authorTextView , timeTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            postTextView = itemView.findViewById(R.id.todoTextView);
            authorTextView = itemView.findViewById(R.id.authorTV);
            timeTextView = itemView.findViewById(R.id.dateTV);

        }
    }
````



## Ekran ustawień - możliwe wylogowanie z aplikacji

![15](https://user-images.githubusercontent.com/61236736/117999579-45cb5180-b345-11eb-8821-e57a8e60448d.jpg)


```java
private void logout(){
        View logoutButton = root.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Singleton.authToken="";
                final NavController navController = Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment);
                navController.navigate(R.id.action_optionsFragment_to_authFragment);
            }
        });
    }
````


# **Dziękujemy, za uwagę.**

**Filip Szczepański**

**Mateusz Sobczyk**
