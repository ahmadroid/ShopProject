package com.example.ahmad2.shopproject;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class NotConnectActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnRetry,btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_connect);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnRetry=findViewById(R.id.btn_not_connect);
        btnExit=findViewById(R.id.btn_exit_not_connect);
        btnExit.setOnClickListener(this);
        btnRetry.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view.equals(btnRetry)) {
            startActivity(new Intent(NotConnectActivity.this, RegisterActivity.class));
            finish();
        }else if (view.equals(btnExit)){
            finish();
        }
    }
}
