package com.d6.android.app.activities

import android.os.Bundle
import com.d6.android.app.R
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.net.Request
import com.d6.android.app.extentions.request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import kotlinx.android.synthetic.main.activity_feed_back.*
import org.jetbrains.anko.toast

/**
 * 举报
 */
class ReportActivity : TitleActivity() {
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private val id by lazy {
        intent.getStringExtra("id")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_back)
        title = "举报"
//        et_content.setHint("写下你的")
        btn_submit.setOnClickListener {
            feedback()
        }
    }

    private fun feedback() {
        val content = et_content.text.toString().trim()
        if (content.isEmpty()) {
            toast("举报内容不能为空")
            return
        }
        dialog()
        Request.report(userId,id,content).request(this){msg,_->
            toast(msg.toString())
            finish()
        }
    }
}