package com.d6.android.app.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.d6.android.app.R
import com.d6.android.app.base.TitleActivity
import kotlinx.android.synthetic.main.activity_choose_sex.*

class ChooseSexActivity : TitleActivity() {
    private var sex = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_sex)

        rbtn_male.setOnCheckedChangeListener{_,isChecked->
            if (isChecked) {
                sex = 1
            }
        }
        rbtn_female.setOnCheckedChangeListener{_,isChecked->
            if (isChecked) {
                sex = 0
            }
        }

        btn_submit.setOnClickListener {
            val intent = Intent()
            intent.putExtra("sex",sex)
            setResult(Activity.RESULT_OK,intent)
            finish()
        }
    }
}
