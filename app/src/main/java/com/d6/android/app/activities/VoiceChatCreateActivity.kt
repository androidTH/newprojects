package com.d6.android.app.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.media.ThumbnailUtils
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import com.amap.api.location.AMapLocationClient
import com.d6.android.app.R
import com.d6.android.app.adapters.AddImageV2Adapter
import com.d6.android.app.adapters.NoticeFriendsQuickAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.CommonTipDialog
import com.d6.android.app.dialogs.VoiceChatTypeDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.AddImage
import com.d6.android.app.models.FriendBean
import com.d6.android.app.models.Imagelocals
import com.d6.android.app.models.TopicBean
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.AppUtils.Companion.context
import com.d6.android.app.utils.Const.CHOOSE_Friends
import com.d6.android.app.widget.ObserverManager
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_create_voicechat.*
import me.nereo.multi_image_selector.MultiImageSelectorActivity
import org.jetbrains.anko.*
import www.morefuntrip.cn.sticker.Bean.BLBeautifyParam
import java.util.*
import kotlin.collections.ArrayList

/**
 * 语音连麦
 */
class VoiceChatCreateActivity : BaseActivity(),Observer{

    private var tagId: String? = null
    private var iIsAnonymous:Int = 2

    private val mImages = ArrayList<AddImage>()
    private val addAdapter by lazy {
        AddImageV2Adapter(mImages)
    }

    private var city:String=""

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
    private var REQUEST_TOPICCODE:Int=11
    private var mChooseFriends = ArrayList<FriendBean>()
    private var REQUESTCODE_IMAGE = 0

    private var sTopicId:String = ""
    private var mVoiceChatType = 0;

    /**
     * 删除图片后更新数据
     */
    override fun update(o: Observable?, arg: Any?) {
          var mImagelocal = arg as Imagelocals
          if(mImagelocal.mType == 0){
              mImages.filter { it.type!=1 }.map {
                  if(mImages.size>mImagelocal.position){
                      mImages.removeAt(mImagelocal.position)
                  }
              }

              if(mImages.size==1){
                 mImages.clear()
                 mImages.add(AddImage("res:///" + R.mipmap.comment_addphoto_icon, 1))
              }
          }else if(mImagelocal.mType == 1){
              mImages.clear()
              mImagelocal.mUrls.forEach {
                  val image = AddImage("file://${it}")
                  image.path = it
                  mImages.add(image)
              }
              mImages.add(AddImage("res:///" + R.mipmap.comment_addphoto_icon, 1))
          }
          addAdapter.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_voicechat)
        immersionBar.init()
        ObserverManager.getInstance().addObserver(this)
        rv_images.setHasFixedSize(true)
        rv_images.layoutManager = GridLayoutManager(this, 3) as RecyclerView.LayoutManager?
        rv_images.adapter = addAdapter
        rv_images.isNestedScrollingEnabled = false
        rv_images.addItemDecoration(SpacesItemDecoration(dip(6),3))
        addAdapter.setOnAddClickListener {
            if(it==1){
                addImagesToSquare()
            }
        }

        mImages.add(AddImage("res:///" + R.mipmap.comment_addphoto_icon, 1))//ic_add_bg
        addAdapter.notifyDataSetChanged()
        tv_back.setOnClickListener {
            mKeyboardKt.hideKeyboard(it)
            onBackPressed()
        }

        locationClient.setLocationListener {
            if (it != null) {
                city = it.city.replace("省","").replace("市","").trim()
                locationClient.stopLocation()
                if (cityType == 0) {
                    tv_address.text = "添加地址"
                    tv_address.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.center_moreicon,0)//R.mipmap.ic_add1
                } else {
                    tv_address.text = city
                    tv_address.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.comment_local_del,0)
                }

            }
        }

        tv_release.setOnClickListener {
            isCheckOnLineAuthUser(this, getLocalUserId()){
                if(!isFastClick()){
                    submitAddSquare()
                    mKeyboardKt.toggleSoftInput(it)
                }
            }
        }

        tv_address.text = city

        RxPermissions(this).request(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.RECORD_AUDIO).subscribe {
            if (it) {
                startLocation()
                SPUtils.instance().put(Const.User.ISNOTLOCATION,false).apply()
            } else {
                cityType=0
                toast("没有定位权限")
                tv_address.text = "添加地址"
                tv_address.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.center_moreicon,0)//R.mipmap.ic_add1
                SPUtils.instance().put(Const.User.ISNOTLOCATION,true).apply()
            }
        }

        tv_address.setOnClickListener {
            if (cityType == 1) {//当前显示定位
                cityType = 0
                city = ""
                tv_address.text = "添加地址"
                tv_address.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.center_moreicon,0)//comment_addlocal_icon

            } else {
                cityType = 1
                RxPermissions(this).request(Manifest.permission.ACCESS_COARSE_LOCATION).subscribe {
                    if (it) {
                        if (city.isNullOrEmpty()) {
                            startLocation()
                        } else {
                            tv_address.text = city
                            tv_address.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.comment_local_del,0)
                        }
                    } else {
                        cityType = 0
                        toast("没有定位权限")
                        tv_address.text = ""
                        tv_address.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.center_moreicon,0)
                    }
                }
            }
        }

        tv_topic_choose.setOnClickListener {
            var topicname = tv_topic_choose.text.toString()
            if(topicname.isNotEmpty()){
                tv_topic_choose.text = ""
                sTopicId = ""
                tv_topic_choose.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.center_moreicon,0)//R.mipmap.ic_add1
            }
        }

        et_content.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) =
                    if(s.isNullOrEmpty() || TextUtils.isEmpty(et_content.text)){
                        tv_release.backgroundDrawable = ContextCompat.getDrawable(context,R.drawable.shape_10r_grey)
                    }else{
                        tv_release.backgroundDrawable = ContextCompat.getDrawable(context,R.drawable.shape_10r_orange)
                    }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        tv_noticeuser.setOnClickListener {
            startActivityForResult<ChooseFriendsActivity>(REQUEST_CHOOSECODE, CHOOSE_Friends to mChooseFriends)
        }

        rl_noticefriends.setOnClickListener {
            startActivityForResult<ChooseFriendsActivity>(REQUEST_CHOOSECODE, CHOOSE_Friends to mChooseFriends)
        }

        ll_topic_choose.setOnClickListener {
            startActivityForResult<TopicSelectionActivity>(REQUEST_TOPICCODE)
        }

        ll_voicechat_choose.setOnClickListener {
              var mVoiceChatTypeDialog = VoiceChatTypeDialog()
              mVoiceChatTypeDialog.arguments = bundleOf("chooseType" to "0")
              mVoiceChatTypeDialog.setDialogListener { p, s ->
                  tv_voicechat_choose.text = s
                  mVoiceChatType = p
              }
              mVoiceChatTypeDialog.show(supportFragmentManager,"VoiceChatTypeDialog")
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

        if(intent.hasExtra("topicname")){
            var topicname = intent.getStringExtra("topicname")
            sTopicId = intent.getStringExtra("topicId")
            if(topicname.isNotEmpty()){
                tv_topic_choose.text = "#${topicname}"
                tv_topic_choose.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.comment_local_del,0)//R.mipmap.ic_add1
            }
        }

    }

    private fun addImagesToSquare(){
        addImages()
    }

    private fun addImages(){
        if (mImages.size >= 10) {//最多9张
            showToast("最多上传9张图片")
            return
        }
        val c = 9
        val paths = mImages.filter { it.type!=1 }.map { it.path }
        val urls = ArrayList<String>(paths)
        startActivityForResult<MultiImageSelectorActivity>(REQUESTCODE_IMAGE
                , MultiImageSelectorActivity.EXTRA_SELECT_MODE to MultiImageSelectorActivity.MODE_MULTI
                ,MultiImageSelectorActivity.EXTRA_SELECT_COUNT to c,MultiImageSelectorActivity.EXTRA_SHOW_CAMERA to true
                ,MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST to urls

        )
    }

    override fun onResume() {
        super.onResume()
//        if(TextUtils.equals("close",IsOpenUnKnow)){
//            sAddPointDesc = "以匿名身份发布动态"
//        }else{
//            getCheckAnonMouseNums()
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUESTCODE_IMAGE && data != null) {
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
                    showSoftInput(et_content)
                    tv_release.backgroundResource = R.drawable.shape_10r_orange
                }
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
            }else if(requestCode==REQUEST_TOPICCODE&&data!=null){
                var mTopicBean = data.getParcelableExtra<TopicBean>(Const.CHOOSE_TOPIC)
                if(mTopicBean!=null){
                    tv_topic_choose.text = "#${mTopicBean.sTopicName}"
                    sTopicId = mTopicBean.sId
                    tv_topic_choose.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.comment_local_del,0)//R.mipmap.ic_add1
                }
            }
        }
    }

    /**
     * 发布图片动态
     */
    private fun publish() {
        val content = et_content.text.toString().trim()
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
//                var userIds = getShareUserId(mChooseFriends)
                Request.releaseSquare(getLocalUserId(), tagId, city, it, content,"",iIsAnonymous,sTopicId,"","","","","","")
            }.request(this,false,success= { _, data ->
                showToast("发布成功")
                if(TextUtils.equals("0",SPUtils.instance().getString(Const.User.USER_SEX))){
                    showTips(data,"发布约会奖励积分","10")
                }
                syncChat(this,"dynamic",sex, getLocalUserId())
                setResult(Activity.RESULT_OK)
//                FinishActivityManager.getManager().finishActivity()
                finish()
            }){code,resMsg->
                if(code == 2){
                    val commonTiphDialog = CommonTipDialog()
                    commonTiphDialog.arguments = bundleOf("resMsg" to resMsg)
                    commonTiphDialog.show(supportFragmentManager, "resMsg")
                }
            }
        } else {
            addTextSquare(content)
        }
    }

    fun addTextSquare(content:String){
        if (content.isEmpty()) {
            showToast("请输入内容")
            return
        }
        val city = if (cityType == 0) {
            ""
        } else {
            this.city
        }
        Request.releaseSquare(getLocalUserId(), tagId, city, null, content,"",iIsAnonymous,sTopicId,"","","","","","").request(this,false,success={
            _, data ->
            showToast("发布成功")
            if(TextUtils.equals("0",SPUtils.instance().getString(Const.User.USER_SEX))){
                showTips(data,"发布约会奖励积分","10")
            }
            syncChat(this,"dynamic",sex, getLocalUserId())
            setResult(Activity.RESULT_OK)
//            FinishActivityManager.getManager().finishActivity()
            finish()
        }){code,resMsg->
            if(code == 2){
                val commonTiphDialog = CommonTipDialog()
                commonTiphDialog.arguments = bundleOf("resMsg" to resMsg)
                commonTiphDialog.show(supportFragmentManager, "resMsg")
            }
        }
    }


    private fun submitAddSquare(){
        if (mImages.size>0&&mImages[0].type == 0) {
            publish()
        } else {
            //发布文字
            val content = et_content.text.toString().trim()
            if (content.isEmpty()) {
                showToast("请输入内容")
                return
            }
            dialog()
            addTextSquare(content)
        }
    }

    private fun getLocalFriendsCount(){
        Request.findUserFriends(getLocalUserId(),"",1).request(this) { _, data ->
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

    private var mRequestCode:Int = 1
    private var iAddPoint :String= "" //匿名发布需要消耗的积分
    private var iRemainPoint:String="" //剩余积分

    private fun startLocation() {
        tv_address.text = "定位中..."
        locationClient.stopLocation()
        locationClient.startLocation()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        locationClient.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionsUtils.getInstance().onRequestPermissionsResult(this,requestCode,permissions,grantResults);
    }
}