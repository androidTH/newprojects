package com.d6.android.app.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.alibaba.fastjson.JSONObject
import com.amap.api.location.AMapLocationClient
import com.d6.android.app.R
import com.d6.android.app.adapters.AddImageAdapter
import com.d6.android.app.adapters.DateTypeAdapter
import com.d6.android.app.adapters.NoticeFriendsQuickAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.*
import com.d6.android.app.extentions.request
import com.d6.android.app.models.AddImage
import com.d6.android.app.models.DateType
import com.d6.android.app.models.FriendBean
import com.d6.android.app.models.Imagelocals
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.CHECK_OPEN_UNKNOW
import com.d6.android.app.utils.Const.User.USER_ADDRESS
import com.d6.android.app.utils.Const.User.USER_PROVINCE
import com.d6.android.app.utils.Const.dateTypes
import com.d6.android.app.utils.Const.dateTypesDefault
import com.d6.android.app.utils.Const.dateTypesSelected
import com.d6.android.app.widget.ObserverManager
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_publish_find_date.*
import me.nereo.multi_image_selector.MultiImageSelectorActivity
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.dip
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.support.v4.startActivity
import java.util.*
import kotlin.collections.ArrayList

/**
 * 自主发布约会
 */
class PublishFindDateActivity : BaseActivity(), Observer {

    private val mImages = ArrayList<AddImage>()
    private val mDateTypes = ArrayList<DateType>()
    private var iIsAnonymous:Int = 2

    private var mFrom:String="otherActivity"
    private var mRequestCode:Int = 1
    private var sAddPointDesc="以匿名身份发布"
    private var iAddPoint :String= "" //匿名发布需要消耗的积分
    private var iRemainPoint:String="" //剩余积分
    private var mTimeOut:Long =  -1
    private var mCostIndex:Int = 0 //费用
    var showDateTypes:Array<DateType> = arrayOf(DateType(6),DateType(2),DateType(1),DateType(3),DateType(7),DateType(8),DateType(5))

    private val addAdapter by lazy {
        AddImageAdapter(mImages)
    }

    private val mDateTypeAdapter by lazy {
        DateTypeAdapter(mDateTypes);
    }

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private val sex by lazy{
        SPUtils.instance().getString(Const.User.USER_SEX)
    }

    private val mDateType by lazy{
        intent.getIntExtra("type",5)
    }

    private var city: String? = null
    private var areaType = -1
    private var area = ""
    private var startTime: String = ""
    private var endTime: String = ""
    private var selectedDateType: DateType? = null
    private var REQUEST_CHOOSECODE:Int=10
    private var mVoiceChatType = 1
    private var loveNums = 0
    private var iOncePayLovePoint = 10

    override fun update(o: Observable?, arg: Any?) {
        var mImagelocal = arg as Imagelocals
        if(mImagelocal.mType == 0){
            if(mImages.size>mImagelocal.position){
                mImages.removeAt(mImagelocal.position)
            }
            Log.i("update","长度：${mImages.size}")
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
    private  var mChooseFriends = ArrayList<FriendBean>()

    private val mDateFriendsQuickAdapter by lazy{
        NoticeFriendsQuickAdapter(mChooseFriends)
    }

    private val locationClient by lazy {
        AMapLocationClient(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publish_find_date)
        immersionBar.init()
        ObserverManager.getInstance().addObserver(this)
        RxPermissions(this).request(Manifest.permission.ACCESS_COARSE_LOCATION).subscribe {
            if (it) {
                locationClient.setLocationListener {
                    if (it != null) {
                        city = it.city
                        tv_city.setText(city)
                        locationClient.stopLocation()
                        SPUtils.instance().put(USER_ADDRESS,it.city).apply() //it.city
                        SPUtils.instance().put(USER_PROVINCE,it.province).apply()
                    }
                }
                startLocation()
            }
        }

//        for (i in 0..(showDateTypes.size-1)) {
//            var dt = showDateTypes[i]
//            dt.dateTypeName = dateTypes[dt.type-1]
//            dt.imgUrl = "res:///${dateTypesDefault[dt.type-1]}"
//            dt.selectedimgUrl = "res:///${dateTypesSelected[dt.type-1]}"
//            dt.isSelected = false
//            mDateTypes.add(dt)
//        }

//        rv_datetype.setHasFixedSize(true)
//        rv_datetype.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        rv_datetype.isNestedScrollingEnabled = false
//        rv_datetype.adapter = mDateTypeAdapter

//        mDateTypeAdapter.setOnItemClickListener { view, position ->
//            for (i in 0..(mDateTypes.size - 1)) {
//                var dt = mDateTypes.get(i)
//                dt.isSelected = i == position
//            }
//            selectedDateType = mDateTypes.get(position)
//            mDateTypeAdapter.notifyDataSetChanged()
//        }

        setTitle()
        rv_images.setHasFixedSize(true)
        rv_images.layoutManager = GridLayoutManager(this, 3)
        rv_images.adapter = addAdapter
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
                    ,MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST to urls
            )
        }
        mImages.add(AddImage("res:///" + R.mipmap.comment_addphoto_icon, 1))//ic_add_bg
        addAdapter.notifyDataSetChanged()

        tv_area.setOnClickListener {
            //            startActivityForResult<FilterCityActivity>(4,"type" to 0)
            startActivityForResult<AreaChooseActivity>(4)
        }

        tv_startTime.setOnClickListener {
//            val dialog = DatePickDialog(System.currentTimeMillis(), -1)
//            dialog.setOnDateSetListener { year, month, day ->
//                dialog.dismissAllowingStateLoss()
//                startTime = String.format("%s-%02d-%02d", year, month, day)
//                tv_startTime.text = startTime
//            }
//            dialog.show(supportFragmentManager, "start")

            selecteDateTime()
        }

        tv_endTime.setOnClickListener {
            var mSelectCostTypeDialog = SelectCostTypeDialog()
            mSelectCostTypeDialog.setDialogListener { p, s ->
                tv_endTime.text = "${s}"
                mCostIndex = p
            }
            mSelectCostTypeDialog.show(supportFragmentManager,"cost")
        }

        tv_sure.setOnClickListener {
            if (!isFastClick()) {
                if (publish()) {
                    if(mDateType==9){
                        publishVoiceChat()
                    }else{
                        Request.getAppointmentAuth(userId,getLoginToken()).request(this, true, success = { msg, data ->
                            dialog()
                            CreateDate(et_content.text.toString().trim())
                        }) { code, msg ->
                            if (code == 0) {
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
        }

        tv_back.setOnClickListener {
            mKeyboardKt.hideKeyboard(it)
            finish()
        }

        tv_notififriends.setOnClickListener {
            startActivityForResult<ChooseFriendsActivity>(REQUEST_CHOOSECODE, Const.CHOOSE_Friends to mChooseFriends)
        }

        tv_unknow_sf.setOnClickListener {
            var mSelectUnknowDialog = SelectUnKnowTypeDialog()
            mSelectUnknowDialog.arguments = bundleOf("type" to "PublishFindDate","IsOpenUnKnow" to getIsOpenUnKnow(),
                    "code" to mRequestCode,"desc" to sAddPointDesc,"iAddPoint" to iAddPoint,"iRemainPoint" to iRemainPoint)
            mSelectUnknowDialog.show(supportFragmentManager,"unknowdialog")
            mSelectUnknowDialog.setDialogListener { p, s ->
                if(p==2){
                    tv_unknow_sf.text = "公开身份"
                }else{
                    tv_unknow_sf.text = "匿名身份"
                }
                iIsAnonymous = p
            }
        }

        ll_voicechat_choose.setOnClickListener {
            var mVoiceChatTypeDialog = VoiceChatTypeDialog()
            mVoiceChatTypeDialog.arguments = bundleOf("chooseType" to "0")
            mVoiceChatTypeDialog.setDialogListener { p, s ->
                mVoiceChatType = p
//                if(p==2||p==3){
//                    loveNums = 0
//                    iOncePayLovePoint = 0
//                    showRewardVoiceChatPoints(p)
//                    mVoiceChatTypeDialog.dismissAllowingStateLoss()
//                }else{
//                    tv_voicechat_choose.text = s
//                }
                tv_voicechat_choose.text = s
            }
            mVoiceChatTypeDialog.show(supportFragmentManager,"VoiceChatTypeDialog")
        }

//        startTime = getTodayTime()
//        tv_startTime.text = startTime

        rv_date_friends.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        rv_date_friends.adapter = mDateFriendsQuickAdapter
        mDateFriendsQuickAdapter.setOnItemChildClickListener { adapter, view, position ->
            if(view.id==R.id.iv_clear||view.id==R.id.ll_clear){
                if(mChooseFriends.size>position){
                    mChooseFriends.removeAt(position)
                    mDateFriendsQuickAdapter.notifyDataSetChanged()
                }
            }
        }

        if(intent.hasExtra("from")){
            mFrom = intent.getStringExtra("from")
        }
        if(TextUtils.equals("otherActivity",mFrom)){
            iIsAnonymous = 2
            tv_unknow_sf.text = "公开身份"
        }else{
            iIsAnonymous = 1
            tv_unknow_sf.text = "匿名身份"
        }
//        date_headView.setImageURI(getLocalUserHeadPic())
        getLocalFriendsCount()
    }

    private fun setTitle(){
        if(TextUtils.equals("0", getUserSex())){
            tv_endtime_name.visibility = View.GONE
            tv_endTime.visibility = View.GONE
            line_datemoney.visibility = View.GONE
        }
        if(mDateType == 1){
            date_headView.setImageURI("res:///"+R.mipmap.invitation_travel_feed)
            tv_title.text ="旅游"
            line_unknow.visibility = View.GONE
            ll_voicechat_choose.visibility = View.GONE
        }else if(mDateType == 2){
            date_headView.setImageURI("res:///"+R.mipmap.invitation_meal_feed)
            tv_title.text ="吃饭"
            line_unknow.visibility = View.GONE
            ll_voicechat_choose.visibility = View.GONE
        }else if(mDateType==3){
            date_headView.setImageURI("res:///"+R.mipmap.invitation_film_feed)
            tv_title.text ="看电影"
            line_unknow.visibility = View.GONE
            ll_voicechat_choose.visibility = View.GONE
        }else if(mDateType==4){
            date_headView.setImageURI("res:///"+R.mipmap.drink_nolimit_feed)
            tv_title.text ="喝酒"
            line_unknow.visibility = View.GONE
            ll_voicechat_choose.visibility = View.GONE
        }else if(mDateType==5){
            date_headView.setImageURI("res:///"+R.mipmap.invitation_nolimit_gray)
            tv_title.text ="邀约"
            line_unknow.visibility = View.GONE
            ll_voicechat_choose.visibility = View.GONE
        }else if(mDateType==6){
            date_headView.setImageURI("res:///"+R.mipmap.chat_nolimit_feed)
            et_content.hint = "介绍你自己，说一说你想聊的话题…"
            tv_title.text ="聊天"

            line_unknow.visibility = View.GONE
            ll_voicechat_choose.visibility = View.GONE
            tv_datearea.visibility = View.GONE
            tv_area.visibility = View.GONE
            line_area.visibility = View.GONE
            tv_endtime_name.visibility = View.GONE
            tv_endTime.visibility = View.GONE
            line_datemoney.visibility=View.GONE
            tv_unknow.visibility = View.GONE
            tv_unknow_sf.visibility = View.GONE
            line_unknow.visibility = View.GONE

        }else if(mDateType==7){
            date_headView.setImageURI("res:///"+R.mipmap.game_nolimit_feed)
            tv_title.text ="游戏"
            line_unknow.visibility = View.GONE
            ll_voicechat_choose.visibility = View.GONE
            tv_datearea.visibility = View.GONE
            tv_area.visibility = View.GONE
            line_area.visibility = View.GONE

            tv_endtime_name.visibility = View.GONE
            tv_endTime.visibility = View.GONE
            line_datemoney.visibility = View.GONE

        }else if(mDateType==8){
            date_headView.setImageURI("res:///"+R.mipmap.game_fitness_feed)
            tv_title.text = "健身"
            line_unknow.visibility = View.GONE
            ll_voicechat_choose.visibility = View.GONE
        }else if(mDateType==9){
            et_content.hint = "介绍你自己，说一说你想聊的话题…"
            date_headView.setImageURI("res:///"+R.mipmap.invitation_voicechat_default)
            tv_title.text = "连麦"

            tv_datearea.visibility = View.GONE
            tv_area.visibility = View.GONE
            line_area.visibility = View.GONE
            tv_endtime_name.visibility = View.GONE
            tv_endTime.visibility = View.GONE
            line_datemoney.visibility=View.GONE
            tv_unknow.visibility = View.GONE
            tv_unknow_sf.visibility = View.GONE
            line_unknow.visibility = View.GONE
        }
    }

    private fun selecteDateTime(){
        var selectVoiceChatTimeDialog = SelectVoiceChatTimeDialog()
        selectVoiceChatTimeDialog.arguments = bundleOf("data" to "${tv_startTime.text}")
        selectVoiceChatTimeDialog.setDialogListener { p, s ->
            tv_startTime.text = s
            mTimeOut = getTimeInMillis("${s}")
        }
        selectVoiceChatTimeDialog.show(supportFragmentManager,"time")
    }

    private fun showRewardVoiceChatPoints(type:Int){
        var mRewardVoiceChatPointsDialog = RewardVoiceChatPointsDialog()
        mRewardVoiceChatPointsDialog.arguments = bundleOf("voicechatType" to type)
        mRewardVoiceChatPointsDialog.show(supportFragmentManager, "d")
        mRewardVoiceChatPointsDialog.setDialogListener { p, s ->
            loveNums = p
            iOncePayLovePoint = p
            if(type==2){
                tv_voicechat_choose.text = s
            }else{
                appVoiceChatPoints(iOncePayLovePoint)
            }
            mRewardVoiceChatPointsDialog.dismissAllowingStateLoss()
        }
    }

    private fun appVoiceChatPoints(sendLoveNums:Int){
        var mApplyVoiceChatPointsDialog = ApplyVoiceChatPointsDialog()
        mApplyVoiceChatPointsDialog.arguments = bundleOf("lovenums" to sendLoveNums)
        mApplyVoiceChatPointsDialog.show(supportFragmentManager, "d")
        mApplyVoiceChatPointsDialog.setDialogListener { p, s ->
            loveNums = sendLoveNums*p
            tv_voicechat_choose.text = s
        }
    }

    override fun onResume() {
        super.onResume()
        if(TextUtils.equals("close", getIsOpenUnKnow())){
            sAddPointDesc = "以匿名身份发布约会"
        }else{
            getCheckAnonMouseNums()
        }
    }

    private fun getCheckAnonMouseNums(){
        Request.getAnonymouseAppointmentPoint(getLoginToken(),1).request(this,false,success = {msg,jsonObject->

        }){code,msg->
            mRequestCode = code
            Log.i("CheckAnonMouseNums","${msg}")
            if(code==2){
                if(msg.isNotEmpty()){
                   var jsonobject = org.json.JSONObject(msg)
//                   var iRemainCount = jsonobject.optString("iRemainCount")//还有匿名次数
                   sAddPointDesc = jsonobject.optString("sAddPointDesc")
                }
            }else if(code==3){
                if(msg.isNotEmpty()){
                    var jsonobject = org.json.JSONObject(msg)
//                    var iRemainCount = jsonobject.optString("iRemainCount")
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0 && data != null) {
//                val path = data.getStringExtra(SelectPhotoDialog.PATH)
//                val size = mImages.size
//                val image = AddImage("file://" + path)
//                image.path = path
//                mImages.add(size - 1, image)
//                addAdapter.notifyDataSetChanged()

                val result: ArrayList<String> = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT)
                mImages.clear()
                result.forEach {
                    val image = AddImage("file://$it")
                    image.path = it
                    mImages.add(image)///storage/emulated/0/Huawei/MagazineUnlock/magazine-unlock-01-2.3.1104-_9E598779094E2DB3E89366E34B1A6D50.jpg
                }
                mImages.add(AddImage("res:///" + R.mipmap.comment_addphoto_icon, 1))
                addAdapter.notifyDataSetChanged()

            } else if (requestCode == 4 && data != null) {
//                areaType = data.getIntExtra("type",0)
                area = data.getStringExtra("area")
                tv_area.text = area
            }else if(requestCode == REQUEST_CHOOSECODE && data!=null){
                mChooseFriends = data!!.getParcelableArrayListExtra(Const.CHOOSE_Friends)
                mDateFriendsQuickAdapter.setNewData(mChooseFriends)
            }
        }
    }

    private fun publish(): Boolean {
        if(mDateType==9){
            val content = et_content.text.toString().trim()
            if (content.isEmpty()) {
                showToast("请输入内容")
                return false
            }
            if(mTimeOut==-1L){
                showToast("请选择连麦时间")
                return false
            }
            var connentStr = tv_voicechat_choose.text.toString().trim()
            if(connentStr.isNullOrEmpty()){
                showToast("请选择连麦方式")
                return false
            }
        }else{

            var content = et_content.text.toString().trim()
            if (content.isEmpty()) {
                showToast("请输入内容")
                return false
            }

            if(mDateType!=6&&mDateType!=7){
                if (area.isNullOrEmpty()) {
                    showToast("请选择城市所属地区")
                    return false
                }
            }

            if(mTimeOut==-1L){
                showToast("请选择有效期")
                return false
            }

            if(mDateType!=6&&mDateType!=7){
                if(mCostIndex== 0&&TextUtils.equals("1", getUserSex())){
                    showToast("请选择约会费用")
                    return false
                }
            }
        }
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
            var userIds = getShareUserId(mChooseFriends)
            Request.releasePullDate(userId, area, content, mDateType, mCostIndex,"${System.currentTimeMillis().toDefaultTime()}", "${mTimeOut.toDefaultTime()}", it,userIds,iIsAnonymous)
        }.request(this, false, success = { _, data ->
            showToast("发布成功")
            dismissDialog()
            if (TextUtils.equals("0", SPUtils.instance().getString(Const.User.USER_SEX))) {
                showTips(data, "", "")
            }
            syncChat(this,"date",sex,userId)
//            setResult(Activity.RESULT_OK)
            startActivity<UserInfoActivity>("id" to getLocalUserId())
//            startActivity<AppointmentActivity>("from"  to "PublishFindDateActivity")
            finish()
        }) { code, msg ->
            dismissDialog()
            if (code == 0) {
                showToast(msg)
            }
        }
    }

    private fun getLocalFriendsCount(){
//        Request.findFriendCount(userId).request(this,false,success = {msg,json->
//            json?.let {
////                var count = it.optInt("obj")
//                Log.i("sssssssss","${json}")
//                if(count>0){
//                    ll_friends.visibility = View.VISIBLE
//                }else{
//                    ll_friends.visibility = View.GONE
//                }
//            }
//        })

        Request.findUserFriends(userId,"",1).request(this) { _, data ->
            if(data?.list?.results!=null){
                ll_friends.visibility = View.GONE
            }else{
                ll_friends.visibility = View.GONE
            }
        }
    }

    private fun CreateDate(content: String) {
        if (mImages.size > 1) {//有图片
            CreateDateOfPics(content)
        } else {
            var userIds = getShareUserId(mChooseFriends)
            Request.releasePullDate(userId, area, content, mDateType, mCostIndex,"${System.currentTimeMillis().toDefaultTime()}", "${mTimeOut.toDefaultTime()}", "",userIds,iIsAnonymous).request(this, false, success = { _, data ->
                showToast("发布成功")
                if (TextUtils.equals("0", SPUtils.instance().getString(Const.User.USER_SEX))) {
                    showTips(data, "", "")
                }
                syncChat(this,"date",sex,userId)
//                setResult(Activity.RESULT_OK)
//                startActivity<MyDateListActivity>()
                startActivity<UserInfoActivity>("id" to getLocalUserId())
//                startActivity<AppointmentActivity>("from"  to "PublishFindDateActivity")
                finish()
            }) { code, msg ->
                if (code == 0) {
                    showToast(msg)
                }
            }
        }
    }

    /**
     * 发布连麦
     */
    private fun publishVoiceChat() {
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
                Request.addConnectVoice(getLocalUserId(),content,mDateType,mVoiceChatType,loveNums,iOncePayLovePoint,it,"${System.currentTimeMillis().toDefaultTime()}","${mTimeOut.toDefaultTime()}")
            }.request(this,false,success= { _, data ->
                showToast("发布成功")
                if(TextUtils.equals("0",SPUtils.instance().getString(Const.User.USER_SEX))){
                    showTips(data,"发布约会奖励积分","10")
                }
//                syncChat(this,"dynamic",sex, getLocalUserId())
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
        Request.addConnectVoice(getLocalUserId(), content,mDateType,mVoiceChatType,loveNums,iOncePayLovePoint,"","${System.currentTimeMillis().toDefaultTime()}","${mTimeOut.toDefaultTime()}").request(this,false,success={
            _, data ->
            showToast("发布成功")
            startActivity<UserInfoActivity>("id" to getLocalUserId())
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
