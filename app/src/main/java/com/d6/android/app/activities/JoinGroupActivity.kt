package com.d6.android.app.activities

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.ApplayJoinGroupDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.widget.frescohelper.FrescoUtils
import com.d6.android.app.widget.frescohelper.IResult
import com.share.utils.ShareUtils
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import kotlinx.android.synthetic.main.activity_joingroup.*
import kotlinx.android.synthetic.main.joingroup_share.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.imageBitmap
import org.jetbrains.anko.toast
import java.lang.ref.WeakReference


/**
 * 加入群
 */
class JoinGroupActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joingroup)
        immersionBar
                .fitsSystemWindows(false)
                .statusBarColor(R.color.trans_parent)
                .init()

        if(intent.hasExtra("groupId")){
            tv_groupnumber.text = intent.getStringExtra("groupId")
        }

        iv_back_close.setOnClickListener {
            finish()
        }
        tv_wxshare.setOnClickListener {
            mDoIndex = 1
            dialog()
            FrescoUtils.loadImage(this, getLocalUserHeadPic(), object : IResult<Bitmap> {
                override fun onResult(result: Bitmap?) {
                    result?.let {
                        mBitmaps.add(result)
                        ThreadPoolManager.getInstance().execute(mSaveBitmapRunnable)
                    }
                }
            })
        }

        tv_pengyougroupshare.setOnClickListener {
            mDoIndex = 2
            dialog()
            FrescoUtils.loadImage(this, getLocalUserHeadPic(), object : IResult<Bitmap> {
                override fun onResult(result: Bitmap?) {
                    result?.let {
                        mBitmaps.add(result)
                        ThreadPoolManager.getInstance().execute(mSaveBitmapRunnable)
                    }
                }
            })
        }

        tv_save_local.setOnClickListener {
            mDoIndex = 0
            dialog()
            FrescoUtils.loadImage(this, getLocalUserHeadPic(), object : IResult<Bitmap> {
                override fun onResult(result: Bitmap?) {
                    result?.let {
                        mBitmaps.add(result)
                        ThreadPoolManager.getInstance().execute(mSaveBitmapRunnable)
                    }
                }
            })
        }
//            ThreadPoolManager.getInstance().execute(mSaveBitmapRunnable) }

        btn_joingroup.setOnClickListener {
            var mApplayJoinGroupDialog = ApplayJoinGroupDialog()
            mApplayJoinGroupDialog.arguments = bundleOf("groupId" to "234456")
            mApplayJoinGroupDialog.show(supportFragmentManager,"joingroup")
        }

        mHandler = JoinGroupActivity.DoHandler(this)
//        groupheaderview_share.setImageURI(getLocalUserHeadPic())
    }

    override fun onResume() {
        super.onResume()
        getUserInfo()
    }



    private fun getUserInfo() {
//        Request.getUserInfo("", getLocalUserId()).request(this, success = { _, data ->
//             data?.let {
//
//             }
//        }) { _, _ ->
//        }

        Request.getAccountInviteLink(getLoginToken()).request(this,false,success={ msg, data->
            data?.let {
                iv_joingroup_qcode.setImageURI(it.sInviteLinkPic)
                FrescoUtils.loadImage(this, it.sInviteLinkPic, object : IResult<Bitmap> {
                    override fun onResult(result: Bitmap?) {
                        result?.let {
                            mBitmaps.add(0,result)
                        }
                    }
                })
            }
        }){code,msg->

        }
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
        var umImage = UMImage(this@JoinGroupActivity, mBitmap)
        umImage.compressStyle=UMImage.CompressStyle.SCALE
        dismissDialog()
        ShareUtils.sharePic(this@JoinGroupActivity, platform, "分享内容", "分享标题",umImage, shareListener)
    }

    protected var mSaveBitmapRunnable=object: Runnable{

        override fun run() {
//            rl_joingroup_share.setDrawingCacheEnabled(true)
//            rl_joingroup_share.buildDrawingCache()
//            rl_joingroup_share.setDrawingCacheBackgroundColor(Color.WHITE)
//            var mBitmap = rl_joingroup_share.getDrawingCache()
            var mBitmap = LongImageUtils.getInstance().getJoinGroupBitmap(this@JoinGroupActivity,"测试","偷偷的告诉你一款社交App，上门有很多高端优男女会员，多金有颜，都是经过人工审核的。还有专属客服24H为你提供交友、约会、线上群聊、线下聚会等私人定制服务。",mBitmaps)
            sendHandlerMessage(mBitmap,mDoIndex)
        }
    }

    private var mDoIndex = -1
    private val mBitmaps = ArrayList<Bitmap>()
    private var mHandler:Handler?=null

    fun sendHandlerMessage(bitmap:Bitmap,index:Int){
        var message= mHandler?.obtainMessage()
        message?.what = index
        message?.obj = bitmap
        mHandler?.sendMessage(message)
    }


    private class DoHandler(activity: JoinGroupActivity) : Handler() {
        //持有弱引用HandlerActivity,GC回收时会被回收掉.
        private val mActivty: WeakReference<JoinGroupActivity>
        init {
            mActivty = WeakReference<JoinGroupActivity>(activity)
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
                    activity.sharePlatFrom(msg.obj as Bitmap, SHARE_MEDIA.WEIXIN)
                }else if(msg.what==2){
                    activity.dismissDialog()
                    activity.sharePlatFrom(msg.obj as Bitmap,SHARE_MEDIA.WEIXIN_CIRCLE)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar?.destroy()
    }
}