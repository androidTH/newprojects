package com.d6.android.app.activities

import android.content.Intent
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
import com.d6.android.app.recoder.AudioPlayListener
import com.d6.android.app.utils.*
import com.d6.android.app.utils.AppUtils.Companion.context
import com.d6.android.app.utils.Const.CustomerServiceId
import com.d6.android.app.utils.Const.CustomerServiceWomenId
import com.d6.android.app.widget.CustomToast
import com.d6.android.app.widget.ObserverManager
import com.d6.android.app.widget.SwipeRefreshRecyclerLayout
import com.d6.android.app.widget.WrapContentLinearLayoutManager
import com.google.gson.JsonObject
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import io.rong.imkit.RongIM
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_user_info_v2.*
import kotlinx.android.synthetic.main.header_user_info_layout.view.*
import kotlinx.android.synthetic.main.layout_userinfo_date.view.*
import me.nereo.multi_image_selector.MultiImageSelectorActivity
import me.nereo.multi_image_selector.utils.FinishActivityManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.*
import www.morefuntrip.cn.sticker.Bean.BLBeautifyParam
import java.util.*
import kotlin.collections.ArrayList

/**
 *
 */
class UserInfoActivity : BaseActivity(), SwipeRefreshRecyclerLayout.OnRefreshListener, Observer {

    private val id by lazy {
        intent.getStringExtra("id")
    }

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private var mData: UserData? = null
    private var MAXPICS = 9

    private var pageNum = 1

    private val headerView by lazy {
        layoutInflater.inflate(R.layout.header_user_info_layout, mSwipeRefreshLayout.mRecyclerView, false)
    }

    private val mSquares = ArrayList<Square>()

    private var deletePic:Boolean = false
    private val squareAdapter by lazy {
        MySquareAdapter(mSquares, 0)
    }

    private val mImages = ArrayList<AddImage>()

    private val mPicsWall = ArrayList<AddImage>()

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

    private val mAudioMedio by lazy{
        AudioPlayUtils(this)
    }

    //播放录音
    private var playIndex = -1
    private var playSquare:Square? = null

    override fun update(o: Observable?, arg: Any?) {
        var mImagelocal = arg as Imagelocals
        if(mImagelocal.mType == 1){
            var localImages = ArrayList<String>()
            mImagelocal.mUrls.forEach {
                localImages.add(it)///storage/emulated/0/Huawei/MagazineUnlock/magazine-unlock-01-2.3.1104-_9E598779094E2DB3E89366E34B1A6D50.jpg
            }
            updateImages(localImages)
        }
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
        ObserverManager.getInstance().addObserver(this)

        tv_back.setOnClickListener {
            finish()
        }

        mSwipeRefreshLayout.setLayoutManager(WrapContentLinearLayoutManager(this))
        squareAdapter.setHeaderView(headerView)
        mSwipeRefreshLayout.setAdapter(squareAdapter)
        mSwipeRefreshLayout.setOnRefreshListener(this)
//        mSwipeRefreshLayout.addItemDecoration(HorizontalDividerItemDecoration.Builder(context)
//                .size(15)
//                .color(ContextCompat.getColor(context, R.color.color_F5F5F5))
//                .build())

        headerView.rv_my_images.setHasFixedSize(true)
        headerView.rv_my_images.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        headerView.rv_my_images.isNestedScrollingEnabled = false
        headerView.rv_my_images.adapter = myImageAdapter
        headerView.rv_my_images.addItemDecoration(VerticalDividerItemDecoration.Builder(this)
                .colorResId(android.R.color.transparent)
                .size(dip(8))
                .build())

        tv_like.setOnClickListener(View.OnClickListener {
            mData?.let {
                if (it.iIsFollow != null) {
                    if (it.iIsFollow == 0) {//mData?.iIsFollow
                        addFollow()
                    } else {
                        delFollow()
                    }
                }
            }
        })
        myImageAdapter.setOnItemClickListener { _, position ->
            val addImg = mImages[position]
            if (addImg.type != 1) {
                mData?.let {
                    val urls = mImages.filter { it.type != 1 }.map { it.imgUrl }
                    startActivityForResult<ImagePagerActivity>(22,  "data" to it,ImagePagerActivity.URLS to urls, ImagePagerActivity.CURRENT_POSITION to position,"delete" to deletePic)
                }
            }else{
                if (mImages.size > MAXPICS) {//最多9张
                    showToast("最多上传9张图片")
                    return@setOnItemClickListener
                }
                val c = (MAXPICS - mImages.size+1)
                startActivityForResult<MultiImageSelectorActivity>(8
                        , MultiImageSelectorActivity.EXTRA_SELECT_MODE to MultiImageSelectorActivity.MODE_MULTI
                        ,MultiImageSelectorActivity.EXTRA_SELECT_COUNT to c,MultiImageSelectorActivity.EXTRA_SHOW_CAMERA to true
                )
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
            val shareDialog = ShareFriendsDialog()
            mData?.let {
                it.iIsInBlackList?.let {
                    shareDialog.arguments = bundleOf("from" to "userInfo","id" to id,"isInBlackList" to it,"sResourceId" to id)
                }
            }
            shareDialog.setDialogListener { p, s ->
                if (p == 0) {
                    report()
                } else if (p == 2) {
                    mData?.let {
                        it.iIsInBlackList?.let {
                            if(it==1){
                                removeBlackList()
                            }else{
                                addBlackList()
                            }
                        }
                    }
                }else if(p==1){

                }
            }
            shareDialog.show(supportFragmentManager, "user")
        }

        tv_msg.setOnClickListener {
            mData?.let {
                startActivityForResult<MyInfoActivity>(0, "data" to it, "images" to mImages)
            }
        }

        headerView.rel_add_square.setOnClickListener {
            //发布动态
//            FinishActivityManager.getManager().addActivity(this)
            startActivityForResult<ReleaseNewTrendsActivity>(3)
        }

        headerView.headView.setOnClickListener {
            mData?.let {
                it.picUrl?.let {
                    var url = it.replace(Const.Pic_Thumbnail_Size_wh200,"")
                    val urls = ArrayList<String>()
                    urls.add(url)
                    startActivityForResult<ImagePagerActivity>(22, ImagePagerActivity.URLS to urls, ImagePagerActivity.CURRENT_POSITION to 0)
                }
            }
        }

        squareAdapter.setOnItemClickListener { view, position ->
            val square = mSquares[position]
//            getTrendDetail(square.id?:""){
//                startActivityForResult<TrendDetailActivity>(18,"data" to it)
//            }
            startActivity<SquareTrendDetailActivity>("id" to "${square.id}", "position" to position)
        }

//        dialog()

        if (TextUtils.equals(getLocalUserId(), id)) {
            rl_doaction.visibility = View.GONE
            tv_more.visibility =View.GONE
            tv_msg.visibility = View.VISIBLE
            deletePic = true
            mImages.add(AddImage("res:///" + myImageAdapter.mRes, 1))
            headerView.tv_user_follow_tips.text = "我喜欢的"
            headerView.tv_user_fans_tips.text = "喜欢我的"
            headerView.rel_add_square.visibility = View.VISIBLE
            headerView.rl_userinfo_date.visibility = View.GONE
        } else {
            rl_doaction.visibility = View.VISIBLE
            tv_more.visibility =View.VISIBLE
            tv_msg.visibility = View.GONE
            headerView.rel_add_square.visibility = View.GONE
            headerView.tv_user_follow_tips.text = "TA喜欢的"
            headerView.tv_user_fans_tips.text = "喜欢TA的"
            deletePic = false
            addVistor()
        }

        getUserInfo()
        getUserFollowAndFansandVistor()

        setAudioListener()
    }

    private fun setAudioListener(){
        mAudioMedio.setmAudioListener(object: AudioPlayListener {
            override fun onPrepared(var1: Int) {

            }

            override fun onBufferingUpdate(var1: Int) {

            }

            override fun onInfo(var1: Int, var2: Int) {
                when (var1) {
                    AudioPlayUtils.MEDIA_INFO_STATE_CHANGED_PAUSED ->{
                        playSquare?.let {
                            it.isPlaying = false
                            mSquares[playIndex] = it
                            squareAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }

            override fun onCompletion() {
                playSquare?.let {
                    it.isPlaying = false
                    mSquares[playIndex] = it
                    squareAdapter.notifyDataSetChanged()
                }
            }
        })

        squareAdapter.setOnSquareAudioToggleClick { position, square ->
            playSquare = square
            playSquare?.let {
                if(!it.isPlaying){
                    it.isPlaying = true
                    mSquares[position]= it
                    if(playIndex>=0&&playIndex!=position){
                        mSquares[playIndex].isPlaying = false
                    }
                    squareAdapter.notifyDataSetChanged()
                    playIndex = position
                }
            }

            var proxyUrl =  getProxyUrl(this,square.sVoiceUrl)
            mAudioMedio.singleAudioPlay(proxyUrl)
        }
    }

    override fun onPause() {
        super.onPause()
        mAudioMedio.onDestoryAudio()
        if(playIndex!=-1){
            playSquare?.let {
                it.isPlaying = false
                mSquares[playIndex] = it
                squareAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mAudioMedio.onDestoryAudio()
    }

    private fun createGroupName(){
        Request.doToUserAnonyMousGroup(getLoginToken(),id,2).request(this,false,success = { msg, jsonObject->
            jsonObject?.let {
                Log.i("createGroupName","json=${it.sId}---sId----${it.iTalkUserid}")
            }
        }){code,msg->
            Log.i("createGroupName","fail${msg}")//保存失败
        }

    }

    private fun setTitleBgAlpha(alpha: Int) {
        colorDrawable.alpha = alpha
        rl_title.backgroundDrawable = colorDrawable
        tv_title_nick.alpha = alpha / 255f
        if (alpha > 128) {
            tv_msg.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.mycenter_edit_orange, 0)
            tv_more.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_more_orange, 0)
            tv_back.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.navigation_back_white, 0)
            immersionBar.statusBarDarkFont(true).init()
        } else {
            tv_msg.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.mycenter_edit, 0)
            tv_more.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_more_white, 0)
            tv_back.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.navigation_back_white, 0)
            immersionBar.statusBarDarkFont(false).init()
        }
    }

    /**
     * 展示约会信息
     */
    private fun setDateInfo(myAppointment: MyAppointment?) {
        headerView.tv_datetype_name.text = Const.dateTypes[myAppointment?.iAppointType!!.toInt() - 1]
        if (myAppointment.iAppointType == 5) {
            var drawable = ContextCompat.getDrawable(context, R.mipmap.invitation_nolimit_feed)
            headerView.tv_datetype_name.setCompoundDrawables(null, drawable, null, null)
            headerView.tv_datetype_name.setCompoundDrawablePadding(dip(3))
        } else {
            var index = myAppointment?.iAppointType!!.toInt() - 1
            if(index !=Const.dateTypesBig.size){
                var drawable = ContextCompat.getDrawable(context, Const.dateTypesBig[index])
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())// 设置边界
                headerView.tv_datetype_name.setCompoundDrawablePadding(dip(3))
                headerView.tv_datetype_name.setCompoundDrawables(null, drawable, null, null)
            }
        }

        var sb = StringBuffer()
        if (!myAppointment.iAge.toString().isNullOrEmpty()) {
            if (myAppointment.iAge != null) {
                sb.append("${myAppointment.iAge}岁")
            }
        }

        if (!myAppointment.iHeight.toString().isNullOrEmpty()) {
            if (myAppointment.iHeight!=null) {
                if(myAppointment.iHeight!!.toInt() > 0){
                    if(sb.length>0){
                        sb.append("·")
                    }
                    sb.append("${myAppointment.iHeight}cm")
                }
            }
        }

//        if (!myAppointment.iWeight.toString().isNullOrEmpty()) {
//            if (!myAppointment.iWeight.toString().equals("0")) {
//                sb.append("·${myAppointment.iWeight}kg")
//            }
//        }

        if (!sb.toString().isNullOrEmpty()) {
            headerView.tv_sub_title.text = sb.toString()
            headerView.tv_sub_title.visibility = View.VISIBLE
        } else {
            headerView.tv_sub_title.visibility = View.GONE
        }

        var time = converTime(myAppointment.dEndtime)
        headerView.tv_time_long.text = "倒计时·${time}"

        headerView.tv_self_address.text = "约会地点·${myAppointment.sPlace}"

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
                val info = UserInfo(data.accountId, data.name, Uri.parse("${data.picUrl}"))
                RongIM.getInstance().refreshUserInfoCache(info)
                tv_title_nick.text = it.name
                headerView.iv_bg.showBlur(it.picUrl)

//                headerView.headView.hierarchy = getHierarchy(it.sex.toString())
                headerView.headView.setImageURI(it.picUrl)

                Log.i("minefragment","个人中心头像url=${it.picUrl}")

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
                    }
                }

                headerView.tv_sex.isSelected = TextUtils.equals("0", it.sex)
                it.age?.let {
                    if (it.toInt() <= 0) {
                        headerView.tv_sex.text = ""
                        headerView.tv_user_date_sex.text = ""
                    } else {
                        headerView.tv_sex.text = it
                        headerView.tv_user_date_sex.text = it
                    }
                }

//                if (TextUtils.equals("0", it.sex)) {
                if(TextUtils.equals(id,CustomerServiceId)||TextUtils.equals(id,CustomerServiceWomenId)){
                    headerView.img_other_auther.visibility = View.GONE
                    headerView.img_date_auther.visibility = View.GONE
                    headerView.img_official.visibility = View.VISIBLE
                    headerView.img_date_auther.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.official_iconnew)
                }else{
                    if (TextUtils.equals("0", it.screen) || TextUtils.equals("3", it.screen) || it.screen.isNullOrEmpty()) {
                        headerView.img_other_auther.visibility = View.GONE
                        headerView.img_date_auther.visibility = View.GONE
                        headerView.img_other_auther.backgroundDrawable=ContextCompat.getDrawable(context,R.mipmap.renzheng_big)
                        headerView.img_date_auther.backgroundDrawable=ContextCompat.getDrawable(context,R.mipmap.renzheng_small)
                        if (TextUtils.equals("3", it.screen)) {
                            headerView.tv_other_auther_sign.visibility = View.GONE
                        } else {
                            headerView.tv_other_auther_sign.visibility = View.GONE
                        }
                    } else if (TextUtils.equals("1", data.screen)) {
                        headerView.img_other_auther.visibility = View.VISIBLE
                        headerView.img_date_auther.visibility = View.VISIBLE
                        headerView.tv_other_auther_sign.visibility = View.GONE

                        headerView.img_other_auther.backgroundDrawable=ContextCompat.getDrawable(context,R.mipmap.video_big)
                        headerView.img_date_auther.backgroundDrawable=ContextCompat.getDrawable(context,R.mipmap.video_small)
                    }

                    if(TextUtils.equals("0",it.isValid)||TextUtils.equals("2",it.isValid)){
                        headerView.img_other_auther.visibility = View.GONE
                        headerView.img_date_auther.visibility = View.VISIBLE
                        headerView.rl_prompt.visibility = View.VISIBLE
                        headerView.img_official.visibility = View.VISIBLE
                        headerView.img_official.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.official_forbidden_icon)
                        headerView.img_date_auther.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.official_forbidden_icon)


                        if(TextUtils.equals(getLocalUserId(), id)){
                            headerView.rl_prompt.visibility = View.GONE
                            headerView.img_official.visibility = View.GONE
                        }else{
                            var lp = RelativeLayout.LayoutParams(headerView.rl_headView.layoutParams)
                            lp?.let {
                                lp.topMargin = resources.getDimensionPixelOffset(R.dimen.height_120)
                                lp.leftMargin = resources.getDimensionPixelOffset(R.dimen.margin16dp)
                                lp.rightMargin = resources.getDimensionPixelOffset(R.dimen.margin_6)
                            }
                            headerView.rl_headView.layoutParams = lp
                        }
                    }
                }

                headerView.img_other_auther.setOnClickListener {
                    toast(getString(R.string.string_auth))
                }

                var drawable = getLevelDrawable(it.userclassesid.toString(),this)
                headerView.tv_vip.backgroundDrawable = drawable
                headerView.tv_date_vip.backgroundDrawable = drawable

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

                if(mTags.size==0){
                    headerView.rv_tags.visibility = View.GONE
                }else{
                    headerView.rv_tags.visibility = View.VISIBLE
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
                        //
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
                    if(deletePic){
                        myImageAdapter.notifyDataSetChanged()
                        headerView.rv_my_images.visibility = View.VISIBLE
                    }else{
                        headerView.rv_my_images.visibility = View.GONE
                    }
                }

                squareAdapter.setUserInfo(mData!!)
                getTrendData()

                if (it.appointment != null) {
                    headerView.rl_userinfo_date.visibility = View.VISIBLE
                    headerView.date_headView.setImageURI(it.picUrl)
                    headerView.tv_name.text = it.name
                    headerView.tv_user_date_sex.isSelected = TextUtils.equals("0", it.sex)
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
                        tv_like.visibility = View.VISIBLE
                        tv_like.text = resources.getString(R.string.string_like)
                        tv_like.backgroundResource = R.drawable.shape_20r_ff6
                        tv_like.textColor = ContextCompat.getColor(context, R.color.white)

                        tv_like.setPadding(resources.getDimensionPixelSize(R.dimen.padding_30), resources.getDimensionPixelSize(R.dimen.margin_12), resources.getDimensionPixelSize(R.dimen.padding_30), resources.getDimensionPixelSize(R.dimen.margin_12))

                    }
                }
            }
        }) { code, msg ->
            if(code==2){
                toast(msg)
                finish()
            }
            mSwipeRefreshLayout.isRefreshing = false
        }
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
        if(deletePic){
            headerView.rv_my_images.visibility = View.VISIBLE
            if(MAXPICS!=mImages.size){
                mImages.add(AddImage("res:///" + R.mipmap.ic_add_v2bg, 1))
            }
        }
        myImageAdapter.notifyDataSetChanged()
    }

    /**
     * 照片墙
     */
    private fun setPicsWall(userData: UserData) {
        mPicsWall.clear()
        if (!TextUtils.equals("null", userData.userpics)) {
            if (!userData.userpics.isNullOrEmpty()) {
                userData.userpics?.let {
                    val images = it.split(",")
                    images.forEach {
                        mPicsWall.add(AddImage(it))
                    }
                }
            }
        }
        if(deletePic){
            mPicsWall.add(AddImage("res:///" + R.mipmap.ic_add_v2bg, 1))
        }
    }

    private fun getTrendData() {
        Request.getMySquares(userId, id, 0, pageNum).request(this, success = { _, data ->
            mSwipeRefreshLayout.isRefreshing = false//15717
            if (pageNum == 1) {
                playIndex = -1
                mAudioMedio.onClickStop()
                mSquares.clear()
            }
            if (data?.list?.results == null || data.list?.results?.isEmpty()) {
                if (pageNum > 1) {
                    mSwipeRefreshLayout.setLoadMoreText("没有更多了")
                    pageNum--
                } else {
                    mSwipeRefreshLayout.setLoadMoreText("暂无数据")
                }
            } else {
                data.list?.let {
                    data.list.results?.let { it1 -> mSquares.addAll(it1) }
                    squareAdapter.notifyDataSetChanged()
                }
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
            Request.addBlackList(userId, id,2).request(this) { _, _ ->
                CustomToast.showToast(getString(R.string.string_blacklist_toast))
                mData?.iIsInBlackList = 1
            }
        }
    }

    //移除黑名单
    private fun removeBlackList(){
        Request.removeBlackList(userId,id,2).request(this){msg,jsonPrimitive->
            CustomToast.showToast(msg.toString())
            mData?.iIsInBlackList = 0
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
            RongIM.getInstance().startConversation(this, Conversation.ConversationType.PRIVATE, id, name)
        }
    }

    private fun signUpDate(myAppointment: MyAppointment) {
        Request.queryAppointmentPoint(userId,"${myAppointment.iAppointUserid}").request(this, false, success = { msg, data ->
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
            tv_like.visibility = View.VISIBLE

            tv_like.text = resources.getString(R.string.string_like)
            tv_like.backgroundResource = R.drawable.shape_20r_ff6
            tv_like.textColor = ContextCompat.getColor(context, R.color.white)

            tv_like.setPadding(resources.getDimensionPixelSize(R.dimen.padding_30), resources.getDimensionPixelSize(R.dimen.margin_12), resources.getDimensionPixelSize(R.dimen.padding_30), resources.getDimensionPixelSize(R.dimen.margin_12))
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK){
            if(requestCode == 22||requestCode==0){
                onRefresh()
            }else if (requestCode == 8 && data != null) {//选择图片
                val result: ArrayList<String> = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT)
                var localImages = ArrayList<String>()
                result.forEach {
                    localImages.add(it)///storage/emulated/0/Huawei/MagazineUnlock/magazine-unlock-01-2.3.1104-_9E598779094E2DB3E89366E34B1A6D50.jpg
                }
                updateImages(localImages)

//                val path = data.getStringExtra(SelectPhotoDialog.PATH)
//                updateImages(path)
//                var param: BLBeautifyParam = BLBeautifyParam()//data.imgUrl.replace("file://","")
//                param.index = 0
//                param.type = Const.User.SELECTIMAGE
//                param.images.add(path)
//                startActivityForResult<BLBeautifyImageActivity>(BLBeautifyParam.REQUEST_CODE_BEAUTIFY_IMAGE, BLBeautifyParam.KEY to param);
            }else if (requestCode == BLBeautifyParam.REQUEST_CODE_BEAUTIFY_IMAGE && data != null) {
//                var param = data.getParcelableExtra<BLBeautifyParam>(BLBeautifyParam.RESULT_KEY);
//                updateImages(param.images[param.index])
            }else if(requestCode == 3){
                pageNum=1
                getTrendData()
            }
        }
    }

    private fun updateImages(mImages:ArrayList<String>) {
        val userData = mData ?: return
        dialog()
        Flowable.fromIterable(mImages).subscribeOn(Schedulers.io()).flatMap {
            //压缩
            val b = BitmapUtils.compressImageFile(it)
            Flowable.just(b)
        }.flatMap {
            Request.uploadFile(it)
        }.toList().toFlowable().flatMap {
            val sb = StringBuilder()
            it.forEach {
                sb.append(it).append(",")
            }
            if (sb.isNotEmpty()) {
                sb.deleteCharAt(sb.length - 1)
            }
            Flowable.just(sb.toString())
        }.flatMap {
            if (userData.userpics.isNullOrEmpty()) {
                userData.userpics = it
            } else {
                userData.userpics = userData.userpics + "," + it
            }
            mData = userData
            Request.updateUserInfo(userData)
        }.request(this) { _, _ ->
            refreshImages(userData)
        }
    }

    override fun onRefresh() {
        pageNum = 1
        getUserInfo()
        getUserFollowAndFansandVistor()
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
