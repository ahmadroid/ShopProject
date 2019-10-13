package com.example.ahmad2.shopproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    /*
    private long codeObject;
    private String name;
    private String user;
    private long price;
    private String description;
    @SerializedName("image")
    private short isImageSelect;
    private short isSell;
     */

    private Context context;
    private static final String DB_NAME="pasazh_db";
    private static final String[] COLUMNS=new String[]{"codeObject","name","user","price","description","isImageSelect","isSell"};
    private static final int VERSION=21;
    public static final String TABLE_NAME="pasazh_tb";
    private static final String SQL_QUERY="CREATE TABLE IF NOT EXISTS '"+TABLE_NAME+"' ("
            +"'codeObject' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            +"'name' TEXT, "
            +"'user' TEXT, "
            +"'price' NUMERIC, "
            +"'description' TEXT, "
            +"'isImageSelect' INTEGER, "
            +"'isSell' INTEGER"
            +")";

    private static final String SHOP_TBBLE="shop_tb";
    private static final String SQL_CREATE_SHOP_TABLE="CREATE TABLE IF NOT EXISTS '"+SHOP_TBBLE+"' ("
            +"'id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            +"'user' TEXT, "
            +"'shop' TEXT"
            +")";

    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, VERSION);
        this.context=context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_QUERY);
        db.execSQL(SQL_CREATE_SHOP_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+SHOP_TBBLE);
        onCreate(db);
    }

    public void insertObjectListToDb(List<ObjectInfo> objectList){
        SQLiteDatabase dbase=getWritableDatabase();
        for (ObjectInfo object:objectList){
            ContentValues values = ObjectInfo.writeObjectToContentValue(object);
            if (searchObjectToDb(object.getCodeObject())) {
                updateObjectListToDb(object.getCodeObject(),values);
            }else {
                dbase.insert(TABLE_NAME, null, values);
            }
        }
        if (dbase.isOpen()){
            dbase.close();
        }
    }

    public List<ObjectInfo> readObjectListFromDb(){
        List<ObjectInfo> objectList=new ArrayList<>();
        SQLiteDatabase dbase=getReadableDatabase();
        Cursor cursor=dbase.rawQuery("SELECT * FROM '"+TABLE_NAME+"'",null);
        objectList = ObjectInfo.getObjectListFromCursor(cursor);
        if (dbase.isOpen()){
            dbase.close();
        }
        return objectList;
    }

    public void updateObjectListToDb(long codeObject,ContentValues values){
        SQLiteDatabase dbase=getWritableDatabase();
        dbase.update(TABLE_NAME,values,"codeObject=?",new String[]{String.valueOf(codeObject)});
        if (dbase.isOpen()){
            dbase.close();
        }
    }

    public boolean searchObjectToDb(long codeObject){
        SQLiteDatabase dbase=getReadableDatabase();
        Cursor cursor = dbase.query(TABLE_NAME, COLUMNS, "codeObject=?", new String[]{String.valueOf(codeObject)}
        , null, null, null);
        List<ObjectInfo> objectList = ObjectInfo.getObjectListFromCursor(cursor);
//        if (dbase.isOpen()){
//            dbase.close();
//        }
        if (objectList.isEmpty()){
            return false;
        }
        return true;
    }

    public void deleteObjectFromDb(long codeObject){
        SQLiteDatabase dbase=getWritableDatabase();
        dbase.delete(TABLE_NAME,"codeObject=?",new String[]{String.valueOf(codeObject)});
        if (dbase.isOpen()){
            dbase.close();
        }
    }

    public void clearDatabase(){
        SQLiteDatabase dbase=getWritableDatabase();
        dbase.delete(TABLE_NAME,null,null);
        if (dbase.isOpen()){
            dbase.close();
        }
    }
}
