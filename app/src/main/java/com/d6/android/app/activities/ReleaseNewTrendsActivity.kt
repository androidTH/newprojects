package com.d6.android.app.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.media.ThumbnailUtils
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
import com.amap.api.location.AMapLocationClient
import com.d6.android.app.R
import com.d6.android.app.adapters.AddImageV2Adapter
import com.d6.android.app.adapters.NoticeFriendsQuickAdapter
import com.d6.android.app.audioconverter.AndroidAudioConverter
import com.d6.android.app.audioconverter.callback.IConvertCallback
import com.d6.android.app.audioconverter.model.AudioFormat
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.CommonTipDialog
import com.d6.android.app.dialogs.SelectUnKnowTypeDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.AddImage
import com.d6.android.app.models.FriendBean
import com.d6.android.app.models.Imagelocals
import com.d6.android.app.models.TopicBean
import com.d6.android.app.net.Request
import com.d6.android.app.recoder.RecoderUtil
import com.d6.android.app.recoder.model.AudioChannel
import com.d6.android.app.recoder.model.AudioSampleRate
import com.d6.android.app.recoder.model.AudioSource
import com.d6.android.app.utils.*
import com.d6.android.app.utils.AppUtils.Companion.context
import com.d6.android.app.utils.Const.CHOOSE_Friends
import com.d6.android.app.widget.ObserverManager
import com.d6.android.app.widget.diskcache.DiskFileUtils
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_release_new_trends.*
import kotlinx.android.synthetic.main.item_audio_square.*
import me.nereo.multi_image_selector.MultiImageSelectorActivity
import me.nereo.multi_image_selector.MultiImageSelectorActivity.PICKER_VIDEO
import me.nereo.multi_image_selector.utils.FinishActivityManager
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
class ReleaseNewTrendsActivity : BaseActivity(),PullTransport.OnAudioChunkPulledListener, MediaPlayer.OnCompletionListener,Observer{

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

    private var MAXTIME = 59 //录音最大时长
    private var MINTIME = 3 //语音最小时长

    private var REQUEST_CHOOSECODE:Int=10
    private var REQUEST_TOPICCODE:Int=11
    private var mChooseFriends = ArrayList<FriendBean>()
    private var REQUESTCODE_VIDEO = 1
    private var REQUESTCODE_IMAGE = 0

    private var sTopicId:String = ""
    private var sVideoUrl:String = ""
    private var sVideoPicUrl:String = ""
    private var sVoiceUrl:String = ""
    private var VideoPaths:ArrayList<String> = ArrayList<String>()
    private var mVideoWidth:Int = 0
    private var mVideoHeight:Int = 0

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
                 setPanelTitleUI(4)
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
        setContentView(R.layout.activity_release_new_trends)
        immersionBar.init()
        ObserverManager.getInstance().addObserver(this)
        rv_images.setHasFixedSize(true)
        rv_images.layoutManager = GridLayoutManager(this, 3) as RecyclerView.LayoutManager?
        rv_images.adapter = addAdapter
        rv_images.isNestedScrollingEnabled = false
        rv_images.addItemDecoration(SpacesItemDecoration(dip(6),3))
        addAdapter.setOnAddClickListener {
            if(it==1){
                addImagesToSquare(tv_img)
            }else if(it==2){
                setPanelTitleUI(4)
            }
        }

        //mImages.add(AddImage("res:///" + R.mipmap.comment_addphoto_icon, 0))//ic_add_bg
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
            isCheckOnLineAuthUser(this,userId){
                submitAddSquare()
                mKeyboardKt.toggleSoftInput(it)
            }
        }

        tv_address.text = city

        RxPermissions(this).request(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.RECORD_AUDIO).subscribe {
            if (it) {
                startLocation()
            } else {
                cityType=0
                toast("没有定位权限")
                tv_address.text = "添加地址"
                tv_address.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.center_moreicon,0)//R.mipmap.ic_add1

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

        var mSoftKeyboardStateHelper = SoftKeyboardStateHelper(et_content)
        mSoftKeyboardStateHelper.addSoftKeyboardStateListener(object: SoftKeyboardStateHelper.SoftKeyboardStateListener{
            override fun onSoftKeyboardClosed() {
            }

            override fun onSoftKeyboardOpened(keyboardHeightInPx: Int) {

                if(mImages.size>=1&&mImages[0].type==0){
                    setPanelTitleUI(1)
                }else if(mImages.size>=1&&mImages[0].type==2){
                    setPanelTitleUI(2)
                }else if(DiskFileUtils.IsExists(fileAudioPath)){
                    var drawable = ContextCompat.getDrawable(context,R.mipmap.input_addpic_gray)
                    setLeftDrawable(drawable,tv_img)
                    tv_img.textColor = ContextCompat.getColor(context,R.color.color_CDCDCD)

                    var drawablevideo = ContextCompat.getDrawable(context,R.mipmap.input_addvideo_gray)
                    setLeftDrawable(drawablevideo,tv_video)
                    tv_video.textColor = ContextCompat.getColor(context,R.color.color_CDCDCD)

                    rl_recoder.visibility = View.GONE
                    var drawableRecoder = ContextCompat.getDrawable(context, R.mipmap.input_addvoice_icon)
                    setLeftDrawable(drawableRecoder,tv_recoder)
                    tv_recoder.textColor = ContextCompat.getColor(context,R.color.color_F7AB00)
                }else{
                    setPanelTitleUI(4)
                }
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
            if(mImages.size>=1&&mImages[0].type==0){
                clearImgsOrVideoDialog(it,"图片、视频、语音不能同时添加，是否清空已添加的图片")
            }else if(mImages.size>=1&&mImages[0].type==2){
                clearImgsOrVideoDialog(it,"图片、视频、语音不能同时添加，是否清空已添加的视频")
            }else{
                if(DiskFileUtils.IsExists(fileAudioPath)){
                    toast("不能重复添加语音")
                    return@setOnClickListener
                }
                hideSoftKeyboard(it)
                rl_recoder.postDelayed(Runnable {
                    setPanelTitleUI(3)
                },300)
            }
        }

        tv_delete_audio.setOnClickListener {
            tv_delete_audio.visibility = View.INVISIBLE
            rl_play_audio.visibility = View.INVISIBLE
            stopPlaying()
            DiskFileUtils.deleteSingleFile(fileAudioPath)
            tv_release.backgroundResource = R.drawable.shape_10r_grey

            setPanelTitleUI(4)
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
                moveupFlag = false
                PermissionsUtils.getInstance().checkPermissions(this, str, object : PermissionsUtils.IPermissionsResult {
                    override fun forbidPermissions() {

                    }

                    override fun passPermissions() {
                        toggleRecording()
                    }
                })
            } else if (event.action == MotionEvent.ACTION_UP) {
                if(!moveupFlag){
                    toggleRecording()
                }
            }
            true
        }

        tv_video.setOnClickListener {
            if(mImages.size>=1&&mImages[0].type==0){
                var  mDialogYesOrNo = getDialogIsorNot(this,1,"图片、视频、语音不能同时添加，是否清空已添加的图片")
                mDialogYesOrNo.setDialogListener { p, s ->
                    mImages.clear()
//                    mImages.add(AddImage("res:///" + R.mipmap.ic_add_bg, 1))
                    addAdapter.notifyDataSetChanged()
                    addVideo(it)
                    setPanelTitleUI(4)
                    mDialogYesOrNo.dismissAllowingStateLoss()
                }
            }else if(DiskFileUtils.IsExists(fileAudioPath)){
                var  mDialogYesOrNo = getDialogIsorNot(this,1,"图片、视频、语音不能同时添加，是否清空已添加的音频")
                mDialogYesOrNo.setDialogListener { p, s ->
                    stopPlaying()
                    DiskFileUtils.deleteSingleFile(fileAudioPath)
                    tv_delete_audio.visibility = View.GONE
                    rl_play_audio.visibility = View.GONE
                    addVideo(it)
                    setPanelTitleUI(4)
                    mDialogYesOrNo.dismissAllowingStateLoss()
                }
            }else{
                if(mImages.size>0&&mImages[0].type==2){
                    toast("不能重复添加视频")
                    return@setOnClickListener
                }
                addVideo(it)
            }
        }

        tv_img.setOnClickListener {
            addImagesToSquare(it)
        }

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

        if(intent.hasExtra("topicname")){
            var topicname = intent.getStringExtra("topicname")
            sTopicId = intent.getStringExtra("topicId")
            if(topicname.isNotEmpty()){
                tv_topic_choose.text = "#${topicname}"
                tv_topic_choose.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.comment_local_del,0)//R.mipmap.ic_add1
            }
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
    }

    private fun addImagesToSquare(it:View){
        if(mImages.size>=1&&mImages[0].type==2){
            //图片、视频、语音不能同时添加，是否清空已添加的xx
            var  mDialogYesOrNo =  getDialogIsorNot(this,1,"图片、视频、语音不能同时添加，是否清空已添加的视频")
            mDialogYesOrNo.setDialogListener { p, s ->
                mImages.clear()
//                mImages.add(AddImage("res:///" + R.mipmap.ic_add_bg, 1))
                addAdapter.notifyDataSetChanged()
                addImages(it)
                setPanelTitleUI(4)
                mDialogYesOrNo.dismissAllowingStateLoss()
            }
        }else if(DiskFileUtils.IsExists(fileAudioPath)){
            var  mDialogYesOrNo = getDialogIsorNot(this,1,"图片、视频、语音不能同时添加，是否清空已添加的音频")
            mDialogYesOrNo.setDialogListener { p, s ->
                stopPlaying()
                DiskFileUtils.deleteSingleFile(fileAudioPath)
                tv_delete_audio.visibility = View.GONE
                rl_play_audio.visibility = View.GONE
                addImages(it)
                setPanelTitleUI(4)
                mDialogYesOrNo.dismissAllowingStateLoss()
            }
        }else{
            addImages(it)
        }
    }

    //录音dialog
    private fun clearImgsOrVideoDialog(it:View,msg:String){
        var  mDialogYesOrNo =  getDialogIsorNot(this,1,msg)
        mDialogYesOrNo.setDialogListener { p, s ->
            mImages.clear()
//            mImages.add(AddImage("res:///" + R.mipmap.ic_add_bg, 1))
            addAdapter.notifyDataSetChanged()
            mDialogYesOrNo.dismissAllowingStateLoss()

            hideSoftKeyboard(it)
            rl_recoder.postDelayed(Runnable {
                setPanelTitleUI(3)
            },300)
        }
    }

    private fun addVideo(it:View){
        hideSoftKeyboard(it)
        //移除
//        val paths = mImages.filter { it.type != 1 }.map { it.path }
//        val urls = ArrayList<String>(paths)
        startActivityForResult<MultiImageSelectorActivity>(REQUESTCODE_VIDEO
                , MultiImageSelectorActivity.EXTRA_SELECT_MODE to MultiImageSelectorActivity.MODE_SINGLE
                , MultiImageSelectorActivity.EXTRA_SELECT_COUNT to 1, MultiImageSelectorActivity.EXTRA_SHOW_CAMERA to false
                , MultiImageSelectorActivity.SELECT_MODE to PICKER_VIDEO
        )
//        val intent = Intent()
//        if (Build.VERSION.SDK_INT < 19) {
//            intent.action = Intent.ACTION_GET_CONTENT
//            intent.type = "video/*"
//        } else {
//            intent.action = Intent.ACTION_OPEN_DOCUMENT
//            intent.addCategory(Intent.CATEGORY_OPENABLE)
//            intent.type = "video/*"
//        }
//        startActivityForResult(Intent.createChooser(intent, "选择要导入的视频"), REQUESTCODE_VIDEO)
    }

    private fun addImages(it:View){
        hideSoftKeyboard(it) //移除
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
        if(TextUtils.equals("close",IsOpenUnKnow)){
            sAddPointDesc = "以匿名身份发布动态"
        }else{
            getCheckAnonMouseNums()
        }
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
//                    setPanelTitleUI(1)
                    showSoftInput(et_content)
                    tv_release.backgroundResource = R.drawable.shape_10r_orange
                }
            }else if(requestCode == REQUESTCODE_VIDEO && data != null){
                VideoPaths = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT)
//                VideoPaths = result[0].split(FileUtils.VideoThumbnail)
                var mBitmap = ThumbnailUtils.createVideoThumbnail(VideoPaths[0],0)
                if(mBitmap!=null){
                    mImages.clear()
                    val image = AddImage("file://${VideoPaths[0]}",2)
                    image.path = VideoPaths[0]
                    mImages.add(image)
                    VideoPaths.add(BitmapUtils.saveImageToFile(mBitmap))
                    mVideoHeight = mBitmap.height
                    mVideoWidth = mBitmap.width
                    mBitmap.recycle()
                    addAdapter.notifyDataSetChanged()
                    Log.i("releaseautio","${VideoPaths[1]}视频地址:${VideoPaths[0]},视频的宽：${mVideoWidth},高：${mVideoHeight}")
                }else{
                    toast(getString(R.string.video_error_string))
                }
//                mImages.clear()
//                val selectedFilepath = GetPathFromUri.getPath(this, data.data)
//                Log.i("onActivityResult","视频路径${selectedFilepath}")
//                if (selectedFilepath != null && "" != selectedFilepath) {
//                    val image = AddImage("file://${selectedFilepath}",2)
//                    image.path = selectedFilepath
//                    mImages.add(image)
//                }
                if(mImages.size == 1){
                    showSoftInput(et_content)
//                    setPanelTitleUI(2)
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
        }else if(type==4){
            var drawable = ContextCompat.getDrawable(context,R.mipmap.input_addpic_icon)
            setLeftDrawable(drawable,tv_img)
            tv_img.textColor = ContextCompat.getColor(context,R.color.color_F7AB00)

            var drawablevideo = ContextCompat.getDrawable(context,R.mipmap.input_addvideo_icon)
            setLeftDrawable(drawablevideo,tv_video)
            tv_video.textColor = ContextCompat.getColor(context,R.color.color_F7AB00)

            rl_recoder.visibility = View.GONE
            var drawableRecoder = ContextCompat.getDrawable(context, R.mipmap.input_addvoice_icon)
            setLeftDrawable(drawableRecoder,tv_recoder)
            tv_recoder.textColor = ContextCompat.getColor(context,R.color.color_F7AB00)
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
                Request.releaseSquare(userId, tagId, city, it, content,"",iIsAnonymous,sTopicId,"","","","","","")
            }.request(this,false,success= { _, data ->
                showToast("发布成功")
                if(TextUtils.equals("0",SPUtils.instance().getString(Const.User.USER_SEX))){
                    showTips(data,"发布约会奖励积分","10")
                }
                syncChat(this,"dynamic",sex,userId)
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

    /**
     * 上传音频
     */
    fun submitAudioSquare(){
        val content = et_content.text.toString().trim()
        dialog()
        if (DiskFileUtils.IsExists(fileAudioPath)) {//有语音
            if(fileAudioPath.endsWith(".wav")){
                AudioConvert(content)
            }else{
                ConvertSuccess(File(fileAudioPath),content)
            }
        } else {
            addTextSquare(content)
        }
    }

    private fun AudioConvert(content:String){
        val callback = object : IConvertCallback {
            override fun onSuccess(convertedFile: File) {
                ConvertSuccess(convertedFile,content)
            }

            override fun onFailure(error: Exception) {

            }
        }

        AndroidAudioConverter.with(this)
                .setFile(File(fileAudioPath))
                .setFormat(AudioFormat.MP3)
                .setCallback(callback)
                .convert()
    }

    /**
     * 音频转码成功
     */
    private fun ConvertSuccess(file:File,content:String){
        Request.uploadFile(file,1).flatMap {
            Log.i("releaseautio","${fileAudioPath}音频地址:"+it)
            Request.releaseSquare(userId, tagId, city, "", content,"",iIsAnonymous,sTopicId,"","","","",it,mVoiceLength)
        }.request(this,false,success= { _, data ->
            showToast("发布成功")
            DiskFileUtils.deleteSingleFile(fileAudioPath)
            if(TextUtils.equals("0",SPUtils.instance().getString(Const.User.USER_SEX))){
                showTips(data,"发布约会奖励积分","10")
            }
            syncChat(this,"dynamic",sex,userId)
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

    /**
     * 上传视频
     */
    fun submitVideoSquare(){
        val content = et_content.text.toString().trim()
        dialog()
        if (mImages[0].type==2) {//有视频
            val temp = File(mImages[0].path)
            Flowable.fromIterable(VideoPaths).subscribeOn(Schedulers.io()).flatMap {
                //压缩
                val b = File(it)
                Flowable.just(b)
            }.flatMap {
                Log.i("releaseautio","地址"+it.path)
                Request.uploadFile(it)
            }.toList().toFlowable().flatMap {
                Flowable.just(it)
            }.flatMap {
               Log.i("releaseautio",it[1]+"视频地址"+it[0]+"视频宽度:${mVideoWidth}")
               Request.releaseSquare(userId, tagId, city, "", content,"",iIsAnonymous,sTopicId,it[1],it[0],"${mVideoWidth}","${mVideoHeight}","","")
            }.request(this,false,success= { _, data ->
                showToast("发布成功")
                if(TextUtils.equals("0",SPUtils.instance().getString(Const.User.USER_SEX))){
                     showTips(data,"发布约会奖励积分","10")
                }
                syncChat(this,"dynamic",sex,userId)
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

//            Request.uploadFile(temp,1).flatMap {
//                Log.i("releaseautio","视频地址"+it)
//                Request.releaseSquare(userId, tagId, city, "", content,"",iIsAnonymous,sTopicId,it,"","","","","")
//            }.request(this,false,success= { _, data ->
//                showToast("发布成功")
//                if(TextUtils.equals("0",SPUtils.instance().getString(Const.User.USER_SEX))){
//                    showTips(data,"发布约会奖励积分","10")
//                }
//                syncChat(this,"dynamic",sex,userId)
//                setResult(Activity.RESULT_OK)
//                finish()
//            }){code,resMsg->
//                if(code == 2){
//                    val commonTiphDialog = CommonTipDialog()
//                    commonTiphDialog.arguments = bundleOf("resMsg" to resMsg)
//                    commonTiphDialog.show(supportFragmentManager, "resMsg")
//                }
//            }
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
        Request.releaseSquare(userId, tagId, city, null, content,"",iIsAnonymous,sTopicId,"","","","","","").request(this,false,success={
            _, data ->
            showToast("发布成功")
            if(TextUtils.equals("0",SPUtils.instance().getString(Const.User.USER_SEX))){
                showTips(data,"发布约会奖励积分","10")
            }
            syncChat(this,"dynamic",sex,userId)
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
        } else if (DiskFileUtils.IsExists(fileAudioPath)) {
            //发布语音动态
            submitAudioSquare()
        } else if (mImages.size>0&&mImages[0].type == 2) {
            submitVideoSquare()
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

    override fun onBackPressed() {
        super.onBackPressed()
        stopPlaying()
        DiskFileUtils.deleteSingleFile(fileAudioPath)
        DiskFileUtils.deleteSingleFile(Environment.getExternalStorageDirectory().toString() + "/recorded_audio.wav")
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
            if (recorderSecondsElapsed > MINTIME) {
                record.background = ContextCompat.getDrawable(context,R.mipmap.voice_pedestal)
                tv_delete_audio.visibility = View.VISIBLE
                setRlPlayAudioWidth()
                tv_audio_time.text = "${mVoiceLength}”"

                //显示软键盘
                showSoftInput(et_content)
                if(DiskFileUtils.IsExists(fileAudioPath)){
                    tv_release.backgroundDrawable = ContextCompat.getDrawable(context,R.drawable.shape_10r_orange)
                    tv_release.postDelayed(Runnable {
                        AudioLocalConvert()
                    },300)
                }
            }else{
                record.background = ContextCompat.getDrawable(context,R.mipmap.voice_pedestal)
                toast(getString(R.string.string_warm_voicetime))
            }
        }else{
            circlebarview.startDefaultProgress()
            record.background = ContextCompat.getDrawable(context,R.mipmap.voice_pedestal_press)
        }
    }

    //录音完成进行本地转码
    private fun AudioLocalConvert(){
        val callback = object : IConvertCallback {
            override fun onSuccess(convertedFile: File) {
//                DiskFileUtils.deleteSingleFile(fileAudioPath)
                fileAudioPath = convertedFile.path
                audio_loading.visibility = View.GONE
                rl_play_audio.isEnabled = true
                Log.i("fileAudioPath","转码路径：${fileAudioPath}")
            }

            override fun onFailure(error: Exception) {
                audio_loading.visibility = View.GONE
                rl_play_audio.isEnabled = true
                Log.i("fileAudioPath","${error.message}")
            }
        }

        AndroidAudioConverter.with(this)
                .setFile(File(fileAudioPath))
                .setFormat(AudioFormat.MP3)
                .setCallback(callback)
                .convert()
        audio_loading.visibility = View.VISIBLE
        rl_play_audio.isEnabled = false
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
    private var mVoiceLength:String = ""
    private var playerSecondsElapsed: Int = 0
    private var timer: Timer? = null
    private var player: MediaPlayer? = null
    private var recorder: Recorder? = null
    private var autoStart: Boolean = false
    private var moveupFlag: Boolean = false
    private var fileAudioPath = Environment.getExternalStorageDirectory().toString() + "/recorded_audio.wav"

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        if (autoStart&&!isRecording) {
            toggleRecording()
        }
    }

    //录制
    fun toggleRecording() {
        fileAudioPath = Environment.getExternalStorageDirectory().toString() + "/recorded_audio.wav"
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
                    PullTransport.Default(RecoderUtil.getMic(AudioSource.MIC, AudioChannel.MONO, AudioSampleRate.HZ_44100), this@ReleaseNewTrendsActivity),
                    File(fileAudioPath))
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
                Log.i("fileAudioPath","路径：${fileAudioPath}")
                it.setDataSource(fileAudioPath)//"http://sc1.111ttt.cn/2017/1/05/09/298092035545.mp3  fileAudioPath
                it.prepare()
                it.start()
                it.setOnCompletionListener(this@ReleaseNewTrendsActivity)
            }
            tv_recoder_time.setText(resources.getString(R.string.string_record_audio))
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
                recorderSecondsElapsed = recorderSecondsElapsed+1
                if(recorderSecondsElapsed<=MAXTIME){
                    tv_recoder_time.text = "正在录制·${recorderSecondsElapsed}”"
                    mVoiceLength = "${recorderSecondsElapsed}"
                }else{
                    setAudioView(isRecording)
                    restartRecording()
//                    isRecording = true //特殊加的
                    moveupFlag = true
                }
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
