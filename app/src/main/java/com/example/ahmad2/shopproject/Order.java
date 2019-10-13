package com.example.ahmad2.shopproject;

import android.os.Parcel;
import android.os.Parcelable;

public class Order implements Parcelable {

    private long id;
    private String name;
    private String user;
    private String unit;
    private String description;
    private short seen;
    private String userShop;
    private String orderDate;
    private int amount;


    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserShop() {
        return userShop;
    }

    public void setUserShop(String userShop) {
        this.userShop = userShop;
    }

    public short getSeen() {
        return seen;
    }

    public void setSeen(short seen) {
        this.seen = seen;
    }

    public Order() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
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
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(user);
        dest.writeString(unit);
        dest.writeString(description);
        dest.writeString(userShop);
        dest.writeString(orderDate);
        dest.writeInt(seen);
        dest.writeInt(amount);
    }

    public Order(Parcel parcel){
        this.id=parcel.readLong();
        this.name=parcel.readString();
        this.user=parcel.readString();
        this.unit=parcel.readString();
        this.description=parcel.readString();
        this.userShop =parcel.readString();
        this.orderDate=parcel.readString();
        this.seen= (short) parcel.readInt();
        this.amount=parcel.readInt();
    }

    public static final Creator<Order> CREATOR=new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel source) {
            return new Order(source);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
}
