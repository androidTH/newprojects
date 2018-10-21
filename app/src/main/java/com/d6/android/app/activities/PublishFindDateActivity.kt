package com.d6.android.app.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import com.amap.api.location.AMapLocationClient
import com.d6.android.app.dialogs.DatePickDialog
import com.d6.android.app.R
import com.d6.android.app.adapters.AddImageAdapter
import com.d6.android.app.adapters.DateTypeAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.AddImage
import com.d6.android.app.models.DateType
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.dateTypesDefault
import com.d6.android.app.utils.Const.dateTypesSelected
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_publish_find_date.*
import org.jetbrains.anko.db.NULL
import org.jetbrains.anko.dip
import org.jetbrains.anko.startActivityForResult

/**
 * 自主发布
 */
class PublishFindDateActivity : BaseActivity() {

    private val mImages = ArrayList<AddImage>()
    private val mDateTypes = ArrayList<DateType>()
    var dateTypes = arrayOf("吃饭","旅行","逛街","看电影","其它")

    private val addAdapter by lazy {
        AddImageAdapter(mImages)
    }

    private val mDateTypeAdapter by lazy {
        DateTypeAdapter(mDateTypes);
    }

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private var city: String? = null
    private var areaType = -1
    private var area = ""
    private var startTime:String = ""
    private var endTime:String = ""
    private var selectedDateType: DateType? = null;
    private val locationClient by lazy {
        AMapLocationClient(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publish_find_date)

        RxPermissions(this).request(Manifest.permission.ACCESS_COARSE_LOCATION).subscribe {
            if (it) {
                locationClient.setLocationListener {
                    sysErr("--location--->"+it)
                    if (it != null) {
                        city = it.city
                        tv_city.setText(city)
                        locationClient.stopLocation()
                    }
                }
                startLocation()
            } else {

            }
        }

        for (i in 0..4){
          var dt = DateType(i)
            dt.dateTypeName =dateTypes[i]
            dt.imgUrl = "res:///${dateTypesDefault[i]}"
            dt.selectedimgUrl ="res:///${dateTypesSelected[i]}"
            dt.isSelected = false
            mDateTypes.add(dt)
        }

        rv_datetype.setHasFixedSize(true)
        rv_datetype.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_datetype.isNestedScrollingEnabled = false
        rv_datetype.adapter = mDateTypeAdapter

        mDateTypeAdapter.setOnItemClickListener { view, position ->
            for (i in 0..(mDateTypes.size-1)){
                var dt = mDateTypes.get(i)
                dt.isSelected = i == position
            }
            selectedDateType = mDateTypes.get(position)
            mDateTypeAdapter.notifyDataSetChanged()
        }

        rv_images.setHasFixedSize(true)
        rv_images.layoutManager = GridLayoutManager(this, 5)
        rv_images.adapter = addAdapter
        rv_images.addItemDecoration(SpacesItemDecoration(dip(6)))
        addAdapter.setOnAddClickListener {
            if (mImages.size >= 10) {//最多9张
                showToast("最多上传9张图片")
                return@setOnAddClickListener
            }
            startActivityForResult<SelectPhotoDialog>(0)
        }
        mImages.add(AddImage("res:///" + R.mipmap.comment_addphoto_icon, 1))//ic_add_bg
        addAdapter.notifyDataSetChanged()

        tv_area.setOnClickListener {
            startActivityForResult<FilterCityActivity>(4,"type" to 0)
        }

        tv_startTime.setOnClickListener {
            val dialog = DatePickDialog(System.currentTimeMillis())
            dialog.setOnDateSetListener { year, month, day ->
                dialog.dismissAllowingStateLoss()
                startTime = String.format("%s-%02d-%02d",year,month,day)
                tv_startTime.text = startTime
            }
            dialog.show(supportFragmentManager,"start")
        }
        tv_endTime.setOnClickListener {
            val dialog = DatePickDialog(System.currentTimeMillis())
            dialog.setOnDateSetListener { year, month, day ->
                dialog.dismissAllowingStateLoss()
                endTime = String.format("%s-%02d-%02d",year,month,day)
                tv_endTime.text = endTime
            }
            dialog.show(supportFragmentManager,"end")
        }

        tv_sure.setOnClickListener {
            publish()
        }

        tv_back.setOnClickListener {
            mKeyboardKt.hideKeyboard(it)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0 && data != null) {
                val path = data.getStringExtra(SelectPhotoDialog.PATH)
                val size = mImages.size
                val image = AddImage("file://" + path)
                image.path = path
                mImages.add(size - 1, image)
                addAdapter.notifyDataSetChanged()
            }else if (requestCode == 4 && data!=null) {
                areaType = data.getIntExtra("type",0)
                area = data.getStringExtra("data")
                tv_area.text = area
            }
        }
    }

    private fun publish() {
//        val city = tv_city.text.toString().trim()
//        if (city.isEmpty()) {
//            showToast("请输入城市")
//            return
//        }
        if(selectedDateType == null){
            showToast("请选择约会类型")
        }

        if (areaType == -1) {//city.isNotEmpty() &&
            showToast("请选择城市所属地区")
            return
        }
        val outArea = if (areaType == 0) {//海外
            area
        } else {
            null
        }

        val inArae = if (areaType == 1) {//国内
            area
        } else {
            null
        }

        val content = et_content.text.toString().trim()
        if (content.isEmpty()) {
            showToast("请输入内容")
            return
        }

        if (startTime.isEmpty()) {
            showToast("请选择开始时间")
            return
        }
        if (endTime.isEmpty()) {
            showToast("请选择结束时间")
            return
        }
        if (mImages.size <= 1) {
            showToast("请上传至少一张图片")
            return
        }
        dialog()
        if (mImages.size > 1) {//有图片
            val temp = mImages.filter { it.type != 1 }
            Flowable.fromIterable(temp).subscribeOn(Schedulers.io()).flatMap {
                //压缩
                val b = BitmapUtils.compressImageFile(it.path)
                Flowable.just(b)
            }.flatMap {
                Request.uploadFile(it)
            }.toList().toFlowable().flatMap {
                val sb = StringBuilder()
                it.forEach {
                    sb.append(it).append(",")
                }
                if (sb.isNotEmpty()) {
                    sb.deleteCharAt(sb.length - 1)
                }
                Flowable.just(sb.toString())
            }.flatMap {
//                sysErr("------releaseSelfAbout--------->"+it) //city
                Request.releasePullDate(userId, area,content, selectedDateType?.type,startTime,endTime ,it)
            }.request(this) { _, data ->
                showToast("发布成功")
                setResult(Activity.RESULT_OK)
                finish()
            }

        } else {
            // area 代替city
            Request.releasePullDate(userId, area,content, selectedDateType?.type,startTime,endTime,"").request(this) { _, data ->
                showToast("发布成功")
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private fun startLocation() {
        locationClient.stopLocation()
        locationClient.startLocation()
    }
    override fun onDestroy() {
        super.onDestroy()
        locationClient.onDestroy()
    }
}
