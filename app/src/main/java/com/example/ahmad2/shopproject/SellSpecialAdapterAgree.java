package com.example.ahmad2.shopproject;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SellSpecialAdapterAgree extends RecyclerView.Adapter<SellSpecialAdapterAgree.MyViewHolder> {

    public interface OnItemClickListener {

        void onItemClick(Shop shop);
        void onFloatDeleteClick(Shop shop);
    }

    private List<Shop> shopList;
    private final OnItemClickListener listener;
    private Context context;

    public SellSpecialAdapterAgree(Context context, List<Shop> shopList, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.shopList = shopList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sell_special_agree, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.bind(context,shopList.get(i), listener);
    }

    @Override
    public int getItemCount() {
        return shopList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {

        private TextView txtName, txtSellSpecial, txtTitle;
        private CheckBox chkAgree;
        private String user="",token="";
        private Context context;
        private Shop shop;
        private FloatingActionButton fltDelete;
        long i=0;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txt_name_item_sell_special_agree);
            txtSellSpecial = itemView.findViewById(R.id.txt_special_item_sell_special_agree);
            txtTitle = itemView.findViewById(R.id.txt_title_item_sell_special_agree);
            chkAgree=itemView.findViewById(R.id.chkbox_item_sell_special_agree);
            fltDelete=itemView.findViewById(R.id.flt_btn_item_sell_special_agree);
        }

        public void bind(final Context context,final Shop shop, final OnItemClickListener listener) {
            txtSellSpecial.setText(shop.getSellSpecial());
            txtName.setText(shop.getShop());
            txtName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(shop);
                }
            });
            this.context=context;
            this.shop=shop;
            txtTitle.setText(shop.getTitle());
            if (shop.getIsSellAgree()==0){
                chkAgree.setChecked(false);
            }else if (shop.getIsSellAgree()==1){
                chkAgree.setChecked(true);
            }
            chkAgree.setOnCheckedChangeListener(this);
            fltDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onFloatDeleteClick(shop);
                }
            });

        }

        private void anima() {
            Log.i("tagAnim","anim");
            if (txtTitle.getAlpha()==1){
                txtTitle.animate().alpha((float) 0.0).setDuration(3000).start();
            }else if (txtTitle.getAlpha()==0){
                txtTitle.animate().alpha((float) 1.0).setDuration(3000).start();
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
            if (isChecked){
                shop.setIsSellAgree((short) 1);
            }else {
                shop.setIsSellAgree((short) 0);
            }
            Login login=new Login();
            login.user=user;
            requestLoginShop.login=login;
            requestLoginShop.shop=shop;
            Call<ResponseMessageToken> call = retrofitService.updateSellSpecialAgree(requestLoginShop);
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


    }

}
