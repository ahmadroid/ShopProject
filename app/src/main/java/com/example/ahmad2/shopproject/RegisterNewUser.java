package com.example.ahmad2.shopproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.scottyab.aescrypt.AESCrypt;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterNewUser extends MyActivity implements View.OnFocusChangeListener, View.OnClickListener {

    //    private static final String REGISTER_URI = "http://ahmadsmart.000webhostapp.com/mysite/insertuserpasstodb.php";
//    private static final String SEARCH_URI = "http://ahmadsmart.000webhostapp.com/mysite/searchforusername.php";
    private EditText edtName, edtUser, edtPass, edtRepPass, edtMobile, edtTel, edtAddress;
    private Button btnReg;
    private TextView txtWarrning, txtNotFull;
    private boolean userWarrning;
    private CheckBox chkShowPass;
    private App app;
    private ProgressBar progBar;
    private SharedPreferences sharedPreferences;
    private RetrofitService retrofitService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_user);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userWarrning = false;
        app = new App(this);
        sharedPreferences = getSharedPreferences("userInfoPre", MODE_PRIVATE);
        init();
        final int lastInputPass = edtPass.getInputType();
        final int lastInputRepPass = edtRepPass.getInputType();
        chkShowPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edtPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    edtRepPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    edtPass.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    edtRepPass.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                } else if (!isChecked) {
                    edtPass.setInputType(lastInputPass);
                    edtRepPass.setInputType(lastInputRepPass);
                    edtPass.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    edtRepPass.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                }
            }
        });
        edtUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                progBar.setVisibility(View.VISIBLE);


                StringRequest requestSearch = new StringRequest(Request.Method.POST, App.SEARCH_USER_URI
                        , new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progBar.setVisibility(View.INVISIBLE);
                        if (response.equals("empty")) {
                            userWarrning = false;
                            txtWarrning.setVisibility(View.GONE);
                            edtUser.setTextColor(Color.BLUE);
                        } else {
                            userWarrning = true;
                            edtUser.setTextColor(Color.RED);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(RegisterNewUser.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("user", String.valueOf(s));
                        return params;
                    }
                };
                app.getRequestQueue().add(requestSearch);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void init() {
        progBar = findViewById(R.id.prog_register_new_user);
        edtName = findViewById(R.id.edt_name_register_new_user);
        edtPass = findViewById(R.id.edt_pass_register_new_user);
        //Locale edtPassLoc = edtPass.getTextLocale();
//        Locale locale=new Locale("en");
        edtPass.setOnFocusChangeListener(this);
        edtRepPass = findViewById(R.id.edt_rep_pass_register_new_user);
        edtRepPass.setOnFocusChangeListener(this);
        edtUser = findViewById(R.id.edt_user_register_new_user);
        edtUser.setOnFocusChangeListener(this);
        btnReg = findViewById(R.id.btn_insertuser_register_new_user);
        btnReg.setOnClickListener(this);
        chkShowPass = findViewById(R.id.chkbox_register_new_user);
        edtMobile = findViewById(R.id.edt_mobile_register_new_user);
        edtTel = findViewById(R.id.edt_tel_register_new_user);
        edtAddress = findViewById(R.id.edt_address_register_new_user);
        userWarrning = false;
        txtWarrning = findViewById(R.id.txt_warrning_register_new_user);
        txtNotFull = findViewById(R.id.txt_notfull_register_new_user);
    }

    public void onClick(View view) {
        if (view.equals(btnReg)) {
            String pass = edtPass.getText().toString().trim();
            final String repPass = edtRepPass.getText().toString().trim();
            if (userWarrning) {
                txtWarrning.setVisibility(View.VISIBLE);
                txtNotFull.setVisibility(View.GONE);
                return;
            } else if (edtUser.getText().toString().isEmpty() ||
                    pass.isEmpty() ||
                    repPass.isEmpty() ||
                    edtMobile.getText().toString().isEmpty() ||
                    edtName.getText().toString().isEmpty()) {
                txtWarrning.setVisibility(View.GONE);
                txtNotFull.setText(getString(R.string.warrning_fill_field));
                txtNotFull.setVisibility(View.VISIBLE);
                return;
            } else if (!pass.equals(repPass)) {
                txtNotFull.setText(getString(R.string.warrnig_not_equal_pass));
                txtNotFull.setVisibility(View.VISIBLE);
                return;
            }
            userWarrning = false;
            txtWarrning.setVisibility(View.GONE);
            txtNotFull.setVisibility(View.GONE);
            progBar.setVisibility(View.VISIBLE);
            User user = new User();
            user.setName(edtName.getText().toString().trim());
            user.setUserName(edtUser.getText().toString().trim());
            try {
                String encryptPass = AESCrypt.encrypt("123", edtPass.getText().toString().trim());
                user.setPassword(encryptPass);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
            user.setMobile(edtMobile.getText().toString().trim());
            user.setTel(edtTel.getText().toString().trim());
            user.setAddress(edtAddress.getText().toString().trim());
            RequestUserApiKey body = new RequestUserApiKey();
            body.user = user;

            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            clientBuilder.addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    okhttp3.Request orginal = chain.request();
                    okhttp3.Request request = orginal.newBuilder()
                            .addHeader("Content-Type", "application/json")
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
            retrofitService = retrofit.create(RetrofitService.class);
            Call<ResponseMessageToken> call = retrofitService.insertUser(body);
            call.enqueue(new Callback<ResponseMessageToken>() {
                @Override
                public void onResponse(Call<ResponseMessageToken> call, retrofit2.Response<ResponseMessageToken> response) {
                    if (response.isSuccessful()) {
                        String token = response.body().token;
                        String message = response.body().message;
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("name", edtName.getText().toString().trim());
                        editor.putString("mobile", edtMobile.getText().toString().trim());
                        editor.putString("tel", edtTel.getText().toString().trim());
                        editor.putString("address", edtAddress.getText().toString().trim());
                        editor.putString("user", edtUser.getText().toString().trim());
                        editor.putString("token", token);
                        editor.apply();
                        progBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(RegisterNewUser.this, message, Toast.LENGTH_SHORT).show();
                        edtName.setText("");
                        edtPass.setText("");
                        edtRepPass.setText("");
                        edtUser.setText("");
                        chkShowPass.setChecked(false);
                        edtMobile.setText("");
                        edtTel.setText("");
                        edtAddress.setText("");
                        startActivity(new Intent(RegisterNewUser.this, UserPage.class));
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<ResponseMessageToken> call, Throwable t) {
                    Toast.makeText(RegisterNewUser.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


//            final StringRequest request = new StringRequest(Request.Method.POST, App.INSERT_USER_URI,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            try {
//                                JSONObject jsonObject = new JSONObject(response);
//                                String token=jsonObject.getString("accessToken");
//                                String message=jsonObject.getString("message");
//                                SharedPreferences.Editor editor=sharedPreferences.edit();
//                                editor.putString("name", edtName.getText().toString().trim());
//                                editor.putString("mobile", edtMobile.getText().toString().trim());
//                                editor.putString("tel", edtTel.getText().toString().trim());
//                                editor.putString("address", edtAddress.getText().toString().trim());
//                                editor.putString("user", edtUser.getText().toString().trim());
//                                editor.putString("token",token);
//                                editor.apply();
//                                progBar.setVisibility(View.INVISIBLE);
//                                Toast.makeText(RegisterNewUser.this, message, Toast.LENGTH_SHORT).show();
//                                edtName.setText("");
//                                edtPass.setText("");
//                                edtRepPass.setText("");
//                                edtUser.setText("");
//                                chkShowPass.setChecked(false);
//                                edtMobile.setText("");
//                                edtTel.setText("");
//                                edtAddress.setText("");
//                                startActivity(new Intent(RegisterNewUser.this,UserPage.class));
//                                finish();
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    progBar.setVisibility(View.INVISIBLE);
//                    Toast.makeText(RegisterNewUser.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }) {
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String, String> params = new HashMap<>();
//                    params.put("name", edtName.getText().toString());
//                    params.put("user", edtUser.getText().toString());
//                    try {
//                        String encryptPass = AESCrypt.encrypt("123", edtPass.getText().toString().trim());
//                        params.put("pass", encryptPass);
//                    } catch (GeneralSecurityException e) {
//                        e.printStackTrace();
//                    }
//                    params.put("mobile", edtMobile.getText().toString());
//                    params.put("tel", edtTel.getText().toString());
//                    params.put("address", edtAddress.getText().toString());
//                    ApiKey apiKey = new ApiKey();
//                    params.put("secretKey", apiKey.secretKey);
//                    params.put("apiKey", apiKey.apiKey);
//                    return params;
//                }
//            };
//            app.getRequestQueue().add(request);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.equals(edtPass) && hasFocus) {
//            edtPass.setError(getString(R.string.warrning_change_language));
            Toast.makeText(this, getString(R.string.warrning_change_language), Toast.LENGTH_SHORT).show();
        } else if (v.equals(edtRepPass) && hasFocus) {
            Toast.makeText(this, getString(R.string.warrning_change_language), Toast.LENGTH_SHORT).show();
//            edtRepPass.setError(getString(R.string.warrning_change_language));

        } else if (v.equals(edtUser) && hasFocus) {
            Toast.makeText(this, getString(R.string.warrning_change_language), Toast.LENGTH_SHORT).show();
//            edtUser.setError(getString(R.string.warrning_change_language));

        }
    }
}
