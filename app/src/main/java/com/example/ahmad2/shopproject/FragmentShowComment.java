package com.example.ahmad2.shopproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentShowComment extends Fragment {

    private String user="",token="";
    private RecyclerView recyclerView;
    private CommentListAdapter adapter;
//    private SharedPreferences preferences;
    private List<Comment> commentList;
    private ProgressBar progBar;
    private MyItemDecoration itemDecoration;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        preferences=getContext().getSharedPreferences("userInfoPre", Context.MODE_PRIVATE);

        commentList=new ArrayList<>();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        itemDecoration=new MyItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        if (App.prefUserInfo!=null){
            user=App.prefUserInfo.getString("user","");
            token=App.prefUserInfo.getString("token",token);
        }
        View view=inflater.inflate(R.layout.layout_show_comment,container,false);
        progBar=view.findViewById(R.id.prog_show_comment);
        recyclerView=view.findViewById(R.id.recycler_list_show_comment);
        progBarVisible();
        getCommentList();
        return view;
    }

    private void getCommentList() {
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
        Login login=new Login();
        login.user=user;
        Call<ResponseCommentListToken> call = retrofitService.getCommentList(login);
        call.enqueue(new Callback<ResponseCommentListToken>() {
            @Override
            public void onResponse(Call<ResponseCommentListToken> call, retrofit2.Response<ResponseCommentListToken> response) {
                progBarInVisible();
                if (response.isSuccessful()){
                    SharedPreferences.Editor editor=App.prefUserInfo.edit();
                    editor.putString("token",response.body().token);
                    editor.apply();
                    commentList=response.body().commentList;
                    showList();
                }
            }

            @Override
            public void onFailure(Call<ResponseCommentListToken> call, Throwable t) {
                progBarInVisible();
            }
        });
    }

    private void showList(){
        adapter=new CommentListAdapter(getContext(), commentList, new CommentListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Comment comment) {
                deleteComment(comment);
            }
        });
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(adapter);
    }

    private void deleteComment(Comment comment) {
        progBarVisible();
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
        OkHttpClient client=clientBuilder.build();
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(App.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        RetrofitService retrofitService=retrofit.create(RetrofitService.class);
        RequestLoginComment requestLoginComment=new RequestLoginComment();
        Login login=new Login();
        login.user=user;
        requestLoginComment.login=login;
        requestLoginComment.comment=comment;
        Call<ResponseMessageToken> call = retrofitService.deleteComment(requestLoginComment);
        call.enqueue(new Callback<ResponseMessageToken>() {
            @Override
            public void onResponse(Call<ResponseMessageToken> call, retrofit2.Response<ResponseMessageToken> response) {
                progBarInVisible();
                if (response.isSuccessful()){
                    SharedPreferences.Editor editor=App.prefUserInfo.edit();
                    editor.putString("token",response.body().token);
                    editor.apply();
                    Toast.makeText(getContext(),response.body().message,Toast.LENGTH_SHORT).show();
                    getCommentList();
                }
            }

            @Override
            public void onFailure(Call<ResponseMessageToken> call, Throwable t) {
                progBarInVisible();
                Toast.makeText(getContext(), getString(R.string.warrning_error_net), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void progBarVisible(){
        try{
            progBar.setVisibility(View.VISIBLE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void progBarInVisible(){
        try{
            progBar.setVisibility(View.INVISIBLE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
