package com.d6.android.app.activities

import android.os.Bundle
import com.d6.android.app.R
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.fragments.SpeedDateFragment

/**
 * 速约
 */
class SpeedDateActivity : TitleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speed_date)
        title = "速约"

        val fragment = SpeedDateFragment()
        fragment.userVisibleHint = true
        supportFragmentManager.beginTransaction()
                .replace(R.id.container,fragment,"s")
                .commitAllowingStateLoss()
    }

}
