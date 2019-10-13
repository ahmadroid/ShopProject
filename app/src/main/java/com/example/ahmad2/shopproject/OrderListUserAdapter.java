package com.example.ahmad2.shopproject;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderListUserAdapter extends RecyclerView.Adapter<OrderListUserAdapter.MyViewHolder> {

    public interface OnItemClickListener {
        public void onItemclick(Order order);
    }

    private final List<Order> orderList;
    private Context context;
    private final OnItemClickListener listener;


    public OrderListUserAdapter(Context context, List<Order> orderList, OnItemClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_list_user, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        myViewHolder.bind(orderList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView txtName, txtAmount, txtUnit, txtSeen,txtDate;
        private FloatingActionButton fltEdit;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAmount = itemView.findViewById(R.id.txt_amount_item_order_list_user);
            fltEdit=itemView.findViewById(R.id.flt_edit_item_order_list_user);
            txtName = itemView.findViewById(R.id.txt_name_item_order_list_user);
            txtName.setMovementMethod(new ScrollingMovementMethod());
            txtUnit = itemView.findViewById(R.id.txt_unit_item_order_list_user);
            txtSeen = itemView.findViewById(R.id.txt_seen_item_order_list_user);
            txtDate=itemView.findViewById(R.id.txt_date_item_order_list_user);

        }

        public void bind(final Order order, final OnItemClickListener listener) {
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    listener.onItemclick(order);
//                }
//            });
            fltEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemclick(order);
                }
            });
            if (order.getSeen() == 1) {
                txtSeen.setText(context.getResources().getString(R.string.seen));
                txtSeen.setTextColor(Color.GREEN);
            } else if (order.getSeen() == 0) {
                txtSeen.setText(context.getResources().getString(R.string.not_seen));
                txtSeen.setTextColor(Color.RED);
            }
            txtUnit.setText(order.getUnit());
            txtName.setText(order.getName());
            Locale farsiLoc=new Locale("fa");
            NumberFormat numberFormat=NumberFormat.getNumberInstance(farsiLoc);
            txtAmount.setText(numberFormat.format(order.getAmount()));
            txtDate.setText(order.getOrderDate());
        }
    }
}
