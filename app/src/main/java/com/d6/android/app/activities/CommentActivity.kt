package com.d6.android.app.activities

import android.app.Activity
import android.os.Bundle
import com.d6.android.app.R
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import kotlinx.android.synthetic.main.activity_comment.*
import org.jetbrains.anko.toast

/**
 * 评论
 */
class CommentActivity : TitleActivity() {
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private val id by lazy {
        intent.getStringExtra("id")
    }
    private val uid by lazy {
        intent.getStringExtra("uid")
    }

    private var iIsAnonymous:Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
        title = "评论"

        btn_submit.setOnClickListener {
            comment()
        }
        iIsAnonymous = 2
    }

    private fun comment() {
        val content = et_content.text.toString().trim()
        if (content.isEmpty()) {
            toast("评论内容不能为空")
            return
        }
        val replyUid = if (uid.isEmpty()) {
            null
        } else {
            uid
        }
        dialog()
        Request.addComment(userId, id,content,replyUid,iIsAnonymous).request(this){ msg, _->
            toast("评论成功")
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}