package com.d6.android.app.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import com.d6.android.app.R
import com.d6.android.app.adapters.RecommentDatePageAdapter
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.dialogs.AreaSelectedPopup
import com.d6.android.app.dialogs.FilterCityDialog
import com.d6.android.app.dialogs.FilterDateTypeDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.fragments.RecommendDateQuickFragment
import com.d6.android.app.models.City
import com.d6.android.app.models.Province
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.widget.CustomToast
import com.d6.android.app.widget.diskcache.DiskFileUtils
import kotlinx.android.synthetic.main.activity_recommend_date.*

/**
 * 全部人工推荐
 */
class RecommendDateActivity : TitleActivity() {

    val fragment = RecommendDateQuickFragment()
    private var iLookType: String = ""
    private var city: String = ""

    var province = Province(Const.LOCATIONCITYCODE,"不限/定位")

    private val lastTime by lazy{
        SPUtils.instance().getString(Const.LASTTIMEOFPROVINCEINFIND)
    }

    private val cityJson by lazy{
        DiskFileUtils.getDiskLruCacheHelper(this).getAsString(Const.PROVINCE_DATAOFFIND)
    }

    lateinit var mPopupArea: AreaSelectedPopup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommend_date)
        immersionBar.init()
        setTitleBold("人工推荐")

        tv_date_city.setOnClickListener {
                showArea()
        }

        tv_datetype.setOnClickListener {
                val filterDateTypeDialog = FilterDateTypeDialog()
                filterDateTypeDialog.setDateType(false)
                filterDateTypeDialog.show(supportFragmentManager, "ftd")
                filterDateTypeDialog.setDialogListener { p, s ->
                    if (p == 6) {
                        iLookType = ""
                        tv_datetype.text = "类型"
                    } else {
                        iLookType = p.toString()
                        tv_datetype.text = s
                    }
                    fragment.getFindRecommend(iLookType,city)
                }
        }

        fragment.userVisibleHint = true
        supportFragmentManager.beginTransaction()
                .replace(R.id.container,fragment,"s")
                .commitAllowingStateLoss()

        mPopupArea = AreaSelectedPopup.create(this)
                .setDimView(container)
                .apply()

        if(!TextUtils.equals(getTodayTime(),lastTime)){
            getProvinceData()
        }else{
            var ProvinceData: MutableList<Province>? = GsonHelper.jsonToList(cityJson, Province::class.java)
            setLocationCity()
            ProvinceData?.add(0,province)
            mPopupArea.setData(ProvinceData)
        }

//        mFragments.add(RecommendDateQuickFragment())
//        mFragments.add(RecommendDateQuickFragment())
//        mFragments.add(RecommendDateQuickFragment())
//        mFragments.add(RecommendDateQuickFragment())
//        mFragments.add(RecommendDateQuickFragment())
//        mFragments.add(RecommendDateQuickFragment())
//
//        viewpager_recommenddate.adapter = RecommentDatePageAdapter(supportFragmentManager,mFragments)
//        viewpager_recommenddate.offscreenPageLimit = mFragments.size
//        tab_recommentdate.setupWithViewPager(viewpager_recommenddate)
    }

    private fun getProvinceData() {
        Request.getProvinceAll().request(this) { _, data ->
            data?.let {
                DiskFileUtils.getDiskLruCacheHelper(this).put(Const.PROVINCE_DATAOFFIND, GsonHelper.getGson().toJson(it))
                setLocationCity()
                it.add(0,province)
                mPopupArea.setData(it)
                SPUtils.instance().put(Const.LASTTIMEOFPROVINCEINFIND, getTodayTime()).apply()
            }
        }
    }

    //设置不限
    private fun setLocationCity(){
//        var city = City("","不限地区")
        var sameCity = SPUtils.instance().getString(Const.User.USER_PROVINCE)
        var city = City("", getReplace(sameCity))
        city.isSelected = true
        city.isSelected = true
        province.lstDicts.add(city)
    }


    private fun showArea(){
        mPopupArea.showAsDropDown(ll_toolbar,0,resources.getDimensionPixelOffset(R.dimen.margin_1))
        mPopupArea.setOnPopupItemClick { basePopup, position, string ->
            if(position == -3){
                city = ""
                tv_date_city.text = "地区"
            }else{
                city = string
                tv_date_city.text = string
            }
            fragment.getFindRecommend(iLookType,city)
        }

        mPopupArea.setOnDismissListener {

        }
    }
}
