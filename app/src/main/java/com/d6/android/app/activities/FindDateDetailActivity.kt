package com.d6.android.app.activities

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.text.TextUtils
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.CardChatManTagAdapter
import com.d6.android.app.adapters.FindDateImagesAdapter
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.dialogs.DateTypeDialog
import com.d6.android.app.dialogs.ShareFriendsDialog
import com.d6.android.app.models.MyDate
import com.d6.android.app.models.UserTag
import com.d6.android.app.utils.*
import com.share.utils.ShareUtils
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import io.rong.imkit.RongIM
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.activity_find_date_detail.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.dip
import org.jetbrains.anko.startActivity

/**
 * 觅约详情
 */
class FindDateDetailActivity : TitleActivity() {

    private val mData by lazy {
        intent.getSerializableExtra("data") as MyDate
    }
    private val mUrls = ArrayList<String>()

    private val imgAdapter by lazy {
        FindDateImagesAdapter(mUrls)
    }

    private val mTags =ArrayList<UserTag>()

    private val mUserTagAdapter by lazy{
         CardChatManTagAdapter(mTags)
    }

    private val shareListener by lazy {
        object : UMShareListener {
            override fun onResult(p0: SHARE_MEDIA?) {

            }

            override fun onCancel(p0: SHARE_MEDIA?) {
            }

            override fun onError(p0: SHARE_MEDIA?, p1: Throwable?) {
            }

            override fun onStart(p0: SHARE_MEDIA?) {
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_date_detail)
        immersionBar.init()
        title = "觅约"
          //R.mipmap.ic_share
        titleBar.addRightButton(rightId = R.mipmap.ic_more_orange, onClickListener = View.OnClickListener {
            val shareDialog = ShareFriendsDialog()
            shareDialog.arguments = bundleOf("from" to "Recommend_findDate","id" to mData.userId.toString(),"sResourceId" to mData.id.toString())
            shareDialog.show(supportFragmentManager, "action")
            shareDialog.setDialogListener { p, s ->
                if (p == 0) {
                    startActivity<ReportActivity>("id" to mData.id.toString(), "tiptype" to "4")
                }else if(p==3){
                    ShareUtils.share(this@FindDateDetailActivity, SHARE_MEDIA.WEIXIN, mData.lookfriendstand ?: "", mData.looknumber?:"", "http://www.d6-zone.com/JyD6/#/miyuexiangqing?ids="+mData.id, shareListener)
                }
            }
        })

        //设置地址和编号view的elevation
//        ViewCompat.setElevation(tv_address_num, dip(3).toFloat())
        rv_images.setHasFixedSize(true)
        rv_images.isNestedScrollingEnabled=false
        rv_images.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_images.adapter = imgAdapter
//        val helper = PagerSnapHelper()
//        helper.attachToRecyclerView(rv_images)

        tv_contact.setOnClickListener {
//            isAuthUser() {
//             ShareUtils.share(this@FindDateDetailActivity, SHARE_MEDIA.WEIXIN, mData.lookfriendstand ?: "", mData.looknumber?:"", "http://www.d6-zone.com/JyD6/#/miyuexiangqing?ids="+mData.id, shareListener)
//            }

            var mDateTypeDialog = DateTypeDialog()
            mDateTypeDialog.setDialogListener { p, s ->
                if(p==1){//分享约会
                    saveBmpToGallery(this,convertViewToBitmap(rl_root_finddate),"finddate_qrcode")
                }else if(p==2){//复制微信

                }
            }
            mDateTypeDialog.show(supportFragmentManager,"dateType")
        }
        refreshUI()
    }

    private fun refreshUI() {
        date_type.text = String.format("觅约：%s", mData.looknumber)
        tv_sex.text = "${mData.age}"

        mTags.clear()
        if (!TextUtils.isEmpty(mData.height)) {
            mTags.add(UserTag("身高 " + mData.height!!, R.mipmap.boy_stature_icon))
        }

        if (!TextUtils.isEmpty(mData.weight)) {
            mTags.add(UserTag("体重 " + mData.weight!!, R.mipmap.boy_weight_grayicon))
        }

//        if (!TextUtils.isEmpty(mData.job)) {
//            mTags.add(UserTag("星座 " + mData.job!!, R.mipmap.boy_profession_icon))
//        }

        if (!TextUtils.isEmpty(mData.city)) {
            mTags.add(UserTag("地区 " + mData.city, R.mipmap.boy_constellation_icon))
        }

        rv_tags.setHasFixedSize(true)
        rv_tags.layoutManager = GridLayoutManager(this, 2)//FlexboxLayoutManager(this)
        rv_tags.isNestedScrollingEnabled = false
        rv_tags.adapter = mUserTagAdapter

        if (!TextUtils.isEmpty(mData.job)) {
            AppUtils.setTvTag(this, "职业 ${mData.job}", 0, 2, tv_job)
        } else {
            tv_job.visibility = View.GONE
        }

        if (!mData.zuojia.isNullOrEmpty()) {
            AppUtils.setTvTag(this, "座驾 ${mData.zuojia}", 0, 2, tv_zuojia)
        } else {
            tv_zuojia.visibility = View.GONE
        }

        if (!mData.hobbit.isNullOrEmpty()) {
            var mHobbies = mData.hobbit?.replace("#", ",")?.split(",")
            var sb = StringBuffer()
            sb.append("爱好 ")
            if (mHobbies != null) {
                for (str in mHobbies) {
                    sb.append("${str} ")
                }
                AppUtils.setTvTag(this, sb.toString(), 0, 2, tv_aihao)
            }
        } else {
            tv_aihao.visibility = View.GONE
        }

        tv_content.text = mData.lookfriendstand

        mData.coverurl?.let {
            val pics = it.split(",")
            mUrls.clear()
            mUrls.addAll(pics.toList())
            imgAdapter.notifyDataSetChanged()
        }

        if (TextUtils.equals(mData.sex, "1")) {
            tv_sex.isSelected = false
            tv_vip.visibility = View.VISIBLE
            img_auther.visibility = View.GONE
            tv_vip.backgroundDrawable = getLevelDrawableOfClassName(mData.classesname.toString(),this)
        } else {
            tv_sex.isSelected = true
            tv_vip.visibility = View.GONE
            img_auther.visibility = View.VISIBLE
            if (TextUtils.equals("1", mData.screen)) {
                img_auther.backgroundDrawable= ContextCompat.getDrawable(this,R.mipmap.video_big)
            } else if(TextUtils.equals("0", mData.screen)){
                img_auther.visibility = View.GONE
            }else if(TextUtils.equals("3",mData.screen)){
                img_auther.visibility = View.GONE
                img_auther.backgroundDrawable=ContextCompat.getDrawable(this,R.mipmap.renzheng_big)
            }
        }


        if (TextUtils.equals(mData.lookstate,"2")) {//已觅约
//            titleBar.hideAllRightButton()
            tv_contact.isEnabled = false
            tv_contact.text = "已觅约"
        } else {
//            titleBar.hideRightButton(0,false)
            tv_contact.isEnabled = true
            tv_contact.text = "联系客服"
        }
    }
}
