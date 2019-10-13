package com.example.ahmad2.shopproject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ObjectListForUserAdapterAgree extends RecyclerView.Adapter<ObjectListForUserAdapterAgree.FavViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(ObjectInfo objectInfo);
    }

    private final Context context;
    private Activity activity;
    private final List<ObjectInfo> objectList;
    private final OnItemClickListener listener;

    public ObjectListForUserAdapterAgree(Context context, Activity activity, List<ObjectInfo> objectList, OnItemClickListener listener) {
        this.listener = listener;
        this.objectList = objectList;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public FavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_for_object_list_for_user_agree, parent, false);
        return new FavViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavViewHolder holder, int position) {

        holder.bind(objectList.get(position), listener,  activity);
    }

    @Override
    public int getItemCount() {
        return objectList.size();
    }

    public class FavViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {

        private TextView txtName, txtPrice, txtDes;
        private ImageView objImg;
        private App app;
        private LruCache<Long, Bitmap> imageCache;
        private int maxMemory;
        private ObjectInfo objectInfo;
        private FloatingActionButton fltDelete;
        private CheckBox chkAgree;
        private String user="",token="";

        public FavViewHolder(@NonNull View itemView) {
            super(itemView);
            objImg = itemView.findViewById(R.id.img_item_object_list_for_user_agree);
            txtName = itemView.findViewById(R.id.txt_name_item_object_list_for_user_agree);
            txtName.setMovementMethod(new ScrollingMovementMethod());
            txtPrice = itemView.findViewById(R.id.txt_price_item_object_list_for_user_agree);
            txtDes = itemView.findViewById(R.id.txt_description_item_object_list_for_user_agree);
            fltDelete =itemView.findViewById(R.id.flt_delete_item_object_list_for_user_agree);
            chkAgree=itemView.findViewById(R.id.chkbox_item_object_list_for_user_agree);
            txtDes.setMovementMethod(new ScrollingMovementMethod());
//            maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        }

        public void bind(final ObjectInfo objectInfo, final OnItemClickListener listener, final Activity activity) {

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    listener.onItemClick(objectInfo);
//                }
//            });
//            if (objectInfo.getIsSell()==1) {
                fltDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(objectInfo);
                    }
                });
//                imageCache = new LruCache<>(maxMemory / 5);
                app = new App(context);
                String uri = App.DOWN_IMAGE_FROM_SERVER_URI + objectInfo.getUser() + String.valueOf(objectInfo.getCodeObject()) + ".jpg";
                Locale farsiLoc = new Locale("fa");
                NumberFormat numberFormat = NumberFormat.getNumberInstance(farsiLoc);
                txtPrice.setText(numberFormat.format(objectInfo.getPrice()));
                txtName.setText(objectInfo.getName());
                txtDes.setText(objectInfo.getDescription());
                this.objectInfo = objectInfo;
                if (objectInfo.getIsAgree()==0){
                    chkAgree.setChecked(false);
                }else if (objectInfo.getIsAgree()==1){
                    chkAgree.setChecked(true);
                }
                chkAgree.setOnCheckedChangeListener(this);
                if (objectInfo.getIsImageSelect() == 1) {
                    Log.i("tagURI",uri+"agree");
                    new PicTask().execute(uri);
//                    ImageRequest imageRequest = new ImageRequest(uri,
//                            new com.android.volley.Response.Listener<Bitmap>() {
//                                @Override
//                                public void onResponse(final Bitmap response) {
//                                    objImg.setImageBitmap(response);
//                                    objImg.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            Dialog imgDialog = new Dialog(context);
//                                            imgDialog.setContentView(R.layout.layout_image_dialog);
//                                            ImageView img = imgDialog.findViewById(R.id.img_dialog);
//                                            img.setImageBitmap(response);
//                                            changeSize(imgDialog.getWindow(), activity);
//                                            imgDialog.show();
//                                        }
//                                    });
//                                }
//                            }, 130, 180, ImageView.ScaleType.FIT_XY,
//                            Bitmap.Config.ARGB_8888,
//                            new com.android.volley.Response.ErrorListener() {
//                                @Override
//                                public void onErrorResponse(VolleyError error) {
//
//                                }
//                            });
//                    app.getRequestQueue().add(imageRequest);



//                    final Bitmap bitmap = imageCache.get(objectInfo.getCodeObject());
//                    if (bitmap == null) {
//                        new PicTask().execute(uri);
//                    } else {
//                        objImg.setImageBitmap(bitmap);
//
//                        objImg.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Dialog imgDialog = new Dialog(context);
//                                imgDialog.setContentView(R.layout.layout_image_dialog);
//                                ImageView img = imgDialog.findViewById(R.id.img_dialog);
//                                img.setImageBitmap(bitmap);
//                                changeSize(imgDialog.getWindow(), activity);
//                                imgDialog.show();
//                            }
//                        });
//                    }
                }
//            }
        }

        private void changeSize(Window window, Activity activity) {
            Point size = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(size);
            window.setLayout((int) (size.x * 0.8), (int) (size.y * 0.8));
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            final SharedPreferences preferences=context.getSharedPreferences("userInfoPre",Context.MODE_PRIVATE);
            if (preferences!=null){
                user=preferences.getString("user","");
                token=preferences.getString("token","");
            }
            OkHttpClient.Builder clientBuilder=new OkHttpClient.Builder();
            clientBuilder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request request=original.newBuilder()
                            .addHeader("Content-Type","application/json")
                            .addHeader("shop-token",token)
                            .method(original.method(),original.body())
                            .build();
                    return chain.proceed(request);
                }
            });
            OkHttpClient client = clientBuilder.build();
            Retrofit retrofit=new Retrofit.Builder()
                    .baseUrl(App.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
            RetrofitService retrofitService=retrofit.create(RetrofitService.class);
            RequestLoginObject requestLoginObject=new RequestLoginObject();
            Login login=new Login();
            login.user=user;
            requestLoginObject.login=login;
            if (isChecked){
                objectInfo.setIsAgree((short) 1);
            }else{
                objectInfo.setIsAgree((short) 0);
            }
            requestLoginObject.objectInfo=objectInfo;
            Call<ResponseMessageToken> call = retrofitService.updateObjectAgree(requestLoginObject);
            call.enqueue(new Callback<ResponseMessageToken>() {
                @Override
                public void onResponse(Call<ResponseMessageToken> call, retrofit2.Response<ResponseMessageToken> response) {
                    if (response.isSuccessful()){
                        SharedPreferences.Editor editor=preferences.edit();
                        editor.putString("token",response.body().token);
                        editor.apply();
                        Toast.makeText(context, response.body().message, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseMessageToken> call, Throwable t) {
                    Toast.makeText(context,t.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }

        private class PicTask extends AsyncTask<String, Void, Bitmap> {

            @Override
            protected Bitmap doInBackground(String... strings) {
                Bitmap pic1 = getPic1(strings[0]);
                return pic1;
            }

            @Override
            protected void onPostExecute(final Bitmap bitmap) {
                if (bitmap != null) {
                    objImg.setImageBitmap(bitmap);
//                    imageCache.put(objectInfo.getCodeObject(), bitmap);
//                    objImg.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Dialog imgDialog = new Dialog(context);
//                            imgDialog.setContentView(R.layout.layout_image_dialog);
//                            ImageView img = imgDialog.findViewById(R.id.img_dialog);
//                            img.setImageBitmap(bitmap);
//                            changeSize(imgDialog.getWindow(), activity);
//                            imgDialog.show();
//                        }
//                    });
                }
            }


        }

        private Bitmap getPic1(String uri) {
            try {
                URL url = new URL(uri);
                InputStream inputStream= (InputStream) url.getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
