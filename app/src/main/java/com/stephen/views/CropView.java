package com.stephen.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by zhushuang on 2017/11/3.
 */

public class CropView extends View {

    private Paint linePaint;
    private Paint pointPaint;
    private static final int DEFAULT_CROP_COUNT = 3;
    private int lineWidth = DEFAULT_LINE_WIDTH;
    private PointF leftTopPoint;
    private PointF rightBottomPoint;
    private static final int DEFAULT_LINE_WIDTH = 2;
    private float width;
    private float height;
    private boolean isCropEnabled = true;
    private static final int DEFAULT_TOUCH_SENSITIVE_AREA = 100;
    private TouchArea mTouchArea = TouchArea.OUT_OF_BOUNDS;
    private float mLastX = 0f;
    private float mLastY = 0f;
    private CropCallback cropCallback;

    private enum TouchArea {
        OUT_OF_BOUNDS,
        LEFT_TOP,
        LEFT_BOTTOM,
        CENTER,
        RIGHT_TOP,
        RIGHT_BOTTOM
    }

    public CropView(Context context) {
        this(context, null);
    }

    public CropView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.WHITE);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(lineWidth);

        pointPaint = new Paint();
        pointPaint.setAntiAlias(true);
        pointPaint.setColor(Color.WHITE);
        pointPaint.setStyle(Paint.Style.FILL);

        leftTopPoint = new PointF(0, 0);
        rightBottomPoint = new PointF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        final int viewHeight = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(viewWidth, viewHeight);

        width = viewWidth - getPaddingLeft() - getPaddingRight();
        height = viewHeight - getPaddingTop() - getPaddingBottom();
        rightBottomPoint.set(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //draw horizontal lines
        for (int i = 0; i <= DEFAULT_CROP_COUNT; i++) {
            canvas.drawLine(leftTopPoint.x, (rightBottomPoint.y - leftTopPoint.y) / DEFAULT_CROP_COUNT * i + leftTopPoint.y, rightBottomPoint.x, (rightBottomPoint.y - leftTopPoint.y) / DEFAULT_CROP_COUNT * i + leftTopPoint.y, linePaint);
        }

        //draw vertical lines
        for (int i = 0; i <= DEFAULT_CROP_COUNT; i++) {
            canvas.drawLine((rightBottomPoint.x - leftTopPoint.x) / DEFAULT_CROP_COUNT * i + leftTopPoint.x, leftTopPoint.y, (rightBottomPoint.x - leftTopPoint.x) / DEFAULT_CROP_COUNT * i + leftTopPoint.x, rightBottomPoint.y, linePaint);
        }

        //draw points
        final int pointRadius = lineWidth * 10;
        canvas.drawCircle(leftTopPoint.x, leftTopPoint.y, pointRadius, pointPaint);
        canvas.drawCircle(rightBottomPoint.x, leftTopPoint.y, pointRadius, pointPaint);
        canvas.drawCircle(rightBottomPoint.x, rightBottomPoint.y, pointRadius, pointPaint);
        canvas.drawCircle(leftTopPoint.x, rightBottomPoint.y, pointRadius, pointPaint);

    }


    public void setCropEnabled(boolean enabled) {
        this.isCropEnabled = enabled;
    }

    public void setCropCallback(CropCallback cropCallback) {
        this.cropCallback = cropCallback;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isCropEnabled) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onActionDown(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                onActionMove(event);
                return true;
            case MotionEvent.ACTION_CANCEL:
                onActionCancel(event);
                return true;
            case MotionEvent.ACTION_UP:
                onActionUp(event);
                return true;
        }
        return false;
    }

    private void onActionDown(MotionEvent event) {
        mLastX = event.getX();
        mLastY = event.getY();
        checkTouchArea(event.getX(), event.getY());
    }

    private void onActionMove(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();
        switch (mTouchArea) {
            case LEFT_TOP:
                if (isMoveValid(x, y) && x <= rightBottomPoint.x - 2 * DEFAULT_TOUCH_SENSITIVE_AREA && y <= rightBottomPoint.y - 2 * DEFAULT_TOUCH_SENSITIVE_AREA) {
                    updatePointPosition(x, y, rightBottomPoint.x, rightBottomPoint.y);
                }
                break;
            case LEFT_BOTTOM:
                if (isMoveValid(x, y) && x <= rightBottomPoint.x - 2 * DEFAULT_TOUCH_SENSITIVE_AREA && y >= rightBottomPoint.y - 2 * DEFAULT_TOUCH_SENSITIVE_AREA) {
                    updatePointPosition(x, leftTopPoint.y, rightBottomPoint.x, y);
                }
                break;
            case RIGHT_BOTTOM:
                if (isMoveValid(x, y) && x >= leftTopPoint.x + 2 * DEFAULT_TOUCH_SENSITIVE_AREA && y >= leftTopPoint.y + 2 * DEFAULT_TOUCH_SENSITIVE_AREA) {
                    updatePointPosition(leftTopPoint.x, leftTopPoint.y, x, y);
                }
                break;
            case RIGHT_TOP:
                if (isMoveValid(x, y) && x >= leftTopPoint.x + 2 * DEFAULT_TOUCH_SENSITIVE_AREA && y <= rightBottomPoint.y - 2 * DEFAULT_TOUCH_SENSITIVE_AREA) {
                    updatePointPosition(leftTopPoint.x, y, x, rightBottomPoint.y);
                }
                break;
            case CENTER:
                if (isMoveValid(x, y)) {
                    float diffX = x - mLastX;
                    float diffY = y - mLastY;
                    final float[] validOffset = getValidOffset(diffX, diffY);
                    updatePointPosition(leftTopPoint.x + validOffset[0], leftTopPoint.y + validOffset[1], rightBottomPoint.x + validOffset[0], rightBottomPoint.y + validOffset[1]);
                }
                break;
            case OUT_OF_BOUNDS:
                break;
            default:
                break;
        }
        mLastX = x;
        mLastY = y;
    }

    private float[] getValidOffset(float diffX, float diffY) {
        float offsetX = diffX / 2;
        float offsetY = diffY / 2;
        float[] offset = {offsetX, offsetY};
        float newLeftTopPointX = leftTopPoint.x + offsetX;
        float newLeftTopPointY = leftTopPoint.y + offsetY;
        float newRightBottomPointX = rightBottomPoint.x + offsetX;
        float newRightBottomPointY = rightBottomPoint.y + offsetY;
        if (isMoveValid(newLeftTopPointX, newLeftTopPointY) && isMoveValid(newRightBottomPointX, newRightBottomPointY)) {
            return offset;
        } else {
            return getValidOffset(offsetX, offsetY);
        }
    }

    private void updatePointPosition(float x, float y, float x1, float y1) {
        leftTopPoint.set(x, y);
        rightBottomPoint.set(x1, y1);
        invalidate();
    }

    private boolean isMoveValid(float x, float y) {
        return x >= 0 && y >= 0 && x <= width && y <= height;
    }

    private void onActionCancel(MotionEvent event) {
        mTouchArea = TouchArea.OUT_OF_BOUNDS;
        invalidate();
        if (cropCallback != null) {
            cropCallback.updatePoint(leftTopPoint.x, leftTopPoint.y, rightBottomPoint.x, rightBottomPoint.y, width, height);
        }
    }

    private void onActionUp(MotionEvent event) {
        mTouchArea = TouchArea.OUT_OF_BOUNDS;
        invalidate();
        if (cropCallback != null) {
            cropCallback.updatePoint(leftTopPoint.x, leftTopPoint.y, rightBottomPoint.x, rightBottomPoint.y, width, height);
        }
    }

    private void checkTouchArea(float x, float y) {

        if (isInsideCornerLeftTop(x, y)) {
            mTouchArea = TouchArea.LEFT_TOP;
            return;
        }
        if (isInsideCornerRightTop(x, y)) {
            mTouchArea = TouchArea.RIGHT_TOP;
            return;
        }
        if (isInsideCornerLeftBottom(x, y)) {
            mTouchArea = TouchArea.LEFT_BOTTOM;
            return;
        }
        if (isInsideCornerRightBottom(x, y)) {
            mTouchArea = TouchArea.RIGHT_BOTTOM;
            return;
        }
        if (isInsideCenter(x, y)) {
            mTouchArea = TouchArea.CENTER;
            return;
        }
    }

    private boolean isInsideCornerLeftTop(float x, float y) {
        return leftTopPoint.x - DEFAULT_TOUCH_SENSITIVE_AREA <= x && x <= leftTopPoint.x + DEFAULT_TOUCH_SENSITIVE_AREA && leftTopPoint.y - DEFAULT_TOUCH_SENSITIVE_AREA <= y && y <= leftTopPoint.y + DEFAULT_TOUCH_SENSITIVE_AREA;
    }

    private boolean isInsideCornerRightTop(float x, float y) {
        return rightBottomPoint.x - DEFAULT_TOUCH_SENSITIVE_AREA <= x && x <= rightBottomPoint.x + DEFAULT_TOUCH_SENSITIVE_AREA && leftTopPoint.y - DEFAULT_TOUCH_SENSITIVE_AREA <= y && y <= leftTopPoint.y + DEFAULT_TOUCH_SENSITIVE_AREA;
    }

    private boolean isInsideCornerLeftBottom(float x, float y) {
        return leftTopPoint.x - DEFAULT_TOUCH_SENSITIVE_AREA <= x && x <= leftTopPoint.x + DEFAULT_TOUCH_SENSITIVE_AREA && rightBottomPoint.y - DEFAULT_TOUCH_SENSITIVE_AREA <= y && y <= rightBottomPoint.y + DEFAULT_TOUCH_SENSITIVE_AREA;
    }

    private boolean isInsideCornerRightBottom(float x, float y) {
        return rightBottomPoint.x - DEFAULT_TOUCH_SENSITIVE_AREA <= x && x <= rightBottomPoint.x + DEFAULT_TOUCH_SENSITIVE_AREA && rightBottomPoint.y - DEFAULT_TOUCH_SENSITIVE_AREA <= y && y <= rightBottomPoint.y + DEFAULT_TOUCH_SENSITIVE_AREA;
    }

    private boolean isInsideCenter(float x, float y) {
        final float minX = (rightBottomPoint.x - leftTopPoint.x) / DEFAULT_CROP_COUNT + leftTopPoint.x;
        final float maxX = (rightBottomPoint.x - leftTopPoint.x) / DEFAULT_CROP_COUNT * (DEFAULT_CROP_COUNT - 1) + leftTopPoint.x;
        final float minY = (rightBottomPoint.y - leftTopPoint.y) / DEFAULT_CROP_COUNT + leftTopPoint.y;
        final float maxY = (rightBottomPoint.y - leftTopPoint.y) / DEFAULT_CROP_COUNT * (DEFAULT_CROP_COUNT - 1) + leftTopPoint.y;
        return minX <= x && x <= maxX && minY <= y && y <= maxY;
    }


    public interface CropCallback {
        void updatePoint(float leftTopX, float leftTopY, float rightBottomX, float rightBottomY, float maxX, float maxY);
    }
}
