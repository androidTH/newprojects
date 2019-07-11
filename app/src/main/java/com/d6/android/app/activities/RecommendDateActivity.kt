package com.d6.android.app.activities

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.RecommentDatePageAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.AreaSelectedPopup
import com.d6.android.app.dialogs.RenGongDateDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.fragments.RecommendDateQuickFragment
import com.d6.android.app.models.City
import com.d6.android.app.models.Province
import com.d6.android.app.models.UserData
import com.d6.android.app.net.Request
import com.d6.android.app.rong.bean.RecommentType
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.User.IS_FIRST_SHOW_RGDIALOG
import com.d6.android.app.utils.Const.User.USER_HEAD
import com.d6.android.app.widget.diskcache.DiskFileUtils
import kotlinx.android.synthetic.main.activity_recommend_date.*
import org.jetbrains.anko.startActivity

/**
 * 全部人工推荐
 */
class RecommendDateActivity : BaseActivity() {

    val fragment = RecommendDateQuickFragment()
    private var iLookType: String = ""
    private var city: String = ""

    private val mRecommentTypes = ArrayList<RecommentType>()

    var province = Province(Const.LOCATIONCITYCODE,"不限/定位")

    private var mFragments = ArrayList<RecommendDateQuickFragment>()

    private var pageSelected = 0

    private val lastTime by lazy{
        SPUtils.instance().getString(Const.LASTTIMEOFPROVINCEINFIND)
    }

    private val cityJson by lazy{
        DiskFileUtils.getDiskLruCacheHelper(this).getAsString(Const.PROVINCE_DATAOFFIND)
    }

    private var userJson = SPUtils.instance().getString(Const.USERINFO)
    private var mUserInfo = GsonHelper.getGson().fromJson(userJson, UserData::class.java)

    private val showRGDialog by lazy{
        SPUtils.instance().getBoolean(IS_FIRST_SHOW_RGDIALOG+getLocalUserId(),true)
    }

    private val headerUrl by lazy{
        SPUtils.instance().getString(USER_HEAD)
    }

    lateinit var mPopupArea: AreaSelectedPopup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommend_date)
        immersionBar.statusBarColor(R.color.color_black).statusBarDarkFont(false).init()

        iv_back_close.setOnClickListener {
            finish()
        }

        tv_date_city.setOnClickListener {
            isAuthUser(){
               showArea()
            }
        }

        ll_userlevel.setOnClickListener {
            isAuthUser(){
                startActivity<MemberActivity>()
            }
        }

        tv_recomendtitle.setOnClickListener {
            var mRgDateDialog = RenGongDateDialog()
            mRgDateDialog.show(supportFragmentManager,"RgDateDailog")
        }

//        tv_datetype.setOnClickListener {
//                val filterDateTypeDialog = FilterDateTypeDialog()
//                filterDateTypeDialog.setDateType(false)
//                filterDateTypeDialog.show(supportFragmentManager, "ftd")
//                filterDateTypeDialog.setDialogListener { p, s ->
//                    if (p == 6) {
//                        iLookType = ""
//                        tv_datetype.text = "类型"
//                    } else {
//                        iLookType = p.toString()
//                        tv_datetype.text = s
//                    }
//                    fragment.getFindRecommend(iLookType,city)
//                }
//        }

//        fragment.userVisibleHint = true
//        supportFragmentManager.beginTransaction()
//                .replace(R.id.container,fragment,"s")
//                .commitAllowingStateLoss()

        mPopupArea = AreaSelectedPopup.create(this)
                .setDimView(ll_recomment_root)
                .apply()

        if(!TextUtils.equals(getTodayTime(),lastTime)){
            getProvinceData()
        }else{
            var ProvinceData: MutableList<Province>? = GsonHelper.jsonToList(cityJson, Province::class.java)
            setLocationCity()
            ProvinceData?.add(0,province)
            mPopupArea.setData(ProvinceData)
        }

        mRecommentTypes.add(RecommentType("全部",""))
        mRecommentTypes.add(RecommentType("觅约","5"))
        mRecommentTypes.add(RecommentType("救火","1"))
        mRecommentTypes.add(RecommentType("征求","2"))
        mRecommentTypes.add(RecommentType("急约","3"))
        mRecommentTypes.add(RecommentType("旅行约","4"))

        mFragments.add(RecommendDateQuickFragment.newInstance("",""))
        mFragments.add(RecommendDateQuickFragment.newInstance("5",""))
        mFragments.add(RecommendDateQuickFragment.newInstance("1",""))
        mFragments.add(RecommendDateQuickFragment.newInstance("2",""))
        mFragments.add(RecommendDateQuickFragment.newInstance("3",""))
        mFragments.add(RecommendDateQuickFragment.newInstance("4",""))

        viewpager_recommenddate.adapter = RecommentDatePageAdapter(supportFragmentManager,mFragments,mRecommentTypes)
        viewpager_recommenddate.offscreenPageLimit = mFragments.size
        tab_recommentdate.setupWithViewPager(viewpager_recommenddate)
        viewpager_recommenddate.addOnPageChangeListener(object:ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                pageSelected = position
            }
        })

        if(TextUtils.equals(mUserInfo.userclassesid,"7")){
            ll_userlevel.visibility = View.GONE
//            recomend_level.visibility = View.GONE
//            tv_userlevel.text = "联系客服"
        }else{
            ll_userlevel.visibility = View.VISIBLE
            recomend_level.setImageURI(headerUrl)
            if(TextUtils.equals(mUserInfo.userclassesid,"29")){
                tv_userlevel.text = "高级会员"
            }else if(TextUtils.equals(mUserInfo.userclassesid,"27")){
                tv_userlevel.text = "初级会员"
            }else{
                tv_userlevel.text = mUserInfo.classesname
            }
        }

        if(showRGDialog){
            var mRgDateDialog = RenGongDateDialog()
            mRgDateDialog.show(supportFragmentManager,"RgDateDailog")
            SPUtils.instance().put(IS_FIRST_SHOW_RGDIALOG+getLocalUserId(),false).apply()
        }

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
        mPopupArea.showAsDropDown(ll_rgchoose,0,resources.getDimensionPixelOffset(R.dimen.margin_1))
        mPopupArea.setOnPopupItemClick { basePopup, position, string ->
            if(position == -3){
                city = ""
                tv_date_city.text = "地区"
            }else{
                city = string
                tv_date_city.text = string
            }
            mFragments.get(pageSelected).getFindRecommend(mRecommentTypes.get(pageSelected).type,city)
        }

        mPopupArea.setOnDismissListener {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar.destroy()
    }
}
