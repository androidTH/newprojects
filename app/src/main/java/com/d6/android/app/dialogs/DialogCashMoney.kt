package com.d6.android.app.dialogs

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.interfaces.RequestManager
import com.d6.android.app.models.UserData
import com.d6.android.app.net.Request
import com.d6.android.app.net.Request.pushCustomerMessage
import com.d6.android.app.utils.*
import com.d6.android.app.widget.CustomToast
import com.umeng.socialize.UMAuthListener
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.bean.SHARE_MEDIA
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_cash_widthdrawal.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.textColor
import org.jetbrains.anko.wrapContent

/**
 * 提现
 */
class DialogCashMoney : DialogFragment(), RequestManager {

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private var userJson = SPUtils.instance().getString(Const.USERINFO)
    private var mUserInfo = GsonHelper.getGson().fromJson(userJson,UserData::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FadeDialog)
    }

    private val compositeDisposable by lazy {
        CompositeDisposable()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window.setLayout((screenWidth() * 0.8f).toInt() + dip(30), wrapContent)
        dialog.window.setGravity(Gravity.CENTER)
        dialog.setCanceledOnTouchOutside(true)
    }

    override fun show(manager: FragmentManager?, tag: String?) {
        val ft = manager?.beginTransaction()
        ft?.add(this, tag)
        ft?.commitAllowingStateLoss()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.dialog_cash_widthdrawal, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var cashmoney = "0"
        var type = -1
        try{
            cashmoney =  if(arguments.containsKey("cashmoney")){
                arguments.getString("cashmoney")
            }else{
                "0"
            }
            type = arguments.getInt("type")
        }catch (e:Exception){
            e.printStackTrace()
            cashmoney = "0"
        }

        tv_cash_money.text = String.format(getString(R.string.string_cash_money), cashmoney)
        tv_redheart_nums.text = "我的 [img src=redheart_small/] : ${cashmoney}（10 [img src=redheart_small/]=1元）"

        if(type==2){
            tv_cashmeny_tips.text = "提现将收取20%的手续费，单次最低提现额度20朵小红花，最高200朵小红花"
            linear_redhearliked.visibility = View.GONE
            tv_redheartliked.visibility = View.VISIBLE
        }else{
            tv_cashmeny_tips.text = "提现将收取20%的手续费，最低提现金额20元"
            linear_redhearliked.visibility = View.VISIBLE
            tv_redheartliked.visibility = View.GONE
        }

        tv_close.setOnClickListener {
            KeyboardktUtils().hideKeyboard(it)
            dismissAllowingStateLoss()
        }

        tv_bindwx.setOnClickListener {
            if (mUserInfo.wxname.isNullOrEmpty()) {
                weChatLogin()
            } else {
                weChatLogin()
            }
        }

        et_cash_input.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    tv_cashok.backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.shape_4r_80_orange)
                } else {
                    tv_cashok.backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.shape_orange)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        tv_cashok.setOnClickListener {
            if(!TextUtils.isEmpty(mUserInfo.wxname)){
                var money = et_cash_input.text.toString().trim()
                if(!TextUtils.isEmpty(money)){
                    var mCashMoney = money.toInt()
                    if (mCashMoney <= cashmoney.toFloat()) {
                        if(mCashMoney>= 20){
                            doCashMoney(money,type)
                        }else{
                            showToast("最低提现金额不能小于20元！")
                        }
                    } else {
                        showToast("提现金额必须小于可提金额！")
                    }
                }
            }else{
                showToast("请绑定微信才能提现!")
            }
        }

        if(mUserInfo.wxname.isNullOrEmpty()){
            isBaseActivity{
                getUserInfo()
            }
        }else{
            iv_cash_headView.setImageURI(mUserInfo.wxpic)
            tv_bindwx.backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.shape_6r_88)
            tv_bindwx.textColor = ContextCompat.getColor(context, R.color.color_888888)
            tv_bindwx.text = "更换微信"
            tv_wx_username.text="微信：${mUserInfo.wxname}"
        }
    }

    private val shareApi by lazy {
        UMShareAPI.get(context)
    }

    private fun weChatLogin() {
        isBaseActivity {
            shareApi.getPlatformInfo(it, SHARE_MEDIA.WEIXIN, object : UMAuthListener {
                override fun onComplete(p0: SHARE_MEDIA?, p1: Int, data: MutableMap<String, String>?) {
                    if (data != null) {
                        val openId = if (data.containsKey("openid")) data["openid"] else ""
                        val unionid = if (data.containsKey("unionid")) data["unionid"] else ""
                        val name= if (data.containsKey("name")) data["name"] else ""
                        val gender = if (data.containsKey("gender")) data["gender"] else "" //"access_token" -> "15_DqQo8GAloYTRPrkvE9Mn1TLJx06t2t8jcTnlVjTtWtCtB10KlEQJ-pksniTDmRlN1qO8OMgEH-6WaTEPbeCYXLegAsvy6iolB3FHfefn4Js"
                        val iconUrl = if (data.containsKey("iconurl")) data["iconurl"] else "" //"refreshToken" -> "15_MGQzdG8xEsuOJP-LvI80gZsR0OLgpcKlTbWjiQXJfAQJEUufz4OxdqmTh6iZnnNZSgOgHskEv-N8FexuWMsqenRdRtSycKVNGKkgfiVNJGs"
                        bindwxId(openId,unionid,name.toString(),iconUrl.toString())
                    } else {
                        toast("拉取微信信息异常！")
                    }
                }

                override fun onCancel(p0: SHARE_MEDIA?, p1: Int) {
                    toast("取消")
                    it.dismissDialog()
                }

                override fun onError(p0: SHARE_MEDIA?, p1: Int, p2: Throwable?) {
                    p2?.printStackTrace()
                    it.dismissDialog()
                }

                override fun onStart(p0: SHARE_MEDIA?) {
                    it.dialog()
                }
            })
        }
    }

    /**
     * 绑定微信号
     */
    private fun bindwxId(wxid:String?,unionid:String?,swxname:String,swxpic:String){
        (context as BaseActivity).dismissDialog()
        Request.doBindWxId(userId,wxid.toString(),swxname,swxpic,unionid.toString()).request(this,false,success={msg,data->
            CustomToast.showToast(msg.toString())
            getUserInfo()
        }){code,msg->
            CustomToast.showToast(msg)
        }
    }

    private fun getUserInfo() {
        Request.getUserInfo("", userId).request(this, success = { _, data ->
            data?.let {
                if (it.wxname.isNullOrEmpty()) {
                    iv_cash_headView.setImageURI(Uri.parse("res://" + context.getPackageName() + "/" + R.mipmap.unbound_wechat_icon))
                    tv_bindwx.backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.shape_6r_oc)
                    tv_bindwx.textColor = ContextCompat.getColor(context, R.color.white)
                    tv_bindwx.text = "绑定微信"
                    tv_wx_username.text="未绑定微信"
                } else {
                    iv_cash_headView.setImageURI(it.wxpic)
                    tv_bindwx.backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.shape_6r_88)
                    tv_bindwx.textColor = ContextCompat.getColor(context, R.color.color_888888)
                    tv_bindwx.text = "更换微信"
                    tv_wx_username.text="微信：${it.wxname}"
                }
                mUserInfo = it
                SPUtils.instance().put(Const.USERINFO,GsonHelper.getGson().toJson(it)).apply()
            }
        })
    }

    /**
     * 提现
     */
    private fun doCashMoney(money:String,type:Int){
        isBaseActivity {
//            var mYKCashMoneyDialog =  YKCashMoneyDialog()
//            mYKCashMoneyDialog.arguments = bundleOf("title" to "提示","content" to "你提现的额度已经达到限额，请联系客服提现")
//            mYKCashMoneyDialog.show(it.supportFragmentManager,"YKCashMoneyDialog")
//            mYKCashMoneyDialog.setDialogListener { p, s ->
//                it.pushCustomerMessage(it,userId,6,""){
//                    chatService(it)
//                }
//            }

            it.dialog()
            if(type==2){
                Request.doCashMoney(userId,money).request(this,false,success={msg,data->
                    it.dismissDialog()
                    showToast("提现成功")
                    dialogListener?.onClick(1,money)
                    dismissAllowingStateLoss()
                }){code,msg->
                    it.dismissDialog()
                    var mYKCashMoneyDialog =  YKCashMoneyDialog()
                    mYKCashMoneyDialog.arguments = bundleOf("title" to "提示","content" to msg)
                    mYKCashMoneyDialog.show(it.supportFragmentManager,"YKCashMoneyDialog")
                    mYKCashMoneyDialog.setDialogListener { p, s ->
                        it.pushCustomerMessage(this,userId,6,""){
                            chatService(it)
                        }
                    }
                }
            }else{
                Request.doCashMoneyOfLoveHeart(userId,money).request(this,false,success={msg,data->
                    it.dismissDialog()
                    showToast("提现成功")
                    dialogListener?.onClick(1,money)
                    dismissAllowingStateLoss()
                }){code,msg->
                    it.dismissDialog()
                    var mYKCashMoneyDialog =  YKCashMoneyDialog()
                    mYKCashMoneyDialog.arguments = bundleOf("title" to "提示","content" to msg)
                    mYKCashMoneyDialog.show(it.supportFragmentManager,"YKCashMoneyDialog")
                    mYKCashMoneyDialog.setDialogListener { p, s ->
                        it.pushCustomerMessage(this,userId,6,""){
                            chatService(it)
                        }
                    }
                }
            }

        }
    }

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
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

    override fun showToast(msg: String) {
        toast(msg)
    }

    override fun onBind(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun dismissDialog() {
        (context as BaseActivity).dismissDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}