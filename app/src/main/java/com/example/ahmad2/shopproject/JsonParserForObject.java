package com.example.ahmad2.shopproject;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonParserForObject {

    public static List<ObjectInfo> getObjectList(String list){
        List<ObjectInfo> objectList=new ArrayList<>();
        try {
            JSONArray jsonArray=new JSONArray(list);
            for (int i=0;i<jsonArray.length();i++){
                ObjectInfo object=new ObjectInfo();
                JSONObject json=jsonArray.getJSONObject(i);
                object.setCodeObject(json.getInt("codeobject"));
                object.setName(json.getString("name"));
                object.setPrice(json.getInt("price"));
                object.setDescription(json.getString("description"));
                object.setIsImageSelect((short) json.getInt("isimageselect"));
                objectList.add(object);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return objectList;
    }
}
