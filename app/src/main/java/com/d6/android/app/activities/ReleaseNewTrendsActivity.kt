package com.d6.android.app.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import com.amap.api.location.AMapLocationClient
import com.d6.android.app.R
import com.d6.android.app.adapters.AddImageAdapter
import com.d6.android.app.adapters.AddImageV2Adapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.SelectCityDialog
import com.d6.android.app.dialogs.SelectTagDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.AddImage
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_release_new_trends.*
import me.nereo.multi_image_selector.MultiImageSelectorActivity
import org.jetbrains.anko.dip
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast

/**
 * 广场发布
 */
class ReleaseNewTrendsActivity : BaseActivity() {

    private var tagId: String? = null
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private val mImages = ArrayList<AddImage>()
    private val addAdapter by lazy {
        AddImageV2Adapter(mImages)
    }

    private var city: String? = null
    private val locationClient by lazy {
        AMapLocationClient(applicationContext)
    }
    private var cityType = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_release_new_trends)

//        RxPermissions(this).request(Manifest.permission.ACCESS_COARSE_LOCATION).subscribe {
//            if (it) {
//                startLocation()
//            } else {
//
//            }
//        }
        city = ""
        rv_images.setHasFixedSize(true)
        rv_images.layoutManager = GridLayoutManager(this, 3)
        rv_images.adapter = addAdapter
        rv_images.isNestedScrollingEnabled = false
        rv_images.addItemDecoration(SpacesItemDecoration(dip(6),3))
        addAdapter.setOnAddClickListener {
            if (mImages.size >= 10) {//最多9张
                showToast("最多上传9张图片")
                return@setOnAddClickListener
            }
//            startActivityForResult<SelectPhotoDialog>(0)
            val c = 9
            val paths = mImages.filter { it.type!=1 }.map { it.path }
            val urls = ArrayList<String>(paths)
            startActivityForResult<MultiImageSelectorActivity>(0
                    ,MultiImageSelectorActivity.EXTRA_SELECT_MODE to MultiImageSelectorActivity.MODE_MULTI
                    ,MultiImageSelectorActivity.EXTRA_SELECT_COUNT to c
                    ,MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST to urls
            )
        }

        mImages.add(AddImage("res:///" + R.mipmap.ic_add_pic, 1))
        addAdapter.notifyDataSetChanged()
        tv_back.setOnClickListener {
            finish()
        }

        locationClient.setLocationListener {
            sysErr("--location--->$it")
            if (it != null) {
                city = it.city
                locationClient.stopLocation()
                if (cityType == 0) {
                    tv_address.text = ""
                    tv_address1.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_location,0,0,0)
                    tv_address.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_add1,0,0,0)
//                    tv_address.setTextColor(ContextCompat.getColor(this,R.color.textColor99))
                } else {
                    tv_address.text = city
                    tv_address1.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_loc_yellow,0,0,0)
                    tv_address.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_delete1,0,0,0)
//                    tv_address.setTextColor(ContextCompat.getColor(this,R.color.orange_f6a))
                }

            }
        }

        tv_release.setOnClickListener {
            publish()
        }

        tv_address.text = city

        RxPermissions(this).request(Manifest.permission.ACCESS_COARSE_LOCATION).subscribe {
            if (it) {

                startLocation()
            } else {
                cityType=0
                toast("没有定位权限")
                tv_address.text = ""
                tv_address1.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_location,0,0,0)
                tv_address.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_add1,0,0,0)
            }
        }

        tv_address.setOnClickListener {
            if (cityType == 1) {//当前显示定位
                cityType = 0
                city = ""
                tv_address.text = ""
                tv_address1.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_location,0,0,0)
                tv_address.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_add1,0,0,0)
            } else {
                cityType = 1
                RxPermissions(this).request(Manifest.permission.ACCESS_COARSE_LOCATION).subscribe {
                    if (it) {
                        if (city.isNullOrEmpty()) {
                            startLocation()
                        } else {
                            tv_address.text = city
                            tv_address1.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_loc_yellow,0,0,0)
                            tv_address.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_delete1,0,0,0)
                        }
                    } else {
                        cityType = 0
                        toast("没有定位权限")
                        tv_address.text = ""
                        tv_address1.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_location,0,0,0)
                        tv_address.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_add1,0,0,0)

                    }
                }
            }
//            val selectCityDialog = SelectCityDialog()
//            selectCityDialog.setDialogListener { p, s ->
//                selectCityDialog.dismissAllowingStateLoss()
//                cityType = p
//                if (p == 0) {
//                    tv_address.text = ""
//                } else {
//                    RxPermissions(this).request(Manifest.permission.ACCESS_COARSE_LOCATION).subscribe {
//                        if (it) {
//                            if (city.isNullOrEmpty()) {
//                                startLocation()
//                            } else {
//                                tv_address.text = city
//                            }
//
//                        } else {
//                            toast("没有定位权限")
//                            tv_address.text = ""
//                        }
//                    }
//                }
//            }
//
//            selectCityDialog.show(supportFragmentManager, "City")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0 && data != null) {
                val result = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT)
                mImages.clear()
                result.forEach {
                    val image = AddImage("file://$it")
                    image.path = it
                    mImages.add(image)
                }
                mImages.add(AddImage("res:///" + R.mipmap.ic_add_pic, 1))
                addAdapter.notifyDataSetChanged()
//                val path = data.getStringExtra(SelectPhotoDialog.PATH)
//                val size = mImages.size
//                val image = AddImage("file://$path")
////                toast("-图片地址->"+path)
//                image.path = path
//                mImages.add(size - 1, image)
//                addAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun publish() {

        val content = et_content.text.toString().trim()
        if (content.isEmpty() && mImages.size <= 1) {
            showToast("请输入内容或上传图片")
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
                val city = if (cityType == 0) {
                    ""
                } else {
                    this.city
                }
                Request.releaseSquare(userId, tagId, city, it, content)
            }.request(this) { _, data ->
                showToast("发布成功")
                setResult(Activity.RESULT_OK)
                finish()
            }

        } else {
            val city = if (cityType == 0) {
                ""
            } else {
                this.city
            }
            Request.releaseSquare(userId, tagId, city, null, content).request(this) { _, data ->
                showToast("发布成功")
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private fun startLocation() {
        tv_address.text = "定位中..."
        locationClient.stopLocation()
        locationClient.startLocation()
    }

    override fun onDestroy() {
        super.onDestroy()
        locationClient.onDestroy()
    }
}
