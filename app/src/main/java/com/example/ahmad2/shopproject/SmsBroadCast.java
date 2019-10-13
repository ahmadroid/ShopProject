package com.example.ahmad2.shopproject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class SmsBroadCast extends BroadcastReceiver {

    public static final String ACTION="android.provider.Telephony.SMS_RECEIVED";
    @Override
    public void onReceive(Context context, Intent intent) {
        Map<String ,String> params=new HashMap<>();
        String sms="";
        params=receiveSms1(intent);
        for (String str:params.keySet()) {
            sms = " From phone " + str + "\n" + params.get(str);
            if (str.equals("1112")) {
                Intent smsIntent = new Intent(context, SmsActivity.class);
                smsIntent.putExtra("sms", sms);
                smsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(smsIntent);
            }
        }
    }

    public void receiveSms(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null)
            return;
        Object[] pdus = (Object[]) extras.get("pdus");
        if (pdus == null)
            return;
        SmsMessage[] smsMessage = new SmsMessage[pdus.length];
        for (int i = 0; i < pdus.length; i++) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                String format = extras.getString("format");
                smsMessage[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
            } else {
                smsMessage[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }
            String sms = "sms" + i + smsMessage[i].getDisplayOriginatingAddress() + ":" + smsMessage[i].getMessageBody();
            Log.i("tagsmsmes", sms);
        }
    }

    public Map<String, String> receiveSms1(Intent intent){
        Map<String,String> params=new HashMap<>();
        Bundle extras = intent.getExtras();
        if (extras==null) return params;
        Object[] pdus = (Object[]) extras.get("pdus");
        if (pdus==null) return params;
        SmsMessage[] smsMessages=new SmsMessage[pdus.length];
        for (int i=0;i<pdus.length;i++){
            if (Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP){
                String format=extras.getString("format");
                smsMessages[i]=SmsMessage.createFromPdu((byte[]) pdus[i],format);
            }else{
                smsMessages[i]=SmsMessage.createFromPdu((byte[]) pdus[i]);
            }
            if (params.get(smsMessages[i].getDisplayOriginatingAddress())==null){
                params.put(smsMessages[i].getDisplayOriginatingAddress(),smsMessages[i].getDisplayMessageBody());
            }else{
                String sms=params.get(smsMessages[i].getDisplayOriginatingAddress());
                sms +=smsMessages[i].getDisplayMessageBody();
                params.put(smsMessages[i].getDisplayOriginatingAddress(),sms);
            }
        }
        return params;
    }
}
