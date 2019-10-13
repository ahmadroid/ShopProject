package com.example.ahmad2.shopproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentObjectListForAdmin extends Fragment {

    private ObjectListForAdminAdapter adapter;
    private List<ObjectInfo> objectList;
    private App app;
    private ProgressBar progBar;
    private SearchView srchView;
    private RecyclerView recyclerView;
//    private SharedPreferences preferences;
    private String user="";
    private String token="";
    private NestedScrollView nstScrl;
    private MyDatabaseHelper dbHelper;
    private RelativeLayout relLayout;
    private TextView txtEmpty;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(App.TAG_FRAGMENT,"CFObjectListForAdmin");
        init();
        dbHelper=new MyDatabaseHelper(getContext());
//        dbHelper.clearDatabase();
//        getObjectList();
    }

    private void init() {
//        preferences = getContext().getSharedPreferences("userInfoPre", Context.MODE_PRIVATE);

        app = new App(getContext());
        if (objectList == null) {
            objectList = new ArrayList<>();
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(App.TAG_FRAGMENT,"CVFObjectListForAdmin");
        if (App.prefUserInfo!=null) {
            token = App.prefUserInfo.getString("token", "");
            user = App.prefUserInfo.getString("user", "");
        }
        View view = inflater.inflate(R.layout.activity_object_list_for_admin, container, false);
        recyclerView = view.findViewById(R.id.recycler_list_admin);
        relLayout =view.findViewById(R.id.rel_layout_list_admin);
        txtEmpty=view.findViewById(R.id.txt_empty_list_admin);
        progBar = view.findViewById(R.id.prog_list_admin);
//        nstScrl=view.findViewById(R.id.nest_scrol_list_admin);
//        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
//            nstScrl.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//                @Override
//                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                    Toast.makeText(getContext(), "scrool", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
        progBarVisible();
//        if (! App.IS_OBJECTLIST_ADMIN) {
//            App.IS_OBJECTLIST_ADMIN=true;
//            getObjectList();
//        }
        getObjectList();
        srchView = view.findViewById(R.id.srch_view_list_admin);
        srchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
//                progBar.setVisibility(View.VISIBLE);
                OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
                clientBuilder.addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        okhttp3.Request orginal = chain.request();
                        okhttp3.Request request = orginal.newBuilder()
                                .addHeader("Content-Type", "application/json")
                                .addHeader("shop-token", token)
                                .build();
                        return chain.proceed(request);
                    }
                });
                OkHttpClient client = clientBuilder.build();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(App.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .build();
                RetrofitService retrofitService = retrofit.create(RetrofitService.class);
                Login login = new Login();
                login.user = user;
                login.pass=newText;
                Call<ResponseObjectListToken> call = retrofitService.getObjectListBySearchForAdmin(login);
                call.enqueue(new Callback<ResponseObjectListToken>() {
                    @Override
                    public void onResponse(Call<ResponseObjectListToken> call, retrofit2.Response<ResponseObjectListToken> response) {
                        progBarInVisible();
                        if (response.isSuccessful()) {
                            SharedPreferences.Editor editor=App.prefUserInfo.edit();
                            editor.putString("token",response.body().token);
                            editor.apply();
                            objectList = response.body().objectList;
                            showList();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObjectListToken> call, Throwable t) {
                        progBarInVisible();
                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
                    }
                });


//                StringRequest searchObjectRequest = new StringRequest(Request.Method.POST, App.SEARCH_OBJECT_FOR_ADMIN_URI,
//                        new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response) {
//                                progBar.setVisibility(View.INVISIBLE);
//                                objectList = JsonParserForObject.getObjectList(response);
//                                showList();
//                            }
//                        }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        progBar.setVisibility(View.INVISIBLE);
//                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
//
//                    }
//                }) {
//                    @Override
//                    protected Map<String, String> getParams() throws AuthFailureError {
//                        Map<String, String> params = new HashMap<>();
//                        params.put("srch", newText);
//                        return params;
//                    }
//                };
//                app.getRequestQueue().add(searchObjectRequest);
                return false;
            }
        });
//        showList();
        return view;
    }

    public void getObjectList() {
//        if (! App.IS_OBJECTLIST_ADMIN){
//            return;
//        }

//        init();
//        progBar.setVisibility(View.VISIBLE);
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Request orginal = chain.request();
                okhttp3.Request request = orginal.newBuilder()
                        .addHeader("Content-Type", "application/json")
                        .addHeader("shop-token", token)
                        .build();
                return chain.proceed(request);
            }
        });
        OkHttpClient client = clientBuilder.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(App.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        final Login login = new Login();
        login.user = user;
        Call<ResponseObjectListToken> call = retrofitService.getObjectListForAdmin(login);
        call.enqueue(new Callback<ResponseObjectListToken>() {
            @Override
            public void onResponse(Call<ResponseObjectListToken> call, retrofit2.Response<ResponseObjectListToken> response) {
                progBarInVisible();
                if (response.isSuccessful()) {
                    SharedPreferences.Editor editor=App.prefUserInfo.edit();
                    editor.putString("token",response.body().token);
                    editor.apply();
//                    MyDatabaseHelper dbHelper=new MyDatabaseHelper(getContext());
//                    dbHelper.insertObjectListToDb(response.body().objectList);
//                    App.objectList=response.body().objectList;
//                    dbHelper.searchObjectToDb(301);
                    objectList = response.body().objectList;
                    if (objectList.size()==0){
                        relLayout.setVisibility(View.INVISIBLE);
                        txtEmpty.setVisibility(View.VISIBLE);
                    }else {
                        relLayout.setVisibility(View.VISIBLE);
                        txtEmpty.setVisibility(View.INVISIBLE);
                        showList();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseObjectListToken> call, Throwable t) {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
                Log.i("tage",t.getMessage());
                progBarInVisible();
            }
        });


//        StringRequest request = new StringRequest(Request.Method.POST, App.READ_OBJECT_URI,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        progBar.setVisibility(View.INVISIBLE);
//                        objectList = JsonParserForObject.getObjectList(response);
//                        showList();
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                progBar.setVisibility(View.INVISIBLE);
//                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//        app.getRequestQueue().add(request);
    }




    public void showList() {
//        objectList=dbHelper.readObjectListFromDb();
//        objectList=App.objectList;
        adapter = new ObjectListForAdminAdapter(getContext(), getActivity(), objectList, new ObjectListForAdminAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ObjectInfo objectInfo) {
                deleteObject(objectInfo);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new MyItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
        progBarInVisible();
    }

    private void deleteObject(ObjectInfo objectInfo) {
//        progBar.setVisibility(View.VISIBLE);
        OkHttpClient.Builder clientBuilder=new OkHttpClient.Builder();
        clientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request request=original.newBuilder()
                        .addHeader("Content-Type","application/json")
                        .addHeader("shop-token",token)
                        .method(original.method(),original.body())
                        .build();
                return chain.proceed(request);
            }
        });
        OkHttpClient client=clientBuilder.build();
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(App.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        RetrofitService retrofitService=retrofit.create(RetrofitService.class);
        Login login=new Login();
        login.user=user;
        login.pass=String.valueOf(objectInfo.getCodeObject());
        Call<ResponseMessageToken> call = retrofitService.deleteObjectFromSell(login);
        call.enqueue(new Callback<ResponseMessageToken>() {
            @Override
            public void onResponse(Call<ResponseMessageToken> call, retrofit2.Response<ResponseMessageToken> response) {
//                progBar.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()){
                    SharedPreferences.Editor editor=App.prefUserInfo.edit();
                    editor.putString("token",response.body().token);
                    editor.apply();
                    Toast.makeText(getContext(), response.body().message, Toast.LENGTH_SHORT).show();
                    App.fragmentObjectListForAdmin.getObjectList();
                }
            }

            @Override
            public void onFailure(Call<ResponseMessageToken> call, Throwable t) {
//                progBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void progBarVisible(){
        try{
            progBar.setVisibility(View.VISIBLE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void progBarInVisible(){
        try{
            progBar.setVisibility(View.INVISIBLE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
