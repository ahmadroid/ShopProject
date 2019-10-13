package com.example.ahmad2.shopproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
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

public class FragmentSellSpecialForUser extends Fragment {

    private SellSpecialAdapter adapter;
    private List<Shop> shopList;
    private App app;
    private ProgressBar progBar;
    private SearchView srchView;
    private RecyclerView recyclerView;
//    private SharedPreferences preferences;
    private String user="";
    private String token="";
    private NestedScrollView nstScrl;
    private RelativeLayout relLayout;
    private TextView txtEmpty;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

    }

    private void init() {
//        preferences = getContext().getSharedPreferences("userInfoPre", Context.MODE_PRIVATE);

        app = new App(getContext());
        if (shopList == null) {
            shopList = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (App.prefUserInfo != null) {
            token = App.prefUserInfo.getString("token", "");
            user = App.prefUserInfo.getString("user", "");
        }
        View view = inflater.inflate(R.layout.layout_fragment_sell_special, container, false);
        recyclerView = view.findViewById(R.id.recycler_sell_special);
        progBar = view.findViewById(R.id.prog_sell_special);
        relLayout=view.findViewById(R.id.rel_layout_sell_special);
        txtEmpty=view.findViewById(R.id.txt_empty_sell_special);
        srchView = view.findViewById(R.id.srch_view_sell_special);
        srchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
//                progBar.setVisibility(View.VISIBLE);
//                if (newText.equals("")) {
//                    getObjectList();
//                    return false;
//                }

                return false;
            }
        });
        progBarVisible();
//        if (!App.IS_SHOPLIST_SELLSPECIAL){
//            App.IS_SHOPLIST_SELLSPECIAL=true;
//            getObjectList();
//        }
        getObjectList();
        return view;
    }

    public void getObjectList() {
//        if (!App.IS_SHOPLIST_SELLSPECIAL){
//            return;
//        }
//        progBar.setVisibility(View.VISIBLE);
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
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
        login.pass="";
        Call<ResponseShopListToken> call = retrofitService.getSellSpecial(login);
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
                Toast.makeText(getContext(), getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
                progBarInVisible();
            }
        });
    }

    public void showList() {
        adapter = new SellSpecialAdapter(getContext(), shopList, new SellSpecialAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Shop shop) {
                Intent intent=new Intent(getContext(),ShowShopInfo.class);
                intent.putExtra("shop",shop);
                startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new MyItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
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
