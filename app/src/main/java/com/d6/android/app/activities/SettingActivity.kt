package com.d6.android.app.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.d6.android.app.R
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.dialogs.DialogUpdateApp
import com.d6.android.app.extentions.request
import com.d6.android.app.models.AddImage
import com.d6.android.app.models.UserData
import com.d6.android.app.net.Request
import com.d6.android.app.net.http.UpdateAppHttpUtil
import com.d6.android.app.utils.*
import com.d6.android.app.widget.CustomToast
import com.umeng.message.PushAgent
import com.umeng.message.UTrack
import com.vector.update_app.UpdateAppBean
import com.vector.update_app.UpdateAppManager
import com.vector.update_app.UpdateCallback
import com.vector.update_app.utils.AppUpdateUtils
import io.rong.imkit.RongIM
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import io.rong.imlib.model.UserInfo
import io.rong.message.TextMessage
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.header_mine_layout.view.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class SettingActivity : TitleActivity() {
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private var mData: UserData?=null
    private val mImages = ArrayList<AddImage>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        immersionBar.init()
        title = "设置"

        val s = "微信查询  防止假冒客服与您联系！"
        tv_search_weChat.text = SpanBuilder(s)
                .color(this,4,s.length,R.color.textColor99)
                .build()

        tv_contact_us.setOnClickListener {
            startActivity<ContactUsActivity>()
//            diyUpdate()//版本更新
        }

        rl_my_info.setOnClickListener {
            mData?.let {
                mImages.clear()
                if (!it.userpics.isNullOrEmpty()) {
                    val images = it.userpics!!.split(",")
                    images.forEach {
                        mImages.add(AddImage(it))
                    }
                }
                mImages.add(AddImage("res:///" + R.mipmap.ic_add_bg, 1))
                startActivityForResult<MyInfoActivity>(0, "data" to it,"images" to mImages)
            }
        }

        tv_online_service.setOnClickListener {
            //            val serviceId = "f67f360c9dde4b3c9eab01a0126f6684"
//            val textMsg = TextMessage.obtain("欢迎使用D6社区APP\nD6社区官网：www-d6-zone.com\n微信公众号：D6社区CM\n可关注实时了解社区动向。")
//            RongIMClient.getInstance().insertIncomingMessage(Conversation.ConversationType.PRIVATE
//                    ,"5" ,"5", Message.ReceivedStatus(0)
//                    , textMsg,object : RongIMClient.ResultCallback<Message>(){
//                override fun onSuccess(p0: Message?) {
//
//                }
//                override fun onError(p0: RongIMClient.ErrorCode?) {
//
//                }
//            })

            val serviceId = "5"
            RongIM.getInstance().startConversation(this, Conversation.ConversationType.PRIVATE, serviceId, "D6客服")
        }

        tv_feedback.setOnClickListener {
            startActivity<FeedBackActivity>()
        }

        tv_aboutUs.setOnClickListener {
            startActivity<AboutUsMainActivity>()
        }

        tv_search_weChat.setOnClickListener {
            startActivity<WeChatSearchActivity>()
        }

        tv_private_chat_type.setOnClickListener {

        }

        btn_sign_out.setOnClickListener {
            SPUtils.instance().remove(Const.User.USER_ID)
                    .remove(Const.User.IS_LOGIN)
                    .remove(Const.User.RONG_TOKEN)
                    .remove(Const.User.USER_TOKEN)
                    .apply()
            SPUtils.instance().remove(Const.USERINFO)
            PushAgent.getInstance(applicationContext).deleteAlias(userId, "D6", { _, _ ->

            })
            RongIM.getInstance().disconnect()
            startActivity<SignInActivity>()
            finish()
        }

        dialog()
        getUserInfo()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            getUserInfo()
        }
    }

    private fun getUserInfo() {
        Request.getUserInfo("",userId).request(this, success = { _, data ->
            this.mData = data
            mSwipeRefreshLayout.isRefreshing = false
            saveUserInfo(data)
            data?.let {
                val info = UserInfo(data.accountId, data.name, Uri.parse("" + data.picUrl))
                RongIM.getInstance().refreshUserInfoCache(info)
                tv_vip.text = String.format("%s", it.classesname)
//                if (TextUtils.equals("0", it.sex)) {//女性
//                    tv_vip.visible()
//                } else {
//                    tv_vip.visible()
//                }
                tv_sex.isSelected = TextUtils.equals("0",it.sex)
                it.age?.let {
                    if(it.toInt()<=0){
                        tv_sex.text =""
                    }else{
                        tv_sex.text = it
                    }
                }
                headView.setImageURI(it.picUrl)
                tv_nick.text = it.name
                tv_signature.text = it.intro

                if(TextUtils.equals("0",mData!!.screen) || mData!!.screen.isNullOrEmpty()){
                    img_auther.visibility = View.GONE
                }else{
                    img_auther.visibility = View.VISIBLE
                }
            }
        }) { _, _ ->
            mSwipeRefreshLayout.isRefreshing = false
        }
    }

    private var updateurl ="https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/json/json.txt"

    private fun diyUpdate() {
        val path = Environment.getExternalStorageDirectory().absolutePath

        val params = HashMap<String, String>()

        params["appKey"] = "ab55ce55Ac4bcP408cPb8c1Aaeac179c5f6f"
        params["appVersion"] = "0.1.0"//AppUpdateUtils.getVersionName(this)
        params["key1"] = "value2"
        params["key2"] = "value3"

        UpdateAppManager.Builder()
                //必须设置，当前Activity
                .setActivity(this)
                //必须设置，实现httpManager接口的对象
                .setHttpManager(UpdateAppHttpUtil())
                //必须设置，更新地址
                .setUpdateUrl(updateurl)
                //以下设置，都是可选
                //设置请求方式，默认get
                .setPost(false)
                //添加自定义参数，默认version=1.0.0（app的versionName）；apkKey=唯一表示（在AndroidManifest.xml配置）
                .setParams(params)
                //设置apk下砸路径，默认是在下载到sd卡下/Download/1.0.0/test.apk
                .setTargetPath(path)
                .build()
                //检测是否有新版本
                .checkNewApp(object : UpdateCallback() {
                    /**
                     * 解析json,自定义协议
                     *
                     * @param json 服务器返回的json
                     * @return UpdateAppBean
                     */
                    override fun parseJson(json: String): UpdateAppBean {
                        val updateAppBean = UpdateAppBean()
                        try {
                            val jsonObject = JSONObject(json)
                            updateAppBean
                                    //（必须）是否更新Yes,No
                                    .setUpdate(jsonObject.optString("update"))
                                    //（必须）新版本号，
                                    .setNewVersion(jsonObject.optString("new_version"))
                                    //（必须）下载地址jsonObject.optString("apk_file_url")
                                    .setApkFileUrl("http://test-1251233192.coscd.myqcloud.com/1_1.apk")//http://test-1251233192.coscd.myqcloud.com/1_1.apk
                                    //（必须）更新内容
                                    .setUpdateLog(jsonObject.optString("update_log"))
                                    //大小，不设置不显示大小，可以不设置
                                    .setTargetSize(jsonObject.optString("target_size"))
                                    //是否强制更新，可以不设置
                                    .setConstraint(false).newMd5 = jsonObject.optString("new_md5")
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                        return updateAppBean
                    }

                    /**
                     * 有新版本
                     *
                     * @param updateApp        新版本信息
                     * @param updateAppManager app更新管理器
                     */
                    public override fun hasNewApp(updateApp: UpdateAppBean, updateAppManager: UpdateAppManager) {
                        //自定义对话框
                        var mDialogUpdateApp =DialogUpdateApp()
                        mDialogUpdateApp.arguments = bundleOf("data" to updateApp)
                        mDialogUpdateApp.show(supportFragmentManager,"updateapp")
                        mDialogUpdateApp.setDialogListener { p, s ->
                            updateAppManager.download()
                        }
                    }

                    /**
                     * 网络请求之前
                     */
                    public override fun onBefore() {
                        dialog()
                    }

                    /**
                     * 网路请求之后
                     */
                    public override fun onAfter() {
                       dismissDialog()
                    }

                    /**
                     * 没有新版本
                     */
                    public override fun noNewApp(error: String?) {
                        showToast("没有新版本")
                    }
                })
    }
}
