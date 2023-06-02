package com.d6zone.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6zone.android.app.R
import com.d6zone.android.app.utils.OnDialogListener
import kotlinx.android.synthetic.main.dialog_mine_action_layout.*
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent

/**
 * 我的页面操作弹窗
 */
class MineActionDialog : DialogFragment() {

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
            inflater?.inflate(R.layout.dialog_mine_action_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_edit.setOnClickListener {
            dialogListener?.onClick(0,"")
            dismissAllowingStateLoss()
        }

        tv_setting.setOnClickListener {
            dialogListener?.onClick(1,"")
            dismissAllowingStateLoss()
        }

        tv_cancel.setOnClickListener {
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