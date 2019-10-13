package com.example.ahmad2.shopproject;

import android.content.IntentFilter;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class SmsActivity extends AppCompatActivity {

    private TextView txtSms;
    private SmsBroadCast smsBroadCast;
    private static final String action="android.provider.Telephony.SMS_RECEIVED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        smsBroadCast=new SmsBroadCast();
        txtSms = findViewById(R.id.txt_sms);
        txtSms.setMovementMethod(new ScrollingMovementMethod());
        String sms = getIntent().getStringExtra("sms");
        if (sms != null)
            txtSms.setText(sms);

        registerReceiver(smsBroadCast,new IntentFilter(SmsBroadCast.ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(smsBroadCast);
    }
}
