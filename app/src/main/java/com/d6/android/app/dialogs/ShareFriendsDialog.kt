package com.d6.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.activities.ChooseFriendsActivity
import com.d6.android.app.adapters.DialogShareFriendsQuickAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.FriendBean
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.OnDialogListener
import com.d6.android.app.utils.SPUtils
import kotlinx.android.synthetic.main.activity_choose_friends.*
import kotlinx.android.synthetic.main.dialog_sharefriends_layout.*
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent


/**
 * 分享好友
 */
class ShareFriendsDialog : DialogFragment() {

    private var mList = ArrayList<FriendBean>()

    private val mUserId by lazy{
        SPUtils.instance().getString(Const.User.USER_ID)
    }

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
        if (TextUtils.equals(mUserId, userId)) {
            tv_deldate.visibility = View.VISIBLE
            rv_chooseuser.visibility = View.VISIBLE
            tv_share.visibility = View.VISIBLE
            tv_report_user.visibility = View.GONE
            tv_joinblack.visibility = View.GONE

            rv_chooseuser.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
            rv_chooseuser.adapter = mDialogShareFriendsQuickAdapter
            getUserFriends()
        } else {
            tv_report_user.visibility = View.VISIBLE
            tv_joinblack.visibility = View.GONE
            tv_deldate.visibility = View.GONE
            rv_chooseuser.visibility = View.GONE
            tv_share.visibility = View.GONE
        }

        mDialogShareFriendsQuickAdapter.setOnItemClickListener { adapter, view, position ->
            if (position == 5) {
                startActivity<ChooseFriendsActivity>()
                dismissAllowingStateLoss()
            } else {
               toast("分享")
               dismissAllowingStateLoss()
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

    private fun getUserFriends() {
        isBaseActivity {
            Request.findUserFriends(mUserId, 1).request(it) { _, data ->
                if (data?.list?.results == null || data.list.results.isEmpty()) {
                    rv_chooseuser.visibility = View.GONE
                } else {
                    mList.addAll(data.list.results)
                }
                mDialogShareFriendsQuickAdapter.setNewData(mList)
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

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }
}