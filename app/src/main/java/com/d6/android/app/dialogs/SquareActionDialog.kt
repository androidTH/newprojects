package com.d6.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.models.Square
import com.d6.android.app.utils.*
import kotlinx.android.synthetic.main.dialog_mine_action_layout.*
import kotlinx.android.synthetic.main.dialog_select_sex_layout.*
import kotlinx.android.synthetic.main.dialog_square_action_layout.*
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent

/**
 * 我的页面操作弹窗
 */
class SquareActionDialog : DialogFragment() {

    private val mSquare by lazy {
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
            inflater?.inflate(R.layout.dialog_square_action_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uid = SPUtils.instance().getString(Const.User.USER_ID)
        if (TextUtils.equals(mSquare.userid, uid)) {//是自己。
            tv_report_squre.gone()
            tv_delete_square.visible()
        } else {
            tv_delete_square.gone()
            tv_report_squre.visible()
        }

        tv_report_squre.setOnClickListener {
            dialogListener?.onClick(0,"")//0代表举报
            dismissAllowingStateLoss()
        }

        tv_delete_square.setOnClickListener {
            dialogListener?.onClick(1,"")//1代表删除
            dismissAllowingStateLoss()
        }

        tv_cancel_square.setOnClickListener {
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