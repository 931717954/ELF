package com.example.pictureloadlibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.os.Environment.DIRECTORY_PICTURES;

/**
 * @author lizixuan
 * @date 2019-4-5
 */
public class NetRequestRunnable implements Runnable {
    private static final String TAG = "MyTag";
    String url;
    private Context context;
    netWorkCallbackAble obj;
    public NetRequestRunnable(String url, netWorkCallbackAble obj, Context context){
        this.url = url;
        this.obj = obj;
        this.context = context;
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            obj.netWorkCallback(url);
        }
    };
    @Override
    public void run() {

        URL mUrl;
        HttpURLConnection connection;
        InputStream in;
        BufferedInputStream inputStream;
        String data = null;
        Bitmap map = null;
        try {
            mUrl = new URL(url);

            connection = (HttpURLConnection) mUrl.openConnection();

            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestMethod("GET");
            connection.connect();
            in = connection.getInputStream();
            map = BitmapFactory.decodeStream(in);
            Log.d(TAG, "run: "+map);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Message msg = new Message();

        Data.putBitmap(url, new SoftReference<>(map));
        handler.sendMessage(msg);

        File file = new File(context.getExternalFilesDir(DIRECTORY_PICTURES),url);
        FileOutputStream fileOutputStream ;
        Log.d(TAG, "run: "+file.exists());
        File parent = file.getParentFile();
        Log.d(TAG, "run: parent "+parent.exists());
//         if(!parent.exists()){
            try {
                parent.mkdirs();
//                fileOutputStream = context.openFileOutput(,Context.MODE_PRIVATE);
                fileOutputStream = new FileOutputStream(file);
                Log.d(TAG, "run: 已写入0");
                map.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
                Log.d(TAG, "run: 已写入");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e(TAG, "run: FileNotFoundException"+e.getMessage() );
            }
//        }
    }
}
