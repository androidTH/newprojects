package com.d6.android.app.activities

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.BlackListAdapter
import com.d6.android.app.adapters.CardManTagAdapter
import com.d6.android.app.adapters.CardUnKnowTagAdapter
import com.d6.android.app.adapters.UserTagAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.VistorPayPointDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.BlackListBean
import com.d6.android.app.models.UserTag
import com.d6.android.app.models.UserUnKnowTag
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.widget.SwipeRefreshRecyclerLayout
import kotlinx.android.synthetic.main.activity_blacklist.*
import kotlinx.android.synthetic.main.activity_unknow.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity

/**
 * 匿名身份
 */
class UnKnownActivity : BaseActivity() {

    private val sex by lazy{
        SPUtils.instance().getString(Const.User.USER_SEX)
    }
    private val mTags = ArrayList<UserUnKnowTag>()

    private val userTagAdapter by lazy {
        CardUnKnowTagAdapter(mTags)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unknow)
        immersionBar.fitsSystemWindows(true).statusBarColor(R.color.color_8F5A5A).statusBarDarkFont(true).init()
        tv_unknow_back.setOnClickListener {
            finish()
        }

        tv_unknow_square.setOnClickListener {
            startActivity<ReleaseNewTrendsActivity>()
        }

        tv_unknow_date.setOnClickListener {
            startActivity<PublishFindDateActivity>()
        }

        tv_unknow_start.setOnClickListener {
            isAuthUser(){
                if(TextUtils.equals("1",sex)){
                    var point = "500"
                    var sAddPointDesc = "支付${point}积分开通匿名身份"
                    val dateDialog = VistorPayPointDialog()
                    dateDialog.arguments = bundleOf("point" to point, "pointdesc" to sAddPointDesc, "type" to 2)
                    dateDialog.setDialogListener { p, s ->
                        if(p==2){
                            tv_unknow_square.visibility = View.VISIBLE
                            tv_unknow_date.visibility = View.VISIBLE
                            tv_unknow_start.visibility = View.GONE
                        }
                    }
                    dateDialog.show(supportFragmentManager, "unknow")
                }else{
                    tv_unknow_square.visibility = View.VISIBLE
                    tv_unknow_date.visibility = View.VISIBLE
                    tv_unknow_start.visibility = View.GONE
                }
            }
        }

        unknow_headview.setImageURI("res:///"+R.mipmap.nimingtouxiang_big)

        rv_unknow_tags.setHasFixedSize(true)
        rv_unknow_tags.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        rv_unknow_tags.isNestedScrollingEnabled = false
        rv_unknow_tags.adapter = userTagAdapter
        getUserInfo()
    }

    private fun getUserInfo() {
        Request.getUserInfo("", getLocalUserId()).request(this, success = { _, data ->
            data?.let {
                tv_sex.isSelected = TextUtils.equals("0", it.sex)
                it.age?.let {
                    if (it.toInt() <= 0) {
                        tv_sex.text = ""
                    } else {
                        tv_sex.text = it
                    }
                }
                var drawable: Drawable? = getLevelDrawable(it.userclassesid.toString(),this)
                //27入门 28中级  29优质
                if(drawable!=null){
                    tv_vip.backgroundDrawable = getLevelDrawable(it.userclassesid.toString(),this)
                }else{
                    tv_vip.visibility = View.GONE
                }

                mTags.clear()

                if (!it.height.isNullOrEmpty()) {
                    mTags.add(UserUnKnowTag("身高","${it.height}",R.mipmap.boy_stature_icon))
                }

                if (!it.weight.isNullOrEmpty()) {
                    mTags.add(UserUnKnowTag("体重","${it.weight}",R.mipmap.boy_weight_icon))
                }

                if (!it.constellation.isNullOrEmpty()) {
                    mTags.add(UserUnKnowTag("星座"," ${it.constellation}",R.mipmap.boy_constellation_icon))
                }

                if (!it.city.isNullOrEmpty()) {
                    mTags.add(UserUnKnowTag("地区","${it.city}",R.mipmap.boy_area_icon))
                }

                if (!it.job.isNullOrEmpty()) {
                    mTags.add(UserUnKnowTag("职业", "${it.job}",R.mipmap.boy_profession_icon))
                }

                if (!it.zuojia.isNullOrEmpty()) {
                    mTags.add(UserUnKnowTag("座驾","${it.zuojia}",R.mipmap.boy_car_icon))
                }

                if (!it.hobbit.isNullOrEmpty()) {
                    var mHobbies = it.hobbit?.replace("#", ",")?.split(",")
                    var sb = StringBuffer()
                    if (mHobbies != null) {
                        for (str in mHobbies) {
//                            mTags.add(UserTag(str, R.drawable.shape_tag_bg_6))
                            sb.append("${str} ")
                        }
                        mTags.add(UserUnKnowTag("爱好",sb.toString(),R.mipmap.boy_hobby_icon))
                    }
                }
                userTagAdapter.notifyDataSetChanged()
            }
        }) { _, _ ->
            //            mSwipeRefreshLayout.isRefreshing = false
        }
    }
}
