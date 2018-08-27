package com.d6.android.app.activities

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.FilterTrendDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.fragments.*
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.gyf.barlibrary.ImmersionBar
import com.umeng.message.PushAgent
import io.rong.imkit.RongIM
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_splash.*
import org.jetbrains.anko.*
import org.jetbrains.anko.collections.forEachWithIndex


/**
 * 主页
 */
class MainActivity : BaseActivity() {
    //    private val tabTexts = arrayOf("主页",  "广场","消息", "我的")
//    private val tabTexts = arrayOf("主页",  "速约","广场", "我的")
    private val tabTexts = arrayOf("约会", "推荐", "动态", "我的")

    private val tabImages = arrayOf(R.drawable.home_main_selector
            , R.drawable.home_speed_date_selector, R.drawable.home_square_selector
            , R.drawable.home_mine_selector)
    //    private val fragmentArray = arrayOf<Class<*>>(HomeFragment::class.java
//            , SquareMainFragment::class.java, MessagesFragment::class.java,
//            MineFragment::class.java)
    private val fragmentArray = arrayOf<Class<*>>(DateFragment::class.java
            , HomeFragment::class.java, SquareMainFragment::class.java,
            MineV2Fragment::class.java)

    private val broadcast by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                runOnUiThread {
                    getUnReadCount()
                }
            }
        }
    }

    private val immersionBar by lazy {
        ImmersionBar.with(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        immersionBar.init()
        registerReceiver(broadcast, IntentFilter(Const.NEW_MESSAGE))
        tabhost.setup(this, supportFragmentManager, R.id.container)
        tabhost.tabWidget.dividerDrawable = null
        tabTexts.forEachWithIndex { i, it ->
            val spec = tabhost.newTabSpec(it).setIndicator(addTab(i))
            tabhost.addTab(spec, fragmentArray[i], null)
        }
        //默认第一个标签
        tabhost.setCurrentTabByTag(tabTexts[0])
        tabhost.setOnTabChangedListener {
            titleBar.visible()
            line.visible()
            iv_right.text = ""
            when {
                TextUtils.equals(it, tabTexts[0]) -> {
//                    titleBar.backgroundColor = Color.TRANSPARENT
                    titleBar.gone()
                    iv_right.gone()
                    tv_title1.gone()
                    tv_title.text = "D6社区"

                    val fragment0 = supportFragmentManager.findFragmentByTag(tabTexts[0])
                    if (fragment0 != null && fragment0 is DateFragment) {
                        fragment0.onFirstVisibleToUser()
                    }
                }
                TextUtils.equals(it, tabTexts[1]) -> {
//                    iv_right.imageResource = R.mipmap.ic_add_orange
//                    tv_title.text = "广场"
                    iv_right.gone()
                    tv_title1.gone()
//                    iv_right.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.ic_filter,0)
                    tv_title.text = "推荐"
                }
                TextUtils.equals(it, tabTexts[2]) -> {
                    iv_right.visible()
                    tv_title1.visible()
                    iv_right.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
//                    iv_right.imageResource = R.mipmap.ic_msg_setting
                    tv_title.text = ""
                    iv_right.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_add_orange, 0)
//                    iv_right.text = "发布"
                    tv_title1.text = "动态"
                }
                TextUtils.equals(it, tabTexts[3]) -> {
                    iv_right.gone()
                    tv_title1.gone()
                    tv_title.text = "我的"
                    titleBar.gone()
                    line.gone()
                }
            }
        }

        tv_title1.setOnClickListener {
            val filterTrendDialog = FilterTrendDialog()
            filterTrendDialog.setDialogListener { p, s ->
                setTrendTitle(p)

                val fragment = supportFragmentManager.findFragmentByTag(tabTexts[2])
                if (fragment != null && fragment is SquareMainFragment) {
                    fragment.filter(p)
                }


            }
            filterTrendDialog.show(supportFragmentManager, "ftd")
        }

        iv_right.setOnClickListener {
            //            when (tabhost.currentTab) {
//                1 -> {
//                    isAuthUser {
//                        startActivityForResult<ReleaseNewTrendsActivity>(1)
//                    }
//                }
//                2 -> {
////                    isAuthUser {
////                        startActivityForResult<FilterActivity>(0)
////                    }
//                    startActivity<MessageSettingActivity>()
//                }
//            }
            when (tabhost.currentTab) {
                1 -> {
                    isAuthUser {
                        startActivityForResult<FilterActivity>(0)
                    }
                }
                2 -> {
                    isAuthUser {
                        startActivityForResult<ReleaseNewTrendsActivity>(1)
                    }
                }
            }
        }
        //默认标题
        tv_title.text = "D6社区"
        titleBar.gone()

        val token = SPUtils.instance().getString(Const.User.RONG_TOKEN)
        if (token.isNotEmpty()) {
            judgeDataB()
            val uid = SPUtils.instance().getString(Const.User.USER_ID)
            val nick = SPUtils.instance().getString(Const.User.USER_NICK)
            val head = SPUtils.instance().getString(Const.User.USER_HEAD)
            RongIM.getInstance().setCurrentUserInfo(UserInfo(uid, nick, Uri.parse(head)))

            RongIM.connect(token, object : RongIMClient.ConnectCallback() {
                override fun onSuccess(p0: String?) {
                }

                override fun onError(p0: RongIMClient.ErrorCode?) {
                }

                override fun onTokenIncorrect() {
                }
            })
        }
        val userId = SPUtils.instance().getString(Const.User.USER_ID)
        PushAgent.getInstance(this.applicationContext).addAlias(userId, "D6") { _, _ -> }
        Request.updateDeviceType(userId).request(this, false) { _, _ -> }
    }

    fun judgeDataB() {
        Request.findMyDatingList(SPUtils.instance().getString(Const.User.USER_ID), 1).request(this) { _, data ->
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                SPUtils.instance().put(Const.User.IASKSOMEONEELSE,false).apply()
            }else{
                SPUtils.instance().put(Const.User.IASKSOMEONEELSE,true).apply()
            }
        }

        Request.findDatingMeList(SPUtils.instance().getString(Const.User.USER_ID),1).request(this){_,data->
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                SPUtils.instance().put(Const.User.SOMEONE_ELSE_MAKES_AN_APPOINTMENT_WITH_ME,false).apply()
            }else{
                SPUtils.instance().put(Const.User.SOMEONE_ELSE_MAKES_AN_APPOINTMENT_WITH_ME,true).apply()
            }
        }
    }



    override fun onResume() {
        super.onResume()
        getUnReadCount()
    }

    private fun getUnReadCount() {
        RongIM.getInstance().getUnreadCount(object : RongIMClient.ResultCallback<Int>() {
            override fun onSuccess(p0: Int?) {
                p0?.let {
                    val view1 = tabhost.tabWidget.getChildTabViewAt(3)
                    System.err.println("-------------->$view1")
                    if (view1 != null) {
                        val view = view1.find<View>(R.id.tv_msg_count)
                        if (p0 > 0) {
                            view?.visible()
                        } else {
                            view?.gone()
                            getSysLastOne()
                        }
                    }
                }
            }

            override fun onError(p0: RongIMClient.ErrorCode?) {

            }

        }, Conversation.ConversationType.PRIVATE)
    }

    fun setTrendTitle(p: Int) {
        tv_title1.text = when (p) {
            0 -> "女生"
            1 -> "男生"
            else -> "全部动态"
        }
    }

    private fun getSysLastOne() {
        val time = SPUtils.instance().getLong(Const.LAST_TIME)
        val userId = SPUtils.instance().getString(Const.User.USER_ID)
        Request.getSystemMessages(userId, 1, time.toString(), pageSize = 1).request(this, false, success = { _, data ->
            val view = tabhost.tabWidget.getChildTabViewAt(3).findViewById<View>(R.id.tv_msg_count)
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                //无数据
                view?.gone()
                getSquareMsg()
            } else {
                if ((data.count ?: 0) > 0) {
                    view?.visible()
                } else {
                    getSquareMsg()
                }
            }

        }) { _, _ ->
            getSquareMsg()
        }
    }

    private fun getSquareMsg() {
        val time = SPUtils.instance().getLong(Const.LAST_TIME)
        val userId = SPUtils.instance().getString(Const.User.USER_ID)
        Request.getSquareMessages(userId, 1, time.toString(), pageSize = 1).request(this, false, success = { _, data ->
            val view = tabhost.tabWidget.getChildTabViewAt(3).findViewById<View>(R.id.tv_msg_count)
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                //无数据
                view?.gone()
            } else {
                if ((data.count ?: 0) > 0) {
                    view?.visible()
                }
            }
        }) { _, _ ->

        }
    }

    /**
     * 设置tab
     */
    private fun addTab(i: Int): View {
        //取得布局实例
        val view = View.inflate(this@MainActivity, R.layout.tab_home_bottom_content, null)
        //取得布局对象
        val textView = view.find<TextView>(R.id.img)
        //设置图标
        textView.setCompoundDrawablesWithIntrinsicBounds(0, tabImages[i], 0, 0)
        //设置标题
        textView.text = tabTexts[i]
        return view
    }

    fun changeTab(index: Int) {
        tabhost.currentTab = index
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0 && data != null) {//筛选
                val area = data.getStringExtra("area")
                val areaType = data.getIntExtra("areaType", -1)
                val typeIds = data.getStringExtra("typeIds")
                val vipIds = data.getStringExtra("vipIds")
                val fragment = supportFragmentManager.findFragmentByTag("速约")
                if (fragment != null && fragment is SpeedDateFragment) {
                    fragment.refresh(area, areaType, typeIds, vipIds)
                }

            } else if (requestCode == 1) {
                val fragment = supportFragmentManager.findFragmentByTag(tabTexts[2])
                if (fragment != null && fragment is SquareMainFragment) {
                    fragment.refresh()
                }
            }
        }
    }

    private var mExitTime: Long = 0

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                toast("再按一次返回桌面")
                mExitTime = System.currentTimeMillis()
            } else {
                moveTaskToBack(true)
//                finish()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        try {
            immersionBar.destroy()
            unregisterReceiver(broadcast)
        } catch (e: Exception) {

        }
        super.onDestroy()
    }
}
