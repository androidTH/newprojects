package com.d6.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.utils.OnDialogListener
import kotlinx.android.synthetic.main.dialog_cost_layout.*
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent

/**
 * 设置费用类型
 */
class SelectCostTypeDialog : DialogFragment() {

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
            inflater?.inflate(R.layout.dialog_cost_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_cancel.setOnClickListener {
            dismissAllowingStateLoss()
        }

        ll_allcost.setOnClickListener {
            dialogListener?.onClick(1, "全包")
            dismissAllowingStateLoss()
        }

        ll_costaa.setOnClickListener {
            dialogListener?.onClick(2, "AA")
            dismissAllowingStateLoss()
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
}