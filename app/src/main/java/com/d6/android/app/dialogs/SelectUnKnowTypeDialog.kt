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

    private val point_nums by lazy {
        SPUtils.instance().getString(Const.User.USERPOINTS_NUMS)
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
            inflater?.inflate(R.layout.dialog_select_unknowtype_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var type = arguments.getString("type")
        var  IsOpenUnKnow = arguments.getString("IsOpenUnKnow","")

        tv_cancel.setOnClickListener {
            dismissAllowingStateLoss()
        }

        ll_unknow_open.setOnClickListener {
            dialogListener?.onClick(1, resources.getString(R.string.string_unknow_open))
            dismissAllowingStateLoss()
        }

        ll_unknow_unknow.setOnClickListener {
            if(TextUtils.equals(IsOpenUnKnow,"close")){
                startActivity<UnKnownActivity>()
            }else{
                if(point_nums.toInt()<50){
                    var openErrorDialog = OpenDatePointNoEnoughDialog()
                    openErrorDialog.arguments= bundleOf("point" to "30","remainPoint" to "50")
                    openErrorDialog.show((context as BaseActivity).supportFragmentManager, "d")
                }else{
//                    SPUtils.instance().put(Const.User.USERPOINTS_NUMS,(point_nums.toInt()-50).toString()).apply()
                    dialogListener?.onClick(2, resources.getString(R.string.string_unknow_unknow))
                }
            }
            dismissAllowingStateLoss()
        }

        if(TextUtils.equals("PublishFindDate",type)){
//            tv_unknow_opentips.text = "以真实身份发布"
            tv_unknow_unknowtips.text ="以匿名身份发布约会"
        }else if(TextUtils.equals("SquareTrendDetail",type)){
//            tv_unknow_opentips.text ="以当前身份发布评论"
            tv_unknow_unknowtips.text ="以匿名身份发布评论"
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