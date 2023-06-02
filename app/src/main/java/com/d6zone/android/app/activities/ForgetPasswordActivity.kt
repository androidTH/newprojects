package com.d6zone.android.app.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputFilter
import com.d6zone.android.app.R
import com.d6zone.android.app.base.TitleActivity
import com.d6zone.android.app.dialogs.SelectCountryTypeDialog
import com.d6zone.android.app.extentions.request
import com.d6zone.android.app.net.Request
import kotlinx.android.synthetic.main.activity_forget_password.*
import org.jetbrains.anko.startActivityForResult

/**
 * 忘记密码
 */
class ForgetPasswordActivity : TitleActivity() {
    private var type = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)
        title = "忘记密码"

        tv_get_code.setOnClickListener {
            getCode()
        }

        btn_next.setOnClickListener {
            next()
        }
        tv_type.setOnClickListener {
            val selectTypeDialog = SelectCountryTypeDialog()
            selectTypeDialog.show(supportFragmentManager,"")
            selectTypeDialog.setDialogListener { p, s ->
                type = p
                tv_type.text = s
                if (type == 0) {//国外
                    et_phone.hint="区号-手机号,如00-0123456789"
                    et_phone.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(12))
                } else {
                    et_phone.hint="输入手机号"
                    et_phone.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(11))
                }
            }
        }
    }

    private fun getCode() {
        val phone = et_phone.text.toString().trim()
        if (type == 0) {
            if (!phone.contains("-")) {
                showToast("请输入区号-手机号格式")
                return
            }
        } else {
            if (phone.length != 11) {
                showToast("请输入正确的手机号")
                return
            }
        }
        dialog()
        Request.sendSMSCode(phone,1,type).request(this){ msg, _->
            showToast("验证码发送成功")
            tv_get_code.isEnabled = false
            countDownTimer.start()
        }
    }

    private val countDownTimer = object : CountDownTimer(60 * 1000, 1000) {
        override fun onFinish() {
            tv_get_code.text = "重新获取"
            tv_get_code.isEnabled = true
        }

        override fun onTick(millisUntilFinished: Long) {
            tv_get_code.text = String.format("%s", millisUntilFinished / 1000)
        }
    }

    private fun next() {
        val phone = et_phone.text.toString().trim()
        if (type == 0) {
            if (!phone.contains("-")) {
                showToast("请输入区号-手机号格式")
                return
            }
        } else {
            if (phone.length != 11) {
                showToast("请输入正确的手机号")
                return
            }
        }
        val code = et_code.text.toString().trim()
        if (code.isEmpty()) {
            showToast("请输入验证码")
            return
        }
        dialog()
        Request.resetPwdFirstStep(phone,code).request(this){_,_->
            startActivityForResult<ResetPasswordActivity>(0,"phone" to phone)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            finish()
        }
    }
}
