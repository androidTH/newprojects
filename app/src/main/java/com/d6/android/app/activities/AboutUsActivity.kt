package com.d6.android.app.activities

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.d6.android.app.R
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.AboutUsInfo
import kotlinx.android.synthetic.main.activity_about_us.*
import kotlinx.android.synthetic.main.activity_about_us_main.*

/**
 * 关于我们
 */
class AboutUsActivity : TitleActivity() {

//    https://github.com/rbro112/Android-Indefinite-Pager-Indicator
    private val mAboutUsInfo = ArrayList<AboutUsInfo>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
        title = "关于我们"

        imageView.setImageURI("res:///"+R.mipmap.about_us_banner)

//        mRecyclerView.setHasFixedSize(true)
//        mRecyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)

//        mAboutUsInfo.add(AboutUsInfo(""))
//        mAboutUsInfo.add(AboutUsInfo(""))
//        mAboutUsInfo.add(AboutUsInfo(""))
//
//        mRecyclerView.adapter = object :BaseRecyclerAdapter<AboutUsInfo>(mAboutUsInfo,R.layout.item_about_us_info){
//            override fun onBind(holder: ViewHolder, position: Int, data: AboutUsInfo) {
//
//            }
//        }
    }
}
