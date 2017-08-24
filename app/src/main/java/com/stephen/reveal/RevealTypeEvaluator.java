package com.stephen.reveal;

import android.animation.TypeEvaluator;

/**
 * @author zhushuang
 * @data 2017/8/23.
 */

public class RevealTypeEvaluator implements TypeEvaluator<Float> {

    @Override
    public Float evaluate(float v, Float start, Float end) {
        return (end - start) * v + start;
    }
}
