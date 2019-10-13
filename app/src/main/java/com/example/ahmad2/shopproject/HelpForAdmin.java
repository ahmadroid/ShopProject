package com.example.ahmad2.shopproject;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HelpForAdmin extends MyActivity {

    private Spinner spinHelp;
    private ArrayAdapter<String> adapter;
    private List<String> helpList;
    private TextView txtDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_for_admin);
        txtDescription = findViewById(R.id.txt_description_help_admin);
        spinHelp = findViewById(R.id.spin_title_help_admin);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        helpList=new ArrayList<>();
        helpList=Arrays.asList(getResources().getStringArray(R.array.spin_help_admin));
        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, helpList);
        spinHelp.setAdapter(adapter);
        spinHelp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        txtDescription.setText(getString(R.string.hlp_des_adminOrder));
                        break;
                    case 1:
                        txtDescription.setText(getString(R.string.hlp_des_insert_object));
                        break;
                    case 2:
                        txtDescription.setText(getString(R.string.hlp_des_store));
                        break;
                    case 3:
                        txtDescription.setText(getString(R.string.hlp_des_shop));
                        break;
                    case 4:
                        txtDescription.setText(getString(R.string.hlp_des_sell_special_admin));
                        break;
                    case 5:
                        txtDescription.setText(getString(R.string.hlp_des_objLst));
                        break;
                    case 6:
                        txtDescription.setText(getString(R.string.hlp_des_myOrder));
                        break;
                    case 7:
                        txtDescription.setText(getString(R.string.hlp_des_shopList));
                        break;
                    case 8:
                        txtDescription.setText(getString(R.string.hlp_des_sell_special));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
