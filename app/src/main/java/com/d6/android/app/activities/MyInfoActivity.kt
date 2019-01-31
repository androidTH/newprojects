package com.d6.android.app.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.widget.DatePicker
import com.d6.android.app.R
import com.d6.android.app.adapters.MyImageAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.*
import com.d6.android.app.extentions.request
import com.d6.android.app.models.AddImage
import com.d6.android.app.models.City
import com.d6.android.app.models.UserData
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration
import io.reactivex.Flowable
import kotlinx.android.synthetic.main.activity_my_info.*
import okhttp3.internal.Util
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.dip
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import www.morefuntrip.cn.sticker.Bean.BLBeautifyParam
import java.io.File
import java.util.*

/**
 *我的个人信息
 */
class MyInfoActivity : BaseActivity() {

    private val SEX_REQUEST_CODE = 9
    private val CONSTELLATION_REQUEST_CODE = 10
    private val AREA_REQUEST_CODE = 11

    private val userData by lazy {
        intent.getSerializableExtra("data") as UserData
    }

    private val mImagesData by lazy<ArrayList<AddImage>>{
        intent.getParcelableArrayListExtra("images")
    }

    private val myImageAdapter by lazy {
        MyImageAdapter(mImagesData)
    }

    private var sex: String = "1"
    private var headFilePath: String? = null
    private var calendar = Calendar.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_info)
        immersionBar.fitsSystemWindows(true).init()

        rv_edit_images.setHasFixedSize(true)
        rv_edit_images.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_edit_images.isNestedScrollingEnabled = false
        rv_edit_images.adapter = myImageAdapter
        rv_edit_images.addItemDecoration(VerticalDividerItemDecoration.Builder(this)
                .colorResId(android.R.color.transparent)
                .size(dip(2))
                .build())
        myImageAdapter.mRes = R.mipmap.person_edit_addphoto
        myImageAdapter.setOnItemClickListener { _, position ->
            val data = mImagesData[position]
            if (data.type != 1) {
                userData?.let {
                    val urls = mImagesData.filter { it.type != 1 }.map { it.imgUrl }
                    startActivityForResult<ImagePagerActivity>(22, "data" to it, ImagePagerActivity.URLS to urls, ImagePagerActivity.CURRENT_POSITION to position, "delete" to true)
                }
            } else {
                if (mImagesData.size >= 9) {
                    toast("最多上传8张图片")
                    return@setOnItemClickListener
                }
                startActivityForResult<SelectPhotoDialog>(8)
            }
        }

        tv_back.setOnClickListener {
            finish()
        }
        tv_save.setOnClickListener {
            saveInfo()
        }

        tv_edit_headview.setOnClickListener {
            startActivityForResult<SelectPhotoDialog>(0)
        }

        tv_inputaddress.setOnClickListener {
            startActivityForResult<AreaChooseActivity>(AREA_REQUEST_CODE)
        }

        tv_sex1.setOnClickListener {
//            startActivityForResult<SexChooseActivity>(SEX_REQUEST_CODE)
        }

        tv_birthday1.setOnClickListener {
            val datePickDialog = DatePickDialog()
            datePickDialog.show(supportFragmentManager,"date")
            datePickDialog.setOnDateSetListener { year, month, day ->
                datePickDialog.dismissAllowingStateLoss()
                val mYear = calendar.get(Calendar.YEAR)
                if(mYear-year<16){
                     showToast("生日选择不能小于16岁")
                }else{
                    val t = String.format("%04d-%02d-%02d", year, month, day)
                    tv_birthday1.text = t
                    userData.birthday = String.format("%04d-%02d-%02d", year, month, day)
                    tv_constellation1.text = getConstellations("$year-$month-$day")
                }
            }

//            val calendar = Calendar.getInstance()
//            val dialog = DatePickerDialog(this@MyInfoActivity,
//                    DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
//                        calendar.set(Calendar.YEAR, year)
//                        calendar.set(Calendar.MONTH, month)
//                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
////                            mTvBirthDay.setText(year.toString() + "-" + month + "-" + dayOfMonth)
//                    },
//                    calendar.get(Calendar.YEAR),
//                    calendar.get(Calendar.MONTH),
//                    calendar.get(Calendar.DAY_OF_MONTH))
//            dialog.show()
        }

        tv_height1.setOnClickListener {
            val selectHeightDialog = SelectHeightDialog()
            userData.height?.let {
                selectHeightDialog.arguments = bundleOf("data" to it)
            }
            selectHeightDialog.show(supportFragmentManager,"h")
            selectHeightDialog.setDialogListener { p, s ->
                tv_height1.text = s
                userData.height = s
            }
        }

        tv_weight1.setOnClickListener {
            val selectWeightDialog = SelectWeightDialog()
            userData.weight?.let {
                selectWeightDialog.arguments = bundleOf("data" to it)
            }
            selectWeightDialog.setDialogListener { p, s ->
                tv_weight1.text = s
                userData.weight = s
            }
            selectWeightDialog.show(supportFragmentManager,"w")
        }

        tv_constellation1.setOnClickListener {
//            val selectConstellationDialog = SelectConstellationDialog()
//            userData.constellation?.let {
//                selectConstellationDialog.arguments = bundleOf("data" to it)
//            }
//
//            selectConstellationDialog.setDialogListener { p, s ->
//                tv_constellation1.text = s
//                userData.constellation = s
//            }
//            selectConstellationDialog.show(supportFragmentManager,"c")

            startActivityForResult<ConstellationChooseActivity>(CONSTELLATION_REQUEST_CODE)
        }

        tv_hobbit1.setOnClickListener({
            val hobbitDialog = HobbitDialog()
            hobbitDialog.show(supportFragmentManager, "hobbit")
            hobbitDialog.setDialogListener { p, s ->
                if(!TextUtils.isEmpty(s)){
                    userData.hobbit = s
                    tv_hobbit1.text = s!!.replace("#",",",false)
                }
            }
        })

        headView.setImageURI(userData.picUrl)
        tv_nickName.setText(userData.name)
//        tv_signature1.setText(userData.signature)
//        tv_city1.setText(userData.city)
//        tv_area1.setText(userData.area)
        tv_birthday1.text = userData.birthday
        tv_height1.text = userData.height
        tv_weight1.text = userData.weight
//        tv_age1.setText(userData.age)
        sex = userData.sex ?: "1"
        tv_sex1.text = if (TextUtils.equals(sex, "1")) {
            "男"
        } else "女"
        tv_job1.setText(userData.job)
        tv_hobbit1.setText(userData.hobbit)
        tv_constellation1.text = userData.constellation
        tv_intro1.setText(userData.intro)
        et_zuojia.setText(userData.zuojia)
        tv_inputaddress.text = userData.city
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0 && data != null) {
                val path = data.getStringExtra(SelectPhotoDialog.PATH)
                startActivityForResult<CropImageActivity>(1, "scale" to 1f, "uri" to path)
            } else if (requestCode == 1) {
//                headFilePath = data?.getStringExtra("path")
//                headView.setImageURI("file://$headFilePath")
                var path = data?.getStringExtra("path")
                var param: BLBeautifyParam = BLBeautifyParam()//data.imgUrl.replace("file://","")
                param.index = 0
                param.type = Const.User.HEADERIMAGE
                param.images.add(path)
                startActivityForResult<BLBeautifyImageActivity>(BLBeautifyParam.REQUEST_CODE_BEAUTIFY_IMAGE, BLBeautifyParam.KEY to param);
            }else if (requestCode == 8 && data != null) {//选择图片
                val path = data.getStringExtra(SelectPhotoDialog.PATH)
//                updateImages(path)
                var param: BLBeautifyParam = BLBeautifyParam()//data.imgUrl.replace("file://","")
                param.index = 0
                param.type = Const.User.SELECTIMAGE
                param.images.add(path)
                startActivityForResult<BLBeautifyImageActivity>(BLBeautifyParam.REQUEST_CODE_BEAUTIFY_IMAGE, BLBeautifyParam.KEY to param);
            }else if(requestCode==22){
                 refreshImages(data!!.getSerializableExtra("data") as UserData)
            }else if(requestCode == BLBeautifyParam.REQUEST_CODE_BEAUTIFY_IMAGE&& data != null){
                var param = data.getParcelableExtra<BLBeautifyParam>(BLBeautifyParam.RESULT_KEY);
                if(param.type.equals(Const.User.SELECTIMAGE)){
                    updateImages(param.images[param.index])
                }else if(param.type.equals(Const.User.HEADERIMAGE)){
                    headFilePath = param.images[param.index]
                    headView.setImageURI("file://$headFilePath")
                }
            }else if(requestCode == SEX_REQUEST_CODE){
                sex = data!!.getStringExtra("sex")
                tv_sex1.text = if (TextUtils.equals(sex, "1")) {
                    "男"
                } else "女"
            }else if(requestCode == CONSTELLATION_REQUEST_CODE){
                tv_constellation1.text = data!!.getStringExtra("xinzuo")
                userData.constellation = data!!.getStringExtra("xinzuo")
            }else if(requestCode == AREA_REQUEST_CODE){
                var area = data!!.getStringExtra("area")
                tv_inputaddress.text = area
            }
        }
    }

    private fun updateImages(path: String) {
        Flowable.just(path).flatMap {
            val file = BitmapUtils.compressImageFile(path)
            Request.uploadFile(file)
        }.flatMap {
            if (userData.userpics.isNullOrEmpty()) {
                userData.userpics = it
            } else {
                userData.userpics = userData.userpics + "," + it
            }
            Request.updateUserInfo(userData)
        }.request(this) { _, _ ->
            refreshImages(userData)
        }
    }

    private fun refreshImages(userData: UserData) {
        mImagesData.clear()
        if (!userData.userpics.isNullOrEmpty()) {
            val images = userData.userpics!!.split(",")
            images.forEach {
                mImagesData.add(AddImage(it))
            }
        }
        mImagesData.add(AddImage("res:///" + R.mipmap.ic_add_bg, 1))
        myImageAdapter.notifyDataSetChanged()
    }

    private fun saveInfo() {
        val nick = tv_nickName.text.toString().trim()
        if (nick.isEmpty()) {
            showToast("昵称不能为空!")
            return
        }
//        val city = tv_city1.text.toString().trim()
//        val area = tv_area1.text.toString().trim()
        val hobbit = tv_hobbit1.text.toString().trim()
        val job = tv_job1.text.toString().trim()
//        val age = tv_age1.text.toString().trim()
        val height = tv_height1.text.toString().trim()
        val weight = tv_weight1.text.toString().trim()
//        val signature = tv_signature1.text.toString().trim()
        val constellation = tv_constellation1.text.toString().trim()
        val intro = tv_intro1.text.toString().trim()
        var zuojia = et_zuojia.text.toString().trim()

        userData.name = nick
        userData.sex = sex
        userData.hobbit = hobbit
        userData.job = job
//        userData.age = age
        userData.height = height
        userData.weight = weight
//        userData.signature = signature
        userData.constellation = constellation
        userData.intro = intro
        userData.userId = SPUtils.instance().getString(Const.User.USER_ID)
        userData.zuojia = zuojia
        userData.city = tv_inputaddress.text.toString().trim()
        dialog()
        if (headFilePath == null) {
            Request.updateUserInfo(userData).request(this) { msg, data ->
                showToast(msg.toString())
                var updateIntent = Intent()
                var bd= Bundle()
                bd.putSerializable("userinfo",data)
                updateIntent.putExtras(bd)
                setResult(Activity.RESULT_OK,updateIntent)
                finish()
            }
        } else {
            Request.uploadFile(File(headFilePath)).flatMap {
                sysErr("----------------->$it")
                userData.picUrl = it
                Request.updateUserInfo(userData)
            }.request(this) { msg, _ ->
                showToast(msg.toString())
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }
}
