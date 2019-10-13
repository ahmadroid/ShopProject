package com.example.ahmad2.shopproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class UserPage extends MyActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private TextView txtNameHeader, txtEmailHeader;
    private ImageView imgHeader;
    private String user;
    private Button btnShop;
    //    private String userName;
    private ViewPager viewPager;
    private List<Fragment> fragmentList;
    private List<String> titleList;
    private FragmentAdapter adapter;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SharedPreferences preference;
    private String token;
    private Retrofit retrofit;
    private Information information;
//    private String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        customActionBar();
        setContentView(R.layout.activity_user_page);
        viewPager = findViewById(R.id.pager_user_page);
        tabLayout = findViewById(R.id.tab_user_page);
//        btnShop=findViewById(R.id.btn_shop_user_page);
//        btnShop.setOnClickListener(this);
//        btnShop.setTranslationY(-200);
//        btnShop.animate().translationY(0).setDuration(1000).start();
        fragmentList = new ArrayList<>();
        titleList = new ArrayList<>();
        getPreference();
        toolbar = findViewById(R.id.toolbar_user_page);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle(getString(R.string.app_name));
//        getSupportActionBar().setIcon(R.drawable.npico2);
//        toolbar.setSubtitle(getString(R.string.welcom_to_user));
        final FragmentOrderForUser fragmentOrderForUser = FragmentOrderForUser.newInstance(user);
        App.fragmentOrderForUser = fragmentOrderForUser;
        final FragmentObjectListForUser fragmentObjectListForUser = FragmentObjectListForUser.newInstance(user, fragmentOrderForUser);
        fragmentList.add(fragmentObjectListForUser);

        fragmentList.add(fragmentOrderForUser);

        final FragmentShopList fragmentShopList = new FragmentShopList();
        App.fragmentShopList = fragmentShopList;
        fragmentList.add(fragmentShopList);

        final FragmentSellSpecialForUser fragmentSellSpecialForUser = new FragmentSellSpecialForUser();
        fragmentList.add(fragmentSellSpecialForUser);

        final FragmentInsertShop fragmentInsertShop = new FragmentInsertShop();
        App.fragmentInsertShop=fragmentInsertShop;
        fragmentList.add(fragmentInsertShop);
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            titleList.add(getString(R.string.title_tab_object_list));
//            titleList.add("خرید");
//            titleList.add(getString(R.string.title_tab_shop_list));
//            titleList.add("فوق");
//            titleList.add(getString(R.string.item_shop));
//        } else {
//            titleList.add(getString(R.string.title_tab_object_list));
//            titleList.add(getString(R.string.title_tab_order_user));
//            titleList.add(getString(R.string.title_tab_shop_list));
//            titleList.add(getString(R.string.title_tab_sell_special));
//            titleList.add(getString(R.string.item_shop));
//        }

        titleList.add(getString(R.string.title_tab_object_list));
        titleList.add(getString(R.string.title_tab_order_user));
        titleList.add(getString(R.string.title_tab_shop_list));
        titleList.add(getString(R.string.title_tab_sell_special));
        titleList.add(getString(R.string.item_shop));

        adapter = new FragmentAdapter(getSupportFragmentManager(), fragmentList, titleList);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_shop_green));
        tabLayout.getTabAt(1).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_order_green));
        tabLayout.getTabAt(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_pasazh_green));
        tabLayout.getTabAt(3).setIcon(ContextCompat.getDrawable(UserPage.this, R.drawable.sell_special_green1));
        tabLayout.getTabAt(4).setIcon(ContextCompat.getDrawable(this, R.drawable.reg_shop));
        drawerLayout = findViewById(R.id.drawer_layout_userPage);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.navi_view_userPage);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        txtNameHeader = headerView.findViewById(R.id.txt_name_hedear);
        txtEmailHeader = headerView.findViewById(R.id.txt_email_hedear);
        imgHeader = headerView.findViewById(R.id.img_header);
        imgHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = getBitmap();
                if (bitmap != null) {
                    Dialog imgDialog = new Dialog(UserPage.this);
                    imgDialog.setContentView(R.layout.layout_image_dialog);
                    changeSize(imgDialog.getWindow());
                    ImageView img = imgDialog.findViewById(R.id.img_dialog);
                    img.setImageBitmap(bitmap);
                    imgDialog.show();
                }
            }
        });
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                String text = (String) tab.getText();
//                if (text.equals(getString(R.string.title_tab_object_list))) {
//                    fragmentObjectListForUser.getObjectList();
//                } else if (text.equals(getString(R.string.title_tab_order_user))) {
//                    fragmentOrderForUser.getOrderList();
//                } else if (text.equals(getString(R.string.title_tab_shop_list))) {
//                    fragmentShopList.getShopList();
//                } else if (text.equals(getString(R.string.title_tab_sell_special))) {
//                    fragmentSellSpecialForUser.getObjectList();
//                } else if (text.equals(getString(R.string.item_shop))) {
//                    fragmentInsertShop.checkIsShop();
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
        getMessageApp();
    }

    private void setTypeFace() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/BKoodkBd.ttf");
    }

    private void customActionBar() {
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
//        actionBar.setBackgroundDrawable(new ColorDrawable(Color.GREEN));
//        actionBar.setIcon(R.drawable.np);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayOptions(androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM | androidx.appcompat.app.ActionBar.DISPLAY_SHOW_HOME | androidx.appcompat.app.ActionBar.DISPLAY_HOME_AS_UP);
        androidx.appcompat.app.ActionBar.LayoutParams layoutParams = new androidx.appcompat.app.ActionBar.LayoutParams(androidx.appcompat.app.ActionBar.LayoutParams.FILL_PARENT, androidx.appcompat.app.ActionBar.LayoutParams.FILL_PARENT);
        ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.layout_custom_actionbar, null);
        actionBar.setCustomView(actionBarLayout, layoutParams);


    }

    private void getMessageApp() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .addHeader("Content-Type", "application/json; charset=utf-8")
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
                    SharedPreferences.Editor editor = preference.edit();
                    editor.putString("token", response.body().token);
                    editor.apply();
                    information = response.body().information;
                    App.VERSION = information.getVersion();
                    if (information.getSeen() == 0) {
                        if (!information.getMessage().equals("")) {

                            final AlertDialog.Builder builder = new AlertDialog.Builder(UserPage.this)
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
//                    Toast.makeText(UserPage.this, responseError.message, Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<ResponseTokenInformation> call, Throwable t) {
                Toast.makeText(UserPage.this, getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeSize(Window window) {
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        window.setLayout((int) (size.x * 0.8), (int) (size.y * 0.5));
    }

    private void getPreference() {
        preference = getSharedPreferences("userInfoPre", MODE_PRIVATE);
        if (preference != null) {
            user = preference.getString("user", "");
//            userName = preference.getString("name", "");
            token = preference.getString("token", "");
        }
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
        Bitmap bmp = getBitmap();
        if (bmp != null)
            imgHeader.setImageBitmap(bmp);
    }

    private Bitmap getBitmap() {
        Bitmap bitmap;
        File subDir = new File(Environment.getExternalStorageDirectory(), "ShopDirPic");
        File file = new File(subDir, "myPic.jpg");
        if (file.exists()) {
            try {

                FileInputStream fileInputStream = new FileInputStream(file);
                bitmap = BitmapFactory.decodeStream(fileInputStream);
                return bitmap;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout_userPage);
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
        if (id == R.id.item_version_user) {
            AlertDialog.Builder builder = new AlertDialog.Builder(UserPage.this);
            if (BuildConfig.VERSION_CODE >= App.VERSION) {
                builder.setTitle(getString(R.string.current_version)).setMessage(getString(R.string.updated_app))
                        .setPositiveButton(getString(R.string.close), null);
            } else if (BuildConfig.VERSION_CODE < App.VERSION) {
                builder.setTitle(getString(R.string.current_version))
                        .setMessage(getString(R.string.not_updated_app))
                    .setPositiveButton(getString(R.string.download), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            VersionApp versionApp=new VersionApp();
//                            versionApp.downloadNewVersin(UserPage.this);
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("bazaar://details?id=" + "com.example.ahmad2.shopproject"));
                                intent.setPackage("com.farsitel.bazaar");
                                startActivity(intent);
                            }catch(Exception e){
                                Toast.makeText(UserPage.this, getString(R.string.mes_install_bazar), Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                        .setNegativeButton(getString(R.string.close), null);
            }
            builder.show();
        } else if (id == R.id.item_exit_user) {
            finish();
        }
//        else if (id == R.id.item_select_pic_user) {
//            Intent intent = new Intent(UserPage.this, SelectImageProfile.class);
//            startActivity(intent);
//        }
//        else if (id == R.id.item_shop_user) {
//            finish();
//            Intent intent = new Intent(UserPage.this, InsertShopActivity.class);
//            startActivity(intent);
//        }
        else if (id == R.id.item_comment_user) {
            Intent commentIntent = new Intent(UserPage.this, InsertComment.class);
            startActivity(commentIntent);
        } else if (id == R.id.item_help_user) {
            startActivity(new Intent(UserPage.this, HelpForUser.class));
        } else if (id == R.id.item_about_user) {
            startActivity(new Intent(UserPage.this, AboutActivity.class));
        }
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout_userPage);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_shop, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_shop) {
            startActivity(new Intent(UserPage.this, InsertShopActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void startInstall() {
        String path = Environment.getExternalStorageDirectory() + "/Download/NahavandPasazh.apk";
        File appApkFile = new File(path);
        if (appApkFile.exists()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri apkUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", appApkFile);
                Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                intent.setData(apkUri);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");

                PackageManager packageManager = getPackageManager();
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent);
                }
            } else {
                Uri apkUri = Uri.fromFile(appApkFile);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                PackageManager packageManager = getPackageManager();
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent);
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.equals(btnShop)) {
            finish();
            startActivity(new Intent(UserPage.this, InsertShopActivity.class));
        }
    }
}
