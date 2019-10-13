package com.example.ahmad2.shopproject;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RegisterListAdapter extends RecyclerView.Adapter<RegisterListAdapter.MyViewHolder> {

    public interface OnItemClickListener{
        public void onItemClick(Register register);
    }

    private final OnItemClickListener listener;
    private List<Register> registerList;
    private Context context;

    public RegisterListAdapter(Context context,List<Register> registerList,OnItemClickListener listener){
        this.listener=listener;
        this.registerList=registerList;
        this.context=context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(context).inflate(R.layout.item_register_list_for_admin,viewGroup,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.bind(registerList.get(i),listener,context);
    }

    @Override
    public int getItemCount() {
        return registerList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView txtMobile,txtRegistered;
        private View itemView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView=itemView;
            txtMobile=itemView.findViewById(R.id.txt_mobile_item_register_list);
            txtRegistered=itemView.findViewById(R.id.txt_registered_item_register_list);
        }

        public void bind(final Register register, final OnItemClickListener listener,Context context){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(register);
                }
            });
            txtMobile.setText(register.getUser());
            if (register.getIsRegister()==1){
                txtRegistered.setText(context.getResources().getString(R.string.isShop));
            }else if (register.getIsRegister()==0){
                txtRegistered.setText(context.getResources().getString(R.string.notShop));
            }
        }
    }
}
