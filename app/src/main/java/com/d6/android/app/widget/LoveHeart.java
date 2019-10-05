package com.d6.android.app.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.d6.android.app.R;

import java.util.Random;

/**
 * author : jinjiarui
 * time   : 2019/10/05
 * desc   :
 * version:
 */
public class LoveHeart extends RelativeLayout {
    private Context mContext;
    float[] num = {-30, -20, 0, 20, 30};//随机心形图片角度

    ImageView imageView;
    public LoveHeart(Context context) {
        super(context);
        initView(context);
    }

    public LoveHeart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LoveHeart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        imageView = new ImageView(mContext);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        imageView.setLayoutParams(params);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        LayoutParams params = new LayoutParams(300, 300);
//        params.leftMargin = (int) event.getX() - 150;
//        params.topMargin = (int) event.getY() - 300;
//        imageView.setLayoutParams(params);
//        imageView.setImageDrawable(getResources().getDrawable(R.mipmap.animation_redheart));
//        addView(imageView);
//
//        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.play(scaleXY(imageView, "scaleX", 2f, 0.9f, 100, 0))
//                //缩放动画 , Y轴2倍缩小至0.9倍
//                .with(scaleXY(imageView, "scaleY", 2f, 0.9f, 100, 0))
//                //旋转动画 , 随机旋转角度
//                .with(rotation(imageView, 0, 0))
//                //透明度动画 , 透明度从0-1
//                .with(alpha(imageView, 0, 1, 100, 0))
//                //缩放动画 , X轴0.9倍缩小至1倍
//                .with(scaleXY(imageView, "scaleX", 0.9f, 1, 50, 150))
//                //缩放动画 , Y轴0.9倍缩小至1倍
//                .with(scaleXY(imageView, "scaleY", 0.9f, 1, 50, 150))
//                //平移动画 , Y轴从0向上移动700单位
//                .with(translationY(imageView, "translationY", 0, -700, 800, 400))
//                //透明度动画 , 透明度从1-0
//                .with(alpha(imageView, 1, 0, 400, 400))
//                //缩放动画 , X轴1倍放大至3倍
//                .with(scaleXY(imageView, "scaleX", 1, 3f, 800, 400))
//                //缩放动画 , Y轴1倍放大至3倍
//                .with(scaleXY(imageView, "scaleY", 1, 3f, 800, 400));
//        animatorSet.start();
//        animatorSet.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                removeViewInLayout(imageView);
//                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
//                Bitmap bmp = drawable.getBitmap();
//                if (null != bmp && !bmp.isRecycled()) {
//                    bmp.recycle();
//                    bmp = null;
//                }
//            }
//        });
        return super.onTouchEvent(event);
    }

    public void showAnimationRedHeart(View view){
//        int[] location = new int[2];
//        view.getLocationOnScreen(location);
//        LayoutParams params = new LayoutParams(300, 300);
//        params.leftMargin = (int) location[0] - 150;
//        params.topMargin = (int)location[1] - 300;
//        imageView.setLayoutParams(params);

        LayoutParams params = new LayoutParams(250, 250);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        imageView.setLayoutParams(params);
        imageView.setImageDrawable(getResources().getDrawable(R.mipmap.animation_redheart));
        addView(imageView);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleXY(imageView, "scaleX", 2f, 0.9f, 100, 0))
                //缩放动画 , Y轴2倍缩小至0.9倍
                .with(scaleXY(imageView, "scaleY", 2f, 0.9f, 100, 0))
                //旋转动画 , 随机旋转角度
                .with(rotation(imageView, 0, 0))
                //透明度动画 , 透明度从0-1
                .with(alpha(imageView, 0, 1, 100, 0))
                //缩放动画 , X轴0.9倍缩小至1倍
                .with(scaleXY(imageView, "scaleX", 0.9f, 1, 50, 150))
                //缩放动画 , Y轴0.9倍缩小至1倍
                .with(scaleXY(imageView, "scaleY", 0.9f, 1, 50, 150))
                //平移动画 , Y轴从0向上移动700单位
                .with(translationY(imageView, "translationY", 0, -700, 800, 400))
                //透明度动画 , 透明度从1-0
                .with(alpha(imageView, 1, 0, 400, 400))
                //缩放动画 , X轴1倍放大至3倍
                .with(scaleXY(imageView, "scaleX", 1, 3f, 800, 400))
                //缩放动画 , Y轴1倍放大至3倍
                .with(scaleXY(imageView, "scaleY", 1, 3f, 800, 400));
        animatorSet.start();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                removeViewInLayout(imageView);
            }
        });
    }

//    public static ObjectAnimator scale(View view, String propertyName, float from, float to, long time, long delayTime) {
//        ObjectAnimator translation = ObjectAnimator.ofFloat(view
//                , propertyName
//                , from, to);
//        translation.setInterpolator(new LinearInterpolator());
//        translation.setStartDelay(delayTime);
//        translation.setDuration(time);
//        return translation;
//    }
//
//    public static ObjectAnimator translationX(View view, float from, float to, long time, long delayTime) {
//        ObjectAnimator translation = ObjectAnimator.ofFloat(view
//                , "translationX"
//                , from, to);
//        translation.setInterpolator(new LinearInterpolator());
//        translation.setStartDelay(delayTime);
//        translation.setDuration(time);
//        return translation;
//    }
//
//    public static ObjectAnimator translationY(View view, float from, float to, long time, long delayTime) {
//        ObjectAnimator translation = ObjectAnimator.ofFloat(view
//                , "translationY"
//                , from, to);
//        translation.setInterpolator(new LinearInterpolator());
//        translation.setStartDelay(delayTime);
//        translation.setDuration(time);
//        return translation;
//    }
//
//    public static ObjectAnimator alpha(View view, float from, float to, long time, long delayTime) {
//        ObjectAnimator translation = ObjectAnimator.ofFloat(view
//                , "alpha"
//                , from, to);
//        translation.setInterpolator(new LinearInterpolator());
//        translation.setStartDelay(delayTime);
//        translation.setDuration(time);
//        return translation;
//    }
//
//    public static ObjectAnimator rotation(View view, long time, long delayTime, float... values) {
//        ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotation", values);
//        rotation.setDuration(time);
//        rotation.setStartDelay(delayTime);
//        rotation.setInterpolator(new TimeInterpolator() {
//            @Override
//            public float getInterpolation(float input) {
//                return input;
//            }
//        });
//        return rotation;
//    }

    /**
     *   X轴或Y轴 缩放
     * @param view
     * @param propertyName
     * @param from
     * @param to
     * @param time
     * @param delayTime
     * @return
     */
    public  ObjectAnimator scaleXY(View view, String propertyName, float from, float to, long time, long delayTime) {
        ObjectAnimator _scaleXY=  ObjectAnimator.ofFloat(view,propertyName,from,to);
        ////设置插值器为 匀速(补充：有加速、先加后减等)
        _scaleXY.setInterpolator(new LinearInterpolator());
        //设置开始前延迟
        _scaleXY.setStartDelay(delayTime);
        //设置动画持续时间
        _scaleXY.setDuration(time);
        _scaleXY.start();
        return _scaleXY;
    }

    /**
     *   X轴或Y轴 平移
     * @param view
     * @param from
     * @param to
     * @param time
     * @param delayTime
     * @return
     */
    public  ObjectAnimator translationY(View view,  String propertyName,float from, float to, long time, long delayTime) {
        ObjectAnimator translation = ObjectAnimator.ofFloat(view, propertyName, from, to);
        translation.setInterpolator(new LinearInterpolator());
        translation.setStartDelay(delayTime);
        translation.setDuration(time);
        return translation;
    }

    /**
     *   透明化处理
     * @param view
     * @param from
     * @param to
     * @param time
     * @param delayTime
     * @return
     */
    public  ObjectAnimator alpha(View view, float from, float to, long time, long delayTime) {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", from, to);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setStartDelay(delayTime);
        alpha.setDuration(time);
        return alpha;
    }

    /**
     *  旋转
     * @param view
     * @param time
     * @param delayTime
     * @return
     */
    public ObjectAnimator rotation(View view, long time, long delayTime) {
        //随机旋转角度
        float angle= num[new Random().nextInt(4)];
        ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotation", angle);
        rotation.setDuration(time);
        rotation.setStartDelay(delayTime);
        rotation.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {
                //抖动系数越小幅度越大 0-1
                return input;
            }
        });
        return rotation;
    }
}
