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
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShopListAdapterAgree extends RecyclerView.Adapter<ShopListAdapterAgree.MyViewHolder> {

    public interface OnItemClickListener {
        public void onItemClick(Shop shop);
        public void onDeleteItemClick(Shop shop);
    }

    private final OnItemClickListener listener;
    private List<Shop> shopList;
    private Context context;
    private Activity activity;

    public ShopListAdapterAgree(Activity activity, Context context, List<Shop> shopList, OnItemClickListener onItemClickListener) {
        this.listener = onItemClickListener;
        this.shopList = shopList;
        this.context = context;
        this.activity = activity;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_shoplist_adapter_agree, viewGroup, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.bind(context, shopList.get(i), listener, activity);
    }

    @Override
    public int getItemCount() {
        return shopList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {

        private TextView txtShop;
        private ImageView imgShop;
        private View itemView;
        private LruCache<String, Bitmap> lruCache;
        private Shop shop;
        private FloatingActionButton fltInfo,fltDelete;
        private CheckBox chkAgree;
        private String user="",token="";

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtShop = itemView.findViewById(R.id.txt_item_shop_adapter_agree);
            imgShop = itemView.findViewById(R.id.img_item_shop_adapter_agree);
            fltInfo=itemView.findViewById(R.id.flt_info_item_shop_adapter_agree);
            fltDelete=itemView.findViewById(R.id.flt_delete_item_shop_adapter_agree);
            chkAgree=itemView.findViewById(R.id.chkbox_item_shop_adapter_agree);
//            fab = itemView.findViewById(R.id.fab_item_shop_adapter);
            this.itemView = itemView;
            int memory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            lruCache = new LruCache<>(memory / 10);
        }

        public void bind(final Context context, final Shop shop, final OnItemClickListener listener, final Activity activity) {
            fltInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(shop);
                }
            });
            fltDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeleteItemClick(shop);
                }
            });
            txtShop.setText(shop.getShop());
            if (shop.getIsShop()==0){
                chkAgree.setChecked(false);
            }else if (shop.getIsShop()==1){
                chkAgree.setChecked(true);
            }
            App app=new App(context);
            String uri = App.DOWN_IMAGE_FROM_SERVER_URI + shop.getUser() + ".jpg";
            chkAgree.setOnCheckedChangeListener(this);
            this.shop = shop;
            if (shop.getIsImageShop() == 1) {
                new PicTask().execute(uri);
//                ImageRequest imageRequest = new ImageRequest(uri,
//                        new com.android.volley.Response.Listener<Bitmap>() {
//                            @Override
//                            public void onResponse(final Bitmap response) {
//                                imgShop.setImageBitmap(response);
//                                imgShop.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        Dialog imgDialog = new Dialog(context);
//                                        imgDialog.setContentView(R.layout.layout_image_dialog);
//                                        ImageView img = imgDialog.findViewById(R.id.img_dialog);
//                                        img.setImageBitmap(response);
//                                        changeSize(imgDialog.getWindow(), activity);
//                                        imgDialog.show();
//                                    }
//                                });
//                            }
//                        }, 130, 180, ImageView.ScaleType.FIT_XY,
//                        Bitmap.Config.ARGB_8888,
//                        new com.android.volley.Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//
//                            }
//                        });
//                app.getRequestQueue().add(imageRequest);


//                final Bitmap bitmap = lruCache.get(shop.getUser());
//                if (bitmap != null) {
//                    imgShop.setImageBitmap(lruCache.get(shop.getUser()));
//                } else {
//                    String uri = App.DOWN_IMAGE_FROM_SERVER_URI + shop.getUser() + ".jpg";
//                    new PicTask().execute(uri);
//                }
            }
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
            RequestLoginShop requestLoginShop=new RequestLoginShop();
            Login login=new Login();
            login.user=user;
            requestLoginShop.login=login;
            if (isChecked){
                shop.setIsShop((short) 1);
            }else{
                shop.setIsShop((short) 0);
            }
            requestLoginShop.shop=shop;
            Call<ResponseMessageToken> call = retrofitService.updateShopAgree(requestLoginShop);
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
                    Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private class PicTask extends AsyncTask<String, Void, Bitmap> {


            @Override
            protected Bitmap doInBackground(String... strings) {
                Bitmap picHttp = getPicHttp(strings[0]);
                return picHttp;
            }

            @Override
            protected void onPostExecute(final Bitmap bitmap) {
                if (bitmap != null) {
                    imgShop.setImageBitmap(bitmap);
//                    lruCache.put(shop.getUser(), bitmap);
//                    imgShop.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
////                            Dialog imgDialog = new Dialog(context);
////                            imgDialog.setContentView(R.layout.layout_image_dialog);
////                            ImageView img = imgDialog.findViewById(R.id.img_dialog);
////                            img.setImageBitmap(bitmap);
////                            changeSize(imgDialog.getWindow(), activity);
////                            imgDialog.show();
////                            Intent intent = new Intent(context, ShowShopInfo.class);
////                            intent.putExtra("shop", shop);
////                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
////                            context.startActivity(intent);
//                        }
//                    });
                }
            }
        }

        private void changeSize(Window window, Activity activity) {
            Point size = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(size);
            window.setLayout((int) (size.x * 0.8), (int) (size.y * 0.5));
        }

        private Bitmap getPicHttp(String uri) {
            try {
                URL url = new URL(uri);
                InputStream inputStream = (InputStream) url.getContent();
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
