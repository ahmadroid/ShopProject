package com.example.ahmad2.shopproject;

import com.google.gson.annotations.SerializedName;

public class RequestObjectApiKey {

    @SerializedName("objectInfo")
    public ObjectInfo object;
    public ApiKey apiKey=new ApiKey();
}
