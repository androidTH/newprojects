package com.d6zone.android.app.activities

import android.os.Bundle
import com.d6zone.android.app.R
import com.d6zone.android.app.base.TitleActivity
import com.d6zone.android.app.net.Request
import com.d6zone.android.app.extentions.request
import com.d6zone.android.app.utils.getLocalUserId
import com.d6zone.android.app.utils.hideSoftKeyboard
import kotlinx.android.synthetic.main.activity_feed_back.*
import org.jetbrains.anko.toast

/**
 * 举报
 */
class ReportActivity : TitleActivity() {
    private val id by lazy {
        intent.getStringExtra("id")
    }

    private var tiptype:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_back)
        immersionBar.init()
        title = "举报"
        tiptype = intent.getStringExtra("tiptype")
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
        Request.report(getLocalUserId(),id,content,tiptype).request(this){ msg, _->
            toast(msg.toString())
            hideSoftKeyboard(et_content)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        hideSoftKeyboard(et_content)
    }
}