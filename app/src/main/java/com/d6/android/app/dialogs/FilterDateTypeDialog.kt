package com.d6.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.utils.OnDialogListener
import com.d6.android.app.utils.SpanBuilder
import com.gyf.barlibrary.ImmersionBar
import kotlinx.android.synthetic.main.dialog_filter_date_type_layout.*
import org.jetbrains.anko.matchParent

/**
 * 筛选约会类型弹窗
 */
class FilterDateTypeDialog : DialogFragment() {

    private val immersionBar by lazy {
        ImmersionBar.with(this, dialog)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FadePopup)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        immersionBar
                .fitsSystemWindows(true)
                .statusBarDarkFont(true)
                .init()
        dialog.window.setLayout(matchParent, matchParent)
        dialog.window.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL)
        dialog.setCanceledOnTouchOutside(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.dialog_filter_date_type_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_all.setOnClickListener {
            dialogListener?.onClick(5, "全部")
            dismissAllowingStateLoss()
        }

//        tv_recommend.setOnClickListener {
////            dialogListener?.onClick(5, "推荐")
////            dismissAllowingStateLoss()
////        }

        tv_2.setOnClickListener {
            dialogListener?.onClick(1, "旅行")
            dismissAllowingStateLoss()
        }

        tv_3.setOnClickListener {
            dialogListener?.onClick(2, "吃饭")
            dismissAllowingStateLoss()
        }

        tv_4.setOnClickListener {
            dialogListener?.onClick(3, "看电影")
            dismissAllowingStateLoss()
        }

        tv_5.setOnClickListener {
            dialogListener?.onClick(4, "喝酒")
            dismissAllowingStateLoss()
        }

        bgView.setOnClickListener { dismissAllowingStateLoss() }

        tv_all.text = SpanBuilder("全部\n全部约会类型")
                .color(context,0,2,R.color.textColor)
                .size(0,2,15)
                .build()

//        tv_recommend.text = SpanBuilder("官方推荐\nD6社区的推荐")
//                .color(context,0,4,R.color.textColor)
//                .size(0,4,15)
//                .build()

        tv_2.text = SpanBuilder("旅行\n在旅行中遇见最美的你")
                .color(context,0,2,R.color.textColor)
                .size(0,2,15)
                .build()

        tv_3.text = SpanBuilder("吃饭\n在旅行中遇见最美的你")
                .color(context,0,2,R.color.textColor)
                .size(0,2,15)
                .build()

        tv_4.text = SpanBuilder("看电影\n快速响应的约会")
                .color(context,0,2,R.color.textColor)
                .size(0,2,15)
                .build()

        tv_5.text = SpanBuilder("喝酒\n一起来旅行吧")
                .color(context,0,3,R.color.textColor)
                .size(0,3,15)
                .build()

    }

    private var dialogListener: OnDialogListener? = null

    fun setDialogListener(l: (p: Int, s: String?) -> Unit) {
        dialogListener = object : OnDialogListener {
            override fun onClick(position: Int, data: String?) {
                l(position, data)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar.destroy()
    }
}