package com.example.ahmad2.shopproject;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class App extends Application {

//    public static final String BASE_URL = "http://ahmadandroid.ir/myrestapi/";
    public static final String BASE_URL = "http://ahmadandroid.ir/myrestapipasazh/";
//    public static final String BASE_URL = "http://127.0.0.1:8080/mysite3/";
//    public static final String BASE_URL = "http://192.168.1.104:8080/mysite3/";
//    public static final String BASE_URL = "http://192.168.43.219:8080/mysite3/";

    public static final String SEARCH_USER_URI = BASE_URL + "searchforusername.php";
    public static final String READ_CODE_URI = BASE_URL + "readlastcodeobject.php";
    public static final String UPLOAD_IMAGE_TO_SERVER_URI = BASE_URL + "UploadToServer.php";
    public static final String UPLOAD_IMAGE_SHOP_TO_SERVER_URI = BASE_URL + "UploadImageShopToServer.php";
    public static final String DOWN_IMAGE_FROM_SERVER_URI = BASE_URL + "uploads/";



    private RequestQueue requestQueue;
    private Context context;
    public static FragmentShopList fragmentShopList;
    public static FragmentObjectListForAdmin fragmentObjectListForAdmin;
    public static FragmentUserListForAdmin fragmentUserListForAdmin;
    public static FragmentOrderForAdmin fragmentOrderForAdmin;
    public static FragmentOrderForUser fragmentOrderForUser;
    public static FragmentObjectListForUser fragmentObjectListForUser;
    public static FragmentStore fragmentStore;
    public static FragmentInsertShop fragmentInsertShop;
    public static final String TAG_FRAGMENT="TagFragmnet";
    public static  Context CONTEXT;
    public static List<ObjectInfo> objectList;
    private SharedPreferences preferences;
    private String user,token;
    public static boolean IS_OBJECTLIST_ADMIN=false;
    public static boolean IS_ORDERLIST_ADMIN=false;
    public static boolean IS_OBJECTLIST_STORE=false;
    public static boolean IS_OBJECTLIST_USER=false;
    public static boolean IS_ORDERLIST_USER=false;
    public static boolean IS_SHOPLIST=false;
    public static boolean IS_SHOPLIST_SELLSPECIAL=false;
    public static boolean IS_INSERTSHOP=false;
    public static boolean IS_INSERTSELL=false;
    public static int VERSION;
    public static SharedPreferences prefUserInfo;
    public static SharedPreferences prefShopInfo;
    public static SharedPreferences prefObjectList;


    public App(Context context) {
        this.context = context;
    }

    public App() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("TagAppChange","App");
        preferences=getSharedPreferences("userInfoPre",MODE_PRIVATE);
        prefShopInfo=getSharedPreferences("shopInfo",MODE_PRIVATE);
        prefUserInfo =getSharedPreferences("userInfoPre",MODE_PRIVATE);
        prefObjectList =getSharedPreferences("objectPre",MODE_PRIVATE);
        if (preferences!=null){
            user=preferences.getString("user","");
            token=preferences.getString("token","");
        }
        objectList=new ArrayList<>();
//        getObjectList();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/BNazanin.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        File dir = Environment.getExternalStorageDirectory();
        File subDir = new File(dir, "ShopDirPic");
        if (!subDir.exists())
            subDir.mkdirs();
        requestQueue = Volley.newRequestQueue(getApplicationContext());

    }

    public synchronized RequestQueue getRequestQueue() {
        if (requestQueue == null)
            return requestQueue = Volley.newRequestQueue(context);
        return requestQueue;
    }
}
