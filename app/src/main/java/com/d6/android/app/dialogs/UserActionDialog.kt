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
import kotlinx.android.synthetic.main.dialog_user_action_layout.*
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent

/**
 * 用户信息页面操作弹窗
 */
class UserActionDialog : DialogFragment() {

    private var iType:String = "-1" //1私聊 2 匿名 3 是群聊

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
            inflater?.inflate(R.layout.dialog_user_action_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var isInBlackList = arguments.getInt("isInBlackList",0)
        if(arguments.containsKey("iType")){
            iType = arguments.getString("iType")
        }

        if(isInBlackList == 1){
            tv_joinblack.text = getString(R.string.string_removeblacklist)
        }else{
            tv_joinblack.text = resources.getString(R.string.string_joinblack)
        }

        if(!TextUtils.equals(iType,"3")&&!TextUtils.equals(iType,"-1")){
            tv_deldate.visibility = View.VISIBLE
            tv_deldate.text="移除好友"
        }

        tv_report_user.setOnClickListener {
            dialogListener?.onClick(0,"")
            dismissAllowingStateLoss()
        }

        tv_joinblack.setOnClickListener {
            dialogListener?.onClick(1,"")
            dismissAllowingStateLoss()
        }

        tv_cancel.setOnClickListener {
            dismissAllowingStateLoss()
        }

        tv_deldate.setOnClickListener {
            dialogListener?.onClick(2,"removeuser")
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