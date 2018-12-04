package com.d6.android.app.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.RadioButton
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.UserData
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import kotlinx.android.synthetic.main.activity_set_user_info.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import java.io.File

/**
 * 完善资料
 */
class SetUserInfoActivity : BaseActivity() {
    private var headFilePath: String? = null
    private var sex = -1
    private var ISNOTEDIT = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_user_info)

        val name = if (intent.hasExtra("name")) {
            intent.getStringExtra("name")
        } else {
            ""
        }
        et_nick.setText(name)

        sex = if (intent.hasExtra("gender")) {
            val s = intent.getStringExtra("gender")
            when (s) {
                "男" ->{
                    rb_male.isChecked = true
                    rb_female.isChecked = false
                    1
                }
                "女" -> {
                    rb_male.isChecked = false
                    rb_female.isChecked = true
                    0
                }
                else -> -1
            }
        } else {
            -1
        }

        headFilePath = if (intent.hasExtra("headerpic")) {
            intent.getStringExtra("headerpic")
        } else {
            ""
        }

        if(!TextUtils.isEmpty(headFilePath)){
            tv_head_tip.visibility = View.GONE
            headView.setImageURI(headFilePath)
        }else{
            ISNOTEDIT = true
            tv_head_tip.visibility = View.VISIBLE
        }


        headView.setOnClickListener {
            startActivityForResult<SelectPhotoDialog>(0)
        }

        rg.setOnCheckedChangeListener { radioGroup, i ->
            val view = radioGroup.findViewById<RadioButton>(i)
            sex = view.tag.toString().toInt()
            tv_error.text = ""
        }

        btn_sign_in.setOnClickListener {
            update()
        }

        et_nick.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                if (p0.isNullOrEmpty()) {
                    tv_error.text = "昵称不能为空"
                    nickLine.setBackgroundResource(R.color.red_fc3)
                } else {
                    tv_error.text = ""
                    nickLine.setBackgroundResource(R.color.orange_f6a)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })
        tv_change_head.gone()
        val s = "注册成功后性别将不可以修改"
        tv_info.text = SpanBuilder(s)
                .color(this,s.length-5,s.length-2,R.color.orange_f6a)
                .build()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0 && data != null) {
                val path = data.getStringExtra(SelectPhotoDialog.PATH)
                startActivityForResult<CropImageActivity>(1, "scale" to 1f, "uri" to path)
            } else if (requestCode == 1) {
                headFilePath = data?.getStringExtra("path")
                headView.setImageURI("file://$headFilePath")
                tv_change_head.visible()
                ISNOTEDIT = true
                tv_head_tip.gone()
                tv_error.text = ""
            }
        }
    }

    private fun update() {
        tv_error.visible()
        tv_error.text = ""
        nickLine.setBackgroundResource(R.color.orange_f6a)
        if(ISNOTEDIT){
            if (headFilePath.isNullOrEmpty()) {
                tv_error.text = "请上传头像"
                return
            }
        }

        if (sex == -1) {
            tv_error.text = "请选择性别"
            return
        }
        val nick = et_nick.text.toString().trim()
        if (nick.isEmpty()) {
            tv_error.text = "昵称不能为空"
            nickLine.setBackgroundResource(R.color.red_fc3)
            return
        }
        val code = et_code.text.toString().trim()
        val accountId = SPUtils.instance().getString(Const.User.USER_ID)
        val user = UserData(accountId)
        user.sex = sex.toString()
        user.name = nick
        user.invitecode = code
        dialog()
        if(ISNOTEDIT){
            Request.uploadFile(File(headFilePath)).flatMap {
                user.picUrl = it
                Request.updateUserInfo(user)
            }.request(this) { _, data ->
                SPUtils.instance()
                        .put(Const.User.IS_LOGIN,true)
                        .put(Const.User.USER_NICK, nick)
                        .put(Const.User.USER_HEAD, user.picUrl)
                        .put(Const.User.USER_SEX, user.sex)
                        .apply()
                startActivity<MainActivity>()
                dismissDialog()
                setResult(Activity.RESULT_OK)
                finish()
            }
        }else{
            user.picUrl = headFilePath
            Request.updateUserInfo(user).request(this, success = {msg,data->
                SPUtils.instance()
                        .put(Const.User.IS_LOGIN,true)
                        .put(Const.User.USER_NICK, nick)
                        .put(Const.User.USER_HEAD, user.picUrl)
                        .put(Const.User.USER_SEX, user.sex)
                        .apply()
                startActivity<MainActivity>()
                dismissDialog()
                setResult(Activity.RESULT_OK)
                finish()
            })
        }
    }
}
