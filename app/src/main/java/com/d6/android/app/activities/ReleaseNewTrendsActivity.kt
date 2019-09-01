package com.d6.android.app.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import com.amap.api.location.AMapLocationClient
import com.d6.android.app.R
import com.d6.android.app.adapters.AddImageV2Adapter
import com.d6.android.app.adapters.NoticeFriendsQuickAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.CommonTipDialog
import com.d6.android.app.dialogs.SelectUnKnowTypeDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.AddImage
import com.d6.android.app.models.FriendBean
import com.d6.android.app.net.Request
import com.d6.android.app.recoder.RecoderUtil
import com.d6.android.app.recoder.model.AudioChannel
import com.d6.android.app.recoder.model.AudioSampleRate
import com.d6.android.app.recoder.model.AudioSource
import com.d6.android.app.utils.*
import com.d6.android.app.utils.AppUtils.Companion.context
import com.d6.android.app.utils.Const.CHOOSE_Friends
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_release_new_trends.*
import kotlinx.android.synthetic.main.item_audio_square.*
import me.nereo.multi_image_selector.MultiImageSelectorActivity
import me.nereo.multi_image_selector.MultiImageSelectorActivity.PICKER_IMAGE_VIDEO
import omrecorder.AudioChunk
import omrecorder.OmRecorder
import omrecorder.PullTransport
import omrecorder.Recorder
import org.jetbrains.anko.*
import www.morefuntrip.cn.sticker.Bean.BLBeautifyParam
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * 发布广场动态
 */
class ReleaseNewTrendsActivity : BaseActivity(),PullTransport.OnAudioChunkPulledListener, MediaPlayer.OnCompletionListener{

    private var tagId: String? = null
    private var iIsAnonymous:Int = 2

    private var mFrom:String="otherActivity"

    private val IsOpenUnKnow by lazy{
        SPUtils.instance().getString(Const.CHECK_OPEN_UNKNOW)
    }

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

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
    private  var mChooseFriends = ArrayList<FriendBean>()
    private  var topicName =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_release_new_trends)
        immersionBar.init()
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
            val c = 9
            val paths = mImages.filter { it.type!=1 }.map { it.path }
            val urls = ArrayList<String>(paths)
            startActivityForResult<MultiImageSelectorActivity>(0
                    , MultiImageSelectorActivity.EXTRA_SELECT_MODE to MultiImageSelectorActivity.MODE_MULTI
                    ,MultiImageSelectorActivity.EXTRA_SELECT_COUNT to c,MultiImageSelectorActivity.EXTRA_SHOW_CAMERA to true
                    ,MultiImageSelectorActivity.SELECT_MODE to PICKER_IMAGE_VIDEO
                    ,MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST to urls
            )
        }

        mImages.add(AddImage("res:///" + R.mipmap.comment_addphoto_icon, 1))//ic_add_bg
        addAdapter.notifyDataSetChanged()
        tv_back.setOnClickListener {
            mKeyboardKt.hideKeyboard(it)
            selectAudio()
            finish()
        }

        locationClient.setLocationListener {
            sysErr("--location--->$it")
            if (it != null) {
                city = it.city.replace("省","").replace("市","").trim()
                locationClient.stopLocation()
                if (cityType == 0) {
                    tv_address.text = "添加地址"
//                    tv_address1.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_location,0,0,0)//R.mipmap.ic_add1
                    tv_address.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.center_moreicon,0)//R.mipmap.ic_add1
//                    tv_address.setTextColor(ContextCompat.getColor(this,R.color.textColor99))
                } else {
                    tv_address.text = city
//                    tv_address1.setCompoundDrawablesWithIntrinsicBounds(,0,0,0)
                    tv_address.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.comment_local_del,0)
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
                tv_address.text = "添加地址"
//                tv_address1.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_location,0,0,0)
//                tv_address.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_add1,0,0,0)
                tv_address.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.center_moreicon,0)//R.mipmap.ic_add1

            }
        }

        tv_address.setOnClickListener {
            if (cityType == 1) {//当前显示定位
                cityType = 0
                city = ""
                tv_address.text = "添加地址"
//                tv_address1.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_location,0,0,0)
//                tv_address.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_add1,0,0,0)
                tv_address.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.center_moreicon,0)//comment_addlocal_icon

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
                            tv_address.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.comment_local_del,0)
                        }
                    } else {
                        cityType = 0
                        toast("没有定位权限")
                        tv_address.text = ""
//                        R.mipmap.comment_addlocal_icon
//                        tv_address1.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_location,0,0,0)
//                        tv_address.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_add1,0,0,0)
                        tv_address.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.center_moreicon,0)
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

        var mSoftKeyboardStateHelper = SoftKeyboardStateHelper(et_content)
        mSoftKeyboardStateHelper.addSoftKeyboardStateListener(object: SoftKeyboardStateHelper.SoftKeyboardStateListener{
            override fun onSoftKeyboardClosed() {

            }

            override fun onSoftKeyboardOpened(keyboardHeightInPx: Int) {
                var drawable = ContextCompat.getDrawable(context,R.mipmap.input_addpic_gray)
                setLeftDrawable(drawable,tv_img)
                tv_img.textColor = ContextCompat.getColor(context,R.color.color_CDCDCD)

                var drawablevideo = ContextCompat.getDrawable(context,R.mipmap.input_addvideo_gray)
                setLeftDrawable(drawablevideo,tv_video)
                tv_video.textColor = ContextCompat.getColor(context,R.color.color_CDCDCD)

                setPanelIsNotShow(false)
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

        tv_recoder.setOnClickListener {
            hideSoftKeyboard(it)
            rl_recoder.postDelayed(Runnable {
                setPanelTitleUI(3)
            },300)
        }

        tv_delete_audio.setOnClickListener {
            tv_delete_audio.visibility = View.GONE
            rl_play_audio.visibility = View.GONE
            stopPlaying()
        }

        rl_play_audio.setOnClickListener {
            togglePlaying()
        }

//        rl_recoder_circlebar.setOnClickListener {
//            var str  = arrayOf(Manifest.permission.RECORD_AUDIO)
//            PermissionsUtils.getInstance().checkPermissions(this, str,object:PermissionsUtils.IPermissionsResult{
//                override fun forbidPermissions() {
//
//                }
//
//                override fun passPermissions() {
//                    toggleRecording()
//                }
//            })
//        }

        rl_recoder_circlebar.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                var str = arrayOf(Manifest.permission.RECORD_AUDIO)
                PermissionsUtils.getInstance().checkPermissions(this, str, object : PermissionsUtils.IPermissionsResult {
                    override fun forbidPermissions() {

                    }

                    override fun passPermissions() {
                        toggleRecording()
                    }
                })
            } else if (event.action == MotionEvent.ACTION_UP) {
                toggleRecording()
            }
            true
        }

        tv_img.setOnClickListener {
            hideSoftKeyboard(it)
            setPanelTitleUI(1)
            if (mImages.size >= 10) {//最多9张
                showToast("最多上传9张图片")
                return@setOnClickListener
            }
            val c = 9
            val paths = mImages.filter { it.type!=1 }.map { it.path }
            val urls = ArrayList<String>(paths)
            startActivityForResult<MultiImageSelectorActivity>(0
                    , MultiImageSelectorActivity.EXTRA_SELECT_MODE to MultiImageSelectorActivity.MODE_MULTI
                    ,MultiImageSelectorActivity.EXTRA_SELECT_COUNT to c,MultiImageSelectorActivity.EXTRA_SHOW_CAMERA to true
                    ,MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST to urls
            )
        }

//        tv_softinput.setOnClickListener {
//            rl_recoder.visibility = View.GONE
//            showSoftInput(it)
//        }

        ll_unknow_choose.setOnClickListener {
           var mSelectUnknowDialog = SelectUnKnowTypeDialog()
           mSelectUnknowDialog.arguments = bundleOf("type" to "ReleaseNewTrends","IsOpenUnKnow" to IsOpenUnKnow,"code" to mRequestCode,"desc" to sAddPointDesc,"iAddPoint" to iAddPoint,"iRemainPoint" to iRemainPoint)
           mSelectUnknowDialog.show(supportFragmentManager,"unknowdialog")
           mSelectUnknowDialog.setDialogListener { p, s ->
               tv_unknow_choose.text = s
               if(p==1){
                   tv_nmtype.textColor = ContextCompat.getColor(this,R.color.color_8F5A5A)
                   var drawable = ContextCompat.getDrawable(this, R.mipmap.key_small)
                   tv_nmtype.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null)
               }else{
                   tv_nmtype.textColor = ContextCompat.getColor(this,R.color.color_333333)
                   var drawable = ContextCompat.getDrawable(this, R.mipmap.public_small_yellow)
                   tv_nmtype.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null)
               }
               iIsAnonymous = p
           }
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

        if(intent.hasExtra("from")){
            mFrom = intent.getStringExtra("from")
        }

        if(TextUtils.equals("otherActivity",mFrom)){
            iIsAnonymous = 2
            tv_unknow_choose.text = resources.getString(R.string.string_unknow_open)
            tv_nmtype.textColor = ContextCompat.getColor(this,R.color.color_333333)
            var drawable = ContextCompat.getDrawable(this, R.mipmap.public_small_yellow)
            tv_nmtype.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null)
        }else{
            iIsAnonymous = 1
            tv_unknow_choose.text = resources.getString(R.string.string_unknow_unknow)
            tv_nmtype.textColor = ContextCompat.getColor(this,R.color.color_8F5A5A)
            var drawable = ContextCompat.getDrawable(this, R.mipmap.key_small)
            tv_nmtype.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null)
        }
        getLocalFriendsCount()
    }

    override fun onResume() {
        super.onResume()
        if(TextUtils.equals("close",IsOpenUnKnow)){
            sAddPointDesc = "以匿名身份发布动态"
        }else{
            getCheckAnonMouseNums()
        }
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
                tv_topic_choose.text = data.getStringExtra(Const.CHOOSE_TOPIC)
            }
        }
    }

    private fun setPanelIsNotShow(show:Boolean){
        if(show){
            rl_recoder.visibility = View.VISIBLE
            var drawable = ContextCompat.getDrawable(context, R.mipmap.input_addvoice_icon)
            setLeftDrawable(drawable,tv_recoder)
            tv_recoder.textColor = ContextCompat.getColor(this,R.color.color_F7AB00)
        }else{
            rl_recoder.visibility = View.GONE
            var drawable = ContextCompat.getDrawable(context, R.mipmap.input_addvideo_gray_icon)
            setLeftDrawable(drawable,tv_recoder)
            tv_recoder.textColor = ContextCompat.getColor(this,R.color.color_CDCDCD)
        }
    }

    private fun setPanelTitleUI(type:Int){
        if(type==1){
            var drawable = ContextCompat.getDrawable(context,R.mipmap.input_addpic_icon)
            setLeftDrawable(drawable,tv_img)
            tv_img.textColor = ContextCompat.getColor(context,R.color.color_F7AB00)

            var drawablevideo = ContextCompat.getDrawable(context,R.mipmap.input_addvideo_gray)
            setLeftDrawable(drawablevideo,tv_video)
            tv_video.textColor = ContextCompat.getColor(context,R.color.color_CDCDCD)
            setPanelIsNotShow(false)
        }else if(type==2){
            var drawable = ContextCompat.getDrawable(context,R.mipmap.input_addpic_gray)
            setLeftDrawable(drawable,tv_img)
            tv_img.textColor = ContextCompat.getColor(context,R.color.color_CDCDCD)

            var drawablevideo = ContextCompat.getDrawable(context,R.mipmap.input_addvideo_icon)
            setLeftDrawable(drawablevideo,tv_video)
            tv_video.textColor = ContextCompat.getColor(context,R.color.color_F7AB00)
            setPanelIsNotShow(false)
        }else if(type==3){
            var drawable = ContextCompat.getDrawable(context,R.mipmap.input_addpic_gray)
            setLeftDrawable(drawable,tv_img)
            tv_img.textColor = ContextCompat.getColor(context,R.color.color_CDCDCD)

            var drawablevideo = ContextCompat.getDrawable(context,R.mipmap.input_addvideo_gray)
            setLeftDrawable(drawablevideo,tv_video)
            tv_video.textColor = ContextCompat.getColor(context,R.color.color_CDCDCD)
            setPanelIsNotShow(rl_recoder.visibility == View.GONE)
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
                Request.releaseSquare(userId, tagId, city, it, content,userIds,iIsAnonymous)
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
            Request.releaseSquare(userId, tagId, city, null, content,userIds,iIsAnonymous).request(this,false,success={
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

    private var mRequestCode:Int = 1
    private var sAddPointDesc="以匿名身份发布动态"
    private var iAddPoint :String= "" //匿名发布需要消耗的积分
    private var iRemainPoint:String="" //剩余积分

    private fun getCheckAnonMouseNums(){
        Request.getAnonymouseAppointmentPoint(getLoginToken(),1).request(this,false,success = {msg,jsonObject->

        }){code,msg->
            mRequestCode = code
            if(code==2){
                if(msg.isNotEmpty()){
                    var jsonobject = org.json.JSONObject(msg)
//                    var iRemainCount = jsonobject.optString("iRemainCount")
                    sAddPointDesc = jsonobject.optString("sAddPointDesc")
                }
            }else if(code==3){
                if(msg.isNotEmpty()){
                    var jsonobject = org.json.JSONObject(msg)
//                    var iRemainCount = jsonobject.optString("iRemainCount")//还有匿名次数
                    sAddPointDesc = jsonobject.optString("sAddPointDesc")//剩余匿名次数描述
                    iRemainPoint = jsonobject.optString("iRemainPoint")//iRemainPoint 剩余积分
                    iAddPoint = jsonobject.optString("iAddPoint")//匿名发布需要消耗的积分
                }
            }else if(code == 4){
                if(msg.isNotEmpty()){
                    var jsonobject = org.json.JSONObject(msg)
//                    var iRemainCount = jsonobject.optString("iRemainCount")//还有匿名次数
                    sAddPointDesc = jsonobject.optString("sAddPointDesc")//剩余匿名次数描述
                    iRemainPoint = jsonobject.optString("iRemainPoint")//iRemainPoint 剩余积分
                    iAddPoint = jsonobject.optString("iAddPoint")//匿名发布需要消耗的积分
                }
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

    //开始播放音频
    private fun startPlayAudioView(){
        tv_delete_audio.visibility = View.VISIBLE
        iv_playaudio.setImageResource(R.drawable.drawable_play_voice)
        starPlayDrawableAnim(iv_playaudio)
    }

    //停止播放音频
    private fun stopPlayAudioView(){
        stopPlayDrawableAnim(iv_playaudio)
        iv_playaudio.setImageResource(R.mipmap.liveroom_recording3)
    }

    private fun setAudioView(Recoding:Boolean){
        if(Recoding){
            if(recorderSecondsElapsed>3){
                record.background = ContextCompat.getDrawable(context,R.mipmap.voice_pedestal)
                tv_delete_audio.visibility = View.VISIBLE
                setRlPlayAudioWidth()
                tv_audio_time.text = "${recorderSecondsElapsed}”"
            }else{
                record.background = ContextCompat.getDrawable(context,R.mipmap.voice_pedestal)
                toast(getString(R.string.string_warm_voicetime))
            }
        }else{
            circlebarview.startDefaultProgress()
            record.background = ContextCompat.getDrawable(context,R.mipmap.voice_pedestal_press)
        }
    }

    //设置播放录音条的宽度
    private fun setRlPlayAudioWidth(){
        rl_play_audio.visibility = View.VISIBLE
        var param = rl_play_audio.layoutParams
        param.width = (resources.getDimensionPixelSize(R.dimen.width_100) + resources.getDimensionPixelSize(R.dimen.width_100)/60*recorderSecondsElapsed)
        rl_play_audio.layoutParams = param
    }

    private var isRecording: Boolean = false
    private var recorderSecondsElapsed: Int = 0
    private var playerSecondsElapsed: Int = 0
    private var timer: Timer? = null
    private var player: MediaPlayer? = null
    private var recorder: Recorder? = null
    private var autoStart: Boolean = false
    private var filePath = Environment.getExternalStorageDirectory().toString() + "/recorded_audio.mp3"

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        if (autoStart&&!isRecording) {
            toggleRecording()
        }
    }

    //录制
    fun toggleRecording() {
        stopPlaying()
        RecoderUtil.wait(100, Runnable {
            if (isRecording) {
//                pauseRecording()
                setAudioView(isRecording)
                restartRecording()
            } else {
                setAudioView(isRecording)
                resumeRecording()
            }
        })
    }

    //播放
    fun togglePlaying() {
        pauseRecording()
        RecoderUtil.wait(100, Runnable {
            if (isPlaying()) {
                stopPlaying()
            } else {
                startPlayAudioView()
                startPlaying()
            }
        })
    }

    private fun pauseRecording() {
        isRecording = false
        if (recorder != null) {
            recorder?.pauseRecording()
        }
        stopTimer()
    }

    fun restartRecording() {
        if (isRecording) {
            stopRecording()
        } else if (isPlaying()) {
            stopPlaying()
        } else {
        }
        isRecording = false //特殊加的
        tv_recoder_time.text = getString(R.string.string_record_audio)
        recorderSecondsElapsed = 0
        playerSecondsElapsed = 0
    }

    private fun resumeRecording() {
        isRecording = true

        if (recorder == null) {
            tv_recoder_time.setText(getString(R.string.string_record_audio))

            recorder = OmRecorder.wav(
                    PullTransport.Default(RecoderUtil.getMic(AudioSource.MIC, AudioChannel.STEREO, AudioSampleRate.HZ_44100), this@ReleaseNewTrendsActivity),
                    File(filePath))
        }
        recorder?.let {
            it.resumeRecording()
        }
        startTimer()
    }


    private fun startPlaying() {
        try {
            stopRecording()
            player = MediaPlayer()
            player?.let {
                it.setDataSource(filePath)
                it.prepare()
                it.start()
                it.setOnCompletionListener(this@ReleaseNewTrendsActivity)
            }
            tv_recoder_time.setText("点击开始录制")
            playerSecondsElapsed = 0
            startTimer()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopRecording() {
        recorderSecondsElapsed = 0
        if (recorder != null) {
            recorder?.stopRecording()
            recorder = null
        }
        circlebarview.stopProgressNum()
        stopTimer()
    }

    private fun stopPlaying() {
        if (player != null) {
            try {
                player!!.stop()
                player!!.reset()
                stopPlayAudioView()
            } catch (e: Exception) {
            }
        }
        stopTimer()
    }

    private fun isPlaying(): Boolean {
        try {
            return player != null && player!!.isPlaying() && !isRecording
        } catch (e: Exception) {
            return false
        }
    }

    private fun startTimer() {
        stopTimer()
        timer = Timer()
        timer?.let {
            it.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    updateTimer()
                }
            }, 0, 1000)
        }
    }

    private fun stopTimer() {
        if (timer != null) {
            timer?.let {
                it.cancel()
                it.purge()
            }
            timer = null
        }
    }

    private fun updateTimer() {
        runOnUiThread {
            if (isRecording) {
                recorderSecondsElapsed++
                tv_recoder_time.text = "正在录制·${recorderSecondsElapsed}S"
            } else if (isPlaying()) {
                playerSecondsElapsed++
            }
        }
    }

    private fun selectAudio() {
        stopRecording()
    }

    override fun onAudioChunkPulled(audioChunk: AudioChunk?) {
        val amplitude = if (isRecording) audioChunk?.maxAmplitude()?.toFloat() else 0f
        Log.i("audio", "大小$amplitude")
    }

    override fun onCompletion(mp: MediaPlayer?) {
        stopPlaying()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionsUtils.getInstance().onRequestPermissionsResult(this,requestCode,permissions,grantResults);
    }
}
