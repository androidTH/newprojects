package com.d6.android.app.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_bindphone_layout.*
import org.jetbrains.anko.*
import org.json.JSONObject

/**
 *
 * 绑定手机号
 */
class BindPhoneActivity : TitleActivity() {

    private var countryCode = "+86"

    private val devicetoken by lazy{
        SPUtils.instance().getString(Const.User.DEVICETOKEN)
    }

    private val channel by lazy{
        SPUtils.instance().getString(Const.OPENSTALL_CHANNEL,"")
    }

    private val openId by lazy {
        intent.getStringExtra("openId")
    }

    private val unionId by lazy {
        intent.getStringExtra("unionId")
    }

    private val name by lazy{
        intent.getStringExtra("name")
    }

    private val gender by lazy{
        intent.getStringExtra("gender")
    }

    private val headerpic by lazy {
        intent.getStringExtra("headerpic")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bindphone_layout)
        immersionBar.init()

        setTitleBold("绑定手机号",true)

        btn_bindphone.setOnClickListener(View.OnClickListener {
            phoneLogin()
        })

        tv_type.setOnClickListener {
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
                btn_bindphone.isEnabled = !(et_code.text.isNullOrEmpty() || !isPhone)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        tv_get_code.setOnClickListener {
            getCode()
        }

        et_code.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                btn_bindphone.isEnabled = !(s.isNullOrEmpty() || !isPhone(et_phone.text))
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                val code = data?.getStringExtra("code")
                tv_type.text = code
                countryCode = code ?: ""
            }else if(requestCode == 3){
                setResult(Activity.RESULT_OK)
                finish()
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
        tv_get_code.textColor = ContextCompat.getColor(this@BindPhoneActivity, R.color.white)//color_F7AB00
        tv_get_code.backgroundResource = R.drawable.shape_code_bindphone_bg //
//        phoneLine.setBackgroundResource(R.color.dividing_line_color)
        phoneLine.backgroundResource = R.color.dividing_line_color
        tv_phone_error.invisible()
    }

    private fun phoneFormatError() {
        if (tv_get_code.isEnabled) {
            tv_get_code.isEnabled = false
            tv_get_code.textColor = ContextCompat.getColor(this@BindPhoneActivity, R.color.color_CCCCCC)
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
            tv_get_code.textColor = ContextCompat.getColor(this@BindPhoneActivity, R.color.color_CCCCCC)
            tv_get_code.backgroundResource = R.drawable.circle_gray_bg
        }
    }

    private fun phoneLogin() {
        val phone = et_phone.text.toString().trim()
        if (phone.isEmpty()) {
            toast("请输入手机号")
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

       Request.bindPhone(p,code,openId,unionId,devicetoken,name,headerpic,sChannelId = channel).request(this,false,success = {msg,data->
           saveMsg(msg)
           saveUserInfo(data)
           data?.let {
               val info = UserInfo(data.accountId, data.name, Uri.parse("" + data.picUrl))
               RongIM.getInstance().refreshUserInfoCache(info)
           }
           if (data?.name == null || data.name!!.isEmpty()) {//如果没有昵称
               startActivityForResult<SetUserInfoActivity>(3,"name" to name, "gender" to gender,"headerpic" to headerpic)
           } else {
               SPUtils.instance().put(Const.User.IS_LOGIN, true).apply()
               startActivity<MainActivity>()
               setResult(Activity.RESULT_OK)
               finish()
           }
       }){code,msg->
           if (code == 2) {
               SPUtils.instance().put(Const.User.IS_LOGIN, true).apply()
               startActivity<MainActivity>()
               setResult(Activity.RESULT_OK)
               finish()
           } else {
               showToast(msg)
           }
       }
    }

    private fun saveMsg(msg:String?){
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

    override fun onDestroy() {
        super.onDestroy()
        try {
            countDownTimer.cancel()
        } catch (e: Exception) {

        }
        immersionBar.destroy()
    }
}
