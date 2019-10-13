package com.example.ahmad2.shopproject;

import com.google.gson.annotations.SerializedName;

public class Register {

    private long id;
    private String user;
    @SerializedName("isregister")
    private Short isRegister;
    private String pass;
    private short seen;

    public short getSeen() {
        return seen;
    }

    public void setSeen(short seen) {
        this.seen = seen;
    }

    public Register(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Short getIsRegister() {
        return isRegister;
    }

    public void setIsRegister(Short isRegister) {
        this.isRegister = isRegister;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
