package com.d6.android.app.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.amap.api.location.AMapLocationClient
import com.d6.android.app.R
import com.d6.android.app.activities.*
import com.d6.android.app.adapters.DateCardAdapter
import com.d6.android.app.adapters.DateWomanCardAdapter
import com.d6.android.app.base.BaseFragment
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.dialogs.*
import com.d6.android.app.extentions.request
import com.d6.android.app.models.*
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.User.IS_FIRST_FAST_CLICK
import com.d6.android.app.utils.Const.User.IS_FIRST_SHOW_FINDDIALOG
import com.d6.android.app.utils.Const.User.USER_ADDRESS
import com.d6.android.app.utils.Const.User.USER_PROVINCE
import com.d6.android.app.widget.CustomToast
import com.d6.android.app.widget.diskcache.DiskFileUtils
import com.d6.android.app.widget.gallery.DSVOrientation
import com.d6.android.app.widget.gallery.transform.ScaleTransformer
import com.d6.android.app.widget.gift.CustormAnim
import com.d6.android.app.widget.gift.GiftControl
import com.d6.android.app.widget.gift.GiftModel
import com.d6.android.app.widget.gift.animutils.GiftAnimationUtil
import com.google.gson.JsonObject
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Flowable
import io.reactivex.subscribers.DisposableSubscriber
import io.rong.imkit.RongIM
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.fragment_date.*
import me.jessyan.autosize.internal.CustomAdapt
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.startActivityForResult
import org.jetbrains.anko.textColor
import java.util.concurrent.TimeUnit

/**
 * 约会
 */
class DateFragment : BaseFragment(), BaseRecyclerAdapter.OnItemClickListener, CustomAdapt {

    override fun isBaseOnWidth(): Boolean {
        return false
    }

    override fun getSizeInDp(): Float {
        return 667.0f
    }

    override fun onItemClick(view: View?, position: Int) {
        if (view?.id == R.id.cardView) {
            val dateBean = mDates[position]
            startActivity<UserInfoActivity>("id" to dateBean.accountId.toString())
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

    private var localLoveHeartNums = SPUtils.instance().getInt(Const.User.USERLOVE_NUMS, 0)
    private var sendLoveHeartNums = 1

    private val locationClient by lazy {
        AMapLocationClient(activity)
    }

    private var mShowCardLastTime = SPUtils.instance().getString(Const.LASTDAYTIME)

    private val lastTime by lazy {
        SPUtils.instance().getString(Const.LASTTIMEOFPROVINCEINFIND)
    }

    private val cityJson by lazy {
        DiskFileUtils.getDiskLruCacheHelper(context).getAsString(Const.PROVINCE_DATAOFFIND)
    }

    private var IsNotFastClick:Boolean = false
    private var pageNum = 1
    private var mDates = ArrayList<FindDate>()
    private var scrollPosition = 0
    var province = Province(Const.LOCATIONCITYCODE, "不限/定位")
    //礼物
    private var giftControl: GiftControl? = null

    override fun contentViewId() = R.layout.fragment_date

    override fun onFirstVisibleToUser() {
        mRecyclerView.setOrientation(DSVOrientation.HORIZONTAL)
        setAdapter()
        mRecyclerView.setSlideOnFling(false)
        mRecyclerView.setItemTransitionTimeMillis(150)
        mRecyclerView.setItemTransformer(ScaleTransformer.Builder()
                .setMinScale(0.9f)
                .build())

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    scrollPosition = mRecyclerView.currentItem + 1
                    if ((mDates.size - scrollPosition) <= 2) {
                        pageNum++
                        getData(city, xingzuo, agemin, agemax)
                    }
                    if (mDates.size > 0) {
                        var findDate = mDates.get(scrollPosition - 1)
                        if (findDate.iIsFans == 1) {
                            fb_heat_like.setImageBitmap(BitmapFactory.decodeResource(resources, R.mipmap.center_like_button))
                        } else {
                            fb_heat_like.setImageBitmap(BitmapFactory.decodeResource(resources, R.mipmap.center_like_button))
                        }
                    }
                }
            }
        })

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

        btn_like.setOnClickListener {
            doNextCard()
            hideRedHeartGuide()
        }

        fb_heat_like.setOnClickListener {
            activity.isAuthUser() {
                if(localLoveHeartNums>0){
                    if(sendLoveHeartNums <= localLoveHeartNums){
                        sendLoveHeartNums = sendLoveHeartNums+1
                        addGiftNums(1, false,false)
                        IsNotFastClick = is500sFastClick()
                        VibrateHelp.Vibrate(activity,VibrateHelp.time50)
                    }else{
                        var mSendRedHeartEndDialog = SendRedHeartEndDialog()
                        mSendRedHeartEndDialog.show(childFragmentManager, "redheartendDialog")
                    }
                }else{
                    var mSendRedHeartEndDialog = SendRedHeartEndDialog()
                    mSendRedHeartEndDialog.show(childFragmentManager, "redheartendDialog")
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
                        addGiftNums(p, false,true)
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

        fb_unlike.setOnClickListener {
            if (mDates.isNotEmpty()) {
                scrollPosition = mRecyclerView.currentItem - 1
                if (scrollPosition >= 0) {
                    mRecyclerView.smoothScrollToPosition(scrollPosition)
                }
            }
            hideRedHeartGuide()
        }

        root_find.setOnClickListener {
            hideRedHeartGuide()
        }

        showDialog()
        getData(city, xingzuo, agemin, agemax)
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
                if (clickNums==0&&!IsNotFastClick) {
                    var mRedHeartGuideDialog = RedHeartGuideDialog()
                    mRedHeartGuideDialog.show(childFragmentManager, "redheartguideDialog")
                    ++clickNums
                    SPUtils.instance().put(IS_FIRST_SHOW_FINDDIALOG + getLocalUserId(),clickNums).apply()
                }else if(clickNums==1&&!IsNotFastClick){
                    tv_redheart_guide.visibility=View.VISIBLE
                    tv_redheart_guide.text = "连击可以送出多个喜欢"
                    ++clickNums
                    SPUtils.instance().put(IS_FIRST_SHOW_FINDDIALOG + getLocalUserId(),clickNums).apply()
//                    Flowable.interval(0, 1, TimeUnit.SECONDS).defaultScheduler().subscribe(diposable)
                }else if(it>=3&&!mIsFirstFastClick){
                    tv_redheart_guide.visibility=View.VISIBLE
                    tv_redheart_guide.text = "长按可以快捷选择520、1314个喜欢"
                    SPUtils.instance().put(IS_FIRST_FAST_CLICK + getLocalUserId(),true).apply()
//                    Flowable.interval(0, 1, TimeUnit.SECONDS).defaultScheduler().subscribe(diposable)
                }
            }
        }
    }

    private val diposable = object : DisposableSubscriber<Long>() {
        override fun onComplete() {}
        override fun onError(t: Throwable?) {}
        override fun onNext(t: Long) {
            if (t == 3L) {
                hideRedHeartGuide()
            }
        }

    }

    private fun hideRedHeartGuide(){
        tv_redheart_guide.visibility=View.GONE
    }

    //连击礼物数量
    private fun addGiftNums(giftnum: Int, currentStart: Boolean = false,JumpCombo:Boolean = false) {
        if (giftnum == 0) {
            return
        } else {
            giftControl?.let {
                //这里最好不要直接new对象
                var giftModel = GiftModel()
                giftModel.setGiftId("礼物Id").setGiftName("礼物名字").setGiftCount(giftnum).setGiftPic("")
                        .setSendUserId("1234").setSendUserName("吕靓茜").setSendUserPic("").setSendGiftTime(System.currentTimeMillis())
                        .setCurrentStart(currentStart)
                if (currentStart) {
                    giftModel.setHitCombo(giftnum)
                }
                if(JumpCombo){
                    giftModel.setJumpCombo(giftnum)
                }
                it.loadGift(giftModel)
                Log.d("TAG", "onClick: " + it.getShowingGiftLayoutCount())
            }
            doAnimation()
        }
    }

    fun refresh() {
        showDialog()
        pageNum = 1
        if (pageNum == 1) {
            mDates.clear()
        }
        getData(city, xingzuo, agemin, agemax)
    }

    fun setAdapter() {
        if (TextUtils.equals(sex, "0")) {
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
            }
        }

        locationClient.setLocationListener {
            if (it != null) {
                locationClient.stopLocation()
                mLat = it.latitude.toString()
                mLon = it.longitude.toString()
                SPUtils.instance().put(USER_PROVINCE, it.province).apply()
                SPUtils.instance().put(USER_ADDRESS, it.city).apply() //it.city
            }
        }
    }

    private fun startLocation() {
        locationClient.stopLocation()
        locationClient.startLocation()
    }

    /**
     * 搜索
     */
    fun getData(city: String = "", xingzuo: String = "", agemin: String = "", agemax: String = "", lat: String = "", lon: String = "") {
        if (mDates.size == 0) {
            tv_main_card_bg_im_id.gone()
            tv_main_card_Bg_tv_id.gone()
            fb_unlike.gone()
            btn_like.gone()
            fb_heat_like.gone()
            fb_find_chat.gone()
        }
        Request.findAccountCardListPage(userId, city, "", xingzuo, agemin, agemax, lat, lon, pageNum).request(this) { _, data ->
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                if (pageNum == 1) {
                    mRecyclerView.visibility = View.GONE
//                    tv_tip.gone()
                    tv_main_card_bg_im_id.visible()
                    tv_main_card_Bg_tv_id.visible()
                    fb_unlike.gone()
                    btn_like.gone()
                    fb_heat_like.gone()
                    fb_find_chat.gone()
                } else {
                    mRecyclerView.visibility = View.VISIBLE
                    tv_main_card_bg_im_id.gone()
                    tv_main_card_Bg_tv_id.gone()
                    fb_unlike.visible()
                    btn_like.visible()
                    fb_heat_like.visible()
                    fb_find_chat.visible()
                }
            } else {
                mRecyclerView.visibility = View.VISIBLE
                tv_main_card_bg_im_id.gone()
                tv_main_card_Bg_tv_id.gone()
                fb_unlike.visible()
                btn_like.visible()
                fb_heat_like.visible()
                fb_find_chat.gone()
                if (pageNum == 1) {
                    mDates.clear()
                }
                mDates.addAll(data.list.results)
                if (pageNum == 1) {
                    joinInCard()
                    var findDate = mDates.get(0)
                    if (findDate.iIsFans == 1) {
                        fb_heat_like.setImageBitmap(BitmapFactory.decodeResource(resources, R.mipmap.center_like_button))//like_complte
                    } else {
                        fb_heat_like.setImageBitmap(BitmapFactory.decodeResource(resources, R.mipmap.center_like_button))//discover_like_button
                    }
                }
                mRecyclerView.adapter.notifyDataSetChanged()
            }
        }
    }

    private fun doAnimation() {
//        iv_date_redheart.setImageResource(R.mipmap.animation_redheart)
//        var imageView = ImageView()
//        val lp = ll_bottom.getLayoutParams() as RelativeLayout.LayoutParams//两个参数分别是layout_width,layout_height
//        lp.addRule(RelativeLayout.CENTER_VERTICAL)
//        ll_bottom.addView(imageView)
        loveheart.showAnimationRedHeart(null)
//        val holder1 = PropertyValuesHolder.ofFloat("scaleX", 0.0f, 3.0f)
//        val holder2 = PropertyValuesHolder.ofFloat("scaleY", 0.0f, 3.0f)
//        val holder3 = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.0f)
//        val animator = ObjectAnimator.ofPropertyValuesHolder(iv_date_redheart, holder1, holder2, holder3)
//        animator.duration = 500
//        animator.start()
//        animator.addListener(object : Animator.AnimatorListener {
//            override fun onAnimationStart(animation: Animator) {
//
//            }
//
//            override fun onAnimationEnd(animation: Animator) {
//            }
//
//            override fun onAnimationCancel(animation: Animator) {
//
//            }
//
//            override fun onAnimationRepeat(animation: Animator) {
//
//            }
//        })
    }

    fun doNextCard() {
        scrollPosition = mRecyclerView.currentItem + 1
        if (mDates.isNotEmpty() && (mDates.size - scrollPosition) >= 1) {
            mRecyclerView.smoothScrollToPosition(scrollPosition)
            if ((mDates.size - scrollPosition) <= 2) {
                pageNum++
                getData(city, xingzuo, agemin, agemax)
            }
        }
    }

    private fun addFollow(giftCount: Int) {
        scrollPosition = mRecyclerView.currentItem
        if (mDates.size > scrollPosition) {
            var findDate = mDates.get(scrollPosition)

            Request.sendLovePoint(getLoginToken(), "${findDate.accountId}", giftCount, 2,"").request(this, false, success = { _, data ->
                Log.i("GiftControl", "礼物数量${giftCount}")
                sendLoveHeartNums = 1
                Request.getUserInfo("", getLocalUserId()).request(this,false,success = { _, data ->
                    data?.let {
                        SPUtils.instance().put(Const.User.USERLOVE_NUMS,it.iLovePoint).apply()
                        localLoveHeartNums = it.iLovePoint
                    }
                })
            }) { code, msg ->
                sendLoveHeartNums = 1
                if (code == 2||code == 3) {
                    var mSendRedHeartEndDialog = SendRedHeartEndDialog()
                    mSendRedHeartEndDialog.show(childFragmentManager, "redheartendDialog")
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
        activity.isCheckOnLineAuthUser(this, userId) {
            Request.getApplyStatus(userId, id, 1).request(this, false, success = { msg, jsonObjetct ->
                jsonObjetct?.let {
                    var code = it.optInt("code")
                    if (code != 7) {
                        RongIM.getInstance().startConversation(activity, Conversation.ConversationType.PRIVATE, id, name)
                    }
                }
            })
//            RongIM.getInstance().startConversation(activity, Conversation.ConversationType.PRIVATE, id, name)
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            immersionBar.init()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar.destroy()
        locationClient.onDestroy()
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
                    //                    showToast("完成度${it.iDatacompletion}")
                    if (it.iDatacompletion < 60) {
                        var mFindDate: FindDate = FindDate(it.accountId)
                        setFindDate(mFindDate, it)
                        if (mDates.size > 4) {
                            mDates.add(4, mFindDate)
                            if (TextUtils.equals(sex, "0")) {
                                (mRecyclerView.adapter as DateCardAdapter).iDateComlete = it.iDatacompletion
                            } else {
                                (mRecyclerView.adapter as DateWomanCardAdapter).iDateComlete = it.iDatacompletion
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
        if (cityJson.isNullOrEmpty()) {
            getServiceProvinceData()
        } else {
            if (!TextUtils.equals(getTodayTime(), lastTime)) {
                getServiceProvinceData()
            } else {
                var ProvinceData: MutableList<Province>? = GsonHelper.jsonToList(cityJson, Province::class.java)
                setLocationCity()
                ProvinceData?.add(0, province)
                mPopupArea.setData(ProvinceData)
            }
        }
    }

    private fun getServiceProvinceData() {
        Request.getProvinceAll().request(this) { _, data ->
            data?.let {
                DiskFileUtils.getDiskLruCacheHelper(context).put(Const.PROVINCE_DATAOFFIND, GsonHelper.getGson().toJson(it))
                SPUtils.instance().put(Const.LASTTIMEOFPROVINCEINFIND, getTodayTime()).apply()
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
    var xingzuo: String = ""
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
                tv_city.text = "同城"
                city = string
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
            getData(city, xingzuo, agemin, agemax, lat, lon)
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
                tv_xingzuo.text = "星座"
                xingzuo = ""
                setSearChUI(1, false)
            } else {
                xingzuo = string
                tv_xingzuo.text = string
                setSearChUI(1, true)
            }
            pageNum = 1
            if (pageNum == 1) {
                mDates.clear()
            }
            getData(city, xingzuo, agemin, agemax)
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
            getData(city, xingzuo, agemin, agemax)
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
            tv_city.textColor = if (iconFlag) ContextCompat.getColor(context, R.color.color_F7AB00) else ContextCompat.getColor(context, R.color.color_black)

        } else if (clickIndex == 1) {
            var drawable = if (iconFlag) ContextCompat.getDrawable(activity, R.mipmap.ic_arrow_up_orange) else ContextCompat.getDrawable(activity, R.mipmap.ic_arrow_down)
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())

            tv_xingzuo.setCompoundDrawables(null, null, drawable, null)
            tv_xingzuo.textColor = if (iconFlag) ContextCompat.getColor(context, R.color.color_F7AB00) else ContextCompat.getColor(context, R.color.color_black)

        } else if (clickIndex == 2) {
            var drawable = if (iconFlag) ContextCompat.getDrawable(activity, R.mipmap.ic_arrow_up_orange) else ContextCompat.getDrawable(activity, R.mipmap.ic_arrow_down)
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())

            tv_type.setCompoundDrawables(null, null, drawable, null)
            tv_type.textColor = if (iconFlag) ContextCompat.getColor(context, R.color.color_F7AB00) else ContextCompat.getColor(context, R.color.color_black)
        } else {
            var drawable = ContextCompat.getDrawable(activity, R.mipmap.ic_arrow_down)
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())

            tv_type.setCompoundDrawables(null, null, drawable, null)
            tv_type.textColor = ContextCompat.getColor(context, R.color.color_black)

            tv_city.textColor = ContextCompat.getColor(context, R.color.color_black)
            tv_xingzuo.textColor = ContextCompat.getColor(context, R.color.color_black)
        }
    }
}