package com.d6.android.app.activities

import android.os.Bundle
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.utils.*
import kotlinx.android.synthetic.main.activity_goodfriends.*

/**
 * 黑名单列表
 */
class InviteGoodFriendsActivity : BaseActivity(){
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goodfriends)
        immersionBar.fitsSystemWindows(true).statusBarColor(R.color.trans_parent).statusBarDarkFont(true).init()

        tv_goodfriends_back.setOnClickListener {
            finish()
        }


    }
}
