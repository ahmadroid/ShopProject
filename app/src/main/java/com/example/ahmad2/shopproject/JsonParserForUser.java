package com.example.ahmad2.shopproject;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

 class JsonParserForUser {

     static List<User> userParser(String list){
        List<User> userList=new ArrayList<>();
        try {
            JSONArray jsonArray=new JSONArray(list);
            for (int i=0;i<jsonArray.length();i++){
                User user=new User();
                user.setName(jsonArray.getJSONObject(i).getString("name"));
                user.setUserName(jsonArray.getJSONObject(i).getString("user"));
                user.setMobile(jsonArray.getJSONObject(i).getString("mobile"));
                user.setTel(jsonArray.getJSONObject(i).getString("tel"));
                user.setAddress(jsonArray.getJSONObject(i).getString("address"));
                user.setAdmin((short) jsonArray.getJSONObject(i).getInt("admin"));
                userList.add(user);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userList;
    }
}

