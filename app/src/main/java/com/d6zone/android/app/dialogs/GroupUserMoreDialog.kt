package com.d6zone.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6zone.android.app.R
import com.d6zone.android.app.models.GroupUserBean
import com.d6zone.android.app.utils.OnDialogListener
import kotlinx.android.synthetic.main.dialog_groupusermore_layout.*
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent

/**
 * 群成员管理
 */
class GroupUserMoreDialog : DialogFragment() {

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
            inflater?.inflate(R.layout.dialog_groupusermore_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(arguments!=null){
            var userId = arguments.getString("userId")
            var mGroupUserBean = arguments.getParcelable("bean") as GroupUserBean
            var status = arguments.getInt("status", -1)
            if (status == 1) {
                if (mGroupUserBean.iIsManager == 1) {
                    tv_allcost.text = "取消管理员"
                } else if (mGroupUserBean.iIsManager == 2) {
                    tv_allcost.text = "设置管理员"
                }
            } else if (status == 2) {
                ll_groupdo.visibility = View.GONE
//                if (mGroupUserBean.iIsOwner == 1 || mGroupUserBean.iIsManager == 1) {
//                    ll_groupdo.visibility = View.GONE
//                }else{
//
//                }
            }
        }
        tv_cancel.setOnClickListener {
            dismissAllowingStateLoss()
        }

        ll_groupdo.setOnClickListener {
            dialogListener?.onClick(1, "设为管理员")
            dismissAllowingStateLoss()
        }

        ll_group_del.setOnClickListener {
            dialogListener?.onClick(2, "删除该成员")
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