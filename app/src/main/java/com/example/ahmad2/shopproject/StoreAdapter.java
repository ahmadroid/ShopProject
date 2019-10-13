package com.example.ahmad2.shopproject;

import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.MyHolder> {

    public interface OnItemClickListener{

        public void onItemInsertClick(ObjectInfo objectInfo);
        public void onItemEditClick(ObjectInfo objectInfo);
    }

    private List<ObjectInfo> objectList;
    private final OnItemClickListener listener;
    private Context context;

    public StoreAdapter(Context context,List<ObjectInfo> objectList,OnItemClickListener listener){
        this.listener=listener;
        this.objectList=objectList;
        this.context=context;

    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(context).inflate(R.layout.item_object_list_store,viewGroup,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        myHolder.bind(objectList.get(i),listener);
    }

    @Override
    public int getItemCount() {
        return objectList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        private TextView txtName;
        private FloatingActionButton fltEdit,fltInsert;
        private RelativeLayout relLayout;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            txtName=itemView.findViewById(R.id.txt_object_name_item_store);
            fltEdit=itemView.findViewById(R.id.flt_edit_item_store);
            fltInsert=itemView.findViewById(R.id.flt_insert_item_store);
            relLayout=itemView.findViewById(R.id.rel_layout_item_store);

        }

        public void bind(final ObjectInfo objectInfo, final OnItemClickListener listener){

            if (Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP){
                ViewGroup.LayoutParams layoutParams = relLayout.getLayoutParams();
                layoutParams.height=300;
                relLayout.setLayoutParams(layoutParams);
            }
            txtName.setText(objectInfo.getName());
//            txtName.setText(String.valueOf(height));
            fltEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemEditClick(objectInfo);
                }
            });
            fltInsert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemInsertClick(objectInfo);
                }
            });
        }
    }
}
