package com.example.ahmad2.shopproject;

import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

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
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentObjectListForUserAgree extends Fragment {

    private RecyclerView recyclerView;
    private List<ObjectInfo> objectList;
    private ObjectListForUserAdapterAgree adapter;
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

    public static FragmentObjectListForUserAgree newInstance(String user, FragmentOrderForUser fragment) {
        fragmentOrderForUser = fragment;
        FragmentObjectListForUserAgree fragmentObjectListForUser = new FragmentObjectListForUserAgree();
        Bundle arg = new Bundle();
        arg.putString("user", user);
//        arg.putString("userName", userName);
        fragmentObjectListForUser.setArguments(arg);
        return fragmentObjectListForUser;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        preferences=getContext().getSharedPreferences("userInfoPre",Context.MODE_PRIVATE);

//        userName = getArguments().getString("userName");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (App.prefUserInfo!=null) {
            token = App.prefUserInfo.getString("token", "");
            user = App.prefUserInfo.getString("user", "");
        }
        View view = inflater.inflate(R.layout.layout_object_list_for_user_agree, container, false);
        progBar = view.findViewById(R.id.prog_list_user_agree);
        relLayout=view.findViewById(R.id.rel_layout_list_user_agree);
        txtEmpty=view.findViewById(R.id.txt_empty_list_user_agree);
        srchView = view.findViewById(R.id.srch_view_list_user_agree);
        recyclerView = view.findViewById(R.id.recycler_list_user_agree);
//        srchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(final String newText) {
//
//                OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
//                clientBuilder.addInterceptor(new Interceptor() {
//                    @Override
//                    public okhttp3.Response intercept(Chain chain) throws IOException {
//                        okhttp3.Request orginal = chain.request();
//                        okhttp3.Request request = orginal.newBuilder()
//                                .addHeader("Content-Type", "application/json")
//                                .addHeader("shop-token", token)
//                                .method(orginal.method(),orginal.body())
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
//                Login login = new Login();
//                login.user = user;
//                login.pass = newText;
//                Call<ResponseObjectListToken> call = retrofitService.getObjectListBySearchForuUser(login);
//                call.enqueue(new Callback<ResponseObjectListToken>() {
//                    @Override
//                    public void onResponse(Call<ResponseObjectListToken> call, retrofit2.Response<ResponseObjectListToken> response) {
//                        progBarInVisible();
//                        if (response.isSuccessful()) {
//                            SharedPreferences.Editor editor=preferences.edit();
//                            editor.putString("token",response.body().token);
//                            editor.apply();
//                            objectList = response.body().objectList;
//                            showList();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseObjectListToken> call, Throwable t) {
//                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
//                        progBarInVisible();
//                    }
//                });
//                return false;
//            }
//        });
        app = new App(getContext());

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
        adapter = new ObjectListForUserAdapterAgree(getContext(), getActivity(), objectList, new ObjectListForUserAdapterAgree.OnItemClickListener() {
            @Override
            public void onItemClick(ObjectInfo objectInfo) {
                progBarVisible();
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
                OkHttpClient client = clientBuilder.build();
                Retrofit retrofit=new Retrofit.Builder()
                        .baseUrl(App.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .build();
                RetrofitService retrofitService=retrofit.create(RetrofitService.class);
                RequestLoginObject requestLoginObject=new RequestLoginObject();
                Login login=new Login();
                login.user=user;
                requestLoginObject.login=login;
                requestLoginObject.objectInfo=objectInfo;
                Call<ResponseMessageToken> call = retrofitService.deleteObjectByAdmin(requestLoginObject);
                call.enqueue(new Callback<ResponseMessageToken>() {
                    @Override
                    public void onResponse(Call<ResponseMessageToken> call, retrofit2.Response<ResponseMessageToken> response) {
                        progBarInVisible();
                        if (response.isSuccessful()){
                            SharedPreferences.Editor editor=App.prefUserInfo.edit();
                            editor.putString("token",response.body().token);
                            editor.apply();
                            Toast.makeText(getContext(), response.body().message, Toast.LENGTH_SHORT).show();
                            getObjectList();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseMessageToken> call, Throwable t) {
                        progBarInVisible();
                        Toast.makeText(getContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
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
        Call<ResponseObjectListToken> call = retrofitService.getObjectListForUserAgree(login);
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
