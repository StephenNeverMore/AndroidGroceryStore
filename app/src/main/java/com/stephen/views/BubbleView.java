package com.stephen.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.stephen.util.DimenUtils;

/**
 * @author zhushuang
 * @data 2017/8/4.
 */

public class BubbleView extends View {

    private Paint mPaint;
    private Paint mPointPaint;
    private PointF startP;
    private PointF endP;
    private int startRadius;
    private int endRadius;
    private double MAX_DISTANCE = DimenUtils.dp2px(300);
    private int viewWidth;
    private int viewHeight;
    private float P0X;
    private float P0Y;
    private float P1X;
    private float P1Y;
    private float P2X;
    private float P2Y;
    private float P3X;
    private float P3Y;
    private PointF pointF;
    private boolean needDraw = true;

    private ExplosionListener listener;

    private enum STATE_DISTANCE {
        STATE_IDLE,
        STATE_IN,
        STATE_OUT
    }

    private STATE_DISTANCE dragState = STATE_DISTANCE.STATE_IDLE;

    public BubbleView(Context context) {
        this(context, null);
    }

    public BubbleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 初始化画笔
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);

        startRadius = DimenUtils.dp2px(20);
        endRadius = DimenUtils.dp2px(50);

        mPointPaint = new Paint();
        mPointPaint.setColor(Color.RED);
        mPointPaint.setDither(true);
        mPointPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (viewWidth == 0 || viewHeight == 0) {
            viewWidth = getWidth();
            viewHeight = getHeight();
            startP = new PointF(viewWidth / 4, viewHeight / 4);
            endP = new PointF(viewWidth / 4, viewHeight / 4);
        }
        if (needDraw){
            canvas.drawCircle(endP.x, endP.y, endRadius, mPaint);
        }
        final double distance = getTwoPointDistance(startP, endP);
        if (distance <= MAX_DISTANCE) {
            canvas.drawCircle(startP.x, startP.y, startRadius, mPaint);
            dragState = STATE_DISTANCE.STATE_IN;
            canvas.drawPath(getBezierPath(startP, endP, 0.4f), mPaint);
            canvas.drawCircle(P0X, P0Y, 8, mPointPaint);
            canvas.drawCircle(P1X, P1Y, 8, mPointPaint);
            canvas.drawCircle(P2X, P2Y, 8, mPointPaint);
            canvas.drawCircle(P3X, P3Y, 8, mPointPaint);
            canvas.drawCircle(pointF.x, pointF.y, 8, mPointPaint);
        } else {
            dragState = STATE_DISTANCE.STATE_OUT;
        }
    }


    /**
     * 获取 Bezier 曲线
     *
     * @param startPoint
     * @param endPoint
     * @param percent
     * @return
     */
    public Path getBezierPath(PointF startPoint, PointF endPoint, float percent) {
        Path bezierPath = new Path();
        // 计算斜率
        float dx = startP.x - endP.x;
        float dy = startP.y - endP.y;
        if (dx == 0) {
            dx = 0.001f;
        }

        float tan = dy / dx;
        // 获取角a度值
        float arcTanA = (float) Math.atan(tan);
        // 依次计算 p0 , p1 , p2 , p3 点的位置
        P0X = (float) (startP.x + startRadius * Math.sin(arcTanA));
        P0Y = (float) (startP.y - startRadius * Math.cos(arcTanA));

        P1X = (float) (endP.x + endRadius * Math.sin(arcTanA));
        P1Y = (float) (endP.y - endRadius * Math.cos(arcTanA));

        P2X = (float) (endP.x - endRadius * Math.sin(arcTanA));
        P2Y = (float) (endP.y + endRadius * Math.cos(arcTanA));

        P3X = (float) (startP.x - startRadius * Math.sin(arcTanA));
        P3Y = (float) (startP.y + startRadius * Math.cos(arcTanA));

        pointF = getPointByPercent(startPoint, endPoint, percent);

        bezierPath.lineTo(P0X, P0Y);
        bezierPath.quadTo(pointF.x, pointF.y, P1X, P1Y);
        bezierPath.lineTo(P2X, P2Y);
        bezierPath.quadTo(pointF.x, pointF.y, P3X, P3Y);
        bezierPath.lineTo(P0X, P0Y);
        bezierPath.close();
        return bezierPath;
    }

    private PointF getPointByPercent(PointF startPoint, PointF endPoint, float percent) {
        PointF controlPointF = new PointF();
        final float X = endPoint.x - startPoint.x;
        final float Y = endPoint.y - startPoint.y;
        final float pointX = Math.abs(X * percent + startPoint.x);
        final float pointY = Math.abs(Y * percent + startPoint.y);
        controlPointF.set(pointX, pointY);
        return controlPointF;
    }

    private void setEndPoint(float x, float y) {
        if (endP != null) {
            endP.set(x, y);
        } else {
            endP = new PointF(x, y);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                needDraw = true;
                if (listener != null){
                    listener.startDrag();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                setEndPoint(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                if (dragState == STATE_DISTANCE.STATE_OUT) {
                    needDraw = false;
                    if (listener != null){
                        listener.releaseDragOut(event.getX(), event.getY());
                    }
                } else {
                    viewWidth = 0;
                    if (listener != null){
                        listener.releaseDragIn();
                    }
                }
                break;
        }
        invalidate();
        return true;
    }


    public double getTwoPointDistance(PointF lPoint, PointF rPoint) {
        double distance = 0;
        final float X = rPoint.x - lPoint.x;
        final float Y = rPoint.y - lPoint.y;
        distance = Math.sqrt(X * X + Y * Y);
        return distance;
    }

    public void setExplosionListener(ExplosionListener l){
        this.listener = l;
    }

    public interface ExplosionListener{
        void startDrag();
        void releaseDragIn();
        void releaseDragOut(float x, float y);
    }
}
