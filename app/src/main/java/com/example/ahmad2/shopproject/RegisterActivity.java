package com.example.ahmad2.shopproject;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends MyActivity implements View.OnClickListener, CheckConnect.OnConnectToData {

    private TextInputLayout txtLayMobile, txtLayPass;
    private LinearLayout linearLayout;
    private EditText edtMobile, edtPass;
    private Button btnNext, btnInput;
    public static final int PERM_CODE = 17;
    private String mobile, pass, user = "";
    private SharedPreferences preferences;
    private ProgressDialog progDialog;
    private CheckConnect receiver;
    private boolean isRegistered;
    private RelativeLayout relativeLayout;
    private ProgressBar progBar;
    private TextView txtMessage;
    private SharedPreferences prefShopInfo;
    private List<Shop> shopList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        receiver = new CheckConnect();
        shopList = new ArrayList<>();
        preferences = getSharedPreferences("userInfoPre", MODE_PRIVATE);
        prefShopInfo = getSharedPreferences("shopInfo", MODE_PRIVATE);
//        progDialog = new ProgressDialog(RegisterActivity.this);
//        progDialog.setMessage(getString(R.string.authorization_message));
//        progDialog.setCancelable(false);
        setView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPerm();
        }

        linearLayout.setVisibility(View.GONE);
        progBar.setVisibility(View.VISIBLE);
        txtMessage.setVisibility(View.VISIBLE);
        if (preferences != null) {
            user = preferences.getString("user", "");
        }
        checkRegister();
//        authorization();

    }

    private void checkRegister() {
        txtMessage.setText("checkRegister");
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .addHeader("Content-Type", "application/json")
                        .method(original.method(), original.body())
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
        Call<ResponseMessageToken> call = retrofitService.checkRegister(login);
        call.enqueue(new Callback<ResponseMessageToken>() {
            @Override
            public void onResponse(Call<ResponseMessageToken> call, retrofit2.Response<ResponseMessageToken> response) {
                if (response.isSuccessful()) {
                    authorization();
                } else {
                    if (response.code() == 404) {
                        linearLayout.setVisibility(View.VISIBLE);
                        progBar.setVisibility(View.INVISIBLE);
                        txtMessage.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseMessageToken> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public boolean chekConnected() {
        ConnectivityManager conMg = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = conMg.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver.setListener(this);
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void authorization() {
        if (preferences!=null){
            user=preferences.getString("user","");
        }
        txtMessage.setText("authorization");
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request orginal = chain.request();
                Request request = orginal.newBuilder()
                        .addHeader("Content-Type", "application/json")
                        .method(orginal.method(), orginal.body())
                        .build();
                return chain.proceed(request);
            }
        });
        OkHttpClient client = clientBuilder.build();
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(App.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        Login login = new Login();
        login.user = user;
        Call<ResponseShopListToken> call = retrofitService.authorization(login);
        call.enqueue(new Callback<ResponseShopListToken>() {
            @Override
            public void onResponse(Call<ResponseShopListToken> call, retrofit2.Response<ResponseShopListToken> response) {
                Shop shop;
                if (response.isSuccessful()) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("token", response.body().token);
//                    editor.putBoolean("isRegistered",true);
                    shopList = response.body().shopList;
                    if (shopList.size() == 0) {
                        editor.putBoolean("hasShop", false);
                        startUserPage();
                        finish();
                    } else {
                        shop = shopList.get(0);
                        if (prefShopInfo != null) {
                            SharedPreferences.Editor shopEditor = prefShopInfo.edit();
                            shopEditor.putString("name", shop.getName());
                            shopEditor.putString("shop", shop.getShop());
                            shopEditor.putString("job", shop.getJob());
                            shopEditor.putString("mobile", shop.getMobile());
                            shopEditor.putString("phone", shop.getPhone());
                            shopEditor.putString("address", shop.getAddress());
                            shopEditor.putInt("isImageShop", shop.getIsImageShop());
                            shopEditor.apply();
                        }
                        if (shop.getIsShop() == 0) {
                            editor.putBoolean("hasShop", true);
                            startUserPage();
                            finish();
                        } else if (shop.getIsShop() == 1) {
                            Intent adminIntent = new Intent(RegisterActivity.this, AdminPage.class);
                            startActivity(adminIntent);
                            finish();
                        }
                    }
//                    switch (response.body().message) {
//                        case "not": {
//                            editor.putBoolean("hasShop", false);
//                            startUserPage();
//                            finish();
//                        }
//                        break;
//                        case "wait": {
//                            editor.putBoolean("hasShop", true);
//                            startUserPage();
//                            finish();
//                        }
//                        break;
//                        case "confirm": {
//                            Intent adminIntent = new Intent(RegisterActivity.this, AdminPage.class);
//                            startActivity(adminIntent);
//                            finish();
//                        }
//                    }
                    editor.apply();

//                    if (preferences.getBoolean("hasShop", false)) {
//                        Intent adminIntent = new Intent(RegisterActivity.this, AdminPage.class);
//                        startActivity(adminIntent);
//                        finish();
//                    } else {
//                        startUserPage();
//                        finish();
//                    }
                } else {
                    ErrorHandler errorHandler = new ErrorHandler(retrofit);
                    ResponseMessageToken error = errorHandler.parseError(response);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("token", error.token);
//                    editor.putBoolean("isRegistered",false);
                    editor.apply();
                }
            }

            @Override
            public void onFailure(Call<ResponseShopListToken> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, getString(R.string.warrning_authorization_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startUserPage() {
        Intent intent = new Intent(RegisterActivity.this, UserPage.class);
        startActivity(intent);
        finish();
    }

    private void checkPerm() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.ACCESS_NETWORK_STATE}, PERM_CODE);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERM_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[3] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[4] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[5] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[6] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(RegisterActivity.this, "permok", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(RegisterActivity.this, "PermNo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setView() {
        txtLayMobile = findViewById(R.id.txt_input_mobile_register);
        txtLayPass = findViewById(R.id.txt_input_pass_register);
        btnNext = findViewById(R.id.btn_next_register);
        btnInput = findViewById(R.id.btn_input_register);
        edtMobile = findViewById(R.id.edt_mobile_register);
        edtPass = findViewById(R.id.edt_pass_register);
        linearLayout = findViewById(R.id.lin_layout_register);
        relativeLayout = findViewById(R.id.rel_layout_register);
        progBar = findViewById(R.id.prog_register);
        txtMessage = findViewById(R.id.txt_register);
        btnNext.setOnClickListener(this);
        btnInput.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.equals(btnNext)) {
            if (!chekConnected()) {
                Toast.makeText(this, getString(R.string.warrning_connect_to_net), Toast.LENGTH_SHORT).show();
                return;
            } else if (edtMobile.getText().toString().isEmpty()) {
                edtMobile.setError(getString(R.string.warrning_input_mobileNumber));
                return;
            } else {
                sendSms();
            }
        } else if (view.equals(btnInput)) {
            if (edtPass.getText().toString().isEmpty()) {
                edtPass.setError(getString(R.string.warrning_input_pass));
                return;
            }
            registerUser();
        }
    }

    private void registerUser() {
        pass = edtPass.getText().toString().trim();
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request orginal = chain.request();
                Request request = orginal.newBuilder()
                        .addHeader("Content-Type", "application/json")
                        .method(orginal.method(), orginal.body())
                        .build();
                return chain.proceed(request);
            }
        });
        OkHttpClient client = clientBuilder.build();
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(App.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        final Login login = new Login();
        login.user = mobile;
        login.pass = pass;
        Call<ResponseMessageToken> call = retrofitService.newRegister(login);
        call.enqueue(new Callback<ResponseMessageToken>() {
            @Override
            public void onResponse(Call<ResponseMessageToken> call, retrofit2.Response<ResponseMessageToken> response) {
                if (response.isSuccessful()) {
                    if (preferences != null) {
                        SharedPreferences.Editor editor = preferences.edit();
//                        editor.putBoolean("isRegistered", true);
                        editor.putString("token", response.body().token);
                        editor.putString("user",mobile);
                        Log.i("tagRegNewUser","10");
                        Log.i("tagRegNewUser",mobile);
                        editor.apply();
                    }
                    Toast.makeText(RegisterActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                    authorization();
                } else {
                    ResponseMessageToken responseError = new ErrorHandler(retrofit).parseError(response);
                    Toast.makeText(RegisterActivity.this, responseError.message, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(RegisterActivity.this, response.message(), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(RegisterActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                    if (preferences!=null) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("token", responseError.token);
                        editor.apply();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseMessageToken> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendSms() {
        txtLayMobile.setVisibility(View.GONE);
        txtLayPass.setVisibility(View.VISIBLE);
        btnNext.setVisibility(View.GONE);
        btnInput.setVisibility(View.VISIBLE);
        edtPass.requestFocus();
        Toast.makeText(this, getString(R.string.input_pass), Toast.LENGTH_SHORT).show();
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request orginal = chain.request();
                Request request = orginal.newBuilder()
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
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        Random random = new Random();
        int min = 111111;
        int max = 999999;
        int rand = random.nextInt(max - min + 1) + min;
//        String[] messages=new String[]{String.valueOf(rand)};
//        String[] mobiles=new String[]{edtMobile.getText().toString().trim()};
        String message = String.valueOf(rand);
        mobile = edtMobile.getText().toString().trim();
        PanelInfo panel = new PanelInfo();
        panel.MobileNumbers = new String[]{mobile};
        panel.Messages = new String[]{message};
        panel.SendDateTime = "";
        panel.LineNumber = "30004747472856";
        panel.CanContinueInCaseOfError = "false";
        panel.user = "";
        panel.pass = "";
        Call<ResponseMessageToken> call = retrofitService.sendSmsRegister(panel);
        call.enqueue(new Callback<ResponseMessageToken>() {
            @Override
            public void onResponse(Call<ResponseMessageToken> call, retrofit2.Response<ResponseMessageToken> response) {
                if (response.isSuccessful()) {
                    txtLayMobile.setVisibility(View.GONE);
                    txtLayPass.setVisibility(View.VISIBLE);
                    btnNext.setVisibility(View.GONE);
                    btnInput.setVisibility(View.VISIBLE);
                    edtPass.requestFocus();
                    Toast.makeText(RegisterActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
//                    if (preferences != null) {
//                        SharedPreferences.Editor editor = preferences.edit();
//                        editor.putString("user", mobile);
//                        editor.apply();
//                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseMessageToken> call, Throwable t) {
//                Toast.makeText(RegisterActivity.this, getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
//                Log.i("tagSendSms",t.getMessage());
            }
        });

    }

    @Override
    public void onConnect(boolean bool) {
        if (bool) {
//            if (isRegistered) {
//                authorization();
//            }
        } else {
            startActivity(new Intent(RegisterActivity.this, NotConnectActivity.class));
            finish();
//            Toast.makeText(this, getString(R.string.warrning_connect_to_net), Toast.LENGTH_SHORT).show();
            if (isRegistered) {
//                progDialog.show();
            }
        }
    }

}
