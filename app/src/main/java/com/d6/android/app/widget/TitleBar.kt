package com.d6.android.app.widget

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.text.InputType
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.d6.android.app.R
import org.jetbrains.anko.*
import java.util.*

/**
 * 标题栏
 * Created by liaoxiang on 16/3/22.
 */
class TitleBar : RelativeLayout {

    constructor(context: Context):super(context)
    constructor(context: Context,attributeSet: AttributeSet):super(context,attributeSet)
    constructor(context: Context,attributeSet: AttributeSet,defStyleAttr:Int):super(context,attributeSet,defStyleAttr)
    private var hasLeft = true
    val leftView: TextView by lazy {
        textView {titleView
            textSize = 14f
            textColor = ContextCompat.getColor(context, R.color.textColor)
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
            maxWidth = dip(80)
            gravity = Gravity.CENTER
        }
    }
    val titleView: EditText by lazy {
        editText {
            setTextSize(TypedValue.COMPLEX_UNIT_DIP,17f)
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
            gravity = Gravity.CENTER
            textColor = ContextCompat.getColor(context, R.color.textColor)
        }
    }
    private val rightViews = ArrayList<View>()

    init {
        //宽度全屏，高度46dp
        layoutParams = ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, dip(46))
        //添加leftView
        leftView
        val param = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        param.bottomMargin = dip(4)
        param.topMargin = dip(4)
//        param.gravity = Gravity.CENTER_VERTICAL
        param.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)
        showLeft(hasLeft)
        leftView.setPadding(dip(16), 0, dip(8), 0)
        leftView.layoutParams = param
        leftView.compoundDrawablePadding = dip(4)
        leftView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.back, 0, 0, 0)
        //添加TitleView
        titleView.setPadding(dip(4), 0, dip(4), 0)
        val param1 = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
        param1.bottomMargin = dip(4)
        param1.topMargin = dip(4)
        param1.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
//        param1.gravity = Gravity.CENTER_VERTICAL
        titleView.layoutParams = param1

        //默认不可输入
        titleView.inputType = InputType.TYPE_NULL
        titleView.isEnabled = false
        //默认白色
        titleView.backgroundDrawable = null
        //默认左键点击关闭页面
        leftView.setOnClickListener {
            (context as Activity ).onBackPressed()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        var lw = 0
        if (hasLeft) {
            lw = leftView.measuredWidth
        }
        var rw = 0
        if (isHasRight) {
            for (view in rightViews) {
                if (view.visibility != View.GONE) {
                    val rightMargin = (view.layoutParams as RelativeLayout.LayoutParams).rightMargin
                    val temp = rw
                    if (rightMargin != temp) {
                        (view.layoutParams as RelativeLayout.LayoutParams).rightMargin = temp
                    }
                    rw += view.measuredWidth + 5
                }
            }
        }
        val leftMargin = (titleView.layoutParams as RelativeLayout.LayoutParams).leftMargin
        val rightMargin = (titleView.layoutParams as RelativeLayout.LayoutParams).rightMargin
        val newMargin = Math.max(lw, rw) + 5
//                System.err.println("onlayout-------$leftMargin--->$rightMargin")
        if (leftMargin != newMargin || rightMargin != newMargin) {
            (titleView.layoutParams as RelativeLayout.LayoutParams).leftMargin = newMargin
            (titleView.layoutParams as RelativeLayout.LayoutParams).rightMargin = newMargin
            titleView.requestLayout()
        }
    }

    fun showLeft(show: Boolean) {
        hasLeft = show
        leftView.visibility = if (show) View.VISIBLE else View.GONE
    }

    fun setCanEditable(canEditable: Boolean) {
        if (canEditable) {
            titleView.inputType = InputType.TYPE_CLASS_TEXT
            titleView.isEnabled = true
        }
    }

    fun setTitle(s: CharSequence) {
        titleView.setText(s)
    }

    fun setTitleColor(@ColorRes color: Int) {
        titleView.setTextColor(ContextCompat.getColor(context, color))
    }

    fun setTitle(s: CharSequence, size: Float) {
        titleView.setText(s)
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
    }

    fun addRightButton(title: String? = null, rightId: Int = 0, onClickListener: View.OnClickListener) {
        //        hasRight = true;
        val padding = dip(16)
        val w = rightViews
                .filter { it.visibility != View.GONE }
                .sumBy { it.measuredWidth + 5 }
        val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT)

        params.rightMargin = w
//        params. = Gravity.RIGHT or Gravity.CENTER_VERTICAL
        params.addRule(ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
        val rightView = TextView(context)
        if (title != null) {
            rightView.text = title
        }
        rightView.setTextColor(Color.parseColor("#333333"))
        rightView.setPadding(padding / 2, 0, padding, 0)
        rightView.gravity = Gravity.CENTER_VERTICAL
        rightView.layoutParams = params
        var right: Drawable? = null
        if (rightId > 0) {
            right = ContextCompat.getDrawable(context, rightId)
        }
        rightView.setCompoundDrawablesWithIntrinsicBounds(null, null, right, null)
        rightView.setOnClickListener(onClickListener)
        addView(rightView)
        rightViews.add(rightView)
    }

    /**
     * 设置右边第p个按钮（0开始）的文字，图片
     */
    fun setRightButton(position: Int=0, title: String, resId: Int = 0) {
        if (isHasRight && rightViews.size > position && position >= 0) {
            (rightViews[position] as TextView).text = title
            if (resId > 0) {
                val drawable = ContextCompat.getDrawable(context, resId)
                (rightViews[position] as TextView).setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
            }
        }
    }

    fun getRightButton(position: Int): TextView {
        val view = rightViews[position]
        if (view is TextView) {
            return view
        }
        return TextView(context)
    }

    fun hideRightButton(position: Int, hide: Boolean) {
        if (isHasRight && rightViews.size > position && position >= 0) {
            rightViews[position].visibility = if (hide) View.GONE else View.VISIBLE
        }
    }

    fun hideAllRightButton() {
        if (isHasRight) {
            for (view in rightViews) {
                view.visibility = View.GONE
            }
        }
    }

    fun leftClick(l:((v:View)->Unit)) = leftView.setOnClickListener(l)

    private val isHasRight: Boolean
        get() = rightViews.size > 0
}