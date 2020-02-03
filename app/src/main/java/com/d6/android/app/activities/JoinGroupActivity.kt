package com.d6.android.app.activities

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.ApplayJoinGroupDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.NewGroupBean
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.widget.frescohelper.FrescoUtils
import com.d6.android.app.widget.frescohelper.IResult
import com.share.utils.ShareUtils
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import io.rong.imkit.RongIM
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.activity_joingroup.*
import kotlinx.android.synthetic.main.joingroup_share.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.imageBitmap
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast
import java.lang.ref.WeakReference


/**
 * 加入群
 */
class JoinGroupActivity : BaseActivity() {

    private var JoinGroupStatus:Int = 1 // 1 申请加入 2 正在审核 3 通过
    private var iIsApply:Int = -1 //1、申请中  2、未申请
    private var mGroupHeaderPic:String = ""
    private var mGroupNum:String = ""
    private var mGroupId:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joingroup)
        immersionBar
                .fitsSystemWindows(false)
                .statusBarColor(R.color.trans_parent)
                .init()

        if(intent.hasExtra("groupBean")){
            var mGroupBean = intent.getParcelableExtra<NewGroupBean>("groupBean")
            mGroupHeaderPic = "${mGroupBean.sGroupPic}"
            mGroupNum = "${mGroupBean.iGroupNum}"
            mGroupId = "${mGroupBean.sId}"
            tv_groupnumber.text = "${mGroupNum}"
            JoinGroupStatus = mGroupBean.iInGroup!!
            iIsApply = mGroupBean.iIsApply!!
            tv_groupname.text = "${mGroupBean.sGroupName}"
            groupheaderview.setImageURI("${mGroupHeaderPic}")
        }

        iv_back_close.setOnClickListener {
            finish()
        }
        tv_wxshare.setOnClickListener {
            mDoIndex = 1
            dialog()
            FrescoUtils.loadImage(this, mGroupHeaderPic, object : IResult<Bitmap> {
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
            FrescoUtils.loadImage(this,mGroupHeaderPic, object : IResult<Bitmap> {
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
            FrescoUtils.loadImage(this, mGroupHeaderPic, object : IResult<Bitmap> {
                override fun onResult(result: Bitmap?) {
                    result?.let {
                        mBitmaps.add(result)
                        ThreadPoolManager.getInstance().execute(mSaveBitmapRunnable)
                    }
                }
            })
        }
//            ThreadPoolManager.getInstance().execute(mSaveBitmapRunnable) }

        if(JoinGroupStatus==1){
            if(iIsApply!=1){
                btn_joingroup.textColor = ContextCompat.getColor(this,R.color.white)
                btn_joingroup.text = "申请加入该群"
                btn_joingroup.background = ContextCompat.getDrawable(this,R.drawable.shape_setting_bg)
            }else{
                btn_joingroup.textColor = ContextCompat.getColor(this,R.color.color_888888)
                btn_joingroup.text = "正在审核中"
                btn_joingroup.background = ContextCompat.getDrawable(this,R.drawable.shape_5r_ef)
            }
        }else if(JoinGroupStatus==2){
            btn_joingroup.textColor = ContextCompat.getColor(this,R.color.color_black)
            btn_joingroup.text = "你已入群，打开群聊"
            btn_joingroup.background = ContextCompat.getDrawable(this,R.drawable.shape_5r_ef)
        }else{
            btn_joingroup.textColor = ContextCompat.getColor(this,R.color.color_888888)
            btn_joingroup.text = "正在审核中"
            btn_joingroup.background = ContextCompat.getDrawable(this,R.drawable.shape_5r_ef)
        }

        btn_joingroup.setOnClickListener {
            if(JoinGroupStatus==1){
                if(iIsApply!=1){
                    var mApplayJoinGroupDialog = ApplayJoinGroupDialog()
//                mApplayJoinGroupDialog.arguments = bundleOf("groupId" to "${mGroupId}")
                    mApplayJoinGroupDialog.setDialogListener { p, s ->
                        if(p==2){
                            s?.let {
                                applayToGroup(p,"${s}")
                            }
                        }else{
                            btn_joingroup.textColor = ContextCompat.getColor(this,R.color.color_black)
                            btn_joingroup.text = "你已入群，打开群聊"
                            btn_joingroup.background = ContextCompat.getDrawable(this,R.drawable.shape_5r_ef)
                        }
                    }
                    mApplayJoinGroupDialog.show(supportFragmentManager,"joingroup")
                }
            }else{
                RongIM.getInstance().startConversation(this, Conversation.ConversationType.GROUP,"${mGroupId}","${tv_groupname.text}")
            }
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
            var mBitmap = LongImageUtils.getInstance().getJoinGroupBitmap(this@JoinGroupActivity,"${tv_groupname.text}","${tv_groupnumber.text}",mBitmaps)
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

    fun applayToGroup(p:Int,content:String){
        Request.applyToGroup("${mGroupId}","${content}").request(this,false,success={msg,data->
            iIsApply = 1
            btn_joingroup.textColor = ContextCompat.getColor(this,R.color.color_888888)
            btn_joingroup.text = "正在审核中"
            btn_joingroup.background = ContextCompat.getDrawable(this,R.drawable.shape_5r_ef)
        }){code,msg->
            toast(msg)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar?.destroy()
    }
}