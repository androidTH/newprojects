package com.d6.android.app.activities

import android.graphics.Color
import android.os.Bundle
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.utils.*
import com.share.utils.ShareUtils
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import kotlinx.android.synthetic.main.activity_goodfriends.*
import com.umeng.socialize.media.UMImage

/**
 * 分享邀请好友
 */
class InviteGoodFriendsActivity : BaseActivity(){

    private val headerUrl by lazy {
        SPUtils.instance().getString(Const.User.USER_HEAD)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goodfriends)
        immersionBar.fitsSystemWindows(true).statusBarColor(R.color.trans_parent).statusBarDarkFont(true).init()

        tv_goodfriends_back.setOnClickListener {
            finish()
        }

        iv_user_headView.setImageURI(headerUrl)
        tv_wxshare.setOnClickListener {
            sharePlatFrom(SHARE_MEDIA.WEIXIN)
        }

        tv_pengyougroupshare.setOnClickListener {
            sharePlatFrom(SHARE_MEDIA.WEIXIN_CIRCLE)
        }

        tv_save_local.setOnClickListener {
            ThreadPoolManager.getInstance().execute(mSaveBitmapRunnable)
        }
    }

    private fun sharePlatFrom(platform: SHARE_MEDIA){
        ll_top.setDrawingCacheEnabled(true)
        ll_top.buildDrawingCache()
        ll_top.setDrawingCacheBackgroundColor(Color.WHITE);
        var mBitmap = ll_top.getDrawingCache()
        var umImage = UMImage(this@InviteGoodFriendsActivity, mBitmap)
        umImage.compressStyle=UMImage.CompressStyle.SCALE
        ShareUtils.sharePic(this@InviteGoodFriendsActivity, platform, "分享内容", "分享标题",umImage, shareListener)

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

    protected var mSaveBitmapRunnable=object: Runnable{
        override fun run() {
            ll_top.setDrawingCacheEnabled(true)
            ll_top.buildDrawingCache()
            ll_top.setDrawingCacheBackgroundColor(Color.WHITE);
            var mBitmap = ll_top.getDrawingCache()
            saveBmpToGallery(this@InviteGoodFriendsActivity,mBitmap,"d6_goodfriends")
        }
    }
}
