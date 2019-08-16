package com.d6.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.activities.UnKnownActivity
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.OnDialogListener
import com.d6.android.app.utils.SPUtils
import kotlinx.android.synthetic.main.dialog_select_unknowtype_layout.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.wrapContent

/**
 * 设置私聊类型
 */
class SelectUnKnowTypeDialog : DialogFragment() {

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
            inflater?.inflate(R.layout.dialog_select_unknowtype_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var type = arguments.getString("type")
        var  IsOpenUnKnow = arguments.getString("IsOpenUnKnow","")
        var mRequestCode = arguments.getInt("code",0)
        var iAddPoint = arguments.getString("iAddPoint","")//匿名发布需要消耗的积分
        var iRemainPoint = arguments.getString("iRemainPoint","")//剩余积分

        tv_cancel.setOnClickListener {
            dismissAllowingStateLoss()
        }

        ll_unknow_open.setOnClickListener {
            dialogListener?.onClick(2, resources.getString(R.string.string_unknow_open))
            dismissAllowingStateLoss()
        }

        ll_unknow_unknow.setOnClickListener {
            if(TextUtils.equals(IsOpenUnKnow,"close")){
                startActivity<UnKnownActivity>()
            }else{
                if (mRequestCode == 4) {
                    var openErrorDialog = OpenDatePointNoEnoughDialog()
                    openErrorDialog.arguments = bundleOf("point" to iAddPoint, "remainPoint" to iRemainPoint)
                    openErrorDialog.show((context as BaseActivity).supportFragmentManager, "d")
                } else {
                    dialogListener?.onClick(1, resources.getString(R.string.string_unknow_unknow))
                }
            }
            dismissAllowingStateLoss()
        }

        if(TextUtils.equals("PublishFindDate",type)){
            var desc = arguments.getString("desc")
            setTextTipMsg(mRequestCode,desc)
        }else if(TextUtils.equals("SquareTrendDetail",type)){
            tv_unknow_unknowtips.text = "以匿名身份发布"
        }else if(TextUtils.equals("ReleaseNewTrends",type)){
            var desc = arguments.getString("desc")
            setTextTipMsg(mRequestCode,desc)
        }
    }

    private fun setTextTipMsg(code:Int,desc:String){
        tv_unknow_unknowtips.text = desc
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