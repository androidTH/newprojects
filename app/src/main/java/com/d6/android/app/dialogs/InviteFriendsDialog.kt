package com.d6.android.app.dialogs

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.d6.android.app.R
import com.d6.android.app.adapters.InviateBannerHolder
import com.d6.android.app.adapters.MemberDescHolder
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.interfaces.RequestManager
import com.d6.android.app.models.InviteLinkBean
import com.d6.android.app.models.MemberDesc
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.widget.convenientbanner.holder.CBViewHolderCreator
import com.d6.android.app.widget.convenientbanner.listener.OnPageChangeListener
import com.share.utils.ShareUtils
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_invitefriends.*
import kotlinx.android.synthetic.main.share_friends_layout.*
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast
import org.jetbrains.anko.wrapContent
import java.lang.ref.WeakReference

/**
 * 约会发送出错
 */
class InviteFriendsDialog : DialogFragment(),RequestManager {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FadeDialog)
    }

    private val compositeDisposable by lazy {
        CompositeDisposable()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window.setLayout((screenWidth() * 0.90f).toInt()+dip(30), wrapContent)
        dialog.window.setGravity(Gravity.CENTER)
        dialog.setCanceledOnTouchOutside(false)

        var params = rl_sharebitmap.layoutParams
        params.width = matchParent
        params.height = (screenHeight() * 0.70f).toInt()
        rl_sharebitmap.layoutParams = params
    }

    override fun show(manager: FragmentManager?, tag: String?) {
        val ft = manager?.beginTransaction()
        ft?.add(this, tag)
        ft?.commitAllowingStateLoss()
    }

    private var mDoIndex = -1

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.dialog_invitefriends, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            var mInviteLinkBean = arguments.getParcelable<InviteLinkBean>("bean")
            if (mInviteLinkBean != null) {
                setInviteGoodFriendsUI(mInviteLinkBean)
            } else {
                getAccountInviteLink()
            }
            tv_copy_url.setOnClickListener {
                dialogListener?.let {
                    it.onClick(4,"${mInviteLinkBean.sInviteLinkPic}")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            getAccountInviteLink()
        }

        tv_close.setOnClickListener { dismissAllowingStateLoss() }

        tv_wxshare.setOnClickListener {
         isBaseActivity {
             mDoIndex = 1
             it.dialog()
             ThreadPoolManager.getInstance().execute(mSaveBitmapRunnable)
         }
        }

        tv_pengyougroupshare.setOnClickListener {
            isBaseActivity {
                mDoIndex = 2
                it.dialog()
                ThreadPoolManager.getInstance().execute(mSaveBitmapRunnable)
            }
        }

        tv_save_local.setOnClickListener {
            isBaseActivity {
                mDoIndex = 0
                it.dialog()
                it.toast("图片已保存到相册")
                ThreadPoolManager.getInstance().execute(mSaveBitmapRunnable)
            }
        }

        iv_invitationfriends_headView.setImageURI(getLocalUserHeadPic())
        tv_invitationfriends_username.text = getLocalUserName()

        mHandler = DoHandler(this)
    }
    private var mHandler:Handler?=null

    protected var mSaveBitmapRunnable=object: Runnable{

        override fun run() {
//            card_share_friends.setDrawingCacheEnabled(true)
//            card_share_friends.buildDrawingCache()
//            card_share_friends.setDrawingCacheBackgroundColor(Color.WHITE)
//            var mBitmap = card_share_friends.getDrawingCache()
//            var mBitmap = LongImageUtils.getInstance().getInviteGoodFriendsBitmap((context as BaseActivity),"测试","偷偷的告诉你一款社交App，上门有很多高端优男女会员，多金有颜，都是经过人工审核的。还有专属客服24H为你提供交友、约会、线上群聊、线下聚会等私人定制服务。",mBitmaps)
            var mBitmap = LongImageUtils.getInstance().getScrollViewBitmap(nestscroll)
            sendHandlerMessage(mBitmap,mDoIndex)
        }
    }

    fun sendHandlerMessage(bitmap: Bitmap, index:Int){
        var message= mHandler?.obtainMessage()
        message?.what = index
        message?.obj = bitmap
        mHandler?.sendMessage(message)
    }

    private fun sharePlatFrom(mBitmap:Bitmap,platform: SHARE_MEDIA){
        var umImage = UMImage(context as BaseActivity, mBitmap)
        umImage.compressStyle=UMImage.CompressStyle.SCALE
        ShareUtils.sharePic(context as BaseActivity, platform, "分享内容", "分享标题",umImage,object:UMShareListener{
            override fun onCancel(p0: SHARE_MEDIA?) {
            }

            override fun onError(p0: SHARE_MEDIA?, p1: Throwable?) {
            }

            override fun onResult(p0: SHARE_MEDIA?) {
                dismissAllowingStateLoss()
            }

            override fun onStart(p0: SHARE_MEDIA?) {

            }
        })
    }

    private class DoHandler(activity: InviteFriendsDialog) : Handler() {
        //持有弱引用HandlerActivity,GC回收时会被回收掉.
        private val mActivty: WeakReference<InviteFriendsDialog>
        init {
            mActivty = WeakReference<InviteFriendsDialog>(activity)
        }
        override fun handleMessage(msg: Message) {
            val activity = mActivty.get()
            super.handleMessage(msg)
            if (activity != null) {
                if(msg.what==0){
                    if(activity.context!=null){
                        (activity.context as BaseActivity).dismissDialog()
                    }
                    saveBmpToGallery((activity.context as BaseActivity),msg.obj as Bitmap,"d6_goodfriends")
                }else if(msg.what==1){
                    if(activity.context!=null){
                        (activity.context as BaseActivity).dismissDialog()
                    }
                    activity.sharePlatFrom(msg.obj as Bitmap,SHARE_MEDIA.WEIXIN)
                }else if(msg.what==2){
                    if(activity.context!=null){
                        (activity.context as BaseActivity).dismissDialog()
                    }
                    activity.sharePlatFrom(msg.obj as Bitmap,SHARE_MEDIA.WEIXIN_CIRCLE)
                }
            }
        }
    }

    private fun getAccountInviteLink(){
        isBaseActivity {
            Request.getAccountInviteLink(getLoginToken()).request(it,false,success={ msg, data->
                data?.let {
                    setInviteGoodFriendsUI(it)
                }
            }){code,msg->

            }
        }
    }

    private fun setInviteGoodFriendsUI(mInviteLinkBean: InviteLinkBean) {
        iv_invitationfriends_qcode.setImageURI(mInviteLinkBean.sInviteLinkPic)
        tv_invitationfriends_desc.text = mInviteLinkBean.sInviteDesc
    }

    private var dialogListener: OnDialogListener? = null

    fun setDialogListener(l: (p: Int, s: String?) -> Unit) {
        dialogListener = object : OnDialogListener {
            override fun onClick(position: Int, data: String?) {
                l(position, data)
            }
        }
    }

    override fun showToast(msg: String) {
        toast(msg)
    }

    override fun onBind(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }

    override fun dismissDialog() {

    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}