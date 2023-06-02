package com.d6zone.android.app.activities

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.widget.RadioButton
import com.d6zone.android.app.R
import com.d6zone.android.app.base.BaseActivity
import com.d6zone.android.app.dialogs.SelectPhotosDialog
import com.d6zone.android.app.extentions.request
import com.d6zone.android.app.models.UserData
import com.d6zone.android.app.net.Request
import com.d6zone.android.app.utils.*
import com.d6zone.android.app.widget.MaxEditTextWatcher
import com.fm.openinstall.OpenInstall
import com.xinstall.XInstall
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_set_user_info.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import java.io.File
import java.util.*


/**
 * 完善资料
 */
class SetUserInfoActivity : BaseActivity() {
    private var headFilePath: String? = null
    private var sex = -1
    private var ISNOTEDIT = false
    private var menHeaderUrl = "http://www.d6-zone.com/JyPhone/static/images/system/d6boy.png"
    private var womenHeaderUrl = "http://img-local.d6-zone.com/d6girl.png"

    private var tempFile: File? = null

//    private val openchannel by lazy{
//        SPUtils.instance().getString(OPENSTALL_CHANNEL,"channel")
//    }

    private var openId = ""
    private var unionId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_user_info)
        immersionBar.statusBarColor(R.color.trans_parent)
                .init()
//        AndroidBug5497Workaround.assistActivity(this)

        var name = if (intent.hasExtra("name")) {
            intent.getStringExtra("name")
        } else {
            ""
        }

        if(name.length>8){
            name = name.substring(0, 8)
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
                "男" -> {
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
//            startActivityForResult<SelectPhotoDialog>(0)

            var mSelectPhotoDialog = SelectPhotosDialog()
            mSelectPhotoDialog.show(supportFragmentManager, "dsate")
            mSelectPhotoDialog.setDialogListener { p, s ->
                if(p==0){
                    AppUtils.initFilePath()
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    val fileName = System.currentTimeMillis().toString() + ".jpg"
                    tempFile = File(AppUtils.PICDIR, fileName)
                    val u = Uri.fromFile(tempFile)
                    intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0)
                    //7.0崩溃问题
                    if (Build.VERSION.SDK_INT < 24) {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, u)
                        startActivityForResult(intent, 0)
                    } else {
                        var list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                        if(list.size>0){
                            val contentValues = ContentValues(1)
                            contentValues.put(MediaStore.Images.Media.DATA, tempFile?.absolutePath)
                            val uri = this@SetUserInfoActivity.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                            startActivityForResult(intent, 0)
                        }
                    }
                }else{
                    val getIntent = Intent(Intent.ACTION_GET_CONTENT,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    getIntent.type = "image/*"
                    startActivityForResult(getIntent, 3)

//                    val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)// 调用android的图库
//                    pickIntent.type = "image/*"
//                    val chooserIntent: Intent = Intent.createChooser(getIntent, "Select Image")
//
//                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf<Intent>(pickIntent,getIntent))
//                    startActivityForResult(chooserIntent, 3)
                }
            }
        }

        tv_change_head.setOnClickListener {
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

        et_nick.addTextChangedListener(object : MaxEditTextWatcher(CHINESE_TWO, 16, this, et_nick) {
            override fun onTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable?) {
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
                .color(this, s.length - 5, s.length - 2, R.color.orange_f6a)
                .build()

//        OpenInstall.reportRegister()
        XInstall.reportRegister()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0) {
                if (tempFile != null && tempFile!!.exists()) {
                    startActivityForResult<CropImageActivity>(1, "scale" to 1f, "uri" to tempFile!!.absolutePath)
                }
//                val path = data.getStringExtra(SelectPhotoDialog.PATH)
            } else if (requestCode == 1) {
                headFilePath = data?.getStringExtra("path")
                headView.setImageURI("file://$headFilePath")
                tv_change_head.visible()
                ISNOTEDIT = true
                tv_head_tip.gone()
                tv_error.text = ""
            }else if(requestCode==3){
                val uri = data!!.data
                if (uri != null) {
                    val path = getUrlPath(uri)
                    if (path != null) {
                        val typeIndex = path.lastIndexOf(".")
                        if (typeIndex != -1) {
                            val fileType = path.substring(typeIndex + 1).toLowerCase(Locale.CHINA)
                            //某些设备选择图片是可以选择一些非图片的文件。然后发送出去或出错。这里简单的通过匹配后缀名来判断是否是图片文件
                            //如果是图片文件则发送。反之给出提示
                            if (fileType == "jpg" || fileType == "gif"
                                    || fileType == "png" || fileType == "jpeg"
                                    || fileType == "bmp" || fileType == "wbmp"
                                    || fileType == "ico" || fileType == "jpe") {
                                startActivityForResult<CropImageActivity>(1, "scale" to 1f, "uri" to path)
                                //			                        	cropImage(path);
                                //			                        	BitmapUtil.getInstance(this).loadImage(iv_image, path);
                            } else {
                                toast("无法识别的图片类型！")
                            }
                        } else {
                            toast("无法识别的图片类型！")
                        }
                    } else {
                        toast("无法识别的图片类型或路径！")
                    }
                }
            }
        }
    }

    private fun update() {
        tv_error.visible()
        tv_error.text = ""
        nickLine.setBackgroundResource(R.color.orange_f6a)
//        if(ISNOTEDIT){
//            if (headFilePath.isNullOrEmpty()) {
//                tv_error.text = "请上传头像"
//                return
//            }
//        }

        if (sex == -1) {
            tv_error.text = "请选择性别"
            return
        }

        if(ISNOTEDIT){
            if (headFilePath.isNullOrEmpty()) {
                ISNOTEDIT = false
            }
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
                        .put(Const.User.IS_LOGIN, true)
                        .put(Const.User.USER_NICK, nick)
                        .put(Const.User.USER_HEAD, user.picUrl)
                        .put(Const.User.USER_SEX, user.sex)
                        .put(Const.User.SLOGINTOKEN, data?.sLoginToken)
                        .apply()
                OpenInstall.reportEffectPoint("perfect_profile", 1)//完善资料成功时上报
                startActivity<MainActivity>()
                dismissDialog()
                setResult(Activity.RESULT_OK)
                finish()
            }

//            Luban.with(this)
//                    .load(headFilePath)
//                    .ignoreBy(900)
//                    .filter(object : CompressionPredicate {
//                        override fun apply(path: String): Boolean {
//                            return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"))
//                        }
//                    }).setCompressListener(object : OnCompressListener {
//                        override fun onStart() {
//
//                        }
//
//                        override fun onSuccess(file: File) {
//                            if(file!=null){
//                                Request.uploadFile(file).flatMap {
//                                    user.picUrl = it
//                                    Request.updateUserInfo(user)
//                                }.request(this@SetUserInfoActivity) { _, data ->
//                                    clearLoginToken()
//                                    SPUtils.instance()
//                                            .put(Const.User.IS_LOGIN, true)
//                                            .put(Const.User.USER_NICK, nick)
//                                            .put(Const.User.USER_HEAD, user.picUrl)
//                                            .put(Const.User.USER_SEX, user.sex)
//                                            .put(Const.User.SLOGINTOKEN, data?.sLoginToken)
//                                            .apply()
//                                    XInstall.reportEvent("perfectprofile", 1)//完善资料成功时上报
//                                    startActivity<MainActivity>()
//                                    dismissDialog()
//                                    setResult(Activity.RESULT_OK)
//                                    finish()
//                                }
//                            }
//                        }
//
//                        override fun onError(e: Throwable?) {
//
//                        }
//                    }).launch()

        }else{
            if (headFilePath.isNullOrEmpty()) {
                if(sex==1){
                    headFilePath = menHeaderUrl
                }else{
                    headFilePath = womenHeaderUrl
                }
            }
            user.picUrl = headFilePath
            Request.updateUserInfo(user).request(this, success = { msg, data ->
                clearLoginToken()
                SPUtils.instance()
                        .put(Const.User.IS_LOGIN, true)
                        .put(Const.User.USER_NICK, nick)
                        .put(Const.User.USER_HEAD, user.picUrl)
                        .put(Const.User.USER_SEX, user.sex)
                        .put(Const.User.SLOGINTOKEN, data?.sLoginToken)
                        .apply()
                val info = UserInfo(accountId, "${user.name}", Uri.parse("${user.picUrl}"))
                RongIM.getInstance().refreshUserInfoCache(info)
                XInstall.reportEvent("perfectprofile", 1)//完善资料成功时上报
//                OpenInstall.reportEffectPoint("perfect_profile",1)//完善资料成功时上报
                startActivity<MainActivity>()
                dismissDialog()
                setResult(Activity.RESULT_OK)
                finish()
            })
        }
    }
}
