package com.d6.android.app.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.amap.api.location.AMapLocationClient
import com.d6.android.app.R
import com.d6.android.app.adapters.AddImageV2Adapter
import com.d6.android.app.adapters.NoticeFriendsQuickAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.CommonTipDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.AddImage
import com.d6.android.app.models.Fans
import com.d6.android.app.models.FriendBean
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.CHOOSE_Friends
import com.d6.android.app.widget.CustomToast
import com.tbruyelle.rxpermissions2.RxPermissions
import com.umeng.analytics.MobclickAgent
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_release_new_trends.*
import me.nereo.multi_image_selector.MultiImageSelectorActivity
import org.jetbrains.anko.*
import www.morefuntrip.cn.sticker.Bean.BLBeautifyParam

/**
 * 广场动态
 */
class ReleaseNewTrendsActivity : BaseActivity(){

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

    private val sex by lazy{
        SPUtils.instance().getString(Const.User.USER_SEX)
    }

    private var cityType = 1
    private val mNoticeFriendsQuickAdapter by lazy{
         NoticeFriendsQuickAdapter(mChooseFriends)
    }
    private var REQUEST_CHOOSECODE:Int=10
    private  var mChooseFriends = ArrayList<FriendBean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_release_new_trends)
        immersionBar.init()
        city = ""
        rv_images.setHasFixedSize(true)
        rv_images.layoutManager = GridLayoutManager(this, 3) as RecyclerView.LayoutManager?
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
                    , MultiImageSelectorActivity.EXTRA_SELECT_MODE to MultiImageSelectorActivity.MODE_MULTI
                    ,MultiImageSelectorActivity.EXTRA_SELECT_COUNT to c,MultiImageSelectorActivity.EXTRA_SHOW_CAMERA to true
                    ,MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST to urls
            )
        }

        mImages.add(AddImage("res:///" + R.mipmap.comment_addphoto_icon, 1))//ic_add_bg
        addAdapter.notifyDataSetChanged()
        tv_back.setOnClickListener {
            mKeyboardKt.hideKeyboard(it)
            finish()
        }

        locationClient.setLocationListener {
            sysErr("--location--->$it")
            if (it != null) {
                city = it.city
                locationClient.stopLocation()
                if (cityType == 0) {
                    tv_address.text = ""
//                    tv_address1.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_location,0,0,0)//R.mipmap.ic_add1
                    tv_address.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.comment_addlocal_icon,0,0,0)//R.mipmap.ic_add1
//                    tv_address.setTextColor(ContextCompat.getColor(this,R.color.textColor99))
                } else {
                    tv_address.text = city
//                    tv_address1.setCompoundDrawablesWithIntrinsicBounds(,0,0,0)
                    tv_address.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.comment_addlocal_icon,0,R.mipmap.comment_local_del,0)
//                    tv_address.setTextColor(ContextCompat.getColor(this,R.color.orange_f6a))
                }

            }
        }

        tv_release.setOnClickListener {
            isCheckOnLineAuthUser(this,userId){
                publish()
                mKeyboardKt.toggleSoftInput(it)
            }
        }

        tv_address.text = city

        RxPermissions(this).request(Manifest.permission.ACCESS_COARSE_LOCATION).subscribe {
            if (it) {
                startLocation()
            } else {
                cityType=0
                toast("没有定位权限")
                tv_address.text = ""
//                tv_address1.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_location,0,0,0)
//                tv_address.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_add1,0,0,0)
                tv_address.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.comment_addlocal_icon,0,0,0)//R.mipmap.ic_add1

            }
        }

        tv_address.setOnClickListener {
            if (cityType == 1) {//当前显示定位
                cityType = 0
                city = ""
                tv_address.text = ""
//                tv_address1.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_location,0,0,0)
//                tv_address.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_add1,0,0,0)
                tv_address.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.comment_addlocal_icon,0,0,0)//comment_addlocal_icon

            } else {
                cityType = 1
                RxPermissions(this).request(Manifest.permission.ACCESS_COARSE_LOCATION).subscribe {
                    if (it) {
                        if (city.isNullOrEmpty()) {
                            startLocation()
                        } else {
                            tv_address.text = city
//                            tv_address1.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_loc_yellow,0,0,0)
//                            tv_address.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_delete1,0,0,0)
                            tv_address.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.comment_addlocal_icon,0,R.mipmap.comment_local_del,0)
                        }
                    } else {
                        cityType = 0
                        toast("没有定位权限")
                        tv_address.text = ""
//                        tv_address1.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_location,0,0,0)
//                        tv_address.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_add1,0,0,0)
                        tv_address.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.comment_addlocal_icon,0,0,0)
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

        et_content.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if(s.isNullOrEmpty() || TextUtils.isEmpty(et_content.text)){
                    tv_release.backgroundResource = R.drawable.shape_10r_grey
                }else{
                    tv_release.backgroundResource = R.drawable.shape_10r_orange
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        tv_noticeuser.setOnClickListener {
            startActivityForResult<ChooseFriendsActivity>(REQUEST_CHOOSECODE, CHOOSE_Friends to mChooseFriends)
        }

        rv_friends.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        rv_friends.adapter = mNoticeFriendsQuickAdapter
        mNoticeFriendsQuickAdapter.setOnItemChildClickListener { adapter, view, position ->
            if(view.id==R.id.iv_clear||view.id==R.id.ll_clear){
                if(mChooseFriends.size>position){
                    mChooseFriends.removeAt(position)
                    mNoticeFriendsQuickAdapter.notifyDataSetChanged()
                }
            }
        }

        getLocalFriendsCount()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0 && data != null) {
                val result: ArrayList<String> = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT)
                mImages.clear()
                result.forEach {
                    val image = AddImage("file://$it")
                    image.path = it
                    mImages.add(image)///storage/emulated/0/Huawei/MagazineUnlock/magazine-unlock-01-2.3.1104-_9E598779094E2DB3E89366E34B1A6D50.jpg
                }
                mImages.add(AddImage("res:///" + R.mipmap.ic_add_bg, 1))
                addAdapter.notifyDataSetChanged()
                if(mImages.size > 1){
                    tv_release.backgroundResource = R.drawable.shape_10r_orange
                }
//                val path = data.getStringExtra(SelectPhotoDialog.PATH)
//                val size = mImages.size
//                val image = AddImage("file://$path")
////                toast("-图片地址->"+path)
//                image.path = path
//                mImages.add(size - 1, image)
//                addAdapter.notifyDataSetChanged()
            }else if(requestCode == BLBeautifyParam.REQUEST_CODE_BEAUTIFY_IMAGE&& data != null){
                var param = data.getParcelableExtra<BLBeautifyParam>(BLBeautifyParam.RESULT_KEY);
                mImages.clear()
                param.images.forEach {
                    val image = AddImage("file://$it")
                    image.path = it
                    mImages.add(image)///storage/emulated/0/Huawei/MagazineUnlock/magazine-unlock-01-2.3.1104-_9E598779094E2DB3E89366E34B1A6D50.jpg
                }
                mImages.add(AddImage("res:///" + R.mipmap.ic_add_bg, 1))
                addAdapter.notifyDataSetChanged()
            }else if(requestCode == REQUEST_CHOOSECODE && data!=null){
                  mChooseFriends = data!!.getParcelableArrayListExtra(CHOOSE_Friends)
                  mNoticeFriendsQuickAdapter.setNewData(mChooseFriends)
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
                var userIds = getShareUserId(mChooseFriends)
                Request.releaseSquare(userId, tagId, city, it, content,userIds)
            }.request(this,false,success= { _, data ->
                showToast("发布成功")
                if(TextUtils.equals("0",SPUtils.instance().getString(Const.User.USER_SEX))){
                    showTips(data,"发布约会奖励积分","10")
                }
                syncChat(this,"dynamic",sex,userId)
                setResult(Activity.RESULT_OK)
                finish()
            }){code,resMsg->
                if(code == 2){
                    val commonTiphDialog = CommonTipDialog()
                    commonTiphDialog.arguments = bundleOf("resMsg" to resMsg)
                    commonTiphDialog.show(supportFragmentManager, "resMsg")
                }
            }
        } else {
            val city = if (cityType == 0) {
                ""
            } else {
                this.city
            }
            var userIds = getShareUserId(mChooseFriends)
            Log.i("notificeMyFriends",userIds)
            Request.releaseSquare(userId, tagId, city, null, content,userIds).request(this,false,success={
                _, data ->
                showToast("发布成功")
                if(TextUtils.equals("0",SPUtils.instance().getString(Const.User.USER_SEX))){
                    showTips(data,"发布约会奖励积分","10")
                }
                syncChat(this,"dynamic",sex,userId)
                setResult(Activity.RESULT_OK)
                finish()
            }){code,resMsg->
               if(code == 2){
                   val commonTiphDialog = CommonTipDialog()
                   commonTiphDialog.arguments = bundleOf("resMsg" to resMsg)
                   commonTiphDialog.show(supportFragmentManager, "resMsg")
               }
            }
        }
    }

    private fun getLocalFriendsCount(){
//        Request.findFriendCount(userId).request(this,false,success = {msg,json->
//            json?.let {
//                var count = it.optInt("obj")
//                if(count>0){
//                    tv_noticeuser.visibility = View.VISIBLE
//                    rv_friends.visibility = View.VISIBLE
//                }else{
//                    rv_friends.visibility = View.GONE
//                }
//            }
//        })

        Request.findUserFriends(userId,"",1).request(this) { _, data ->
            if(data?.list?.results!=null){
                tv_noticeuser.visibility = View.VISIBLE
                rv_friends.visibility = View.VISIBLE
                view_bottomline.visibility = View.VISIBLE
            }else {
                rv_friends.visibility = View.GONE
                tv_noticeuser.visibility = View.GONE
                view_bottomline.visibility = View.GONE
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
