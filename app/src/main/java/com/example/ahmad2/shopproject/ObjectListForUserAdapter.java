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
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
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
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ObjectListForUserAdapter extends RecyclerView.Adapter<ObjectListForUserAdapter.FavViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(ObjectInfo objectInfo);
    }

    private final Context context;
    private Activity activity;
    private final List<ObjectInfo> objectList;
    private final OnItemClickListener listener;

    public ObjectListForUserAdapter(Context context, Activity activity, List<ObjectInfo> objectList, OnItemClickListener listener) {

        this.listener = listener;
        this.objectList = objectList;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public FavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_for_object_list_for_user, parent, false);
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

    public class FavViewHolder extends RecyclerView.ViewHolder {

        private TextView txtName, txtPrice, txtDes;
        private ImageView objImg;
        private App app;
        private LruCache<Long, Bitmap> imageCache;
        private int maxMemory;
        private ObjectInfo objectInfo;
        private FloatingActionButton fltIns;

        public FavViewHolder(@NonNull View itemView) {
            super(itemView);
            objImg = itemView.findViewById(R.id.img_item_object_list_for_user);
            txtName = itemView.findViewById(R.id.txt_name_item_object_list_for_user);
            txtName.setMovementMethod(new ScrollingMovementMethod());
            txtPrice = itemView.findViewById(R.id.txt_price_item_object_list_for_user);
            txtDes = itemView.findViewById(R.id.txt_description_item_object_list_for_user);
            fltIns =itemView.findViewById(R.id.flt_btn_item_object_list_for_user);
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
                fltIns.setOnClickListener(new View.OnClickListener() {
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

//            ImageRequest imgRequest = new ImageRequest(uri,
//                    new Response.Listener<Bitmap>() {
//                        @Override
//                        public void onResponse(Bitmap response) {
//                            objImg.setImageBitmap(response);
////                            Toast.makeText(context, "imagerequest", Toast.LENGTH_SHORT).show();
////                                objectInfo.setBitmap(response);
////                            imageCache.put(objectInfo.getCodeObject(), response);
//                        }
//                    }, 60, 60, ImageView.ScaleType.FIT_XY,
//                    Bitmap.Config.ARGB_8888,
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                        }
//                    });
//            app.getRequestQueue().add(imgRequest);
                if (objectInfo.getIsImageSelect() == 1) {
                    new PicTask().execute(uri);
//                    ImageRequest imageRequest = new ImageRequest(uri,
//                            new Response.Listener<Bitmap>() {
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
//                            new Response.ErrorListener() {
//                                @Override
//                                public void onErrorResponse(VolleyError error) {
//
//                                }
//                            });
//                    app.getRequestQueue().add(imageRequest);


//                    final Bitmap bitmap = imageCache.get(objectInfo.getCodeObject());
//                    if (bitmap == null) {
//                        new PicTask().execute(uri);
////                    ImageRequest imgRequest = new ImageRequest(uri,
////                            new Response.Listener<Bitmap>() {
////                                @Override
////                                public void onResponse(final Bitmap response) {
////                                    objImg.setImageBitmap(response);
////                                    objImg.setOnClickListener(new View.OnClickListener() {
////                                        @Override
////                                        public void onClick(View v) {
////                                            Dialog imgDialog=new Dialog(context);
////                                            imgDialog.setContentView(R.layout.layout_image_dialog);
////                                            ImageView img=imgDialog.findViewById(R.id.img_dialog);
////                                            img.setImageBitmap(response);
////                                            changeSize(imgDialog.getWindow(),activity);
////                                            imgDialog.show();
////                                        }
////                                    });
//////                                objectInfo.setBitmap(response);
////                                    imageCache.put(objectInfo.getCodeObject(), response);
////                                }
////                            }, 60, 60, ImageView.ScaleType.FIT_XY,
////                            Bitmap.Config.ARGB_8888,
////                            new Response.ErrorListener() {
////                                @Override
////                                public void onErrorResponse(VolleyError error) {
////                                }
////                            });
////                    app.getRequestQueue().add(imgRequest);
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
                    objImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Dialog imgDialog = new Dialog(context);
                            imgDialog.setContentView(R.layout.layout_image_dialog);
                            ImageView img = imgDialog.findViewById(R.id.img_dialog);
                            img.setImageBitmap(bitmap);
                            changeSize(imgDialog.getWindow(), activity);
                            imgDialog.show();
                        }
                    });
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
