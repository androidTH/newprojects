package com.d6.android.app.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.d6.android.app.R
import com.d6.android.app.application.D6Application
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import kotlinx.android.synthetic.main.activity_sign_choose.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult

/**
 * 登入登出选择
 */
class SignChooseActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_choose)

        btn_sign_in.setOnClickListener {
            startActivityForResult<SignInActivity>(0)
        }

        btn_sign_up.setOnClickListener {
            startActivityForResult<SignUpActivity>(1)
        }

        tv_user_agreement.setOnClickListener {
            startActivity<WebViewActivity>("title" to "用户协议","url" to "file:///android_asset/yonghuxieyi.html")
        }
        val isLogin = SPUtils.instance().getBoolean(Const.User.IS_LOGIN)
        if (isLogin) {
            startActivity<MainActivity>()
            finish()
            return
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0) {
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        D6Application.isChooseLoginPage = false
    }
}
