package com.d6zone.android.app.activities

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import com.d6zone.android.app.R
import com.d6zone.android.app.base.TitleActivity
import com.d6zone.android.app.extentions.request
import com.d6zone.android.app.net.Request
import com.d6zone.android.app.utils.md5
import kotlinx.android.synthetic.main.activity_reset_password.*
import org.jetbrains.anko.toast

/**
 * 重置密码
 */
class ResetPasswordActivity : TitleActivity() {
    private val phone by lazy {
        intent.getStringExtra("phone")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        title = "重置密码"

        btn_sure.setOnClickListener {
            resetPassword()
        }
    }

    private fun resetPassword() {
        val pwd = et_password.text.toString().trim()
        if (pwd.isEmpty()) {
            toast("密码不能为空")
            return
        }
        if (pwd.length < 6) {
            showToast("请输入6-16位密码")
            return
        }
//        if (!pwd.isValidPwd()) {
//            showToast("密码不能为纯数字或字母")
//            return
//        }

        val cPwd = et_confirm_password.text.toString().trim()
        if (cPwd.isEmpty()) {
            toast("再次输入密码不能为空")
            return
        }

        if (!TextUtils.equals(cPwd, pwd)) {
            toast("两次密码不一致")
            return
        }
        dialog()

        Request.resetPwd(phone,pwd.md5()).request(this){_,_->
            showToast("密码重置成功")
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}