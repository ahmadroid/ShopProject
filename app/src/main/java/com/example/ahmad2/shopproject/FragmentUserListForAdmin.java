package com.example.ahmad2.shopproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import androidx.appcompat.widget.SearchView;
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

public class FragmentUserListForAdmin extends Fragment {

    private List<Register> registerList;
    private User user;
    private App app;
    private ProgressBar progBar;
    private SearchView srchView;
    private RecyclerView recyclerView;
    private RegisterListAdapter adapter;
    private MyItemDecoration myItemDecoration;
//    private SharedPreferences preferences;
    private String token;
    private String myUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(App.TAG_FRAGMENT,"CFUserListForAdmin");
//        preferences = getContext().getSharedPreferences("userInfoPre", Context.MODE_PRIVATE);

        app = new App(getContext());
        if (registerList == null)
            registerList = new ArrayList<>();


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(App.TAG_FRAGMENT,"CVFUserListForAdmin");
        if (App.prefUserInfo != null) {
            token = App.prefUserInfo.getString("token", "");
            myUser = App.prefUserInfo.getString("user", "");
        }
        View view = inflater.inflate(R.layout.layout_user_list_for_admin, container, false);
        recyclerView = view.findViewById(R.id.recycler_user_for_admin);
        progBar = view.findViewById(R.id.prog_admin);
        srchView = (SearchView) view.findViewById(R.id.srch_view_admin);
//        srchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(final String newText) {
//                progBar.setVisibility(View.VISIBLE);
//                if (newText.equals("")) {
//                    getRegisterList();
//                    return false;
//                }
//                OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
//                clientBuilder.addInterceptor(new Interceptor() {
//                    @Override
//                    public okhttp3.Response intercept(Chain chain) throws IOException {
//                        okhttp3.Request orginal = chain.request();
//                        okhttp3.Request request = orginal.newBuilder()
//                                .addHeader("Content-Type", "application/json")
//                                .addHeader("shop-token", token)
//                                .method(orginal.method(), orginal.body())
//                                .build();
//                        return chain.proceed(request);
//                    }
//                });
//                OkHttpClient client = clientBuilder.build();
//                Retrofit retrofit = new Retrofit.Builder()
//                        .baseUrl(App.BASE_URL)
//                        .addConverterFactory(GsonConverterFactory.create())
//                        .client(client)
//                        .build();
//                RetrofitService retrofitService = retrofit.create(RetrofitService.class);
//                RequestUserEditForAdmin requestUserEditForAdmin = new RequestUserEditForAdmin();
//                requestUserEditForAdmin.user = myUser;
//                requestUserEditForAdmin.userSel = newText;
//                Call<ResponseRegisterListToken> call = retrofitService.searchRegisterListForAdmin(requestUserEditForAdmin);
//                call.enqueue(new Callback<ResponseRegisterListToken>() {
//                    @Override
//                    public void onResponse(Call<ResponseRegisterListToken> call, retrofit2.Response<ResponseRegisterListToken> response) {
//                        progBar.setVisibility(View.INVISIBLE);
//                        if (response.isSuccessful()) {
//                            SharedPreferences.Editor editor = preferences.edit();
//                            editor.putString("token", response.body().token);
//                            editor.apply();
//                            registerList = response.body().registerList;
//                            showList();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseRegisterListToken> call, Throwable t) {
//                        progBar.setVisibility(View.INVISIBLE);
//                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//
////                StringRequest searchRequest = new StringRequest(Request.Method.POST, App.SEARCH_USER_FOR_ADMIN_URI,
////                        new Response.Listener<String>() {
////                            @Override
////                            public void onResponse(String response) {
////                                progBar.setVisibility(View.INVISIBLE);
////                                registerList=JsonParserForUser.userParser(response);
////                                showList();
////                            }
////                        }, new Response.ErrorListener() {
////                    @Override
////                    public void onErrorResponse(VolleyError error) {
////                        progBar.setVisibility(View.INVISIBLE);
////                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
////                    }
////                }){
////                    @Override
////                    protected Map<String, String> getParams() throws AuthFailureError {
////                        Map<String,String> params=new HashMap<>();
////                        params.put("srch",newText);
////                        return params;
////                    }
////                };
////                app.getRequestQueue().add(searchRequest);
//                return false;
//            }
//        });
        myItemDecoration = new MyItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        getRegisterList();
        return view;
    }


    public void getRegisterList() {
        progBarVisible();
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Request orginal = chain.request();
                okhttp3.Request request = orginal.newBuilder()
                        .addHeader("Content-Type", "application/json")
                        .addHeader("shop-token", token)
                        .method(orginal.method(), orginal.body())
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
        login.user = myUser;
        Call<ResponseRegisterListToken> call = retrofitService.getRegisteList(login);
        call.enqueue(new Callback<ResponseRegisterListToken>() {
            @Override
            public void onResponse(Call<ResponseRegisterListToken> call, retrofit2.Response<ResponseRegisterListToken> response) {
                progBarInVisible();
                if (response.isSuccessful()) {
                    SharedPreferences.Editor editor = App.prefUserInfo.edit();
                    editor.putString("token", response.body().token);
                    editor.apply();
                    registerList = response.body().registerList;
                    showList();
                }
            }

            @Override
            public void onFailure(Call<ResponseRegisterListToken> call, Throwable t) {
                progBarInVisible();
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();

            }
        });


//        StringRequest request = new StringRequest(Request.Method.POST, App.READ_USER_FOR_ADMIN_URI,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        progBar.setVisibility(View.INVISIBLE);
//                        registerList = JsonParserForUser.userParser(response);
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

    private void showList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(myItemDecoration);
        adapter = new RegisterListAdapter(getContext(), registerList, new RegisterListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Register register) {

            }
        });
//        adapter = new RegisterListAdapter(getContext(), registerList, new RegisterListAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(User user) {
//                Intent intent=new Intent(getContext(),EditUserInfoForAdmin.class);
//                intent.putExtra("user",user);
//                startActivity(intent);
//            }
//        });
        recyclerView.setAdapter(adapter);
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
