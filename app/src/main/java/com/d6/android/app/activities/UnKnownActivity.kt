package com.d6.android.app.activities

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.CardUnKnowTagAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.OpenDateErrorDialog
import com.d6.android.app.dialogs.OpenDatePointNoEnoughDialog
import com.d6.android.app.dialogs.VistorPayPointDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.UserUnKnowTag
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.CHECK_OPEN_UNKNOW_MSG
import kotlinx.android.synthetic.main.activity_unknow.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity
import org.json.JSONObject

/**
 * 匿名身份
 */
class UnKnownActivity : BaseActivity() {

    private val mTags = ArrayList<UserUnKnowTag>()

    private val open_unknow_msg by lazy{
        SPUtils.instance().getString(CHECK_OPEN_UNKNOW_MSG,"")
    }

    private val IsOpenUnKnow by lazy{
        SPUtils.instance().getString(Const.CHECK_OPEN_UNKNOW,"")
    }

    private val userTagAdapter by lazy {
        CardUnKnowTagAdapter(mTags)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unknow)
        immersionBar.fitsSystemWindows(true).statusBarColor(R.color.color_8F5A5A).statusBarDarkFont(true).init()
        tv_unknow_back.setOnClickListener {
            finish()
        }

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
        rv_unknow_tags.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        rv_unknow_tags.isNestedScrollingEnabled = false
        rv_unknow_tags.adapter = userTagAdapter
        getUserInfo()
//        if(TextUtils.equals("open",IsOpenUnKnow)){
//            tv_unknow_square.visibility = View.VISIBLE
//            tv_unknow_date.visibility = View.VISIBLE
//            tv_unknow_start.visibility = View.GONE
//        }else if(TextUtils.equals("close",IsOpenUnKnow)){
//            tv_unknow_square.visibility = View.GONE
//            tv_unknow_date.visibility = View.GONE
//            tv_unknow_start.visibility = View.VISIBLE
//            tv_unknow_tips.text = open_unknow_msg
//        }else{
//            getUserQueryAnonymous()
//        }

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
        Request.getUserInfo("", getLocalUserId()).request(this, success = { _, data ->
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
        }) { _, _ ->
            //            mSwipeRefreshLayout.isRefreshing = false
        }
    }
}
