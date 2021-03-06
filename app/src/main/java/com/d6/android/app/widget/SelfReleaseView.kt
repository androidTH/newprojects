package com.d6.android.app.widget

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.d6.android.app.R
import com.d6.android.app.adapters.SelfReleaselmageAdapter
import com.d6.android.app.models.MyDate
import com.d6.android.app.utils.*
import kotlinx.android.synthetic.main.view_self_release_view.view.*

/**
 * Created on 2017/12/17.
 */
class SelfReleaseView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    private var myDate: MyDate? = null
    private val mImages = ArrayList<String>()
    private val imageAdapter by lazy {
        SelfReleaselmageAdapter(mImages,1)
    }
    init {
        LayoutInflater.from(context).inflate(R.layout.view_self_release_view, this, true)
        rv_images.setHasFixedSize(true)
//        rv_images.layoutManager = GridLayoutManager(context, 3)
        rv_images.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv_images.adapter = imageAdapter
//        rv_images.addItemDecoration(SpacesItemDecoration(dip(4)))
    }

    fun update(myDate: MyDate) {
        this.myDate = myDate
        headView.setImageURI(myDate.picUrl)
        tv_name.text = myDate.name
        tv_date_user_sex.isSelected = TextUtils.equals("0", myDate.sex)
        tv_date_user_sex.text = myDate.age
        val start = myDate.beginTime?.parserTime("yyyy-MM-dd")
        val end = myDate.endTime?.parserTime("yyyy-MM-dd")
        val time = String.format("%s-%s",start?.toTime("MM.dd"),end?.toTime("MM.dd"))
//        val s = if (myDate.city.isNullOrEmpty()) {
//            time
//        } else {
//            time+" | " +myDate.city
//        }
//        tv_sub_title.text = SpanBuilder(s)
//                .color(context,0,time.length,R.color.color_369)
//                .build()
        tv_time_long.text = "倒计时·${time}"

        tv_self_address.text = "约会地点·${myDate.city}"

        tv_content.text = myDate.content

        if (myDate.selfpicurl.isNullOrEmpty()) {
            rv_images.gone()
        } else {
            rv_images.visible()
        }
        mImages.clear()
        val images = myDate.selfpicurl?.split(",")
        if (images != null) {
            mImages.addAll(images.toList())
        }
        imageAdapter.notifyDataSetChanged()
    }
}