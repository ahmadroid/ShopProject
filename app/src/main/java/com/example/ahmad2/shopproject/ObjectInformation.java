package com.example.ahmad2.shopproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ObjectInformation extends MyActivity implements View.OnClickListener {

    private EditText edtPrice, edtDes,edtInventory,edtUnit;
    private TextView txtCode, txtName;
    private Button btnEdit, btnDelete;
    private ImageView objectImage, imageDelete,imgAddPic;
    private ObjectInfo object;
    private App app;
    private ProgressBar progBar, progBarDialog;
    private static final int REQ_CODE = 12;
    private boolean imageSelect, oldImageSelect;
    private Drawable drawable;
    private File subDir;
    private String imageName = "";
    private SharedPreferences preferences;
    private String token;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_information);
        preferences = getSharedPreferences("userInfoPre", MODE_PRIVATE);
        if (preferences!=null) {
            token = preferences.getString("token", "");
            user = preferences.getString("user", "");
        }
        subDir = new File(Environment.getExternalStorageDirectory(), "ShopDirPic");
        imageSelect = false;
        oldImageSelect = false;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        app = new App(this);
        setView();
        drawable = objectImage.getDrawable();
        Bundle extras = getIntent().getExtras();
        object = new ObjectInfo();
        if (extras != null) {
            object = extras.getParcelable("objectInfo");
            edtPrice.setText(String.valueOf(object.getPrice()));
            edtInventory.setText(String.valueOf(object.getInventory()));
            edtUnit.setText(object.getUnit());
            txtName.setText(object.getName());
            txtCode.setText(String.valueOf(object.getCodeObject()));
            if (object.getIsImageSelect() == 1) {
                getBeforeImage();
                oldImageSelect = true;
                imageSelect = true;
                imageName = user + txtCode.getText().toString().trim() + ".jpg";
                imageDelete.setVisibility(View.VISIBLE);
                imgAddPic.setVisibility(View.INVISIBLE);
            } else if (object.getIsImageSelect() == 0) {
                imageDelete.setVisibility(View.INVISIBLE);
                imgAddPic.setVisibility(View.VISIBLE);
                imageSelect = false;
            }
        }

        imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageSelect = false;
                objectImage.setImageDrawable(drawable);
                imageDelete.setVisibility(View.INVISIBLE);
                imgAddPic.setVisibility(View.VISIBLE);
                File file = new File(subDir, imageName);
                if (file.exists() && file.isFile())
                    file.delete();
            }
        });

        imgAddPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imageIntent = new Intent(Intent.ACTION_PICK);
                imageIntent.setType("image/*");
                startActivityForResult(imageIntent, REQ_CODE);
            }
        });
    }

    private void getBeforeImage() {
        progBar.setVisibility(View.VISIBLE);
        String url = App.DOWN_IMAGE_FROM_SERVER_URI + user + txtCode.getText().toString().trim() + ".jpg";
        ImageRequest imageRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        progBar.setVisibility(View.INVISIBLE);
                        objectImage.setImageBitmap(response);
                    }
                }, 60, 60, ImageView.ScaleType.FIT_XY,
                Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(ObjectInformation.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        app.getRequestQueue().add(imageRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE && resultCode == RESULT_OK) {
            imageDelete.setVisibility(View.VISIBLE);
            imgAddPic.setVisibility(View.INVISIBLE);
            imageSelect = true;
            File file;
            try {
                Uri imageUri = data.getData();
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                objectImage.setImageURI(imageUri);
                imageName = user + txtCode.getText().toString().trim() + ".jpg";
                file = new File(subDir, imageName);
                ByteArrayOutputStream outputStream = ResizePicture.changeImageSize(inputStream);
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
//                buffer.close();
//                fileOutputStream.close();

//                UploadPicToServerDb uploadPicToServerDb=new UploadPicToServerDb(subDir.getAbsolutePath(),imageName,App.UPLOAD_IMAGE_TO_SERVER_URI);
//                uploadPicToServerDb.uploadStart();
//                if (file.exists() && file.isFile())
//                    file.delete();
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

    private void setView() {
        imageDelete = findViewById(R.id.imge_delete_object_information);
        imgAddPic=findViewById(R.id.imge_add_pic_object_information);
//        btnSelectPic = findViewById(R.id.btn_selectPic_object_information);
        objectImage = findViewById(R.id.imge_selectPic_object_information);
        progBar = findViewById(R.id.prog_object_information);
        txtCode = findViewById(R.id.txt_code_object_information);
        txtName = findViewById(R.id.txt_name_object_information);
        edtPrice = findViewById(R.id.edt_price_object_information);
        edtUnit=findViewById(R.id.edt_unit_object_information);
        edtDes = findViewById(R.id.edt_description_object_information);
        btnDelete = findViewById(R.id.btn_delete_object_information);
        btnDelete.setOnClickListener(this);
        btnEdit = findViewById(R.id.btn_edit_object_information);
        btnEdit.setOnClickListener(this);
        edtInventory=findViewById(R.id.edt_inventory_object_information);
    }

    public void onClick(View view) {

        if (view.equals(btnEdit)) {
            progBar.setVisibility(View.VISIBLE);
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
            RequestObjectApiKey requestObjectApiKey = new RequestObjectApiKey();
            ObjectInfo objectInfo = new ObjectInfo();
            objectInfo.setUser(user);
            objectInfo.setCodeObject(Long.valueOf(txtCode.getText().toString().trim()));
            objectInfo.setName(txtName.getText().toString().trim());
            objectInfo.setUnit(edtUnit.getText().toString().trim());
            try {
                objectInfo.setPrice(Integer.valueOf(edtPrice.getText().toString().trim()));
                objectInfo.setInventory(Long.valueOf(edtInventory.getText().toString().trim()));
            }catch(Exception e){
                objectInfo.setInventory(0);
                objectInfo.setPrice(0);
            }
            objectInfo.setDescription(edtDes.getText().toString().trim());
            if (imageSelect)
                objectInfo.setIsImageSelect((short) 1);
            else if (!imageSelect)
                objectInfo.setIsImageSelect((short) 0);
            requestObjectApiKey.object = objectInfo;
            Call<ResponseMessageToken> call = retrofitService.updateObject(requestObjectApiKey);
            call.enqueue(new Callback<ResponseMessageToken>() {
                @Override
                public void onResponse(Call<ResponseMessageToken> call, retrofit2.Response<ResponseMessageToken> response) {

                    if (response.isSuccessful()) {
                        SharedPreferences.Editor editor=preferences.edit();
                        editor.putString("token",response.body().token);
                        editor.apply();
                        Toast.makeText(ObjectInformation.this, response.body().message, Toast.LENGTH_SHORT).show();
                        if (!oldImageSelect && imageSelect) {
                            Utility utility = new Utility();
                            utility.dir = subDir.getAbsolutePath();
                            utility.fileName = imageName;
                            utility.url = App.UPLOAD_IMAGE_SHOP_TO_SERVER_URI;
                            new UploadTask().execute(utility);
                        }else {
                            App.fragmentObjectListForAdmin.getObjectList();
                            App.fragmentStore.getObjectList();
                            progBar.setVisibility(View.INVISIBLE);
                        }

                    }
                }

                @Override
                public void onFailure(Call<ResponseMessageToken> call, Throwable t) {
                    progBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(ObjectInformation.this, getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
                }
            });


//            StringRequest request = new StringRequest(Request.Method.POST, App.EDIT_OBJECT_URI,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            Toast.makeText(ObjectInformation.this, response, Toast.LENGTH_SHORT).show();
//                            if (!oldImageSelect && imageSelect){
//                                Utility utility=new Utility();
//                                utility.dir=subDir.getAbsolutePath();
//                                utility.fileName=imageName;
//                                utility.url=App.UPLOAD_IMAGE_TO_SERVER_URI;
//                                new UploadTask().execute(utility);
//                            }
//                            App.fragmentObjectListForAdmin.getObjectList();
//                            progBar.setVisibility(View.INVISIBLE);
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
//                    progBar.setVisibility(View.INVISIBLE);
//                }
//            }) {
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String, String> params = new HashMap<>();
//                    params.put("codeobject", txtCode.getText().toString());
//                    params.put("name", txtName.getText().toString());
//                    params.put("price", edtPrice.getText().toString());
//                    params.put("description", edtDes.getText().toString());
//                    if (imageSelect)
//                        params.put("image", "1");
//                    else if (!imageSelect)
//                        params.put("image", "0");
//                    if (oldImageSelect && !imageSelect){
//                        params.put("imagename",imageName);
//                    }
//                    return params;
//                }
//            };
//            app.getRequestQueue().add(request);


        } else if (view.equals(btnDelete)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.warrning_object_delete)).
                    setTitle(getString(R.string.notic_object_delete)).
                    setCancelable(false).
                    setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progBar.setVisibility(View.VISIBLE);
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
                            RequestObjectApiKey requestObjectApiKey = new RequestObjectApiKey();
                            ObjectInfo objectInfo = new ObjectInfo();
                            objectInfo.setUser(user);
                            objectInfo.setCodeObject(Long.valueOf(txtCode.getText().toString().trim()));
                            requestObjectApiKey.object = objectInfo;
                            Call<ResponseMessageToken> call = retrofitService.deleteObject(requestObjectApiKey);
                            call.enqueue(new Callback<ResponseMessageToken>() {
                                @Override
                                public void onResponse(Call<ResponseMessageToken> call, retrofit2.Response<ResponseMessageToken> response) {
                                    progBar.setVisibility(View.INVISIBLE);
                                    if (response.isSuccessful()) {
                                        SharedPreferences.Editor editor=preferences.edit();
                                        editor.putString("token",response.body().token);
                                        editor.apply();
                                        Toast.makeText(ObjectInformation.this, response.body().message, Toast.LENGTH_SHORT).show();
                                        App.fragmentObjectListForAdmin.getObjectList();
                                        App.fragmentStore.getObjectList();
                                        finish();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseMessageToken> call, Throwable t) {
                                    progBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(ObjectInformation.this, getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
                                }
                            });








//                            StringRequest request = new StringRequest(Request.Method.POST, App.DELETE_OBJECT_URI
//                                    , new Response.Listener<String>() {
//                                @Override
//                                public void onResponse(String response) {
//                                    Toast.makeText(ObjectInformation.this, response, Toast.LENGTH_SHORT).show();
//                                    App.fragmentObjectListForAdmin.getObjectList();
//                                    progBar.setVisibility(View.INVISIBLE);
//                                }
//                            }, new Response.ErrorListener() {
//                                @Override
//                                public void onErrorResponse(VolleyError error) {
//                                    Toast.makeText(ObjectInformation.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//                                    progBar.setVisibility(View.INVISIBLE);
//                                }
//                            }) {
//                                @Override
//                                protected Map<String, String> getParams() throws AuthFailureError {
//                                    Map<String, String> params = new HashMap<>();
//                                    params.put("codeobject", txtCode.getText().toString());
//                                    if (imageSelect) {
//                                        params.put("imagename", imageName);
//                                    }
//                                    return params;
//                                }
//                            };
//                            app.getRequestQueue().add(request);
                        }
                    }).
                    setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private class Utility {
        public String dir;
        public String fileName;
        public String url;
    }

    private class UploadTask extends AsyncTask<Utility, Void, String> {

        @Override
        protected String doInBackground(Utility... utilities) {
            Utility utility = utilities[0];
            UploadPicToServerDb uploadPicToServerDb = new UploadPicToServerDb(utility.dir, utility.fileName, utility.url);
            uploadPicToServerDb.uploadStart();
            return "uploadImage";
        }

        @Override
        protected void onPostExecute(String s) {
            App.fragmentObjectListForAdmin.getObjectList();
            App.fragmentStore.getObjectList();
            progBar.setVisibility(View.INVISIBLE);
        }
    }
}
