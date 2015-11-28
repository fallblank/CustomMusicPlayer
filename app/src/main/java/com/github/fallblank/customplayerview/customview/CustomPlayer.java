package com.github.fallblank.customplayerview.customview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.github.fallblank.customplayerview.R;


/**
 * Created by fallb on 2015/11/26.
 */
public class CustomPlayer extends View{

    /**
     * the listener when play/pause button is clicked,the relevant method in it will be invoked.
     */
    private PlayPauseToggle mToggle;

    /**
     * member variables for drawing cover
     */
    private Bitmap mBitmapCover;

    private Shader mCoverShader;

    private Drawable mCoverDrawable;

    private int mCoverBackground = Color.GRAY;

    private Paint mCoverPaint;

    //mIsRotating implies the music is whether playing or not.
    private boolean mIsRotating;

    private int mRotateDegree = 0;

    private static int ROTATE_VELOCITY = 1;

    private static int ROTATE_REFRESH_TIME = 10;

    private Handler mRotateHandler;

    private Runnable mRotateRunner = new Runnable() {
        @Override
        public void run() {
            if (mIsRotating) {
                updateRotate();
                mRotateHandler.postDelayed(mRotateRunner, ROTATE_REFRESH_TIME);
            }
        }
    };

    /**
     * member variables for drawing play/pause button area.
     */
    private Paint mPlayPausePaint;
    private PlayPauseDrawable mPlayPauseDrawable;
    private int mPlayPauseBackground = Color.RED;

    /**
     * RectF is symbol that the view area.
     */
    private RectF mRectF;


    public CustomPlayer(Context context) {
        super(context);
        init(context, null);
    }

    public CustomPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mPlayPausePaint = new Paint();
        mCoverPaint = new Paint();
        mRectF = new RectF();
        mIsRotating = false;
        mRotateHandler = new Handler();
        mPlayPauseDrawable = new PlayPauseDrawable(context);
        mPlayPauseDrawable.setCallback(callback);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomPlayer, 0, 0);
        if (a == null) return;
        try {
            mCoverBackground = a.getColor(R.styleable.CustomPlayer_cover_background, Color.GRAY);
            mCoverDrawable = a.getDrawable(R.styleable.CustomPlayer_cover_default);
            if (mCoverDrawable != null) {
                mBitmapCover = convertDrawableToBitmap(mCoverDrawable);
            }
        } finally {
            a.recycle();
        }

    }

    /**
     * Play pause drawable callback
     */
    Drawable.Callback callback = new Drawable.Callback() {
        @Override public void invalidateDrawable(Drawable who) {
            postInvalidate();
        }

        @Override public void scheduleDrawable(Drawable who, Runnable what, long when) {

        }

        @Override public void unscheduleDrawable(Drawable who, Runnable what) {

        }
    };


    /**
     * this method will convert a drawable object to bitmap one
     *
     * @param drawable the drawable object which will be convert to bitmap
     * @return the corresponding bitmap object.
     */
    private Bitmap convertDrawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /**
     * coordinates and size of view
     */
    private int mWidth, mHeight;
    private float mCenterX, mCenterY;
    private float mPlayPauseRadius, mCoverRadius;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        int minSize = Math.min(mWidth, mHeight);
        mWidth = minSize;
        mHeight = minSize;

        setMeasuredDimension(mWidth, mHeight);

        mCenterX = mWidth / 2f;
        mCenterY = mHeight / 2f;

        mRectF.set(20.0f, 20.0f, mWidth - 20.0f, mHeight - 20.0f);

        mPlayPauseRadius = (mWidth - 20.f) / 8.0f;
        mCoverRadius = (mWidth - 100.f) / 2;

        mPlayPauseDrawable.resize((1.2f * mPlayPauseRadius / 5.0f), (3.0f * mPlayPauseRadius / 5.0f) + 10.0f,
                (mPlayPauseRadius / 5.0f));

        mPlayPauseDrawable.setBounds(0, 0, mWidth, mHeight);

        createShader();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * create a shader to print the cover area.
     * make sure scaling the cover bit map fit the window.
     */
    private void createShader() {
        if (mBitmapCover == null) {
            mBitmapCover = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
            mBitmapCover.eraseColor(mCoverBackground);
        }
        float coverScale = ((float) mWidth) / (float) mBitmapCover.getWidth();

        mBitmapCover = Bitmap.createScaledBitmap(mBitmapCover, (int) (mBitmapCover.getWidth() * coverScale),
                (int) (mBitmapCover.getHeight() * coverScale), true);

        mCoverShader = new BitmapShader(mBitmapCover, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mCoverPaint = new Paint();
        mCoverPaint.setAntiAlias(true);
        mCoverPaint.setShader(mCoverShader);
    }


    public void setCoverDrawable(Drawable coverDrawable) {
        mCoverDrawable = coverDrawable;
        mBitmapCover = convertDrawableToBitmap(mCoverDrawable);
        createShader();
        postInvalidate();
    }



    @Override
    protected void onDraw(Canvas canvas) {
        mCoverPaint.setAntiAlias(true);
        canvas.rotate(mRotateDegree, mCenterX, mCenterY);
        canvas.drawCircle(mCenterX, mCenterY, mCoverRadius, mCoverPaint);


        mPlayPausePaint.setAntiAlias(true);
        mPlayPausePaint.setStyle(Paint.Style.FILL);
        mPlayPausePaint.setColor(mPlayPauseBackground);
        canvas.rotate(-mRotateDegree, mCenterX, mCenterY);
        canvas.drawCircle(mCenterX, mCenterY, mPlayPauseRadius, mPlayPausePaint);

        mPlayPauseDrawable.draw(canvas);
    }

    public void setBitmapCover(Bitmap bitmapCover) {
        mBitmapCover = bitmapCover;
        createShader();
        postInvalidate();
    }

    public Bitmap getBitmapCover() {
        return mBitmapCover;
    }

    /***
     * update rotate degree,once Rotate_VELOCITY(default=1) degree
     */
    private void updateRotate() {
        mRotateDegree += ROTATE_VELOCITY;
        mRotateDegree %= 360;
        postInvalidate();
    }

    public void start() {
        mIsRotating = true;
        mPlayPauseDrawable.setPlayState(mIsRotating);
        mRotateHandler.removeCallbacksAndMessages(null);
        mRotateHandler.postDelayed(mRotateRunner, ROTATE_REFRESH_TIME);
        postInvalidate();
    }

    public void pause() {
        mIsRotating = false;
        mPlayPauseDrawable.setPlayState(mIsRotating);
        postInvalidate();
    }

    public void setPlayPauseToggle(PlayPauseToggle toggle) {
        mToggle = toggle;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = mGestureDetector.onTouchEvent(event);
        return result;
    }

    /**
     * fetch gesture and response to clip the play/pause button area.
     */
    private GestureDetector mGestureDetector = new GestureDetector(CustomPlayer.this.getContext(),new GestureListener());

    private class GestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDown(MotionEvent event) {
            float x=event.getX(),y=event.getY();
            double distance = Math.pow((mCenterX-x),2)+Math.pow((mCenterY-y),2);
            if (distance<=Math.pow(mPlayPauseRadius,2)){
                return true;
            }
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (mIsRotating){
                if (mToggle!=null){
                    mToggle.pause();
                }
                pause();
            }else {
                if (mToggle!=null){
                    mToggle.play();
                }
                start();
            }
            return true;
        }
    }

}
