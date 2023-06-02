package com.d6zone.android.app.activities

import android.os.Bundle
import com.d6zone.android.app.R
import com.d6zone.android.app.base.TitleActivity
import com.d6zone.android.app.net.Request
import com.d6zone.android.app.extentions.request
import com.d6zone.android.app.utils.Const
import com.d6zone.android.app.utils.SPUtils
import kotlinx.android.synthetic.main.activity_feed_back.*
import org.jetbrains.anko.toast

/**
 * 意见反馈
 */
class FeedBackActivity : TitleActivity() {
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_back)
        immersionBar.init()
        title = "意见反馈"

        btn_submit.setOnClickListener {
            feedback()
        }
    }

    private fun feedback() {
        val content = et_content.text.toString().trim()
        if (content.isEmpty()) {
            toast("反馈内容不能为空")
            return
        }
        dialog()
        Request.feedback(userId,content).request(this){msg,_->
            toast(msg.toString())
            finish()
        }
    }
}