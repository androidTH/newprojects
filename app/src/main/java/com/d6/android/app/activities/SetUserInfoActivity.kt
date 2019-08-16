package com.d6.android.app.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.RadioButton
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.UserData
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.OPENSTALL_CHANNEL
import com.d6.android.app.widget.MaxEditTextWatcher
import com.fm.openinstall.OpenInstall
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

//    private val openchannel by lazy{
//        SPUtils.instance().getString(OPENSTALL_CHANNEL,"channel")
//    }

    private var openId = ""
    private var unionId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_user_info)
//        immersionBar.statusBarColor(R.color.trans_parent)
//                .init()
//        AndroidBug5497Workaround.assistActivity(this)

        val name = if (intent.hasExtra("name")) {
            intent.getStringExtra("name")
        } else {
            ""
        }
        et_nick.setText(name)

        openId = if(intent.hasExtra("openId")){
            intent.getStringExtra("openId")
        }else{
            ""
        }

        unionId = if(intent.hasExtra("unionid")){
            intent.getStringExtra("unionid")
        }else{
            ""
        }

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

        et_nick.addTextChangedListener(object: MaxEditTextWatcher(CHINESE_TWO,16,this,et_nick){
            override fun onTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {
                super.onTextChanged(charSequence, i, i1, i2)
            }

            override fun afterTextChanged(editable: Editable?) {
                super.afterTextChanged(editable)
                if (editable.isNullOrEmpty()) {
                    tv_error.text = "昵称不能为空"
                    nickLine.setBackgroundResource(R.color.red_fc3)
                } else {
                    tv_error.text = ""
                    nickLine.setBackgroundResource(R.color.orange_f6a)
                }
            }

        })
        tv_change_head.gone()
        val s = "注册成功后性别将不可以修改"
        tv_info.text = SpanBuilder(s)
                .color(this,s.length-5,s.length-2,R.color.orange_f6a)
                .build()

        OpenInstall.reportRegister()
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
            tv_error.text = "请输入昵称"
            nickLine.setBackgroundResource(R.color.red_fc3)
            return
        }

//        if(checkLimitEx(nick)){
//            tv_error.text = "昵称中不能包含特殊符号或表情"
//            return
//        }

        val code = et_code.text.toString().trim()
        val accountId = SPUtils.instance().getString(Const.User.USER_ID)
        val user = UserData(accountId)
        user.sex = sex.toString()
        user.name = nick
        user.invitecode = code
        user.wxid = openId
        user.sUnionid = unionId
        dialog()
        if(ISNOTEDIT){
            Request.uploadFile(File(headFilePath)).flatMap {
                user.picUrl = it
                Request.updateUserInfo(user)
            }.request(this) { _, data ->
                clearLoginToken()
                SPUtils.instance()
                        .put(Const.User.IS_LOGIN,true)
                        .put(Const.User.USER_NICK, nick)
                        .put(Const.User.USER_HEAD, user.picUrl)
                        .put(Const.User.USER_SEX, user.sex)
                        .put(Const.User.SLOGINTOKEN,data?.sLoginToken)
                        .apply()
                OpenInstall.reportEffectPoint("perfect_profile",1)//会员转化
                startActivity<MainActivity>()
                dismissDialog()
                setResult(Activity.RESULT_OK)
                finish()
            }
        }else{
            user.picUrl = headFilePath
            Request.updateUserInfo(user).request(this, success = {msg,data->
                clearLoginToken()
                SPUtils.instance()
                        .put(Const.User.IS_LOGIN,true)
                        .put(Const.User.USER_NICK, nick)
                        .put(Const.User.USER_HEAD, user.picUrl)
                        .put(Const.User.USER_SEX, user.sex)
                        .put(Const.User.SLOGINTOKEN,data?.sLoginToken)
                        .apply()
                OpenInstall.reportEffectPoint("perfect_profile",1)//会员转化
                startActivity<MainActivity>()
                dismissDialog()
                setResult(Activity.RESULT_OK)
                finish()
            })
        }
    }
}
