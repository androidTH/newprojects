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
import com.d6zone.android.app.utils.md5
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.jetbrains.anko.startActivityForResult

/**
 * 注册
 */
class SignUpActivity : TitleActivity() {
    private var type = 1
    private var sex = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        title = "注册"

        tv_get_code.setOnClickListener {
            getCode()
        }

        btn_submit.setOnClickListener {
            register()
        }
        tv_type.setOnClickListener {
            val selectTypeDialog = SelectCountryTypeDialog()
            selectTypeDialog.show(supportFragmentManager,"")
            selectTypeDialog.setDialogListener { p, s ->
                type = p
                tv_type.text = s
                if (type == 0) {//国外
                    et_phone.hint="国家区号-手机号,如美国1-7321234871"
                    et_phone.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(12))
                } else {
                    et_phone.hint="输入手机号"
                    et_phone.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(11))
                }
            }
        }
        tv_choose_sex.setOnClickListener {
            startActivityForResult<ChooseSexActivity>(3)
        }
//        rb_male.setOnCheckedChangeListener{_,isChecked->
//            if (isChecked) {
//                sex = 1
//            }
//        }
//        rb_female.setOnCheckedChangeListener{_,isChecked->
//            if (isChecked) {
//                sex = 0
//            }
//        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 3 && data!=null) {
                sex = data.getIntExtra("sex",1)
                tv_choose_sex.text = if (sex == 1) {
                    "我是男生"
                } else {
                    "我是女生"
                }
            }
        }
    }

    private fun register() {
        val phone = et_phone.text.toString().trim()
        if (type == 0) {
            if (!phone.contains("-")) {
                showToast("请输入国家区号-手机号格式")
                return
            }
        } else {
            if (phone.length != 11) {
                showToast("请输入正确的手机号")
                return
            }
        }

        val password = et_password.text.toString().trim()
        if (password.length<6){
            showToast("请输入6-16位密码")
            return
        }
//        if (!password.isValidPwd()) {
//            showToast("密码不能为纯数字或字母")
//            return
//        }
        val code = et_code.text.toString().trim()
        if (code.isEmpty()) {
            showToast("请输入验证码")
            return
        }
        if (sex == -1) {
            showToast("请选择性别")
            return
        }

        dialog()
        Request.register(phone,password.md5(),code,type.toString(),sex).request(this){msg,data->
            showToast("注册成功！")
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun getCode() {
        val phone = et_phone.text.toString().trim()
        if (type == 0) {
            if (!phone.contains("-")) {
                showToast("请输入国家区号-手机号格式")
                return
            }
        } else {
            if (phone.length != 11) {
                showToast("请输入正确的手机号")
                return
            }
        }
        dialog()
        Request.sendSMSCode(phone,0,type).request(this){msg,data->
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
}
