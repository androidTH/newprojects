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
import com.d6.android.app.utils.*
import com.d6.android.app.widget.CustomToast
import com.umeng.socialize.UMAuthListener
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.bean.SHARE_MEDIA
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_cash_widthdrawal.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.textColor
import org.jetbrains.anko.wrapContent

/**
 * 约会发送出错
 */
class DialogCashMoney : DialogFragment(), RequestManager {

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

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

        var mUserInfo: UserData = (arguments.get("data") as UserData)
        var cashmoney = arguments.getString("cashmoney")

        tv_cash_money.text = String.format(getString(R.string.string_cash_money), cashmoney)

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
                var money = et_cash_input.text.toString().toFloat()
                if (money <= cashmoney.toFloat()) {
                    if(money>=20){
                        doCashMoney(et_cash_input.text.toString())
                    }else{
                        showToast("最低提现金额不能小于20元！")
                    }
                } else {
                    showToast("提现金额必须小于可提金额！")
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

    private var dialogListener: OnDialogListener? = null

    fun setDialogListener(l: (p: Int, s: String?) -> Unit) {
        dialogListener = object : OnDialogListener {
            override fun onClick(position: Int, data: String?) {
                l(position, data)
            }
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
                        val name= if (data.containsKey("name")) data["name"] else ""
                        val gender = if (data.containsKey("gender")) data["gender"] else "" //"access_token" -> "15_DqQo8GAloYTRPrkvE9Mn1TLJx06t2t8jcTnlVjTtWtCtB10KlEQJ-pksniTDmRlN1qO8OMgEH-6WaTEPbeCYXLegAsvy6iolB3FHfefn4Js"
                        val iconUrl = if (data.containsKey("iconurl")) data["iconurl"] else "" //"refreshToken" -> "15_MGQzdG8xEsuOJP-LvI80gZsR0OLgpcKlTbWjiQXJfAQJEUufz4OxdqmTh6iZnnNZSgOgHskEv-N8FexuWMsqenRdRtSycKVNGKkgfiVNJGs"
                        bindwxId(openId,name.toString(),iconUrl.toString())
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

    private fun bindwxId(wxid:String?,swxname:String,swxpic:String){
        (context as BaseActivity).dismissDialog()
        Request.doBindWxId(userId,wxid.toString(),swxname,swxpic).request(this,false,success={msg,data->
            getUserInfo()
        }){code,msg->
            CustomToast.showToast(msg)
            (context as BaseActivity).dismissDialog()
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
            }
        })
    }

    /**
     * 提现
     */
    private fun doCashMoney(money:String){
        isBaseActivity {
            Request.doCashMoney(userId,money).request(this,false,success={msg,data->
                it.dismissDialog()
            }){code,msg->
               showToast(msg)
                it.dismissDialog()
            }
        }
    }

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
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