package com.d6.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.models.Square
import com.d6.android.app.utils.OnDialogListener
import com.d6.android.app.utils.screenWidth
import com.d6.android.app.utils.toTime
import kotlinx.android.synthetic.main.dialog_select_sex_layout.*
import kotlinx.android.synthetic.main.dialog_trend_content_layout.*
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor
import org.jetbrains.anko.wrapContent

/**
 * 动态文本内容弹出窗
 */
class TrendContentDialog : DialogFragment() {
    private val mTrend by lazy {
        if (arguments!=null && arguments.containsKey("data")) {
            arguments.getSerializable("data") as Square
        } else {
            Square()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.Dialog)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window.setLayout(matchParent, wrapContent)
        dialog.window.setGravity(Gravity.BOTTOM)
        dialog.setCanceledOnTouchOutside(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.dialog_trend_content_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        headView.setImageURI(mTrend.picUrl)
        tv_nick.text = mTrend.name
        tv_age.isSelected = TextUtils.equals("0", mTrend.sex)
        tv_age.text = mTrend.age
        tv_time.text = mTrend.updatetime.toTime("MM.dd")
        tv_content.text = mTrend.content
        tv_close.setOnClickListener { dismissAllowingStateLoss() }
    }

    private var dialogListener: OnDialogListener? = null

    fun setDialogListener(l: (p: Int, s: String?) -> Unit) {
        dialogListener = object : OnDialogListener {
            override fun onClick(position: Int, data: String?) {
                l(position, data)
            }
        }
    }
}