package com.example.ahmad2.shopproject;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ErrorHandler {

    private Retrofit retrofit;

    public ErrorHandler(Retrofit retrofit){
        this.retrofit=retrofit;
    }
    public ResponseMessageToken parseError(Response response){
        Converter<ResponseBody,ResponseMessageToken> converter=retrofit.responseBodyConverter(ResponseMessageToken.class,new Annotation[0]);
        ResponseMessageToken responseError=null;
        try {
            responseError=converter.convert(response.errorBody());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseError;
    }


}
