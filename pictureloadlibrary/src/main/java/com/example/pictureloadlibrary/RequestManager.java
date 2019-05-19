package com.example.pictureloadlibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.os.Environment.DIRECTORY_PICTURES;

public class RequestManager implements netWorkCallbackAble {
    private Context context;
    private String url;
    private RequestBuilder requestBuilder;
    public static RequestManager get(Context context){

        return new RequestManager(context);
    }

    private RequestManager(Context context){
        this.context = context;
    }

    public RequestBuilder load(Bitmap bitmap){
        RequestBuilder requestBuilder = new RequestBuilder(context,url);
        requestBuilder.bitmapReady(bitmap);
        this.requestBuilder = requestBuilder;
        return requestBuilder;
    }
    public RequestBuilder load(Drawable drawable){
        RequestBuilder requestBuilder = new RequestBuilder(context,url);
        requestBuilder.drawableReady(drawable);
        this.requestBuilder = requestBuilder;
        return requestBuilder;
    }
    public RequestBuilder load(String url){
        Bitmap data;
        this.url = url;
        requestBuilder = new RequestBuilder(context,url);
        if(url.startsWith("http")){

            data = Data.getBitmap(url);
            if(data != null) {
                netWorkCallback(url);
            }
            else {
                File file = new File(context.getExternalFilesDir(DIRECTORY_PICTURES),url);
                FileInputStream fos = null;

                if(file.exists()){
                    try {
                        fos = new FileInputStream(file);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();

                    }
                    Data.putBitmap(url,new SoftReference<>(BitmapFactory.decodeStream(fos)));
//                    Bitmap bitmap = BitmapFactory.decodeStream(fos);

                    netWorkCallback(url);
                }
                else {
                    sendRequest(url);
                }

            }

        }

            return requestBuilder;
    }

    private void sendRequest(String url){
        ExecutorService executor = Executors.newFixedThreadPool(3);
        executor.execute(new NetRequestRunnable(url,this,context));
    }

    @Override
    public void netWorkCallback(String url) {
        if(requestBuilder != null)
        requestBuilder.dataReady(url);
    }
}
