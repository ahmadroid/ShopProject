package com.example.ahmad2.shopproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentObjectListForUser extends Fragment {

    private RecyclerView recyclerView;
    private List<ObjectInfo> objectList;
    private ObjectListForUserAdapter adapter;
    private MyItemDecoration myItemDecoration;
    private App app;
    private ProgressBar progBar, editPassProgBar;
    private SearchView srchView;
    private String user="";
//    private String userName;
    private static FragmentOrderForUser fragmentOrderForUser;
    private String token="";
//    private SharedPreferences preferences;
    private TextView txtEmpty;
    private RelativeLayout relLayout;

    public static FragmentObjectListForUser newInstance(String user, FragmentOrderForUser fragment) {
        fragmentOrderForUser = fragment;
        FragmentObjectListForUser fragmentObjectListForUser = new FragmentObjectListForUser();
        Bundle arg = new Bundle();
        arg.putString("user", user);
//        arg.putString("userName", userName);
        fragmentObjectListForUser.setArguments(arg);
        return fragmentObjectListForUser;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(App.TAG_FRAGMENT,"CFObjectListForUser");
//        preferences=getContext().getSharedPreferences("userInfoPre",Context.MODE_PRIVATE);

//        userName = getArguments().getString("userName");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(App.TAG_FRAGMENT,"CVFObjectListForUser");
        if (App.prefUserInfo!=null) {
            token = App.prefUserInfo.getString("token", "");
            user = App.prefUserInfo.getString("user", "");
        }
        View view = inflater.inflate(R.layout.layout_object_list_for_user, container, false);
        progBar = view.findViewById(R.id.prog_list_user);
        relLayout=view.findViewById(R.id.rel_layout_list_user);
        txtEmpty=view.findViewById(R.id.txt_empty_list_user);
        srchView = view.findViewById(R.id.srch_view_list_user);
        srchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
//                progBar.setVisibility(View.VISIBLE);
//                if (newText.isEmpty()) {
//                    getObjectList();
//                    return false;
//                }
                OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
                clientBuilder.addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        okhttp3.Request orginal = chain.request();
                        okhttp3.Request request = orginal.newBuilder()
                                .addHeader("Content-Type", "application/json")
                                .addHeader("shop-token", token)
                                .method(orginal.method(),orginal.body())
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
                login.pass = newText;
                Call<ResponseObjectListToken> call = retrofitService.getObjectListBySearchForuUser(login);
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
                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
                        progBarInVisible();
                    }
                });


//                StringRequest searchObjectUser = new StringRequest(Request.Method.POST, App.SEARCH_OBJECT_FOR_USER_URI,
//                        new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response) {
//                                progBar.setVisibility(View.INVISIBLE);
//                                objectList = JsonParserForObject.getObjectList(response);
//                                showList();
//
//                            }
//                        }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        progBar.setVisibility(View.INVISIBLE);
//                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }) {
//                    @Override
//                    protected Map<String, String> getParams() throws AuthFailureError {
//                        Map<String, String> params = new HashMap<>();
//                        params.put("srch", newText);
//                        return params;
//                    }
//                };
//                app.getRequestQueue().add(searchObjectUser);
                return false;
            }
        });
        app = new App(getContext());
        recyclerView = view.findViewById(R.id.recycler_list_user);
        if (objectList == null) {
            objectList = new ArrayList<>();
        }
        myItemDecoration = new MyItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        progBarVisible();
//        if (!App.IS_OBJECTLIST_USER) {
//            App.IS_OBJECTLIST_USER=true;
//            getObjectList();
//        }
        getObjectList();
        return view;
    }

    private void showList() {
        recyclerView.addItemDecoration(myItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new ObjectListForUserAdapter(getContext(), getActivity(), objectList, new ObjectListForUserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ObjectInfo objectInfo) {
                Intent intent = new Intent(getContext(), InsertOrderUser.class);
                intent.putExtra("object", objectInfo);
                intent.putExtra("user", user);
//                intent.putExtra("userName", userName);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void changeSize(Window window) {
        Point size = new Point();
        if (getActivity() != null)
            getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        window.setLayout(size.x, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void getObjectList() {
//        if (!App.IS_OBJECTLIST_USER) {
//            return;
//        }
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
        Login login = new Login();
        login.user = user;
        Call<ResponseObjectListToken> call = retrofitService.getObjectListForUser(login);
        call.enqueue(new Callback<ResponseObjectListToken>() {
            @Override
            public void onResponse(Call<ResponseObjectListToken> call, retrofit2.Response<ResponseObjectListToken> response) {
                progBarInVisible();
                if (response.isSuccessful()) {
                    SharedPreferences.Editor editor=App.prefUserInfo.edit();
                    editor.putString("token",response.body().token);
                    editor.apply();
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
                progBarInVisible();
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
