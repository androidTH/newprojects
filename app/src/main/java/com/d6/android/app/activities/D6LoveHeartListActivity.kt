package com.d6.android.app.activities

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.adapters.LoveHeartPageAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.fragments.BangDanListQuickRichFragment
import com.d6.android.app.fragments.CharmBangdanFragment
import com.d6.android.app.fragments.ManyLoveHeartListQuickFragment
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import kotlinx.android.synthetic.main.activity_d6loveheartlist.*
import kotlinx.android.synthetic.main.item_loveheart.view.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast

/**
 * 开通会员
 */
class D6LoveHeartListActivity : BaseActivity() {

    private var mTitles = ArrayList<String>()
    private var mFragments = ArrayList<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_d6loveheartlist)
        immersionBar.init()

        tv_membership_back.setOnClickListener {
            onBackPressed()
        }

        tv_click_bangdan.setOnClickListener {
            startActivity<ReleaseNewTrendsActivity>("from" to "bangdan")
        }

        mTitles.add("魅力榜")
        mTitles.add("土豪榜")

        mFragments.add(CharmBangdanFragment.newInstance("魅力榜",2))
        mFragments.add(BangDanListQuickRichFragment.newInstance("土豪榜",1))
        viewpager_loveheart.adapter = LoveHeartPageAdapter(supportFragmentManager,mFragments,mTitles)
        viewpager_loveheart.offscreenPageLimit = mFragments.size

        tab_loveheartitle.setupWithViewPager(viewpager_loveheart)
        viewpager_loveheart.addOnPageChangeListener(object:ViewPager.OnPageChangeListener{
            override fun onPageSelected(index: Int) {
                if(index==0){
                    if(TextUtils.equals("1", getUserSex())){
                        ll_self_bangdan_order.visibility = View.GONE
                        tv_click_bangdan.visibility = View.GONE
                    }else{
                        ll_self_bangdan_order.visibility = View.VISIBLE
                        tv_click_bangdan.visibility = View.VISIBLE
                    }
                }else{
                    if(TextUtils.equals("1", getUserSex())){
                        ll_self_bangdan_order.visibility = View.VISIBLE
                        tv_click_bangdan.visibility = View.GONE
                    }else{
                        ll_self_bangdan_order.visibility = View.GONE
                    }
                }

            }

            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }
        })

        if(TextUtils.equals("1", getUserSex())){
            ll_self_bangdan_order.visibility = View.GONE
            tv_click_bangdan.visibility = View.GONE
        }
        updateTopBangDan(85)
    }

    private fun updateTopBangDan(position:Int){
        Request.getUserInfo(getLocalUserId(), getLocalUserId()).request(this, success = { _, data ->
            data?.let {
                user_self_headView.setImageURI(it.picUrl)
                tv_self_name.text = "${it.name}"
                tv_self_sex.isSelected = TextUtils.equals("0",it.sex)
                if (TextUtils.equals("1", getUserSex())&& TextUtils.equals(it.sex, "0")) {//0 女 1 男
                    tv_self_vip.visibility =View.GONE
                } else {
                    tv_self_vip.visibility = View.VISIBLE
                    tv_self_vip.backgroundDrawable = getLevelDrawable("${it.userclassesid}",this)
                }

                if(TextUtils.equals("0",it.sex)){
                    if(it.iReceiveLovePoint!=0){
                        tv_self_receivedliked.text = "收到${it.iReceiveLovePoint}"
                    }else{
                        tv_self_receivedliked.visibility = View.GONE
                    }
                }else{
                    if(it.iSendLovePoint!=0){
                        tv_self_receivedliked.text = "送出${it.iSendLovePoint}"
                    }else{
                        tv_self_receivedliked.visibility = View.GONE
                    }
                }

                if(position==1){
                    tv_self_order.textColor = ContextCompat.getColor(this,R.color.color_FF4500)
                }else if(position==2){
                    tv_self_order.textColor = ContextCompat.getColor(this,R.color.color_BE34FF)
                }else if(position==3){
                    tv_self_order.textColor = ContextCompat.getColor(this,R.color.color_34B1FF)
                }else{
                    tv_self_order.textColor = ContextCompat.getColor(this,R.color.color_888888)
                }

                if(position<9){
                    tv_self_order.text = "0${position}"
                }else{
                    if(position>100||it.iReceiveLovePoint==0){
                        tv_self_order.text = "--"
                    }else{
                        tv_self_order.text = "${position}"
                    }
                }
                user_self_headView.setOnClickListener {
                    if(data.iListSetting!=2){
                        startActivity<UserInfoActivity>("id" to "${data.accountId}")
                    }
                }
            }
        }) { code, msg ->
            if(code==2){
                toast(msg)
            }
        }
    }

    private fun setTabSelected(flag: Boolean, tab: com.d6.android.app.widget.tablayout.TabLayout.Tab) {
        var tv = tab.getCustomView()!!.findViewById<TextView>(R.id.tv_tab)
        var paint = tv.getPaint()
        paint.setFakeBoldText(flag)
        if (flag) {
            tv.textColor = ContextCompat.getColor(this, R.color.color_F7AB00)
        } else {
            tv.textColor = ContextCompat.getColor(this, R.color.color_666666)
        }
    }


    private fun InitUIData() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar?.destroy()
    }
}