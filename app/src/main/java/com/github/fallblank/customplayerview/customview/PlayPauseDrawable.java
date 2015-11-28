package com.github.fallblank.customplayerview.customview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Property;

import com.github.fallblank.customplayerview.R;

/**
 * Created by fallb on 2015/11/27.
 */
public class PlayPauseDrawable extends Drawable {
    private Path mLeftPauseBar;
    private Path mRightPauseBar;
    private Paint mBarPaint;


    private boolean mIsPlay;

    private float mProgress;

    private static float mPauseDistance;
    private static float mPauseWidth;
    private static float mPauseHeight;
    private RectF mBound;

    private float mWidth;
    private float mHeight;


    public PlayPauseDrawable(Context context) {
        mLeftPauseBar = new Path();
        mRightPauseBar = new Path();
        mBound = new RectF();
        mBarPaint = new Paint();
        mBarPaint.setAntiAlias(true);
        mBarPaint.setColor(Color.WHITE);
        mBarPaint.setStyle(Paint.Style.FILL);

        mIsPlay = false;
        mProgress = 1;

        Resources res = context.getResources();
        mPauseDistance = res.getDimensionPixelSize(R.dimen.pause_bar_distance);
        mPauseHeight = res.getDimensionPixelSize(R.dimen.pause_bar_height);
        mPauseWidth = res.getDimensionPixelSize(R.dimen.pause_bar_width);
    }


    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mBound.set(bounds);
        mWidth = mBound.width();
        mHeight = mBound.height();
    }

    /**
     * re-size button's appearance,including width height and distance
     *
     * @param width    the width of pause bar
     * @param height   the height of pause bar
     * @param distance the distance of two pause bar
     */
    public void resize(float width, float height, float distance) {
        mPauseWidth = width;
        mPauseHeight = height;
        mPauseDistance = distance;
        invalidateSelf();
    }

    @Override
    public void draw(Canvas canvas) {
        mLeftPauseBar.rewind();
        mRightPauseBar.rewind();

        // The current distance between the two pause bars.
        final float barDist = lerp(mPauseDistance, 0, mProgress);
        // The current width of each pause bar.
        final float barWidth = lerp(mPauseWidth, mPauseHeight / 2f, mProgress);
        // The current position of the left pause bar's top left coordinate.
        final float firstBarTopLeft = lerp(0, barWidth, mProgress);
        // The current position of the right pause bar's top right coordinate.
        final float secondBarTopRight = lerp(2 * barWidth + barDist, barWidth + barDist, mProgress);

        mLeftPauseBar.moveTo(0, 0);
        mLeftPauseBar.lineTo(firstBarTopLeft, -mPauseHeight);
        mLeftPauseBar.lineTo(barWidth, -mPauseHeight);
        mLeftPauseBar.lineTo(barWidth, 0);
        mLeftPauseBar.close();

        mRightPauseBar.moveTo(barWidth + barDist, 0);
        mRightPauseBar.lineTo(barWidth + barDist, -mPauseHeight);
        mRightPauseBar.lineTo(secondBarTopRight, -mPauseHeight);
        mRightPauseBar.lineTo(2 * barWidth + barDist, 0);
        mRightPauseBar.close();

        canvas.save();

        // Translate the play button a tiny bit to the right so it looks more centered.
        canvas.translate(lerp(0, mPauseHeight / 8f, mProgress), 0);

        // (1) Pause --> Play: rotate 0 to 90 degrees clockwise.
        // (2) Play --> Pause: rotate 90 to 180 degrees clockwise.
        final float rotationProgress = mIsPlay ? 1 - mProgress : mProgress;
        final float startingRotation = mIsPlay ? 90 : 0;
        canvas.rotate(lerp(startingRotation, startingRotation + 90, rotationProgress), mWidth / 2f,
                mHeight / 2f);

        // Position the pause/play button in the center of the drawable's bounds.
        canvas.translate(mWidth / 2f - ((2 * barWidth + barDist) / 2f),
                mHeight / 2f + (mPauseHeight / 2f));

        // Draw the two bars that form the animated pause/play button.
        canvas.drawPath(mLeftPauseBar, mBarPaint);
        canvas.drawPath(mRightPauseBar, mBarPaint);

        canvas.restore();
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }


    public void setPlayState(boolean playing) {
        mIsPlay = playing;
        if (playing) {
            mProgress = 0;
        } else {
            mProgress = 1;
        }
        invalidateSelf();
    }


    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }
}
