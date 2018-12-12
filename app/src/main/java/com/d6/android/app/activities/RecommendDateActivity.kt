package com.d6.android.app.activities

import android.os.Bundle
import android.text.TextUtils
import com.d6.android.app.R
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.dialogs.AreaSelectedPopup
import com.d6.android.app.dialogs.FilterCityDialog
import com.d6.android.app.dialogs.FilterDateTypeDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.fragments.RecommendDateQuickFragment
import com.d6.android.app.models.Province
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.GsonHelper
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.getTodayTime
import com.d6.android.app.widget.CustomToast
import kotlinx.android.synthetic.main.activity_recommend_date.*

/**
 * 全部人工推荐
 */
class RecommendDateActivity : TitleActivity() {

    val fragment = RecommendDateQuickFragment()
    private var iLookType: String = ""
    private var cityType: Int = -2
    private var city: String = ""

    private val lastTime by lazy{
        SPUtils.instance().getString(Const.LASTLONGTIME)
    }

    private val cityJson by lazy{
        SPUtils.instance().getString(Const.PROVINCE_DATA)
    }

    lateinit var mPopupArea: AreaSelectedPopup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommend_date)
        immersionBar.init()
        setTitleBold("全部人工推荐")

        tv_date_city.setOnClickListener {
//            val filterCityDialog = FilterCityDialog()
//            filterCityDialog.hidleCancel(TextUtils.isEmpty(city))
//            filterCityDialog.setCityValue(cityType, tv_date_city.text.toString())
//            filterCityDialog.show(supportFragmentManager, "fcd")
//            filterCityDialog.setDialogListener { p, s ->
//                if (p == 1 || p == 0|| p==2) {
//                    city = s
//                } else if (p == -2) {//取消选择
//                    city = ""
//                }
//                cityType = p
//                tv_date_city.text = s
//
//            }

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

                fragment.pullRefresh(iLookType,city)
//                if(cityType==-2){
//                    fragment.pullRefresh(iLookType,"")
//                }else{
//                    fragment.pullRefresh(iLookType,tv_date_city.text.toString())
//                }

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
            mPopupArea.setData(ProvinceData)
        }
    }

    private fun getProvinceData() {
        Request.getProvince().request(this) { _, data ->
            data?.let {
                mPopupArea.setData(it)
                SPUtils.instance().put(Const.PROVINCE_DATA, GsonHelper.getGson().toJson(it)).apply()
                SPUtils.instance().put(Const.LASTLONGTIME, getTodayTime()).apply()
            }
        }
    }

    private fun showArea(){
        mPopupArea.showAsDropDown(ll_toolbar,0,resources.getDimensionPixelOffset(R.dimen.margin_1))
        mPopupArea.setOnPopupItemClick { basePopup, position, string ->
            city = string
            tv_date_city.text = string
            fragment.getData(iLookType,city)
        }

        mPopupArea.setOnDismissListener {

        }
    }
}
