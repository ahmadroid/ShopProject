package com.example.ahmad2.shopproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

public class InsertShopActivity extends MyActivity {

    private EditText edtShop, edtName, edtMobile, edtPhone, edtJob, edtAddress;
    private Button btnApply;
    private ProgressBar progBar;
    private TextView txtMessage;
    private ImageView imgDelete, imgShop,imgAddpic;
    private SharedPreferences prefUserInfo,prefShopInfo;
    private String user, token;
    public static final int REQ_CODE = 10;
    private Drawable drawable;
    private boolean imageSelect;
    private File subDir;
    private String imageName;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_insert_shop_in_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fragment=new FragmentInsertShop();
        getSupportFragmentManager().beginTransaction().replace(R.id.lin_layout_insert_shop_menu,fragment).commit();







//        subDir=new File(Environment.getExternalStorageDirectory(),"ShopDirPic");
//        imageSelect=false;
//        prefUserInfo = getSharedPreferences("userInfoPre", MODE_PRIVATE);
//        if (prefUserInfo!=null) {
//            token = prefUserInfo.getString("token", "");
//            user = prefUserInfo.getString("user", "");
//        }
//        prefShopInfo=getSharedPreferences("shopInfo",MODE_PRIVATE);
//        imageName=user+".jpg";
//        setView();
//        btnApply.setOnClickListener(this);
//        imgAddpic.setOnClickListener(this);
//        imgDelete.setOnClickListener(this);

    }

//    private void setView() {
//        edtName = findViewById(R.id.edt_name_insert_shop);
//        edtAddress = findViewById(R.id.edt_address_insert_shop);
//        edtJob = findViewById(R.id.edt_job_insert_shop);
//        edtMobile = findViewById(R.id.edt_mobile_insert_shop);
//        edtPhone = findViewById(R.id.edt_phone_insert_shop);
//        edtShop = findViewById(R.id.edt_shop_insert_shop);
//        btnApply = findViewById(R.id.btn_insertShop_insert_shop);
////        btnSelecPic = findViewById(R.id.btn_selectPic_insert_shop);
//        imgAddpic=findViewById(R.id.imge_add_pic_insert_shop);
//        progBar = findViewById(R.id.prog_insert_shop);
//        txtMessage = findViewById(R.id.txt_notfull_insert_shop);
//        imgShop = findViewById(R.id.imge_selectPic_insert_shop);
//        drawable = imgShop.getDrawable();
//        imgDelete = findViewById(R.id.imge_delete_insert_shop);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(InsertShopActivity.this, UserPage.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onClick(View view) {
//        if (view.equals(btnApply)) {
//            if (edtMobile.getText().toString().isEmpty()
//                    || edtShop.getText().toString().isEmpty()
//                    || edtJob.getText().toString().isEmpty()
//                    || edtName.getText().toString().isEmpty()) {
//                txtMessage.setVisibility(View.VISIBLE);
//                return;
//            }
//            txtMessage.setVisibility(View.GONE);
//            progBar.setVisibility(View.VISIBLE);
//            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
//            clientBuilder.addInterceptor(new Interceptor() {
//                @Override
//                public Response intercept(Chain chain) throws IOException {
//                    Request orginal = chain.request();
//                    Request request = orginal.newBuilder()
//                            .addHeader("Content-Type", "application/json")
//                            .addHeader("shop-token", token)
//                            .method(orginal.method(), orginal.body())
//                            .build();
//                    return chain.proceed(request);
//                }
//            });
//            OkHttpClient client = clientBuilder.build();
//            Retrofit retrofit = new Retrofit.Builder()
//                    .baseUrl(App.BASE_URL)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .client(client)
//                    .build();
//            RetrofitService retrofitService = retrofit.create(RetrofitService.class);
//            Shop shop = new Shop();
//            shop.setAddress(edtAddress.getText().toString().trim());
//            shop.setUser(user);
//            shop.setName(edtName.getText().toString().trim());
//            if (imageSelect) {
//                shop.setIsImageShop((short) 1);
//            }else if (!imageSelect){
//                shop.setIsImageShop((short) 0);
//            }
//            shop.setJob(edtJob.getText().toString().trim());
//            shop.setShop(edtShop.getText().toString().trim());
//            shop.setMobile(edtMobile.getText().toString().trim());
//            shop.setPhone(edtPhone.getText().toString().trim());
//            RequestShopApiKey requestShopApiKey = new RequestShopApiKey();
//            requestShopApiKey.shop = shop;
//            Call<ResponseMessageToken> call = retrofitService.insertShop(requestShopApiKey);
//            call.enqueue(new Callback<ResponseMessageToken>() {
//                @Override
//                public void onResponse(Call<ResponseMessageToken> call, retrofit2.Response<ResponseMessageToken> response) {
//
//                    if (response.isSuccessful()) {
//                        Toast.makeText(InsertShopActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
//                        SharedPreferences.Editor editor = prefUserInfo.edit();
//                        editor.putString("token", response.body().token);
//                        editor.putBoolean("hasShop",true);
//                        editor.apply();
//                        SharedPreferences.Editor shopEditor=prefShopInfo.edit();
//                        shopEditor.putString("name",edtName.getText().toString().trim());
//                        shopEditor.putString("shop",edtShop.getText().toString().trim());
//                        shopEditor.putString("job",edtJob.getText().toString().trim());
//                        shopEditor.putString("mobile" ,edtMobile.getText().toString().trim());
//                        shopEditor.putString("phone",edtPhone.getText().toString().trim());
//                        shopEditor.putString("address",edtAddress.getText().toString().trim());
//                        if (imageSelect) {
//                            shopEditor.putInt("isImageShop", 1);
//                        }else{
//                            shopEditor.putInt("isImageShop",0);
//                        }
//                        shopEditor.apply();
//                        if (imageSelect) {
//                            Utility utility = new Utility();
//                            utility.imageDir = subDir.getAbsolutePath();
//                            utility.imageName = imageName;
//                            utility.uri = App.UPLOAD_IMAGE_SHOP_TO_SERVER_URI;
//                            new UploadTask().execute(utility);
//                        }else {
//                            progBar.setVisibility(View.INVISIBLE);
//                            edtAddress.setText("");
//                            edtJob.setText("");
//                            edtName.setText("");
//                            edtMobile.setText("");
//                            edtPhone.setText("");
//                            edtShop.setText("");
//                            imgShop.setImageDrawable(drawable);
//                            imgDelete.setVisibility(View.INVISIBLE);
//                            imgAddpic.setVisibility(View.VISIBLE);
//                            imageSelect=false;
//                            startActivity(new Intent(InsertShopActivity.this, AdminPage.class));
//                            finish();
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseMessageToken> call, Throwable t) {
//                    progBar.setVisibility(View.INVISIBLE);
//                    Toast.makeText(InsertShopActivity.this, getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
//                }
//            });
//        } else if (view.equals(imgAddpic)) {
//            Intent picIntent = new Intent(Intent.ACTION_PICK);
//            picIntent.setType("image/*");
//            startActivityForResult(picIntent, REQ_CODE);
//        } else if (view.equals(imgDelete)) {
//            imageSelect=false;
//            imgShop.setImageDrawable(drawable);
//            imgDelete.setVisibility(View.INVISIBLE);
//            imgAddpic.setVisibility(View.VISIBLE);
//            File file=new File(subDir,imageName);
//            if (file.exists() && file.isFile()){
//                file.delete();
//            }
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQ_CODE && resultCode == RESULT_OK) {
//            imgDelete.setVisibility(View.VISIBLE);
//            imgAddpic.setVisibility(View.INVISIBLE);
//            imageSelect=true;
//            Uri uri = data.getData();
//            imgShop.setImageURI(uri);
//            try {
//                InputStream inputStream = getContentResolver().openInputStream(uri);
//                ByteArrayOutputStream outputStream = ResizePicture.changeImageSize(inputStream);
//                File file = new File(subDir, imageName);
//                FileOutputStream fileOutputStream = new FileOutputStream(file);
//                fileOutputStream.write(outputStream.toByteArray());
//                fileOutputStream.close();
//                outputStream.close();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private Bitmap decodeUri(Uri imagePath) throws FileNotFoundException {
//
//        BitmapFactory.Options o = new BitmapFactory.Options();
//        o.inJustDecodeBounds = true;
//        BitmapFactory.decodeStream(getContentResolver().openInputStream(imagePath), null, o);
//
//        final int REQUIRED_SIZE = 140;
//
//        int width_tmp = o.outWidth, height_tmp = o.outHeight;
//        int scale = 1;
//        while (true) {
//            if (width_tmp / 2 < REQUIRED_SIZE
//                    || height_tmp / 2 < REQUIRED_SIZE) {
//                break;
//            }
//            width_tmp /= 2;
//            height_tmp /= 2;
//            scale *= 2;
//        }
//
//        BitmapFactory.Options o2 = new BitmapFactory.Options();
//        o2.inSampleSize = scale;
//        return BitmapFactory.decodeStream(getContentResolver().openInputStream(imagePath), null, o2);
//
//    }
//
//    private class Utility {
//        public String imageDir;
//        public String imageName;
//        public String uri;
//    }
//
//    private class UploadTask extends AsyncTask<Utility, Void, String> {
//
//        @Override
//        protected String doInBackground(Utility... utilities) {
//            UploadPicToServerDb uploadPicToServerDb=new UploadPicToServerDb(utilities[0].imageDir,utilities[0].imageName,utilities[0].uri);
//            uploadPicToServerDb.uploadStart();
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            progBar.setVisibility(View.INVISIBLE);
//            edtAddress.setText("");
//            edtJob.setText("");
//            edtName.setText("");
//            edtMobile.setText("");
//            edtPhone.setText("");
//            edtShop.setText("");
//            imgShop.setImageDrawable(drawable);
//            imgDelete.setVisibility(View.INVISIBLE);
//            imgAddpic.setVisibility(View.VISIBLE);
//            imageSelect=false;
//            File file=new File(subDir,imageName);
//            if (file.exists()){
//                file.delete();
//            }
//            startActivity(new Intent(InsertShopActivity.this, AdminPage.class));
//            finish();
//        }
//    }
}
