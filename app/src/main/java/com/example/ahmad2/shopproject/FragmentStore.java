package com.example.ahmad2.shopproject;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
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

public class FragmentStore extends Fragment {

    private RecyclerView recyclerView;
    private SearchView srchView;
//    private SharedPreferences preferences;
    private String user="", token="";
    private List<ObjectInfo> objectList;
    private StoreAdapter adapter;
    private ProgressBar progBar;
    private RelativeLayout relLayout;
    private TextView txtEmpty;

    public FragmentStore newInstance() {
        FragmentStore fragmentStore = new FragmentStore();
        Bundle arg = new Bundle();
        setArguments(arg);
        return fragmentStore;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(App.TAG_FRAGMENT,"CFStore");
//        preferences = getContext().getSharedPreferences("userInfoPre", Context.MODE_PRIVATE);

        objectList = new ArrayList<>();
//        getObjectListOnCreate();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(App.TAG_FRAGMENT,"CVFStore");
        if (App.prefUserInfo != null) {
            user = App.prefUserInfo.getString("user", "");
            token = App.prefUserInfo.getString("token", "");
        }
        View view = inflater.inflate(R.layout.layout_fragment_store, container, false);
        recyclerView = view.findViewById(R.id.recycler_objectList_store);
        relLayout=view.findViewById(R.id.rel_layout_store);
        txtEmpty=view.findViewById(R.id.txt_empty_store);
        srchView = view.findViewById(R.id.srch_view_store);
        progBar = view.findViewById(R.id.prog_store);
        progBarVisible();
//        if (! App.IS_OBJECTLIST_STORE) {
//            App.IS_OBJECTLIST_STORE=true;
//            getObjectList();
//        }
        getObjectList();
//        else{
//            showList();
//        }
        srchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
//                progBar.setVisibility(View.VISIBLE);
                OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
                clientBuilder.addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .addHeader("Content-Type", "application/json")
                                .addHeader("shop-token", token)
                                .method(original.method(), original.body())
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
                login.pass = s;
                Call<ResponseObjectListToken> call = retrofitService.getObjectListForStoreBySearch(login);
                call.enqueue(new Callback<ResponseObjectListToken>() {
                    @Override
                    public void onResponse(Call<ResponseObjectListToken> call, retrofit2.Response<ResponseObjectListToken> response) {
                        progBarInVisible();
                        if (response.isSuccessful()) {
                            SharedPreferences.Editor editor = App.prefUserInfo.edit();
                            editor.putString("token", token);
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
                return false;
            }
        });
//        showList();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("TagFragment","VCStore");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("TagFragment","DestroyStore");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("TagFragmentdes","DestroyViewStore");
    }

    public void getObjectListOnCreate(){
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
        Login login = new Login();
        login.user = user;
        Call<ResponseObjectListToken> call = retrofitService.getObjectListForStore(login);
        call.enqueue(new Callback<ResponseObjectListToken>() {
            @Override
            public void onResponse(Call<ResponseObjectListToken> call, retrofit2.Response<ResponseObjectListToken> response) {
                Log.i("tagGetobject","writeObjectToContentValue");
                if (response.isSuccessful()){
                    SharedPreferences.Editor editor=App.prefUserInfo.edit();
                    editor.putString("token",response.body().token);
                    editor.apply();
                }
                objectList=response.body().objectList;
            }

            @Override
            public void onFailure(Call<ResponseObjectListToken> call, Throwable t) {

            }
        });

    }

    public void getObjectList() {
//        if (! App.IS_OBJECTLIST_STORE) {
//            return;
//        }
//        progBar.setVisibility(View.VISIBLE);
        Log.i("taguserpref",user);
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .addHeader("Content-Type", "application/json")
                        .addHeader("shop-token", token)
                        .method(original.method(), original.body())
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
        Call<ResponseObjectListToken> call = retrofitService.getObjectListForStore(login);
        call.enqueue(new Callback<ResponseObjectListToken>() {
            @Override
            public void onResponse(Call<ResponseObjectListToken> call, retrofit2.Response<ResponseObjectListToken> response) {
                progBarInVisible();
                if (response.isSuccessful()) {
                    SharedPreferences.Editor editor = App.prefUserInfo.edit();
                    editor.putString("token", response.body().token);
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
                progBarInVisible();
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showList() {
        recyclerView.addItemDecoration(new MyItemDecoration(getContext(), LinearLayout.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new StoreAdapter(getContext(), objectList, new StoreAdapter.OnItemClickListener() {

            @Override
            public void onItemInsertClick(ObjectInfo objectInfo) {
                insertToSell(objectInfo);
            }

            @Override
            public void onItemEditClick(ObjectInfo objectInfo) {
                Intent intent = new Intent(getContext(), ObjectInformation.class);
                intent.putExtra("objectInfo", objectInfo);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        progBarInVisible();
    }

    private void insertToSell(ObjectInfo objectInfo) {
        progBarVisible();
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .addHeader("Content-Type", "application/json")
                        .addHeader("shop-token", token)
                        .method(original.method(), original.body())
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
        login.pass = String.valueOf(objectInfo.getCodeObject());
        Call<ResponseMessageToken> call = retrofitService.insertObjectToSell(login);
        call.enqueue(new Callback<ResponseMessageToken>() {
            @Override
            public void onResponse(Call<ResponseMessageToken> call, retrofit2.Response<ResponseMessageToken> response) {
                progBarInVisible();
                if (response.isSuccessful()) {
                    SharedPreferences.Editor editor = App.prefUserInfo.edit();
                    editor.putString("token", response.body().token);
                    editor.apply();
                    Toast.makeText(getContext(), response.body().message, Toast.LENGTH_SHORT).show();
                    App.fragmentObjectListForAdmin.getObjectList();
                }
            }

            @Override
            public void onFailure(Call<ResponseMessageToken> call, Throwable t) {
                progBarInVisible();
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
