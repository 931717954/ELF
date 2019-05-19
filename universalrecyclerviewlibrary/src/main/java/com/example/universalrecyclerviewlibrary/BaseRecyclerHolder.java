package com.example.universalrecyclerviewlibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pictureloadlibrary.Flow;

public class BaseRecyclerHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> views;
    private Context context;
    private BaseRecyclerHolder(Context context,@NonNull View itemView) {
        super(itemView);
        this.context = context;
        views = new SparseArray<>(8);
    }
    public static BaseRecyclerHolder getRecyclerHolder(Context context,@NonNull View itemView){
        return new BaseRecyclerHolder(context,itemView);
    }
    public SparseArray<View> getViews() {
        return views;
    }
    public <T extends View>T getView(@IdRes int viewId){
        View view = views.get(viewId);
        if(view == null){
            view = itemView.findViewById(viewId);
            views.put(viewId,view);
        }
        return (T) view;
    }
    /**
     * 设置字符串
     */
    public BaseRecyclerHolder setText(int viewId,String text){
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    /**
     * 设置图片
     */
    public BaseRecyclerHolder setImageResource(int viewId,int drawableId){
        ImageView iv = getView(viewId);
        iv.setImageResource(drawableId);
        return this;
    }

    /**
     * 设置图片
     */
    public BaseRecyclerHolder setImageBitmap(int viewId, Bitmap bitmap){
        ImageView iv = getView(viewId);
        iv.setImageBitmap(bitmap);
        return this;
    }

    /**
     * 设置图片
     */
    public BaseRecyclerHolder setImageByUrl(int viewId,String url){
        Flow.with(context).load(url).into((ImageView) getView(viewId));

        return this;
    }


}
