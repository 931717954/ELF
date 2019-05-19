package com.zixuan.elf.customizeView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.pictureloadlibrary.Flow;
import com.example.pictureloadlibrary.RequestBuilder;
import com.zixuan.elf.R;

public class GramophoneView extends View {


    private final float DEFAULT_DISK_ROTATE_SPEED = 1f;
    private final float DEFAULT_PICTURE_RADIUS = 200; // 中间图片默认半径
    private final float DEFAULT_PAUSE_NEEDLE_DEGREE = -45; // 暂停状态时唱针的旋转角度
    private final float DEFAULT_PLAYING_NEEDLE_DEGREE = -15; // 播放状态时唱针的旋转角度

    private int pictureRadius;



    private float halfMeasureWidth;

    private float diskRotateSpeed; // 唱片旋转速度
    private Bitmap pictureBitmap;
    private Paint diskPaint;

    //状态控制
    private boolean isPlaying;
    private float currentDiskDegree; // 唱片旋转角度
    private float currentNeddleDegree = DEFAULT_PLAYING_NEEDLE_DEGREE; // 唱针旋转角度

    public GramophoneView(Context context) {
        this(context, null);
    }

    public GramophoneView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        diskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.GramophoneView);


        pictureRadius = (int) typedArray.getDimension(R.styleable.GramophoneView_picture_radiu, DEFAULT_PICTURE_RADIUS);
        diskRotateSpeed = typedArray.getFloat(R.styleable.GramophoneView_disk_rotate_speed, DEFAULT_DISK_ROTATE_SPEED);
        Drawable drawable = typedArray.getDrawable(R.styleable.GramophoneView_src);
        if (drawable == null) {
            pictureBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        } else {
            pictureBitmap = ((BitmapDrawable) drawable).getBitmap();
        }






    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = (pictureRadius ) * 2;
        int hight = (pictureRadius ) * 2 ;

        int measureWidth = resolveSize(width, widthMeasureSpec);
        int measureHeight = resolveSize(hight, heightMeasureSpec);

        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        halfMeasureWidth = getMeasuredWidth() >> 1;
        drawDisk(canvas);
//        drawNeedle(canvas);
        if (currentNeddleDegree > DEFAULT_PAUSE_NEEDLE_DEGREE && isPlaying) {
            invalidate();
        }
    }

    private void drawDisk(Canvas canvas) {
        currentDiskDegree = currentDiskDegree % 360 + diskRotateSpeed;

        canvas.save();
        canvas.translate(halfMeasureWidth, pictureRadius);
        canvas.rotate(currentDiskDegree);
//        diskPaint.setColor(Color.BLACK);
//        diskPaint.setStyle(Paint.Style.STROKE);
        diskPaint.setStrokeWidth(pictureRadius / 2);
        canvas.drawCircle(0, 0, pictureRadius , diskPaint);


        Path path = new Path(); // 裁剪的path路径 （为了裁剪成圆形图片，其实是将画布剪裁成了圆形）
        path.addCircle(0, 0, pictureRadius, Path.Direction.CW);
        canvas.clipPath(path);

        Rect src = new Rect(); //将要画bitmap的那个范围
        src.set(0, 0, pictureBitmap.getWidth(), pictureBitmap.getHeight());
        Rect dst = new Rect();
        dst.set(-pictureRadius, -pictureRadius, pictureRadius, pictureRadius); //将要将bitmap画要坐标系的那个位置
        canvas.drawBitmap(pictureBitmap, src, dst, null);
        canvas.restore();
    }



    public void pauseOrstart() {
        isPlaying = !isPlaying;
        invalidate();
    }
    public void pause(){
        if(isPlaying){
            Log.d("lzx", "pause: "+isPlaying);
            isPlaying = !isPlaying;
            invalidate();
        }
    }
    public void start(){
        if(!isPlaying){
            isPlaying = !isPlaying;
            invalidate();
        }
    }

    public void setPictureRadius(int pictureRadius) {
        this.pictureRadius = pictureRadius;
    }



    public void setDiskRotateSpeed(float diskRotateSpeed) {
        this.diskRotateSpeed = diskRotateSpeed;
    }


    public void setPictureRes(int resId) {
        pictureBitmap = BitmapFactory.decodeResource(getContext().getResources(), resId);
        invalidate();
    }
    public void setPictureByBitmap(Bitmap bitmap){
        pictureBitmap = bitmap;
        invalidate();
    }
    public void setPictureUrl(String url){
        Log.d("MyTag", "setPictureUrl: ");
        Flow.with(getContext()).load(url).get(callbackAble);
    }
    RequestBuilder.CallbackAble callbackAble = new RequestBuilder.CallbackAble() {
        @Override
        public void callBack(Bitmap bitmap) {
            Log.d("MyTag", "callBack: ");
            pictureBitmap = bitmap;
            invalidate();
        }
    };
}
