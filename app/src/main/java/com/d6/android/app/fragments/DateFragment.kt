package com.d6.android.app.fragments

import android.Manifest
import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import com.amap.api.location.AMapLocationClient
import com.d6.android.app.R
import com.d6.android.app.activities.MyDateActivity
import com.d6.android.app.activities.UserInfoActivity
import com.d6.android.app.adapters.DateCardAdapter
import com.d6.android.app.adapters.DateWomanCardAdapter
import com.d6.android.app.base.BaseFragment
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.dialogs.FilterCityDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.DateBean
import com.d6.android.app.models.FindDate
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.User.USER_ADDRESS
import com.d6.android.app.widget.gallery.CardScaleHelper
import com.google.gson.JsonObject
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.fragment_date.*
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

    private var city: String? = null
    private var outCity: String? = null
    private var cityType: Int = -2

    private var pageNum = 1
    private var mDates = ArrayList<FindDate>()
    private var scrollPosition = 0
    lateinit var mCardScaleHelper:CardScaleHelper

    override fun contentViewId() = R.layout.fragment_date

    override fun onFirstVisibleToUser() {
        immersionBar.statusBarColor(R.color.colorPrimaryDark).init()
        mRecyclerView.layoutManager=LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        setAdapter()
        mRecyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    var position = mRecyclerView.verticalScrollbarPosition
                    if ((mDates.size-position) <= 2) {
                        pageNum++
                        if(cityType == -2){
                            getData(SPUtils.instance().getString(USER_ADDRESS),"")
                        }else{
                            getData("",tv_city.text.toString())
                        }
                    }
                }
            }
        })
        tv_city.setOnClickListener {
            val filterCityDialog = FilterCityDialog()
            filterCityDialog.hidleCancel(TextUtils.isEmpty(city) && TextUtils.isEmpty(outCity))
            filterCityDialog.setCityValue(cityType, tv_city.text.toString())
            filterCityDialog.show(childFragmentManager, "fcd")
            filterCityDialog.setDialogListener { p, s ->
                if (p == 1 || p == 0) {
                    city = s
                    outCity = null
                    tv_city.textColor = resources.getColor(R.color.color_F7AB00)
                    tv_type.textColor = resources.getColor(R.color.color_black)
                    tv_type.text = resources.getString(R.string.string_samecity)
                } else if (p == 2) {
                    city = null
                    outCity = s
                    tv_city.textColor = resources.getColor(R.color.color_F7AB00)
                    tv_type.textColor = resources.getColor(R.color.color_black)
                    tv_type.text = resources.getString(R.string.string_samecity)
                } else if (p == -2) {//取消选择
                    city = null
                    outCity = null
                    tv_city.textColor = resources.getColor(R.color.color_black)
                    tv_type.textColor = resources.getColor(R.color.color_F7AB00)
                    tv_type.text = resources.getString(R.string.string_samecity)
                }
                cityType = p
                tv_city.text = s
                pageNum =1
                if(p == -2){
                    getData(SPUtils.instance().getString(USER_ADDRESS),"")
                }else{
                    getData("",s.toString())
                }
            }
        }

        tv_type.setOnClickListener {
//            val filterDateTypeDialog = FilterDateTypeDialog()
//            filterDateTypeDialog.show(childFragmentManager, "ftd")
//            filterDateTypeDialog.setDialogListener { p, s ->
//                type = p
//                tv_type.text = s
//                getData(1)
//            }
        }

        btn_like.setOnClickListener {
//            tv_tip.gone()
            scrollPosition = mCardScaleHelper.currentItemPos + 1
            if (mDates.isNotEmpty()) {
//                val date = mDates[0]
//                showDialog()
//                sendDateRequest(date)
                mRecyclerView.smoothScrollToPosition(scrollPosition)
                if((mDates.size - scrollPosition)<=2){
                    pageNum++
                    getData()
                }
            }
//            else {
//                tv_tip.visible()
//            }
        }

        fb_heat_like.setOnClickListener {
            if(mDates.isNotEmpty()){
                addFollow()
            }
        }

        fb_unlike.setOnClickListener {
            // val dateErrorDialog = DateErrorDialog()
//            dateErrorDialog.show(childFragmentManager, "d")
//            tv_tip.gone()
//            tv_main_card_bg_im_id.gone()
//            tv_main_card_Bg_tv_id.gone()
            if (mDates.isNotEmpty()) {
//                val date = mDates[0]
//                mDates.remove(date)
//                mRecyclerView.adapter.notifyDataSetChanged()
//                getNext()
                scrollPosition = mCardScaleHelper.currentItemPos - 1
                if(scrollPosition >= 0){
                    mRecyclerView.smoothScrollToPosition(scrollPosition)
                }
            } else {
//                tv_tip.visible()
//                tv_main_card_bg_im_id.visible()
//                tv_main_card_Bg_tv_id.visible()
            }
        }

        showDialog()
        checkLocation()
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
                tv_type.text = it.city
                tv_type.textColor = resources.getColor(R.color.color_F7AB00)
                locationClient.stopLocation()
                updateAddress(tv_type.text.toString().trim())
                SPUtils.instance().put(USER_ADDRESS, it.city).apply()
                getData(it.city,"")
            }
        }
    }

    private fun startLocation() {
        locationClient.stopLocation()
        locationClient.startLocation()
    }

    fun getData(sPosition:String = "",city:String = "") {
        if (mDates.size == 0) {
            tv_main_card_bg_im_id.visible()
            tv_main_card_Bg_tv_id.visible()
            fb_unlike.gone()
            btn_like.gone()
        }
        Request.findAccountCardListPage(userId,sPosition, city,pageNum).request(this) { _, data ->
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                if(pageNum == 1){
                    mRecyclerView.visibility = View.GONE
                    tv_tip.gone()
                    tv_main_card_bg_im_id.visible()
                    tv_main_card_Bg_tv_id.visible()
                    fb_unlike.gone()
                    btn_like.gone()
                    fb_heat_like.gone()
                }else{
                    mRecyclerView.visibility = View.VISIBLE
                    tv_main_card_bg_im_id.gone()
                    tv_main_card_Bg_tv_id.gone()
                    fb_unlike.visible()
                    btn_like.visible()
                    fb_heat_like.visible()
                }
            } else {
                mRecyclerView.visibility = View.VISIBLE
                tv_main_card_bg_im_id.gone()
                tv_main_card_Bg_tv_id.gone()
                fb_unlike.visible()
                btn_like.visible()
                fb_heat_like.visible()
                if(cityType == -2&&pageNum == 1){
                   mDates.clear()
                }else if(cityType != -2&&pageNum == 1){
                    mDates.clear()
                }
                mDates.addAll(data.list.results)
                setRecycler()
            }
        }
    }

    var flag:Boolean = false
    fun setRecycler(){
        if(!flag){
            flag = true
            mCardScaleHelper = CardScaleHelper()
            mCardScaleHelper.attachToRecyclerView(mRecyclerView)
            mCardScaleHelper.setCurrentItemPos(0)
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
        doAnimation()
        var findDate = mDates.get(scrollPosition)
        Request.getAddFollow(userId,findDate.accountId.toString()).request(this){ s: String?, jsonObject: JsonObject? ->
            //toast("$s,$jsonObject")
        }
    }

    fun getNext() {
        if (mDates.size <= 2) {
            getData("","")
        }
    }

    private fun getAuthState() {
        startActivity<MyDateActivity>()
        tv_tip.visibility = View.GONE
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
}