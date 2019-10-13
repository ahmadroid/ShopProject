package com.example.ahmad2.shopproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentEditUserInformation extends Fragment {

    private User user;
    private AppCompatEditText edtName, edtUser, edtMobile, edtTel, edtAddress;
    private AppCompatButton btnEdit;
    private ProgressBar progBar;
    private App app;
    private SharedPreferences preferences;
    private String token;

    public static FragmentEditUserInformation newInstance(Parcelable user) {
        FragmentEditUserInformation fragment = new FragmentEditUserInformation();
        Bundle arg = new Bundle();
        arg.putParcelable("userParcel", user);
        fragment.setArguments(arg);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() != null)
            preferences = getContext().getSharedPreferences("userInfoPre", Context.MODE_PRIVATE);
        token=preferences.getString("token","");
//        if (getArguments() != null && getArguments().containsKey("userParcel"))
//            user = (User) getArguments().getParcelable("userParcel");
        app = new App(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_edit_user_information, container, false);
        edtName = view.findViewById(R.id.edt_name_edit_register);
        edtUser = view.findViewById(R.id.edt_user_edit_register);
        edtMobile = view.findViewById(R.id.edt_mobile_edit_register);
        edtTel = view.findViewById(R.id.edt_tel_edit_register);
        edtAddress = view.findViewById(R.id.edt_address_edit_register);
        btnEdit = view.findViewById(R.id.btn_edit_edit_register);
        progBar = view.findViewById(R.id.prog_edit_register);
        edtName.setText(preferences.getString("name", ""));
        edtMobile.setText(preferences.getString("mobile", ""));
        edtTel.setText(preferences.getString("tel", ""));
        edtAddress.setText(preferences.getString("address", ""));
        edtUser.setText(preferences.getString("user", ""));
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progBar.setVisibility(View.VISIBLE);
                final SharedPreferences.Editor editor = preferences.edit();
                if (edtAddress.getText() != null)
                    editor.putString("address", edtAddress.getText().toString().trim());
                if (edtTel.getText() != null)
                    editor.putString("tel", edtTel.getText().toString().trim());
                if (edtMobile.getText() != null)
                    editor.putString("mobile", edtMobile.getText().toString().trim());
                if (edtUser.getText() != null)
                    editor.putString("user", edtUser.getText().toString().trim());
                if (edtName.getText() != null)
                    editor.putString("name", edtName.getText().toString().trim());
                editor.apply();
                OkHttpClient.Builder clientBuilder=new OkHttpClient.Builder();
                clientBuilder.addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        okhttp3.Request orginal = chain.request();
                        okhttp3.Request request=orginal.newBuilder()
                                .addHeader("Content-Type","application/json")
                                .addHeader("shop-token",token)
                                .method(orginal.method(),orginal.body())
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
                RequestUserApiKey requestUserApiKey=new RequestUserApiKey();
                user=new User();
                user.setName(edtName.getText().toString().trim());
                user.setUserName(edtUser.getText().toString().trim());
                user.setMobile(edtMobile.getText().toString().trim());
                user.setTel(edtTel.getText().toString().trim());
                user.setAddress(edtAddress.getText().toString().trim());
                requestUserApiKey.user=user;
                Call<ResponseMessageToken> call = retrofitService.updateUserInformation(requestUserApiKey);
                call.enqueue(new Callback<ResponseMessageToken>() {
                    @Override
                    public void onResponse(Call<ResponseMessageToken> call, retrofit2.Response<ResponseMessageToken> response) {
                        progBar.setVisibility(View.INVISIBLE);
                        if (response.isSuccessful()){
                            SharedPreferences.Editor editor=preferences.edit();
                            editor.putString("token",response.body().token);
                            editor.apply();
                            Toast.makeText(getContext(), response.body().message, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseMessageToken> call, Throwable t) {
                        progBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


//                StringRequest editRequest = new StringRequest(Request.Method.POST, App.EDIT_USER_INFORMATION_URI,
//                        new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response) {
//                                progBar.setVisibility(View.INVISIBLE);
//                                Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
//                            }
//                        }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        progBar.setVisibility(View.INVISIBLE);
//                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }) {
//                    @Override
//                    protected Map<String, String> getParams() throws AuthFailureError {
//                        Map<String, String> params = new HashMap<>();
//                        if (edtUser.getText() != null)
//                            params.put("user", edtUser.getText().toString().trim());
//                        if (edtAddress.getText() != null)
//                            params.put("address", edtAddress.getText().toString().trim());
//                        if (edtTel.getText() != null)
//                            params.put("tel", edtTel.getText().toString().trim());
//                        if (edtMobile.getText() != null)
//                            params.put("mobile", edtMobile.getText().toString().trim());
//                        if (edtName.getText() != null)
//                            params.put("name", edtName.getText().toString().trim());
//                        return params;
//                    }
//                };
//                app.getRequestQueue().add(editRequest);
            }
        });
        return view;
    }
}
