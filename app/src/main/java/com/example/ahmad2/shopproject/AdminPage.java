package com.example.ahmad2.shopproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminPage extends MyActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager viewPager;
    private FragmentAdapter adapter;
    private List<Fragment> fragmentList;
    private TabLayout tabLayout;
    private List<String> titleList;
    private Toolbar toolbar;
    private String user, token;
    private AppCompatTextView txtNameHeader, txtEmailHeader;
    private ImageView imgHeader;
    private SharedPreferences prefUserInfo, prefShopInfo;
    private FragmentObjectListForAdmin fragmentObjectListForAdmin;
    private FragmentUserListForAdmin fragmentUserListForAdmin;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private String mobile;
    private Retrofit retrofit;
    private Bitmap bitmap;
    private Information information;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);
        Log.i(App.TAG_FRAGMENT, "AdminPage");
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getPreference();
        toolbar = (Toolbar) findViewById(R.id.toolbar_admin_page);
//        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
//        getSupportActionBar().setIcon(R.drawable.npico2);
//        getSupportActionBar().setTitle(getString(R.string.app_name));
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle(getString(R.string.app_name));
//        toolbar.setSubtitle(getString(R.string.welcom_to_user));
        tabLayout = findViewById(R.id.tab_admin_page);
        fragmentList = new ArrayList<>();
        titleList = new ArrayList<>();
        viewPager = findViewById(R.id.pager_admin_page);
        FragmentMyShop fragmentMyShop = new FragmentMyShop();
        fragmentList.add(fragmentMyShop);
        titleList.add(getString(R.string.title_tab_myShop));
        FragmentUserForAdmin fragmentUserForAdmin = new FragmentUserForAdmin();
        fragmentList.add(fragmentUserForAdmin);
        titleList.add(getString(R.string.title_tab_user_for_admin));
        adapter = new FragmentAdapter(getSupportFragmentManager(), fragmentList, titleList);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
//        Log.i("tagposition",String.valueOf(viewPager.getAdapter().getItemPosition(FragmentObjectListForAdmin.class)));
//                    case "اضافه کردن کالا": {
//                        FragmentInsertObject fragmentInsertObject = (FragmentInsertObject) viewPager.getAdapter().instantiateItem(viewPager, i);
//                        fragmentInsertObject.getSpinner();
//                    }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_adminPage);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.navi_view_adminPage);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        txtEmailHeader = headerView.findViewById(R.id.txt_email_hedear);
        txtNameHeader = headerView.findViewById(R.id.txt_name_hedear);
        TextView txtAppName = headerView.findViewById(R.id.txt_app_name);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/BVahidBd.ttf");
        txtAppName.setTypeface(typeface);
        imgHeader = headerView.findViewById(R.id.img_header);
        imgHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap = getBitmap();
                if (bitmap != null) {
                    Dialog imgDialog = new Dialog(AdminPage.this);
                    imgDialog.setContentView(R.layout.layout_image_dialog);
                    changeSize(imgDialog.getWindow());
                    ImageView img = imgDialog.findViewById(R.id.img_dialog);
                    img.setImageBitmap(bitmap);
                    imgDialog.show();
                }
            }
        });

        getMessageApp();
    }

    private void getMessageApp() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(String.class, new UTFTypeAdapter())
                .create();
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
        retrofit = new Retrofit.Builder()
                .baseUrl(App.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        final Login login = new Login();
        login.user = user;
        Call<ResponseTokenInformation> call = retrofitService.getMessageApp(login);
        call.enqueue(new Callback<ResponseTokenInformation>() {
            @Override
            public void onResponse(Call<ResponseTokenInformation> call, retrofit2.Response<ResponseTokenInformation> response) {
                if (response.isSuccessful()) {
                    SharedPreferences.Editor editor = prefUserInfo.edit();
                    editor.putString("token", response.body().token);
                    editor.apply();
                    information = response.body().information;
                    App.VERSION = information.getVersion();
                    if (information.getSeen() == 0) {
                        if (!information.getMessage().equals("")) {

                            final AlertDialog.Builder builder = new AlertDialog.Builder(AdminPage.this)
                                    .setTitle(getString(R.string.message_app))
                                    .setMessage(information.getMessage())
                                    .setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                            builder.show();
                        }
                    }
                } else {
                    ResponseMessageToken responseError = new ErrorHandler(retrofit).parseError(response);
//                    Toast.makeText(AdminPage.this, responseError.message, Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<ResponseTokenInformation> call, Throwable t) {
                Toast.makeText(AdminPage.this, getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeSize(Window window) {
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        window.setLayout((int) (size.x * 0.8), (int) (size.y * 0.5));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setHeader();
    }

    private void setHeader() {
        getPreference();
        txtNameHeader.setText(user);
//        txtEmailHeader.setText(mobile);
        bitmap = getBitmap();
        if (bitmap != null) {
            imgHeader.setImageBitmap(bitmap);
        }
    }

    private Bitmap getBitmap() {
        Bitmap bmp;
        File subDir = new File(Environment.getExternalStorageDirectory(), "ShopDirPic");
        File file = new File(subDir, "myPic.jpg");
        if (file.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                bmp = BitmapFactory.decodeStream(fileInputStream);
                return bmp;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void getPreference() {
        prefUserInfo = getSharedPreferences("userInfoPre", MODE_PRIVATE);
        if (prefUserInfo != null) {
//            userName= prefUserInfo.getString("name","");
            user = prefUserInfo.getString("user", "");
            token = prefUserInfo.getString("token", "");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_adminPage);
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.item_comment_admin) {
            Intent commentIntent = new Intent(AdminPage.this, InsertComment.class);
            startActivity(commentIntent);
        } else if (id == R.id.item_version_admin) {
            AlertDialog.Builder builder=new AlertDialog.Builder(AdminPage.this);
            if (BuildConfig.VERSION_CODE>=App.VERSION){
                builder.setTitle(getString(R.string.current_version)).setMessage(getString(R.string.updated_app))
                        .setPositiveButton(getString(R.string.close),null);
            }else if (BuildConfig.VERSION_CODE<App.VERSION){
                        builder.setTitle(getString(R.string.current_version))
                        .setMessage(getString(R.string.not_updated_app))
                    .setPositiveButton(getString(R.string.download), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            VersionApp versionApp=new VersionApp();
//                            versionApp.downloadNewVersin(AdminPage.this);
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("bazaar://details?id=" + "com.example.ahmad2.shopproject"));
                                intent.setPackage("com.farsitel.bazaar");
                                startActivity(intent);
                            }catch(Exception e){
                                Toast.makeText(AdminPage.this, getString(R.string.mes_install_bazar), Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                        .setNegativeButton(getString(R.string.close), null);
            }
            builder.show();

        } else if (id == R.id.item_exit_admin) {
            finish();
        }
//        else if (id==R.id.item_select_pic_admin){
//            Intent intent=new Intent(AdminPage.this,SelectImageProfile.class);
//            startActivity(intent);
//        }
        else if (id == R.id.item_edit_shop) {
            Intent intent = new Intent(AdminPage.this, EditShopInformation.class);
            startActivity(intent);
        } else if (id == R.id.item_help_admin) {
            startActivity(new Intent(AdminPage.this, HelpForAdmin.class));
        } else if (id == R.id.item_about_admin) {
            startActivity(new Intent(AdminPage.this, AboutActivity.class));
        }
        else if (id == R.id.item_insert_info_admin) {
            Intent intent = new Intent(AdminPage.this, InsertInformationApp.class);
            startActivity(intent);

        }
        else if (id==R.id.item_setting_admin){
            Intent settingIntent=new Intent(AdminPage.this,SettingsActivity.class);
            startActivity(settingIntent);
        }
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_adminPage);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}
