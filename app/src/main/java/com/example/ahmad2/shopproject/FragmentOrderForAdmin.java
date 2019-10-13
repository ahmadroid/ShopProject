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
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentOrderForAdmin extends Fragment {

    private RecyclerView recyclerView;
    private List<Order> orderList;
    private OrderListAdminAdapter adapter;
    private ProgressBar progBar;
    private App app;
    private SearchView srchView;
    private String token="";
    private String user="";
//    private SharedPreferences preferences;
    private TextView txtEmpty;
    private RelativeLayout relLayout;

    public static FragmentOrderForAdmin newInstance(String user) {
        FragmentOrderForAdmin fragmentOrderForAdmin = new FragmentOrderForAdmin();
        Bundle arg = new Bundle();
        arg.putString("user", user);
        fragmentOrderForAdmin.setArguments(arg);
        return fragmentOrderForAdmin;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(App.TAG_FRAGMENT,"CFOrderForAdmin");
//        showList();
//        preferences=getContext().getSharedPreferences("userInfoPre",Context.MODE_PRIVATE);

        app = new App(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(App.TAG_FRAGMENT,"CVFOrderForAdmin");
        if (App.prefUserInfo!=null) {
            token = App.prefUserInfo.getString("token", "");
            user = App.prefUserInfo.getString("user", "");
        }
        View view = inflater.inflate(R.layout.layout_order_list_for_admin, container, false);
        recyclerView = view.findViewById(R.id.recycler_order_list_for_admin);
        relLayout=view.findViewById(R.id.rel_layout_order_list_for_admin);
        txtEmpty=view.findViewById(R.id.txt_empty_order_list_for_admin);
        progBar = view.findViewById(R.id.prog_order_list_for_admin);
        srchView = view.findViewById(R.id.srch_view_order_list_for_admin);
        srchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String s) {
//                if (s.equals("")) {
//                    getOrderList();
//                    return false;
//                }
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
                RequestUserEditForAdmin requestUserEditForAdmin=new RequestUserEditForAdmin();
                requestUserEditForAdmin.user=user;
                requestUserEditForAdmin.userSel=s;
                Call<ResponseOrderListToken> call = retrofitService.getOrderListForAdminBySearch(requestUserEditForAdmin);
                call.enqueue(new Callback<ResponseOrderListToken>() {
                    @Override
                    public void onResponse(Call<ResponseOrderListToken> call, retrofit2.Response<ResponseOrderListToken> response) {
                        progBarInVisible();
                        if (response.isSuccessful()) {
                            SharedPreferences.Editor editor = App.prefUserInfo.edit();
                            editor.putString("token", response.body().token);
                            editor.apply();
                            orderList = response.body().orderList;
                            showList();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseOrderListToken> call, Throwable t) {
                        progBarInVisible();
                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
                    }
                });






//                StringRequest srchRequest = new StringRequest(Request.Method.POST, App.SEARCH_ORDER_USER_FOR_ADMIN_URI,
//                        new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response) {
//                                progBar.setVisibility(View.INVISIBLE);
//                                orderList = JsonParserForOrder.getOrderList(response);
//                                showList();
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
//                        params.put("name", s);
//                        return params;
//                    }
//                };
//                app.getRequestQueue().add(srchRequest);
                return false;
            }
        });
        progBarVisible();
//        if (!App.IS_ORDERLIST_ADMIN) {
//            App.IS_ORDERLIST_ADMIN=true;
//            getOrderList();
//        }
        getOrderList();
        return view;
    }

    public void getOrderList() {
//        if (!App.IS_ORDERLIST_ADMIN){
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
        Call<ResponseOrderListToken> call = retrofitService.getOrderListForAdmin(login);
        call.enqueue(new Callback<ResponseOrderListToken>() {
            @Override
            public void onResponse(Call<ResponseOrderListToken> call, retrofit2.Response<ResponseOrderListToken> response) {
                progBarInVisible();
                if (response.isSuccessful()) {
                    SharedPreferences.Editor editor = App.prefUserInfo.edit();
                    editor.putString("token", response.body().token);
                    editor.apply();
                    orderList = response.body().orderList;
                    if (orderList.size()==0){
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
            public void onFailure(Call<ResponseOrderListToken> call, Throwable t) {
                progBarInVisible();
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
            }
        });


//        StringRequest orderRequest = new StringRequest(Request.Method.POST, App.READ_ORDER_LIST_FOR_ADMIN_URI,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        progBar.setVisibility(View.INVISIBLE);
//                        orderList = JsonParserForOrder.getOrderList(response);
//                        showList();
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                progBar.setVisibility(View.INVISIBLE);
//                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//        app.getRequestQueue().add(orderRequest);
    }

    private void showList() {
        adapter = new OrderListAdminAdapter(getContext(), orderList, new OrderListAdminAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final Order order) {
                Intent intent = new Intent(getContext(), OrderPageForAdmin.class);
                intent.putExtra("order", order);
                startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new MyItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
    }

    public SearchView getSrchView() {
        return srchView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        Toast.makeText(getContext(), "onviewcreate in fragmentorderforadmin", Toast.LENGTH_SHORT).show();
    }

    public OrderListAdminAdapter getAdapter() {
        return adapter;
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
