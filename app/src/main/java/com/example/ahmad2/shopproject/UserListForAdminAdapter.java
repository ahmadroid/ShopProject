package com.example.ahmad2.shopproject;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class UserListForAdminAdapter extends RecyclerView.Adapter<UserListForAdminAdapter.MyViewHolder> {

    public interface OnItemClickListener {
        public void onItemClick(User user);
    }

    private final List<User> userList;
    private final OnItemClickListener listener;
    private Context context;

    public UserListForAdminAdapter(Context context, List<User> userList, OnItemClickListener listener) {
        this.listener = listener;
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_list_for_admin, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.bind(userList.get(i), listener);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtName, txtMobile, txtUserType;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMobile = itemView.findViewById(R.id.txt_mobile_item_user_lis_for_admin);
            txtName = itemView.findViewById(R.id.txt_name_item_user_lis_for_admin);
            txtUserType = itemView.findViewById(R.id.txt_userType_item_user_lis_for_admin);
        }

        public void bind(final User user, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(user);
                }
            });
            if (user.getAdmin() == 1) {
                txtUserType.setText(context.getResources().getString(R.string.admin));
                txtUserType.setTextColor(Color.GREEN);
            } else if (user.getAdmin() == 0) {
                txtUserType.setText(context.getResources().getString(R.string.user_sample));
                txtUserType.setTextColor(Color.RED);
            }
            txtName.setText(user.getUserName());
            txtMobile.setText(user.getMobile());
        }
    }
}
