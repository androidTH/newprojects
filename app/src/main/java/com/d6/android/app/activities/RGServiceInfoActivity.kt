package com.d6.android.app.activities

import android.os.Bundle
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.MyDate
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.share.utils.ShareUtils
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import kotlinx.android.synthetic.main.activity_rgserviceinfo.*
import org.jetbrains.anko.startActivity

/**
 * 人工服务说明页
 */
class RGServiceInfoActivity : BaseActivity() {

    private val mData by lazy {
        intent.getSerializableExtra("data") as MyDate
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rgserviceinfo)
        immersionBar.fitsSystemWindows(true).statusBarColor(R.color.color_black).statusBarDarkFont(false).init()

        tv_rgservice_back.setOnClickListener {
            finish()
        }

        tv_rgservice_invite.setOnClickListener {
            mData?.let {
                var userClassId =  SPUtils.instance().getString(Const.User.USER_CLASS_ID)
                if(userClassId.isNotEmpty()){
                    var iLookClass = mData.iLookClass!!.toInt()
                    if(iLookClass==31){
                        isAuthUser("mine") {
                            startActivity<MemberActivity>()
                        }
                    }else if(iLookClass<= userClassId!!.toInt()){
                        if (mData.iType == 1) {
                            ShareUtils.share(this@RGServiceInfoActivity, SHARE_MEDIA.WEIXIN, it.lookfriendstand
                                    ?: "", it.looknumber
                                    ?: "", "http://www.d6-zone.com/JyD6/#/miyuexiangqing?ids=" + it.id, shareListener)
                        } else if (mData.iType == 2) {
                            ShareUtils.share(this@RGServiceInfoActivity, SHARE_MEDIA.WEIXIN, it.speedcontent
                                    ?: "", it.speednumber
                                    ?: "", "http://www.d6-zone.com/JyD6/#/suyuexiangqing?ids=" + it.id, shareListener)
                        }
//                        chatService(this)
                    } else{
                        isAuthUser("mine") {
                            chatService(this)
                        }
                    }
                }
            }
        }

        getUserInfo()
    }

    private fun getUserInfo() {
        var ClassId = SPUtils.instance().getString(Const.User.USER_CLASS_ID)
        var userClassId = 7
        if(ClassId.isNotEmpty()){
            userClassId = ClassId.toInt()
        }
        if(mData.iLookClass!=null){
            var iLookClass = mData.iLookClass!!.toInt()
            if (iLookClass == 31) {
                if(userClassId!=7){
                    sdv_vipservice.setImageURI("res:///"+R.mipmap.vip_serves_one)
                    tv_rgservice_invite.text = "联系客服"
                }else{
                    sdv_vipservice.setImageURI("res:///"+R.mipmap.vip_serves_two)
                    tv_rgservice_invite.text = "开通会员"
                }
            } else if (iLookClass <= userClassId) {
                if(userClassId==31){
                    sdv_vipservice.setImageURI("res:///"+R.mipmap.vip_serves_two)
                    tv_rgservice_tips.text = "对方要求${mData.sLookUserClass}才能速配"
                    tv_rgservice_invite.text = "开通会员"
                }else{
                    sdv_vipservice.setImageURI("res:///"+R.mipmap.vip_serves_one)
                    tv_rgservice_tips.text = "想要约她可将卡片分享给客服"
                    tv_rgservice_invite.text = "联系客服"
                }
            } else {
                sdv_vipservice.setImageURI("res:///"+R.mipmap.vip_serves_two)
                tv_rgservice_tips.text = "对方要求${mData.sLookUserClass}才能速配"
                tv_rgservice_invite.text = "开通会员"
            }
        }else{
            sdv_vipservice.setImageURI("res:///"+R.mipmap.vip_serves_one)
            tv_rgservice_tips.text = "想要约她可将卡片分享给客服"
            tv_rgservice_invite.text = "联系客服"
        }

//        Request.getUserInfo("", getLocalUserId()).request(this, success = { _, data ->
//            data?.let {
//                var userClassId = data.userclassesid?.toInt()
//                var iLookClass = mData.iLookClass!!.toInt()
//                if (iLookClass == 31) {
//                    if(userClassId!=7){
//                        sdv_vipservice.setImageURI("res:///"+R.mipmap.vip_serves_one)
//                        tv_rgservice_invite.text = "联系客服"
//                    }else{
//                        sdv_vipservice.setImageURI("res:///"+R.mipmap.vip_serves_two)
//                        tv_rgservice_invite.text = "联系客服升级会员"
//                    }
//                } else if (iLookClass <= userClassId!!) {
//                    sdv_vipservice.setImageURI("res:///"+R.mipmap.vip_serves_one)
//                    tv_rgservice_tips.text = "想要约她可将卡片分享给客服"
//                    tv_rgservice_invite.text = "联系客服"
//                } else {
//                    sdv_vipservice.setImageURI("res:///"+R.mipmap.vip_serves_two)
//                    tv_rgservice_tips.text = "对方${mData.sLookUserClass}会员才能速配"
//                    tv_rgservice_invite.text = "联系客服升级会员"
//                }
//                saveUserInfo(data)
//            }
//        })
    }

    private val shareListener by lazy {
        object : UMShareListener {
            override fun onResult(p0: SHARE_MEDIA?) {

            }

            override fun onCancel(p0: SHARE_MEDIA?) {
            }

            override fun onError(p0: SHARE_MEDIA?, p1: Throwable?) {
            }

            override fun onStart(p0: SHARE_MEDIA?) {
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
