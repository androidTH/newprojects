package com.d6.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.adapters.ArrayWheelAdapter
import com.d6.android.app.utils.OnDialogListener
import kotlinx.android.synthetic.main.dialog_select_wheel_layout.*
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent


/**
 * 分享给好友
 */
class SelectHeightDialog : DialogFragment() {
    private val height = ArrayList<String>()
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
            inflater?.inflate(R.layout.dialog_select_wheel_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_cancel.setOnClickListener {
            dismissAllowingStateLoss()
        }

        tv_sure.setOnClickListener {
            val value = wheelView.currentItem
            dialogListener?.onClick(0, height[value])
            dismissAllowingStateLoss()
        }

        (1..250).forEach {
            height.add("${it}CM")
        }

        wheelView.adapter = ArrayWheelAdapter(height)

        val selected = if (arguments != null && arguments.containsKey("data")) {
            arguments.getString("data")
        } else {
            ""
        }
        if (selected.isNotEmpty()) {
            val i = height.indexOf(selected)
            if (i > -1) {
                wheelView.currentItem = i
            } else {
                wheelView.currentItem = 159
            }
        }else {
            wheelView.currentItem = 159
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