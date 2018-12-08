package com.d6.android.app.activities

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.support.annotation.NonNull
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.FilterTrendDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.fragments.*
import com.d6.android.app.models.Response
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.umeng.message.PushAgent
import io.rong.imkit.RongIM
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import org.jetbrains.anko.collections.forEachWithIndex


/**
 * 主页
 */
class MainActivity : BaseActivity() {
    private val tabTexts = arrayOf( "约会","发现", "动态","消息", "我的")

    private val tabImages = arrayOf(R.drawable.home_main_selector,R.drawable.home_speed_date_selector,R.drawable.home_square_selector
            ,R.drawable.home_msg_selector, R.drawable.home_mine_selector)
    private val fragmentArray = arrayOf<Class<*>>(HomeFragment::class.java,
            DateFragment::class.java, SquareMainFragment::class.java,
            MessageFragment::class.java,MineV2Fragment::class.java)
    private var unReadMsg:Int?=-1

    private val broadcast by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                runOnUiThread {
                    getUnReadCount()
                }
            }
        }
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
        //获取我都约会未读消息
        tabhost.setOnTabChangedListener {
            titleBar.visible()
            line.visible()
            iv_right.text = ""
            when {
                TextUtils.equals(it, tabTexts[0]) -> {
//                    iv_right.imageResource = R.mipmap.ic_add_orange
//                    tv_title.text = "广场"
                    tv_create_date.visible()
                    tv_date_mydate.visible()
                    date_headView.visible()
                    setNoticeIsNoShow()
                    iv_right.gone()
                    tv_title1.gone()
//                    iv_right.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.ic_filter,0)
                    tv_title.text = "约会"
                }
                TextUtils.equals(it, tabTexts[1]) -> {

                    //titleBar.backgroundColor = Color.TRANSPARENT
                    titleBar.gone()
                    iv_right.gone()
                    tv_title1.gone()
                    tv_title.text = "D6社区"
//                    val fragment0 = supportFragmentManager.findFragmentByTag(tabTexts[0])
//                    if (fragment0 != null && fragment0 is DateFragment) {
//                        fragment0.onFirstVisibleToUser()
//                    }
                }
                TextUtils.equals(it, tabTexts[2]) -> {
                    tv_create_date.gone()
                    tv_date_mydate.gone()
                    date_headView.gone()
                    iv_mydate_newnotice.gone()
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
                    titleBar.gone()
                    iv_right.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
//                    iv_right.imageResource = R.mipmap.ic_msg_setting
                    tv_title.text = "消息"
//                    iv_right.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_msg_setting, 0)
//                    iv_right.text = "发布"
                    tv_title1.text = ""
                }
                TextUtils.equals(it, tabTexts[4]) -> {
                    titleBar.gone()
                    iv_right.gone()
                    tv_title1.gone()
                    tv_title.text = "我的"
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

//        tv_create_date.gone()

        tv_create_date.setOnClickListener {
            isAuthUser {
                startActivityForResult<PublishFindDateActivity>(10)
            }
        }

        date_headView.setOnClickListener {
            getAuthState()
        }

        tv_date_mydate.setOnClickListener {
            getAuthState()
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

        titleBar.visibility = View.VISIBLE

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

        val head = SPUtils.instance().getString(Const.User.USER_HEAD)
        date_headView.setImageURI(head)
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

    private fun getAuthState() {
//        startActivity<MyDateActivity>()
        startActivity<MyDateListActivity>()
//        tv_tip.visibility = View.GONE
//        SPUtils.instance().put(Const.User.IS_FIRST_SHOW_TIPS,false).apply()
    }

    override fun onResume() {
        super.onResume()
        if(tabhost.currentTab==0){
            myDateUnMsg()
        }
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

    private fun myDateUnMsg(){
        Request.getUnreadAppointmentCount(SPUtils.instance().getString(Const.User.USER_ID)).request(this, success = { msg, data ->
            if (data != null) {
                unReadMsg = data.unreadCount
                setNoticeIsNoShow()
            }
        })
    }

    private fun setNoticeIsNoShow(){
        val view = tabhost.tabWidget.getChildTabViewAt(0).findViewById<View>(R.id.tv_msg_count)
        if(unReadMsg!! > 0){
            iv_mydate_newnotice.visibility = View.VISIBLE
            view.visibility = View.VISIBLE
        }else{
            iv_mydate_newnotice.visibility = View.GONE
            view.visibility = View.GONE
        }
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
//                val area = data.getStringExtra("area")
//                val areaType = data.getIntExtra("areaType", -1)
//                val typeIds = data.getStringExtra("typeIds")
//                val vipIds = data.getStringExtra("vipIds")
//                val fragment = supportFragmentManager.findFragmentByTag("速约")
//                if (fragment != null && fragment is SpeedDateFragment) {
//                    fragment.refresh(area, areaType, typeIds, vipIds)
//                }
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
