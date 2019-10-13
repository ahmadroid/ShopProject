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

public class FragmentShopList extends Fragment {

    private RecyclerView recyclerView;
    private ShopListAdapter adapter;
    private SearchView srchShop;
    private ProgressBar progBar;
    private MyItemDecoration itemDecoration;
    private List<Shop> shopList;
//    private SharedPreferences preferences;
    private String token="",user="";
    private RelativeLayout relLayout;
    private TextView txtEmpty;



    public static FragmentShopList newInstance(){
        FragmentShopList fragmentShopList=new FragmentShopList();
        Bundle arg=new Bundle();
        fragmentShopList.setArguments(arg);
        return fragmentShopList;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(App.TAG_FRAGMENT,"CFShopList");
        Bundle arguments = getArguments();
//        preferences=getContext().getSharedPreferences("userInfoPre",Context.MODE_PRIVATE);

        itemDecoration=new MyItemDecoration(getContext(),LinearLayoutManager.VERTICAL);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(App.TAG_FRAGMENT,"CVFShopList");
        if (App.prefUserInfo!=null) {
            token = App.prefUserInfo.getString("token", "");
            user = App.prefUserInfo.getString("user", "");
        }
        View view=inflater.inflate(R.layout.layout_shop_list,container,false);
        recyclerView=view.findViewById(R.id.recycler_shop_list);
        srchShop=view.findViewById(R.id.srch_view_shop_list);
        relLayout=view.findViewById(R.id.rel_layout_shop_list);
        txtEmpty=view.findViewById(R.id.txt_empty_shop_list);
        srchShop.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
//                progBar.setVisibility(View.VISIBLE);
                OkHttpClient.Builder clientBuilder=new OkHttpClient.Builder();
                clientBuilder.addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request orginal = chain.request();
                        Request request=orginal.newBuilder()
                                .addHeader("Content-Type","application/json")
                                .addHeader("shop-token",token)
                                .method(orginal.method(),orginal.body())
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
                login.pass=s;
                Call<ResponseShopListToken> call = retrofitService.getShopListBySearch(login);
                call.enqueue(new Callback<ResponseShopListToken>() {
                    @Override
                    public void onResponse(Call<ResponseShopListToken> call, retrofit2.Response<ResponseShopListToken> response) {
                        progBarInVisible();
                        if (response.isSuccessful()){
                            SharedPreferences.Editor editor=App.prefUserInfo.edit();
                            editor.putString("token",response.body().token);
                            editor.apply();
                            shopList=response.body().shopList;
                            showList();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseShopListToken> call, Throwable t) {
                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
                        progBarInVisible();
                    }
                });
                return false;
            }
        });
        progBar=view.findViewById(R.id.prog_shop_list);
        progBarVisible();
//        if (!App.IS_SHOPLIST) {
//            App.IS_SHOPLIST=true;
//            getShopList();
//        }
        getShopList();
        return view;
    }

    public void getShopList() {
//        if (!App.IS_SHOPLIST) {
//            return;
//        }
//        progBar.setVisibility(View.VISIBLE);
        shopList=new ArrayList<>();
        OkHttpClient.Builder clientbuilder=new OkHttpClient.Builder();
        clientbuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request orginal = chain.request();
                Request request=orginal.newBuilder()
                        .addHeader("Content-Type","application/json")
                        .addHeader("shop-token",token)
                        .method(orginal.method(),orginal.body())
                        .build();
                return chain.proceed(request);
            }
        });
        OkHttpClient client=clientbuilder.build();
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(App.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        RetrofitService retrofitService=retrofit.create(RetrofitService.class);
        final Login login=new Login();
        login.user=user;
        Call<ResponseShopListToken> call = retrofitService.getShopList(login);
        call.enqueue(new Callback<ResponseShopListToken>() {
            @Override
            public void onResponse(Call<ResponseShopListToken> call, retrofit2.Response<ResponseShopListToken> response) {
                progBarInVisible();
                if (response.isSuccessful()){
                    SharedPreferences.Editor editor=App.prefUserInfo.edit();
                    editor.putString("token",response.body().token);
                    editor.apply();
                    shopList=response.body().shopList;
                    if (shopList.size()==0){
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
            public void onFailure(Call<ResponseShopListToken> call, Throwable t) {
//                Toast.makeText(getContext(), getContext().getResources().getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                progBarInVisible();
            }
        });
    }

    public void showList(){
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        adapter=new ShopListAdapter(getActivity(), getContext(), shopList, new ShopListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Shop shop) {
                Intent intent=new Intent(getContext(),ShowShopInfo.class);
                intent.putExtra("shop",shop);
                getContext().startActivity(intent);
            }
        });
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
