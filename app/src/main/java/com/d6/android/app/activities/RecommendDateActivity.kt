package com.d6.android.app.activities

import android.os.Bundle
import android.text.TextUtils
import com.d6.android.app.R
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.dialogs.FilterCityDialog
import com.d6.android.app.dialogs.FilterDateTypeDialog
import com.d6.android.app.fragments.RecommendDateQuickFragment
import kotlinx.android.synthetic.main.activity_recommend_date.*

/**
 * 全部人工推荐
 */
class RecommendDateActivity : TitleActivity() {

    val fragment = RecommendDateQuickFragment()
    private var iLookType: String = ""
    private var cityType: Int = -2
    private var city: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommend_date)
        immersionBar.init()
        setTitleBold("全部人工推荐")

        tv_date_city.setOnClickListener {
            val filterCityDialog = FilterCityDialog()
            filterCityDialog.hidleCancel(TextUtils.isEmpty(city))
            filterCityDialog.setCityValue(cityType, tv_date_city.text.toString())
            filterCityDialog.show(supportFragmentManager, "fcd")
            filterCityDialog.setDialogListener { p, s ->
                if (p == 1 || p == 0|| p==2) {
                    city = s
                } else if (p == -2) {//取消选择
                    city = ""
                }
                cityType = p
                tv_date_city.text = s
                fragment.getData(iLookType,city.toString())
            }
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

                if(cityType==-2){
                    fragment.pullRefresh(iLookType,"")
                }else{
                    fragment.pullRefresh(iLookType,tv_date_city.text.toString())
                }

            }
        }

        fragment.userVisibleHint = true
        supportFragmentManager.beginTransaction()
                .replace(R.id.container,fragment,"s")
                .commitAllowingStateLoss()
    }
}
