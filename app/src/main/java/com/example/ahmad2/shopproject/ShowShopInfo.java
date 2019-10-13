package com.example.ahmad2.shopproject;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

public class ShowShopInfo extends MyActivity {

    private TextView txtName, txtShop, txtJob, txtMobile, txtPhone, txtAddress;
    private TextInputLayout txtInputName, txtInputShop, txtInputJob, txtInputMobile, txtInputPhone, txtInputAddress;
    private Shop shop;
    private SharedPreferences preferences;
    private int showInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_shop_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setView();
        if (getIntent().getParcelableExtra("shop") != null) {
            shop = new Shop();
            shop = getIntent().getParcelableExtra("shop");
        }

        fullView();
        setComponnentVisibility();
    }

    private void setComponnentVisibility() {
        if (shop.getShowInfo().charAt(5) == '1') {
            txtInputName.setVisibility(View.VISIBLE);
        }
        if (shop.getShowInfo().charAt(4) == '1') {
            txtInputShop.setVisibility(View.VISIBLE);
        }
        if (shop.getShowInfo().charAt(3) == '1') {
            txtInputJob.setVisibility(View.VISIBLE);
        }
        if (shop.getShowInfo().charAt(2) == '1') {
            txtInputMobile.setVisibility(View.VISIBLE);
        }
        if (shop.getShowInfo().charAt(1) == '1') {
            txtInputPhone.setVisibility(View.VISIBLE);
        }
        if (shop.getShowInfo().charAt(0) == '1') {
            txtInputAddress.setVisibility(View.VISIBLE);
        }

    }

    private void fullView() {
        txtName.setText(shop.getName());
        txtShop.setText(shop.getShop());
        txtJob.setText(shop.getJob());
        txtMobile.setText(shop.getMobile());
        txtPhone.setText(shop.getPhone());
        txtAddress.setText(shop.getAddress());
    }

    private void setView() {
        txtName = findViewById(R.id.txt_name_show_shop_info);
        txtShop = findViewById(R.id.txt_shop_show_shop_info);
        txtJob = findViewById(R.id.txt_job_show_shop_info);
        txtMobile = findViewById(R.id.txt_mobile_show_shop_info);
        txtPhone = findViewById(R.id.txt_phone_show_shop_info);
        txtAddress = findViewById(R.id.txt_address_show_shop_info);
        txtInputName = findViewById(R.id.txt_input_name_show_shop_info);
        txtInputShop = findViewById(R.id.txt_input_shop_show_shop_info);
        txtInputJob = findViewById(R.id.txt_input_job_show_shop_info);
        txtInputMobile = findViewById(R.id.txt_input_mobile_show_shop_info);
        txtInputPhone = findViewById(R.id.txt_input_phone_show_shop_info);
        txtInputAddress = findViewById(R.id.txt_input_address_show_shop_info);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
