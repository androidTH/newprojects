package com.d6.android.app.dialogs

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.activities.ChooseFriendsActivity
import com.d6.android.app.activities.MyInfoActivity
import com.d6.android.app.activities.ShareFriendsActivity
import com.d6.android.app.adapters.DialogShareFriendsQuickAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.AddImage
import com.d6.android.app.models.FriendBean
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.dialog_sharefriends_layout.*
import kotlinx.android.synthetic.main.dialog_womenauth_layout.*
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast
import org.jetbrains.anko.wrapContent


/**
 * 分享好友
 */
class WomenAuthDialog : DialogFragment() {

    private var lianxifangshi = 0
    private var qurenzheng = 0
    private var wanshanziliao = 0


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
            inflater?.inflate(R.layout.dialog_womenauth_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AppUtils.setTvStyle(context, resources.getString(R.string.first_step_info), 0, 11, tv_base_info);
//        AppUtils.setTvStyle( this, resources.getString(R.string.second_step_info),0 ,10 , tv_contact_info);
        AppUtils.setTvStyle(context, resources.getString(R.string.third_step_info), 0, 9, tv_auth);

        iv_womenauth_close.setOnClickListener {
            dismissAllowingStateLoss()
        }

        //第一步认证
        tv_base_info.setOnClickListener {
            dialogListener?.onClick(1,"baseinfo")
        }

        //第三步认证
        tv_auth.setOnClickListener {
            isBaseActivity {
//                var localUserId = getLocalUserId()
                chatService(it)
//                it.pushCustomerMessage(it, localUserId, 2, localUserId, next = {
//                    chatService(it)
//                })
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getAuthPercent()
    }

    private fun getAuthPercent() {
        isBaseActivity {
                Request.getAuthState(getLocalUserId()).request(it, success = { _, data ->
                    if (data != null&&context!= null) {
                        wanshanziliao = data.optDouble("wanshanziliao").toInt()
                        tv_percent.text = "${wanshanziliao * 10}%"
                        lianxifangshi = data.optInt("lianxifangshi")
                        if (lianxifangshi == 0) {
                            tv_contact_state.text = "未完成"
                        } else {
                            tv_contact_state.text = "已完成"
                            tv_contact_state.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                        }
                        qurenzheng = data.optInt("qurenzheng")
                        if (qurenzheng == 0) {
                            tv_auth_state.text = "未完成"
                        } else {
                            tv_auth_state.text = "已完成"
                            tv_contact_state.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                        }
                    } else {

                    }
                })
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