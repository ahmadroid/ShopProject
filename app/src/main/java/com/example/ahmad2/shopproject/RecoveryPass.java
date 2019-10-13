package com.example.ahmad2.shopproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.scottyab.aescrypt.AESCrypt;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Random;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecoveryPass extends MyActivity implements View.OnClickListener {

    private EditText edtUser, edtMobile;
    private ProgressBar progBar;
    private Button btnRecovery;
    private Retrofit retrofit;
    private String user;
    private String mobile;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery_pass);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        edtMobile = findViewById(R.id.edt_mobile_RecoveryPass);
        edtUser = findViewById(R.id.edt_user_RecoveryPass);
        btnRecovery = findViewById(R.id.btn_RecoveryPass);
        btnRecovery.setOnClickListener(this);
        progBar = findViewById(R.id.prog_RecoveryPass);
        preferences=getSharedPreferences("userInfoPre",MODE_PRIVATE);

    }


    public void onClick(View view) {
        if (view.equals(btnRecovery)) {
            sendSms();
        }
    }

    private void sendSms() {
        mobile=edtMobile.getText().toString().trim();
        user=edtUser.getText().toString().trim();
        progBar.setVisibility(View.VISIBLE);
        Random random=new Random();
        int min=111111;
        int max=999999;
        String messages=(String.valueOf(random.nextInt(max-min+1)+min));

        PanelInfo panel=new PanelInfo();

        panel.Messages=new String[]{messages};
        panel.LineNumber="30004747472856";
        panel.CanContinueInCaseOfError="false";
        panel.MobileNumbers=new String[]{mobile};
        panel.SendDateTime="";

        panel.user=user;
        try {
            String encPass=AESCrypt.encrypt("123",messages);
            panel.pass=encPass;
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        OkHttpClient.Builder clientBuilder=new OkHttpClient.Builder();
        clientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request orginal = chain.request();
                Request request=orginal.newBuilder()
                        .addHeader("Content-Type","application/json")
                        .method(orginal.method(),orginal.body())
                        .build();
                return chain.proceed(request);
            }
        });
        OkHttpClient client=clientBuilder.build();
        retrofit=new Retrofit.Builder()
                .baseUrl(App.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        RetrofitService retrofitService=retrofit.create(RetrofitService.class);
        Call<ResponseMessageToken> call = retrofitService.sendSmsRecovery(panel);
        call.enqueue(new Callback<ResponseMessageToken>() {
            @Override
            public void onResponse(Call<ResponseMessageToken> call, retrofit2.Response<ResponseMessageToken> response) {
                progBar.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()){
                    Toast.makeText(RecoveryPass.this, response.body().message, Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putString("token",response.body().token);
                    editor.apply();
                }else{
                    Toast.makeText(RecoveryPass.this, "not valid", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseMessageToken> call, Throwable t) {
                progBar.setVisibility(View.INVISIBLE);
                Toast.makeText(RecoveryPass.this, getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
