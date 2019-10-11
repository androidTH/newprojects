package com.d6.android.app.activities

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import com.d6.android.app.R
import com.d6.android.app.adapters.MyImageAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.*
import com.d6.android.app.extentions.request
import com.d6.android.app.models.AddImage
import com.d6.android.app.models.Imagelocals
import com.d6.android.app.models.UserData
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.widget.MaxEditTextWatcher
import com.d6.android.app.widget.ObserverManager
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_my_info.*
import me.nereo.multi_image_selector.MultiImageSelectorActivity
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
class MyInfoActivity : BaseActivity(),Observer{

    private val SEX_REQUEST_CODE = 9
    private val CONSTELLATION_REQUEST_CODE = 10
    private val AREA_REQUEST_CODE = 11

    private var tempFile: File? = null

    private val userData by lazy {
        intent.getSerializableExtra("data") as UserData
    }

    private val mImagesData by lazy<ArrayList<AddImage>>{
        intent.getParcelableArrayListExtra("images")
    }

    private val myImageAdapter by lazy {
        MyImageAdapter(mImagesData)
    }

    private var MAXPICS = 9

    override fun update(o: Observable?, arg: Any?) {
        var mImagelocal = arg as Imagelocals
        if(mImagelocal.mType == 1){
            var localImages = ArrayList<String>()
            mImagelocal.mUrls.forEach {
                localImages.add(it)///storage/emulated/0/Huawei/MagazineUnlock/magazine-unlock-01-2.3.1104-_9E598779094E2DB3E89366E34B1A6D50.jpg
            }
            updateImages(localImages)
        }
    }

    private var sex: String = "1"
    private var headFilePath: String? = null
    private var calendar = Calendar.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_info)
        immersionBar.init()
        ObserverManager.getInstance().addObserver(this)

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
                if (mImagesData.size > MAXPICS) {//最多9张
                    showToast("最多上传9张图片")
                    return@setOnItemClickListener
                }
                val c = (MAXPICS - mImagesData.size + 1)
                startActivityForResult<MultiImageSelectorActivity>(8
                        , MultiImageSelectorActivity.EXTRA_SELECT_MODE to MultiImageSelectorActivity.MODE_MULTI
                        ,MultiImageSelectorActivity.EXTRA_SELECT_COUNT to c,MultiImageSelectorActivity.EXTRA_SHOW_CAMERA to true
                )
            }
        }

        tv_back.setOnClickListener {
            mKeyboardKt.hideKeyboard(it)
            finish()
        }
        tv_save.setOnClickListener {
            if(!isFastClick()){
                saveInfo()
            }
        }

        tv_edit_headview.setOnClickListener {
//            startActivityForResult<SelectPhotoDialog>(0)
            var mSelectPhotoDialog = SelectPhotosDialog()
            mSelectPhotoDialog.show(supportFragmentManager,"dsate")
            mSelectPhotoDialog.setDialogListener { p, s ->
               if(p==0){
                   AppUtils.initFilePath()
                   val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                   val fileName = System.currentTimeMillis().toString() + ".jpg"
                   tempFile = File(AppUtils.PICDIR, fileName)
                   val u = Uri.fromFile(tempFile)
                   intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0)
                   //7.0崩溃问题
                   if (Build.VERSION.SDK_INT < 24) {
                       intent.putExtra(MediaStore.EXTRA_OUTPUT, u)
                       startActivityForResult(intent, 0)
                   } else {
                       var list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                       if(list.size>0) {
                           val contentValues = ContentValues(1)
                           contentValues.put(MediaStore.Images.Media.DATA, tempFile?.absolutePath)
                           val uri = this@MyInfoActivity.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                           intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                           startActivityForResult(intent, 0)
                       }
                   }
               }else{
                   val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)// 调用android的图库
                   intent.type = "image/*"
                   startActivityForResult(intent, 1)
               }
            }
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

        tv_nickName.addTextChangedListener(object: MaxEditTextWatcher(CHINESE_TWO,16,this,tv_nickName){
            override fun onTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {
                super.onTextChanged(charSequence, i, i1, i2)
            }

            override fun afterTextChanged(editable: Editable?) {
                super.afterTextChanged(editable)
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

        //顶部添加提示文案：完成度复用旧版算法
        //-60%：😔资料完成度：xx% 要约别人先完善自己
        //60%-80%：🙂资料完成度：xx% 离完美的自己就差一步啦
        //80%-：😄资料完成度：xx% D6不会泄漏你的任何信息
        var dataCompletion:Double =(userData.iDatacompletion/120.0)
        var percent = Math.round((dataCompletion*100)).toInt()
        if(percent<=60){
            tv_userinfo_percent.text ="\uD83D\uDE14资料完成度：${percent}% 要约别人先完善自己 "
        }else if(percent>60&&percent<=80){
            tv_userinfo_percent.text ="\uD83D\uDE42资料完成度：${percent}% 离完美的自己就差一步啦 "
        }else{
            tv_userinfo_percent.text = "\uD83D\uDE04资料完成度：${percent}% D6不会泄漏你的任何信息"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0) {
                if (tempFile != null && tempFile!!.exists()) {
                    startActivityForResult<CropImageActivity>(3, "scale" to 1f, "uri" to tempFile!!.absolutePath)
                }
//                val path = data.getStringExtra(SelectPhotoDialog.PATH)
            } else if(requestCode==1){
                val uri = data!!.data
                if (uri != null) {
                    val path = getUrlPath(uri)
                    if (path != null) {
                        val typeIndex = path.lastIndexOf(".")
                        if (typeIndex != -1) {
                            val fileType = path.substring(typeIndex + 1).toLowerCase(Locale.CHINA)
                            //某些设备选择图片是可以选择一些非图片的文件。然后发送出去或出错。这里简单的通过匹配后缀名来判断是否是图片文件
                            //如果是图片文件则发送。反之给出提示
                            if (fileType == "jpg" || fileType == "gif"
                                    || fileType == "png" || fileType == "jpeg"
                                    || fileType == "bmp" || fileType == "wbmp"
                                    || fileType == "ico" || fileType == "jpe") {
                                startActivityForResult<CropImageActivity>(3, "scale" to 1f, "uri" to path)
                                //			                        	cropImage(path);
                                //			                        	BitmapUtil.getInstance(this).loadImage(iv_image, path);
                            } else {
                                toast("无法识别的图片类型！")
                            }
                        } else {
                            toast("无法识别的图片类型！")
                        }
                    } else {
                        toast("无法识别的图片类型或路径！")
                    }
                }
            }else if (requestCode == 3) {
                var path = data?.getStringExtra("path")
                var param: BLBeautifyParam = BLBeautifyParam()//data.imgUrl.replace("file://","")
                param.index = 0
                param.type = Const.User.HEADERIMAGE
                param.images.add(path)
                startActivityForResult<BLBeautifyImageActivity>(BLBeautifyParam.REQUEST_CODE_BEAUTIFY_IMAGE, BLBeautifyParam.KEY to param);
            }else if (requestCode == 8 && data != null) {
                    //选择图片
                var result: ArrayList<String> = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT)
                var localImages = ArrayList<String>()
                result.forEach {
                    localImages.add(it)///storage/emulated/0/Huawei/MagazineUnlock/magazine-unlock-01-2.3.1104-_9E598779094E2DB3E89366E34B1A6D50.jpg
                }
                updateImages(localImages)
            }else if(requestCode==22){
                 refreshImages(data!!.getSerializableExtra("data") as UserData)
            }else if(requestCode == BLBeautifyParam.REQUEST_CODE_BEAUTIFY_IMAGE&& data != null){
                var param = data.getParcelableExtra<BLBeautifyParam>(BLBeautifyParam.RESULT_KEY);
                if (param.type.equals(Const.User.HEADERIMAGE)) {
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

//    private fun updateImages(path: String) {
//        Flowable.just(path).flatMap {
//            val file = BitmapUtils.compressImageFile(path)
//            Request.uploadFile(file)
//        }.flatMap {
//            if (userData.userpics.isNullOrEmpty()) {
//                userData.userpics = it
//            } else {
//                userData.userpics = userData.userpics + "," + it
//            }
//            Request.updateUserInfo(userData)
//        }.request(this) { _, _ ->
//            refreshImages(userData)
//        }
//    }

    private fun updateImages(mImages:ArrayList<String>) {
        dialog()
        Flowable.fromIterable(mImages).subscribeOn(Schedulers.io()).flatMap {
            //压缩
            val b = BitmapUtils.compressImageFile(it)
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
        if(MAXPICS!=mImagesData.size){
            mImagesData.add(AddImage("res:///" + R.mipmap.ic_add_bg, 1))
        }
        myImageAdapter.notifyDataSetChanged()
    }

    private fun saveInfo() {
        val nick = tv_nickName.text.toString().trim()
        if (nick.isEmpty()) {
            showToast("昵称不能为空!")
            return
        }

//        if(checkLimitEx(nick)){
//            showToast("昵称中不能包含特殊符号或表情")
//            return
//        }

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
        userData.sex = ""
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
