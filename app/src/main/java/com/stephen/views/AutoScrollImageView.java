package com.stephen.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by zhushuang on 2017/12/5.
 */

public class AutoScrollImageView extends android.support.v7.widget.AppCompatImageView {

    private int mMinDy;
    private int mDy;

    public AutoScrollImageView(Context context) {
        super(context);
    }

    public AutoScrollImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoScrollImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mMinDy = h;
    }


    public void setDy(int dy) {
        if (getDrawable() == null) {
            return;
        }
        mDy = dy - mMinDy;
        if (mDy <= 0) {
            mDy = 0;
        }
        if (mDy > getDrawable().getBounds().height() - mMinDy) {
            mDy = getDrawable().getBounds().height() - mMinDy;
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null){
            super.onDraw(canvas);
            return;
        }
        int w = getWidth();
        int h = (int) (getWidth() * 1.0f / drawable.getIntrinsicWidth() * drawable.getIntrinsicHeight());
        drawable.setBounds(0, 0, w, h);
        canvas.save();
        canvas.translate(0, -getDy());
        super.onDraw(canvas);
        canvas.restore();
    }

    public int getDy() {
        return mDy;
    }

}
