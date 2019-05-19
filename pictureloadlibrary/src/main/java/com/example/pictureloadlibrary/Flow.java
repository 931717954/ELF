package com.example.pictureloadlibrary;

import android.content.Context;

public class Flow {
    private Flow(){ }
    public static RequestManager with(Context context){
        return RequestManager.get(context);
    }
}
