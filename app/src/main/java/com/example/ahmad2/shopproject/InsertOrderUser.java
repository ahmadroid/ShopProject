package com.example.ahmad2.shopproject;

import android.content.SharedPreferences;
import android.os.Bundle;
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

public class InsertOrderUser extends MyActivity {

    private Button btnInsert;
    private TextView txtName;
    private EditText edtAmount, edtDes;
    private Spinner spinUnit;
    private ProgressBar progBarOrder;
    private App app;
    private ObjectInfo objectInfo;
    private String user;
//    private String userName;
    private SharedPreferences preferences;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_order_user);
        preferences = getSharedPreferences("userInfoPre", MODE_PRIVATE);
        if (preferences!=null) {
            user = preferences.getString("user", "");
//            userName = preferences.getString("name", "");
            token = preferences.getString("token", "");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("object"))
                objectInfo = extras.getParcelable("object");
        }
        app = new App(this);
        setView();
        txtName.setText(objectInfo.getName());
        List<String> unitList = new ArrayList<>();
        unitList = Arrays.asList(getResources().getStringArray(R.array.unit_array));
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, unitList);
        spinUnit.setAdapter(adapter);
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtAmount.getText().toString().isEmpty()) {
                    edtAmount.setError(getString(R.string.warrning_amount_order));
                    edtAmount.requestFocus();
                    return;
                }
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
                Order order = new Order();
                order.setName(txtName.getText().toString().trim());
                order.setUser(user);
                order.setUserShop(objectInfo.getUser());
                String orderDate = ShamsiDate.getCurrentShamsidate();
                order.setOrderDate(orderDate);
                order.setUnit(spinUnit.getSelectedItem().toString());
                try {
                    order.setAmount(Integer.valueOf(edtAmount.getText().toString().trim()));
                }catch(Exception e){
                    order.setAmount(0);
                }
                order.setDescription(edtDes.getText().toString().trim());
                requestOrder.order = order;
                Call<ResponseMessageToken> call = retrofitService.insertOrder(requestOrder);
                call.enqueue(new Callback<ResponseMessageToken>() {
                    @Override
                    public void onResponse(Call<ResponseMessageToken> call, retrofit2.Response<ResponseMessageToken> response) {
                        progBarOrder.setVisibility(View.INVISIBLE);
                        if (response.isSuccessful()){
                            SharedPreferences.Editor editor=preferences.edit();
                            editor.putString("token",response.body().token);
                            editor.apply();
                            App.fragmentOrderForUser.getOrderList();
                            Toast.makeText(InsertOrderUser.this, response.body().message, Toast.LENGTH_SHORT).show();
                        }else{
//                            Toast.makeText(InsertOrderUser.this, "not successful", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseMessageToken> call, Throwable t) {
                        progBarOrder.setVisibility(View.INVISIBLE);
//                        Toast.makeText(InsertOrderUser.this, "error", Toast.LENGTH_SHORT).show();
                        Toast.makeText(InsertOrderUser.this, getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
                    }
                });


//                final StringRequest orderRequest = new StringRequest(Request.Method.POST, App.INSERT_ORDER_USER_URI,
//                        new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response) {
//                                progBarOrder.setVisibility(View.INVISIBLE);
//                                Toast.makeText(InsertOrderUser.this, response, Toast.LENGTH_SHORT).show();
//                                App.fragmentOrderForUser.getOrderList();
//                            }
//                        }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        progBarOrder.setVisibility(View.INVISIBLE);
//                        Toast.makeText(InsertOrderUser.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }) {
//                    @Override
//                    protected Map<String, String> getParams() throws AuthFailureError {
//                        Map<String, String> params = new HashMap<>();
//                        params.put("name", txtName.getText().toString().trim());
//                        params.put("user", InsertOrderUser.this.user);
//                        params.put("userName", userName);
////                        Date orderDate = new Date();
//                        String orderDate = ShamsiDate.getCurrentShamsidate();
//                        params.put("orderdate", String.valueOf(orderDate));
//                        params.put("unit", spinUnit.getSelectedItem().toString());
//                        params.put("amount", edtAmount.getText().toString().trim());
//                        params.put("description", edtDes.getText().toString().trim());
//                        return params;
//                    }
//                };
//                app.getRequestQueue().add(orderRequest);
            }
        });
    }

    private void setView() {
        btnInsert = findViewById(R.id.btn_insert_order_user);
        txtName = findViewById(R.id.txt_name_order_user);
        edtAmount = findViewById(R.id.edt_amount_order_user);
        edtDes = findViewById(R.id.edt_description_order_user);
        spinUnit = findViewById(R.id.spin_unit_order_user);
        progBarOrder = findViewById(R.id.prog_order_user);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
