package com.example.ahmad2.shopproject;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.List;

public class ObjectInfo implements Parcelable {

    private long codeObject;
    private String name;
    private String user;
    private long price;
    private String description;
    @SerializedName("image")
    private short isImageSelect;
    private short isSell;
    private long inventory;
    private String unit;
    private short isAgree;

    public short getIsAgree() {
        return isAgree;
    }

    public void setIsAgree(short isAgree) {
        this.isAgree = isAgree;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public long getInventory() {
        return inventory;
    }

    public void setInventory(long inventory) {
        this.inventory = inventory;
    }

    public short getIsSell() {
        return isSell;
    }

    public void setIsSell(short isSell) {
        this.isSell = isSell;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public short getIsImageSelect() {
        return isImageSelect;
    }

    public void setIsImageSelect(short isImageSelect) {
        this.isImageSelect = isImageSelect;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ObjectInfo() {
    }

    public long getCodeObject() {
        return this.codeObject;
    }

    public void setCodeObject(long codeObject) {
        this.codeObject = codeObject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return name;
    }

    public ObjectInfo(Parcel parcel) {
        this.codeObject = parcel.readLong();
        this.name = parcel.readString();
        this.price = parcel.readLong();
        this.isImageSelect = (short) parcel.readInt();
        this.user = parcel.readString();
        this.description = parcel.readString();
        this.isSell = (short) parcel.readInt();
        this.inventory=parcel.readLong();
        this.unit=parcel.readString();
        this.isAgree= (short) parcel.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(codeObject);
        dest.writeString(name);
        dest.writeLong(price);
        dest.writeInt(isImageSelect);
        dest.writeString(user);
        dest.writeString(description);
        dest.writeInt(isSell);
        dest.writeLong(inventory);
        dest.writeString(unit);
        dest.writeInt(isAgree);
    }

    public static final Creator<ObjectInfo> CREATOR = new Creator<ObjectInfo>() {
        @Override
        public ObjectInfo createFromParcel(Parcel source) {
            return new ObjectInfo(source);
        }

        @Override
        public ObjectInfo[] newArray(int size) {
            return new ObjectInfo[size];
        }
    };

    public static ContentValues writeObjectToContentValue(ObjectInfo object) {
        ContentValues values = new ContentValues();
        values.put("codeObject", object.getCodeObject());
        values.put("name", object.getName());
        values.put("price", object.getPrice());
        values.put("user", object.getUser());
        values.put("isImageSelect", object.getIsImageSelect());
        values.put("description", object.getDescription());
        values.put("isSell", object.getIsSell());
        return values;
    }

    public static List<ObjectInfo> getObjectListFromCursor(Cursor cursor) {
        List<ObjectInfo> objectList = new ArrayList<>();
        if (cursor.moveToFirst()) {

            do {
                ObjectInfo object = new ObjectInfo();
                object.setCodeObject(cursor.getLong(cursor.getColumnIndex("codeObject")));
                object.setName(cursor.getString(cursor.getColumnIndex("name")));
                object.setUser(cursor.getString(cursor.getColumnIndex("user")));
                object.setPrice(cursor.getLong(cursor.getColumnIndex("price")));
                object.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                object.setIsImageSelect((short) cursor.getInt(cursor.getColumnIndex("isImageSelect")));
                object.setIsSell((short) cursor.getInt(cursor.getColumnIndex("isSell")));
                objectList.add(object);
            } while (cursor.moveToNext());
        }
        return objectList;
    }
}
