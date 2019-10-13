package com.example.ahmad2.shopproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

        private String user = "", token = "";
        Preference prefShowInfo;
        String showInfo;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            if (App.prefUserInfo != null) {
                user = App.prefUserInfo.getString("user", "");
                token = App.prefUserInfo.getString("token", "");
            }
            prefShowInfo = findPreference("info_sel");
//            prefShowInfo.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//                @Override
//                public boolean onPreferenceChange(Preference preference, Object newValue) {
////                    changeShowInfo();
////                    Toast.makeText(getContext(), "change", Toast.LENGTH_SHORT).show();
//                    return false;
//                }
//            });
        }

        private void changeShowInfo() {
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            clientBuilder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .addHeader("Content-Type", "application/json")
                            .addHeader("shop-token", token)
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
            login.pass = getShowInfo();
            Call<ResponseMessageToken> call = retrofitService.updateShowInfo(login);
            call.enqueue(new Callback<ResponseMessageToken>() {
                @Override
                public void onResponse(Call<ResponseMessageToken> call, retrofit2.Response<ResponseMessageToken> response) {
                    if (response.isSuccessful()) {
                        SharedPreferences.Editor editor = App.prefUserInfo.edit();
                        editor.putString("token", response.body().token);
                        editor.apply();
                    }

                }

                @Override
                public void onFailure(Call<ResponseMessageToken> call, Throwable t) {
                }
            });
        }

        private String getShowInfo() {
            showInfo = "234567";
            String newShowInfo = "";
            if (getContext() != null) {
                Set<String> showInfoSet = prefShowInfo.getPersistedStringSet(new HashSet<String>());
                for (String str : showInfoSet) {
                    switch (str) {
                        case "name":
                            showInfo = showInfo.replace("7", "1");
                            break;
                        case "shop":
                            showInfo = showInfo.replace("6", "1");
                            break;
                        case "job":
                            showInfo=showInfo.replace("5", "1");
                            break;
                        case "mobile":
                            showInfo=showInfo.replace("4", "1");
                            break;
                        case "phone":
                            showInfo=showInfo.replace("3", "1");
                            break;
                        case "address":
                            showInfo=showInfo.replace("2", "1");
                            break;
                    }
                }
            }

            return showInfo;
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("info_sel")) {
                changeShowInfo();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}