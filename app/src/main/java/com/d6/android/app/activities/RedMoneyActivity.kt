package com.d6.android.app.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.SENDLOVEHEART_DIALOG
import kotlinx.android.synthetic.main.activity_redmoney.*
import org.jetbrains.anko.toast
import org.jetbrains.anko.startActivity


class RedMoneyActivity : BaseActivity() {

    private var localLoveHeartNums = SPUtils.instance().getInt(Const.User.USERLOVE_NUMS, 0)
    private var sResourceId:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_redmoney)
        immersionBar.init()

        LocalBroadcastManager.getInstance(this).registerReceiver(buyloveheart, IntentFilter(Const.LOCALBROADCAST_SENDREDMONEY))
        if(intent.hasExtra("sResourceId")){
            sResourceId = intent.getStringExtra("sResourceId")//cf0549c2-a702-4992-ae69-623662dda9f3
            Log.i("RedMoneyActivity","sResourceId=${sResourceId}")
        }

        iv_back_close.setOnClickListener {
            hideSoftKeyboard(it)
            finish()
        }

        tv_buyloveheart.setOnClickListener {
            startActivity<MyPointsActivity>("fromType" to SENDLOVEHEART_DIALOG)
        }

        tv_redmoney_tips.text = "红包总额不能低于1000 [img src=taren_gray_icon/]，10个 [img src=taren_gray_icon/] =1元"

        btn_create_redmoney.setOnClickListener {
            var redHeart = et_redmoney.text.toString().trim()
            if(!TextUtils.isEmpty(redHeart)){
                var redHeartNums = redHeart.toInt()
//                if(redHeartNums<=localLoveHeartNums){
                   var nums =  et_redmoney_nums.text.toString().trim()
                    if(!TextUtils.isEmpty(nums)){
                        if(redHeartNums>=nums.toInt()){
                            if(!isFastClick()){
                                sendEnvelop(redHeartNums,nums.toInt(),2,sResourceId,tv_redmoney_desc.text.toString().trim())
                            }
                        }else{
                            toast("红包总额不能小于红包个数量")
                        }
                    }else{
                        toast("红包个数不能为空")
                    }
//                }else{
//
////                    toast("红包数量不能低于现有红星数量")
//                }
            }else{
                toast("红包数量不能为空")
            }
        }
    }

    //红包类型 1、私发 2、群发
    private fun checkRedHeartNums(){
        Request.getUserInfo("", getLocalUserId()).request(this,false,success = { _, data ->
            data?.let {
                SPUtils.instance().put(Const.User.USERLOVE_NUMS,it.iLovePoint).apply()
            }
        })
    }

    private fun sendEnvelop(iLovePoint:Int,iLoveCount:Int,iType:Int,sResourceId:String, sEnvelopeDesc:String){
        Request.saveEnvelope(iLovePoint,iLoveCount,iType,sResourceId, sEnvelopeDesc).request(this,false,success = {_,data->
            data?.let {
                hintKeyBoard()
                var rescode =  it.optString("resCode")
                if(TextUtils.equals("100",rescode)){
                    ll_buyloveheart.visibility = View.VISIBLE
                    tv_surplusnums.text = "剩余${localLoveHeartNums}[img src=redheart_small/]，数量不足️"
                }else{
                    finish()
                }
            }
            if(data==null){
                hintKeyBoard()
                finish()
            }
            Log.i("RedMoneyActivity","json=${data}")
        }){code,msg->
              if(code==100){

              }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(buyloveheart)
    }

    private val buyloveheart by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                runOnUiThread {
                    intent?.let {
                        var status = it.getBooleanExtra("buy_success",false)
                        if(status){
                            ll_buyloveheart.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }
}
