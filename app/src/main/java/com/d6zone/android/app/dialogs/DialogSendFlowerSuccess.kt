package com.d6zone.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6zone.android.app.R
import com.d6zone.android.app.base.BaseActivity
import com.d6zone.android.app.eventbus.LikeMsgEvent
import com.d6zone.android.app.extentions.request
import com.d6zone.android.app.interfaces.RequestManager
import com.d6zone.android.app.models.UserData
import com.d6zone.android.app.net.Request
import com.d6zone.android.app.utils.*
import com.google.gson.JsonObject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.rong.imkit.RongIM
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.dialog_send_redflower_success.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.toast

/**
 * 约会发送出错
 */
class DialogSendFlowerSuccess : DialogFragment(),RequestManager {

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private var mUserInfo: UserData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FadeDialog)
    }

    private val compositeDisposable by lazy {
        CompositeDisposable()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window.setLayout((screenWidth() * 0.8f).toInt()+dip(30), wrapContent)
        dialog.window.setGravity(Gravity.CENTER)
        dialog.setCanceledOnTouchOutside(true)
    }

    override fun show(manager: FragmentManager?, tag: String?) {
        val ft = manager?.beginTransaction()
        ft?.add(this, tag)
        ft?.commitAllowingStateLoss()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.dialog_send_redflower_success, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var id = arguments.getString("userId")
        var nums = arguments.getString("nums")
        var mToFromType = arguments.getInt("ToFromType")
        tv_sendflower_nums.text = nums

        if (mToFromType == 5) {
            tv_sendflower_success_siliao.visibility = View.GONE
        }

        tv_close.setOnClickListener { dismissAllowingStateLoss() }

        tv_sendflower_success_like.setOnClickListener {
            mUserInfo?.let {
                if (it.iIsFollow == 0) {
                    addFollow(it.accountId.toString())
                } else {
                    delFollow(it.accountId.toString())
                }
            }
        }

        tv_sendflower_success_siliao.setOnClickListener {
            mUserInfo?.let {
                showDatePayPointDialog(id, it.name.toString())
            }
        }
        getUserInfo(id)
    }

    private fun getUserInfo(id: String) {
        Request.getUserInfo(userId, id).request(this,false,success={msg,data->
            data?.let {
                mUserInfo = it
                tv_sendflower_success_name.text = it.name
                iv_sendflower_success_headView.setImageURI(it.picUrl)
                if(it.iIsFollow !=null) {
                    if (it.iIsFollow == 1) {
                        tv_sendflower_success_like.visibility = View.GONE

                        tv_sendflower_success_like.setCompoundDrawables(null,null,null,null);
                        tv_sendflower_success_like.text = resources.getString(R.string.string_liked)
                        tv_sendflower_success_like.backgroundResource = R.drawable.shape_20r_33ff6452
                        tv_sendflower_success_like.textColor = ContextCompat.getColor(AppUtils.context,R.color.color_FF6452)

                        tv_sendflower_success_like.setPadding(resources.getDimensionPixelSize(R.dimen.margin_20),resources.getDimensionPixelSize(R.dimen.margin_8),resources.getDimensionPixelSize(R.dimen.margin_20),resources.getDimensionPixelSize(R.dimen.margin_8))
                        tv_sendflower_success_siliao.setPadding(resources.getDimensionPixelSize(R.dimen.padding_40),resources.getDimensionPixelSize(R.dimen.margin_8),resources.getDimensionPixelSize(R.dimen.padding_40),resources.getDimensionPixelSize(R.dimen.margin_8))

                    }else{
                        tv_sendflower_success_like.text= resources.getString(R.string.string_like)
                        tv_sendflower_success_like.backgroundResource = R.drawable.shape_20r_ff6
                        tv_sendflower_success_like.textColor = ContextCompat.getColor(AppUtils.context,R.color.white)

                        tv_sendflower_success_like.setPadding(resources.getDimensionPixelSize(R.dimen.margin_20),resources.getDimensionPixelSize(R.dimen.margin_12),resources.getDimensionPixelSize(R.dimen.margin_20),resources.getDimensionPixelSize(R.dimen.margin_12))
                    }
                }
            }
        })
    }

    private var dialogListener: OnDialogListener? = null

    fun setDialogListener(l: (p: Int, s: String?) -> Unit) {
        dialogListener = object : OnDialogListener {
            override fun onClick(position: Int, data: String?) {
                l(position, data)
            }
        }
    }

    private fun addFollow(id:String){
        Request.getAddFollow(userId, id).request(this,true){ s: String?, jsonObject: JsonObject? ->
            //            toast("$s,$jsonObject")
//            headerView.iv_isfollow.imageResource = R.mipmap.usercenter_liked_button

            tv_sendflower_success_like.setCompoundDrawables(null,null,null,null);

            tv_sendflower_success_like.text = resources.getString(R.string.string_liked)
            tv_sendflower_success_like.backgroundResource = R.drawable.shape_20r_33ff6452
            tv_sendflower_success_like.textColor = ContextCompat.getColor(AppUtils.context,R.color.color_FF6452)

            tv_sendflower_success_like.setPadding(resources.getDimensionPixelSize(R.dimen.margin_20),resources.getDimensionPixelSize(R.dimen.margin_8),resources.getDimensionPixelSize(R.dimen.margin_20),resources.getDimensionPixelSize(R.dimen.margin_8))
            tv_sendflower_success_siliao.setPadding(resources.getDimensionPixelSize(R.dimen.padding_40),resources.getDimensionPixelSize(R.dimen.margin_8),resources.getDimensionPixelSize(R.dimen.padding_40),resources.getDimensionPixelSize(R.dimen.margin_8))

            mUserInfo?.iIsFollow = 1
            showTips(jsonObject,"","")
            EventBus.getDefault().post(LikeMsgEvent(1))
        }
    }

    private fun delFollow(id:String){
        Request.getDelFollow(userId, id).request(this){ s: String?, jsonObject: JsonObject? ->
            //            data.optDouble("wanshanziliao")
//            toast("$s,$jsonObject")
//            headerView.iv_isfollow.imageResource = R.mipmap.usercenter_like_button

            var drawable = ContextCompat.getDrawable(AppUtils.context,R.mipmap.icon_like_button)
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//这句一定要加
            tv_sendflower_success_like.setCompoundDrawables(drawable,null,null,null);

            tv_sendflower_success_like.text= resources.getString(R.string.string_like)
            tv_sendflower_success_like.backgroundResource = R.drawable.shape_20r_ff6
            tv_sendflower_success_like.textColor = ContextCompat.getColor(AppUtils.context,R.color.white)


            tv_sendflower_success_like.setPadding(resources.getDimensionPixelSize(R.dimen.padding_30),resources.getDimensionPixelSize(R.dimen.margin_8),resources.getDimensionPixelSize(R.dimen.padding_30),resources.getDimensionPixelSize(R.dimen.margin_8))
            tv_sendflower_success_siliao.setPadding(resources.getDimensionPixelSize(R.dimen.padding_30),resources.getDimensionPixelSize(R.dimen.margin_8),resources.getDimensionPixelSize(R.dimen.padding_30),resources.getDimensionPixelSize(R.dimen.margin_8))
            mUserInfo?.iIsFollow = 0
            EventBus.getDefault().post(LikeMsgEvent(0))
        }
    }

    private fun showDatePayPointDialog(id:String,name:String){
        (activity as BaseActivity).isAuthUser{
            RongIM.getInstance().startConversation(activity, Conversation.ConversationType.PRIVATE, id, name)
//            Request.getApplyStatus(userId, id).request(this, false, success = { msg, jsonObjetct ->
//                jsonObjetct?.let {
//                    var code = it.optInt("code")
//                    if (code != 7) {
//                        RongIM.getInstance().startConversation(activity, Conversation.ConversationType.PRIVATE, id, name)
//                    }
//                }
//            })
        }
//        Request.getApplyStatus(userId,id).request(this,false,success={msg,jsonObjetct->
//            jsonObjetct?.let {
//                var code = it.optInt("code")
//                if(code!=7){
//                    if(code == 8){
//                        CustomToast.showToast(getString(R.string.string_addblacklist))
//                    }else {
//                        RongIM.getInstance().startConversation(activity, Conversation.ConversationType.PRIVATE, id, name)
//                    }
//                }else{
//                    startActivity<DateAuthStateActivity>()
//                }
//            }
//        })
//        if(TextUtils.equals("7",userclassId)){
//            startActivity<DateAuthStateActivity>()
//        }else{
//            RongIM.getInstance().startConversation(activity, Conversation.ConversationType.PRIVATE, id, name)
//        }
//        Request.doTalkJustify(userId, id).request(this,false,success = {msg,data->
//            if(data!=null){
//                var code = data!!.optInt("code")
//                if(code == 1){
//                    var point = data!!.optString("iTalkPoint")
//                    var remainPoint = data!!.optString("iRemainPoint")
//                    if(point.toInt() > remainPoint.toInt()){
//                        dismissAllowingStateLoss()
//                        val dateDialog = OpenDatePointNoEnoughDialog()
//                        var point = data!!.optString("iTalkPoint")
//                        var remainPoint = data!!.optString("iRemainPoint")
//                        dateDialog.arguments= bundleOf("point" to point,"remainPoint" to remainPoint)
//                        dateDialog.show((context as BaseActivity).supportFragmentManager, "d")
//                    }else{
//                        dismissAllowingStateLoss()
//                        val dateDialog = OpenDatePayPointDialog()
//                        dateDialog.arguments= bundleOf("point" to point,"remainPoint" to remainPoint,"username" to name,"chatUserId" to id)
//                        dateDialog.show((context as BaseActivity).supportFragmentManager, "d")
//                    }
//                } else if(code == 0){
//                    showToast(msg.toString())
////                    RongIM.getInstance().startConversation(this, Conversation.ConversationType.PRIVATE, id, name)
//                } else {
//                    dismissAllowingStateLoss()
//                    val dateDialog = OpenDatePointNoEnoughDialog()
//                    var point = data!!.optString("iTalkPoint")
//                    var remainPoint = data!!.optString("iRemainPoint")
//                    dateDialog.arguments= bundleOf("point" to point,"remainPoint" to remainPoint)
//                    dateDialog.show((context as BaseActivity).supportFragmentManager, "d")
//                }
//            }else{
//                dismissAllowingStateLoss()
//                RongIM.getInstance().startConversation(context, Conversation.ConversationType.PRIVATE, id, name)
//            }
//        }) { code, msg ->
//            if(code == 0){
//                showToast(msg)
//            }
//        }
    }

    override fun showToast(msg: String) {
        toast(msg)
    }

    override fun onBind(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun dismissDialog() {

    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}