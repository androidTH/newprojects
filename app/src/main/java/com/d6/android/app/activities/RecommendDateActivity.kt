package com.d6.android.app.activities

import android.os.Bundle
import com.d6.android.app.R
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.fragments.RecommendDateFragment
import com.d6.android.app.fragments.SpeedDateFragment

/**
 * 速约
 */
class RecommendDateActivity : TitleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommend_date)
        setTitleBold("全部人工推荐")

        val fragment = RecommendDateFragment()
        fragment.userVisibleHint = true
        supportFragmentManager.beginTransaction()
                .replace(R.id.container,fragment,"s")
                .commitAllowingStateLoss()
    }

}
