package com.example.ahmad2.shopproject;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.scottyab.aescrypt.AESCrypt;

import java.io.IOException;
import java.security.GeneralSecurityException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RequestLogin extends MyActivity implements View.OnClickListener {


    private EditText edtUser, edtPass;
    private TextView txtForget;
    private Button btnLogin, btnReg;
    private App app;
    private ProgressBar progressBar;
    private static final int REQ_CODE_PER = 123;
    private Toolbar toolbar;
    private SharedPreferences preferences;

    private RetrofitService retrofitService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_login);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            chckPermission();
        app = new App(this);
        txtForget = findViewById(R.id.txt_forgetpass_request);
        edtPass = findViewById(R.id.edt_pass_request);
        edtUser = findViewById(R.id.edt_user_request);
        btnLogin = findViewById(R.id.btn_login_request);
        btnLogin.setOnClickListener(this);
        btnReg = findViewById(R.id.btn_register_request);
        btnReg.setOnClickListener(this);
        progressBar = findViewById(R.id.prog_login);
        preferences = getSharedPreferences("userInfoPre", MODE_PRIVATE);

//        TextView txtUserName=findViewById(R.id.txt_lin1_main);
//        Typeface typeface=Typeface.createFromAsset(getAssets(),"fonts/Behdad.ttf");
//        txtUserName.setTypeface(typeface);
        txtForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RequestLogin.this, RecoveryPass.class));
            }
        });

    }

    private void chckPermission() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS}, REQ_CODE_PER);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_CODE_PER) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[3] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[4] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[5] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(RequestLogin.this, "permok", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(RequestLogin.this, "PermNo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onClick(View view) {
        if (view.equals(btnLogin)) {
            if (edtUser.getText().toString().trim().equals("") || edtPass.getText().toString().trim().equals("")) {
                Toast.makeText(this, getString(R.string.warrning_input_user_or_pass), Toast.LENGTH_SHORT).show();
                return;
            }
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
            Login login = new Login();
            String Str = edtPass.getText().toString().trim();
            String decStr = "";
            try {
                decStr = AESCrypt.encrypt("123", Str);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
            login.pass = decStr;
            login.user = edtUser.getText().toString().trim();
            Call<User> call = retrofitService.getUser(login);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, retrofit2.Response<User> response) {
                    SharedPreferences.Editor editor = preferences.edit();
                    if (response.isSuccessful()) {
                        editor.putString("name", response.body().name);
                        editor.putString("mobile", response.body().mobile);
                        editor.putString("tel", response.body().tel);
                        editor.putString("address", response.body().address);
                        editor.putString("user", response.body().UserName);
                        editor.putString("token", response.body().token);
                        editor.apply();
                        int admin = response.body().admin;
                        if (admin == 1) {
                            Intent intent = new Intent(RequestLogin.this, AdminPage.class);
                            startActivity(intent);
                        } else if (admin == 0) {
                            Intent intent = new Intent(RequestLogin.this, UserPage.class);
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(RequestLogin.this, getString(R.string.warrning_mistake_user_or_pass), Toast.LENGTH_SHORT).show();
                }
            });


//            progressBar.setVisibility(View.VISIBLE);
//            final StringRequest loginRequest = new StringRequest(Request.Method.POST, App.READ_USER_URI,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            progressBar.setVisibility(View.INVISIBLE);
//                            try {
//                                SharedPreferences.Editor editor = preferences.edit();
//                                JSONObject json = new JSONObject(response);
//                                JSONObject jsonObject = json.getJSONObject("jsn");
//                                String user = jsonObject.getString("user");
//                                String decPass = "", pass = "";
//                                String str = jsonObject.getString("pass");
//                                try {
//                                    decPass = AESCrypt.decrypt("123", str);
//                                } catch (GeneralSecurityException e) {
//                                    e.printStackTrace();
//                                }
//                                pass = decPass;
//                                String name = jsonObject.getString("name");
//                                editor.putString("name", name);
//                                editor.putString("mobile", jsonObject.getString("mobile"));
//                                editor.putString("tel", jsonObject.getString("tel"));
//                                editor.putString("address", jsonObject.getString("address"));
//                                editor.putString("user", user);
//                                editor.putString("token", jsonObject.getString("token"));
//                                editor.apply();
//                                int admin = jsonObject.getInt("admin");
//                                if (admin == 1) {
//                                    Intent intent = new Intent(RequestLogin.this, AdminPage.class);
//                                    startActivity(intent);
//                                } else if (admin == 0) {
//                                    Intent intent = new Intent(RequestLogin.this, UserPage.class);
//                                    startActivity(intent);
//                                }
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                                Toast.makeText(RequestLogin.this, getString(R.string.warrning_mistake_user_or_pass), Toast.LENGTH_LONG).show();
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    progressBar.setVisibility(View.INVISIBLE);
//                    Toast.makeText(RequestLogin.this, "error", Toast.LENGTH_SHORT).show();
//                }
//            }) {
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String, String> params = new HashMap<>();
//                    params.put("user", edtUser.getText().toString().trim());
//                    String Str = edtPass.getText().toString().trim();
//                    String decStr = "";
//                    try {
//                        decStr = AESCrypt.encrypt("123", Str);
//                    } catch (GeneralSecurityException e) {
//                        e.printStackTrace();
//                    }
//                    params.put("pass", decStr);
//                    ApiKey apiKey = new ApiKey();
//                    params.put("secretKey", apiKey.secretKey);
//                    params.put("apiKey", apiKey.apiKey);
//                    return params;
//                }
//            };
//            app.getRequestQueue().add(loginRequest);
        } else if (view.equals(btnReg)) {
            startActivity(new Intent(RequestLogin.this, RegisterNewUser.class));
        }
    }

}
