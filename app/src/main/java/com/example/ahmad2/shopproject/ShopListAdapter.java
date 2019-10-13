package com.example.ahmad2.shopproject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class ShopListAdapter extends RecyclerView.Adapter<ShopListAdapter.MyViewHolder> {

    public interface OnItemClickListener {
        public void onItemClick(Shop shop);
    }

    private final OnItemClickListener listener;
    private List<Shop> shopList;
    private Context context;
    private Activity activity;

    public ShopListAdapter(Activity activity, Context context, List<Shop> shopList, OnItemClickListener onItemClickListener) {
        this.listener = onItemClickListener;
        this.shopList = shopList;
        this.context = context;
        this.activity = activity;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_shoplist_adapter, viewGroup, false);

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

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView txtShop;
        private ImageView imgShop;
        private View itemView;
        private LruCache<String, Bitmap> lruCache;
        private Shop shop;
        private FloatingActionButton fltInfo;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtShop = itemView.findViewById(R.id.txt_item_shop_adapter);
            imgShop = itemView.findViewById(R.id.img_item_shop_adapter);
            fltInfo=itemView.findViewById(R.id.flt_info_item_shop_adapter);
//            fab = itemView.findViewById(R.id.fab_item_shop_adapter);
            this.itemView = itemView;
//            int memory = (int) (Runtime.getRuntime().maxMemory() / 1024);
//            lruCache = new LruCache<>(memory / 10);
        }

        public void bind(final Context context, final Shop shop, final OnItemClickListener listener, final Activity activity) {
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    listener.onItemClick(shop);
//                }
//            });
            App app=new App(context);
            fltInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(shop);
                }
            });
            txtShop.setText(shop.getShop());
            this.shop = shop;
            String uri = App.DOWN_IMAGE_FROM_SERVER_URI + shop.getUser() + ".jpg";
//            fab.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(context, ShowShopInfo.class);
//                    intent.putExtra("shop", shop);
////                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(intent);
//                }
//            });
            if (shop.getIsImageShop() == 1) {
                new PicTask().execute(uri);
//                ImageRequest imageRequest = new ImageRequest(uri,
//                        new Response.Listener<Bitmap>() {
//                            @Override
//                            public void onResponse(final Bitmap response) {
//                                imgShop.setImageBitmap(response);
////                                imgShop.setOnClickListener(new View.OnClickListener() {
////                                    @Override
////                                    public void onClick(View v) {
////                                        Dialog imgDialog = new Dialog(context);
////                                        imgDialog.setContentView(R.layout.layout_image_dialog);
////                                        ImageView img = imgDialog.findViewById(R.id.img_dialog);
////                                        img.setImageBitmap(response);
////                                        changeSize(imgDialog.getWindow(), activity);
////                                        imgDialog.show();
////                                    }
////                                });
//                            }
//                        }, 130, 180, ImageView.ScaleType.FIT_XY,
//                        Bitmap.Config.ARGB_8888,
//                        new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//
//                            }
//                        });
//                app.getRequestQueue().add(imageRequest);



//                final Bitmap bitmap = lruCache.get(shop.getUser());
//                if (bitmap != null) {
//                    imgShop.setImageBitmap(lruCache.get(shop.getUser()));
////                    Dialog imgDialog = new Dialog(context);
////                    imgDialog.setContentView(R.layout.layout_image_dialog);
////                    ImageView img = imgDialog.findViewById(R.id.img_dialog);
////                    img.setImageBitmap(bitmap);
////                    changeSize(imgDialog.getWindow(), activity);
////                    imgDialog.show();
////                    Intent intent = new Intent(context, ShowShopInfo.class);
////                    intent.putExtra("shop", shop);
////                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
////                    context.startActivity(intent);
//
//                } else {
//                    String uri = App.DOWN_IMAGE_FROM_SERVER_URI + shop.getUser() + ".jpg";
//                    new PicTask().execute(uri);
//                }
            }
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
