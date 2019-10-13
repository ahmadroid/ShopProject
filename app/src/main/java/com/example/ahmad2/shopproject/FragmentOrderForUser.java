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
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentOrderForUser extends Fragment {

    private RecyclerView recyclerView;
    private List<Order> orderList;
    private OrderListUserAdapter adapter;
    private ProgressBar progBar;
    private App app;
    private String user="";
    private String token="";
//    private SharedPreferences preferences;
    private RelativeLayout relLayout;
    private TextView txtEmpty;

    public static FragmentOrderForUser newInstance(String user) {
        FragmentOrderForUser fragmentOrderForUser = new FragmentOrderForUser();
        Bundle arg = new Bundle();
        arg.putString("user", user);
        fragmentOrderForUser.setArguments(arg);
        return fragmentOrderForUser;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(App.TAG_FRAGMENT, "CFOrderForUser");
//        preferences = getContext().getSharedPreferences("userInfoPre", Context.MODE_PRIVATE);

        app = new App(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(App.TAG_FRAGMENT, "CVFOrderForUser");
        if (App.prefUserInfo != null) {
            user = App.prefUserInfo.getString("user", "");
            token = App.prefUserInfo.getString("token", "");
        }
        View view = inflater.inflate(R.layout.layout_order_list_for_user, container, false);
        recyclerView = view.findViewById(R.id.recycler_order_list_for_user);
        txtEmpty = view.findViewById(R.id.txt_empty_order_list_for_user);
        relLayout = view.findViewById(R.id.rel_layout_order_list_for_user);

        progBar = view.findViewById(R.id.prog_order_list_for_user);
        progBarVisible();
//        if (!App.IS_ORDERLIST_USER) {
//            App.IS_ORDERLIST_USER = true;
//            getOrderList();
//        }
        getOrderList();
        return view;
    }

    public void getOrderList() {
//        if (!App.IS_ORDERLIST_USER) {
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
        Call<ResponseOrderListToken> call = retrofitService.getOrderListForUser(login);
        call.enqueue(new Callback<ResponseOrderListToken>() {
            @Override
            public void onResponse(Call<ResponseOrderListToken> call, retrofit2.Response<ResponseOrderListToken> response) {
                progBarInVisible();
                if (response.isSuccessful()) {
                    SharedPreferences.Editor editor = App.prefUserInfo.edit();
                    editor.putString("token", response.body().token);
                    editor.apply();
                    orderList = response.body().orderList;
                    if (orderList.size() == 0) {
                        relLayout.setVisibility(View.INVISIBLE);
                        txtEmpty.setVisibility(View.VISIBLE);
                    } else {
                        relLayout.setVisibility(View.VISIBLE);
                        txtEmpty.setVisibility(View.INVISIBLE);
                        showList();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseOrderListToken> call, Throwable t) {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
                progBarInVisible();
            }
        });
    }

    private void showList() {
        adapter = new OrderListUserAdapter(getContext(), orderList, new OrderListUserAdapter.OnItemClickListener() {
            @Override
            public void onItemclick(final Order order) {
                Intent intent = new Intent(getContext(), OrderPageForUser.class);
                intent.putExtra("order", order);
                startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new MyItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
    }

    private void changeSize(Window window) {
        Point size = new Point();
        if (getActivity() != null)
            getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        window.setLayout(size.x, ViewGroup.LayoutParams.WRAP_CONTENT);
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
