package com.stephen.evaluator;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

/**
 * @author zhushuang
 * @data 2017/8/9.
 */

public class BezierEvaluator implements TypeEvaluator<PointF> {

    private PointF point1, point2;

    public BezierEvaluator(PointF startPoint, PointF endPoint) {
        this.point1 = startPoint;
        this.point2 = endPoint;
    }

    @Override
    public PointF evaluate(float fraction, PointF point0, PointF point3) {
        PointF point = new PointF();
        //实现
        point.x = point0.x * (1 - fraction) * (1 - fraction) * (1 - fraction)
                + 3 * point1.x * fraction * (1 - fraction) * (1 - fraction)
                + 3 * point2.x * fraction * fraction * (1 - fraction)
                + point3.x * fraction * fraction * fraction;

        point.y = point0.y * (1 - fraction) * (1 - fraction) * (1 - fraction)
                + 3 * point1.y * fraction * (1 - fraction) * (1 - fraction)
                + 3 * point2.y * fraction * fraction * (1 - fraction)
                + point3.y * fraction * fraction * fraction;
        return point;
    }
}
