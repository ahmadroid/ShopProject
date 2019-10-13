package com.example.ahmad2.shopproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ResizePicture {

    public static ByteArrayOutputStream changeImageSize(InputStream stream){
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        Bitmap bmpOriginal=BitmapFactory.decodeStream(stream);
        int width = bmpOriginal.getWidth();
        int height = bmpOriginal.getHeight();
        final int dstWidth=400;
        if (width>dstWidth){
            int dstHeight=height/(width/dstWidth);
            Bitmap bmpResize=Bitmap.createScaledBitmap(bmpOriginal,dstWidth,dstHeight,false);
            bmpResize.compress(Bitmap.CompressFormat.JPEG,60,outputStream);
        }
        return outputStream;
    }
}
