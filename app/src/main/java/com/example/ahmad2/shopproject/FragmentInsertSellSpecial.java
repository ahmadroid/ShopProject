package com.example.ahmad2.shopproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

public class FragmentInsertSellSpecial extends Fragment implements View.OnClickListener {

    private String user="",token="";
//    private SharedPreferences preferences;
    private EditText edtTitle,edtText;
    private ProgressBar progBar;
    private Button btnApply,btnEdit,btnDelete;
    private LinearLayout linLayout,linLayout1;
    private TextView txtWait,txtDes;
    private retrofit2.Response<ResponseMessageToken> responseOrginal;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        preferences=getContext().getSharedPreferences("userInfoPre",Context.MODE_PRIVATE);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (App.prefUserInfo!=null){
            user=App.prefUserInfo.getString("user","");
            token=App.prefUserInfo.getString("token","");
        }
        View view=inflater.inflate(R.layout.layout_fragment_insert_sell_special,container,false);
        edtText=view.findViewById(R.id.edt_text_insert_sell_special);
        edtTitle=view.findViewById(R.id.edt_title_insert_sell_special);
        btnApply=view.findViewById(R.id.btn_insert_sell_special);
        btnDelete=view.findViewById(R.id.btn_delete_insert_sell_special);
        btnEdit=view.findViewById(R.id.btn_edit_insert_sell_special);
        progBar=view.findViewById(R.id.prog_insert_sell_special);
        linLayout=view.findViewById(R.id.lin_layout_insert_sell_special);
        linLayout1=view.findViewById(R.id.lin1_layout_insert_sell_special);
        txtWait=view.findViewById(R.id.txt_wait_agree_insert_sell_special);
        txtDes=view.findViewById(R.id.txt_description_insert_sell_special);
        btnApply.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        progBarVisible();
//        if (!App.IS_INSERTSELL) {
//            App.IS_INSERTSELL=true;
//            getSellSpecial();
//        }
        getSellSpecial();
        return view;
    }

    public void getSellSpecial() {
//        if (!App.IS_INSERTSELL){
//            return;
//        }
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
        login.pass=user;
        Call<ResponseShopListToken> call = retrofitService.getSellSpecial(login);
        call.enqueue(new Callback<ResponseShopListToken>() {
            @Override
            public void onResponse(Call<ResponseShopListToken> call, retrofit2.Response<ResponseShopListToken> response) {
                progBarInVisible();
                if (response.isSuccessful()){
                    SharedPreferences.Editor editor=App.prefUserInfo.edit();
                    editor.putString("token",response.body().token);
                    editor.apply();
                    List<Shop> shopList = new ArrayList<>();
                    shopList=response.body().shopList;
                    if (shopList.size()>0) {
                        Shop shop = shopList.get(0);
                        edtText.setText(shop.getSellSpecial());
                        edtTitle.setText(shop.getTitle());
                        if (shop.getIsSellAgree()==0){
                            linLayout.setVisibility(View.INVISIBLE);
                            txtWait.setVisibility(View.VISIBLE);
                            txtWait.setText(getString(R.string.txt_wait_agree_sellSpecial));
                        }else{
                            linLayout.setVisibility(View.VISIBLE);
                            txtWait.setVisibility(View.INVISIBLE);
                            linLayout1.setVisibility(View.VISIBLE);
                            btnApply.setVisibility(View.GONE);
                            txtDes.setText(getString(R.string.txt_edit_sell_special_for_admin));

                        }
                    }else{
                        txtDes.setText(getString(R.string.txt_sell_special_for_admin));
                        btnApply.setText(getString(R.string.btn_confirm));
                        linLayout.setVisibility(View.VISIBLE);
                        txtWait.setVisibility(View.INVISIBLE);
                        btnApply.setVisibility(View.VISIBLE);
                        linLayout1.setVisibility(View.GONE);
                        edtText.setText("");
                        edtTitle.setText("");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseShopListToken> call, Throwable t) {
                progBarInVisible();
                Toast.makeText(getContext(), getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.equals(btnApply)) {
            if (edtTitle.getText().toString().trim().isEmpty()){
                edtTitle.setError(getString(R.string.warrning_not_empty));
                return;
            }else if (edtText.getText().toString().trim().isEmpty()){
                edtText.setError(getString(R.string.warrning_not_empty));
                return;
            }
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
            OkHttpClient client=clientBuilder.build();
            Retrofit retrofit=new Retrofit.Builder()
                    .baseUrl(App.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
            RetrofitService retrofitService=retrofit.create(RetrofitService.class);
            RequestShopApiKey requestShopApiKey=new RequestShopApiKey();
            Shop shop=new Shop();
            shop.setUser(user);
            shop.setTitle(edtTitle.getText().toString().trim());
            shop.setSellSpecial(edtText.getText().toString().trim());
            requestShopApiKey.shop=shop;
            Call<ResponseMessageToken> call = retrofitService.insertSellSpecial(requestShopApiKey);
            call.enqueue(new Callback<ResponseMessageToken>() {
                @Override
                public void onResponse(Call<ResponseMessageToken> call, retrofit2.Response<ResponseMessageToken> response) {

                    progBarInVisible();
                    if (response.isSuccessful()){
                        SharedPreferences.Editor editor=App.prefUserInfo.edit();
                        editor.putString("token",response.body().token);
                        editor.apply();
                        Toast.makeText(getContext(), getString(R.string.mes_insert_sell_special), Toast.LENGTH_SHORT).show();
                        linLayout.setVisibility(View.INVISIBLE);
                        txtWait.setVisibility(View.VISIBLE);
                        txtWait.setText(getString(R.string.txt_wait_agree_sellSpecial));
                    }
                }

                @Override
                public void onFailure(Call<ResponseMessageToken> call, Throwable t) {
                    progBarInVisible();
                    Toast.makeText(getContext(), getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
                }
            });
        }else if (view.equals(btnEdit)){
            if (edtTitle.getText().toString().trim().isEmpty()){
                edtTitle.setError(getString(R.string.warrning_not_empty));
                return;
            }else if (edtText.getText().toString().trim().isEmpty()){
                edtText.setError(getString(R.string.warrning_not_empty));
                return;
            }
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
            OkHttpClient client=clientBuilder.build();
            Retrofit retrofit=new Retrofit.Builder()
                    .baseUrl(App.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
            RetrofitService retrofitService=retrofit.create(RetrofitService.class);
            RequestShopApiKey requestShopApiKey=new RequestShopApiKey();
            Shop shop=new Shop();
            shop.setUser(user);
            shop.setTitle(edtTitle.getText().toString().trim());
            shop.setSellSpecial(edtText.getText().toString().trim());
            requestShopApiKey.shop=shop;
            Call<ResponseMessageToken> call = retrofitService.insertSellSpecial(requestShopApiKey);
            call.enqueue(new Callback<ResponseMessageToken>() {
                @Override
                public void onResponse(Call<ResponseMessageToken> call, retrofit2.Response<ResponseMessageToken> response) {

                    progBarInVisible();
                    if (response.isSuccessful()){
                        SharedPreferences.Editor editor=App.prefUserInfo.edit();
                        editor.putString("token",response.body().token);
                        editor.apply();
                        Toast.makeText(getContext(), getString(R.string.mes_edit_sell_special), Toast.LENGTH_SHORT).show();
                        linLayout.setVisibility(View.INVISIBLE);
                        txtWait.setVisibility(View.VISIBLE);
                        txtWait.setText(getString(R.string.txt_wait_agree_sellSpecial));
                    }
                }

                @Override
                public void onFailure(Call<ResponseMessageToken> call, Throwable t) {
                    progBarInVisible();
                    Toast.makeText(getContext(), getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
                }
            });
        }else if (view.equals(btnDelete)){
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
            OkHttpClient client=clientBuilder.build();
            Retrofit retrofit=new Retrofit.Builder()
                    .baseUrl(App.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
            RetrofitService retrofitService=retrofit.create(RetrofitService.class);
            RequestShopApiKey requestShopApiKey=new RequestShopApiKey();
            Shop shop=new Shop();
            shop.setUser(user);
            shop.setTitle("");
            shop.setSellSpecial("");
            requestShopApiKey.shop=shop;
            Call<ResponseMessageToken> call = retrofitService.insertSellSpecial(requestShopApiKey);
            call.enqueue(new Callback<ResponseMessageToken>() {
                @Override
                public void onResponse(Call<ResponseMessageToken> call, retrofit2.Response<ResponseMessageToken> response) {

                    progBarInVisible();
                    if (response.isSuccessful()){
                        SharedPreferences.Editor editor=App.prefUserInfo.edit();
                        editor.putString("token",response.body().token);
                        editor.apply();
                        Toast.makeText(getContext(), getString(R.string.mes_delete_sell_special), Toast.LENGTH_SHORT).show();
                        linLayout.setVisibility(View.VISIBLE);
                        txtDes.setText(getString(R.string.txt_sell_special_for_admin));
                        btnApply.setVisibility(View.VISIBLE);
                        linLayout1.setVisibility(View.GONE);
                        txtWait.setVisibility(View.INVISIBLE);
                        edtText.setText("");
                        edtTitle.setText("");
                    }
                }

                @Override
                public void onFailure(Call<ResponseMessageToken> call, Throwable t) {
                    progBarInVisible();
                    Toast.makeText(getContext(), getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private retrofit2.Response<ResponseMessageToken> getResponse() {
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
        OkHttpClient client=clientBuilder.build();
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(App.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        RetrofitService retrofitService=retrofit.create(RetrofitService.class);
        RequestShopApiKey requestShopApiKey=new RequestShopApiKey();
        Shop shop=new Shop();
        shop.setUser(user);
        shop.setTitle(edtTitle.getText().toString().trim());
        shop.setSellSpecial(edtText.getText().toString().trim());
        requestShopApiKey.shop=shop;
        Call<ResponseMessageToken> call = retrofitService.insertSellSpecial(requestShopApiKey);
        call.enqueue(new Callback<ResponseMessageToken>() {
            @Override
            public void onResponse(Call<ResponseMessageToken> call, retrofit2.Response<ResponseMessageToken> response) {

                progBarInVisible();
                if (response.isSuccessful()){
                    SharedPreferences.Editor editor=App.prefUserInfo.edit();
                    editor.putString("token",response.body().token);
                    editor.apply();
                    Toast.makeText(getContext(), response.body().message, Toast.LENGTH_SHORT).show();
                    responseOrginal =response;
                }
            }

            @Override
            public void onFailure(Call<ResponseMessageToken> call, Throwable t) {
                progBarInVisible();
                Toast.makeText(getContext(), getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
            }
        });
        return responseOrginal;
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
