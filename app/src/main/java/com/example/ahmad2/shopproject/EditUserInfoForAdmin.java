package com.example.ahmad2.shopproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

public class EditUserInfoForAdmin extends MyActivity implements View.OnClickListener {

    private TextView txtName, txtUserType, txtMobile, txtTel, txtAddress;
    private Button btnDelete, btnEdit;
    private CheckBox chkUserType;
    private ProgressBar progBar;
    private User user;
    private String myUser;
    private App app;
    private SharedPreferences preferences;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info_for_admin);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        app = new App(this);
        preferences = getSharedPreferences("userInfoPre", MODE_PRIVATE);
        if (preferences!=null) {
            token = preferences.getString("token", "");
            myUser = preferences.getString("user", "");
        }
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("user"))
            user = getIntent().getParcelableExtra("user");
        txtAddress = findViewById(R.id.txt_address_edit_userinfo_for_admin);
        txtMobile = findViewById(R.id.txt_mobile_edit_userinfo_for_adminr);
        txtName = findViewById(R.id.txt_name_edit_userinfo_for_admin);
        txtTel = findViewById(R.id.txt_tel_edit_userinfo_for_admin);
        txtUserType = findViewById(R.id.txt_userType_edit_userinfo_for_admin);
        btnDelete = findViewById(R.id.btn_delete_edit_userinfo_for_admin);
        btnDelete.setOnClickListener(this);
        btnEdit = findViewById(R.id.btn_edit_edit_userinfo_for_admin);
        btnEdit.setOnClickListener(this);
        progBar = findViewById(R.id.prog_edit_userinfo_for_admin);
        chkUserType = findViewById(R.id.chk_userType_edit_userinfo_for_adminr);
        if (user.getAdmin() == 1) {
            txtUserType.setText(getString(R.string.admin));
            chkUserType.setChecked(true);
        } else if (user.getAdmin() == 0) {
            txtUserType.setText(getString(R.string.user_sample));
            chkUserType.setChecked(false);
        }
        txtTel.setText(user.getTel());
        txtName.setText(user.getName());
        txtMobile.setText(user.getMobile());
        txtAddress.setText(user.getAddress());
        chkUserType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    txtUserType.setText(getString(R.string.admin));
                else
                    txtUserType.setText(getString(R.string.user_sample));
            }
        });
    }

    public void onClick(View view) {
        if (view.equals(btnDelete)) {
            progBar.setVisibility(View.VISIBLE);
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
            RequestUserEditForAdmin requestUserEditForAdmin = new RequestUserEditForAdmin();
            requestUserEditForAdmin.userSel = user.getUserName();
            requestUserEditForAdmin.user = myUser;
            Call<ResponseMessageToken> call = retrofitService.deleteUser(requestUserEditForAdmin);
            call.enqueue(new Callback<ResponseMessageToken>() {
                @Override
                public void onResponse(Call<ResponseMessageToken> call, retrofit2.Response<ResponseMessageToken> response) {

                    progBar.setVisibility(View.INVISIBLE);
                    if (response.isSuccessful()) {
                        Toast.makeText(EditUserInfoForAdmin.this, response.body().message, Toast.LENGTH_SHORT).show();
                        App.fragmentUserListForAdmin.getRegisterList();
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("token", response.body().token);
                        editor.apply();
                    }
                }

                @Override
                public void onFailure(Call<ResponseMessageToken> call, Throwable t) {
                    progBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(EditUserInfoForAdmin.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


//            StringRequest deleteRequest=new StringRequest(Request.Method.POST, App.DELETE_USER_FOR_ADMIN_URI,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            progBar.setVisibility(View.INVISIBLE);
//                            Toast.makeText(EditUserInfoForAdmin.this, response, Toast.LENGTH_SHORT).show();
//                            App.fragmentUserListForAdmin.getRegisterList();
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    progBar.setVisibility(View.INVISIBLE);
//                    Toast.makeText(EditUserInfoForAdmin.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }){
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String ,String> params=new HashMap<>();
//                    params.put("user",user.getUserShop());
//                    return params;
//                }
//            };
//            app.getRequestQueue().add(deleteRequest);
        } else if (view.equals(btnEdit)) {
            progBar.setVisibility(View.VISIBLE);
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
            RequestUserEditForAdmin requestUserEditForAdmin = new RequestUserEditForAdmin();
            requestUserEditForAdmin.userSel = user.getUserName();
            requestUserEditForAdmin.user = myUser;
            if (txtUserType.getText().toString().trim().equals(getString(R.string.user_sample))) {
                requestUserEditForAdmin.admin=0;
            } else if (txtUserType.getText().toString().trim().equals(getString(R.string.admin))) {
                requestUserEditForAdmin.admin=1;
            }
            Call<ResponseMessageToken> call = retrofitService.updateSeen(requestUserEditForAdmin);
            call.enqueue(new Callback<ResponseMessageToken>() {
                @Override
                public void onResponse(Call<ResponseMessageToken> call, retrofit2.Response<ResponseMessageToken> response) {

                    progBar.setVisibility(View.INVISIBLE);
                    if (response.isSuccessful()) {
                        Toast.makeText(EditUserInfoForAdmin.this, response.body().message, Toast.LENGTH_SHORT).show();
                        App.fragmentUserListForAdmin.getRegisterList();
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("token", response.body().token);
                        editor.apply();
                    }
                }

                @Override
                public void onFailure(Call<ResponseMessageToken> call, Throwable t) {
                    progBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(EditUserInfoForAdmin.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


//            StringRequest editRequest = new StringRequest(Request.Method.POST, App.EDIT_USER_FOR_ADMIN_URI,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            progBar.setVisibility(View.INVISIBLE);
//                            Toast.makeText(EditUserInfoForAdmin.this, response, Toast.LENGTH_SHORT).show();
//                            App.fragmentUserListForAdmin.getRegisterList();
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    progBar.setVisibility(View.INVISIBLE);
//                    Toast.makeText(EditUserInfoForAdmin.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }) {
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String, String> params = new HashMap<>();
//                    params.put("user", user.getUserShop());
//                    if (txtUserType.getText().toString().equals(getString(R.string.user_sample))) {
//                        params.put("admin", "0");
//                    } else if (txtUserType.getText().toString().equals(getString(R.string.admin))) {
//                        params.put("admin", "1");
//                    }
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
