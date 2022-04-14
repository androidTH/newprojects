package com.d6.android.app.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.TextPaint
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.amap.api.location.AMapLocationClient
import com.d6.android.app.R
import com.d6.android.app.activities.*
import com.d6.android.app.adapters.DanmaKuViewHolder
import com.d6.android.app.adapters.DateCardAdapter
import com.d6.android.app.adapters.DateWomanCardAdapter
import com.d6.android.app.base.BaseFragment
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.dialogs.*
import com.d6.android.app.extentions.request
import com.d6.android.app.models.*
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.LOGIN_FOR_POINT_NEW
import com.d6.android.app.utils.Const.User.IS_FIRST_FAST_CLICK
import com.d6.android.app.utils.Const.User.IS_FIRST_SHOW_FINDDIALOG
import com.d6.android.app.utils.Const.User.IS_FIRST_SHOW_FINDLASTDAYNOTICEDIALOG
import com.d6.android.app.utils.Const.User.IS_FIRST_SHOW_FINDNOTICEDIALOG
import com.d6.android.app.utils.Const.User.USER_ADDRESS
import com.d6.android.app.utils.Const.User.USER_PROVINCE
import com.d6.android.app.widget.DanMuImageWare
import com.d6.android.app.widget.diskcache.DiskFileUtils
import com.d6.android.app.widget.gallery.DSVOrientation
import com.d6.android.app.widget.gallery.transform.ScaleTransformer
import com.d6.android.app.widget.gift.CustormAnim
import com.d6.android.app.widget.gift.GiftControl
import com.d6.android.app.widget.gift.GiftModel
import com.tbruyelle.rxpermissions2.RxPermissions
import io.rong.imageloader.core.ImageLoader
import io.rong.imkit.RongIM
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.activity_publish_find_date.*
import kotlinx.android.synthetic.main.fragment_date.*
import kotlinx.android.synthetic.main.fragment_date.ll_finddate03
import kotlinx.android.synthetic.main.fragment_date.rl_homefindtop
import kotlinx.android.synthetic.main.fragment_date.sv_finddate03
import kotlinx.android.synthetic.main.fragment_date.tv_03
import kotlinx.android.synthetic.main.fragment_date.tv_city
import kotlinx.android.synthetic.main.fragment_date.tv_finddate_02
import master.flame.danmaku.controller.IDanmakuView
import master.flame.danmaku.danmaku.model.BaseDanmaku
import master.flame.danmaku.danmaku.model.DanmakuTimer
import master.flame.danmaku.danmaku.model.IDanmakus
import master.flame.danmaku.danmaku.model.IDisplayer
import master.flame.danmaku.danmaku.model.android.AndroidDisplayer
import master.flame.danmaku.danmaku.model.android.DanmakuContext
import master.flame.danmaku.danmaku.model.android.Danmakus
import master.flame.danmaku.danmaku.model.android.ViewCacheStuffer
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser
import me.jessyan.autosize.AutoSizeConfig
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.startActivityForResult
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.textColor
import java.io.InputStream
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

/**
 * 约会
 */
class DateFragment : BaseFragment(), BaseRecyclerAdapter.OnItemClickListener {

    override fun onItemClick(view: View?, position: Int) {
        if (view?.id == R.id.cardView||view?.id==R.id.rl_small_mendate_layout||view?.id==R.id.imageViewbg||view?.id==R.id.rl_big_mendate_layout) {
            if(mDates!=null&&mDates.size>position){
                val dateBean = mDates[position]
                startActivity<UserInfoActivity>("id" to "${dateBean.accountId}")
            }
        } else if (view?.id == R.id.tv_perfect_userinfo) {
            mUserInfoData?.let {
                startActivityForResult<MyInfoActivity>(Const.DOUPDATEUSERINFOCODE, "data" to it, "images" to mImages)
            }
        }
        hideRedHeartGuide()
    }

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private val sex by lazy {
        SPUtils.instance().getString(Const.User.USER_SEX)
    }

    private val heardPic by lazy {
        SPUtils.instance().getString(Const.User.USER_HEAD)
    }

    private var localLoveHeartNums = SPUtils.instance().getInt(Const.User.USERLOVE_NUMS, 0)
    private var sendLoveHeartNums = 1
    private var mTotalPages = -1

    private val locationClient by lazy {
        AMapLocationClient(activity)
    }

    private var mShowCardLastTime = SPUtils.instance().getString(Const.LASTDAYTIME)

    private val lastTime by lazy {
        SPUtils.instance().getString(Const.LASTTIMEOFPROVINCEINFIND+getLocalUserId())
    }

    private val cityJson by lazy {
        DiskFileUtils.getDiskLruCacheHelper(context).getAsString(Const.PROVINCE_DATAOFFIND+getLocalUserId())
    }

    private var IsNotFastClick: Boolean = false
    private var pageNum = 1
    private var DANMU_pageNum = 1
    private var mDates = ArrayList<FindDate>()
    private var scrollPosition = 0
    private var province = Province(Const.LOCATIONCITYCODE, "不限/定位")
    //礼物
    private var giftControl: GiftControl? = null
    private var mDesc = ""

    override fun contentViewId() = R.layout.fragment_date

    override fun onFirstVisibleToUser() {
        mRecyclerView.setOrientation(DSVOrientation.HORIZONTAL)
        setAdapter()
        mRecyclerView.setSlideOnFling(false)
        mRecyclerView.setItemTransitionTimeMillis(150)
        mRecyclerView.setItemTransformer(ScaleTransformer.Builder()
                .setMinScale(1.0f)
                .setMaxScale(1.0f)
                .build())

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    scrollPosition = mRecyclerView.currentItem + 1
                    if ((mDates.size - scrollPosition) == 0) {
                        pageNum++
                        if (pageNum <= mTotalPages) {
                            setLoadingShow()
                            getData(city, userclassesid, agemin, agemax)
                        }
                    }

                    if (mDates.size > 0) {
                        var findDate = mDates.get(scrollPosition - 1)
                        if (TextUtils.equals(findDate.accountId, getLocalUserId())) {
                            ll_bottom.visibility = View.GONE
                        }else{
                            ll_bottom.visibility = View.VISIBLE
                        }
                        if(TextUtils.equals(sex, "1")){
                            clearDanMu()
                            if((scrollPosition - 1) != 4 || !TextUtils.equals(findDate.accountId, getLocalUserId())){
                                getFindReceiveLoveHeart("${findDate.accountId}","2")
                            }
                        }
                    }
                }
            }
        })

//        iv_back.setOnClickListener {
//            activity.finish()
//        }

        fb_liwu.setOnClickListener {
            activity.isAuthUser() {
                showGiftDialog()
            }
        }

        tv_city.setOnClickListener {
            activity.isCheckOnLineAuthUser(this, userId) {
                showArea(it)
            }
            hideRedHeartGuide()
        }

        tv_xingzuo.setOnClickListener {
            activity.isCheckOnLineAuthUser(this, userId) {
                showConstellations(it)
            }
            hideRedHeartGuide()
        }

        tv_type.setOnClickListener {
            activity.isCheckOnLineAuthUser(this, userId) {
                showAges(it)
            }
            hideRedHeartGuide()
        }

//        btn_like.setOnClickListener {
//            doNextCard()
//            hideRedHeartGuide()
//        }

        fb_heat_like.setOnClickListener {
            activity.isAuthUser() {
                if (TextUtils.equals(getUserSex(), "0")) {
                    if (localLoveHeartNums > 0) {
                        if (sendLoveHeartNums <= localLoveHeartNums) {
                            sendLoveHeartNums = sendLoveHeartNums + 1
                            addGiftNums(1, false, false, "")
                            IsNotFastClick = is500sFastClick()
                            VibrateHelp.Vibrate(activity, VibrateHelp.time50)
                        } else {
                            var mSendRedHeartEndDialog = SendRedHeartEndDialog()
                            mSendRedHeartEndDialog.show(childFragmentManager, "redheartendDialog")
                        }
                    } else {
                        var mSendRedHeartEndDialog = SendRedHeartEndDialog()
                        mSendRedHeartEndDialog.show(childFragmentManager, "redheartendDialog")
                    }
                }else{
                    if (localLoveHeartNums > 0) {
                        if (sendLoveHeartNums <= localLoveHeartNums) {
                            sendLoveHeartNums = sendLoveHeartNums + 1
                            addGiftNums(10, false, true, "")
                            IsNotFastClick = is500sFastClick()
                            VibrateHelp.Vibrate(activity, VibrateHelp.time50)
                        } else {
                            var mSendRedHeartEndDialog = SendRedHeartEndDialog()
                            mSendRedHeartEndDialog.show(childFragmentManager, "redheartendDialog")
                        }
                    }else{
                        var mSendLoveHeartDialog = SendLoveHeartDialog()
                        mDates.get(mRecyclerView.currentItem)
                        if (mDates.size > mRecyclerView.currentItem) {
                            var findDate = mDates.get(mRecyclerView.currentItem)
                            mSendLoveHeartDialog.arguments = bundleOf("userId" to "${findDate.accountId}")
                        }
                        mSendLoveHeartDialog.setDialogListener { p, s ->
                            addGiftNums(p, false, true, "${s}")
                        }
                        mSendLoveHeartDialog.show(childFragmentManager, "sendloveheartDialog")
                    }
//                    if(!isFastClick()) {
//
//                    }
                }
                hideRedHeartGuide()
            }
        }

        fb_heat_like.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                activity.isAuthUser() {
                    var mSendLoveHeartDialog = SendLoveHeartDialog()
                    if (mDates.size > mRecyclerView.currentItem) {
                        var findDate = mDates.get(mRecyclerView.currentItem)
                        mSendLoveHeartDialog.arguments = bundleOf("userId" to "${findDate.accountId}")
                    }
                    mSendLoveHeartDialog.setDialogListener { p, s ->
                        addGiftNums(p, false, true,"${s}")
                    }
                    mSendLoveHeartDialog.show(childFragmentManager, "sendloveheartDialog")
                    hideRedHeartGuide()
                }
                return true
            }
        })

        fb_find_chat.setOnClickListener {
            //            activity.isNoAuthToChat("5") {
            scrollPosition = mRecyclerView.currentItem
            if (mDates.size > scrollPosition) {
                var findChat = mDates.get(scrollPosition)
                findChat?.let {
                    val name = it.name
                    showDatePayPointDialog(name, it.accountId.toString())
                }
            }
//            }
        }

//        fb_unlike.setOnClickListener {
//            if (mDates.isNotEmpty()) {
//                scrollPosition = mRecyclerView.currentItem - 1
//                if (scrollPosition >= 0) {
//                    mRecyclerView.smoothScrollToPosition(scrollPosition)
//                }
//            }
//            hideRedHeartGuide()
//        }

        tv_mycard.setOnClickListener {
//            iv_mycade_newnotice.visibility = View.GONE
            SPUtils.instance().put(IS_FIRST_SHOW_FINDLASTDAYNOTICEDIALOG + getLocalUserId(),System.currentTimeMillis()).apply()
            startActivity<MyCardActivity>()
//            startActivity<D6LoveHeartListActivity>()
        }

        rl_bangdanpage.setOnClickListener{
            startActivity<D6LoveHeartListActivity>()
        }

        rl_groupchat.setOnClickListener {
            startActivity<FindGroupListActivity>()
        }

        root_find.setOnClickListener {
            hideRedHeartGuide()
        }

//        showDialog()
        setLoadingShow()
        getData(city, userclassesid, agemin, agemax)
        checkLocation()

        mPopupAges = AgeSelectedPopup.create(activity)
                .setDimView(rl_date)
                .apply()

        mPopupConstellation = ConstellationSelectedPopup.create(activity)
                .setDimView(rl_date)
                .apply()

        mPopupArea = AreaSelectedPopup.create(activity)
                .setDimView(rl_date)
                .apply()

        getProvinceData()

        initGift()

        loading_headView.setImageURI(heardPic)

        initDanMu()

        getPeoples()
    }

    private fun showGiftDialog(){
        var mSelectGiftListDialog = SelectGiftListDialog()
        if (mDates.size > mRecyclerView.currentItem) {
            var findDate = mDates.get(mRecyclerView.currentItem)
            mSelectGiftListDialog.arguments= bundleOf("titleStype" to 4,"receiveUserId" to "${findDate.accountId}")
            mSelectGiftListDialog.setDialogListener { p, s ->
                RongIM.getInstance().startConversation(activity, Conversation.ConversationType.PRIVATE, "${findDate.accountId}", "${findDate.name}")
            }
        }
        mSelectGiftListDialog.show(childFragmentManager,"gift")
    }

    private var mDanmakuContext: DanmakuContext? = null
    private var mParser: BaseDanmakuParser? = null

    private fun initDanMu() {
        val maxLinesPair = HashMap<Int, Int>()
        maxLinesPair[BaseDanmaku.TYPE_SCROLL_RL] = 3 // 滚动弹幕最大显示5行
        // 设置是否禁止重叠
        var overlappingEnablePair = HashMap<Int, Boolean>()
        overlappingEnablePair[BaseDanmaku.TYPE_SCROLL_RL] = true
        overlappingEnablePair[BaseDanmaku.TYPE_FIX_TOP] = true
        mDanmakuContext = DanmakuContext.create()
        mDanmakuContext?.let {
            it.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_NONE, 3f).setDuplicateMergingEnabled(false).setScrollSpeedFactor(1.5f).setScaleTextSize(1.2f)
                    .setCacheStuffer(object : ViewCacheStuffer<DanmaKuViewHolder>() {
                        override fun onBindViewHolder(viewType: Int, viewHolder: DanmaKuViewHolder?, danmaku: BaseDanmaku?, displayerConfig: AndroidDisplayer.DisplayerConfig?, paint: TextPaint?) {
                            if (paint != null) {
                                try {
                                    danmaku?.let {
                                        viewHolder?.mText?.paint?.set(paint)
                                        viewHolder?.mText?.text = danmaku.text
                                        viewHolder?.mText?.setTextColor(danmaku.textColor)
                                        viewHolder?.mText?.setTextSize(TypedValue.COMPLEX_UNIT_PX, danmaku.textSize)
                                        if (danmaku.tag != null) {
                                            val imageWare = danmaku.tag as DanMuImageWare
                                            if (imageWare != null) {
                                                if (imageWare.bitmap != null) {
                                                    viewHolder?.mIcon?.setImageBitmap(imageWare.bitmap)
                                                } else {
                                                    viewHolder?.mIcon?.setImageResource(R.mipmap.default_head)
                                                }
                                            }
                                        }
                                    }
                                }catch (e:Exception){
                                    e.printStackTrace()
                                }
                            }
                        }

                        override fun onCreateViewHolder(viewType: Int): DanmaKuViewHolder {
                            return DanmaKuViewHolder(View.inflate(activity, R.layout.layout_view_cache, null))
                        }

                        override fun prepare(danmaku: BaseDanmaku?, fromWorkerThread: Boolean) {
                            try{
                                danmaku?.let {
                                    if (it.isTimeOut()) {
                                        return
                                    }
                                    if(danmaku.tag!=null){
                                        var imageWare: DanMuImageWare? = danmaku.tag as DanMuImageWare
                                        ImageLoader.getInstance().displayImage(imageWare!!.getImageUri(), imageWare)
                                    }
                                }
                            }catch (e:Exception){
                                e.printStackTrace()
                            }
                        }

                        override fun releaseResource(danmaku: BaseDanmaku?) {
                            super.releaseResource(danmaku)
                        }

                    }, null)
                    .setMaximumLines(maxLinesPair)
                    .preventOverlapping(overlappingEnablePair)
        }
        if (sv_danmaku != null) {
            mParser = createParser(null)
            sv_danmaku.setCallback(object : master.flame.danmaku.controller.DrawHandler.Callback {
                override fun updateTimer(timer: DanmakuTimer) {

                }

                override fun drawingFinished() {

                }

                override fun danmakuShown(danmaku: BaseDanmaku) {
                    //                    Log.d("DFM", "danmakuShown(): text=" + danmaku.text);
                }

                override fun prepared() {
                    sv_danmaku.start()
                }
            })
            sv_danmaku.setOnDanmakuClickListener(object : IDanmakuView.OnDanmakuClickListener {

                override fun onDanmakuClick(danmakus: IDanmakus): Boolean {
                    Log.d("DFM", "onDanmakuClick: danmakus size:" + danmakus.size())
                    val latest = danmakus.last()
                    if (null != latest) {
                        startActivity<UserInfoActivity>("id" to "${latest.userId}")
                        Log.d("DFM", "onDanmakuClick: text of latest danmaku:" + latest!!.text)
                        return true
                    }
                    return false
                }

                override fun onDanmakuLongClick(danmakus: IDanmakus): Boolean {
                    return false
                }

                override fun onViewClick(view: IDanmakuView): Boolean {
                    return false
                }
            })
            sv_danmaku.prepare(mParser, mDanmakuContext)
            sv_danmaku.showFPS(false)
            sv_danmaku.enableDanmakuDrawingCache(true)
        }
    }

    private fun createParser(stream: InputStream?): BaseDanmakuParser {
        return object : BaseDanmakuParser() {

            override fun parse(): Danmakus {
                return Danmakus()
            }
        }
    }

    private fun initGift() {
        giftControl = GiftControl(context)
        giftControl?.let {
            it.setGiftLayout(ll_gift_parent, 1)
                    .setHideMode(false)
                    .setCustormAnim(CustormAnim())
            it.setmGiftAnimationEndListener {
                if (mDates.isNotEmpty()) {
                    addFollow(it)
//                    if(it<){
//                        addFollow(it)
//                    }else{
//                        var mSendRedHeartEndDialog = SendRedHeartEndDialog()
//                        mSendRedHeartEndDialog.show(childFragmentManager, "redheartendDialog")
//                    }
                }

                var clickNums = getIsNotFirstDialog()
                var mIsFirstFastClick = getIsNotFirstFastClick()
                if (clickNums == 0 && !IsNotFastClick) {
                    var mRedHeartGuideDialog = RedHeartGuideDialog()
                    mRedHeartGuideDialog.show(childFragmentManager, "redheartguideDialog")
                    ++clickNums
                    SPUtils.instance().put(IS_FIRST_SHOW_FINDDIALOG + getLocalUserId(), clickNums).apply()
                } else if (clickNums == 1 && !IsNotFastClick) {
                    if(TextUtils.equals(getUserSex(), "0")){
                        tv_redheart_guide.visibility = View.GONE
                        tv_redheart_guide.text = "连击可以送出多个喜欢"
                        ++clickNums
                        SPUtils.instance().put(IS_FIRST_SHOW_FINDDIALOG + getLocalUserId(), clickNums).apply()
                    }
//                    Flowable.interval(0, 1, TimeUnit.SECONDS).defaultScheduler().subscribe(diposable)
                } else if (it >= 3 && !mIsFirstFastClick) {
                    if(TextUtils.equals(getUserSex(), "0")){
                        tv_redheart_guide.visibility = View.GONE
                        tv_redheart_guide.text = "长按可快捷选择520、1314个喜欢"
                        SPUtils.instance().put(IS_FIRST_FAST_CLICK + getLocalUserId(), true).apply()
                    }
//                    Flowable.interval(0, 1, TimeUnit.SECONDS).defaultScheduler().subscribe(diposable)
                }
            }
        }
    }

    private fun hideRedHeartGuide() {
        tv_redheart_guide.visibility = View.GONE
    }

    //连击礼物数量
    private fun addGiftNums(giftnum: Int, currentStart: Boolean = false, JumpCombo: Boolean = false,desc:String) {
        if (giftnum == 0) {
            return
        } else {
            giftControl?.let {
                //这里最好不要直接new对象
                var giftModel = GiftModel()
                giftModel.setGiftId("礼物Id").setGiftName("礼物名字").setGiftCount(giftnum).setGiftPic("")
                        .setSendUserId("1234").setSendUserName("吕靓茜").setSendUserPic("").setSendGiftTime(System.currentTimeMillis())
                        .setCurrentStart(currentStart)
                mDesc = desc
                if (currentStart) {
                    giftModel.setHitCombo(giftnum)
                }
                if (JumpCombo) {
                    giftModel.setJumpCombo(giftnum)
                }
                it.loadGift(giftModel)
                Log.d("TAG", "onClick: " + it.getShowingGiftLayoutCount())
            }
            doAnimation()
        }
    }

    fun refresh() {
//        showDialog()
        tv_redheart_guide.visibility = View.GONE
        pageNum = 1
        if (pageNum == 1) {
            mDates.clear()
        }
        clearDanMu()
        setLoadingShow()
        getData(city, userclassesid, agemin, agemax)
    }

    fun setAdapter() {
        if (TextUtils.equals(sex, "0")) {
            sv_danmaku.visibility = View.GONE
            mRecyclerView.adapter = DateCardAdapter(mDates)
        } else {
            mRecyclerView.adapter = DateWomanCardAdapter(mDates)
        }
        (mRecyclerView.adapter as BaseRecyclerAdapter<*>).setOnItemClickListener(this)
    }

    private fun checkLocation() {
        RxPermissions(activity).request(Manifest.permission.ACCESS_COARSE_LOCATION).subscribe {
            if (it) {
                startLocation()
                SPUtils.instance().put(Const.User.ISNOTLOCATION,false).apply()
            }else{
                SPUtils.instance().put(Const.User.ISNOTLOCATION,true).apply()
            }
        }

        locationClient.setLocationListener {
            if (it != null) {
                locationClient.stopLocation()
                mLat = it.latitude.toString()
                mLon = it.longitude.toString()
                SPUtils.instance().put(USER_PROVINCE, it.province).apply()
                SPUtils.instance().put(USER_ADDRESS, it.city).apply() //it.city
                getUserLocation(it.city,it.province,it.country,"${it.latitude}","${it.longitude}")
            }
        }
    }

    private fun startLocation() {
        locationClient.stopLocation()
        locationClient.startLocation()
    }

    /**
     * 经纬度提交给服务端
     */
    private fun getUserLocation(city:String,sProvince:String,sCountry:String,lat:String,lon:String){
        Request.updateUserPosition(getLocalUserId(),sProvince,sCountry,city,lat,lon).request(this,false,success={_,data->
        })
    }


    private fun setLoadingShow(){
        rl_loading.visibility = View.VISIBLE
        mRecyclerView.visibility = View.GONE
        find_waveview.start()
    }
    /**
     * 搜索
     */
    fun getData(city: String = "", userclassesid: String = "", agemin: String = "", agemax: String = "", lat: String = "", lon: String = "") {
        if (mDates.size == 0) {
            tv_main_card_bg_im_id.gone()
            tv_main_card_Bg_tv_id.gone()
//            fb_unlike.gone()
//            btn_like.gone()
            fb_heat_like.gone()
            fb_find_chat.gone()
            if (TextUtils.equals(sex, "1")) {
                sv_danmaku.removeAllDanmakus(true)
                mReceiveLoveHearts.clear()
            }
        }
        Request.findAccountCardListPage(userId, city, "", userclassesid, agemin, agemax, lat, lon, pageNum).request(this) { _, data ->
                rl_loading.visibility = View.GONE
                find_waveview.stop()
                mRecyclerView.visibility = View.VISIBLE
                mTotalPages = data?.list?.totalPage!!
                Log.i("DateFragment", "${data?.list?.totalPage}---${pageNum}")
                if (data?.list?.results == null || data.list.results.isEmpty()) {
                    if (pageNum == 1) {
                        mRecyclerView.visibility = View.GONE
//                    tv_tip.gone()
                        tv_main_card_bg_im_id.visible()
                        tv_main_card_Bg_tv_id.visible()
//                        fb_unlike.gone()
//                        btn_like.gone()
                        fb_heat_like.gone()
                        fb_find_chat.gone()
                    } else {
                        mRecyclerView.visibility = View.VISIBLE
                        tv_main_card_bg_im_id.gone()
                        tv_main_card_Bg_tv_id.gone()
//                        fb_unlike.visible()
//                        btn_like.visible()
                        fb_heat_like.visible()
                        fb_find_chat.visible()
                    }
                } else {
                    mRecyclerView.visibility = View.VISIBLE
                    tv_main_card_bg_im_id.gone()
                    tv_main_card_Bg_tv_id.gone()
//                    fb_unlike.visible()
//                    btn_like.visible()
                    fb_heat_like.visible()
                    fb_find_chat.visible()
                    if (pageNum == 1) {
                        mDates.clear()
                    }

                    data.list.results?.let {
                        mDates.addAll(it)
                        var h = LinkedHashSet<FindDate>(mDates)
                        mDates.clear()
                        mDates.addAll(h.toList())
                    }
                    if (pageNum == 1) {
                        joinInCard()
                        var findDate = mDates.get(0)
                        if (TextUtils.equals(sex, "1")) {
                            getFindReceiveLoveHeart("${findDate.accountId}","http")
                        }
                    }
                    mRecyclerView.adapter.notifyDataSetChanged()
                    if(pageNum!=1){
                        try{
                            scrollPosition = mRecyclerView.currentItem + 1
                            if (mRecyclerView.layoutManager.itemCount > scrollPosition) {
                                mRecyclerView.postDelayed(object : Runnable {
                                    override fun run() {
                                        mRecyclerView.smoothScrollToPosition(scrollPosition)
                                    }
                                }, 200)
                            }
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                    }
                }
            }
    }

    private fun doAnimation() {
        loveheart.showAnimationRedHeart(null)
    }

    fun doNextCard() {
        scrollPosition = mRecyclerView.currentItem + 1
        if (mDates.isNotEmpty() && (mDates.size - scrollPosition) >= 0) {
            if ((mDates.size - scrollPosition) ==0) {
                pageNum++
                setLoadingShow()
                getData(city, userclassesid, agemin, agemax)
            }else{
                mRecyclerView.smoothScrollToPosition(scrollPosition)
            }
        }
    }

    private fun addFollow(giftCount: Int) {
        scrollPosition = mRecyclerView.currentItem
        if (mDates.size > scrollPosition) {
            var findDate = mDates.get(scrollPosition)

            Request.sendLovePoint(getLoginToken(), "${findDate.accountId}", giftCount, 2, "","","${mDesc}").request(this, false, success = { _, data ->
                Log.i("GiftControl", "礼物数量${giftCount}")
                sendLoveHeartNums = 1
                Request.getUserInfo("", getLocalUserId()).request(this, false, success = { _, data ->
                    data?.let {
                        SPUtils.instance().put(Const.User.USERLOVE_NUMS, it.iLovePoint).apply()
                        localLoveHeartNums = it.iLovePoint
                    }
                })

                var mLoveHeartFan = LoveHeartFans("${findDate.accountId}".toInt())
                mLoveHeartFan.sSendUserName = getLocalUserName()
                mLoveHeartFan.sPicUrl = getLocalUserHeadPic()
                mLoveHeartFan.iPoint = giftCount
                mLoveHeartFan.iSenduserid = "${getLocalUserId()}".toInt()

                if(mReceiveLoveHearts.size>0){
                    Log.i("dateFragment","这里了----大于")
                    mReceiveLoveHearts.add(mLoveHeartFan)
                }else{
                    Log.i("dateFragment","这里了----")
                    addDanmaku(mLoveHeartFan,1,false)
                }
                Log.i("dateFragment","数量${mReceiveLoveHearts.size}----")
//                addDanmaku(mLoveHeartFans,false)
            }) { code, msg ->
                sendLoveHeartNums = 1
                if (code == 2 || code == 3) {
                    var mSendRedHeartEndDialog = SendRedHeartEndDialog()
                    mSendRedHeartEndDialog.show(childFragmentManager, "redheartendDialog")
                }else{
                    toast(msg)
                }
            }
//            if(findDate.iIsFans==0){
//                Request.getAddFollow(userId, findDate.accountId.toString()).request(this, false) { s: String?, jsonObject: JsonObject? ->
//                    //toast("$s,$jsonObject")
//                    fb_heat_like.setImageBitmap(BitmapFactory.decodeResource(resources,R.mipmap.center_like_button))
//                    mDates.get(scrollPosition).iIsFans = 1
////                    doAnimation()
////                    doNextCard()
//                    showTips(jsonObject, "", "")
//                }
//            }else{
//                Request.getDelFollow(userId, findDate.accountId.toString()).request(this,true) { s: String?, jsonObject: JsonObject? ->
//                    mDates.get(scrollPosition).iIsFans = 0
//                    fb_heat_like.setImageBitmap(BitmapFactory.decodeResource(resources,R.mipmap.center_like_button))
////                    doNextCard()
//                }
//            }
        }
    }

    private fun showDatePayPointDialog(name: String, id: String) {
        activity.isAuthUser{
            RongIM.getInstance().startConversation(activity, Conversation.ConversationType.PRIVATE, id, name)
//            Request.getApplyStatus(userId, id, 1).request(this, false, success = { msg, jsonObjetct ->
//                jsonObjetct?.let {
//                    var code = it.optInt("code")
//                    if (code != 7) {
//                        RongIM.getInstance().startConversation(activity, Conversation.ConversationType.PRIVATE, id, name)
//                    }
//                }
//            })
//            RongIM.getInstance().startConversation(activity, Conversation.ConversationType.PRIVATE, id, name)
        }
    }

    fun showNotice(){
        Request.getUserInfo("", getLocalUserId()).request(this,false,success = { _, data ->
            data?.let {
                if(getOneDay(SPUtils.instance().getLong(IS_FIRST_SHOW_FINDLASTDAYNOTICEDIALOG + getLocalUserId(), System.currentTimeMillis()))){
                    if(it.iLastDayExposureCount>0){
//                        iv_mycade_newnotice.visibility = View.VISIBLE
                    }else{
//                        iv_mycade_newnotice.visibility = View.GONE
                    }
                }else{
//                    iv_mycade_newnotice.visibility = View.GONE
                }
                tv_mycard.postDelayed(object:Runnable{
                    override fun run() {
                        var flag = SPUtils.instance().getBoolean(IS_FIRST_SHOW_FINDNOTICEDIALOG + getLocalUserId(),false)
                        if(!flag){
//                            iv_mycade_newnotice.visibility = View.VISIBLE
                            SPUtils.instance().put(IS_FIRST_SHOW_FINDNOTICEDIALOG + getLocalUserId(), true).apply()
                        }
                    }
                },300)
            }
        })
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            immersionBar.init()
            immersionBar.navigationBarColor("#FFFFFF")
            sv_danmaku.resume()
        }
    }

    override fun onResume() {
        super.onResume()
        if (sv_danmaku != null && sv_danmaku.isPrepared() && sv_danmaku.isPaused()) {
            sv_danmaku.resume()
        }
        if(mDanMuHandler!=null){
            if(mReceiveLoveHearts!=null&&mReceiveLoveHearts.size>0){
               mDanMuHandler.sendEmptyMessage(0)
            }
        }
        showNotice()
    }

    override fun onPause() {
        super.onPause()
        if (sv_danmaku != null && sv_danmaku.isPrepared()) {
            sv_danmaku.pause()
        }
        if(mDanMuHandler!=null){
           mDanMuHandler.removeCallbacksAndMessages(null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar.destroy()
        locationClient.onDestroy()

        if (sv_danmaku != null) {
            // dont forget release!
            sv_danmaku.release()
        }
    }


    private var mRunnable = Runnable {
        getLatestNews()
    }

    public fun getPeoples(){
//        Request.findAppointmentList(userId, "", "", "${mDefualtSex}", 1).request(this) { _, data ->
//            sv_finddate01.visibility = View.GONE
//            tv_01.text = "速约/觅约/救火/旅行约"
//
//            sv_finddate02.visibility = View.GONE
//            tv_02.text = "已有${data?.iAllAppointCount}人邀约成功"

//            sv_finddate03.visibility = View.GONE
//            tv_03.text = "魅力榜·土豪榜"
//
//            tv_finddate_02.text = "优质私密群等你加入"
//
//            rl_homefindtop.removeCallbacks(mRunnable)
//            rl_homefindtop.postDelayed(mRunnable,2000)
//        }

        sv_finddate03.visibility = View.GONE
        tv_03.text = "魅力榜·土豪榜"

        tv_finddate_02.text = "优质私密群等你加入"

        rl_homefindtop.removeCallbacks(mRunnable)
        rl_homefindtop.postDelayed(mRunnable,2000)
    }

    fun setResetTopInfo(){
        rl_homefindtop.removeCallbacks(mRunnable)
//        Request.findAppointmentList(userId, "", "", "${mDefualtSex}", 1).request(this) { _, data ->
//            sv_finddate01.visibility = View.GONE
//            tv_01.text = "速约/觅约/救火/旅行约"
//
//            sv_finddate02.visibility = View.GONE
//            tv_02.text = "已有${data?.iAllAppointCount}人邀约成功"

//            sv_finddate03.visibility = View.GONE
//            tv_03.text = "魅力榜·土豪榜"
//
//            tv_finddate_02.text = "优质私密群等你加入"
//        }

        sv_finddate03.visibility = View.GONE
        tv_03.text = "魅力榜·土豪榜"

        tv_finddate_02.text = "优质私密群等你加入"
    }

    private lateinit var mFindDateInfo:FindDateInfo
    private fun getLatestNews(){
        Request.queryLatestNews().request(this){ _, data->
            data?.let {
//                var rongGroup_name = it.optString("rongGroup_name")
                mFindDateInfo = it
                if(mFindDateInfo!=null){

//                    if(it.lookabout_picurl.isNotEmpty()){
//                        sv_finddate01.setImageURI(it.lookabout_picurl)
//                        lookAbout()
//                    }
//
//                    if(it.appointment_picurl.isNotEmpty()){
//                        sv_finddate02.setImageURI("${it.appointment_picurl}")
//                        dateCount()
//                    }

                    if(it.userpoint_picUrl.isNotEmpty()){
                        sv_finddate03.setImageURI(it.userpoint_picUrl)
                        bangdang()
                    }

                    if(mFindDateInfo.rongGroup_count>=100){
                        groupChat()
                    }
                }
            }
        }
    }

    private fun bangdang(){
        var annotation2 = AnimationUtils.loadAnimation(context, R.anim.hide_anim)
        annotation2.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationEnd(animation: Animation?) {
                mFindDateInfo?.let {
                    tv_03.text = "收到${mFindDateInfo.userpoint_allLovePoint} [img src=redheart_small/]"
                    sv_finddate03.visibility = View.VISIBLE
                }

                var annotation2 = AnimationUtils.loadAnimation(context, R.anim.show_anim)
                ll_finddate03.startAnimation(annotation2)
            }

            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        })

        ll_finddate03.startAnimation(annotation2)
    }

    private fun groupChat(){
        var annotation3 = AnimationUtils.loadAnimation(context, R.anim.hide_anim)
        annotation3.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationEnd(animation: Animation?) {
                tv_finddate_02.text = "新人报道群(${mFindDateInfo.rongGroup_count}人）"
                var annotation3 = AnimationUtils.loadAnimation(context, R.anim.show_anim)
                tv_finddate_02.startAnimation(annotation3)
            }

            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        })
        tv_finddate_02.startAnimation(annotation3)
    }

    private val mImages = ArrayList<AddImage>()
    private var mUserInfoData: UserData? = null

    /**
     * 插入用户信息
     */
    private fun joinInCard() {
        if (!TextUtils.equals(getTodayTime(), mShowCardLastTime)) {
            var UserInfoJson = SPUtils.instance().getString(Const.USERINFO)
            if (!TextUtils.isEmpty(UserInfoJson)) {
                mUserInfoData = GsonHelper.getGson().fromJson(UserInfoJson, UserData::class.java)
                mUserInfoData?.let {
                    if (it.iDatacompletion < 1100) {
                        var mFindDate: FindDate = FindDate(it.accountId)
                        setFindDate(mFindDate, it)
                        if (mDates.size > 4) {
                            mDates.add(4, mFindDate)
                            var dataCompletion:Double =(it.iDatacompletion/120.0)
                            var percent = Math.round((dataCompletion*100)).toInt()
                            if (TextUtils.equals(sex, "0")) {
                                (mRecyclerView.adapter as DateCardAdapter).iDateComlete = percent
                            } else {
                                (mRecyclerView.adapter as DateWomanCardAdapter).iDateComlete = percent
                            }
                        }
                    }
                }
            }
            SPUtils.instance().put(Const.LASTDAYTIME, getTodayTime()).apply()
        }
    }

    private fun setFindDate(mFindDate: FindDate, it: UserData) {
        mFindDate.name = it.name.toString()
        mFindDate.classesname = it.classesname.toString()
        mFindDate.nianling = it.age.toString()
        mFindDate.sex = it.sex.toString()
        mFindDate.gexingqianming = it.intro.toString()
        mFindDate.picUrl = it.picUrl.toString()
        mFindDate.userpics = it.userpics.toString()
        mFindDate.shengao = it.height.toString()
        mFindDate.tizhong = it.weight.toString()
        mFindDate.xingzuo = it.constellation.toString()
        mFindDate.city = it.city.toString()
        mFindDate.zhiye = it.job.toString()
        mFindDate.zuojia = it.zuojia.toString()
        mFindDate.xingquaihao = it.hobbit.toString()
        mFindDate.iVistorCountAll = it.iVistorCountAll
        mFindDate.iFansCountAll = it.iFansCountAll
        mFindDate.sOnlineMsg = it.sOnlineMsg
        mFindDate.iOnline = it.iOnline
        mFindDate.sPosition = "${it.area}"
        mImages.clear()
        if (!it.userpics.isNullOrEmpty()) {
            val images = it.userpics!!.split(",")
            images.forEach {
                mImages.add(AddImage(it))
            }
        }
        mImages.add(AddImage("res:///" + R.mipmap.ic_add_bg, 1))
    }

    private fun getProvinceData() {
        try{
            if (cityJson.isNullOrEmpty()) {
                getServiceProvinceData()
            } else {
                if (TextUtils.equals(getTodayTime(), lastTime)) {
                    getServiceProvinceData()
                } else {
                    var ProvinceData: MutableList<Province>? = GsonHelper.jsonToList(cityJson, Province::class.java)
                    setLocationCity()
                    ProvinceData?.add(0, province)
                    mPopupArea.setData(ProvinceData)
                }
            }
        }catch(e:Exception){
            e.printStackTrace()
            getServiceProvinceData()
        }
    }

    private fun getServiceProvinceData() {
        Request.getProvinceAll("1").request(this) { _, data ->
            data?.let {
                DiskFileUtils.getDiskLruCacheHelper(context).put(Const.PROVINCE_DATAOFFIND+getLocalUserId(), GsonHelper.getGson().toJson(it))
                SPUtils.instance().put(Const.LASTTIMEOFPROVINCEINFIND+getLocalUserId(), getTodayTime()).apply()
                setLocationCity()
                it.add(0, province)
                mPopupArea.setData(it)
            }
        }
    }

    //设置定位城市
    private fun setLocationCity() {
        var sameCity = SPUtils.instance().getString(USER_PROVINCE)
        var city = City("", getReplace(sameCity))
        city.isSelected = true
        province.lstDicts.add(city)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Const.DOUPDATEUSERINFOCODE && resultCode == Activity.RESULT_OK) {
            var bundle = data?.extras
            mUserInfoData = (bundle?.getSerializable("userinfo") as? UserData)
            mUserInfoData?.let {
                if (TextUtils.equals(sex, "0")) {
                    (mRecyclerView.adapter as DateCardAdapter).iDateComlete = it.iDatacompletion
                } else {
                    (mRecyclerView.adapter as DateWomanCardAdapter).iDateComlete = it.iDatacompletion
                }
                setFindDate(mDates.get(4), it)
            }
            mRecyclerView.adapter.notifyItemChanged(4)
            SPUtils.instance().put(Const.USERINFO, GsonHelper.getGson().toJson(mUserInfoData)).apply()
        }
    }

    lateinit var mPopupAges: AgeSelectedPopup
    lateinit var mPopupConstellation: ConstellationSelectedPopup
    lateinit var mPopupArea: AreaSelectedPopup
    var ageIndex = -1;
    var constellationIndex = -1
    var userclassesid: String = ""
    var agemin: String = ""
    var agemax: String = ""
    var areaIndex = -3
    var city: String = ""
    var mLat: String = ""
    var mLon: String = ""

    private fun showArea(view: View) {
        mPopupArea.showAsDropDown(view, 0, resources.getDimensionPixelOffset(R.dimen.margin_1))
        mPopupArea.setOnPopupItemClick { basePopup, position, string ->
            areaIndex = position
            var lat = ""
            var lon = ""
            if (areaIndex == -1) {
                city = string
                tv_city.text = city //"同城"
                lat = mLat
                lon = mLon
                setSearChUI(0, true)
            } else if (areaIndex == -2) {
                //定位失败
                startLocation()
            } else if (areaIndex == -3) {
                city = ""
                tv_city.text = "地区"
            } else {
                city = string
                tv_city.text = string
                lat = ""
                lon = ""
                setSearChUI(0, true)
            }
            pageNum = 1
            if (pageNum == 1) {
                mDates.clear()
            }
            setLoadingShow()
            getData(city, userclassesid, agemin, agemax, lat, lon)
        }

        mPopupArea.setOnDismissListener {
            if (areaIndex == -3) {
                setSearChUI(0, false)
            }
        }

        if (mPopupArea.isShowing) {
            setSearChUI(0, true)
        }
    }

    /**
     * 星座
     */
    private fun showConstellations(view: View) {
        mPopupConstellation.showAsDropDown(view, 0, resources.getDimensionPixelOffset(R.dimen.margin_1))
        mPopupConstellation.setOnPopupItemClick { basePopup, position, string ->
            constellationIndex = position
            if (constellationIndex == 0) {
                constellationIndex = -1
                tv_xingzuo.text = "会员等级"
                userclassesid = ""
                setSearChUI(1, false)
            } else {
                userclassesid = "${position}"
                tv_xingzuo.text = string
                setSearChUI(1, true)
            }
            pageNum = 1
            if (pageNum == 1) {
                mDates.clear()
            }
            setLoadingShow()
            getData(city, userclassesid, agemin, agemax)
        }

        mPopupConstellation.setOnDismissListener {
            if (constellationIndex == -1) {
                setSearChUI(1, false)
            }
        }

        if (mPopupConstellation.isShowing) {
            setSearChUI(1, true)
        }
    }

    /**
     * 年龄弹窗
     */
    private fun showAges(view: View) {
        mPopupAges.showAsDropDown(view, 0, resources.getDimensionPixelOffset(R.dimen.margin_1))
        mPopupAges.setOnPopupItemClick { basePopup, position, string ->
            ageIndex = position
            if (ageIndex == 0) {
                ageIndex = -1
                tv_type.text = "年龄"
                agemin = ""
                agemax = ""
                setSearChUI(2, false)
            } else {
                tv_type.text = string
                setSearChUI(2, true)
                if (ageIndex == 1) {
                    agemin = ""
                    agemax = "18"
                } else if (ageIndex == 2) {
                    agemin = "18"
                    agemax = "25"
                } else if (ageIndex == 3) {
                    agemin = "26"
                    agemax = "30"
                } else if (ageIndex == 4) {
                    agemin = "31"
                    agemax = "40"
                } else if (ageIndex == 5) {
                    agemin = "40"
                    agemax = ""
                }
            }
            pageNum = 1
            if (pageNum == 1) {
                mDates.clear()
            }
            setLoadingShow()
            getData(city, userclassesid, agemin, agemax)
        }

        mPopupAges.setOnDismissListener {
            if (ageIndex == -1) {
                setSearChUI(2, false)
            }
        }

        if (mPopupAges.isShowing) {
            setSearChUI(2, true)
        }
    }

    private fun setSearChUI(clickIndex: Int, iconFlag: Boolean) {
        if (clickIndex == 0) {
            var drawable = if (iconFlag) ContextCompat.getDrawable(activity, R.mipmap.ic_arrow_up_orange) else ContextCompat.getDrawable(activity, R.mipmap.ic_arrow_down)
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())

            tv_city.setCompoundDrawables(null, null, drawable, null)
            tv_city.textColor = if (iconFlag) ContextCompat.getColor(context, R.color.color_F7AB00) else ContextCompat.getColor(context, R.color.color_666666)

        } else if (clickIndex == 1) {
            var drawable = if (iconFlag) ContextCompat.getDrawable(activity, R.mipmap.ic_arrow_up_orange) else ContextCompat.getDrawable(activity, R.mipmap.ic_arrow_down)
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())

            tv_xingzuo.setCompoundDrawables(null, null, drawable, null)
            tv_xingzuo.textColor = if (iconFlag) ContextCompat.getColor(context, R.color.color_F7AB00) else ContextCompat.getColor(context, R.color.color_666666)

        } else if (clickIndex == 2) {
            var drawable = if (iconFlag) ContextCompat.getDrawable(activity, R.mipmap.ic_arrow_up_orange) else ContextCompat.getDrawable(activity, R.mipmap.ic_arrow_down)
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())

            tv_type.setCompoundDrawables(null, null, drawable, null)
            tv_type.textColor = if (iconFlag) ContextCompat.getColor(context, R.color.color_F7AB00) else ContextCompat.getColor(context, R.color.color_666666)
        } else {
            var drawable = ContextCompat.getDrawable(activity, R.mipmap.ic_arrow_down)
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())

            tv_type.setCompoundDrawables(null, null, drawable, null)
            tv_type.textColor = ContextCompat.getColor(context, R.color.color_666666)

            tv_city.textColor = ContextCompat.getColor(context, R.color.color_666666)
            tv_xingzuo.textColor = ContextCompat.getColor(context, R.color.color_666666)
        }
    }

    private var mReceiveLoveHearts = ArrayList<LoveHeartFans>()
    private var  mDanMuIndex = 0

    private fun getFindReceiveLoveHeart(iUserId: String,type:String) {
//        toast("类型：${type}")
        Request.findReceiveLoveHeartList(iUserId, getLoginToken(), DANMU_pageNum).request(this, false, success = { _, data ->
            data?.let {
//                toast("返回")
                if (DANMU_pageNum == 1) {
                    mReceiveLoveHearts.clear()
                }
                if (it.list?.results == null || it.list?.results?.isEmpty() as Boolean) {
//                    toast("返回null")
                    if(DANMU_pageNum > 1) {
                        DANMU_pageNum--
                    }else{

                    }
                } else {
                    it.list?.results?.let {
                        mReceiveLoveHearts.addAll(it)
                    }
                    if (DANMU_pageNum == 1) {
                        sendDanMu()
                    }else{

                    }
                }
            }
        }) { code, msg ->
            toast("${msg}")
        }
    }

    private fun sendDanMu(){
        if(mReceiveLoveHearts!=null&&mReceiveLoveHearts.size>0){
            mDanMuHandler.sendEmptyMessage(0)
        }
    }

    private var mDanMuHandler:DanMuHandler  = DanMuHandler()

    internal inner class DanMuHandler : Handler(){
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            try{
                if(msg?.what==0){
                    if ((mReceiveLoveHearts.size-1) >= mDanMuIndex) {
                        addDanmaku(mReceiveLoveHearts.get(mDanMuIndex),mDanMuIndex, false)
                        mDanMuIndex = mDanMuIndex +1
                        mDanMuHandler.sendEmptyMessageDelayed(0,1000)
                    }else{
                        mReceiveLoveHearts.clear()
                    }
                    Log.i("datefragment","${mReceiveLoveHearts.size}--${mDanMuIndex}")
                    if (mReceiveLoveHearts.size - mDanMuIndex == 5) {
                        DANMU_pageNum = DANMU_pageNum + 1
                        if(mDates.size>0&&mDates.size>mRecyclerView.currentItem){
                            var findDate = mDates.get(mRecyclerView.currentItem)
                            getFindReceiveLoveHeart("${findDate.accountId}","handler")
                        }
                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    private fun addDanmaku(loveHeartFans: LoveHeartFans,index:Int, islive: Boolean) {
        val danmaku = mDanmakuContext!!.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL)
        if (danmaku == null || sv_danmaku == null) {
            return
        }
        if(loveHeartFans.giftName.isNullOrEmpty()){
            danmaku!!.text = "${loveHeartFans.sSendUserName}：送了${loveHeartFans.iPoint}颗 [img src=redheart_small/]"
        }else{
            danmaku!!.text = "${loveHeartFans.sSendUserName}：送了${loveHeartFans.giftName}(${loveHeartFans.iPoint}颗 [img src=redheart_small/])"
        }
        danmaku!!.padding = 5
        danmaku!!.priority = 0 //可能会被各种过滤器过滤并隐藏显示
        danmaku!!.isLive = islive
        danmaku!!.setTime(sv_danmaku.getCurrentTime() + index*300)
        danmaku!!.textSize = 12f * (mParser!!.getDisplayer().density - 0.6f)
        danmaku!!.textColor = ContextCompat.getColor(activity, R.color.color_333333)
        danmaku!!.textShadowColor = 0
        danmaku!!.underlineColor = 0
        danmaku!!.borderColor = 0
        danmaku!!.padding = 5
        danmaku!!.userId = loveHeartFans.iSenduserid!!
        danmaku!!.tag = DanMuImageWare(loveHeartFans.sPicUrl,danmaku,30,30,sv_danmaku)
        sv_danmaku.addDanmaku(danmaku)
    }

    private fun clearDanMu(){
        mDanMuHandler.removeCallbacksAndMessages(null)
        sv_danmaku.removeAllDanmakus(true)
        mReceiveLoveHearts.clear()
        DANMU_pageNum = 1
        mDanMuIndex = 0
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: Int) =
                DateFragment().apply {
                    arguments = Bundle().apply {

                    }
                }
    }
}