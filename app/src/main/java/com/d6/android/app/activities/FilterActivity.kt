package com.d6.android.app.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.d6.android.app.R
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.gone
import com.d6.android.app.utils.visible
import kotlinx.android.synthetic.main.activity_filter.*
import org.jetbrains.anko.startActivityForResult

class FilterActivity : TitleActivity() {

    private var vipIds = ""
    private var typeIds = ""
    private var area = ""
    private var areaType = 0
    private val type by lazy {
        intent.getIntExtra("type",0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        tv_address.setOnClickListener {
            startActivityForResult<FilterCityActivity>(0,"type" to 1)
        }

        tv_level.setOnClickListener {
            startActivityForResult<FilterVipLevelActivity>(1)
        }
        tv_type.setOnClickListener {
            startActivityForResult<FilterTypeActivity>(2)
        }

        val sex = SPUtils.instance().getString(Const.User.USER_SEX)
        //男生
        if (TextUtils.equals(sex,"1")) {
            tv_level1.gone()
            tv_level.gone()
        } else {
            tv_level1.visible()
            tv_level.visible()
        }

        if (type == 2) {
            tv_type.gone()
            tv_type1.gone()
        } else {
            tv_type.visible()
            tv_type1.visible()
        }

        btn_sure.setOnClickListener {
            val intent = Intent()
            intent.putExtra("vipIds", vipIds)
            intent.putExtra("typeIds", typeIds)
            intent.putExtra("area", area)
            intent.putExtra("areaType", areaType)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 2 && data != null) {
                typeIds = data.getStringExtra("ids")
                val types = data.getStringExtra("datas")
                tv_type.text = types
            } else if (requestCode == 1 && data != null) {
                vipIds = data.getStringExtra("ids")
                val datas = data.getStringExtra("datas")
                tv_level.text = datas
            }else if (requestCode == 0 && data != null) {
//                area = data.getStringExtra("area")
                area = data.getStringExtra("data")
                areaType = data.getIntExtra("type",0)
                tv_address.text = area
            }
        }
    }
}
