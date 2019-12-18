package com.d6.android.app.widget.popup.blur;

import android.animation.TypeEvaluator;

/**
 * 功能描述：回弹效果
 * Created by AsiaLYF on 2018/3/22.
 */

public class KickBackAnimator implements TypeEvaluator<Float>{
    private final float s = 1.70158f;
    float mDuration = 0f;

    public void setDuration(float duration) {
        mDuration = duration;
    }

    public Float evaluate(float fraction, Float startValue, Float endValue) {
        float t = mDuration * fraction;
        float b = startValue.floatValue();
        float c = endValue.floatValue() - startValue.floatValue();
        float d = mDuration;
        float result = calculate(t, b, c, d);
        return result;
    }

    public Float calculate(float t, float b, float c, float d) {
        return c * ((t = t / d - 1) * t * ((s + 1) * t + s) + 1) + b;
    }
}