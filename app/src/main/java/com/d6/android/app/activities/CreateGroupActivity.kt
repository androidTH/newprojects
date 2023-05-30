package com.d6.android.app.activities

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import com.d6.android.app.R
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.dialogs.SelectPhotosDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.NewGroupBean
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.xinstall.XInstall
import io.reactivex.Flowable
import kotlinx.android.synthetic.main.activity_creategroup.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import java.io.File
import java.util.*

class CreateGroupActivity : TitleActivity() {

    private var tempFile: File? = null
    private var headFilePath: String? = null
    private lateinit var mGroupBean: NewGroupBean
    fun IsNotNullGroupBean()=::mGroupBean.isInitialized

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creategroup)
        immersionBar.init()
        if(intent.hasExtra("bean")){
            setTitleBold("群名称")
            mGroupBean = intent.getParcelableExtra("bean")
            btn_creategroup.text = "保存"
            btn_creategroup.background = ContextCompat.getDrawable(this@CreateGroupActivity,R.drawable.shape_3r_orange_f7)
            if(IsNotNullGroupBean()){
                group_headView.setImageURI("${mGroupBean.sGroupPic}")
                et_groupname.setText("${mGroupBean.sGroupName}")
            }
        }else{
            setTitleBold("创建群聊")
        }

        Const.UPDATE_GROUPS_STATUS = -1

        et_groupname.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!TextUtils.isEmpty("${s}")){
                    btn_creategroup.background = ContextCompat.getDrawable(this@CreateGroupActivity,R.drawable.shape_3r_orange_f7)
                }else{
                    btn_creategroup.background = ContextCompat.getDrawable(this@CreateGroupActivity,R.drawable.shape_3r_80_orange)
                }
            }
        })

        group_headView.setOnClickListener {
            var mSelectPhotoDialog = SelectPhotosDialog()
            mSelectPhotoDialog.show(supportFragmentManager,"dsate")
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
                            val uri = this@CreateGroupActivity.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                            startActivityForResult(intent, 0)
                        }
                    }
                }else{
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)// 调用android的图库
                    intent.type = "image/*"
                    startActivityForResult(intent, 3)
                }
            }
        }

        btn_creategroup.setOnClickListener {
            var groupname = "${et_groupname.text.trim()}"
            if(!TextUtils.isEmpty(groupname)){
//                if(AndroidEmoji.isEmoji(groupname)){
//
//                }
                if(groupname.length>=2&&groupname.length<=10){
                    if(IsNotNullGroupBean()){
                        updateGroup()
                    }else{
                        if(!TextUtils.isEmpty(headFilePath)){
                            doCreateGroup()
                        }else{
                            toast("请上传群头像")
                        }
                    }
                }else{
                    toast("群名称要求2-10个字")
                }
            }
        }
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
                group_headView.setImageURI("file://$headFilePath")
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

    fun doCreateGroup(){
//        SealUserInfoManager.getInstance().addGroup(Groups(mGroupId, mGroupName, imageUrl, 0.toString()))
//        BroadcastManager.getInstance(mContext).sendBroadcast(REFRESH_GROUP_UI)
//        toast("创建群成功")
//        RongIM.getInstance().startConversation(this, Conversation.ConversationType.GROUP, mGroupId, "${et_groupname.text.trim()}")
//        finish()
        dialog("加载中...",true,false)
//        Request.uploadFile(File(headFilePath)).flatMap {
//            Request.createRongCloudGroup("${et_groupname.text.trim()}","${it}","")
//        }.request(this,success={ _, data ->
//            dismissDialog()
//            data?.let {
//                toast("创建群成功")
//                startActivity<GroupSettingActivity>("bean" to it)
//                finish()
//            }
//        }){code,msg->
//            toast(msg)
//        }

        Flowable.just(headFilePath).flatMap {
            val file = BitmapUtils.compressImageFile("${it}")
            Request.uploadFile(file)
        }.flatMap {
            Request.createRongCloudGroup("${et_groupname.text.trim()}","${it}","")
        }.request(this,success={ _, data ->
            dismissDialog()
            data?.let {
                toast("创建群成功")
                startActivity<GroupSettingActivity>("bean" to it)
                finish()
            }
        }){msg,code->
            dismissDialog()
            toast(msg)
        }


    }

    fun updateGroup(){
//        SealUserInfoManager.getInstance().addGroup(Groups(mGroupId, mGroupName, imageUrl, 0.toString()))
//        BroadcastManager.getInstance(mContext).sendBroadcast(REFRESH_GROUP_UI)
//        toast("创建群成功")
//        RongIM.getInstance().startConversation(this, Conversation.ConversationType.GROUP, mGroupId, "${et_groupname.text.trim()}")
//        finish()
        if(headFilePath==null){
            dialog()
            Request.updateGroup("${mGroupBean.sId}","${et_groupname.text.trim()}","${mGroupBean.sGroupPic}","").request(this) { _, data ->
                toast("修改成功")
                finish()
            }

        }else{
            dialog()
//            Request.uploadFile(File(headFilePath)).flatMap {
//                Request.updateGroup("${mGroupBean.sId}","${et_groupname.text.trim()}","${it}","")
//            }.request(this) { _, data ->
//                toast("修改成功")
//                finish()
//            }

            Flowable.just(headFilePath).flatMap {
                val file = BitmapUtils.compressImageFile("${it}")
                Request.uploadFile(file)
            }.flatMap {
                Request.updateGroup("${mGroupBean.sId}","${et_groupname.text.trim()}","${it}","")
            }.request(this,success={ _, data ->
                toast("修改成功")
                finish()
            }){msg,code->
                toast(msg)
            }
        }
    }
}
