package com.example.ahmad2.shopproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InsertComment extends MyActivity implements View.OnClickListener {

    private Button btnInsert;
    private EditText edtMessage;
    private ProgressBar progBar;
    private SharedPreferences preferences;
    private String user,token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_comment);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferences=getSharedPreferences("userInfoPre",MODE_PRIVATE);
        if (preferences!=null){
            user=preferences.getString("user","");
            token=preferences.getString("token","");
        }
        progBar=findViewById(R.id.prog_insert_comment);
        edtMessage=findViewById(R.id.edt_message_insert_comment);
        btnInsert=findViewById(R.id.btn_insert_comment);
        btnInsert.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view.equals(btnInsert)){
            if (edtMessage.getText().toString().isEmpty()){
                edtMessage.setError(getString(R.string.msg_comment));
                edtMessage.requestFocus();
                return;
            }
            progBarVisible();
            OkHttpClient.Builder clientBuilder=new OkHttpClient.Builder();
            clientBuilder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request request=original.newBuilder()
                            .addHeader("Content-Type","application/json")
                            .addHeader("shop-token",token)
                            .method(original.method(),original.body())
                            .build();
                    return chain.proceed(request);
                }
            });
            OkHttpClient client=clientBuilder.build();
            Retrofit retrofit=new Retrofit.Builder()
                    .baseUrl(App.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
            RetrofitService retrofitService=retrofit.create(RetrofitService.class);
            Login login=new Login();
            login.user=user;
            login.pass=edtMessage.getText().toString().trim();
            Call<ResponseMessageToken> call = retrofitService.insertComment(login);
            call.enqueue(new Callback<ResponseMessageToken>() {
                @Override
                public void onResponse(Call<ResponseMessageToken> call, retrofit2.Response<ResponseMessageToken> response) {
                    progBarInVisible();
                    if (response.isSuccessful()){
                        SharedPreferences.Editor editor=preferences.edit();
                        editor.putString("token",response.body().token);
                        editor.apply();
                        Toast.makeText(InsertComment.this,response.body().message,Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseMessageToken> call, Throwable t) {
                    progBarInVisible();
                    Toast.makeText(InsertComment.this, getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
                }
            });
        }
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
