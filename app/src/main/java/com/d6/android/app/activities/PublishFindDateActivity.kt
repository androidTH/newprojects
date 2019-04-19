package com.d6.android.app.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import com.alibaba.fastjson.JSONObject
import com.amap.api.location.AMapLocationClient
import com.d6.android.app.dialogs.DatePickDialog
import com.d6.android.app.R
import com.d6.android.app.adapters.AddImageAdapter
import com.d6.android.app.adapters.DateTypeAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.OpenDateErrorDialog
import com.d6.android.app.dialogs.VistorPayPointDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.AddImage
import com.d6.android.app.models.DateType
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.dateTypesDefault
import com.d6.android.app.utils.Const.dateTypesSelected
import com.d6.android.app.widget.CustomToast
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_publish_find_date.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.dip
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult

/**
 * 自主发布
 */
class PublishFindDateActivity : BaseActivity() {

    private val mImages = ArrayList<AddImage>()
    private val mDateTypes = ArrayList<DateType>()
    var dateTypes = arrayOf("旅行", "吃饭", "电影", "喝酒", "不限")

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
    private var startTime: String = ""
    private var endTime: String = ""
    private var selectedDateType: DateType? = null;
    private val locationClient by lazy {
        AMapLocationClient(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publish_find_date)
        immersionBar.init()
        RxPermissions(this).request(Manifest.permission.ACCESS_COARSE_LOCATION).subscribe {
            if (it) {
                locationClient.setLocationListener {
                    sysErr("--location--->" + it)
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

        for (i in 0..4) {
            var dt = DateType((i + 1))
            dt.dateTypeName = dateTypes[i]
            dt.imgUrl = "res:///${dateTypesDefault[i]}"
            dt.selectedimgUrl = "res:///${dateTypesSelected[i]}"
            dt.isSelected = false
            mDateTypes.add(dt)
        }

        rv_datetype.setHasFixedSize(true)
        rv_datetype.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_datetype.isNestedScrollingEnabled = false
        rv_datetype.adapter = mDateTypeAdapter

        mDateTypeAdapter.setOnItemClickListener { view, position ->
            for (i in 0..(mDateTypes.size - 1)) {
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
            //            startActivityForResult<FilterCityActivity>(4,"type" to 0)
            startActivityForResult<AreaChooseActivity>(4)
        }

        tv_startTime.setOnClickListener {
            val dialog = DatePickDialog(System.currentTimeMillis(), -1)
            dialog.setOnDateSetListener { year, month, day ->
                dialog.dismissAllowingStateLoss()
                startTime = String.format("%s-%02d-%02d", year, month, day)
                tv_startTime.text = startTime
            }
            dialog.show(supportFragmentManager, "start")
        }
        tv_endTime.setOnClickListener {
            val dialog = DatePickDialog(System.currentTimeMillis(), -1)
            if (!TextUtils.isEmpty(startTime)) {
                dialog.isCheckedStartTime(startTime.isNotEmpty(), startTime.substring(startTime.length - 2, startTime.length))
            }
            dialog.setOnDateSetListener { year, month, day ->
                dialog.dismissAllowingStateLoss()
                endTime = String.format("%s-%02d-%02d", year, month, day)
                tv_endTime.text = endTime
            }
            dialog.show(supportFragmentManager, "end")
        }

        tv_sure.setOnClickListener {
            if (!isFastClick()) {
                if (publish()) {
                    Request.getAppointmentAuth(userId).request(this, true, success = { msg, data ->
                        CreateDate(et_content.text.toString().trim())
                    }) { code, msg ->
                        if (code == 0) {
                            var sex = SPUtils.instance().getString(Const.User.USER_SEX)
                            if(TextUtils.equals("1",sex)){
                                startActivity<MenMemberActivity>()
                            }else{
                                startActivity<DateAuthStateActivity>()
                            }
                        } else if (code == 2) {
                            if (msg.isNotEmpty()) {
                                val jsonObject = JSONObject.parseObject(msg)
                                var point = jsonObject.getString("iAddPoint")
                                var sAddPointDesc = jsonObject.getString("sAddPointDesc")
                                val dateDialog = VistorPayPointDialog()
                                dateDialog.arguments = bundleOf("point" to point, "pointdesc" to sAddPointDesc, "type" to 1)
                                dateDialog.show(supportFragmentManager, "vistor")
                                dateDialog.setDialogListener { p, s ->
                                    if (p == 1) {
                                        dialog()
                                        CreateDate(et_content.text.toString().trim())
                                    }
                                }
                            }
                        } else if (code == 3) {
                            if (msg != "null") {
                                val jsonObject = JSONObject.parseObject(msg)
//                            var point = jsonObject.getIntValue("iAddPoint")
//                            var remainPoint = jsonObject.getString("iRemainPoint")
                                var sAddPointDesc = jsonObject.getString("sAddPointDesc")
//                            val dateDialog = OpenDatePointNoEnoughDialog()
//                            dateDialog.arguments= bundleOf("point" to point.toString(),"remainPoint" to remainPoint)
//                            dateDialog.show(supportFragmentManager, "d")

                                var openErrorDialog = OpenDateErrorDialog()
                                openErrorDialog.arguments = bundleOf("code" to 2, "msg" to sAddPointDesc)
                                openErrorDialog.show(supportFragmentManager, "publishfindDateActivity")
                            }
                        }
                    }
                }
            }
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
            } else if (requestCode == 4 && data != null) {
//                areaType = data.getIntExtra("type",0)
                area = data.getStringExtra("area")
                tv_area.text = area
            }
        }
    }

    private fun publish(): Boolean {
        if (selectedDateType == null) {
            showToast("请选择约会类型")
            return false
        }
        if (area.isNullOrEmpty()) {
            showToast("请选择城市所属地区")
            return false
        }

        var content = et_content.text.toString().trim()
        if (content.isEmpty()) {
            showToast("请输入内容")
            return false
        }

        if (startTime.isEmpty()) {
            showToast("请选择开始时间")
            return false
        }
        if (endTime.isEmpty()) {
            showToast("请选择结束时间")
            return false
        }

        if (!isDateOneBigger(endTime, startTime)) {
            showToast("发布约会截止日期不能早于开始日期")
            return false
        }
        dialog()
        return true
    }

    private fun CreateDateOfPics(content: String) {
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
            Request.releasePullDate(userId, area, content, selectedDateType?.type, startTime, endTime, it)
        }.request(this, false, success = { _, data ->
            showToast("发布成功")
            if (TextUtils.equals("0", SPUtils.instance().getString(Const.User.USER_SEX))) {
                showTips(data, "", "")
            }
            setResult(Activity.RESULT_OK)
            startActivity<MyDateListActivity>()
            finish()
        }) { code, msg ->
            if (code == 0) {
                showToast(msg)
            }
        }
    }


    private fun CreateDate(content: String) {
        if (mImages.size > 1) {//有图片
            CreateDateOfPics(content)
        } else {
            Request.releasePullDate(userId, area, content, selectedDateType?.type, startTime, endTime, "").request(this, false, success = { _, data ->
                showToast("发布成功")
                if (TextUtils.equals("0", SPUtils.instance().getString(Const.User.USER_SEX))) {
                    showTips(data, "", "")
                }
                setResult(Activity.RESULT_OK)
                startActivity<MyDateListActivity>()
                finish()
            }) { code, msg ->
                if (code == 0) {
                    showToast(msg)
                }
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
        immersionBar.destroy()
    }
}
