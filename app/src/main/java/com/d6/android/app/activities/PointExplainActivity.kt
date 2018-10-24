package com.d6.android.app.activities

import android.os.Bundle
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import kotlinx.android.synthetic.main.activity_pointexplain_layout.*

/**
 * 积分说明
 */
class PointExplainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pointexplain_layout)

        tv_back.setOnClickListener {
            mKeyboardKt.hideKeyboard(it)
            finish()
        }
        getData();
    }

    private fun getData() {
        Request.getInfo(Const.SCORE_EXPLAIN_CODE).request(this) { _, data ->
            data?.let {
                var str = data.optString("score_explain")
                tv_title0.text = str
            }
        }
    }
}
