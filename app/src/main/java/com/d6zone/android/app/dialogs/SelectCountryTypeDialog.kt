package com.d6zone.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6zone.android.app.R
import com.d6zone.android.app.utils.OnDialogListener
import com.d6zone.android.app.utils.screenWidth
import kotlinx.android.synthetic.main.dialog_select_country_type_layout.*
import org.jetbrains.anko.wrapContent

/**
 * 选择国家类型弹窗
 */
class SelectCountryTypeDialog : DialogFragment() {

    private var type = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.Dialog)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window.setLayout((screenWidth()*0.9f).toInt(), wrapContent)
        dialog.window.setGravity(Gravity.BOTTOM)
        dialog.setCanceledOnTouchOutside(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.dialog_select_country_type_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            type = arguments.getInt("sex")
        }

        tv_type1.setOnClickListener {
            type = 1
//            changeSex()
            dialogListener?.onClick(type, "国内" )
            dismissAllowingStateLoss()
        }

        tv_type2.setOnClickListener {
            type = 0
//            changeSex()
            dialogListener?.onClick(type, "国外" )
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