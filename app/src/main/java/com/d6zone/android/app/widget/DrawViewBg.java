package com.d6zone.android.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * author : jinjiarui
 * time   : 2020/03/03
 * desc   :
 * version:
 */
public class DrawViewBg extends View {

    private Paint paint = new Paint();
    private int width = 0;
    private int height =0;
    public DrawViewBg(Context context) {
        this(context,null);
    }

    public DrawViewBg(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DrawViewBg(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint.setAntiAlias(true);
        paint.setColor(Color.TRANSPARENT);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        RectF r = new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight());
        canvas.drawRect(r, paint);
        canvas.restore();
    }

    public void setPaintColor(int color,int w,int h){
        paint.setColor(color);
        this.width = w;
        this.height =h;
        postInvalidate();
    }
}
