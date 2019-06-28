package com.d6.android.app.activities

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.SplashHolder
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.BaseFragment
import com.d6.android.app.extentions.request
import com.d6.android.app.models.MemberDesc
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.OPENSTALL_CHANNEL
import com.d6.android.app.widget.convenientbanner.holder.CBViewHolderCreator
import com.d6.android.app.widget.convenientbanner.listener.OnPageChangeListener
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.umeng.socialize.UMAuthListener
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.bean.SHARE_MEDIA
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.fragment_splash.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.toast
import org.json.JSONObject


class SplashActivity : BaseActivity() {

    private val channel by lazy{
        SPUtils.instance().getString(OPENSTALL_CHANNEL,"Openinstall")
    }

    var mMemberDesc = ArrayList<MemberDesc>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        immersionBar
                .statusBarColor(R.color.trans_parent).init()

        btn_phonelogin.setOnClickListener {
//            SPUtils.instance().put(Const.User.IS_FIRST, false).apply()
//            val isLogin = SPUtils.instance().getBoolean(Const.User.IS_LOGIN)
//            if (!isLogin) {
//                startActivity<SignInActivity>()
//            }
            startActivity<SignInActivity>()
        }

        btn_wxlogin.setOnClickListener {
            if (wxApi.isWXAppInstalled) {
                SPUtils.instance().put(Const.User.IS_FIRST, false).apply()
                val isLogin = SPUtils.instance().getBoolean(Const.User.IS_LOGIN)
                if (!isLogin) {
                    weChatLogin()
                }
            } else {
                toast("请先安装微信")
            }
        }

        mMemberDesc.add(MemberDesc("人工精准匹配约会，超高成功率","专属客服匹配","res:///"+R.mipmap.tezheng1_big,
                R.drawable.shape_tz1))
        mMemberDesc.add(MemberDesc("直接开聊拒绝骚扰","一对一私聊",
                "res:///"+R.mipmap.tezheng2_big,R.drawable.shape_tz2))
        mMemberDesc.add(MemberDesc("多金？有颜？总有一个是你的菜","一对一私聊",
                "res:///"+R.mipmap.tezheng3_big,R.drawable.shape_tz3))
        mMemberDesc.add(MemberDesc("提供交友、线上群聊、线下聚会、酒店旅行折扣","私人定制服务",
                "res:///"+R.mipmap.tezheng4_big,R.drawable.shape_tz4))

        splasy_banner.setPages(
                object : CBViewHolderCreator {
                    override fun createHolder(itemView: View): SplashHolder {
                        return SplashHolder(itemView)
                    }

                    override fun getLayoutId(): Int {
                        return R.layout.fragment_splash
                    }
                },mMemberDesc)

        splasy_banner.setOnPageChangeListener(object: OnPageChangeListener {
            override fun onPageSelected(index: Int) {
                when(index){
                    0-> {
                        tv_numone.isEnabled = false
                        tv_numtwo.isEnabled = true
                        tv_numthree.isEnabled = true
                        tv_numfour.isEnabled = true
                    }
                    1->{
                        tv_numone.isEnabled = true
                        tv_numtwo.isEnabled = false
                        tv_numthree.isEnabled = true
                        tv_numfour.isEnabled = true
                    }
                    2->{
                        tv_numone.isEnabled = true
                        tv_numtwo.isEnabled = true
                        tv_numthree.isEnabled = false
                        tv_numfour.isEnabled = true
                    }
                    3->{
                        tv_numone.isEnabled = true
                        tv_numtwo.isEnabled = true
                        tv_numthree.isEnabled = true
                        tv_numfour.isEnabled = false
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            }
        })

//        mViewPager.offscreenPageLimit = 4
//        mViewPager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
//            override fun getItem(position: Int): Fragment {
//                return SplashFragment.instance(position)
//            }
//
//            override fun getCount() = 4
//        }
//
//        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
//            override fun onPageScrollStateChanged(state: Int) {
//
//            }
//
//            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//            }
//
//            override fun onPageSelected(position: Int) {
//                if (position == 0) {
//                    tv_numone.isEnabled = false
//                    tv_numtwo.isEnabled = true
//                    tv_numthree.isEnabled = true
//                    tv_numfour.isEnabled = true
//                } else if (position == 1) {
//                    tv_numone.isEnabled = true
//                    tv_numtwo.isEnabled = false
//                    tv_numthree.isEnabled = true
//                    tv_numfour.isEnabled = true
//                } else if(position==2){
//                    tv_numone.isEnabled = true
//                    tv_numtwo.isEnabled = true
//                    tv_numthree.isEnabled = false
//                    tv_numfour.isEnabled = true
//                }else{
//                    tv_numone.isEnabled = true
//                    tv_numtwo.isEnabled = true
//                    tv_numthree.isEnabled = true
//                    tv_numfour.isEnabled = false
//                }
//            }
//        })

        tv_protocols.movementMethod = LinkMovementMethod.getInstance()
        val s = "点击登录/注册即表示同意 用户协议"
        tv_protocols.text = SpanBuilder(s)
                .click(s.length - 5, s.length, MClickSpan(this))
                .build()

        clearLoginToken()
    }

    override fun onResume() {
        super.onResume()
        splasy_banner.startTurning()
    }

    override fun onStop() {
        super.onStop()
        splasy_banner.stopTurning()
    }

    class SplashFragment : BaseFragment() {
        val isLogin = SPUtils.instance().getBoolean(Const.User.IS_LOGIN)

        companion object {
            fun instance(p: Int): Fragment {
                val fragment = SplashFragment()
                fragment.arguments = bundleOf("p" to p)
                return fragment
            }
        }

        override fun contentViewId() = R.layout.fragment_splash

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            immersionBar
                    .fitsSystemWindows(false)
                    .statusBarColor(R.color.trans_parent).init()
            val p = arguments.getInt("p")
            val ids = when (p) {
                0 -> R.mipmap.tezheng1_big //R.mipmap.page_01
                1 -> R.mipmap.tezheng2_big //R.mipmap.page_02
                2 -> R.mipmap.tezheng3_big //R.mipmap.page_02
                else -> R.mipmap.tezheng4_big // R.mipmap.page_03
            }
//            val builder = GenericDraweeHierarchyBuilder(resources)
//            val hierarchy = builder
//                    .setPlaceholderImage(ids)
//                    .build()
//            imageView.hierarchy = hierarchy
//            imageView.setImageURI("res:///${ids}")
            if (p == 2) {
                splash_title.text ="优质会员任你挑"
                splash_smalltitle.text = "多金？有颜？总有一个是你的菜"
                next.visible()
            } else if (p == 1) {
                splash_title.text ="一对一私聊"
                splash_smalltitle.text = "直接开聊拒绝骚扰"
            } else if(p == 1){
                splash_title.text ="私人定制服务"
                splash_smalltitle.text = "提供交友、线上群聊、线下聚会、酒店旅行折扣"
            }else {
                splash_title.text ="专属客服匹配"
                splash_smalltitle.text = "人工精准匹配约会，超高成功率"
                next.gone()
            }

            next.setOnClickListener {
                SPUtils.instance().put(Const.User.IS_FIRST, false).apply()
                val isLogin = SPUtils.instance().getBoolean(Const.User.IS_LOGIN)
                if (isLogin) {
                    startActivity<MainActivity>()
                } else {
                    startActivity<SignInActivity>()
                }
                activity.finish()
            }
        }

        override fun onFirstVisibleToUser() {
        }
    }

    private val wxApi by lazy {
        WXAPIFactory.createWXAPI(this, "wx43d13a711f68131c")
    }

    private val shareApi by lazy {
        UMShareAPI.get(this)
    }

    private fun weChatLogin() {
        shareApi.getPlatformInfo(this, SHARE_MEDIA.WEIXIN, object : UMAuthListener {
            override fun onComplete(p0: SHARE_MEDIA?, p1: Int, data: MutableMap<String, String>?) {
                if (data != null) {
                    val openId = if (data.containsKey("openid")) data["openid"] else ""
                    val unionId = if (data.containsKey("unionid")) data["unionid"] else ""
                    val name = if (data.containsKey("name")) data["name"] else ""
                    val gender = if (data.containsKey("gender")) data["gender"] else "" //"access_token" -> "15_DqQo8GAloYTRPrkvE9Mn1TLJx06t2t8jcTnlVjTtWtCtB10KlEQJ-pksniTDmRlN1qO8OMgEH-6WaTEPbeCYXLegAsvy6iolB3FHfefn4Js"
                    val iconUrl = if (data.containsKey("iconurl")) data["iconurl"] else "" //"refreshToken" -> "15_MGQzdG8xEsuOJP-LvI80gZsR0OLgpcKlTbWjiQXJfAQJEUufz4OxdqmTh6iZnnNZSgOgHskEv-N8FexuWMsqenRdRtSycKVNGKkgfiVNJGs"
                    sysErr("------->$gender--->$openId--->$name--->$unionId")
//                    startActivity<BindPhoneActivity>()
                    thirdLogin(openId ?: "",unionId ?:"", name ?: "", iconUrl ?: "", gender ?: "", iconUrl ?: "")
                } else {
                    toast("拉取微信信息异常！")
                }
            }

            override fun onCancel(p0: SHARE_MEDIA?, p1: Int) {
                toast("取消登录")
                dismissDialog()
            }

            override fun onError(p0: SHARE_MEDIA?, p1: Int, p2: Throwable?) {
                p2?.printStackTrace()
                toast("微信登录异常！")
                dismissDialog()
            }

            override fun onStart(p0: SHARE_MEDIA?) {
                dialog()
            }

        })
    }

    private fun thirdLogin(openId: String,unionid: String, name: String, url: String, gender: String, iconurl: String) {
        Request.loginV2New(0, openId = openId,sUnionid=unionid,sChannelId = channel).request(this, false, success = { msg, data ->
            data?.let {
                if (it.accountId.isNullOrEmpty()) {
                    startActivityForResult<BindPhoneActivity>(2, "openId" to openId,"unionId" to unionid, "name" to name, "gender" to gender, "headerpic" to iconurl)
                } else {
                    msg?.let {
                        try {
                            val json = JSONObject(it)
                            val token = json.optString("token")
                            SPUtils.instance().put(Const.User.RONG_TOKEN, token)
                                    .apply()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    saveUserInfo(it)
                    val info = UserInfo(it.accountId, it.name, Uri.parse("" + data.picUrl))
                    RongIM.getInstance().refreshUserInfoCache(info)
                    if (it.name == null || it.name!!.isEmpty()) {//如果没有昵称
                        startActivity<SetUserInfoActivity>("name" to name, "gender" to gender, "headerpic" to iconurl,"openid" to openId,"unionid" to unionid)
                    } else {
                        SPUtils.instance().put(Const.User.IS_LOGIN, true).apply()
                        startActivity<MainActivity>()
                    }

                    finish()
                }
            }
        }) { code, data ->
            if(TextUtils.equals("0","${code}")){
                startActivityForResult<BindPhoneActivity>(2, "openId" to openId, "name" to name, "gender" to gender, "headerpic" to iconurl)
            }else{
                toast(data)
            }
        }
    }

    private class MClickSpan(val context: Context) : ClickableSpan() {
        override fun onClick(p0: View?) {
            context.startActivity<WebViewActivity>("title" to "用户协议", "url" to "file:///android_asset/yonghuxieyi.html")
        }

        override fun updateDrawState(ds: TextPaint?) {
            ds?.color = ContextCompat.getColor(context, R.color.color_F7AB00)
            ds?.isUnderlineText = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar.destroy()
    }
}
