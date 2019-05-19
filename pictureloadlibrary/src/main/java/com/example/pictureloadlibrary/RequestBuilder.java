package com.example.pictureloadlibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.util.Log;
import android.widget.ImageView;

import java.lang.ref.SoftReference;

public class RequestBuilder {
    static int num;
    private static final String TAG ="MyTag";
    private  boolean isDataReady = false;
    private boolean isImageViewReady = false;
    private boolean isBitmapReady = false;
    private boolean isDrawableReady = false;
    private boolean isGet = false;
    private Bitmap bitmap;
    private Drawable drawable;
    private Context context;
    private int width;
    private int height;
    private ImageView imageView;
    private String url;
    private CallbackAble callbackAble;
    public RequestBuilder(Context context,String url){
        this.context = context;
        this.url = url;
    }
    public void dataReady(String url){
        this.bitmap = Data.getBitmap(url);
        isDataReady = true;
        loadBitmap();

    }
    public void bitmapReady(Bitmap bitmap){
        this.bitmap = bitmap;
        isBitmapReady = true;
        loadBitmap();
    }
    public void drawableReady(Drawable drawable){
        this.drawable = drawable;
        isDrawableReady = true;
        loadBitmap();
    }
    /*
    /如果数据和imageView的宽高都得到 就加载图片
     */
    private void loadBitmap(){
        if(bitmap == null){
            return;
        }
        Log.d(TAG, "loadBitmap: isDataready"+isDataReady);
        if(isImageViewReady && isDataReady){
            Log.d(TAG, "loadBitmap: "+height);
            Bitmap bitmap = readBitmapFromBitmap(this.bitmap,width,height);
            Log.d(TAG, "loadBitmap: size of bitmap"+bitmap.getByteCount());
            if( imageView.getTag() == url){
            imageView.setImageBitmap(bitmap);
            }
        }
        if(isImageViewReady && isBitmapReady){
            Bitmap bitmap = readBitmapFromBitmap(this.bitmap,width,height);
            Log.d(TAG, "loadBitmap: size of bitmap"+bitmap.getByteCount());
            if(imageView.getTag() == url)
            imageView.setImageBitmap(bitmap);
        }
        if(isDrawableReady && isImageViewReady){
            Bitmap bitmap = readBitmapFromDrawable(this.drawable,width,height);
            Log.d(TAG, "loadBitmap: size of bitmap"+bitmap.getByteCount());
            if(imageView.getTag() == url)
            imageView.setImageBitmap(bitmap);
        }
        if(isGet && isDataReady){
            Bitmap bitmap = readBitmapFromBitmap(this.bitmap,this.bitmap.getWidth(),this.bitmap.getHeight());
            callbackAble.callBack(bitmap);
        }
    }

    public void into(ImageView imageView){
        height = imageView.getLayoutParams().height;
        Log.d(TAG, "into: "+imageView.getLayoutParams().width);
        width = imageView.getLayoutParams().width;
        Log.d(TAG, "into: "+imageView.getWidth());
        if(tag){
            imageView.setImageBitmap(readBitmapFromDrawable(context.getDrawable(resourceid),width,height));
        }
        num++;
        imageView.setTag(url);
        Log.d(TAG, "into: setTag:"+url+" getTag"+imageView.getTag());

        this.imageView = imageView;
        isImageViewReady = true;
        loadBitmap();
    }

    public static Bitmap readBitmapFromDrawable(Drawable drawable,int width, int height){
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return readBitmapFromBitmap(bitmap,width,height);
    }

    public static Bitmap readBitmapFromBitmap(Bitmap bitmap,int w,int h){
        if(bitmap == null){
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth, scaleHeight, x, y;
        Bitmap newbmp = null;
        Matrix matrix = new Matrix();
        if (width > height) {
            scaleWidth = ((float) h / height);
            scaleHeight = ((float) h / height);
            x = (width - w * height / h) / 2;//获取bitmap源文件中x做表需要偏移的像数大小
            y = 0;
        } else if (width < height) {
            scaleWidth = ((float) w / width);
            scaleHeight = ((float) w / width);
            x = 0;
            y = (height - h * width / w) / 2;//获取bitmap源文件中y做表需要偏移的像数大小
        } else {
            scaleWidth = ((float) w / width);
            scaleHeight = ((float) w / width);
            x = 0;
            y = 0;
        }
        matrix.postScale(scaleWidth, scaleHeight);
        try {
            if(w <= 0 && h <= 0){
                newbmp = Bitmap.createBitmap(bitmap);
            }else
            newbmp = Bitmap.createBitmap(bitmap, (int) x, (int) y, (int) (width - x), (int) (height - y), matrix,
                    true);//createBitmap()方法中定义的参数x+width要小于或等于bitmap.getWidth()，y+height要小于或等于bitmap.getHeight()
        } catch (Exception e) {
            e.printStackTrace();
//            return null;
        }
        return newbmp;
    }
    public static Bitmap readBitmapFromString(String data, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        byte [] list = data.getBytes();
        BitmapFactory.decodeByteArray(list,0,list.length,options);
        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;
        int inSampleSize = 1;

        if (srcWidth > height && srcWidth > width) {
            inSampleSize  = srcWidth /width;
        } else if(srcWidth <height  && srcHeight >height  ){
            inSampleSize  = srcHeight /height ;
        }

        if(inSampleSize  <=0){
            inSampleSize  =1;
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;

        return BitmapFactory.decodeByteArray(list,0,list.length,options);
    }
    private boolean tag = false;
    private @DrawableRes int resourceid;
    public RequestBuilder placeholder(@DrawableRes int resourceid){
       tag = true;
       this.resourceid = resourceid;
        return this;

    }
    public void get(CallbackAble callbackAble){
        isGet = true;
        this.callbackAble = callbackAble;
        loadBitmap();
    }
    public interface CallbackAble{
        void callBack(Bitmap bitmap);
    }
}
