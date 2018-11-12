package com.d6.android.app.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.umeng.socialize.UMAuthListener
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.bean.SHARE_MEDIA
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.jetbrains.anko.*
import org.json.JSONObject

/**
 *
 * 登录
 */
class SignInActivity : BaseActivity() {

    private var countryCode = "+86"
    private var type = 1
    private val wxApi by lazy {
        WXAPIFactory.createWXAPI(this, "wx43d13a711f68131c")
    }
    private val shareApi by lazy {
        UMShareAPI.get(this)
    }

    private val devicetoken by lazy{
        SPUtils.instance().getString(Const.User.DEVICETOKEN)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        immersionBar.init()
//        title = "D6社区"

        btn_sign_in.setOnClickListener {
            phoneLogin()
        }

        tv_forget_password.setOnClickListener {
            startActivity<ForgetPasswordActivity>()
        }

        action_sign_up.setOnClickListener {
            startActivity<SignUpActivity>()
        }

        tv_contact.setOnClickListener {
            //            val contactUsDialog = ContactUsDialog()
//            contactUsDialog.show(supportFragmentManager,"")
            startActivity<ContactUsActivity>()
        }

        tv_type.setOnClickListener {
            //            val selectTypeDialog = SelectLoginTypeDialog()
//            selectTypeDialog.show(supportFragmentManager, "")
//            selectTypeDialog.setDialogListener { p, s ->
//                type = p
//                et_phone.hint = if (p == 0) {
//                    "输入会员账号"
//                } else {
//                    "输入手机号"
//                }
//                tv_type.text = s
//            }
            startActivityForResult<ChooseCountryActivity>(1)
        }

        et_phone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val isPhone = isPhone(et_phone.text)
                if (isPhone) {
                    phoneFormatOk()
                } else {
                    phoneFormatError()
                }
                btn_sign_in.isEnabled = !(et_code.text.isNullOrEmpty() || !isPhone)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

//        et_phone.setOnFocusChangeListener { view, b ->
//            if (b) {
//
//            } else {
//
//            }
//        }

        tv_get_code.setOnClickListener {
            getCode()
        }

        et_code.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                btn_sign_in.isEnabled = !(s.isNullOrEmpty() || !isPhone(et_phone.text))
                if (s.isNullOrEmpty()) {
                    tv_code_error.visible()
//                    codeLine.setBackgroundResource(R.color.red_fc3)
                } else {
                    tv_code_error.gone()
//                    codeLine.setBackgroundResource(R.color.dividing_line_color)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        tv_wechat_login.setOnClickListener {
            if (wxApi.isWXAppInstalled) {
                weChatLogin()
            } else {
                toast("请先安装微信")
            }
        }

        action_protocols.movementMethod = LinkMovementMethod.getInstance()
        val s = "注册即表示同意 D6社区用户协议"
        action_protocols.text = SpanBuilder(s)
                .click(s.length - 8, s.length, MClickSpan(this))
                .build()

        SPUtils.instance().put(Const.User.IS_FIRST,false).apply()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        shareApi.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                val code = data?.getStringExtra("code")
                tv_type.text = code
                countryCode = code ?: ""
            }
        }
    }

    private fun isPhone(s: Editable?): Boolean {
//        return if (countryCode == "+86") {//中国
//            s.toString().isValidPhone()
//        } else {
//            !s.isNullOrEmpty()
//        }
        return !s.isNullOrEmpty()
    }

    private fun phoneFormatOk() {
        tv_get_code.isEnabled = true
        tv_get_code.textColor = ContextCompat.getColor(this@SignInActivity, R.color.color_F7AB00)
        tv_get_code.backgroundResource = R.drawable.shape_code_btn_bg
//        phoneLine.setBackgroundResource(R.color.dividing_line_color)
        phoneLine.backgroundResource = R.color.dividing_line_color
        tv_phone_error.invisible()
    }

    private fun phoneFormatError() {
        if (tv_get_code.isEnabled) {
            tv_get_code.isEnabled = false
            tv_get_code.textColor = ContextCompat.getColor(this@SignInActivity, R.color.color_CCCCCC)
            tv_get_code.backgroundResource = R.drawable.circle_gray_bg
        }
        phoneLine.backgroundResource = R.color.red_fc3
        tv_phone_error.visible()
    }

    private fun getCode() {
        val phone = et_phone.text.toString().trim()
        if (phone.isEmpty()) {
            showToast("请输入正确的手机号")
            return
        }
        val p = if (countryCode == "+86") {
            phone
        } else if (countryCode.startsWith("+")) {
            countryCode.substring(1) + "-" + phone
        } else {
            "$countryCode-$phone"
        }
        dialog()
        Request.getVerifyCodeV2(p, 0).request(this) { msg, data ->
            showToast("验证码发送成功")
            tv_get_code.isEnabled = false
            countDownTimer.start()
            tv_get_code.textColor = ContextCompat.getColor(this@SignInActivity, R.color.color_CCCCCC)
            tv_get_code.backgroundResource = R.drawable.circle_gray_bg
        }
    }

    private fun weChatLogin() {
        shareApi.getPlatformInfo(this, SHARE_MEDIA.WEIXIN, object : UMAuthListener {
            override fun onComplete(p0: SHARE_MEDIA?, p1: Int, data: MutableMap<String, String>?) {
                dismissDialog()
                if (data != null) {
                    val openId = if (data.containsKey("openid")) data["openid"] else ""
                    val name = if (data.containsKey("name")) data["name"] else ""
                    val gender = if (data.containsKey("gender")) data["gender"] else ""
                    val iconUrl = if (data.containsKey("iconurl")) data["iconurl"] else ""
                    sysErr("------->$gender--->$openId--->$name")
                    thirdLogin(openId ?: "", name ?: "", iconUrl ?: "", gender ?: "")
                } else {
                    toast("拉取微信信息异常！")
                }
            }

            override fun onCancel(p0: SHARE_MEDIA?, p1: Int) {
                toast("取消登录")
                dismissDialog()
            }

            override fun onError(p0: SHARE_MEDIA?, p1: Int, p2: Throwable?) {
                p2?.printStackTrace()
                toast("微信登录异常！")
                dismissDialog()
            }

            override fun onStart(p0: SHARE_MEDIA?) {
                dialog()
            }

        })
    }

    private fun phoneLogin() {
        val phone = et_phone.text.toString().trim()
        if (phone.isEmpty()) {
//            if (type == 1) {
            toast("请输入手机号")
//            } else {
//                toast("请输入会员账号")
//            }
            return
        }

        val code = et_code.text.toString().trim()
        if (code.isEmpty()) {
            toast("验证码不能为空")
            return
        }
        dialog()
        //【1:phone方式登录，0: 微信登录】
        val p = if (countryCode == "+86") {
            phone
        } else if (countryCode.startsWith("+")) {
            countryCode.substring(1) + "-" + phone
        } else {
            "$countryCode-$phone"
        }
        sysErr("------->$p")
        Request.loginV2(1, code, p,devicetoken).request(this) { msg, data ->
            msg?.let {
                try {
                    val json = JSONObject(it)
                    val token = json.optString("token")
                    SPUtils.instance().put(Const.User.RONG_TOKEN, token)
                            .apply()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            saveUserInfo(data)
            data?.let {
                val info = UserInfo(data.accountId, data.name, Uri.parse("" + data.picUrl))
                RongIM.getInstance().refreshUserInfoCache(info)
            }
            if (data?.name == null || data.name!!.isEmpty()) {//如果没有昵称
                startActivity<SetUserInfoActivity>()
            } else {
                SPUtils.instance().put(Const.User.IS_LOGIN, true).apply()
                startActivity<MainActivity>()
            }
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun thirdLogin(openId: String, name: String, url: String, gender: String) {
        dialog("登录中...")
        Request.loginV2(0, openId = openId).request(this) { msg, data ->
            msg?.let {
                try {
                    val json = JSONObject(it)
                    val token = json.optString("token")
                    SPUtils.instance().put(Const.User.RONG_TOKEN, token)
                            .apply()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            saveUserInfo(data)
            data?.let {
                val info = UserInfo(data.accountId, data.name, Uri.parse("" + data.picUrl))
                RongIM.getInstance().refreshUserInfoCache(info)
            }
            if (data?.name == null || data.name!!.isEmpty()) {//如果没有昵称
                startActivity<SetUserInfoActivity>("name" to name, "gender" to gender)
            } else {
                SPUtils.instance().put(Const.User.IS_LOGIN, true).apply()
                startActivity<MainActivity>()
            }
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private val countDownTimer = object : CountDownTimer(60 * 1000, 1000) {
        override fun onFinish() {
            tv_get_code.text = "重新获取"
            tv_get_code.isEnabled = true
//            tv_get_code.textColor = ContextCompat.getColor(this@SignInActivity, R.color.color_CCCCCC)
        }

        override fun onTick(millisUntilFinished: Long) {
            tv_get_code.text = String.format("%ss", millisUntilFinished / 1000)//重新获取(%s)
        }
    }

    private class MClickSpan(val context: Context) : ClickableSpan() {
        override fun onClick(p0: View?) {
            context.startActivity<WebViewActivity>("title" to "用户协议", "url" to "file:///android_asset/yonghuxieyi.html")
        }

        override fun updateDrawState(ds: TextPaint?) {
            ds?.color = ContextCompat.getColor(context, R.color.color_C1C1C6)
            ds?.isUnderlineText = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            countDownTimer.cancel()
        } catch (e: Exception) {

        }
    }
}
