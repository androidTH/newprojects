package com.d6zone.android.app.widget.gift.animutils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by KathLine on 2017/1/8.
 */
public class GiftAnimationUtil {


    /**
     * @param target
     * @param star     动画起始坐标
     * @param end      动画终止坐标
     * @param duration 持续时间
     * @return 创建一个从左到右的飞入动画
     * 礼物飞入动画
     */
    public static ObjectAnimator createFlyFromLtoR(final View target, float star, float end, int duration, TimeInterpolator interpolator) {
        //1.个人信息先飞出来
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(target, "translationX",
                star, end);
        anim1.setInterpolator(interpolator);
        anim1.setDuration(duration);
        return anim1;
    }


    /**
     * @param target
     * @return 播放帧动画
     */
    public static AnimationDrawable startAnimationDrawable(ImageView target) {
        AnimationDrawable animationDrawable = (AnimationDrawable) target.getDrawable();
        if (animationDrawable != null) {
            target.setVisibility(View.VISIBLE);
            animationDrawable.start();
        }
        return animationDrawable;
    }


    /**
     * @param target
     * @param drawable 设置帧动画
     */
    public static void setAnimationDrawable(ImageView target, AnimationDrawable drawable) {

        target.setBackground(drawable);
    }


    /**
     * @param target
     * @return 送礼数字变化
     */
    public static ObjectAnimator scaleGiftNum(final TextView target) {
        PropertyValuesHolder anim4 = PropertyValuesHolder.ofFloat("scaleX",
                1f, 0.8f, 1f);
        PropertyValuesHolder anim5 = PropertyValuesHolder.ofFloat("scaleY",
                1f, 0.8f, 1f);
        PropertyValuesHolder anim6 = PropertyValuesHolder.ofFloat("alpha",
                1.0f, 0f, 1f);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(target, anim4, anim5, anim6).setDuration(0);
        return animator;
    }


    /**
     * @param target
     * @param star
     * @param end
     * @param duration
     * @param startDelay
     * @return 向上飞 淡出
     */
    public static ObjectAnimator createFadeAnimator(final View target, float star, float end, int duration, int startDelay) {

        PropertyValuesHolder translationY = PropertyValuesHolder.ofFloat("translationY", star, end);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0f);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(target, translationY, alpha);
        animator.setStartDelay(startDelay);
        animator.setDuration(duration);
        return animator;
    }

    /**
     * @param animator1
     * @param animator2
     * @return 按顺序播放动画
     */
    public static AnimatorSet startAnimation(ObjectAnimator animator1, ObjectAnimator animator2) {
        AnimatorSet animSet = new AnimatorSet();
//        animSet.playSequentially(animators);
        animSet.play(animator1).before(animator2);
        animSet.start();
        return animSet;
    }

    /**
     * @param animator1
     * @param animator2
     * @param animator3
     * @param animator4
     * @param animator5
     * @return 按顺序播放动画
     */
    public static AnimatorSet startAnimation(ObjectAnimator animator1, ObjectAnimator animator2, ObjectAnimator animator3, ObjectAnimator animator4, ObjectAnimator animator5) {
        AnimatorSet animSet = new AnimatorSet();
//        animSet.playSequentially(animators);
        animSet.play(animator1).before(animator2);
        animSet.play(animator3).after(animator2);
        animSet.play(animator4).after(animator3);
        animSet.play(animator5).after(animator4);
        animSet.start();
        return animSet;
    }


    static float[] num = {-30, -20, 0, 20, 30};//随机心形图片角度

    public static void showRedHeartAnimation(ImageView iv_animation_redheart){
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scale(iv_animation_redheart, "scaleX", 2f, 0.9f, 100, 0))
                .with(scale(iv_animation_redheart, "scaleY", 2f, 0.9f, 100, 0))
                .with(rotation(iv_animation_redheart, 0, 0, num[new Random().nextInt(4)]))
                .with(alpha(iv_animation_redheart, 0, 1, 100, 0))
                .with(scale(iv_animation_redheart, "scaleX", 0.9f, 1, 50, 150))
                .with(scale(iv_animation_redheart, "scaleY", 0.9f, 1, 50, 150))
                .with(translationY(iv_animation_redheart, 0, -600, 800, 400))
                .with(alpha(iv_animation_redheart, 1, 0, 300, 400))
                .with(scale(iv_animation_redheart, "scaleX", 1, 3f, 700, 400))
                .with(scale(iv_animation_redheart, "scaleY", 1, 3f, 700, 400));

        animatorSet.start();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
//                removeViewInLayout(iv_animation_redheart);
            }
        });
    }

    public static ObjectAnimator scale(View view, String propertyName, float from, float to, long time, long delayTime) {
        ObjectAnimator translation = ObjectAnimator.ofFloat(view
                , propertyName
                , from, to);
        translation.setInterpolator(new LinearInterpolator());
        translation.setStartDelay(delayTime);
        translation.setDuration(time);
        return translation;
    }

    public static ObjectAnimator translationX(View view, float from, float to, long time, long delayTime) {
        ObjectAnimator translation = ObjectAnimator.ofFloat(view
                , "translationX"
                , from, to);
        translation.setInterpolator(new LinearInterpolator());
        translation.setStartDelay(delayTime);
        translation.setDuration(time);
        return translation;
    }

    public static ObjectAnimator translationY(View view, float from, float to, long time, long delayTime) {
        ObjectAnimator translation = ObjectAnimator.ofFloat(view
                , "translationY"
                , from, to);
        translation.setInterpolator(new LinearInterpolator());
        translation.setStartDelay(delayTime);
        translation.setDuration(time);
        return translation;
    }

    public static ObjectAnimator alpha(View view, float from, float to, long time, long delayTime) {
        ObjectAnimator translation = ObjectAnimator.ofFloat(view
                , "alpha"
                , from, to);
        translation.setInterpolator(new LinearInterpolator());
        translation.setStartDelay(delayTime);
        translation.setDuration(time);
        return translation;
    }

    public static ObjectAnimator rotation(View view, long time, long delayTime, float... values) {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotation", values);
        rotation.setDuration(time);
        rotation.setStartDelay(delayTime);
        rotation.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {
                return input;
            }
        });
        return rotation;
    }

}
