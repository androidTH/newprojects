package com.d6.android.app.activities

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.utils.*
import com.d6.android.app.widget.frescohelper.FrescoUtils
import com.d6.android.app.widget.frescohelper.IResult
import com.share.utils.ShareUtils
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import kotlinx.android.synthetic.main.activity_goodfriends.*
import com.umeng.socialize.media.UMImage
import kotlinx.android.synthetic.main.share_friends_layout.*
import java.lang.ref.WeakReference

/**
 * 分享邀请好友
 */
class InviteGoodFriendsActivity : BaseActivity(){

    private val headerUrl by lazy {
        SPUtils.instance().getString(Const.User.USER_HEAD)
    }

    private val userName by lazy {
        SPUtils.instance().getString(Const.User.USER_NICK)
    }

    private var mDoIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goodfriends)
        immersionBar.fitsSystemWindows(true).statusBarColor(R.color.trans_parent).statusBarDarkFont(true).init()

        tv_goodfriends_back.setOnClickListener {
            finish()
        }
        tv_invitationfriends_username.text = userName
        iv_user_headView.setImageURI(headerUrl)
        iv_invitationfriends_headView.setImageURI(headerUrl)
        iv_invitationfriends_circleheadView.visibility = View.GONE
        tv_wxshare.setOnClickListener {
            mDoIndex = 1
            dialog()
            ThreadPoolManager.getInstance().execute(mSaveBitmapRunnable)
//            FrescoUtils.loadImage(this,headerUrl,object: IResult<Bitmap> {
//                override fun onResult(result: Bitmap?) {
//                    result?.let {
//                        mBitmaps.add(result)
//                        ThreadPoolManager.getInstance().execute(mSaveBitmapRunnable)
//                    }
//                }
//            })
        }

        tv_pengyougroupshare.setOnClickListener {
//            sharePlatFrom(SHARE_MEDIA.WEIXIN_CIRCLE)
            mDoIndex = 2
            dialog()
            ThreadPoolManager.getInstance().execute(mSaveBitmapRunnable)
//            FrescoUtils.loadImage(this,headerUrl,object: IResult<Bitmap> {
//                override fun onResult(result: Bitmap?) {
//                    result?.let {
//                        mBitmaps.add(result)
//                        ThreadPoolManager.getInstance().execute(mSaveBitmapRunnable)
//                    }
//                }
//            })
        }

        tv_save_local.setOnClickListener {
            mDoIndex = 0
            dialog()
            ThreadPoolManager.getInstance().execute(mSaveBitmapRunnable)
//            FrescoUtils.loadImage(this,headerUrl,object: IResult<Bitmap> {
//                override fun onResult(result: Bitmap?) {
//                    result?.let {
//                        mBitmaps.add(result)
//                        ThreadPoolManager.getInstance().execute(mSaveBitmapRunnable)
//                    }
//                }
//            })
        }
        mHandler = DoHandler(this)
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

    private fun sharePlatFrom(mBitmap:Bitmap,platform: SHARE_MEDIA){
        var umImage = UMImage(this@InviteGoodFriendsActivity, mBitmap)
        umImage.compressStyle=UMImage.CompressStyle.SCALE
        dismissDialog()
        ShareUtils.sharePic(this@InviteGoodFriendsActivity, platform, "分享内容", "分享标题",umImage, shareListener)
    }

    protected var mSaveBitmapRunnable=object: Runnable{

        override fun run() {
            card_share_friends.setDrawingCacheEnabled(true)
            card_share_friends.buildDrawingCache()
            card_share_friends.setDrawingCacheBackgroundColor(Color.WHITE)
            var mBitmap = card_share_friends.getDrawingCache()
//            var mBitmap = LongImageUtils.getInstance().getInviteGoodFriendsBitmap(this@InviteGoodFriendsActivity,"测试","偷偷的告诉你一款社交App，上门有很多高端优男女会员，多金有颜，都是经过人工审核的。还有专属客服24H为你提供交友、约会、线上群聊、线下聚会等私人定制服务。",mBitmaps)
            sendHandlerMessage(mBitmap,mDoIndex)
        }
    }

    private val mBitmaps = ArrayList<Bitmap>()
    private var mHandler:Handler?=null

    fun sendHandlerMessage(bitmap:Bitmap,index:Int){
        var message= mHandler?.obtainMessage()
         message?.what = index
         message?.obj = bitmap
         mHandler?.sendMessage(message)
    }

    private class DoHandler(activity: InviteGoodFriendsActivity) : Handler() {
        //持有弱引用HandlerActivity,GC回收时会被回收掉.
        private val mActivty: WeakReference<InviteGoodFriendsActivity>
        init {
            mActivty = WeakReference<InviteGoodFriendsActivity>(activity)
        }
        override fun handleMessage(msg: Message) {
            val activity = mActivty.get()
            super.handleMessage(msg)
            if (activity != null) {
                if(msg.what==0){
                    activity.dismissDialog()
                    saveBmpToGallery(activity,msg.obj as Bitmap,"d6_goodfriends")
                }else if(msg.what==1){
                    activity.dismissDialog()
                    activity.sharePlatFrom(msg.obj as Bitmap,SHARE_MEDIA.WEIXIN)
                }else if(msg.what==2){
                    activity.dismissDialog()
                    activity.sharePlatFrom(msg.obj as Bitmap,SHARE_MEDIA.WEIXIN_CIRCLE)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler=null
    }
}