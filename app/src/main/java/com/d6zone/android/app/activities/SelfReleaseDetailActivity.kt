package com.d6zone.android.app.activities

import android.os.Bundle
import com.d6zone.android.app.R
import com.d6zone.android.app.base.TitleActivity
import com.d6zone.android.app.models.MyDate
import com.d6zone.android.app.utils.Const
import com.d6zone.android.app.utils.SPUtils
import com.d6zone.android.app.utils.isCheckOnLineAuthUser
import io.rong.imkit.RongIM
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.activity_self_release_detail.*

class SelfReleaseDetailActivity : TitleActivity() {
    private val mData by lazy {
        intent.getSerializableExtra("data") as MyDate
    }

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_self_release_detail)

        srv_view.update(mData)

        fl_action.setOnClickListener {
            isCheckOnLineAuthUser(this,userId) {
                mData.userId?.let {
//                    checkChatCount(it) {
                        RongIM.getInstance().startConversation(this, Conversation.ConversationType.PRIVATE, mData.userId, mData.name)
//                    }
                }
            }
        }
    }
}
