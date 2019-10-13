package com.example.ahmad2.shopproject;

import android.os.Message;

import java.util.Date;

public class PanelInfo {

    public String[] Messages;
    public String[] MobileNumbers;
    public String LineNumber;
    public String SendDateTime;
    public String CanContinueInCaseOfError;
    public String user;
    public String pass;
    public ApiKeySms apiKey=new ApiKeySms();

//    public PanelInfo(){
//        this.Messages=new String[1];
//        this.LineNumber="30004747472856";
//        this.CanContinueInCaseOfError="false";
//        this.MobileNumbers=new String[]{"09125084200"};
//        Date date=new Date();
//        long time = date.getTime();
//        this.SendDateTime="";
//    }


    @Override
    public String toString() {
        return Messages[0]+":"+MobileNumbers[0]+":"+LineNumber+":"+SendDateTime+":"+CanContinueInCaseOfError+":"+user+":"+pass;
    }
}
