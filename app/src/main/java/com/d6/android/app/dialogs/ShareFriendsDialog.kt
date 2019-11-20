package com.d6.android.app.dialogs

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
import com.d6.android.app.activities.ShareFriendsActivity
import com.d6.android.app.adapters.DialogShareFriendsQuickAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.FriendBean
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.DIALOG_SHOW_MAX
import com.d6.android.app.widget.CustomToast
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import kotlinx.android.synthetic.main.dialog_sharefriends_layout.*
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.toast
import org.jetbrains.anko.wrapContent


/**
 * 分享好友
 */
class ShareFriendsDialog : DialogFragment() {

    private var mList = ArrayList<FriendBean>()
    private var userId = ""
    private var fromType = ""

    private val mUserId by lazy{
        getLocalUserId()
    }

    private val wxApi by lazy {
        WXAPIFactory.createWXAPI(context, "wx43d13a711f68131c")
    }

    private var iType = 0 //1、约会 2、动态 3、速约 4、觅约 5、个人主页
    private var sResourceId = ""
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
        if (arguments != null) {
            userId = arguments.getString("id", "")
            fromType = arguments.getString("from","")
        }

        initRecycler()
        if (TextUtils.equals(mUserId, userId)) {
            rv_chooseuser.visibility = View.VISIBLE
            tv_share.visibility = View.VISIBLE

            tv_report_user.visibility = View.GONE
            tv_joinblack.visibility = View.GONE
            tv_sharewx.visibility = View.GONE

            if(TextUtils.equals(fromType,"userInfo")){
                ll_action.visibility = View.GONE
                getUserFriends()
            }else if(TextUtils.equals(fromType,"square")){
                sResourceId = arguments.getString("sResourceId")
                iType = 2
                getUserFriends()
            }else if(TextUtils.equals(fromType,"myDateDetail")){
                ll_dialog_title.visibility = View.GONE
                rv_chooseuser.visibility = View.GONE
                sResourceId = arguments.getString("sResourceId")
                var sAppointmentSignupId = arguments.getString("sAppointmentSignupId","")
                iType = 1
                if(TextUtils.isEmpty(sAppointmentSignupId)){
                    tv_deldate.visibility = View.VISIBLE
                }else{
                    ll_action.visibility = View.VISIBLE
                }
                Log.i("shareFriends","sid=${sAppointmentSignupId}")
            }else{
                tv_deldate.visibility = View.VISIBLE
                getUserFriends()
            }
        } else {
            tv_report_user.visibility = View.VISIBLE
            tv_deldate.visibility = View.GONE
            tv_sharewx.visibility = View.GONE

            var fromType = arguments.getString("from")
            if(TextUtils.equals(fromType,"userInfo")){
                tv_joinblack.visibility = View.VISIBLE
                var isInBlackList = arguments.getInt("isInBlackList", 0)
                if (isInBlackList == 1) {
                    tv_joinblack.text = getString(R.string.string_removeblacklist)
                } else {
                    tv_joinblack.text = resources.getString(R.string.string_joinblack)
                }
                iType = 5
                sResourceId = arguments.getString("sResourceId")
                showFriends()
            }else if(TextUtils.equals(fromType,"Recommend_speedDate")||TextUtils.equals(fromType,"Recommend_findDate")){
                tv_sharewx.visibility = View.VISIBLE
                showFriends()
                sResourceId = arguments.getString("sResourceId")
                iType = if(TextUtils.equals(fromType,"Recommend_speedDate")){
                    3
                }else{
                    4
                }
            }else if(TextUtils.equals(fromType,"selfPullDate")||TextUtils.equals(fromType,"myDateDetail")){
//                showFriends()
                ll_dialog_title.visibility = View.GONE
                tv_sharewx.visibility = View.GONE
                rv_chooseuser.visibility = View.GONE
                sResourceId = arguments.getString("sResourceId")
                iType = 1
            }else if(TextUtils.equals(fromType,"square")){
                showFriends()
                sResourceId = arguments.getString("sResourceId")
                iType = 2
            }else if(TextUtils.equals(fromType,"mysquare")){
                showFriends()
                sResourceId = arguments.getString("sResourceId")
                iType = 2
            }else{
                tv_share.visibility = View.GONE
                tv_share_tips.visibility = View.GONE
                rv_chooseuser.visibility = View.GONE
                tv_joinblack.visibility = View.GONE
            }
        }

        mDialogShareFriendsQuickAdapter.setOnItemClickListener { adapter, view, position ->
            if (position == DIALOG_SHOW_MAX) {
                startActivity<ShareFriendsActivity>("iType" to iType,"sResourceId" to sResourceId)
                dismissAllowingStateLoss()
            } else {
               shareChat((context as BaseActivity),"dynamic", getUserSex(),mUserId)
               isBaseActivity {
                   var friendBean = mDialogShareFriendsQuickAdapter.getItem(position)
                   Request.shareMessage(mUserId,iType,sResourceId,friendBean?.iUserid.toString()).request(it,true,success = {msg,data->
                       it.toast(msg.toString())
                       dismissAllowingStateLoss()
                   })
               }
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

        tv_sharewx.setOnClickListener {
            if (wxApi.isWXAppInstalled){
                (context as BaseActivity).isAuthUser{
                    dialogListener?.onClick(3,"")//1代表删除
                }
                dismissAllowingStateLoss()
            }else{
                CustomToast.showToast("请先安装微信!")
            }
        }

        tv_cancel.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

    private fun initRecycler(){
        rv_chooseuser.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        rv_chooseuser.adapter = mDialogShareFriendsQuickAdapter
    }

    private fun showFriends(){
        tv_share.visibility = View.VISIBLE
        rv_chooseuser.visibility = View.VISIBLE
        getUserFriends()
    }

    private fun getUserFriends() {
        isBaseActivity {
            Request.findUserFriends(mUserId, "",1).request(it) { _, data ->
                if (data?.list?.results == null || data.list.results.isEmpty()) {
                    rv_chooseuser.visibility = View.GONE
                    tv_share.visibility = View.GONE
                } else {
                    if(data.list.results.size>10){
                        mList.addAll(data.list.results.subList(0, 10))
                    }else{
                        mList.addAll(data.list.results)
                    }
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