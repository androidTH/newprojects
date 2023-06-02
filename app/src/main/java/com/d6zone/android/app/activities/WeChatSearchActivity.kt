package com.d6zone.android.app.activities

import android.os.Bundle
import com.d6zone.android.app.R
import com.d6zone.android.app.base.TitleActivity
import com.d6zone.android.app.extentions.request
import com.d6zone.android.app.net.Request
import com.d6zone.android.app.utils.optInt
import kotlinx.android.synthetic.main.activity_we_chat_search.*

/**
 * 微信查询
 */
class WeChatSearchActivity : TitleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_we_chat_search)
        immersionBar.init()
        title = "微信查询"

        btn_submit.setOnClickListener {
            search()
        }
    }

    private fun search() {
        val name = et_content.text.toString().trim()
        if (name.isEmpty()) {
            showToast("请输入查询的微信号")
            return
        }
        dialog()
        Request.searchWeChatId(name).request(this,success = {_,data->
            tv_status.text = "未认证 请谨慎添加"
            data?.let {
                if (data.has("status")){
                    val status = data.optInt("status")
                    if (status == 1) {
                        tv_status.text = "已认证,为官方微信,请放心添加"
                    }else if (status == 0) {
                        tv_status.text = "已认证,为加盟微信,请放心添加"
                    }
                }
            }
        })
    }
}
