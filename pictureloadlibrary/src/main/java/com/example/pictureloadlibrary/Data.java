package com.example.pictureloadlibrary;

import android.graphics.Bitmap;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.WeakHashMap;

public class Data {
    private static HashMap<String,SoftReference<Bitmap>> map = new HashMap<>();

    private Data(){ }


    public static Bitmap getBitmap(String key){
        if(map.get(key) != null){
            return map.get(key).get();
        }
        else{
            return null;
        }
    }
    public static void putBitmap(String key,SoftReference<Bitmap> bitmap){
        map.put(key,bitmap);
    }
}
