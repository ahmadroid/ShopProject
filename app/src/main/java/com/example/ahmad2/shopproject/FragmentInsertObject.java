package com.example.ahmad2.shopproject;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatSpinner;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentInsertObject extends Fragment {

    private EditText edtPrice, edtDes, edtObjectName,edtInventory,edtUnit;
    private ImageView imgSelectPic, imageDelete, imgAddPic;
    private TextView txtCode;
    private Button btnInsert, btnNewObject, btnSelectPic;
    //    private AppCompatSpinner spinner;
    private List<String> objectList;
    private ArrayAdapter adapter;
    private App app;
    private ProgressBar progBar;
    private Set<String> set;
    private Set<String> objectSet;
    private static FragmentObjectListForAdmin fragmentObjectListForAdmin;
    private static final int REQ_CODE = 10;
    private boolean imageSelect;
    private Drawable drawable;
    private File subDir;
    private String imageName = "";
    private String token;
    private String myUser;
//    private SharedPreferences prefUserInfo;
//    private SharedPreferences prefObjectList;
    private long price;

    public static FragmentInsertObject newInstance(FragmentObjectListForAdmin fragment) {
        fragmentObjectListForAdmin = fragment;
        FragmentInsertObject newFragment = new FragmentInsertObject();
        return newFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(App.TAG_FRAGMENT,"startFragInsertObject");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(App.TAG_FRAGMENT,"resumeFragInsertObject");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(App.TAG_FRAGMENT,"stopFragInsertObject");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(App.TAG_FRAGMENT,"pauseFragInsertObject");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(App.TAG_FRAGMENT,"destroyFragInsertObject");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("tagOnViewCreate","OnViewCretae");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        price=0;
        Log.i(App.TAG_FRAGMENT,"CFInsertObject");
        subDir = new File(Environment.getExternalStorageDirectory(), "ShopDirPic");
        imageSelect = false;
//        prefObjectList = getContext().getSharedPreferences("objectPre", Context.MODE_PRIVATE);
//        prefUserInfo = getContext().getSharedPreferences("userInfoPre", Context.MODE_PRIVATE);

        imageName = "pic" + ".jpg";
        if (objectSet == null)
            objectSet = new HashSet<>();
        app = new App(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(App.TAG_FRAGMENT,"CVFInsertObject");
        if (App.prefUserInfo != null) {
            myUser = App.prefUserInfo .getString("user", "");
            token = App.prefUserInfo .getString("token", "");
        }
        View view = inflater.inflate(R.layout.activity_insert_object, container, false);
        setView(view);
        drawable = imgSelectPic.getDrawable();

//        getNewCodeObject();
//        getSpinner();
        imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageSelect = false;
                imgSelectPic.setImageDrawable(drawable);
                imageDelete.setVisibility(View.INVISIBLE);
                imgAddPic.setVisibility(View.VISIBLE);
                File file = new File(subDir, imageName);
                if (file.exists() && file.isFile())
                    file.delete();

            }
        });
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtObjectName.getText().toString().isEmpty()) {
                    edtObjectName.setError(getString(R.string.warrning_not_empty));
                    edtObjectName.requestFocus();
                    return;
                } else if (edtPrice.getText().toString().isEmpty()) {
                    edtPrice.setError(getString(R.string.warrning_not_empty));
                    edtPrice.requestFocus();
                    return;
                }
                progBarVisible();
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
                final RequestObjectApiKey requestObjectApiKey = new RequestObjectApiKey();
                ObjectInfo objectInfo = new ObjectInfo();
                objectInfo.setName(edtObjectName.getText().toString().trim());
                try {
                    objectInfo.setPrice(price);
                    objectInfo.setInventory(Long.valueOf(edtInventory.getText().toString().trim()));
                }catch (Exception e){
                    Log.i("tagException",e.getMessage());
                    objectInfo.setPrice(0);
                    objectInfo.setInventory(0);
                }
                objectInfo.setUnit(edtUnit.getText().toString().trim());
                objectInfo.setDescription(edtDes.getText().toString());
                objectInfo.setUser(myUser);
                if (imageSelect)
                    objectInfo.setIsImageSelect((short) 1);
                else if (!imageSelect) {
                    objectInfo.setIsImageSelect((short) 0);
                }
                requestObjectApiKey.object = objectInfo;
                Call<ResponseMessageToken> call = retrofitService.insertObject(requestObjectApiKey);
                call.enqueue(new Callback<ResponseMessageToken>() {
                    @Override
                    public void onResponse(Call<ResponseMessageToken> call, retrofit2.Response<ResponseMessageToken> response) {

                        if (response.isSuccessful()) {
                            SharedPreferences.Editor editor = App.prefUserInfo .edit();
                            editor.putString("token", response.body().token);
                            editor.apply();
                            Toast.makeText(getContext(), response.body().message, Toast.LENGTH_SHORT).show();
                            if (imageSelect) {
                                Utility utility = new Utility();
                                utility.dir = subDir.getAbsolutePath();
                                utility.fileNmae = imageName;
                                utility.url = App.UPLOAD_IMAGE_TO_SERVER_URI;
                                new UploadTask().execute(utility);
                            } else {
                                progBarInVisible();
//                                App.fragmentObjectListForUser.getObjectList();
//                                fragmentObjectListForAdmin.getObjectList();
                                App.fragmentStore.getObjectList();
//                            getNewCodeObject();
                                edtDes.setText("");
                                edtPrice.setText("0");
                                edtObjectName.setText("");
                                edtInventory.setText("0");
                                edtUnit.setText("");
                                imageSelect = false;
                                imageDelete.setVisibility(View.INVISIBLE);
                                imgAddPic.setVisibility(View.VISIBLE);
                                imgSelectPic.setImageDrawable(drawable);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseMessageToken> call, Throwable t) {
                        progBarInVisible();
                        Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


//                StringRequest request = new StringRequest(Request.Method.POST, App.INSERT_OBJECT_URI,
//                        new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response) {
//                                progBar.setVisibility(View.INVISIBLE);
//                                Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
//                                if (imageSelect) {
//                                    Utility utility = new Utility();
//                                    utility.dir = subDir.getAbsolutePath();
//                                    utility.fileNmae = imageName;
//                                    utility.url = App.UPLOAD_IMAGE_TO_SERVER_URI;
//                                    new UploadTask().execute(utility);
//                                }
//                                fragmentObjectListForAdmin.getObjectList();
//                                getNewCodeObject();
//                                edtDes.setText("");
//                                edtPrice.setText("");
//                                imageSelect = false;
//                                imageDelete.setVisibility(View.INVISIBLE);
//                                imgSelectPic.setImageDrawable(drawable);
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
//                        params.put("name", spinner.getSelectedItem().toString());
//                        params.put("price", edtPrice.getText().toString());
//                        params.put("description", edtDes.getText().toString());
//                        if (imageSelect)
//                            params.put("image", "1");
//                        else if (!imageSelect)
//                            params.put("image", "0");
//                        return params;
//                    }
//                };
//                app.getRequestQueue().add(request);
            }
        });
//        btnSelectPic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent picIntent = new Intent(Intent.ACTION_PICK);
//                picIntent.setType("image/*");
//                startActivityForResult(picIntent, REQ_CODE);
//            }
//        });
        imgAddPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picIntent = new Intent(Intent.ACTION_PICK);
                picIntent.setType("image/*");
                startActivityForResult(picIntent, REQ_CODE);
            }
        });
        btnNewObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.layout_insert_object_for_spin);
                if (dialog.getWindow() != null)
                    changeView(dialog.getWindow());
                Button btnInsertObject = dialog.findViewById(R.id.btn_object_for_spin);
                final EditText edtInsertObject = dialog.findViewById(R.id.edt_object_for_spin);
                Button btnExit = dialog.findViewById(R.id.btn_extt_for_spin);
                btnInsertObject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Set<String> saveSet = new HashSet<>();
                        if (App.prefObjectList != null) {
                            saveSet = App.prefObjectList.getStringSet("objectlist", new HashSet<String>());
                            if (saveSet != null)
                                for (String str : saveSet)
                                    objectSet.add(str);
                            if (edtInsertObject.getText().toString().isEmpty()) {
                                edtInsertObject.setError(getContext().getResources().getString(R.string.warrning_input_objectName));
                                return;
                            }
                            objectSet.add(edtInsertObject.getText().toString());
                            SharedPreferences.Editor editor = App.prefObjectList.edit();
                            editor.putStringSet("objectlist", objectSet);
                            editor.apply();
                        }
//                        FragmentInsertObject fragmentInsertObject = (FragmentInsertObject) viewPager.getAdapter().
//                                instantiateItem(viewPager, 2);
//                        getSpinner();

                        dialog.dismiss();
                    }
                });
                btnExit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });
        return view;
    }

    private void changeView(Window window) {
        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        window.setLayout(size.x, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

//    public void getSpinner() {
//        prefObjectList = getContext().getSharedPreferences("objectPre", Context.MODE_PRIVATE);
//        objectList = new ArrayList<>();
//        set = new HashSet<>();
//        set = prefObjectList.getStringSet("objectlist", new HashSet<String>());
//        if (set != null && set.size() == 0) {
//            Toast.makeText(getContext(), getContext().getResources().getString(R.string.empty_objectList), Toast.LENGTH_SHORT).show();
//            return;
//        }
//        for (String str : set) {
//            objectList.add(str);
//        }
//        adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, objectList);
//        spinner.setAdapter(adapter);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UploadPicToServerDb uploadPicToServerDb = null;
        if (requestCode == REQ_CODE && resultCode == getActivity().RESULT_OK) {
            if (data != null && data.getData() != null) {
                imageDelete.setVisibility(View.VISIBLE);
                imgAddPic.setVisibility(View.INVISIBLE);
                imageSelect = true;
                File file;
                try {
                    Uri imagePath = data.getData();
                    imgSelectPic.setImageURI(imagePath);
                    InputStream inputStream = getContext().getContentResolver().openInputStream(imagePath);
                    ByteArrayOutputStream outputStream = ResizePicture.changeImageSize(inputStream);
                    file = new File(subDir, imageName);
//                Bitmap bitmap=BitmapFactory.decodeFile(file.getAbsolutePath());
//                imgSelectPic.setImageBitmap(bitmap);
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
    }

    private Bitmap decodeUri(Uri imagePath) throws FileNotFoundException {

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(imagePath), null, o);

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
        return BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(imagePath), null, o2);

    }

    private void setView(View view) {
        imageDelete = view.findViewById(R.id.imge_delete_insert);
//        spinner = view.findViewById(R.id.spin_object_name_insert);
        edtObjectName = view.findViewById(R.id.edt_object_name_insert);
        imgAddPic = view.findViewById(R.id.imge_add_pic_insert);
        progBar = view.findViewById(R.id.prog_insert);
        edtInventory=view.findViewById(R.id.edt_inventory_insert);
        edtPrice = view.findViewById(R.id.edt_price_insert);
        edtUnit=view.findViewById(R.id.edt_unit_insert);
        edtPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !edtPrice.getText().toString().isEmpty()) {
                    price = Long.valueOf(edtPrice.getText().toString().trim());
                    Locale farsiLoc = new Locale("fa");
                    NumberFormat numberFormat = NumberFormat.getNumberInstance(farsiLoc);
                    edtPrice.setText(numberFormat.format(price));
                }
                if (hasFocus){
                    edtPrice.setText(String.valueOf(price));
                }
            }
        });
        btnSelectPic = view.findViewById(R.id.btn_selectPic_insert);
        edtDes = view.findViewById(R.id.edt_description_insert);
        txtCode = view.findViewById(R.id.txt_codeobject_insert);
        btnInsert = view.findViewById(R.id.btn_insert_insert);
        btnNewObject = view.findViewById(R.id.btn_newObject_insert);
        imgSelectPic = view.findViewById(R.id.imge_selectPic_insert);

    }

    private class Utility {
        public String dir;
        public String fileNmae;
        public String url;
    }

    private class UploadTask extends AsyncTask<Utility, Void, String> {

        @Override
        protected String doInBackground(Utility... utilities) {
            Utility utility = utilities[0];
            UploadPicToServerDb uploadPicToServerDb = new UploadPicToServerDb(utility.dir, utility.fileNmae, utility.url);
            uploadPicToServerDb.uploadStart();
            return "uploadImage";
        }

        @Override
        protected void onPostExecute(String s) {
            progBarInVisible();
//            fragmentObjectListForAdmin.getObjectList();
//            App.fragmentObjectListForUser.getObjectList();
            App.fragmentStore.getObjectList();
//                            getNewCodeObject();
            edtDes.setText("");
            edtPrice.setText("0");
            edtObjectName.setText("");
            edtInventory.setText("0");
            edtUnit.setText("");
            imageSelect = false;
            imageDelete.setVisibility(View.INVISIBLE);
            imgAddPic.setVisibility(View.VISIBLE);
            imgSelectPic.setImageDrawable(drawable);
            File file = new File(subDir, imageName);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private void progBarVisible(){
        try{
            progBar.setVisibility(View.VISIBLE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void progBarInVisible(){
        try{
            progBar.setVisibility(View.INVISIBLE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
