package com.d6.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.activities.ChooseFriendsActivity
import com.d6.android.app.adapters.DialogShareFriendsQuickAdapter
import com.d6.android.app.utils.OnDialogListener
import kotlinx.android.synthetic.main.dialog_sharefriends_layout.*
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.wrapContent


/**
 * 分享好友
 */
class ShareFriendsDialog : DialogFragment() {

    private var mList = ArrayList<String>()

    private var  mDialogShareFriendsQuickAdapter = DialogShareFriendsQuickAdapter(mList)
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
            inflater?.inflate(R.layout.dialog_sharefriends_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var userId = arguments.getString("id")
        mList.add("测试-1")
        mList.add("测试-2")
        mList.add("测试-3")
        mList.add("测试-4")
        mList.add("测试-5")
        mList.add("测试-6")
        rv_chooseuser.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        mDialogShareFriendsQuickAdapter.setNewData(mList)
        rv_chooseuser.adapter = mDialogShareFriendsQuickAdapter
        mDialogShareFriendsQuickAdapter.setOnItemClickListener { adapter, view, position ->
                if(position == 5){
                    startActivity<ChooseFriendsActivity>()
                    dismissAllowingStateLoss()
                }else{

                }
        }

        tv_joinblack.setOnClickListener {
            dialogListener?.onClick(2,"")//2代表加入黑名单
            dismissAllowingStateLoss()
        }

        tv_report_user.setOnClickListener {
            dialogListener?.onClick(0,"")//0代表举报
            dismissAllowingStateLoss()
        }

        tv_deldate.setOnClickListener {
            dialogListener?.onClick(1,"")//1代表删除
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