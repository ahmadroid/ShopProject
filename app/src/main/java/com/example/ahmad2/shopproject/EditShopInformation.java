package com.example.ahmad2.shopproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Url;

public class EditShopInformation extends MyActivity implements View.OnClickListener {

    private SharedPreferences prefUserInfo, prefShopInfo;
    private String token, user, imageName;
    private boolean imageSelect, oldImage;
    private Drawable drawable;
    private Shop shop;
    private EditText edtName, edtJob, edtMobile, edtPhone, edtAddress, edtShop;
    private ImageView imgShop,imgDelete, imgAddPic;
    private ProgressBar progBar;
    private Button btnEdit, btnDelete;
    private App app;
    private File subDir;
    public static final int REQ_CODE = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_shop_information);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        subDir = new File(Environment.getExternalStorageDirectory(), "ShopDirPic");
        imageSelect = false;
        oldImage = false;
        app = new App(this);
        getPreferences();
        imageName = user + ".jpg";
        setView();
        drawable = imgShop.getDrawable();

        imgDelete.setOnClickListener(this);
        imgAddPic.setOnClickListener(this);
        btnEdit.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferences();
        setViewByShop(shop);
    }

    private void getPreferences() {
        prefUserInfo = getSharedPreferences("userInfoPre", MODE_PRIVATE);
        if (prefUserInfo != null) {
            token = prefUserInfo.getString("token", "");
            user = prefUserInfo.getString("user", "");
        }
        prefShopInfo = getSharedPreferences("shopInfo", MODE_PRIVATE);
        if (prefShopInfo != null) {
            shop = new Shop();
            shop.setName(prefShopInfo.getString("name", ""));
            shop.setShop(prefShopInfo.getString("shop", ""));
            shop.setJob(prefShopInfo.getString("job", ""));
            shop.setMobile(prefShopInfo.getString("mobile", ""));
            shop.setPhone(prefShopInfo.getString("phone", ""));
            shop.setAddress(prefShopInfo.getString("address", ""));
            shop.setIsImageShop((short) prefShopInfo.getInt("isImageShop", 0));
        }
    }

    private void setViewByShop(Shop shop) {
        edtName.setText(shop.getName());
        edtJob.setText(shop.getJob());
        edtShop.setText(shop.getShop());
        edtMobile.setText(shop.getMobile());
        edtPhone.setText(shop.getPhone());
        edtAddress.setText(shop.getAddress());
        if (shop.getIsImageShop() == 1) {
            imageSelect = true;
            oldImage = true;
//            getOldImage();
            String uri = App.DOWN_IMAGE_FROM_SERVER_URI + user + ".jpg";
            new ImgageTask().execute(uri);
            imgDelete.setVisibility(View.VISIBLE);
            imgAddPic.setVisibility(View.INVISIBLE);
        }
    }

    private class ImgageTask extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = getimage(strings[0]);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap!=null) {
                imgShop.setImageBitmap(bitmap);
            }
        }
    }

    private Bitmap getimage(String uri){
//        Bitmap bitmap=null;
        try {
            URL url=new URL(uri);
            InputStream inputStream= (InputStream) url.getContent();
            Bitmap bitmap=BitmapFactory.decodeStream(inputStream);
            return bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void getOldImage() {
        progBar.setVisibility(View.VISIBLE);
        String uri = App.DOWN_IMAGE_FROM_SERVER_URI + user + ".jpg";
        ImageRequest request = new ImageRequest(uri, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                progBar.setVisibility(View.INVISIBLE);
                if (response != null) {
                    imgShop.setImageBitmap(response);

                }
            }
        }, 60, 60, ImageView.ScaleType.FIT_XY,
                Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progBar.setVisibility(View.INVISIBLE);
                    }
                });
        app.getRequestQueue().add(request);
    }

    private void setView() {
        edtAddress = findViewById(R.id.edt_address_edit_shop_info);
        edtPhone = findViewById(R.id.edt_phone_edit_shop_info);
        edtJob = findViewById(R.id.edt_job_edit_shop_info);
        edtMobile = findViewById(R.id.edt_mobile_edit_shop_info);
        edtShop = findViewById(R.id.edt_shop_edit_shop_info);
        edtName = findViewById(R.id.edt_name_edit_shop_info);
        imgDelete = findViewById(R.id.imge_delete_edit_shop_info);
        imgShop = findViewById(R.id.imge_selectPic_edit_shop_info);
        progBar = findViewById(R.id.prog_edit_shop_info);
//        btnSelPic = findViewById(R.id.btn_selectPic_edit_shop_info);
        imgAddPic=findViewById(R.id.imge_add_pic_edit_shop_info);
        btnEdit = findViewById(R.id.btn_edit_edit_shop_info);
//        btnDelete=findViewById(R.id.btn_delete_edit_shop_info);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view.equals(imgDelete)) {
            imgDelete.setVisibility(View.INVISIBLE);
            imgAddPic.setVisibility(View.VISIBLE);
            imageSelect = false;
            imgShop.setImageDrawable(drawable);
            deletePicFromMemory();
        } else if (view.equals(imgAddPic)) {
            Intent imgIntent = new Intent(Intent.ACTION_PICK);
            imgIntent.setType("image/*");
            startActivityForResult(imgIntent, REQ_CODE);
        } else if (view.equals(btnEdit)) {
            progBar.setVisibility(View.VISIBLE);
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            clientBuilder.addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request orginal = chain.request();
                    Request request = orginal.newBuilder()
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
            Shop shop = new Shop();
            shop.setUser(user);
            shop.setName(edtName.getText().toString().trim());
            shop.setShop(edtShop.getText().toString().trim());
            shop.setJob(edtJob.getText().toString().trim());
            shop.setMobile(edtMobile.getText().toString().trim());
            shop.setPhone(edtPhone.getText().toString().trim());
            shop.setAddress(edtAddress.getText().toString().trim());
            if (imageSelect) {
                shop.setIsImageShop((short) 1);
            } else {
                shop.setIsImageShop((short) 0);
            }
            RequestShopApiKey requestShopApiKey = new RequestShopApiKey();
            requestShopApiKey.shop = shop;
            Call<ResponseMessageToken> call = retrofitService.updateShopInfo(requestShopApiKey);
            call.enqueue(new Callback<ResponseMessageToken>() {
                @Override
                public void onResponse(Call<ResponseMessageToken> call, retrofit2.Response<ResponseMessageToken> response) {

                    if (response.isSuccessful()) {
                        SharedPreferences.Editor editor = prefUserInfo.edit();
                        editor.putString("token", response.body().token);
                        editor.apply();
                        SharedPreferences.Editor shopEditor = prefShopInfo.edit();
                        shopEditor.putString("name", edtName.getText().toString().trim());
                        shopEditor.putString("shop", edtShop.getText().toString().trim());
                        shopEditor.putString("job", edtJob.getText().toString().trim());
                        shopEditor.putString("mobile", edtMobile.getText().toString().trim());
                        shopEditor.putString("phone", edtPhone.getText().toString().trim());
                        shopEditor.putString("address", edtAddress.getText().toString().trim());
                        if (imageSelect) {
                            shopEditor.putInt("isImageShop", 1);
                        } else {
                            shopEditor.putInt("isImageShop", 0);
                        }
                        shopEditor.apply();
                        Toast.makeText(EditShopInformation.this, response.body().message, Toast.LENGTH_SHORT).show();
                        if (imageSelect) {
                            Utility utility = new Utility();
                            utility.dir = subDir.getAbsolutePath();
                            utility.fileName = imageName;
                            utility.url = App.UPLOAD_IMAGE_SHOP_TO_SERVER_URI;
                            new ImageTask().execute(utility);
                        } else {
//                            App.fragmentShopList.getShopList();
                            progBar.setVisibility(View.INVISIBLE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseMessageToken> call, Throwable t) {
                    progBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(EditShopInformation.this, getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
                    Log.i("tagerrormessage", t.getMessage());
                }
            });
        }
    }

    private void deletePicFromMemory() {
        File file = new File(subDir, imageName);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE && resultCode == RESULT_OK) {
            imageSelect = true;
            imgDelete.setVisibility(View.VISIBLE);
            imgAddPic.setVisibility(View.INVISIBLE);
            deletePicFromMemory();
            Uri uri = data.getData();
            imgShop.setImageURI(uri);
            File file = new File(subDir, imageName);
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                ByteArrayOutputStream outputStream=ResizePicture.changeImageSize(inputStream);
                FileOutputStream fileOutputStream=new FileOutputStream(file);
                fileOutputStream.write(outputStream.toByteArray());
                fileOutputStream.flush();
                fileOutputStream.close();
                outputStream.close();



//                FileOutputStream fileOutputStream = new FileOutputStream(file);
//                BufferedInputStream buffer = new BufferedInputStream(inputStream);
//                byte[] bytes = new byte[1024];
//                int count = 0;
//                while ((count = buffer.read(bytes)) != -1) {
//                    fileOutputStream.write(bytes, 0, count);
//                }
//                fileOutputStream.flush();
//                fileOutputStream.close();
//                buffer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private Bitmap decodeUri(Uri imagePath) throws FileNotFoundException {

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(imagePath), null, o);

        final int REQUIRED_SIZE = 140;

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
                    || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(imagePath), null, o2);

    }

    private class Utility {
        public String dir;
        public String fileName;
        public String url;
    }

    private class ImageTask extends AsyncTask<Utility, Void, String> {


        @Override
        protected String doInBackground(Utility... utilities) {
            Utility util = new Utility();
            util = utilities[0];
            UploadPicToServerDb uploadPicToServerDb = new UploadPicToServerDb(util.dir, util.fileName, util.url);
            uploadPicToServerDb.uploadStart();
            return "uploaded";
        }

        @Override
        protected void onPostExecute(String s) {
//            App.fragmentShopList.getShopList();
            progBar.setVisibility(View.INVISIBLE);
        }
    }
}
