package com.d6.android.app.fragments

import android.Manifest
import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.amap.api.location.AMapLocationClient
import com.d6.android.app.R
import com.d6.android.app.activities.MyDateActivity
import com.d6.android.app.activities.UserInfoActivity
import com.d6.android.app.adapters.DateCardAdapter
import com.d6.android.app.adapters.DateWomanCardAdapter
import com.d6.android.app.base.BaseFragment
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.dialogs.*
import com.d6.android.app.extentions.request
import com.d6.android.app.models.City
import com.d6.android.app.models.DateBean
import com.d6.android.app.models.FindDate
import com.d6.android.app.models.Province
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.User.USER_ADDRESS
import com.d6.android.app.widget.gallery.DSVOrientation
import com.d6.android.app.widget.gallery.transform.ScaleTransformer
import com.d6.android.app.widget.popup.EasyPopup
import com.d6.android.app.widget.popup.XGravity
import com.google.gson.JsonObject
import com.tbruyelle.rxpermissions2.RxPermissions
import io.rong.imkit.RongIM
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.activity_user_info_v2.*
import kotlinx.android.synthetic.main.fragment_date.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.textColor

/**
 * 约会
 */
class DateFragment : BaseFragment(), BaseRecyclerAdapter.OnItemClickListener {
    override fun onItemClick(view: View?, position: Int) {
        val dateBean = mDates[position]
        startActivity<UserInfoActivity>("id" to dateBean.accountId.toString())
    }

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private val sex by lazy {
        SPUtils.instance().getString(Const.User.USER_SEX)
    }

    private val locationClient by lazy {
        AMapLocationClient(activity)
    }

    private val sameCity by lazy {
        SPUtils.instance().getString(USER_ADDRESS);
    }

    private val lastTime by lazy{
        SPUtils.instance().getString(Const.LASTLONGTIME)
    }

    private val cityJson by lazy{
        SPUtils.instance().getString(Const.PROVINCE_DATA)
    }

    private var pageNum = 1
    private var mDates = ArrayList<FindDate>()
    private var scrollPosition = 0
    var province = Province("100010","定位")

    override fun contentViewId() = R.layout.fragment_date

    override fun onFirstVisibleToUser() {
        immersionBar.statusBarColor(R.color.colorPrimaryDark).init()
        mRecyclerView.setOrientation(DSVOrientation.HORIZONTAL)
        setAdapter()
        mRecyclerView.setSlideOnFling(false)
        mRecyclerView.setItemTransitionTimeMillis(150)
        mRecyclerView.setItemTransformer(ScaleTransformer.Builder()
                .setMinScale(0.9f)
                .build())

        mRecyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    scrollPosition = mRecyclerView.currentItem+1
                    if ((mDates.size-scrollPosition) <= 2) {
                        pageNum++
                        getData(sPosition,city,xingzuo,agemin,agemax)
//                        if(cityType == -2){
//                            getData()
//                        }else{
//                            getData("",tv_city.text.toString().trim())
//                        }
                    }
                }
            }
        })

        tv_city.setOnClickListener {
//            val filterCityDialog = FilterCityDialog()
//            filterCityDialog.hidleCancel(TextUtils.isEmpty(city) && TextUtils.isEmpty(outCity))
//            filterCityDialog.setCityValue(cityType, tv_city.text.toString())
//            filterCityDialog.show(childFragmentManager, "fcd")
//            filterCityDialog.setDialogListener { p, s ->
//                if (p == 1 || p == 0) {
//                    city = s
//                    outCity = null
//                    tv_city.textColor = ContextCompat.getColor(context,R.color.color_F7AB00)
//                    tv_type.textColor = ContextCompat.getColor(context,R.color.color_black)
//                    tv_type.text = resources.getString(R.string.string_samecity)
//                } else if (p == 2) {
//                    city = null
//                    outCity = s
//                    tv_city.textColor = ContextCompat.getColor(context,R.color.color_F7AB00)
//                    tv_type.textColor = ContextCompat.getColor(context,R.color.color_black)
//                    tv_type.text = resources.getString(R.string.string_samecity)
//                } else if (p == -2) {//取消选择
//                    city = null
//                    outCity = null
//                    tv_city.textColor = ContextCompat.getColor(context,R.color.color_black)
//                    tv_type.textColor = ContextCompat.getColor(context,R.color.color_black)
//                    tv_type.text = resources.getString(R.string.string_samecity)
//                }
//                cityType = p
//                tv_city.text = s
//                pageNum =1
//                if(p == -2){
//                    getData("","")
//                }else{
//                    getData("",tv_city.text.toString().trim())
//                }
//            }
            showArea(it)
        }

        tv_xingzuo.setOnClickListener {
            showConstellations(it)
        }

        tv_type.setOnClickListener {
            showAges(it)
//            pageNum=1
//            tv_type.textColor = ContextCompat.getColor(context,R.color.color_F7AB00)
//            getData(sameCity,"")
        }

        btn_like.setOnClickListener {
            doNextCard()
        }

        fb_heat_like.setOnClickListener {
            if(mDates.isNotEmpty()){
                addFollow()
            }
        }

        fb_find_chat.setOnClickListener {
//            activity.isNoAuthToChat("5") {
                scrollPosition = mRecyclerView.currentItem
                if(mDates.size > scrollPosition){
                    var findChat = mDates.get(scrollPosition)
                    findChat?.let {
                        val name = it.name
                        showDatePayPointDialog(name,it.accountId.toString())
                    }
                }
//            }
        }

        fb_unlike.setOnClickListener {
            // val dateErrorDialog = DateErrorDialog()
//            dateErrorDialog.show(childFragmentManager, "d")
//            tv_tip.gone()
//            tv_main_card_bg_im_id.gone()
//            tv_main_card_Bg_tv_id.gone()
            if (mDates.isNotEmpty()) {
                scrollPosition = mRecyclerView.currentItem - 1
                if(scrollPosition >= 0){
                    mRecyclerView.smoothScrollToPosition(scrollPosition)
                }
            }
        }

        showDialog()
        getData(sPosition,city,xingzuo,agemin,agemax)
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
    }

    fun setAdapter(){
        if(TextUtils.equals(sex,"0")){
            mRecyclerView.adapter = DateCardAdapter(mDates)
        }else{
            mRecyclerView.adapter =  DateWomanCardAdapter(mDates)
        }
        (mRecyclerView.adapter as BaseRecyclerAdapter<*>).setOnItemClickListener(this)
    }

    private fun checkLocation(){
        RxPermissions(activity).request(Manifest.permission.ACCESS_COARSE_LOCATION).subscribe {
            if (it) {
                startLocation()
            }
        }

        locationClient.setLocationListener {
            if (it != null) {
                locationClient.stopLocation()
//                updateAddress(sameCity)
                lat = it.latitude.toString()
                lon = it.longitude.toString()
                SPUtils.instance().put(USER_ADDRESS,it.city).apply()
            }
        }

    }

    private fun startLocation() {
        locationClient.stopLocation()
        locationClient.startLocation()
    }

    fun getData(sPosition:String = "",city:String = "",xingzuo:String="",agemin:String="",agemax:String="") {
        if (mDates.size == 0) {
            tv_main_card_bg_im_id.visible()
            tv_main_card_Bg_tv_id.visible()
            fb_unlike.gone()
            btn_like.gone()
            fb_heat_like.gone()
            fb_find_chat.gone()
        }

        Request.findAccountCardListPage(userId,sPosition, city,"",xingzuo,agemin,agemax,lat,lon,pageNum).request(this) { _, data ->
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                if(pageNum == 1){
                    mRecyclerView.visibility = View.GONE
//                    tv_tip.gone()
                    tv_main_card_bg_im_id.visible()
                    tv_main_card_Bg_tv_id.visible()
                    fb_unlike.gone()
                    btn_like.gone()
                    fb_heat_like.gone()
                    fb_find_chat.gone()
                }else{
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
                fb_find_chat.visible()
                if(pageNum == 1){
                   mDates.clear()
                }
                mDates.addAll(data.list.results)
                mRecyclerView.adapter.notifyDataSetChanged()
            }
        }
    }

    private fun updateAddress(address:String){
        Request.updateUserPosition(userId,address).request(this, success={msg,data->

        })
    }

    private fun doAnimation(){
        iv_date_redheart.setImageResource(R.mipmap.animation_redheart)
        val holder1 = PropertyValuesHolder.ofFloat("scaleX", 0.0f, 3.0f)
        val holder2 = PropertyValuesHolder.ofFloat("scaleY", 0.0f, 3.0f)
        val holder3 = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.0f)
        val animator = ObjectAnimator.ofPropertyValuesHolder(iv_date_redheart, holder1, holder2, holder3)
        animator.duration = 500
        animator.start()
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        })
    }

    fun doNextCard(){
        scrollPosition = mRecyclerView.currentItem+1
        if (mDates.isNotEmpty()&&(mDates.size-scrollPosition)>=1) {
            mRecyclerView.smoothScrollToPosition(scrollPosition)
            if((mDates.size - scrollPosition)<=2){
                pageNum++
//                if(cityType == -2){
//                  getData()
//                }else{
//                  getData("",tv_city.text.toString().trim())
//                }
                getData(sPosition,city,xingzuo,agemin,agemax)
            }
        }
    }

    private fun sendDateRequest(dateBean: DateBean) {
//        Request.dateUser(userId, dateBean.accountId).request(this, success = { msg, data ->
//            val dateSendedDialog = DateSendedDialog()//35619 35641  35643    35589
//            dateSendedDialog.arguments = bundleOf("data" to dateBean,"msg" to if(msg!=null)msg else "")
//            dateSendedDialog.show(childFragmentManager, "d")
//            mDates.remove(dateBean)
//            mRecyclerView.adapter.notifyDataSetChanged()
//            //请求下次
//            getNext()
//        }) { code, msg ->
//            val dateErrorDialog = DateErrorDialog()
//            val images = dateBean.userpics?.split(",")
//            val img = if (images != null && images.isNotEmpty()) {
//                images[0]
//            } else {
//                dateBean.picUrl ?: ""
//            }
//            dateErrorDialog.arguments = bundleOf("msg" to msg, "img" to img)
//            dateErrorDialog.show(childFragmentManager, "d")
//        }
    }

    private fun addFollow(){
//        showDialog()//35578
        scrollPosition = mRecyclerView.currentItem
        if(mDates.size > scrollPosition){
            doAnimation()
            var findDate = mDates.get(scrollPosition)
            Request.getAddFollow(userId,findDate.accountId.toString()).request(this){ s: String?, jsonObject: JsonObject? ->
                //toast("$s,$jsonObject")
                doNextCard()
                showTips(jsonObject,"","")
            }
        }
    }

    private fun showDatePayPointDialog(name:String,id:String){
        Request.doTalkJustify(userId, id).request(this,false,success = {msg,data->
            if(data!=null){
                var code = data!!.optInt("code")
                if(code == 1){
                    var point = data!!.optString("iTalkPoint")
                    var remainPoint = data!!.optString("iRemainPoint")
                    if(point.toInt() > remainPoint.toInt()){
                        val dateDialog = OpenDatePointNoEnoughDialog()
                        var point = data!!.optString("iTalkPoint")
                        var remainPoint = data!!.optString("iRemainPoint")
                        dateDialog.arguments= bundleOf("point" to point,"remainPoint" to remainPoint)
                        dateDialog.show(activity.supportFragmentManager, "d")
                    }else{
                        val dateDialog = OpenDatePayPointDialog()
                        dateDialog.arguments= bundleOf("point" to point,"remainPoint" to remainPoint,"username" to name,"chatUserId" to id)
                        dateDialog.show(activity.supportFragmentManager, "d")
                    }
                } else if(code == 0){
                    RongIM.getInstance().startConversation(activity, Conversation.ConversationType.PRIVATE, id, name)
                } else {
                    val dateDialog = OpenDatePointNoEnoughDialog()
                    var point = data!!.optString("iTalkPoint")
                    var remainPoint = data!!.optString("iRemainPoint")
                    dateDialog.arguments= bundleOf("point" to point,"remainPoint" to remainPoint)
                    dateDialog.show(activity.supportFragmentManager, "d")
                }
            }else{
                RongIM.getInstance().startConversation(activity, Conversation.ConversationType.PRIVATE, id, name)
            }
        }) { _, msg ->

        }
    }

    private fun getAuthState() {
        startActivity<MyDateActivity>()
//        tv_tip.visibility = View.GONE
        SPUtils.instance().put(Const.User.IS_FIRST_SHOW_TIPS,false).apply()
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

    private fun getProvinceData() {
        if(!TextUtils.equals(getTodayTime(),lastTime)){
            Request.getProvince().request(this) { _, data ->
                data?.let {
                    SPUtils.instance().put(Const.PROVINCE_DATA, GsonHelper.getGson().toJson(it)).apply()
                    setLocationCity()
                    it.add(0,province)
                    mPopupArea.setData(it)
                }
            }
        }else{
            var ProvinceData: MutableList<Province>? = GsonHelper.jsonToList(cityJson, Province::class.java)
            setLocationCity()
            ProvinceData?.add(0,province)
            mPopupArea.setData(ProvinceData)
        }
    }

    //设置定位城市
    private fun setLocationCity(){
        var city = City("",sameCity)
        city.isSelected = true
        province.lstDicts.add(city)
    }

    private var mProvinces = ArrayList<Province>()
    lateinit var mPopupAges:AgeSelectedPopup
    lateinit var mPopupConstellation:ConstellationSelectedPopup
    lateinit var mPopupArea:AreaSelectedPopup
    var ageIndex = -1;
    var constellationIndex = -1
    var xingzuo:String=""
    var agemin:String=""
    var agemax:String=""
    var areaIndex = -3
    var city:String=""
    var sPosition:String=""
    var lat:String=""
    var lon:String=""

    private fun showArea(view:View){
        mPopupArea.showAsDropDown(view,0,resources.getDimensionPixelOffset(R.dimen.margin_1))
        mPopupArea.setOnPopupItemClick { basePopup, position, string ->
            areaIndex = position
            if(areaIndex == -1){
                tv_city.text = "同城"
                sPosition = string
                city = ""
                setSearChUI(0,true)
            }else if(areaIndex == -2){
                //定位失败
            }else {
                sPosition = ""
                city = string
                tv_city.text = string
                setSearChUI(0,true)
            }
            pageNum=1
            getData(sPosition,city,xingzuo,agemin,agemax)
        }

        mPopupArea.setOnDismissListener {
            if(areaIndex == -3){
                setSearChUI(0,false)
            }
        }

        if(mPopupArea.isShowing){
            setSearChUI(0,true)
        }
    }
    /**
     * 星座
     */
    private fun showConstellations(view:View){
        mPopupConstellation.showAsDropDown(view,0,resources.getDimensionPixelOffset(R.dimen.margin_1))
        mPopupConstellation.setOnPopupItemClick { basePopup, position, string ->
            constellationIndex = position
            if(constellationIndex == 0){
                constellationIndex = -1
                tv_xingzuo.text = "星座"
                xingzuo = ""
                setSearChUI(1,false)
            }else{
                xingzuo = string
                tv_xingzuo.text = string
                setSearChUI(1,true)
            }
            pageNum=1
            getData(sPosition,city,xingzuo,agemin,agemax)
        }

        mPopupConstellation.setOnDismissListener {
            if(constellationIndex == -1){
                setSearChUI(1,false)
            }
        }

        if(mPopupConstellation.isShowing){
            setSearChUI(1,true)
        }
    }

    /**
     * 年龄弹窗
     */
    private fun showAges(view:View){
        mPopupAges.showAsDropDown(view,0,resources.getDimensionPixelOffset(R.dimen.margin_1))
        mPopupAges.setOnPopupItemClick { basePopup, position, string ->
              ageIndex = position
              if(ageIndex == 0){
                  ageIndex = -1
                  tv_type.text = "年龄"
                  agemin = ""
                  agemax = ""
                  setSearChUI(2,false)
              }else{
                  tv_type.text = string
                  setSearChUI(2,true)
                  if(ageIndex == 1){
                    agemin=""
                    agemax="18"
                  }else if(ageIndex == 2){
                      agemin="18"
                      agemax="25"
                  }else if(ageIndex == 3){
                      agemin="26"
                      agemax="30"
                  }else if(ageIndex == 4){
                      agemin="31"
                      agemax="40"
                  } else if(ageIndex == 5){
                      agemin="40"
                      agemax=""
                  }
              }
            pageNum=1
            getData(sPosition,city,xingzuo,agemin,agemax)
        }

        mPopupAges.setOnDismissListener {
            if(ageIndex == -1){
                setSearChUI(2,false)
            }
        }

        if(mPopupAges.isShowing){
            setSearChUI(2,true)
        }
    }

    private fun setSearChUI(clickIndex:Int,iconFlag:Boolean){
         if(clickIndex == 0){
             var drawable = if(iconFlag) ContextCompat.getDrawable(activity,R.mipmap.ic_arrow_up_orange)else ContextCompat.getDrawable(activity,R.mipmap.ic_arrow_down)
             drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())

             tv_city.setCompoundDrawables(null,null,drawable,null)
             tv_city.textColor = if(iconFlag)ContextCompat.getColor(context,R.color.color_F7AB00) else ContextCompat.getColor(context,R.color.color_black)

         }else if(clickIndex == 1){
             var drawable = if(iconFlag) ContextCompat.getDrawable(activity,R.mipmap.ic_arrow_up_orange)else ContextCompat.getDrawable(activity,R.mipmap.ic_arrow_down)
             drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())

             tv_xingzuo.setCompoundDrawables(null,null,drawable,null)
             tv_xingzuo.textColor = if(iconFlag)ContextCompat.getColor(context,R.color.color_F7AB00) else ContextCompat.getColor(context,R.color.color_black)

         }else if(clickIndex == 2){
             var drawable = if(iconFlag) ContextCompat.getDrawable(activity,R.mipmap.ic_arrow_up_orange)else ContextCompat.getDrawable(activity,R.mipmap.ic_arrow_down)
             drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())

             tv_type.setCompoundDrawables(null,null,drawable,null)
             tv_type.textColor = if(iconFlag)ContextCompat.getColor(context,R.color.color_F7AB00) else ContextCompat.getColor(context,R.color.color_black)
         }else{
             var drawable = ContextCompat.getDrawable(activity,R.mipmap.ic_arrow_down)
             drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())

             tv_type.setCompoundDrawables(null,null,drawable,null)
             tv_type.textColor = ContextCompat.getColor(context,R.color.color_black)

             tv_city.textColor = ContextCompat.getColor(context,R.color.color_black)
             tv_xingzuo.textColor = ContextCompat.getColor(context,R.color.color_black)
         }
    }
}