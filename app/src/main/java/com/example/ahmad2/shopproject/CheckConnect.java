package com.example.ahmad2.shopproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class CheckConnect extends BroadcastReceiver {

    public interface OnConnectToData{

        public void onConnect(boolean bool);
    }

    private OnConnectToData listener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("tagOnreceiver","receiver");
        if (chekConnect(context)){
            listener.onConnect(true);
        }else{
            listener.onConnect(false);
        }
    }

    public boolean chekConnect(Context context){
        ConnectivityManager conMg= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = conMg.getActiveNetworkInfo();
        if (activeNetworkInfo!=null && activeNetworkInfo.isConnected()){
            return true;
        }
        return false;
    }

    public void setListener(OnConnectToData listener) {
        this.listener = listener;
    }
}
