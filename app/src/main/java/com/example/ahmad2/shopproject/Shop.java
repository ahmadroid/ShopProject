package com.example.ahmad2.shopproject;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Shop implements Parcelable {

    private long id;
    private String name;
    private String user;
    private String shop;
    private String address;
    private String phone;
    private String mobile;
    private String job;
    @SerializedName("isimageselect")
    private short isImageShop;
    private String sellSpecial;
    private String title;
    private short isSellAgree;
    private short isShop;
    private String showInfo;

    public String getShowInfo() {
        return showInfo;
    }

    public void setShowInfo(String showInfo) {
        this.showInfo = showInfo;
    }

    public short getIsShop() {
        return isShop;
    }

    public void setIsShop(short isShop) {
        this.isShop = isShop;
    }

    public short getIsSellAgree() {
        return isSellAgree;
    }

    public void setIsSellAgree(short isSellAgree) {
        this.isSellAgree = isSellAgree;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSellSpecial() {
        return sellSpecial;
    }

    public void setSellSpecial(String sellSpecial) {
        this.sellSpecial = sellSpecial;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public short getIsImageShop() {
        return isImageShop;
    }

    public void setIsImageShop(short isImageShop) {
        this.isImageShop = isImageShop;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user);
        dest.writeString(shop);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(phone);
        dest.writeString(mobile);
        dest.writeString(job);
        dest.writeInt(isImageShop);
        dest.writeLong(id);
        dest.writeString(sellSpecial);
        dest.writeString(title);
        dest.writeInt(isSellAgree);
        dest.writeInt(isShop);
        dest.writeString(showInfo);

    }

    public Shop(){}

    public Shop(Parcel parcel){
        this.user=parcel.readString();
        this.shop=parcel.readString();
        this.name=parcel.readString();
        this.address=parcel.readString();
        this.phone=parcel.readString();
        this.mobile=parcel.readString();
        this.job=parcel.readString();
        this.isImageShop= (short) parcel.readInt();
        this.id=parcel.readLong();
        this.sellSpecial=parcel.readString();
        this.title=parcel.readString();
        this.isSellAgree= (short) parcel.readInt();
        this.isShop= (short) parcel.readInt();
        this.showInfo=parcel.readString();
    }

    public static final Creator<Shop> CREATOR=new Creator<Shop>() {
        @Override
        public Shop createFromParcel(Parcel source) {
            return new Shop(source);
        }

        @Override
        public Shop[] newArray(int size) {
            return new Shop[size];
        }
    };


}
