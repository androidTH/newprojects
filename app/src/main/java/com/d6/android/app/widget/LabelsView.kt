package com.d6.android.app.widget

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.d6.android.app.R

import java.util.ArrayList

class LabelsView : ViewGroup, View.OnClickListener {

    private var mContext: Context? = null

    private var mTextColor: ColorStateList? = null
    private var mTextSize: Float = 0.toFloat()
    private var mLabelBg: Drawable? = null
    var textPaddingLeft: Int = 0
        private set
    var textPaddingTop: Int = 0
        private set
    var textPaddingRight: Int = 0
        private set
    var textPaddingBottom: Int = 0
        private set
    private var mWordMargin: Int = 0
    private var mDrawablePadding: Int = 0
    private var mLineMargin: Int = 0
    private var mSelectType: SelectType? = null
    private var mMaxSelect: Int = 0

    private val mLabels = ArrayList<Any?>()
    //保存选中的label的位置
    private val mSelectLabels = ArrayList<Int>()

    //保存必选项。在多选模式下，可以设置必选项，必选项默认选中，不能反选
    private val mCompulsorys = ArrayList<Int>()

    private var mLabelClickListener: OnLabelClickListener? = null
    private var mLabelSelectChangeListener: OnLabelSelectChangeListener? = null

    /**
     * 获取必选项，
     *
     * @return
     */
    /**
     * 设置必选项，只有在多项模式下，这个方法才有效
     *
     * @param positions
     */
    //必选项发生改变，就要恢复到初始状态。
    var compulsorys: List<Int>?
        get() = mCompulsorys
        set(positions) {
            if (mSelectType == SelectType.MULTI && positions != null) {
                mCompulsorys.clear()
                mCompulsorys.addAll(positions)
                innerClearAllSelect()
                setSelects(positions)
            }
        }

    /**
     * 获取选中的label(返回的是所有选中的标签的位置)
     *
     * @return
     */
    val selectLabels: List<Int>
        get() = mSelectLabels

    /**
     * 设置标签的文字大小（单位是px）
     *
     * @param size
     */
    var labelTextSize: Float
        get() = mTextSize
        set(size) {
            if (mTextSize != size) {
                mTextSize = size
                val count = childCount
                for (i in 0 until count) {
                    val label = getChildAt(i) as TextView
                    label.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
                }
            }
        }

    /**
     * 设置行间隔
     */
    var lineMargin: Int
        get() = mLineMargin
        set(margin) {
            if (mLineMargin != margin) {
                mLineMargin = margin
                requestLayout()
            }
        }

    /**
     * 设置标签的间隔
     */
    var wordMargin: Int
        get() = mWordMargin
        set(margin) {
            if (mWordMargin != margin) {
                mWordMargin = margin
                requestLayout()
            }
        }

    /**
     * 设置标签的选择类型
     *
     * @param selectType
     */
    //选择类型发生改变，就要恢复到初始状态。
    var selectType: SelectType?
        get() = mSelectType
        set(selectType) {
            if (mSelectType != selectType) {
                mSelectType = selectType
                innerClearAllSelect()

                if (mSelectType == SelectType.SINGLE_IRREVOCABLY) {
                    setSelects(0)
                }

                if (mSelectType != SelectType.MULTI) {
                    mCompulsorys.clear()
                }

                ensureLabelClickable()
            }
        }

    /**
     * 设置最大的选择数量
     *
     * @param maxSelect
     */
    //最大选择数量发生改变，就要恢复到初始状态。
    var maxSelect: Int
        get() = mMaxSelect
        set(maxSelect) {
            if (mMaxSelect != maxSelect) {
                mMaxSelect = maxSelect
                if (mSelectType == SelectType.MULTI) {
                    innerClearAllSelect()
                }
            }
        }

    /**
     * Label的选择类型
     */
    enum class SelectType private constructor(internal var value: Int) {
        //不可选中，也不响应选中事件回调。（默认）
        NONE(1),
        //单选,可以反选。
        SINGLE(2),
        //单选,不可以反选。这种模式下，至少有一个是选中的，默认是第一个
        SINGLE_IRREVOCABLY(3),
        //多选
        MULTI(4);


        companion object {

            internal operator fun get(value: Int): SelectType {
                when (value) {
                    1 -> return NONE
                    2 -> return SINGLE
                    3 -> return SINGLE_IRREVOCABLY
                    4 -> return MULTI
                }
                return NONE
            }
        }
    }

    constructor(context: Context) : super(context) {
        mContext = context
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mContext = context
        getAttrs(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mContext = context
        getAttrs(context, attrs)
    }

    private fun getAttrs(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.labels_view)
            val type = mTypedArray.getInt(R.styleable.labels_view_selectType, 1)
            mSelectType = SelectType[type]

            mMaxSelect = mTypedArray.getInteger(R.styleable.labels_view_maxSelect, 0)
            mTextColor = mTypedArray.getColorStateList(R.styleable.labels_view_labelTextColor)
            mTextSize = mTypedArray.getDimension(R.styleable.labels_view_labelTextSize,
                    sp2px(context, 14f).toFloat())
            textPaddingLeft = mTypedArray.getDimensionPixelOffset(
                    R.styleable.labels_view_labelTextPaddingLeft, 0)
            textPaddingTop = mTypedArray.getDimensionPixelOffset(
                    R.styleable.labels_view_labelTextPaddingTop, 0)
            textPaddingRight = mTypedArray.getDimensionPixelOffset(
                    R.styleable.labels_view_labelTextPaddingRight, 0)
            textPaddingBottom = mTypedArray.getDimensionPixelOffset(
                    R.styleable.labels_view_labelTextPaddingBottom, 0)
            mLineMargin = mTypedArray.getDimensionPixelOffset(R.styleable.labels_view_lineMargin, 0)
            mWordMargin = mTypedArray.getDimensionPixelOffset(R.styleable.labels_view_wordMargin, 0)
            mDrawablePadding = mTypedArray.getDimensionPixelOffset(R.styleable.labels_view_drawablepadding, 0)
            val labelBgResId = mTypedArray.getResourceId(R.styleable.labels_view_labelBackground, 0)
            if (labelBgResId != 0) {
                mLabelBg = resources.getDrawable(labelBgResId)
            } else {
                val labelBgColor = mTypedArray.getColor(R.styleable.labels_view_labelBackground, Color.TRANSPARENT)
                mLabelBg = ColorDrawable(labelBgColor)
            }
            mTypedArray.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val count = childCount
        val maxWidth = View.MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight

        var contentHeight = 0 //记录内容的高度
        var lineWidth = 0 //记录行的宽度
        var maxLineWidth = 0 //记录最宽的行宽
        var maxItemHeight = 0 //记录一行中item高度最大的高度
        var begin = true //是否是行的开头

        for (i in 0 until count) {
            val view = getChildAt(i)
            measureChild(view, widthMeasureSpec, heightMeasureSpec)

            if (!begin) {
                lineWidth += mWordMargin
            } else {
                begin = false
            }

            if (maxWidth <= lineWidth + view.measuredWidth) {
                contentHeight += mLineMargin
                contentHeight += maxItemHeight
                maxItemHeight = 0
                maxLineWidth = Math.max(maxLineWidth, lineWidth)
                lineWidth = 0
                begin = true
            }
            maxItemHeight = Math.max(maxItemHeight, view.measuredHeight)

            lineWidth += view.measuredWidth
        }

        contentHeight += maxItemHeight
        maxLineWidth = Math.max(maxLineWidth, lineWidth)

        setMeasuredDimension(measureWidth(widthMeasureSpec, maxLineWidth),
                measureHeight(heightMeasureSpec, contentHeight))
    }

    private fun measureWidth(measureSpec: Int, contentWidth: Int): Int {
        var result = 0
        val specMode = View.MeasureSpec.getMode(measureSpec)
        val specSize = View.MeasureSpec.getSize(measureSpec)

        if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = contentWidth + paddingLeft + paddingRight
            if (specMode == View.MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize)
            }
        }
        result = Math.max(result, suggestedMinimumWidth)
        return result
    }

    private fun measureHeight(measureSpec: Int, contentHeight: Int): Int {
        var result = 0
        val specMode = View.MeasureSpec.getMode(measureSpec)
        val specSize = View.MeasureSpec.getSize(measureSpec)

        if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = contentHeight + paddingTop + paddingBottom
            if (specMode == View.MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize)
            }
        }
        result = Math.max(result, suggestedMinimumHeight)
        return result
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {

        var x = paddingLeft
        var y = paddingTop

        val contentWidth = right - left
        var maxItemHeight = 0

        val count = childCount
        for (i in 0 until count) {
            val view = getChildAt(i)

            if (contentWidth < x + view.measuredWidth + paddingRight) {
                x = paddingLeft
                y += mLineMargin
                y += maxItemHeight
                maxItemHeight = 0
            }
            view.layout(x, y, x + view.measuredWidth, y + view.measuredHeight)
            x += view.measuredWidth
            x += mWordMargin
            maxItemHeight = Math.max(maxItemHeight, view.measuredHeight)
        }
    }

    override fun onSaveInstanceState(): Parcelable? {

        val bundle = Bundle()
        //保存父类的信息
        bundle.putParcelable(KEY_SUPER_STATE, super.onSaveInstanceState())
        //保存标签文字颜色
        if (mTextColor != null) {
            bundle.putParcelable(KEY_TEXT_COLOR_STATE, mTextColor)
        }
        //保存标签文字大小
        bundle.putFloat(KEY_TEXT_SIZE_STATE, mTextSize)
        //保存标签背景 (由于背景改用Drawable,所以不能自动保存和恢复)
        //        bundle.putInt(KEY_BG_RES_ID_STATE, mLabelBgResId);
        //保存标签内边距
        bundle.putIntArray(KEY_PADDING_STATE, intArrayOf(textPaddingLeft, textPaddingTop, textPaddingRight, textPaddingBottom))
        //保存标签间隔
        bundle.putInt(KEY_WORD_MARGIN_STATE, mWordMargin)
        //保存行间隔
        bundle.putInt(KEY_LINE_MARGIN_STATE, mLineMargin)
        //保存标签的选择类型
        bundle.putInt(KEY_SELECT_TYPE_STATE, mSelectType!!.value)
        //保存标签的最大选择数量
        bundle.putInt(KEY_MAX_SELECT_STATE, mMaxSelect)
        //保存标签列表
        //        if (!mLabels.isEmpty()) {
        //            bundle.putStringArrayList(KEY_LABELS_STATE, mLabels);
        //        }
        //保存已选择的标签列表
        if (!mSelectLabels.isEmpty()) {
            bundle.putIntegerArrayList(KEY_SELECT_LABELS_STATE, mSelectLabels)
        }

        //保存必选项列表
        if (!mCompulsorys.isEmpty()) {
            bundle.putIntegerArrayList(KEY_COMPULSORY_LABELS_STATE, mCompulsorys)
        }

        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
//恢复父类信息
            super.onRestoreInstanceState(state.getParcelable(KEY_SUPER_STATE))

            //恢复标签文字颜色
            val color = state.getParcelable<ColorStateList>(KEY_TEXT_COLOR_STATE)
            if (color != null) {
                setLabelTextColor(color)
            }
            //恢复标签文字大小
            labelTextSize = state.getFloat(KEY_TEXT_SIZE_STATE, mTextSize)
            //            //恢复标签背景  (由于背景改用Drawable,所以不能自动保存和恢复)
            //            int resId = bundle.getInt(KEY_BG_RES_ID_STATE, mLabelBgResId);
            //            if (resId != 0) {
            //                setLabelBackgroundResource(resId);
            //            }
            //恢复标签内边距
            val padding = state.getIntArray(KEY_PADDING_STATE)
            if (padding != null && padding.size == 4) {
                setLabelTextPadding(padding[0], padding[1], padding[2], padding[3])
            }
            //恢复标签间隔
            wordMargin = state.getInt(KEY_WORD_MARGIN_STATE, mWordMargin)
            //恢复行间隔
            lineMargin = state.getInt(KEY_LINE_MARGIN_STATE, mLineMargin)
            //恢复标签的选择类型
            selectType = SelectType[state.getInt(KEY_SELECT_TYPE_STATE, mSelectType!!.value)]
            //恢复标签的最大选择数量
            maxSelect = state.getInt(KEY_MAX_SELECT_STATE, mMaxSelect)
            //            //恢复标签列表
            //            ArrayList<String> labels = bundle.getStringArrayList(KEY_LABELS_STATE);
            //            if (labels != null && !labels.isEmpty()) {
            //                setLabels(labels);
            //            }
            //恢复必选项列表
            //            ArrayList<Integer> compulsory = bundle.getIntegerArrayList(KEY_COMPULSORY_LABELS_STATE);
            //            if (compulsory != null && !compulsory.isEmpty()) {
            //                setCompulsorys(compulsory);
            //            }
            //            //恢复已选择的标签列表
            //            ArrayList<Integer> selectLabel = bundle.getIntegerArrayList(KEY_SELECT_LABELS_STATE);
            //            if (selectLabel != null && !selectLabel.isEmpty()) {
            //                int size = selectLabel.size();
            //                int[] positions = new int[size];
            //                for (int i = 0; i < size; i++) {
            //                    positions[i] = selectLabel.get(i);
            //                }
            //                setSelects(positions);
            //            }
            return
        }
        super.onRestoreInstanceState(state)
    }

    /**
     * 设置标签列表
     *
     * @param labels
     */
    fun setLabels(labels: List<String>) {
        setLabels(labels, object : LabelTextProvider<String> {
            override fun getLabelText(label: TextView, position: Int, data: String): CharSequence {
                return data.trim { it <= ' ' }
            }
        })
    }

    fun setLabels(labels: List<String>, resId: Int) {
        setLabels(labels, object : LabelTextProvider<String> {
            override fun getLabelText(label: TextView, position: Int, data: String): CharSequence {
                return data.trim { it <= ' ' }
            }
        }, resId)
    }

    /**
     * 设置标签列表，标签列表的数据可以是任何类型的数据，它最终显示的内容由LabelTextProvider根据标签的数据提供。
     *
     * @param labels
     * @param provider
     * @param <T>
    </T> */
    fun <T> setLabels(labels: List<T>?, provider: LabelTextProvider<T>) {
        //清空原有的标签
        innerClearAllSelect()
        removeAllViews()
        mLabels.clear()

        if (labels != null) {
            mLabels.addAll(labels)
            val size = labels.size
            for (i in 0 until size) {
                addLabel(labels[i], i, provider)
            }
            ensureLabelClickable()
        }

        if (mSelectType == SelectType.SINGLE_IRREVOCABLY) {
            setSelects(0)
        }
    }

    fun <T> setLabels(labels: List<T>?, provider: LabelTextProvider<T>, resId: Int) {
        //清空原有的标签
        removeAllViews()
        mLabels.clear()

        if (labels != null) {
            mLabels.addAll(labels)
            val size = labels.size
            for (i in 0 until size) {
                addLabel(labels[i], i, provider, resId)
            }
            ensureLabelClickable(resId)
        }
    }

    /**
     * 获取标签列表
     *
     * @return
     */
    fun <T> getLabels(): List<T> {
        return mLabels as List<T>
    }

    private fun <T> addLabel(data: T, position: Int, provider: LabelTextProvider<T>) {
        val label = TextView(mContext)
        label.setPadding(textPaddingLeft, textPaddingTop, textPaddingRight, textPaddingBottom)
        label.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize)
        label.setTextColor(if (mTextColor != null) mTextColor else ColorStateList.valueOf(-0x1000000))
        //设置给label的背景(Drawable)是一个Drawable对象的拷贝，
        // 因为如果所有的标签都共用一个Drawable对象，会引起背景错乱。
        label.setBackgroundDrawable(mLabelBg!!.constantState!!.newDrawable())
        //label通过tag保存自己的数据(data)和位置(position)
        label.setTag(KEY_DATA, data)
        label.setTag(KEY_POSITION, position)
        label.setOnClickListener(this)
        val drawable = resources.getDrawable(R.mipmap.comment_local_del)
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)// 设置边界
        label.setCompoundDrawables(null, null, drawable, null)
        label.compoundDrawablePadding = mDrawablePadding
        addView(label)
        label.text = provider.getLabelText(label, position, data)
    }

    private fun <T> addLabel(data: T, position: Int, provider: LabelTextProvider<T>, resId: Int) {
        val view = LayoutInflater.from(mContext).inflate(R.layout.layout_tag, null)
        val label = view.findViewById<View>(R.id.tv_label) as TextView
        val IVLabelDel = view.findViewById<View>(R.id.iv_label_del) as TextView
        label.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize)
        label.setTextColor(if (mTextColor != null) mTextColor else ColorStateList.valueOf(-0x1000000))
        //设置给label的背景(Drawable)是一个Drawable对象的拷贝，
        // 因为如果所有的标签都共用一个Drawable对象，会引起背景错乱。
        //        label.setBackgroundDrawable(mLabelBg.getConstantState().newDrawable());
        //label通过tag保存自己的数据(data)和位置(position)
        IVLabelDel.setTag(KEY_DATA, data)
        IVLabelDel.setTag(KEY_POSITION, position)
        IVLabelDel.setOnClickListener(this)
        addView(view)
        label.text = provider.getLabelText(label, position, data)
    }

    /**
     * 确保标签是否能响应事件，如果标签可选或者标签设置了点击事件监听，则响应事件。
     */
    private fun ensureLabelClickable() {
        val count = childCount
        for (i in 0 until count) {
            val label = getChildAt(i) as TextView
            label.isClickable = mLabelClickListener != null || mSelectType != SelectType.NONE
        }
    }

    private fun ensureLabelClickable(resId: Int) {
        val count = childCount
        for (i in 0 until count) {
            val view = getChildAt(i)
            val label = view.findViewById<View>(R.id.iv_label_del) as TextView
            label.isClickable = mLabelClickListener != null || mSelectType != SelectType.NONE
        }
    }

    override fun onClick(v: View) {
        if (v is TextView) {
            if (mLabelClickListener != null) {
                mLabelClickListener!!.onLabelClick(v, v.getTag(KEY_DATA), v.getTag(KEY_POSITION) as Int)
            }
        }
    }

    private fun setLabelSelect(label: TextView, isSelect: Boolean) {
        if (label.isSelected != isSelect) {
            label.isSelected = isSelect
            if (isSelect) {
                mSelectLabels.add(label.getTag(KEY_POSITION) as Int)
            } else {
                mSelectLabels.remove(label.getTag(KEY_POSITION) as Int)
            }
            if (mLabelSelectChangeListener != null) {
                mLabelSelectChangeListener!!.onLabelSelectChange(label, label.getTag(KEY_DATA),
                        isSelect, label.getTag(KEY_POSITION) as Int)
            }
        }
    }

    /**
     * 取消所有选中的label
     */
    fun clearAllSelect() {
        if (mSelectType != SelectType.SINGLE_IRREVOCABLY) {
            if (mSelectType == SelectType.MULTI && !mCompulsorys.isEmpty()) {
                clearNotCompulsorySelect()
            } else {
                innerClearAllSelect()
            }
        }
    }

    private fun innerClearAllSelect() {
        val count = childCount
        for (i in 0 until count) {
            setLabelSelect(getChildAt(i) as TextView, false)
        }
        mSelectLabels.clear()
    }

    private fun clearNotCompulsorySelect() {
        val count = childCount
        val temps = ArrayList<Int>()
        for (i in 0 until count) {
            if (!mCompulsorys.contains(i)) {
                setLabelSelect(getChildAt(i) as TextView, false)
                temps.add(i)
            }

        }
        mSelectLabels.removeAll(temps)
    }

    /**
     * 设置选中label
     *
     * @param positions
     */
    fun setSelects(positions: List<Int>?) {
        if (positions != null) {
            val size = positions.size
            val ps = IntArray(size)
            for (i in 0 until size) {
                ps[i] = positions[i]
            }
            setSelects(*ps)
        }
    }

    /**
     * 设置选中label
     *
     * @param positions
     */
    fun setSelects(vararg positions: Int) {
        if (mSelectType != SelectType.NONE) {
            val selectLabels = ArrayList<TextView>()
            val count = childCount
            val size = if (mSelectType == SelectType.SINGLE || mSelectType == SelectType.SINGLE_IRREVOCABLY)
                1
            else
                mMaxSelect
            for (p in positions) {
                if (p < count) {
                    val label = getChildAt(p) as TextView
                    if (!selectLabels.contains(label)) {
                        setLabelSelect(label, true)
                        selectLabels.add(label)
                    }
                    if (size > 0 && selectLabels.size == size) {
                        break
                    }
                }
            }

            for (i in 0 until count) {
                val label = getChildAt(i) as TextView
                if (!selectLabels.contains(label)) {
                    setLabelSelect(label, false)
                }
            }
        }
    }

    /**
     * 设置必选项，只有在多项模式下，这个方法才有效
     *
     * @param positions
     */
    fun setCompulsorys(vararg positions: Int) {
        if (mSelectType == SelectType.MULTI && positions != null) {
            val ps = ArrayList<Int>(positions.size)
            for (i in positions) {
                ps.add(i)
            }
            compulsorys = ps
        }
    }

    /**
     * 清空必选项，只有在多项模式下，这个方法才有效
     */
    fun clearCompulsorys() {
        if (mSelectType == SelectType.MULTI && !mCompulsorys.isEmpty()) {
            mCompulsorys.clear()
            //必选项发生改变，就要恢复到初始状态。
            innerClearAllSelect()
        }
    }

    /**
     * 获取选中的label(返回的是所头选中的标签的数据)
     *
     * @param <T>
     * @return
    </T> */
    fun <T> getSelectLabelDatas(): List<T> {
        val list = ArrayList<T>()
        val size = mSelectLabels.size
        for (i in 0 until size) {
            val label = getChildAt(mSelectLabels[i])
            val data = label.getTag(KEY_DATA)
            if (data != null) {
                list.add(data as T)
            }
        }
        return list
    }

    /**
     * 设置标签背景
     *
     * @param resId
     */
    fun setLabelBackgroundResource(resId: Int) {
        setLabelBackgroundDrawable(resources.getDrawable(resId))
    }

    /**
     * 设置标签背景
     *
     * @param color
     */
    fun setLabelBackgroundColor(color: Int) {
        setLabelBackgroundDrawable(ColorDrawable(color))
    }

    /**
     * 设置标签背景
     *
     * @param drawable
     */
    fun setLabelBackgroundDrawable(drawable: Drawable) {
        mLabelBg = drawable
        val count = childCount
        for (i in 0 until count) {
            val label = getChildAt(i) as TextView
            label.setBackgroundDrawable(mLabelBg!!.constantState!!.newDrawable())
        }
    }

    /**
     * 设置标签内边距
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    fun setLabelTextPadding(left: Int, top: Int, right: Int, bottom: Int) {
        if (textPaddingLeft != left || textPaddingTop != top
                || textPaddingRight != right || textPaddingBottom != bottom) {
            textPaddingLeft = left
            textPaddingTop = top
            textPaddingRight = right
            textPaddingBottom = bottom
            val count = childCount
            for (i in 0 until count) {
                val label = getChildAt(i) as TextView
                label.setPadding(left, top, right, bottom)
            }
        }
    }

    /**
     * 设置标签的文字颜色
     *
     * @param color
     */
    fun setLabelTextColor(color: Int) {
        setLabelTextColor(ColorStateList.valueOf(color))
    }

    /**
     * 设置标签的文字颜色
     *
     * @param color
     */
    fun setLabelTextColor(color: ColorStateList) {
        mTextColor = color
        val count = childCount
        for (i in 0 until count) {
            val label = getChildAt(i) as TextView
            label.setTextColor(if (mTextColor != null) mTextColor else ColorStateList.valueOf(-0x1000000))
        }
    }

    fun getLabelTextColor(): ColorStateList? {
        return mTextColor
    }

    /**
     * 设置标签的点击监听
     *
     * @param l
     */
    fun setOnLabelClickListener(l: OnLabelClickListener) {
        mLabelClickListener = l
        ensureLabelClickable()
    }

    fun setOnLabelClickListener(l: OnLabelClickListener, resId: Int) {
        mLabelClickListener = l
        ensureLabelClickable(resId)
    }

    /**
     * 设置标签的选择监听
     *
     * @param l
     */
    fun setOnLabelSelectChangeListener(l: OnLabelSelectChangeListener) {
        mLabelSelectChangeListener = l
    }

    interface OnLabelClickListener {

        /**
         * @param label    标签
         * @param data     标签对应的数据
         * @param position 标签位置
         */
        fun onLabelClick(label: TextView, data: Any, position: Int)
    }

    interface OnLabelSelectChangeListener {

        /**
         * @param label    标签
         * @param data     标签对应的数据
         * @param isSelect 是否选中
         * @param position 标签位置
         */
        fun onLabelSelectChange(label: TextView, data: Any, isSelect: Boolean, position: Int)
    }

    /**
     * 给标签提供最终需要显示的数据。因为LabelsView的列表可以设置任何类型的数据，而LabelsView里的每个item的是一
     * 个TextView，只能显示CharSequence的数据，所以LabelTextProvider需要根据每个item的数据返回item最终要显示
     * 的CharSequence。
     *
     * @param <T>
    </T> */
    interface LabelTextProvider<T> {

        /**
         * 根据data和position返回label需要需要显示的数据。
         *
         * @param label
         * @param position
         * @param data
         * @return
         */
        fun getLabelText(label: TextView, position: Int, data: T): CharSequence
    }

    companion object {

        //用于保存label数据的key
        private val KEY_DATA = R.id.tag_key_data
        //用于保存label位置的key
        private val KEY_POSITION = R.id.tag_key_position

        /*  用于保存View的信息的key  */
        private val KEY_SUPER_STATE = "key_super_state"
        private val KEY_TEXT_COLOR_STATE = "key_text_color_state"
        private val KEY_TEXT_SIZE_STATE = "key_text_size_state"
        private val KEY_BG_RES_ID_STATE = "key_bg_res_id_state"
        private val KEY_PADDING_STATE = "key_padding_state"
        private val KEY_WORD_MARGIN_STATE = "key_word_margin_state"
        private val KEY_LINE_MARGIN_STATE = "key_line_margin_state"
        private val KEY_SELECT_TYPE_STATE = "key_select_type_state"
        private val KEY_MAX_SELECT_STATE = "key_max_select_state"
        // 由于新版(1.4.0)的标签列表允许设置任何类型的数据，而不仅仅是String。并且标签显示的内容
        // 最终由LabelTextProvider提供，所以LabelsView不再在onSaveInstanceState()和onRestoreInstanceState()
        // 中保存和恢复标签列表的数据。
        private val KEY_LABELS_STATE = "key_labels_state"
        private val KEY_SELECT_LABELS_STATE = "key_select_labels_state"
        private val KEY_COMPULSORY_LABELS_STATE = "key_select_compulsory_state"

        /**
         * sp转px
         */
        fun sp2px(context: Context, spVal: Float): Int {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                    spVal, context.resources.displayMetrics).toInt()
        }
    }

}
