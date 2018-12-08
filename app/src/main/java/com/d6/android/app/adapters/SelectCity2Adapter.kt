package com.d6.android.app.adapters

import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.City
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.textColor

/**
 *
 */

class SelectCity2Adapter(mData: ArrayList<City>) : BaseRecyclerAdapter<City>(mData, R.layout.item_city2) {


    var cityType:Int = -2//城市区分
    var cityName:String? = ""

    override fun onBind(holder: ViewHolder, position: Int, data: City) {
        val tv_name = holder.bind<TextView>(R.id.tv_name)
        tv_name.text = data.name
        if(TextUtils.equals(cityName, data.name)&&cityType == 1){
            tv_name.textColor = ContextCompat.getColor(context, R.color.white)
            tv_name.backgroundResource = R.drawable.shape_orange_city
        }else {
            tv_name.textColor = ContextCompat.getColor(context, R.color.color_333333)
            tv_name.backgroundColor = ContextCompat.getColor(context, R.color.trans_parent)
        }
    }

    fun setValue(type:Int, name:String?){
        this.cityType = type
        this.cityName = name
    }
}
