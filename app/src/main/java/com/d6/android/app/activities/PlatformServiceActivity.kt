package com.d6.android.app.activities

import android.os.Bundle
import com.d6.android.app.R
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.utils.gone
import com.d6.android.app.utils.visible
import kotlinx.android.synthetic.main.activity_platform_service.*
import org.jetbrains.anko.startActivity

class  PlatformServiceActivity : TitleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_platform_service)
        title = "平台服务"

        imageView.setImageURI("res:///"+R.mipmap.service_banner)
        rb_male.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                ll_male.visible()
                ll_female.gone()
            }
        }

        rb_female.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                ll_female.visible()
                ll_male.gone()
            }
        }

        btn_contact.setOnClickListener {
            startActivity<ContactUsActivity>()
        }
    }
}
