package com.d6zone.android.app.recoder.model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 李鸿达 on 2017/5/22.
 */

public class HDCircleProgressView extends View {

    private int mMaxProgress = 60 * 10;//90个100ms
    private int mProgress = 0;
    private final int mCircleLineStrokeWidth = 10;

    private final RectF mRectF;
    private final Paint mPaint;
//    private final Paint mPaintHas;

    private int hasMax = 0;

    private String colorBg = "#F9F7F6";
    private String colorHas = "#E0E0E0";
    private String colorCurtProgress = "#F7AB00";
    private boolean isRecording = false;

    public HDCircleProgressView(Context context) {
        super(context, null);
        mRectF = new RectF();
        mPaint = new Paint();
//        mPaintHas = new Paint();
    }

    public HDCircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        mRectF = new RectF();
        mPaint = new Paint();
//        mPaintHas = new Paint();
    }

    public HDCircleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRectF = new RectF();
        mPaint = new Paint();
//        mPaintHas = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = this.getWidth();
        int height = this.getHeight();

        if (width != height) {
            int min = Math.min(width, height);
            width = min;
            height = min;
        }
        canvas.drawColor(Color.TRANSPARENT);

        //设置  当前进度画笔
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor(colorBg));
        mPaint.setStrokeWidth(mCircleLineStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);

//        mPaintHas.setAntiAlias(true);
//        mPaintHas.setColor(Color.parseColor(colorBg));
//        mPaintHas.setStrokeWidth(mCircleLineStrokeWidth);
//        mPaintHas.setStyle(Paint.Style.STROKE);

        //位置
        mRectF.left = mCircleLineStrokeWidth / 2;
        mRectF.top = mCircleLineStrokeWidth / 2;
        mRectF.right = width - mCircleLineStrokeWidth / 2;
        mRectF.bottom = height - mCircleLineStrokeWidth / 2;

        //绘制圆圈，进度条背景
        //todo 绘制整个圆圈灰色背景
        canvas.drawArc(mRectF, -90, 360, false, mPaint);

        //todo 绘制进度背景
        mPaint.setColor(Color.parseColor(colorCurtProgress));

        //绘制当前进度2
        canvas.drawArc(mRectF, -90, ((float) mProgress / mMaxProgress) * 360, false, mPaint);

        canvas.save();

        //画布移动到中心点
        canvas.translate(width / 2, height / 2);

        mPaint.setColor(Color.parseColor(colorCurtProgress));
        //计算圆的位置
        int radius = width / 2 - mCircleLineStrokeWidth / 2;//绘制圆的半径等于矩形的宽度 - 线的一半宽度
        double angle = 1.0f * mProgress / mMaxProgress * Math.PI * 2;
        //当前的进度/总进度*360=当前的角度   当前的进度/总进度*2PI=当前的弧度
        float circleX = (float) (radius * Math.cos(angle - Math.PI / 2));
        float circleY = (float) (radius * Math.sin(angle - Math.PI / 2));//从-90°开始计算角度
        //计算圆的半径
        int circleRadius = mCircleLineStrokeWidth / 2;
        mPaint.setStyle(Paint.Style.FILL);
//        //绘制末点的圆
        //绘制初始点的圆
        if(isRecording){
            canvas.drawCircle(circleX, circleY, circleRadius, mPaint);
            canvas.drawCircle(0, -radius, circleRadius, mPaint);
        }
        canvas.restore();
    }


    public void setProgressNotInUiThread(int progress) {
        this.mProgress = progress;
        this.isRecording = true;
        this.postInvalidate();
    }

    public int getmMaxProgress() {
        return mMaxProgress;
    }

    public void setmMaxProgress(int mMaxProgress) {
        this.mMaxProgress = mMaxProgress;
    }

    public int getmProgress() {
        return mProgress;
    }

    public void setmProgress(int mProgress) {
        this.mProgress = mProgress;
    }

    public void clear() {
        hasMax = 0;
        mProgress = 0;
        this.invalidate();
    }

}
