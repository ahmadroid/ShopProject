package com.example.ahmad2.shopproject;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.RequiresPermission;

import com.google.gson.annotations.SerializedName;

public class User implements Parcelable {

    public long id;
    public String name;
    @SerializedName("user")
    public String UserName;
    @SerializedName("pass")
    public String password;
    public short admin;
    public String shop;
    public String mobile;
    public String tel;
    public String address;
    public String token;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User() {
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public short getAdmin() {
        return admin;
    }

    public void setAdmin(short admin) {
        this.admin = admin;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(admin);
        dest.writeString(name);
        dest.writeString(UserName);
        dest.writeString(password);
        dest.writeString(mobile);
        dest.writeString(tel);
        dest.writeString(address);
        dest.writeString(token);
    }

    public User(Parcel parcel){
        this.admin= (short) parcel.readInt();
        this.name=parcel.readString();
        this.UserName=parcel.readString();
        this.password=parcel.readString();
        this.mobile=parcel.readString();
        this.tel=parcel.readString();
        this.address=parcel.readString();
        this.token=parcel.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
