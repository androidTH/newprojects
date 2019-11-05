package com.d6.android.app.activities

import android.os.Bundle
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import kotlinx.android.synthetic.main.activity_privacy.*
import org.jetbrains.anko.*

/**
 * 客服查找
 */
class PrivacySettingActivity : BaseActivity() {

    private val mLocalUserSex by lazy{
        SPUtils.instance().getString(Const.User.USER_SEX)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy)
        immersionBar.init()

        iv_back_close.setOnClickListener {
            hideSoftKeyboard(it)
            finish()
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

            }else{

            }
        }

        sw_loveisvisible.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){

            }else{

            }
        }

        getUserInfo()
    }

    private fun getUserInfo() {
        Request.getUserInfo(getLocalUserId(), getLocalUserId()).request(this, success = { _, data ->
            data?.let {
                if(it.iIsFind==1){
                    sw_card_off.isChecked = true
                }else{
                    sw_card_off.isChecked = false
                }

            }
        }) { code, msg ->
            if(code==2){
                toast(msg)
            }
        }
    }

    private fun updateIsFind(iIsFind:Int){
        Request.updateCardIsFind(getLoginToken(),iIsFind).request(this,false,success={_,data->

        })
    }

}