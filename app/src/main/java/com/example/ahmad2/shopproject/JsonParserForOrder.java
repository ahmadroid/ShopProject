package com.example.ahmad2.shopproject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonParserForOrder {

    public static List<Order> getOrderList(String response){
        List<Order> orderList=new ArrayList<>();
        try {
            JSONArray jsonArray=new JSONArray(response);
            for (int i=0;i<jsonArray.length();i++){
                Order order=new Order();
                JSONObject object=jsonArray.getJSONObject(i);
                order.setName(object.getString("name"));
                order.setAmount(object.getInt("amount"));
                order.setUser(object.getString("user"));
                order.setDescription(object.getString("description"));
                order.setUnit(object.getString("unit"));
                order.setSeen((short) object.getInt("seen"));
                order.setId(object.getLong("id"));
                order.setUserShop(object.getString("username"));
                order.setOrderDate(object.getString("orderdate"));
                orderList.add(order);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return orderList;
    }
}
