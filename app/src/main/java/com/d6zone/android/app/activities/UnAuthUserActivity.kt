package com.d6zone.android.app.activities

import android.content.Context
import android.os.Bundle
import android.text.ClipboardManager
import android.text.TextUtils
import com.d6zone.android.app.R
import com.d6zone.android.app.base.TitleActivity
import com.d6zone.android.app.extentions.request
import com.d6zone.android.app.net.Request
import com.d6zone.android.app.utils.Const
import com.d6zone.android.app.utils.SPUtils
import com.d6zone.android.app.utils.optString
import kotlinx.android.synthetic.main.activity_un_auth_user.*
import org.jetbrains.anko.toast


class UnAuthUserActivity : TitleActivity() {

    private var weChat = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_un_auth_user)
        immersionBar.init()
        title = "认证中心"

        tv_action.setOnClickListener {
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            // 将文本内容放到系统剪贴板里。
            cm.text = weChat
            toast("已复制到剪切板")
        }

        dialog()
        getData()
        getWeChat()
    }

    private fun getData() {

        Request.getInfo("Authentication-D6").request(this) { _, data ->
            data?.let {
                val content1 = data.optString("ext1")
                val content2 = data.optString("ext2")
                val content3 = data.optString("ext3")
                val content4 = data.optString("ext4")
                val content5 = data.optString("ext5")
                val content6 = data.optString("ext6")
                val content7 = data.optString("ext7")
                var c = ""
                if (!content5.isEmpty()) {
                    c+="\n"+content5
                }
                if (!content6.isEmpty()) {
                    c+="\n"+content6
                }
                if (!content7.isEmpty()) {
                    c+="\n"+content7
                }
//                weChat = data.optString("ext8")
                tv_content.text = String.format("%s\n%s\n%s\n%s", content1, content2, content3, content4+c)
//                tv_action.text = String.format("微信号:%s  点击复制微信号", weChat)
            }

        }
    }

    private fun getWeChat() {
        Request.getInfo(Const.SERVICE_WECHAT_CODE).request(this) { _, data ->
            data?.let {
                val sex = SPUtils.instance().getString(Const.User.USER_SEX)
                if(TextUtils.equals(sex, "0")){
                    weChat  = data.optString("ext1")
                }else{
                    weChat = data.optString("ext2")
                }
                tv_action.text = String.format("微信号:%s  点击复制微信号", weChat)
            }
        }
    }
}
