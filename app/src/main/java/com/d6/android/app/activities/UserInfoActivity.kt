package com.d6.android.app.activities

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.d6.android.app.R
import com.d6.android.app.adapters.MyImageAdapter
import com.d6.android.app.adapters.MySquareAdapter
import com.d6.android.app.adapters.SelfReleaselmageAdapter
import com.d6.android.app.adapters.UserTagAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.*
import com.d6.android.app.eventbus.LikeMsgEvent
import com.d6.android.app.extentions.request
import com.d6.android.app.extentions.showBlur
import com.d6.android.app.models.*
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.AppUtils.Companion.context
import com.d6.android.app.widget.CustomToast
import com.d6.android.app.widget.SwipeRefreshRecyclerLayout
import com.google.gson.JsonObject
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration
import io.rong.imkit.RongIM
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_user_info_v2.*
import kotlinx.android.synthetic.main.header_user_info_layout.view.*
import kotlinx.android.synthetic.main.layout_userinfo_date.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.*

/**
 *
 */
class UserInfoActivity : BaseActivity(), SwipeRefreshRecyclerLayout.OnRefreshListener {
    private val id by lazy {
        intent.getStringExtra("id")
    }

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private var mData: UserData? = null

    private var pageNum = 1
    private val headerView by lazy {
        layoutInflater.inflate(R.layout.header_user_info_layout, mSwipeRefreshLayout.mRecyclerView, false)
    }
    private val mSquares = ArrayList<Square>()
    private val squareAdapter by lazy {
        MySquareAdapter(mSquares, 0)
    }
    private val mImages = ArrayList<AddImage>()

    private val myImageAdapter by lazy {
        MyImageAdapter(mImages)
    }

    private val mDateImages = ArrayList<String>()
    private val mDateimageAdapter by lazy {
        SelfReleaselmageAdapter(mDateImages, 1)
    }

    private val mTags = ArrayList<UserTag>()
    private val userTagAdapter by lazy {
        UserTagAdapter(mTags)
    }
    private val colorDrawable by lazy {
        ColorDrawable(Color.WHITE).mutate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info_v2)

        immersionBar
                .fitsSystemWindows(false)
                .statusBarColor(R.color.trans_parent)
                .titleBar(rl_title)
                .statusBarDarkFont(false)
                .keyboardEnable(true)
                .init()
        EventBus.getDefault().register(this)

        tv_back.setOnClickListener {
            finish()
        }

        mSwipeRefreshLayout.setLayoutManager(LinearLayoutManager(this))
        squareAdapter.setHeaderView(headerView)
        mSwipeRefreshLayout.setAdapter(squareAdapter)
        mSwipeRefreshLayout.setOnRefreshListener(this)

        headerView.rv_my_images.setHasFixedSize(true)
        headerView.rv_my_images.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        headerView.rv_my_images.isNestedScrollingEnabled = false
        headerView.rv_my_images.adapter = myImageAdapter
        headerView.rv_my_images.addItemDecoration(VerticalDividerItemDecoration.Builder(this)
                .colorResId(android.R.color.transparent)
                .size(dip(8))
                .build())

        tv_like.setOnClickListener(View.OnClickListener {
            if (mData?.iIsFollow != null) {
                if (mData?.iIsFollow == 0) {//mData?.iIsFollow
                    addFollow()
                } else {
                    delFollow()
                }
            }
        })
        myImageAdapter.setOnItemClickListener { _, position ->
            val data = mImages[position]
            if (data.type != 1) {
                val urls = mImages.filter { it.type != 1 }.map { it.imgUrl }
                startActivityForResult<ImagePagerActivity>(22, ImagePagerActivity.URLS to urls, ImagePagerActivity.CURRENT_POSITION to position)
            }
        }
        headerView.rv_tags.setHasFixedSize(true)
        headerView.rv_tags.layoutManager = GridLayoutManager(this, 2)//FlexboxLayoutManager(this)
        headerView.rv_tags.isNestedScrollingEnabled = false
        headerView.rv_tags.adapter = userTagAdapter

        mSwipeRefreshLayout.mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (recyclerView?.layoutManager is LinearLayoutManager) {
                    val l = (recyclerView.layoutManager as LinearLayoutManager)
                    val f = l.findFirstVisibleItemPosition()
                    if (f == 0) {
                        val view = l.findViewByPosition(0)
                        val h = view.top
                        sysErr("----------onScrolled-----$h-")
                        val alpha = if (-h > 255) {
                            255
                        } else {
                            -h
                        }
                        setTitleBgAlpha(alpha)
                    }
                }
            }
        })

        tv_siliao.setOnClickListener {
            mData?.let {
                val name = it.name ?: ""
                showDatePayPointDialog(name)
            }
        }

        iv_sendflower.setOnClickListener {
            mData?.let {
                it.accountId?.let {
                    var dialogSendRedFlowerDialog = SendRedFlowerDialog()
                    dialogSendRedFlowerDialog.arguments = bundleOf("ToFromType" to 3, "userId" to it)
                    dialogSendRedFlowerDialog.show(supportFragmentManager, "sendflower")
                }
            }
        }

        tv_more.setOnClickListener {
            val userActionDialog = UserActionDialog()
            userActionDialog.setDialogListener { p, s ->
                if (p == 0) {//举报
                    report()
                } else if (p == 1) {
                    addBlackList()
                }
            }
            userActionDialog.show(supportFragmentManager, "user")
        }

        headerView.headView.setOnClickListener {
            mData?.let {
                it.picUrl?.let {
                    var url = it.replace(Const.Pic_Thumbnail_Size_wh200,Const.Pic_Thumbnail_Size_wh600)
                    val urls = ArrayList<String>()
                    urls.add(url.toString())
                    startActivityForResult<ImagePagerActivity>(22, ImagePagerActivity.URLS to urls, ImagePagerActivity.CURRENT_POSITION to 0)
                }
            }
        }

        squareAdapter.setOnItemClickListener { view, position ->
            val square = mSquares[position]
//            getTrendDetail(square.id?:""){
//                startActivityForResult<TrendDetailActivity>(18,"data" to it)
//            }
            startActivity<SquareTrendDetailActivity>("id" to (square.id
                    ?: ""), "position" to position)
        }

        dialog()
        if (TextUtils.equals(userId, id)) {
            rl_doaction.visibility = View.GONE
        } else {
            rl_doaction.visibility = View.VISIBLE
        }
        getUserInfo()
        addVistor()
        getUserFollowAndFansandVistor()
    }

    private fun setTitleBgAlpha(alpha: Int) {
        colorDrawable.alpha = alpha
        rl_title.backgroundDrawable = colorDrawable
        tv_title_nick.alpha = alpha / 255f
        if (alpha > 128) {
            tv_msg.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_chat_orange, 0)
            tv_more.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_more_orange, 0)
            immersionBar.statusBarDarkFont(true).init()
        } else {
            tv_msg.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_chat_white1, 0)
            tv_more.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_more_white, 0)
            immersionBar.statusBarDarkFont(false).init()
        }
    }

    /**
     * 展示约会信息
     */
    private fun setDateInfo(myAppointment: MyAppointment?) {
        headerView.tv_datetype_name.text = Const.dateTypes[myAppointment?.iAppointType!!.toInt() - 1]
        if (myAppointment.iAppointType!!.toInt() == Const.dateTypesBig.size) {
            var drawable = ContextCompat.getDrawable(context, R.mipmap.invitation_nolimit_feed)
            headerView.tv_datetype_name.setCompoundDrawables(null, drawable, null, null)
            headerView.tv_datetype_name.setCompoundDrawablePadding(dip(3))
        } else {
            var drawable = ContextCompat.getDrawable(context, Const.dateTypesBig[myAppointment?.iAppointType!!.toInt() - 1])
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// 设置边界
            headerView.tv_datetype_name.setCompoundDrawablePadding(dip(3));
            headerView.tv_datetype_name.setCompoundDrawables(null, drawable, null, null);
        }

        var sb = StringBuffer()
        if (!myAppointment.iAge.toString().isNullOrEmpty()) {
            if (myAppointment.iAge != null) {
                sb.append("${myAppointment.iAge}岁")
            }
        }

        if (!myAppointment.iHeight.toString().isNullOrEmpty()) {
            if (myAppointment.iHeight!!.toInt() > 0) {
                sb.append("·${myAppointment.iHeight}cm")
            }
        }

        if (!myAppointment.iWeight.toString().isNullOrEmpty()) {
            if (!myAppointment.iWeight.toString().equals("0")) {
                sb.append("·${myAppointment.iWeight}kg")
            }
        }

        if (!sb.toString().isNullOrEmpty()) {
            headerView.tv_sub_title.text = sb.toString()
            headerView.tv_sub_title.visibility = View.VISIBLE
        } else {
            headerView.tv_sub_title.visibility = View.GONE
        }

        var time = converTime(myAppointment.dEndtime)
        headerView.tv_time_long.text = "倒计时:${time}"

        headerView.tv_self_address.text = myAppointment.sPlace

        headerView.tv_content.text = myAppointment.sDesc

        if (myAppointment.sAppointPic.isNullOrEmpty()) {
            headerView.rv_images.gone()
        } else {
            headerView.rv_images.visible()
            mDateImages.clear()
            val images = myAppointment.sAppointPic?.split(",")
            if (images != null) {
                mDateImages.addAll(images.toList())
            }

            headerView.rv_images.setHasFixedSize(true)
            headerView.rv_images.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            headerView.rv_images.adapter = mDateimageAdapter
        }

        headerView.tv_send_date.setOnClickListener {
            signUpDate(myAppointment)
        }
    }

    private fun getUserInfo() {
        Request.getUserInfo(userId, id).request(this, success = { _, data ->
            mSwipeRefreshLayout.isRefreshing = false
            this.mData = data
            data?.let {
                val info = UserInfo(data.accountId, data.name, Uri.parse("" + data.picUrl))
                RongIM.getInstance().refreshUserInfoCache(info)
                tv_title_nick.text = it.name
                headerView.iv_bg.showBlur(it.picUrl)
                headerView.headView.setImageURI(it.picUrl)
                headerView.tv_nick.text = it.name
                if (!TextUtils.isEmpty(it.intro)) {
                    headerView.tv_signature.text = it.intro
                } else {
                    headerView.tv_signature.text = "本宝宝还没想到好的自我介绍~"
                }
                it.isValid?.let {
                    if (TextUtils.equals("1", it)) {
                        headerView.rl_prompt.visibility = View.GONE
                        var lp = RelativeLayout.LayoutParams(headerView.rl_headView.layoutParams)
                        lp?.let {
                            lp.topMargin = resources.getDimensionPixelOffset(R.dimen.height_75)
                            lp.leftMargin = resources.getDimensionPixelOffset(R.dimen.margin16dp)
                            lp.rightMargin = resources.getDimensionPixelOffset(R.dimen.margin_6)
                        }
                        headerView.rl_headView.layoutParams = lp
                    } else {
                        headerView.rl_prompt.visibility = View.VISIBLE
                    }
                }

                headerView.tv_sex.isSelected = TextUtils.equals("0", it.sex)
                it.age?.let {
                    if (it.toInt() <= 0) {
                        headerView.tv_sex.text = ""
                    } else {
                        headerView.tv_sex.text = it
                    }
                }

                if (TextUtils.equals("0", it.sex)) {
                    if (TextUtils.equals("0", it.screen) || TextUtils.equals("3", it.screen) || it.screen.isNullOrEmpty()) {
                        headerView.img_other_auther.visibility = View.GONE
                        headerView.img_date_auther.visibility = View.GONE
                        if (TextUtils.equals("3", it.screen)) {
                            headerView.tv_other_auther_sign.visibility = View.GONE
                        } else {
                            headerView.tv_other_auther_sign.visibility = View.VISIBLE
                        }
                    } else if (TextUtils.equals("1", data.screen)) {
                        headerView.img_other_auther.visibility = View.VISIBLE
                        headerView.img_date_auther.visibility = View.VISIBLE
                        headerView.tv_other_auther_sign.visibility = View.GONE
                    }
                } else {
                    headerView.img_other_auther.visibility = View.GONE
                    headerView.img_date_auther.visibility = View.GONE
                }

                headerView.img_other_auther.setOnClickListener {
                    toast(getString(R.string.string_auth))
                }

                headerView.tv_other_auther_sign.setOnClickListener {
                    startActivity<DateAuthStateActivity>()
                }

                headerView.tv_vip.text = String.format("%s", it.classesname)
                mTags.clear()
                if (!it.height.isNullOrEmpty()) {
                    mTags.add(UserTag("身高 ${it.height}", R.mipmap.boy_stature_whiteicon))
                }
                if (!it.weight.isNullOrEmpty()) {
                    mTags.add(UserTag("体重 ${it.weight}", R.mipmap.boy_weight_whiteicon))
                }

                if (!it.constellation.isNullOrEmpty()) {
                    mTags.add(UserTag("星座 ${it.constellation}", R.mipmap.boy_constellation_whiteicon))
                }

                if (!it.city.isNullOrEmpty()) {
                    mTags.add(UserTag("地区 ${it.city}", R.mipmap.boy_area_whiteicon))
                }

                userTagAdapter.notifyDataSetChanged()

                if (!it.job.isNullOrEmpty()) {
                    AppUtils.setUserInfoTvTag(this, "职业 ${it.job}", 0, 2, headerView.tv_job)
                } else {
                    headerView.tv_job.visibility = View.GONE
                }

                if (!it.zuojia.isNullOrEmpty()) {
                    AppUtils.setUserInfoTvTag(this, "座驾 ${it.zuojia}", 0, 2, headerView.tv_zuojia)
                } else {
                    headerView.tv_zuojia.visibility = View.GONE
                }

                if (!it.hobbit.isNullOrEmpty()) {
                    var mHobbies = it.hobbit?.replace("#", ",")?.split(",")
                    var sb = StringBuffer()
                    sb.append("爱好 ")
                    if (mHobbies != null) {
                        //,,
                        for (str in mHobbies) {
                            if (!TextUtils.isEmpty(str)) {
                                sb.append("${str} ")
                            }
                        }
                        if (sb.toString().trim().length <= 2) {
                            headerView.tv_aihao.visibility = View.GONE
                        }
                        AppUtils.setUserInfoTvTag(this, sb.toString(), 0, 2, headerView.tv_aihao)
                    }
                } else {
                    headerView.tv_aihao.visibility = View.GONE
                }

                if (!TextUtils.equals("null", it.userpics)) {
                    refreshImages(it)
                }else{
                    headerView.rv_my_images.visibility = View.GONE
                }

                squareAdapter.setUserInfo(mData!!)
                getTrendData()

                if (it.appointment != null) {
                    headerView.date_headView.setImageURI(it.picUrl)
                    headerView.tv_name.text = it.name
                    headerView.tv_name.isSelected = TextUtils.equals("0", it.sex)
                    setDateInfo(it.appointment)
                } else {
                    headerView.rl_userinfo_date.visibility = View.GONE
                }

                if (data.iIsFollow != null) {
                    if (data.iIsFollow == 1) {
                        tv_like.setCompoundDrawables(null, null, null, null);
                        tv_like.text = resources.getString(R.string.string_liked)
                        tv_like.backgroundResource = R.drawable.shade_20r_white
                        tv_like.textColor = ContextCompat.getColor(context, R.color.color_666666)

                        tv_like.setPadding(resources.getDimensionPixelSize(R.dimen.margin_20), resources.getDimensionPixelSize(R.dimen.margin_12), resources.getDimensionPixelSize(R.dimen.margin_20), resources.getDimensionPixelSize(R.dimen.margin_12))
                        tv_siliao.setPadding(resources.getDimensionPixelSize(R.dimen.padding_60), resources.getDimensionPixelSize(R.dimen.margin_10), resources.getDimensionPixelSize(R.dimen.padding_60), resources.getDimensionPixelSize(R.dimen.margin_10))
                    } else {
                        tv_like.text = resources.getString(R.string.string_like)
                        tv_like.backgroundResource = R.drawable.shape_20r_ff6
                        tv_like.textColor = ContextCompat.getColor(context, R.color.white)

                        tv_like.setPadding(resources.getDimensionPixelSize(R.dimen.padding_30), resources.getDimensionPixelSize(R.dimen.margin_12), resources.getDimensionPixelSize(R.dimen.padding_30), resources.getDimensionPixelSize(R.dimen.margin_12))

                    }
                }
            }
        }) { _, _ -> mSwipeRefreshLayout.isRefreshing = false }
    }

    private fun refreshImages(userData: UserData) {
        mImages.clear()
        if (!userData.userpics.isNullOrEmpty()) {
            userData.userpics?.let {
                val images = it.split(",")
                images.forEach {
                    mImages.add(AddImage(it))
                }
            }
        }else{
            headerView.rv_my_images.visibility = View.GONE
        }
        myImageAdapter.notifyDataSetChanged()
    }

    private fun getTrendData() {
        Request.getMySquares(userId, id, 0, pageNum).request(this, success = { _, data ->
            mSwipeRefreshLayout.isRefreshing = false//15717
            if (pageNum == 1) {
                mSquares.clear()
            }
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                if (pageNum > 1) {
                    mSwipeRefreshLayout.setLoadMoreText("没有更多了")
                    pageNum--
                } else {
                    mSwipeRefreshLayout.setLoadMoreText("暂无数据")
                }
            } else {
                mSquares.addAll(data.list.results)
                squareAdapter.notifyDataSetChanged()
            }
        }) { _, _ ->
            mSwipeRefreshLayout.isRefreshing = false
        }
    }

    private fun report() {
        startActivity<ReportActivity>("id" to id, "tiptype" to "1")
    }

    private fun addBlackList() {
        var mDialogAddBlackList = DialogAddBlackList()
        mDialogAddBlackList.show(supportFragmentManager, "addBlacklist")
        mDialogAddBlackList.setDialogListener { p, s ->
            dialog()
            Request.addBlackList(userId, id).request(this) { _, _ ->
                CustomToast.showToast(getString(R.string.string_blacklist_toast))
            }
        }
    }

    //关注粉丝访客
    fun getUserFollowAndFansandVistor() {
        Request.getUserFollowAndFansandVistor(id).request(this, success = { s: String?, data: FollowFansVistor? ->
            //            toast("$s,${data?.iFansCount},${data?.iFansCountAll},${data?.iUserid}")
            data?.let {
                headerView.tv_user_fans_count.text = data.iFansCountAll.toString()
                headerView.tv_user_follow_count.text = data.iFollowCount.toString()
//                headerView.tv_user_vistor_count.text = data.iVistorCountAll.toString()
            }
        })
    }

    private fun addFollow() {
        dialog()//35578
        Request.getAddFollow(userId, id).request(this,false,success ={ s: String?, jsonObject: JsonObject? ->
            //            toast("$s,$jsonObject")
//            headerView.iv_isfollow.imageResource = R.mipmap.usercenter_liked_button

            tv_like.setCompoundDrawables(null, null, null, null);

            tv_like.text = resources.getString(R.string.string_liked)
            tv_like.backgroundResource = R.drawable.shade_20r_white
            tv_like.textColor = ContextCompat.getColor(context, R.color.color_666666)

            tv_like.setPadding(resources.getDimensionPixelSize(R.dimen.margin_20), resources.getDimensionPixelSize(R.dimen.margin_12), resources.getDimensionPixelSize(R.dimen.margin_20), resources.getDimensionPixelSize(R.dimen.margin_12))
            tv_siliao.setPadding(resources.getDimensionPixelSize(R.dimen.padding_60), resources.getDimensionPixelSize(R.dimen.margin_10), resources.getDimensionPixelSize(R.dimen.padding_60), resources.getDimensionPixelSize(R.dimen.margin_10))

            mData?.iIsFollow = 1
            showTips(jsonObject, "", "")
        }){code,msg->
            CustomToast.showToast(msg)
        }
    }

    private fun delFollow() {
        dialog()
        Request.getDelFollow(userId, id).request(this,true) { s: String?, jsonObject: JsonObject? ->
            //            data.optDouble("wanshanziliao")
//            toast("$s,$jsonObject")
//            headerView.iv_isfollow.imageResource = R.mipmap.usercenter_like_button

            var drawable = ContextCompat.getDrawable(context, R.mipmap.icon_like_button)
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//这句一定要加
            tv_like.setCompoundDrawables(drawable, null, null, null);

            tv_like.text = resources.getString(R.string.string_like)
            tv_like.backgroundResource = R.drawable.shape_20r_ff6
            tv_like.textColor = ContextCompat.getColor(context, R.color.white)


            tv_like.setPadding(resources.getDimensionPixelSize(R.dimen.padding_30), resources.getDimensionPixelSize(R.dimen.margin_12), resources.getDimensionPixelSize(R.dimen.padding_30), resources.getDimensionPixelSize(R.dimen.margin_12))
            tv_siliao.setPadding(resources.getDimensionPixelSize(R.dimen.padding_30), resources.getDimensionPixelSize(R.dimen.margin_10), resources.getDimensionPixelSize(R.dimen.padding_30), resources.getDimensionPixelSize(R.dimen.margin_10))
            mData?.iIsFollow = 0
        }
    }

    //添加访客
    private fun addVistor() {
        Request.getAddVistor(id, userId).request(this) { s: String?, jsonObject: JsonObject? ->
        }
    }

    private fun showDatePayPointDialog(name: String) {
        isAuthUser{
            Request.getApplyStatus(userId, id).request(this, false, success = { msg, jsonObjetct ->
                jsonObjetct?.let {
                    var code = it.optInt("code")
                    if (code != 7) {
                        RongIM.getInstance().startConversation(this, Conversation.ConversationType.PRIVATE, id, name)
                    }else if(code == 7){
                        startActivity<MenMemberActivity>()
                    }
                }
            })
        }
//        Request.doTalkJustify(userId, id).request(this,false,success = {msg,data->
//            if(data!=null){
//                var code = data!!.optInt("code")
//                if(code == 1){
//                    var point = data!!.optString("iTalkPoint")
//                    var remainPoint = data!!.optString("iRemainPoint")
//                    if(point.toInt() > remainPoint.toInt()){
//                        val dateDialog = OpenDatePointNoEnoughDialog()
//                        var point = data!!.optString("iTalkPoint")
//                        var remainPoint = data!!.optString("iRemainPoint")
//                        dateDialog.arguments= bundleOf("point" to point,"remainPoint" to remainPoint)
//                        dateDialog.show(supportFragmentManager, "d")
//                    }else{
//                        val dateDialog = OpenDatePayPointDialog()
//                        dateDialog.arguments= bundleOf("point" to point,"remainPoint" to remainPoint,"username" to name,"chatUserId" to id)
//                        dateDialog.show(supportFragmentManager, "d")
//                    }
//                } else if(code == 0){
//                    showToast(msg.toString())
////                    RongIM.getInstance().startConversation(this, Conversation.ConversationType.PRIVATE, id, name)
//                } else {
//                    val dateDialog = OpenDatePointNoEnoughDialog()
//                    var point = data!!.optString("iTalkPoint")
//                    var remainPoint = data!!.optString("iRemainPoint")
//                    dateDialog.arguments= bundleOf("point" to point,"remainPoint" to remainPoint)
//                    dateDialog.show(supportFragmentManager, "d")
//                }
//            }else{
//                RongIM.getInstance().startConversation(this, Conversation.ConversationType.PRIVATE, id, name)
//            }
//        }) { code, msg ->
//             if(code == 0){
//                 showToast(msg)
//             }
//        }
    }

    private fun signUpDate(myAppointment: MyAppointment) {
        Request.queryAppointmentPoint(userId).request(this, false, success = { msg, data ->
            val dateDialog = OpenDateDialog()
            dateDialog.arguments = bundleOf("data" to myAppointment, "explain" to data!!)
            dateDialog.show(supportFragmentManager, "d")
        }) { code, msg ->
            if (code == 2) {
                var openErrorDialog = OpenDateErrorDialog()
                openErrorDialog.arguments = bundleOf("code" to code, "msg" to msg)
                openErrorDialog.show(supportFragmentManager, "d")
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: LikeMsgEvent) {
        if (event.type == 1) {
            tv_like.setCompoundDrawables(null, null, null, null);
            tv_like.text = resources.getString(R.string.string_liked)
            tv_like.backgroundResource = R.drawable.shade_20r_white
            tv_like.textColor = ContextCompat.getColor(context, R.color.color_666666)

            tv_like.setPadding(resources.getDimensionPixelSize(R.dimen.margin_20), resources.getDimensionPixelSize(R.dimen.margin_12), resources.getDimensionPixelSize(R.dimen.margin_20), resources.getDimensionPixelSize(R.dimen.margin_12))
            tv_siliao.setPadding(resources.getDimensionPixelSize(R.dimen.padding_60), resources.getDimensionPixelSize(R.dimen.margin_10), resources.getDimensionPixelSize(R.dimen.padding_60), resources.getDimensionPixelSize(R.dimen.margin_10))
        } else {
            tv_like.text = resources.getString(R.string.string_like)
            tv_like.backgroundResource = R.drawable.shape_20r_ff6
            tv_like.textColor = ContextCompat.getColor(context, R.color.white)

            tv_like.setPadding(resources.getDimensionPixelSize(R.dimen.padding_30), resources.getDimensionPixelSize(R.dimen.margin_12), resources.getDimensionPixelSize(R.dimen.padding_30), resources.getDimensionPixelSize(R.dimen.margin_12))
        }
    }

    override fun onRefresh() {
        pageNum = 1
        getUserInfo()
    }

    override fun showToast(msg: String) {
    }

    override fun onLoadMore() {
        pageNum++
        getTrendData()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}
