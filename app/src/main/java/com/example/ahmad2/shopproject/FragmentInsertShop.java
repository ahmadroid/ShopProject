package com.example.ahmad2.shopproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentInsertShop extends Fragment implements View.OnClickListener {

    private EditText edtShop, edtName, edtMobile, edtPhone, edtJob, edtAddress;
    private Button btnApply, btnInput;
    private ProgressBar progBar;
    private TextView txtMessage;
    private ImageView imgDelete, imgShop, imgAddpic;
//    private SharedPreferences prefUserInfo, prefShopInfo;
    private String user="", token="";
    public static final int REQ_CODE = 10;
    private Drawable drawable;
    private boolean imageSelect;
    private File subDir;
    private String imageName;
    private LinearLayout linLayout;
    private TextView txtWait;
    private boolean hasShop=false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subDir = new File(Environment.getExternalStorageDirectory(), "ShopDirPic");
        imageSelect = false;
//        prefUserInfo = getContext().getSharedPreferences("userInfoPre", Context.MODE_PRIVATE);

//        prefShopInfo = getContext().getSharedPreferences("shopInfo", Context.MODE_PRIVATE);
        imageName = user + ".jpg";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (App.prefUserInfo != null) {
            token = App.prefUserInfo.getString("token", "");
            user = App.prefUserInfo.getString("user", "");
            hasShop = App.prefUserInfo.getBoolean("hasShop", false);
        }
        View view = inflater.inflate(R.layout.activity_insert_shop, container, false);
        setView(view);
        btnApply.setOnClickListener(this);
        imgAddpic.setOnClickListener(this);
        imgDelete.setOnClickListener(this);
        btnInput.setOnClickListener(this);
        progBarVisible();
//        if (!App.IS_INSERTSHOP) {
//            App.IS_INSERTSHOP = true;
//            checkIsShop();
//        }
        checkIsShop();
        return view;
    }

    public void checkIsShop() {
//        if (!App.IS_INSERTSHOP) {
//            return;
//        }
        if (App.prefUserInfo != null) {
            token = App.prefUserInfo.getString("token", "");
            user = App.prefUserInfo.getString("user", "");
            hasShop = App.prefUserInfo.getBoolean("hasShop", false);
        }
        if (!hasShop) {
            progBarInVisible();
        } else {
            linLayout.setVisibility(View.INVISIBLE);

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
            final Login login = new Login();
            login.user = user;
            Call<ResponseMessageToken> call = retrofitService.checkShop(login);
            call.enqueue(new Callback<ResponseMessageToken>() {
                @Override
                public void onResponse(Call<ResponseMessageToken> call, retrofit2.Response<ResponseMessageToken> response) {
                    progBarInVisible();
                    if (response.isSuccessful()) {
                        SharedPreferences.Editor editor = App.prefUserInfo.edit();
                        editor.putString("token",response.body().token);
                        switch (response.body().message) {
                            case "not": {
                                editor.putBoolean("hasShop", false);
                                linLayout.setVisibility(View.VISIBLE);
                                txtWait.setVisibility(View.INVISIBLE);
                                btnInput.setVisibility(View.INVISIBLE);
                            }
                            break;
                            case "wait": {
                                linLayout.setVisibility(View.INVISIBLE);
                                txtWait.setVisibility(View.VISIBLE);
                                txtWait.setText(getString(R.string.txt_wait_agree));
                                btnInput.setVisibility(View.INVISIBLE);
                            }
                            break;
                            case "confirm": {
                                linLayout.setVisibility(View.INVISIBLE);
                                btnInput.setVisibility(View.VISIBLE);
                                txtWait.setVisibility(View.VISIBLE);
                                txtWait.setText(getString(R.string.txt_confirm));
                            }
                        }
                        editor.apply();
                    }
                }

                @Override
                public void onFailure(Call<ResponseMessageToken> call, Throwable t) {
                    progBarInVisible();
                    Toast.makeText(getContext(), getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setView(View view) {
        edtName = view.findViewById(R.id.edt_name_insert_shop);
        edtAddress = view.findViewById(R.id.edt_address_insert_shop);
        edtJob = view.findViewById(R.id.edt_job_insert_shop);
        edtMobile = view.findViewById(R.id.edt_mobile_insert_shop);
        edtPhone = view.findViewById(R.id.edt_phone_insert_shop);
        edtShop = view.findViewById(R.id.edt_shop_insert_shop);
        btnApply = view.findViewById(R.id.btn_insertShop_insert_shop);
//        btnSelecPic = findViewById(R.id.btn_selectPic_insert_shop);
        imgAddpic = view.findViewById(R.id.imge_add_pic_insert_shop);
        progBar = view.findViewById(R.id.prog_insert_shop);
        txtMessage = view.findViewById(R.id.txt_notfull_insert_shop);
        imgShop = view.findViewById(R.id.imge_selectPic_insert_shop);
        drawable = imgShop.getDrawable();
        imgDelete = view.findViewById(R.id.imge_delete_insert_shop);
        linLayout = view.findViewById(R.id.lin_layout_insert_shop);
        txtWait = view.findViewById(R.id.txt_wait_agree_insert_shop);
        btnInput = view.findViewById(R.id.btn_input_insert_shop);
    }

    @Override
    public void onClick(View view) {
        if (view.equals(btnApply)) {
            if (edtMobile.getText().toString().isEmpty()
                    || edtShop.getText().toString().isEmpty()
                    || edtJob.getText().toString().isEmpty()
                    || edtName.getText().toString().isEmpty()) {
                txtMessage.setVisibility(View.VISIBLE);
                return;
            }
            txtMessage.setVisibility(View.GONE);
            progBar.setVisibility(View.VISIBLE);
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            clientBuilder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
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
            shop.setAddress(edtAddress.getText().toString().trim());
            shop.setUser(user);
            shop.setName(edtName.getText().toString().trim());
            if (imageSelect) {
                shop.setIsImageShop((short) 1);
            } else if (!imageSelect) {
                shop.setIsImageShop((short) 0);
            }
            shop.setJob(edtJob.getText().toString().trim());
            shop.setShop(edtShop.getText().toString().trim());
            shop.setMobile(edtMobile.getText().toString().trim());
            shop.setPhone(edtPhone.getText().toString().trim());
            RequestShopApiKey requestShopApiKey = new RequestShopApiKey();
            requestShopApiKey.shop = shop;
            Call<ResponseMessageToken> call = retrofitService.insertShop(requestShopApiKey);
            call.enqueue(new Callback<ResponseMessageToken>() {
                @Override
                public void onResponse(Call<ResponseMessageToken> call, retrofit2.Response<ResponseMessageToken> response) {

                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), response.body().message, Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = App.prefUserInfo.edit();
                        editor.putString("token", response.body().token);
                        editor.putBoolean("hasShop", true);
                        editor.apply();
                        SharedPreferences.Editor shopEditor = App.prefShopInfo.edit();
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
                        if (imageSelect) {
                            FragmentInsertShop.Utility utility = new FragmentInsertShop.Utility();
                            utility.imageDir = subDir.getAbsolutePath();
                            utility.imageName = imageName;
                            utility.uri = App.UPLOAD_IMAGE_SHOP_TO_SERVER_URI;
                            new FragmentInsertShop.UploadTask().execute(utility);
                        } else {
                            progBarInVisible();
                            edtAddress.setText("");
                            edtJob.setText("");
                            edtName.setText("");
                            edtMobile.setText("");
                            edtPhone.setText("");
                            edtShop.setText("");
                            imgShop.setImageDrawable(drawable);
                            imgDelete.setVisibility(View.INVISIBLE);
                            imgAddpic.setVisibility(View.VISIBLE);
                            imageSelect = false;
//                            startActivity(new Intent(getContext(), AdminPage.class));
//                            getActivity().finish();
                            linLayout.setVisibility(View.INVISIBLE);
                            txtWait.setVisibility(View.VISIBLE);
                            txtWait.setText(getString(R.string.txt_wait_agree));
                            btnInput.setVisibility(View.INVISIBLE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseMessageToken> call, Throwable t) {
                    progBarInVisible();
                    Toast.makeText(getContext(), getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
                }
            });
        } else if (view.equals(imgAddpic)) {
            Intent picIntent = new Intent(Intent.ACTION_PICK);
            picIntent.setType("image/*");
            startActivityForResult(picIntent, REQ_CODE);
        } else if (view.equals(imgDelete)) {
            imageSelect = false;
            imgShop.setImageDrawable(drawable);
            imgDelete.setVisibility(View.INVISIBLE);
            imgAddpic.setVisibility(View.VISIBLE);
            File file = new File(subDir, imageName);
            if (file.exists() && file.isFile()) {
                file.delete();
            }
        } else if (view.equals(btnInput)) {
            startActivity(new Intent(getContext(), AdminPage.class));
            getActivity().finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE && resultCode == getActivity().RESULT_OK) {
            imgDelete.setVisibility(View.VISIBLE);
            imgAddpic.setVisibility(View.INVISIBLE);
            imageSelect = true;
            Uri uri = data.getData();
            imgShop.setImageURI(uri);
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                ByteArrayOutputStream outputStream = ResizePicture.changeImageSize(inputStream);
                File file = new File(subDir, imageName);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(outputStream.toByteArray());
                fileOutputStream.close();
                outputStream.close();
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
        BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imagePath), null, o);

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
        return BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imagePath), null, o2);

    }

    private class Utility {
        public String imageDir;
        public String imageName;
        public String uri;
    }

    private class UploadTask extends AsyncTask<FragmentInsertShop.Utility, Void, String> {

        @Override
        protected String doInBackground(FragmentInsertShop.Utility... utilities) {
            UploadPicToServerDb uploadPicToServerDb = new UploadPicToServerDb(utilities[0].imageDir, utilities[0].imageName, utilities[0].uri);
            uploadPicToServerDb.uploadStart();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progBarInVisible();
            edtAddress.setText("");
            edtJob.setText("");
            edtName.setText("");
            edtMobile.setText("");
            edtPhone.setText("");
            edtShop.setText("");
            imgShop.setImageDrawable(drawable);
            imgDelete.setVisibility(View.INVISIBLE);
            imgAddpic.setVisibility(View.VISIBLE);
            imageSelect = false;
            File file = new File(subDir, imageName);
            if (file.exists()) {
                file.delete();
            }
//            startActivity(new Intent(getContext(), AdminPage.class));
//            getActivity().finish();
            linLayout.setVisibility(View.INVISIBLE);
            txtWait.setVisibility(View.VISIBLE);
            txtWait.setText(getString(R.string.txt_wait_agree));
            btnInput.setVisibility(View.INVISIBLE);
        }
    }

    private void progBarVisible() {
        try {
            progBar.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void progBarInVisible() {
        try {
            progBar.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
