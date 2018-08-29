package com.d6.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.GridLayoutManager
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.adapters.SelectCity2Adapter
import com.d6.android.app.adapters.SelectHotCity2Adapter
import com.d6.android.app.adapters.SelectOutCity2Adapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.interfaces.RequestManager
import com.d6.android.app.models.City
import com.d6.android.app.net.Request
import com.d6.android.app.utils.OnDialogListener
import com.gyf.barlibrary.ImmersionBar
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_filter_date_city_layout.*
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.toast

/**
 * 筛选约会地区弹窗
 */
class FilterCityDialog : DialogFragment(), RequestManager {

    private val compositeDisposable by lazy {
        CompositeDisposable()
    }

    private val immersionBar by lazy {
        ImmersionBar.with(this, dialog)
    }
    private val mHotCities = ArrayList<City>()
    private val mCities = ArrayList<City>()
    private val mOutCities = ArrayList<City>()
    private var Flag_Hidle_Cancel:Boolean = false
    private var cityType:Int = -2
    private var cityName:String? = ""

    private val hotAdapter by lazy {
        SelectHotCity2Adapter(mHotCities)
    }

    private val cityAdapter by lazy {
        SelectCity2Adapter(mCities)
    }

    private val outCityAdapter by lazy {
        SelectOutCity2Adapter(mOutCities)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FadePopup)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        immersionBar
                .fitsSystemWindows(true)
                .statusBarDarkFont(true)
                .init()
        dialog.window.setLayout(matchParent, matchParent)
        dialog.window.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL)
        dialog.setCanceledOnTouchOutside(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.dialog_filter_date_city_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bgView.setOnClickListener { dismissAllowingStateLoss() }
        rv_hot.setHasFixedSize(true)
        rv_hot.layoutManager = GridLayoutManager(context, 4)
        rv_hot.isNestedScrollingEnabled = false
        rv_hot.adapter = hotAdapter

        hotAdapter.setValue(cityType, cityName)

        hotAdapter.setOnItemClickListener { view, position ->
            val city = mHotCities[position]
            dialogListener?.onClick(0,city.name)
            dismissAllowingStateLoss()
        }

        rv_guonei.setHasFixedSize(true)
        rv_guonei.layoutManager = GridLayoutManager(context, 4)
        rv_guonei.isNestedScrollingEnabled = false
        rv_guonei.adapter = cityAdapter
        cityAdapter.setValue(cityType, cityName)


        cityAdapter.setOnItemClickListener { view, position ->
            val city = mCities[position]
            dialogListener?.onClick(1,city.name)
            dismissAllowingStateLoss()
        }

        rv_out.setHasFixedSize(true)
        rv_out.layoutManager = GridLayoutManager(context, 4)
        rv_out.isNestedScrollingEnabled = false
        rv_out.adapter = outCityAdapter
        outCityAdapter.setValue(cityType, cityName)

        outCityAdapter.setOnItemClickListener { view, position ->
            val city = mOutCities[position]
            dialogListener?.onClick(2,city.name)
            dismissAllowingStateLoss()
        }
        if (context is BaseActivity) {
            (context as BaseActivity).dialog()
        }

        tv_cancel.setOnClickListener {
            dialogListener?.onClick(-2,"地区")
            dismissAllowingStateLoss()
        }

        if(Flag_Hidle_Cancel){
            tv_cancel.visibility = View.GONE
        }else{
            tv_cancel.visibility = View.VISIBLE
        }

        //全国
        getData("1")
        getData("0")
    }

    private fun getData(key: String) {
        Request.getCities(key).request(this) { _, data ->
            mHotCities.clear()
            if (key == "0") {
                mCities.clear()
            } else {
                mOutCities.clear()
            }
            data?.let {
                if (key == "0") {
                    mCities.addAll(it)//国内城市
                } else {
                    mOutCities.addAll(it)//海外城市
                }
                it.forEach {
                    //isValid 1 热门地市，0 普通地市
                    if (TextUtils.equals(it.isValid, "1")) {
                        mHotCities.add(it)
                    }
                }
                if (key == "0") {
                    cityAdapter.notifyDataSetChanged()
                } else {
                    outCityAdapter.notifyDataSetChanged()
                }
                hotAdapter.notifyDataSetChanged()
            }
        }
    }

    private var dialogListener: OnDialogListener? = null

    fun setDialogListener(l: (p: Int, s: String?) -> Unit) {
        dialogListener = object : OnDialogListener {
            override fun onClick(position: Int, data: String?) {
                l(position, data)
            }
        }
    }

    fun hidleCancel(flag : Boolean):Unit{
        this.Flag_Hidle_Cancel = flag
    }

    fun setCityValue(type:Int ,name:String?){
      this.cityType = type;
      this.cityName = name
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
        immersionBar.destroy()
    }

    override fun showToast(msg: String) {
        toast(msg)
    }

    override fun dismissDialog() {
        if (context is BaseActivity) {
            (context as BaseActivity).dismissDialog()
        }
    }

    override fun onBind(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

}