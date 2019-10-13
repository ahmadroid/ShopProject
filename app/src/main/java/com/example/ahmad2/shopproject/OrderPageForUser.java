package com.example.ahmad2.shopproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderPageForUser extends MyActivity implements View.OnClickListener {

    private Order order;
    private Button btnDelete, btnEdit;
    private TextView txtName;
    private EditText edtAmount, edtDes;
    private Spinner spinUnit;
    private ProgressBar progBarOrder;
    private App app;
    private SharedPreferences preferences;
    private String token;
//    private String userName;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_page_for_user);
        preferences = getSharedPreferences("userInfoPre", MODE_PRIVATE);
        if (preferences!=null) {
            token = preferences.getString("token", "");
            user = preferences.getString("user", "");
//            userName = preferences.getString("name", "");
        }
        app = new App(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("order"))
            order = extras.getParcelable("order");
        setView();
        txtName.setText(order.getName());
        edtAmount.setText(String.valueOf(order.getAmount()));
        edtDes.setText(order.getDescription());
        txtName.setText(order.getName());
        List<String> unitList = new ArrayList<>();
        unitList = Arrays.asList(getResources().getStringArray(R.array.unit_array));
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, unitList);
        spinUnit.setAdapter(adapter);
    }

    private void setView() {
        btnDelete = findViewById(R.id.btn_delete_edit_order_user);
        btnDelete.setOnClickListener(this);
        btnEdit = findViewById(R.id.btn_edit_edit_order_user);
        btnEdit.setOnClickListener(this);
        txtName = findViewById(R.id.txt_name_edit_order_user);
        edtAmount = findViewById(R.id.edt_amount_edit_order_user);
        edtDes = findViewById(R.id.edt_description_edit_order_user);
        spinUnit = findViewById(R.id.spin_unit_edit_order_user);
        progBarOrder = findViewById(R.id.prog_edit_order_user);
    }

    public void onClick(View view) {
        if (view.equals(btnDelete)) {
            progBarOrder.setVisibility(View.VISIBLE);
            AlertDialog.Builder builder = new AlertDialog.Builder(OrderPageForUser.this);
            builder.setCancelable(false).setTitle(getString(R.string.title_order_delete))
                    .setMessage(getString(R.string.warrning_order_delete))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
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
                                        App.fragmentOrderForUser.getOrderList();
                                        finish();
                                        Toast.makeText(OrderPageForUser.this, response.body().message, Toast.LENGTH_SHORT).show();
                                    }

                                }

                                @Override
                                public void onFailure(Call<ResponseMessageToken> call, Throwable t) {
                                    progBarOrder.setVisibility(View.INVISIBLE);
                                    Toast.makeText(OrderPageForUser.this, getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();


//            StringRequest deleteRequest = new StringRequest(Request.Method.POST, App.DELETE_ORDER_URI,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            progBarOrder.setVisibility(View.INVISIBLE);
//                            Toast.makeText(OrderPageForUser.this, response, Toast.LENGTH_SHORT).show();
//                            App.fragmentOrderForUser.getOrderList();
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    progBarOrder.setVisibility(View.INVISIBLE);
//                    Toast.makeText(OrderPageForUser.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }) {
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String, String> params = new HashMap<>();
//                    params.put("id", String.valueOf(order.getId()));
//                    return params;
//                }
//            };
//            app.getRequestQueue().add(deleteRequest);
        } else if (view.equals(btnEdit)) {
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
            order1.setUserShop("username");
            String orderDate = ShamsiDate.getCurrentShamsidate();
            order1.setOrderDate(orderDate);
            order1.setUnit(spinUnit.getSelectedItem().toString());
            try {
                order1.setAmount(Integer.valueOf(edtAmount.getText().toString().trim()));
            }catch(Exception e){
                order1.setAmount(0);
            }
            order1.setDescription(edtDes.getText().toString().trim());
            requestOrder.order = order1;
            Call<ResponseMessageToken> call = retrofitService.updateOrder(requestOrder);
            call.enqueue(new Callback<ResponseMessageToken>() {
                @Override
                public void onResponse(Call<ResponseMessageToken> call, retrofit2.Response<ResponseMessageToken> response) {
                    progBarOrder.setVisibility(View.INVISIBLE);
                    if (response.isSuccessful()) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("token", response.body().token);
                        editor.apply();
                        App.fragmentOrderForUser.getOrderList();
                        Toast.makeText(OrderPageForUser.this, response.body().message, Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<ResponseMessageToken> call, Throwable t) {
                    progBarOrder.setVisibility(View.INVISIBLE);
                    Toast.makeText(OrderPageForUser.this, getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
                    Log.i("tagOrderUser",t.getMessage());
                }
            });


//            StringRequest editRequest=new StringRequest(Request.Method.POST, App.UPDATE_ORDER_URI,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            progBarOrder.setVisibility(View.INVISIBLE);
//                            Toast.makeText(OrderPageForUser.this,response,Toast.LENGTH_SHORT).show();
//                            App.fragmentOrderForUser.getOrderList();
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    progBarOrder.setVisibility(View.INVISIBLE);
//                    Toast.makeText(OrderPageForUser.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }){
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String ,String> params=new HashMap<>();
//                    params.put("id",String.valueOf(order.getId()));
//                    params.put("amount",edtAmount.getText().toString());
//                    params.put("description",edtDes.getText().toString());
//                    params.put("unit",spinUnit.getSelectedItem().toString());
//                    String orderDate = ShamsiDate.getCurrentShamsidate();
//                    params.put("orderdate",String.valueOf(orderDate));
//                    return params;
//                }
//            };
//            app.getRequestQueue().add(editRequest);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
