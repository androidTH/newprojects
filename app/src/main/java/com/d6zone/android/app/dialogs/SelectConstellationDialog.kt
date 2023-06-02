package com.d6zone.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6zone.android.app.R
import com.d6zone.android.app.adapters.ArrayWheelAdapter
import com.d6zone.android.app.utils.OnDialogListener
import com.d6zone.android.app.utils.sysErr
import kotlinx.android.synthetic.main.dialog_select_wheel_layout.*
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent


/**
 * 选择星座弹窗
 */
class SelectConstellationDialog : DialogFragment() {
    private val constellation = ArrayList<String>()
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
            dialogListener?.onClick(0, constellation[value])
            dismissAllowingStateLoss()
        }

//白羊座、金牛座、双子座、巨蟹座、狮子座、处女座、天秤座、天蝎座、射手座、摩羯座、水瓶座、双鱼座
        constellation.add("白羊座")
        constellation.add("金牛座")
        constellation.add("双子座")
        constellation.add("巨蟹座")
        constellation.add("狮子座")
        constellation.add("处女座")
        constellation.add("天秤座")
        constellation.add("天蝎座")
        constellation.add("射手座")
        constellation.add("摩羯座")
        constellation.add("水瓶座")
        constellation.add("双鱼座")

        wheelView.adapter = ArrayWheelAdapter(constellation)

        val selected = if (arguments != null && arguments.containsKey("data")) {
            arguments.getString("data")
        } else {
            ""
        }
        if (selected.isNotEmpty()) {
            val i = constellation.indexOf(selected)
            sysErr("---->$i")
            if (i > -1) {
                wheelView.currentItem = i
            } else {
                wheelView.currentItem = 59
            }

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