package com.example.ahmad2.shopproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderPageForAdmin extends MyActivity implements View.OnClickListener {

    private TextView txtName, txtUser, txtAmount, txtUnit, txtDes,txtMobile;
    private Button btnSeen, btnDelete;
    private ProgressBar progBarOrder;
    private Order order;
    private App app;
    private SharedPreferences preferences;
    private String token;
    private String user;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_page_for_admin);
        preferences=getSharedPreferences("userInfoPre",MODE_PRIVATE);
        if (preferences!=null) {
            token = preferences.getString("token", "");
            user = preferences.getString("user", "");
        }
        setView();
        app = new App(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("order"))
            order = extras.getParcelable("order");
        txtAmount.setText(String.valueOf(order.getAmount()));
        txtDes.setText(order.getDescription());
        txtName.setText(order.getName());
        txtUser.setText(order.getUserShop());
        txtUnit.setText(order.getUnit());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onClick(View view) {
        if (view.equals(btnSeen)) {
            progBarOrder.setVisibility(View.VISIBLE);
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
            RequestOrder requestOrder = new RequestOrder();
            Order order1 = new Order();
            order1.setId(order.getId());
            order1.setUser(user);
            requestOrder.order = order1;
            Call<ResponseMessageToken> call = retrofitService.updateSeen(requestOrder);
            call.enqueue(new Callback<ResponseMessageToken>() {
                @Override
                public void onResponse(Call<ResponseMessageToken> call, retrofit2.Response<ResponseMessageToken> response) {
                    progBarOrder.setVisibility(View.INVISIBLE);
                    if (response.isSuccessful()) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("token", response.body().token);
                        editor.apply();
//                        App.fragmentOrderForAdmin.getAdapter().notifyDataSetChanged();
                        App.fragmentOrderForAdmin.getOrderList();
                        Toast.makeText(OrderPageForAdmin.this, response.body().message, Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<ResponseMessageToken> call, Throwable t) {
                    progBarOrder.setVisibility(View.INVISIBLE);
                    Toast.makeText(OrderPageForAdmin.this, getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
                }
            });











//            StringRequest seenRequest = new StringRequest(Request.Method.POST, App.UPDATE_SEEN_URI,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            progBarOrder.setVisibility(View.INVISIBLE);
//                            Toast.makeText(OrderPageForAdmin.this, response, Toast.LENGTH_SHORT).show();
//                            App.fragmentOrderForAdmin.getOrderList();
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    progBarOrder.setVisibility(View.INVISIBLE);
//                    Toast.makeText(OrderPageForAdmin.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//
//                }
//            }) {
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String, String> params = new HashMap<>();
//                    params.put("id", String.valueOf(order.getId()));
//                    params.put("seen", "1");
//                    return params;
//                }
//            };
//            app.getRequestQueue().add(seenRequest);
        } else if (view.equals(btnDelete)) {
            progBarOrder.setVisibility(View.VISIBLE);
            final AlertDialog.Builder builder = new AlertDialog.Builder(OrderPageForAdmin.this);
            builder.setCancelable(false).setTitle(OrderPageForAdmin.this.getResources().getString(R.string.title_order_delete))
                    .setMessage(OrderPageForAdmin.this.getResources().getString(R.string.warrning_order_delete))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progBarOrder.setVisibility(View.VISIBLE);
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
                            RequestOrder requestOrder = new RequestOrder();
                            Order order1 = new Order();
                            order1.setId(order.getId());
                            order1.setUser(user);
                            requestOrder.order = order1;
                            Call<ResponseMessageToken> call = retrofitService.deleteOrder(requestOrder);
                            call.enqueue(new Callback<ResponseMessageToken>() {
                                @Override
                                public void onResponse(Call<ResponseMessageToken> call, retrofit2.Response<ResponseMessageToken> response) {
                                    progBarOrder.setVisibility(View.INVISIBLE);
                                    if (response.isSuccessful()) {
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("token", response.body().token);
                                        editor.apply();
//                                        App.fragmentOrderForAdmin.getSrchView().setQuery("",false);
                                        App.fragmentOrderForAdmin.getOrderList();
                                        finish();
                                        Toast.makeText(OrderPageForAdmin.this, response.body().message, Toast.LENGTH_SHORT).show();
                                    }

                                }

                                @Override
                                public void onFailure(Call<ResponseMessageToken> call, Throwable t) {
                                    progBarOrder.setVisibility(View.INVISIBLE);
                                    Toast.makeText(OrderPageForAdmin.this, getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
                                }
                            });


//                            StringRequest deleteRequest=new StringRequest(Request.Method.POST, App.DELETE_ORDER_URI,
//                                    new Response.Listener<String>() {
//                                        @Override
//                                        public void onResponse(String response) {
//                                            progBarOrder.setVisibility(View.INVISIBLE);
//                                            Toast.makeText(OrderPageForAdmin.this, response, Toast.LENGTH_SHORT).show();
//                                            App.fragmentOrderForAdmin.getSrchView().setQuery("",false);
////                                                        getOrderList();
//                                        }
//                                    }, new Response.ErrorListener() {
//                                @Override
//                                public void onErrorResponse(VolleyError error) {
//                                    progBarOrder.setVisibility(View.INVISIBLE);
//                                    Toast.makeText(OrderPageForAdmin.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            }){
//                                @Override
//                                protected Map<String, String> getParams() throws AuthFailureError {
//                                    Map<String ,String> params=new HashMap<>();
//                                    params.put("id",String.valueOf(order.getId()));
//                                    return params;
//                                }
//                            };
//                            app.getRequestQueue().add(deleteRequest);
                        }
                    }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private void setView() {
        txtAmount = findViewById(R.id.txt_amount_order_admin);
        txtName = findViewById(R.id.txt_name_order_admin);
        txtUser = findViewById(R.id.txt_user_order_admin);
        txtDes = findViewById(R.id.txt_description_order_admin);
        txtUnit = findViewById(R.id.txt_unit_order_admin);
        btnSeen = findViewById(R.id.btn_seen_order_admin);
        btnSeen.setOnClickListener(this);
        btnDelete = findViewById(R.id.btn_delete_order_admin);
        btnDelete.setOnClickListener(this);
        progBarOrder = findViewById(R.id.prog_order_admin);
    }
}
