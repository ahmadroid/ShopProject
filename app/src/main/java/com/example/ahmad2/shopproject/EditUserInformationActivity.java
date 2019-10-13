package com.example.ahmad2.shopproject;

import android.os.Parcelable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class EditUserInformationActivity extends MyActivity {

    private Parcelable parcel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_information);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FragmentEditUserInformation fragmentEditUserInformation = new FragmentEditUserInformation();
        getSupportFragmentManager().beginTransaction().replace(R.id.lin_edit_information, fragmentEditUserInformation).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
