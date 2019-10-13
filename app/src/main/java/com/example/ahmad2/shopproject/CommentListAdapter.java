package com.example.ahmad2.shopproject;

import android.content.Context;

import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.MyViewHolder> {

    public interface OnItemClickListener{
        public void onItemClick(Comment comment);
    }

    private final OnItemClickListener listener;
    private final Context context;
    private List<Comment> commentList;

    public CommentListAdapter(Context context,List<Comment> commentList,OnItemClickListener listener){
        this.listener=listener;
        this.context=context;
        this.commentList=commentList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_show_comment,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.bind(commentList.get(i),listener,context);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView txtMessage,txtUser;
        private FloatingActionButton fltDelete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            fltDelete=itemView.findViewById(R.id.flt_btn_item_show_comment);
            txtMessage=itemView.findViewById(R.id.txt_message_item_show_comment);
            txtUser=itemView.findViewById(R.id.txt_user_item_show_comment);
        }

        public void bind(final Comment comment, final OnItemClickListener listener, Context context){
            fltDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(comment);
                }
            });
            txtMessage.setText(comment.getMessage());
            txtUser.setText(comment.getUser());
        }
    }
}
