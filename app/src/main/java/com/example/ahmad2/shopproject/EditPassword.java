package com.example.ahmad2.shopproject;

import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
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

import com.scottyab.aescrypt.AESCrypt;

import java.io.IOException;
import java.security.GeneralSecurityException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditPassword extends MyActivity {

    private App app;
    private EditText edtEditPass, edtRepEditPass;
    private Button btnEditPass;
    private ProgressBar editPassProgBar;
    private TextView txtMsg;
    private CheckBox chkBox;
    private String user;
    private String token;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        app = new App(this);
        btnEditPass = findViewById(R.id.btn_edit_pass);
        edtEditPass = findViewById(R.id.edt_edit_pass);
        edtRepEditPass = findViewById(R.id.edt_rep_edit_pass);
        editPassProgBar = findViewById(R.id.prog_edit_pass);
        txtMsg = findViewById(R.id.txt_msg_edit_pass);
        preferences = getSharedPreferences("userInfoPre", MODE_PRIVATE);
        token = preferences.getString("token", "");
        user = preferences.getString("user", "");
//        Bundle extras = getIntent().getExtras();
//        if (extras != null && extras.containsKey("user"))
//            user = extras.getString("user");
        final int lastInputEditPass = edtEditPass.getInputType();
        final int lastInputRepEditPass = edtRepEditPass.getInputType();
        chkBox = findViewById(R.id.chkbox_edit_pass);
        chkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edtEditPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    edtRepEditPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    edtEditPass.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    edtRepEditPass.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                } else if (!isChecked) {
                    edtEditPass.setInputType(lastInputEditPass);
                    edtRepEditPass.setInputType(lastInputRepEditPass);
                    edtEditPass.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    edtRepEditPass.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                }
            }
        });
        btnEditPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtEditPass.getText().toString().isEmpty()) {
                    edtEditPass.setError(getString(R.string.error_edit_pass));
                    edtEditPass.requestFocus();
                    return;
                } else if (edtRepEditPass.getText().toString().isEmpty()) {
                    edtRepEditPass.setError(getString(R.string.error_edit_repPass));
                    edtRepEditPass.requestFocus();
                    return;
                } else if (!edtRepEditPass.getText().toString().equals(edtEditPass.getText().toString())) {
                    txtMsg.setVisibility(View.VISIBLE);
                    return;
                }
                txtMsg.setVisibility(View.GONE);
                editPassProgBar.setVisibility(View.VISIBLE);
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
                Login login = new Login();
                login.user = user;
                try {
                    String encryptPass = AESCrypt.encrypt("123", edtEditPass.getText().toString().trim());
                    login.pass = encryptPass;
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                Call<ResponseMessageToken> call = retrofitService.editPass(login);
                call.enqueue(new Callback<ResponseMessageToken>() {
                    @Override
                    public void onResponse(Call<ResponseMessageToken> call, retrofit2.Response<ResponseMessageToken> response) {
                        if (response.isSuccessful()){
                            String message = response.body().message;
                            Toast.makeText(EditPassword.this, message, Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor=preferences.edit();
                            editor.putString("token",response.body().token);
                            editor.apply();
                            editPassProgBar.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseMessageToken> call, Throwable t) {
                        editPassProgBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(EditPassword.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


//                StringRequest editPassUser = new StringRequest(Request.Method.POST, App.EDIT_PASS_FOR_USER_URI,
//                        new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response) {
//                                Toast.makeText(EditPassword.this, response, Toast.LENGTH_SHORT).show();
//                                editPassProgBar.setVisibility(View.INVISIBLE);
//                            }
//                        }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(EditPassword.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//                        editPassProgBar.setVisibility(View.INVISIBLE);
//                    }
//                }) {
//                    @Override
//                    protected Map<String, String> getParams() throws AuthFailureError {
//                        Map<String, String> params = new HashMap<>();
//                        params.put("user", user);
//                        try {
//                            String encryptPass = AESCrypt.encrypt("123", edtEditPass.getText().toString().trim());
//                            params.put("pass", encryptPass);
//                        } catch (GeneralSecurityException e) {
//                            e.printStackTrace();
//                        }
//                        return params;
//                    }
//                };
//                app.getRequestQueue().add(editPassUser);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}


