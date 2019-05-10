package com.d6.android.app.activities

import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.text.TextUtils
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.FindDateImagesAdapter
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.dialogs.ShareFriendsDialog
import com.d6.android.app.models.MyDate
import com.d6.android.app.utils.*
import com.share.utils.ShareUtils
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import io.rong.imkit.RongIM
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.activity_find_date_detail.*
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

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
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
        title = "觅约详情"
          //R.mipmap.ic_share
        titleBar.addRightButton(rightId = R.mipmap.ic_more_orange, onClickListener = View.OnClickListener {
            val shareDialog = ShareFriendsDialog()
            shareDialog.arguments = bundleOf("from" to "Recommend_findDate","id" to mData.userId.toString(),"sResourceId" to mData.id.toString())
            shareDialog.show(supportFragmentManager, "action")
            shareDialog.setDialogListener { p, s ->
                if (p == 0) {
                    startActivity<ReportActivity>("id" to mData.userId.toString(), "tiptype" to 1)
                }else if(p==3){
                    ShareUtils.share(this@FindDateDetailActivity, SHARE_MEDIA.WEIXIN, mData.lookfriendstand ?: "", mData.looknumber?:"", "http://www.d6-zone.com/JyD6/#/miyuexiangqing?ids="+mData.id, shareListener)
                }
            }
        })

        //设置地址和编号view的elevation
        ViewCompat.setElevation(tv_address_num, dip(3).toFloat())
        rv_images.setHasFixedSize(true)
        rv_images.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_images.adapter = imgAdapter
        val helper = PagerSnapHelper()
        helper.attachToRecyclerView(rv_images)

        tv_contact.setOnClickListener {
            isCheckOnLineAuthUser(this,userId) {
                chatService(this)
//                ShareUtils.share(this@FindDateDetailActivity, SHARE_MEDIA.WEIXIN, mData.lookfriendstand ?: "", mData.looknumber?:"", "http://www.d6-zone.com/JyD6/#/miyuexiangqing?ids="+mData.id, shareListener)
            }
        }
        refreshUI()
    }

    private fun refreshUI() {
        val s = mData.classesname + ""
        if (TextUtils.equals("1", mData.screen)) {
            tv_vip.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.ic_audio_auth,0)
        } else {
            tv_vip.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
        }
        tv_vip.text = s
        tv_age.text = mData.age
        tv_height.text = mData.height
        tv_weight.text = mData.weight
        tv_job.text = mData.job
        tv_content.text = mData.lookfriendstand
        tv_hobbit.text = mData.hobbit
        tv_address_num.text = String.format("%s %s", "", mData.looknumber)
        mData.coverurl?.let {
            val pics = it.split(",")
            mUrls.clear()
            mUrls.addAll(pics.toList())
            imgAdapter.notifyDataSetChanged()
        }
        if (TextUtils.equals(mData.sex, "1")) {
            ll4.visible()
            tv_vip.visible()
            tv_car.text = mData.zuojia
        } else {
            ll4.gone()
            tv_vip.invisible()
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
