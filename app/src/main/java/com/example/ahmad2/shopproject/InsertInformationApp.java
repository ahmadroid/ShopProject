package com.example.ahmad2.shopproject;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InsertInformationApp extends MyActivity implements View.OnClickListener {

    private EditText edtVersion,edtMessage;
    private Button btnInsert;
    private ProgressBar progBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_information_app);
        edtMessage=findViewById(R.id.edt_message_insert_info_app);
        edtVersion=findViewById(R.id.edt_version_insert_info_app);
        btnInsert=findViewById(R.id.btn_insert_info_app);
        progBar=findViewById(R.id.prog_insert_info_app);
        btnInsert.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.equals(btnInsert)){
            OkHttpClient client=new OkHttpClient();
            Retrofit retrofit=new Retrofit.Builder()
                    .baseUrl(App.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
            RetrofitService retrofitService=retrofit.create(RetrofitService.class);
            String version=edtVersion.getText().toString().trim();
            String message=edtMessage.getText().toString().trim();
            Call<ResponseMessageToken> call = retrofitService.insertInformation(version, message);
            call.enqueue(new Callback<ResponseMessageToken>() {
                @Override
                public void onResponse(Call<ResponseMessageToken> call, Response<ResponseMessageToken> response) {
                    if (response.isSuccessful()){
                        Toast.makeText(InsertInformationApp.this, response.body().message, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseMessageToken> call, Throwable t) {

                }
            });
        }
    }
}
