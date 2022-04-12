package com.d6.android.app.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.d6.android.app.R
import com.d6.android.app.adapters.CardUnKnowTagAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.MailListDialog
import com.d6.android.app.dialogs.OpenDatePointNoEnoughDialog
import com.d6.android.app.dialogs.VistorPayPointDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.PhoneBookEntity
import com.d6.android.app.models.PhoneNumEntity
import com.d6.android.app.models.UserUnKnowTag
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import kotlinx.android.synthetic.main.activity_privacy.*
import org.jetbrains.anko.*
import org.json.JSONObject

/**
 * 隐私保护
 */
class PrivacySettingActivity : BaseActivity() {

    private var iPhonePrivacy:String = "2"

    private val mIsShowGift by lazy{
        SPUtils.instance().getBoolean("IsShowGift"+ getLocalUserId(),false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy)
        immersionBar.init()

        iv_back_close.setOnClickListener {
            hideSoftKeyboard(it)
            finish()
        }

        sw_gifthistory_off.setOnCheckedChangeListener{buttonView,isChecked->
             if(isChecked){
                 updateIsShowGift(0)
                 SPUtils.instance().put("IsShowGift"+ getLocalUserId(),true).apply()
             }else{
                 updateIsShowGift(1)
                 SPUtils.instance().put("IsShowGift"+ getLocalUserId(),false).apply()
             }
        }

        sw_card_off.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                updateIsFind(1)
            }else{
                updateIsFind(2)
            }
        }

        sw_list_off.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                updateListSetting(2)
            }else{
                updateListSetting(1)
            }
        }

        sw_loveisvisible.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                updateSendPointShow(2)
            }else{
                updateSendPointShow(1)
            }
        }

        sw_lianxi.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                if(TextUtils.equals("2",iPhonePrivacy)){
                    var mMailListDialog = MailListDialog()
                    mMailListDialog.setDialogListener { p, s ->
                        if(p==2){
                            iPhonePrivacy = "2"
                            sw_lianxi.isChecked = false
                            updatePhonePrivacy()
                        }else{
                            val permissList = arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE)
                            PermissionsUtils.getInstance().checkPermissions(this, permissList, object : PermissionsUtils.IPermissionsResult {
                                override fun forbidPermissions() {
                                    iPhonePrivacy = "2"
                                    sw_lianxi.isChecked = false
                                    updatePhonePrivacy()
                                }

                                override fun passPermissions() {
                                    if(p==1){
                                        iPhonePrivacy = "${p}"
                                        sw_lianxi.isChecked = true
                                    }
                                    ContactsTask().execute()
//                                sw_lianxi.postDelayed(object:Runnable{
//                                    override fun run() {
//
//                                    }
//                                },300)
                                }
                            })
                        }
                    }
                    mMailListDialog.show(supportFragmentManager,"MailListDialog")
                }
            }else{
                sw_lianxi.isChecked = false
                iPhonePrivacy = "2"
                updatePhonePrivacy()
            }
        }

        unknowInfo()
        getUserInfo()

        PermissionsUtils.showSystemSetting = false

    }

    private val mTags = ArrayList<UserUnKnowTag>()

    private val userTagAdapter by lazy {
        CardUnKnowTagAdapter(mTags)
    }

    private fun unknowInfo(){
        tv_unknow_square.setOnClickListener {
            checkPoints(1)
        }

        tv_unknow_date.setOnClickListener {
            checkPoints(2)
        }

        tv_unknow_start.setOnClickListener {
            isAuthUser(){
                paypointopenQueryAnonymous()
            }
        }

        unknow_headview.setImageURI("res:///"+R.mipmap.nimingtouxiang_big)

        rv_unknow_tags.setHasFixedSize(true)
        rv_unknow_tags.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        rv_unknow_tags.isNestedScrollingEnabled = false
        rv_unknow_tags.adapter = userTagAdapter

        getUserQueryAnonymous()
    }


    private fun getUserQueryAnonymous(){
        Request.getUserQueryAnonymous(getLoginToken()).request(this,false,success={msg,jsonobject->
            tv_unknow_square.visibility = View.VISIBLE
            tv_unknow_date.visibility = View.VISIBLE
            tv_unknow_start.visibility = View.GONE
            SPUtils.instance().put(Const.CHECK_OPEN_UNKNOW,"open").apply()
        }){code,msg->
            tv_unknow_square.visibility = View.GONE
            tv_unknow_date.visibility = View.GONE
            tv_unknow_start.visibility = View.VISIBLE
            if(code == 2){
                val jsonObject = JSONObject(msg)
                tv_unknow_tips.text = jsonObject.optString("sAnonymousDesc")
            }
            SPUtils.instance().put(Const.CHECK_OPEN_UNKNOW,"close").apply()
        }
    }

    //查询匿名需要支付的积分
    private fun paypointopenQueryAnonymous(){
        Request.getQueryAnonymous(getLoginToken()).request(this,false,success = {msg,jsonobject->

        }){code,msg->
            if(code == 2){
                if(msg.isNotEmpty()){
                    var obj = JSONObject(msg)
                    var point = obj.optString("iAddPoint")
                    var sAddPointDesc = obj.optString("sAddPointDesc")
                    val dateDialog = VistorPayPointDialog()
                    dateDialog.arguments = bundleOf("point" to point, "pointdesc" to sAddPointDesc, "type" to 2)
                    dateDialog.setDialogListener { p, s ->
                        if (p == 2) {
                            SPUtils.instance().put(Const.CHECK_OPEN_UNKNOW,"open").apply()
                            tv_unknow_square.visibility = View.VISIBLE
                            tv_unknow_date.visibility = View.VISIBLE
                            tv_unknow_start.visibility = View.GONE
                            tv_unknow_tips.visibility = View.GONE
                        }
                    }
                    dateDialog.show(supportFragmentManager, "unknow")
                }
            }else if(code == 3){
                if(msg.isNotEmpty()){
                    if (msg.isNotEmpty()) {
                        val jsonObject = JSONObject(msg)
                        var iAddPoint = jsonObject.getString("iAddPoint")
                        var iRemainPoint = jsonObject.getString("iRemainPoint")
                        var openErrorDialog = OpenDatePointNoEnoughDialog()
                        openErrorDialog.arguments = bundleOf("point" to iAddPoint, "remainPoint" to iRemainPoint)
                        openErrorDialog.show(supportFragmentManager, "d")
                    }
                }
            }
        }
    }

    private fun checkPoints(type:Int){
        Request.getAnonymouseAppointmentPoint(getLoginToken(),1).request(this,false,success = {msg,jsonObject->
            if(type==1){
                startActivity<ReleaseNewTrendsActivity>("from" to "UnknowActivity")
            }else if(type==2){
                startActivity<PublishFindDateActivity>("from" to "UnknowActivity")
            }
        }){code,msg->
            if(code == 4){
                if(msg.isNotEmpty()){
                    var jsonobject = JSONObject(msg)
                    var iRemainPoint = jsonobject.optString("iRemainPoint")//iRemainPoint 剩余积分
                    var iAddPoint = jsonobject.optString("iAddPoint")//匿名发布需要消耗的积分
                    var openErrorDialog = OpenDatePointNoEnoughDialog()
                    openErrorDialog.arguments = bundleOf("point" to iAddPoint, "remainPoint" to iRemainPoint)
                    openErrorDialog.show(supportFragmentManager, "d")
                }
            }else{
                if(type==1){
                    startActivity<ReleaseNewTrendsActivity>("from" to "UnknowActivity")
                }else if(type==2){
                    startActivity<PublishFindDateActivity>("from" to "UnknowActivity")
                }
            }
        }
    }

    private fun getUserInfo() {
        Request.getUserInfo(getLocalUserId(), getLocalUserId()).request(this, success = { _, data ->
            data?.let {
                if(it.iIsFind==1){
                    sw_card_off.isChecked = true
                }else{
                    sw_card_off.isChecked = false
                }

                if(it.iListSetting==1){
                    sw_list_off.isChecked = false
                }else{
                    sw_list_off.isChecked = true
                }

                if(it.iSendPointShow==1){
                    sw_loveisvisible.isChecked = false
                }else{
                    sw_loveisvisible.isChecked = true
                }

                if(it.iPhonePrivacy==1){
                    iPhonePrivacy = "1"
                    sw_lianxi.isChecked = true
                }else{
                    iPhonePrivacy = "2"
                    sw_lianxi.isChecked = false
                }

                if(it.isShowGift==1){
                    sw_gifthistory_off.isChecked = false
                    SPUtils.instance().put("IsShowGift"+ getLocalUserId(),false).apply()
                }else{
                    sw_gifthistory_off.isChecked = true
                    SPUtils.instance().put("IsShowGift"+ getLocalUserId(),true).apply()
                }

                data?.let {
                    tv_sex.isSelected = TextUtils.equals("0", it.sex)
                    it.age?.let {
                        if (it.toInt() <= 0) {
                            tv_sex.text = ""
                        } else {
                            tv_sex.text = it
                        }
                    }
                    var drawable: Drawable? = getLevelDrawable(it.userclassesid.toString(),this)
                    //27入门 28中级  29优质
                    if(drawable!=null){
                        tv_vip.backgroundDrawable = getLevelDrawable(it.userclassesid.toString(),this)
                    }else{
                        tv_vip.visibility = View.GONE
                    }

                    mTags.clear()

//                if (!it.height.isNullOrEmpty()) {
//                    mTags.add(UserUnKnowTag("身高","${it.height}",R.mipmap.boy_stature_icon))
//                }
//
//                if (!it.weight.isNullOrEmpty()) {
//                    mTags.add(UserUnKnowTag("体重","${it.weight}",R.mipmap.boy_weight_icon))
//                }

                    if (!it.constellation.isNullOrEmpty()) {
                        mTags.add(UserUnKnowTag("星座"," ${it.constellation}",R.mipmap.boy_constellation_icon))
                    }else{
                        mTags.add(UserUnKnowTag("星座","-",R.mipmap.boy_constellation_icon))
                    }

                    if (!it.city.isNullOrEmpty()) {
                        mTags.add(UserUnKnowTag("地区","${it.city}",R.mipmap.boy_area_icon))
                    }else{
                        mTags.add(UserUnKnowTag("地区","-",R.mipmap.boy_area_icon))
                    }

                    if (!it.job.isNullOrEmpty()) {
                        mTags.add(UserUnKnowTag("职业", "${it.job}",R.mipmap.boy_profession_icon))
                    }else{
                        mTags.add(UserUnKnowTag("职业", "-",R.mipmap.boy_profession_icon))
                    }

                    if (!it.zuojia.isNullOrEmpty()) {
                        mTags.add(UserUnKnowTag("座驾","${it.zuojia}",R.mipmap.boy_car_icon))
                    }else{
                        mTags.add(UserUnKnowTag("座驾","-",R.mipmap.boy_car_icon))
                    }
                    if (!it.hobbit.isNullOrEmpty()) {
                        var mHobbies = it.hobbit?.replace("#", ",")?.split(",")
                        var sb = StringBuffer()
                        if (mHobbies != null) {
                            for (str in mHobbies) {
//                            mTags.add(UserTag(str, R.drawable.shape_tag_bg_6))
                                sb.append("${str} ")
                            }
                            mTags.add(UserUnKnowTag("爱好",sb.toString(),R.mipmap.boy_hobby_icon))
                        }
                    }else{
                        mTags.add(UserUnKnowTag("爱好","-",R.mipmap.boy_hobby_icon))
                    }

                    userTagAdapter.notifyDataSetChanged()

                }

            }
        }) { code, msg ->
            if(code==2){
                toast(msg)
            }
        }
    }

    private fun updateIsShowGift(isShowGift:Int){
        Request.updateIsShowState(getLoginToken(),isShowGift).request(this,false,success={_,data->

        })
    }

    private fun updateIsFind(iIsFind:Int){
        Request.updateCardIsFind(getLoginToken(),iIsFind).request(this,false,success={_,data->

        })
    }

    private fun updateListSetting(iListSetting:Int){
        Request.updateListSetting(getLoginToken(),iListSetting).request(this,false,success={_,data->

        })
    }

    private fun updateSendPointShow(iSendPointShow:Int){
        Request.updateSendPointShow(getLoginToken(),iSendPointShow).request(this,false,success={_,data->

        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionsUtils.getInstance().onRequestPermissionsResult(this,requestCode,permissions,grantResults);
    }

    //[{"displayName":"181 2821 1883","mobileNum":"18128211883"},
    // {"displayName":"181 2821 1883","mobileNum":""},
    // {"displayName":"阿里巴巴钉钉客服","mobileNum":"057128834033"},
    // {"displayName":"钉钉DING消息","mobileNum":"01053912851"},
    // {"displayName":"钉钉官方短信","mobileNum":"106575258192144"},
    // {"displayName":"华为客服","mobileNum":"4008308300"},
    // {"displayName":"企业微信官方客服","mobileNum":"075536557977"},
    // {"displayName":"企业微信授权服务中心","mobileNum":"02260624298"},
    // {"displayName":"傻逼","mobileNum":"18755061619"},
    // {"displayName":"推销","mobileNum":"075536336581"},
    // {"displayName":"推销","mobileNum":"01052729739"},
    // {"displayName":"央视频多人通话","mobileNum":"01052729739"},
    // {"displayName":"智行火车票","mobileNum":"02122500914"}]
    inner class ContactsTask : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg params: String?): String? {
            return ContactHelper.getInstance().getSbContacts(this@PrivacySettingActivity)
        }

        override fun onPostExecute(result: String?) {
            result?.let {
//                var json = GsonHelper.getGson().toJson(result)
//                var json = ""
                Log.i("privacy","联系人数量：,联系人列表：${it}")
                Request.updatePhonePrivacy("${iPhonePrivacy}","${it}").request(this@PrivacySettingActivity,false,success={msg,data->

                })
            }
        }
    }

    private fun updatePhonePrivacy(){
        Request.updatePhonePrivacy("${iPhonePrivacy}","").request(this@PrivacySettingActivity,false,success={msg,data->

        }){code,msg->
            toast(msg)
        }
    }
}