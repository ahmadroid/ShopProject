package com.example.ahmad2.shopproject;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class SellSpecialAdapter extends RecyclerView.Adapter<SellSpecialAdapter.MyViewHolder> {

    public interface OnItemClickListener {

        void onItemClick(Shop shop);
    }

    private List<Shop> shopList;
    private final OnItemClickListener listener;
    private Context context;

    public SellSpecialAdapter(Context context, List<Shop> shopList, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.shopList = shopList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sell_special, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.bind(shopList.get(i), listener);
    }

    @Override
    public int getItemCount() {
        return shopList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView txtName, txtSellSpecial, txtTitle;
        long i=0;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txt_name_item_sell_special);
            txtSellSpecial = itemView.findViewById(R.id.txt_special_item_sell_special);
            txtTitle = itemView.findViewById(R.id.txt_title_item_sell_special);
        }

        public void bind(final Shop shop, final OnItemClickListener listener) {
            txtSellSpecial.setText(shop.getSellSpecial());
            txtName.setText(shop.getShop());
            txtName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(shop);
                }
            });
            txtTitle.setText(shop.getTitle());
//            txtTitle.animate().alpha((float) 0.0).setDuration(3000).start();
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            txtTitle.animate().alpha((float) 1.0).setDuration(3000).start();

//            new android.os.Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    anima();
//                    new android.os.Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            anima();
//                            new android.os.Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    anima();
//                                    new android.os.Handler().postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            anima();
//                                            new android.os.Handler().postDelayed(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    anima();
//                                                    new android.os.Handler().postDelayed(new Runnable() {
//                                                        @Override
//                                                        public void run() {
//                                                            anima();
//                                                        }
//                                                    },3000);
//                                                }
//                                            },3000);
//                                        }
//                                    },3000);
//                                }
//                            },3000);
//                        }
//                    },3000);
//                }
//            },0);

//            for (;i<10;){
////                new android.os.Handler().postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        Log.i("tagFor","delay");
////                    }
////                },3000);
//                i++;
//                Log.i("tagFor",String.valueOf(i));
//                if (txtTitle.getAlpha()==1){
//                    txtTitle.animate().alpha((float) 0.0).setDuration(3000).start();
////                    new android.os.Handler().postDelayed(new Runnable() {
////                        @Override
////                        public void run() {
////
////                        }
////                    },3000);
//                }else if (txtTitle.getAlpha()==0){
//                    txtTitle.animate().alpha((float) 1.0).setDuration(3000).start();
//
//                }
//            }




//            new Timer().scheduleAtFixedRate(new TimerTask() {
//                @Override
//                public void run() {
////                    if (txtTitle.getAlpha() == 1)
////                        txtTitle.animate().alpha((float) 0.0).setDuration(3000).start();
////                    if (txtTitle.getAlpha() == 0)
////                        txtTitle.animate().alpha((float) 1.0).setDuration(3000).start();
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            txtTitle.animate().alpha((float) 0.0).setDuration(3000).start();
//                        }
//                    }).start();
//                }
//            }, 0, 3000);

        }

        private void anima() {
            Log.i("tagAnim","anim");
            if (txtTitle.getAlpha()==1){
                txtTitle.animate().alpha((float) 0.0).setDuration(3000).start();
            }else if (txtTitle.getAlpha()==0){
                txtTitle.animate().alpha((float) 1.0).setDuration(3000).start();
            }


        }
    }

}
