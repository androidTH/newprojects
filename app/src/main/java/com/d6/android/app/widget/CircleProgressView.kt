package com.d6.android.app.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.TextView
import com.d6.android.app.R
import org.jetbrains.anko.dip
import java.lang.Math.PI

/**
 *     author : jinjiarui
 *     time   : 2019/12/13
 *     desc   :
 *     version:
 */
class CircleProgressView (context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var bgPaint: Paint? = null//绘制背景圆弧的画笔
    private var progressPaint: Paint? = null//绘制圆弧的画笔
    private var pointPaint: Paint? = null//绘制圆弧的画笔

    private var mRectF: RectF? = null//绘制圆弧的矩形区域

    private var anim: CircleBarAnim? = null

    private var progressNum: Float = 0.toFloat()//可以更新的进度条数值
    private var maxNum: Float = 0.toFloat()//进度条最大值

    private var progressColor: Int = 0//进度条圆弧颜色
    private var bgColor: Int = 0//背景圆弧颜色
    private var startAngle: Float = 0.toFloat()//背景圆弧的起始角度
    private var sweepAngle: Float = 0.toFloat()//背景圆弧扫过的角度
    private var barWidth: Float = 0.toFloat()//圆弧进度条宽度

    private var defaultSize: Int = 0//自定义View默认的宽高
    private var progressSweepAngle: Float = 0.toFloat()//进度条圆弧扫过的角度

    private var textView: TextView? = null
    private var onAnimationListener: OnAnimationListener? = null
    private var r_point = 0f
    private var pointScale = 1.6f
    private var min = 0

    init {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleBarView)

        progressColor = typedArray.getColor(R.styleable.CircleBarView_progress_color, Color.GREEN)
        bgColor = typedArray.getColor(R.styleable.CircleBarView_bg_color, Color.GRAY)
        startAngle = typedArray.getFloat(R.styleable.CircleBarView_start_angle, 0f)
        sweepAngle = typedArray.getFloat(R.styleable.CircleBarView_sweep_angle, 360f)
        barWidth = typedArray.getDimension(R.styleable.CircleBarView_bar_width, dip(10).toFloat())
        typedArray.recycle()

        progressNum = 0f
        maxNum = 100f
        defaultSize = dip(100)
        mRectF = RectF()

        progressPaint = Paint()
        progressPaint!!.style = Paint.Style.STROKE//只描边，不填充
        progressPaint!!.color = progressColor
        progressPaint!!.isAntiAlias = true//设置抗锯齿
        progressPaint!!.strokeWidth = barWidth
        progressPaint!!.strokeCap = Paint.Cap.ROUND//设置画笔为圆角

        bgPaint = Paint()
        bgPaint!!.style = Paint.Style.STROKE//只描边，不填充
        bgPaint!!.color = bgColor
        bgPaint!!.isAntiAlias = true//设置抗锯齿
        bgPaint!!.strokeWidth = barWidth
        bgPaint!!.strokeCap = Paint.Cap.ROUND


        pointPaint = Paint()
        pointPaint!!.style = Paint.Style.FILL//只描边，不填充
        pointPaint!!.color = progressColor
        pointPaint!!.isAntiAlias = true//设置抗锯齿
        pointPaint!!.strokeCap = Paint.Cap.ROUND//设置画笔为圆角

        anim = CircleBarAnim()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val height = measureSize(defaultSize, heightMeasureSpec)
        val width = measureSize(defaultSize, widthMeasureSpec)
        min = Math.min(width, height)// 获取View最短边的长度
        setMeasuredDimension(min, min)// 强制改View为以最短边为长度的正方形

        if (min >= barWidth * 2) {
            mRectF!!.set(barWidth / 2+barWidth*pointScale/2, barWidth / 2+barWidth*pointScale/2,
                    min - barWidth / 2-barWidth*pointScale/2, min - barWidth / 2-barWidth*pointScale/2)
        }
        r_point = (min - barWidth) / 2-barWidth*pointScale/2
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawArc(mRectF!!, startAngle, sweepAngle, false, bgPaint!!)
        canvas.drawArc(mRectF!!, startAngle, progressSweepAngle, false, progressPaint!!)
//        var gree = (progressSweepAngle+startAngle)%360
//        var x = min/2 + Math.sqrt((Math.pow(r_point.toDouble(), 2.00) - Math.pow(r_point * Math.sin(PI /180*(gree).toDouble()), 2.00))).toFloat() * if (gree > 90 && gree < 270) {
//            -1.0f
//        } else {
//            1.0f
//        }
//        var y = min/2 + Math.sqrt((Math.pow(r_point.toDouble(), 2.00) - Math.pow(r_point * Math.cos(PI/180*(gree).toDouble()), 2.00))).toFloat() * if (gree > 180) {
//            -1.0f
//        } else {
//            1.0f
//        }
//        canvas.drawCircle(x, y, barWidth/2*pointScale , pointPaint)
    }

    inner class CircleBarAnim : Animation() {

        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {//interpolatedTime从0渐变成1,到1时结束动画,持续时间由setDuration（time）方法设置
            super.applyTransformation(interpolatedTime, t)
            progressSweepAngle = interpolatedTime * sweepAngle * progressNum / maxNum
            if (onAnimationListener != null) {
                if (textView != null) {
                    textView!!.text = onAnimationListener!!.howToChangeText(interpolatedTime, progressNum, maxNum)
                }
                onAnimationListener!!.howTiChangeProgressColor(progressPaint, interpolatedTime, progressNum, maxNum)
            }
            postInvalidate()
        }
    }

    private fun measureSize(defaultSize: Int, measureSpec: Int): Int {
        var result = defaultSize
        val specMode = View.MeasureSpec.getMode(measureSpec)
        val specSize = View.MeasureSpec.getSize(measureSpec)

        if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize
        } else if (specMode == View.MeasureSpec.AT_MOST) {
            result = Math.min(result, specSize)
        }
        return result
    }


    /**
     * 设置进度条最大值
     * @param maxNum
     */
    fun setMaxNum(maxNum: Float) {
        this.maxNum = maxNum
    }

    /**
     * 设置进度条数值
     * @param progressNum 进度条数值
     * @param time 动画持续时间
     */
    fun setProgressNum(progressNum: Float, time: Int) {
        this.progressNum = progressNum
        anim!!.duration = time.toLong()
        this.startAnimation(anim)
    }

    /**
     * 设置显示文字的TextView
     * @param textView
     */
    fun setTextView(textView: TextView) {
        this.textView = textView
    }

    interface OnAnimationListener {

        /**
         * 如何处理要显示的文字内容
         * @param interpolatedTime 从0渐变成1,到1时结束动画
         * @param updateNum 进度条数值
         * @param maxNum 进度条最大值
         * @return
         */
        fun howToChangeText(interpolatedTime: Float, updateNum: Float, maxNum: Float): String

        /**
         * 如何处理进度条的颜色
         * @param paint 进度条画笔
         * @param interpolatedTime 从0渐变成1,到1时结束动画
         * @param updateNum 进度条数值
         * @param maxNum 进度条最大值
         */
        fun howTiChangeProgressColor(paint: Paint?, interpolatedTime: Float, updateNum: Float, maxNum: Float)

    }

    fun setOnAnimationListener(onAnimationListener: OnAnimationListener) {
        this.onAnimationListener = onAnimationListener
    }
}