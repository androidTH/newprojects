package com.d6.android.app.activities

import android.os.Bundle
import com.d6.android.app.R
import com.d6.android.app.base.TitleActivity
import kotlinx.android.synthetic.main.activity_business_cooperation.*

class BusinessCooperationActivity : TitleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_cooperation)
        title = "商务合作"
        imageView.setImageURI("res:///"+R.mipmap.b_banner)
    }
}
