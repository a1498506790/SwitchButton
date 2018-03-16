package com.mir.switchbutton.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author by lx
 * @github https://github.com/a1498506790
 * @data 2018-03-16
 * @desc
 */

public class SwitchButton extends View {

    private int mCloseColor = Color.parseColor("#999999");
    private int mOpenColor= Color.parseColor("#FF4081");
    private int mCircleColor = Color.parseColor("#FFFFFF");

    private Paint mOpenPaint;
    private Paint mClosePaint;
    private Paint mCirclePaint;
    private int mWidth;
    private int mHeight;
    private int mRadius;
    private Bitmap mBitmap;
    private Canvas mCanvas;

    private float mCircleDx;
    private float mDx;

    private PorterDuffXfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    private float mDownX;
    private long mStartTime;

    //是否打开
    private boolean mIsOpen = false;

    public SwitchButton(Context context) {
        this(context, null);
    }

    public SwitchButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStyle(Paint.Style.FILL);

        mOpenPaint = new Paint();
        mOpenPaint.setAntiAlias(true);
        mOpenPaint.setColor(mOpenColor);
        mOpenPaint.setStyle(Paint.Style.FILL);
        mOpenPaint.setXfermode(mXfermode);

        mClosePaint = new Paint();
        mClosePaint.setAntiAlias(true);
        mClosePaint.setColor(mCloseColor);
        mClosePaint.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        mRadius = mHeight / 2;

        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        mCanvas.drawRoundRect(0, 0, mWidth, mHeight, mRadius, mRadius, mClosePaint);
        mCanvas.drawRect(0, 0, mDx, mHeight, mOpenPaint);
        canvas.drawBitmap(mBitmap, 0, 0, null);
        canvas.drawCircle(mRadius + mCircleDx, mRadius, mRadius, mCirclePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mStartTime = System.currentTimeMillis();
                mDownX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float abs = Math.abs(moveX - mDownX);
                if (abs <= mWidth - mRadius * 2) {
                    if (moveX > mDownX && mDownX < mWidth / 2) {//从左向右滑动
                        mCircleDx = abs;
                        mDx = abs + mRadius;
                    }else if (moveX < mDownX && mDownX > mWidth / 2){ //从右向左滑动
                        mCircleDx = (mWidth - mRadius * 2) - abs;
                        mDx = mWidth - abs - mRadius;
                    }
                }else{
                    if (moveX > mDownX) {//从左向右滑动
                        mCircleDx = mWidth - mRadius * 2;
                        mDx = mWidth;
                    }else{
                        mCircleDx = 0;
                        mDx = 0;
                    }
                }
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                long endTime = System.currentTimeMillis();
                float upX = event.getX();
                if (endTime - mStartTime < 100) { //发生了点击操作
                    startAnimator();
                }else{
                    float distance = upX - mDownX;
                    float absDistance = Math.abs(distance);
                    if (distance > 0) { //从左向右滑动 滑动的距离超过了 控件宽度的一半
                        if (absDistance > (mWidth - mRadius * 2) / 2) {
                            mCircleDx = mWidth - mRadius * 2;
                            mDx = mWidth;
                            mIsOpen = true;
                        }else{
                            mDx = 0;
                            mCircleDx = 0;
                            mIsOpen = false;
                        }
                    }else{ //从右向左
                        if (absDistance > (mWidth - mRadius * 2) / 2) {
                            mDx = 0;
                            mCircleDx = 0;
                            mIsOpen = false;
                        }else{
                            mDx = mWidth;
                            mCircleDx = mWidth - mRadius * 2;
                            mIsOpen = true;
                        }
                    }
                    postInvalidate();
                    if (mOnCompleteListener != null) {
                        mOnCompleteListener.onComplete(mIsOpen);
                    }
                }
                break;

        }
        return true;
    }

    private void startAnimator(){
        ValueAnimator circleAnimator;
        if (mIsOpen) {
            circleAnimator = ValueAnimator.ofFloat(mWidth - mRadius * 2, 0);
        }else{
            circleAnimator = ValueAnimator.ofFloat(0, mWidth - mRadius * 2);
        }
        circleAnimator.setDuration(300);
        circleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mCircleDx = (float) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        circleAnimator.start();

        ValueAnimator rectFAnimator;
        if (mIsOpen) {
            rectFAnimator = ValueAnimator.ofFloat(mWidth, 0);
        }else{
            rectFAnimator = ValueAnimator.ofFloat(0, mWidth);
        }
        rectFAnimator.setDuration(300);
        rectFAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mDx = (float) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        rectFAnimator.start();
        mIsOpen = !mIsOpen;
        if (mOnCompleteListener != null) {
            mOnCompleteListener.onComplete(mIsOpen);
        }
    }

    private OnCompleteListener mOnCompleteListener;
    public interface OnCompleteListener{
        void onComplete(boolean isOpen);
    }

    public void OnCompleteListener(OnCompleteListener onCompleteListener){
        this.mOnCompleteListener = onCompleteListener;
    }
}
