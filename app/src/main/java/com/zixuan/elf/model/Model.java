package com.zixuan.elf.model;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.zixuan.elf.presenter.IPresenter;
import com.zixuan.elf.presenter.Presenter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Model implements IModel {
    private static Model model ;
    IPresenter presenter;
    int type;
    private Model(){}
    public static Model getModel(IPresenter presenter){
        if(model == null){
            synchronized (Model.class){
                if(model == null){
                    model = new Model();
                }
            }
        }
        model.presenter = presenter;
        return model;
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String message = bundle.getString("msg");
            presenter.netWorkCallback(message,type);
        }
    };
    @Override
    public void getInformation(final String url, final int type) {
        this.type = type;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                HttpURLConnection connection = null;
                BufferedReader reader = null;
                String msg = "";
                try {
                    URL url1 = new URL(url);
                    connection = (HttpURLConnection) url1.openConnection();
                    if(type == 1 || type == 0 ||type == 3){
                    connection.setRequestMethod("GET");}
                    else{
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");//使用的是表单请求类型
                    }
                    connection.setReadTimeout(5000);
                    connection.setConnectTimeout(5000);


                    connection.connect();

                    InputStream inputStream = connection.getInputStream();
                    InputStreamReader streamReader = new InputStreamReader(inputStream);
                    reader = new BufferedReader(streamReader);
                    boolean tag = true;
                    while (tag){
                        String flag = reader.readLine();
                        if(flag != null){
                        msg+=flag;
                        }
                        else {
                            tag = false;
                        }
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    connection.disconnect();
                    try {
                        reader.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    } catch (IOException e) {
                        e.printStackTrace();
                        connection.disconnect();
                        try {
                        reader.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("msg",msg);
                message.setData(bundle);
                handler.sendMessage(message);
            }

        };
        ExecutorService service = Executors.newFixedThreadPool(2);
        service.execute(runnable);

    }

}
