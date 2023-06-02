package com.d6zone.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6zone.android.app.R
import com.d6zone.android.app.utils.OnDialogListener
import com.gyf.barlibrary.ImmersionBar
import kotlinx.android.synthetic.main.dialog_filter_trend_layout.*
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.wrapContent

/**
 * 动态筛选操作弹窗
 */
class FilterTrendDialog : DialogFragment() {

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
                .fitsSystemWindows(true).init()
        dialog.window.setLayout(dip(145), wrapContent)
        dialog.window.setGravity(Gravity.RIGHT or Gravity.TOP)
        dialog.setCanceledOnTouchOutside(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.dialog_filter_trend_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_create_square.setOnClickListener {
            dialogListener?.onClick(0, "")
            dismissAllowingStateLoss()
        }

        tv_create_date.setOnClickListener {
            dialogListener?.onClick(1, "")
            dismissAllowingStateLoss()
        }

        ll_root.setOnClickListener {
            dismissAllowingStateLoss()
        }
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