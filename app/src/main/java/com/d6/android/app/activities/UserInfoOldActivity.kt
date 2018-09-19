package com.d6.android.app.activities

import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import com.d6.android.app.R
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.UserData
import com.d6.android.app.net.Request
import com.d6.android.app.utils.checkChatCount
import com.d6.android.app.utils.invisible
import com.d6.android.app.utils.isAuthUser
import com.d6.android.app.utils.visible
import io.rong.imkit.RongIM
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_user_info.*

/**
 *旧版用户信息
 */
class UserInfoOldActivity : TitleActivity() {
    private val id by lazy {
        intent.getStringExtra("id")
    }

    private var mData: UserData?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        btn_chat.setOnClickListener {
            isAuthUser {
                mData?.let {
                    val name = it.name?:""
                    checkChatCount(id){
                        RongIM.getInstance().startConversation(this, Conversation.ConversationType.PRIVATE,id,name)
                    }
                }
            }
        }
        dialog()
        getUserInfo()
    }

    private fun getUserInfo() {

        Request.getUserInfo("",id).request(this,success = { _, data->
            this.mData = data
            data?.let {
                val info = UserInfo(data.accountId,data.name, Uri.parse(""+data.picUrl))
                RongIM.getInstance().refreshUserInfoCache(info)

                headView.setImageURI(it.picUrl)
                tv_name.text = it.name
                tv_name.isSelected = TextUtils.equals("0",it.sex)
                tv_signature.text = it.signature
                tv_age.text = it.age
                tv_height.text = it.height
                tv_weight.text = it.weight
                tv_job.text = it.job

                tv_city.text = it.city
                if (it.city.isNullOrEmpty()) {
                    tv_city.invisible()
                } else {
                    tv_city.visible()
                }
                tv_hobbit.text = it.hobbit
                if (it.hobbit.isNullOrEmpty()) {
                    tv_hobbit.invisible()
                } else {
                    tv_hobbit.visible()
                }
                tv_constellation.text = it.constellation
                if (it.constellation.isNullOrEmpty()) {
                    tv_constellation.invisible()
                } else {
                    tv_constellation.visible()
                }

                tv5.text = if (TextUtils.equals(it.sex,"1")) "自我介绍" else "要求"
                tv_content.text = it.intro
                if (it.intro.isNullOrEmpty()) {
                    tv_content.invisible()
                } else {
                    tv_content.visible()
                }
            }
        })
    }
}
