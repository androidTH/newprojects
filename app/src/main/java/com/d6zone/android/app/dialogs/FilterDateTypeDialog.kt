package com.d6zone.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6zone.android.app.R
import com.d6zone.android.app.utils.OnDialogListener
import com.d6zone.android.app.utils.SpanBuilder
import com.gyf.barlibrary.ImmersionBar
import kotlinx.android.synthetic.main.dialog_filter_date_type_layout.*
import org.jetbrains.anko.matchParent

/**
 * 筛选约会类型弹窗
 */
class FilterDateTypeDialog : DialogFragment() {

    private var mShowType:Boolean = true
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

    fun setDateType(showType:Boolean = true){
        this.mShowType = showType
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.dialog_filter_date_type_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_all.setOnClickListener {
            dialogListener?.onClick(6, "全部")
            dismissAllowingStateLoss()
        }

        if(mShowType){
            line_all.visibility = View.GONE
            tv_all.visibility = View.GONE
        }else{
            line_all.visibility = View.VISIBLE
            tv_all.visibility = View.VISIBLE
        }

        tv_recommend.setOnClickListener {
            dialogListener?.onClick(5, if(mShowType )"全部" else "觅约")
            dismissAllowingStateLoss()
        }

        tv_2.setOnClickListener {
            dialogListener?.onClick(6, if(mShowType )"聊天" else "救火")
            dismissAllowingStateLoss()
        }

        tv_3.setOnClickListener {
            dialogListener?.onClick(2,  if(mShowType) "吃饭" else "征求")
            dismissAllowingStateLoss()
        }

        tv_4.setOnClickListener {
            dialogListener?.onClick(1, if(mShowType) "旅行" else "急约")
            dismissAllowingStateLoss()
        }

        tv_5.setOnClickListener {
            dialogListener?.onClick(3, if(mShowType )"电影" else "旅行约")
            dismissAllowingStateLoss()
        }


        tv_6.setOnClickListener {
            dialogListener?.onClick(7, if(mShowType )"游戏" else "旅行约")
            dismissAllowingStateLoss()
        }

        tv_7.setOnClickListener {
            dialogListener?.onClick(8, if(mShowType )"健身" else "旅行约")
            dismissAllowingStateLoss()
        }



        bgView.setOnClickListener { dismissAllowingStateLoss() }

        tv_all.text = SpanBuilder("全部\n所有约会")
                        .color(context,0,2,R.color.textColor)
                        .size(0,2,15)
                        .build()

        tv_recommend.text = if(mShowType )"全部" else SpanBuilder("觅约\n每日最新会员")
                .color(context,0,2,R.color.textColor)
                .size(0,2,12)
                .build()


        tv_2.text = if(mShowType )"聊天" else SpanBuilder("救火\n当天快速匹配")
                .color(context,0,2,R.color.textColor)
                .size(0,2,12)
                .build()


        tv_3.text = if(mShowType) "吃饭" else SpanBuilder("征求\n精准匹配,寻找合拍的TA")
                .color(context,0,2,R.color.textColor)
                .size(0,2,12)
                .build()


        tv_4.text = if(mShowType) "旅行" else SpanBuilder("急约\n近期快速匹配")
                .color(context,0,2,R.color.textColor)
                .size(0,2,12)
                .build()


        tv_5.text = if(mShowType )"电影" else SpanBuilder("旅行约\n边旅行,边约会")
                .color(context,0,3,R.color.textColor)
                .size(0,3,12)
                .build()


        tv_6.text = if(mShowType )"游戏" else SpanBuilder("旅行约\n边旅行,边约会")
                .color(context,0,3,R.color.textColor)
                .size(0,3,12)
                .build()

        tv_7.text = if(mShowType )"健身" else SpanBuilder("旅行约\n边旅行,边约会")
                .color(context,0,3,R.color.textColor)
                .size(0,3,12)
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