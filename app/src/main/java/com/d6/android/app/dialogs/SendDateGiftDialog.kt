package com.d6.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.activities.MyPointsActivity
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.interfaces.RequestManager
import com.d6.android.app.models.Square
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.SENDLOVEHEART_DIALOG
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_senddategift.*
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent
import org.jetbrains.anko.support.v4.startActivity

/**
 * 邀约礼物
 */
class SendDateGiftDialog : DialogFragment(),RequestManager {

    private var voiceChat:Square?=null
    private var voicechatType = "1"
    private var mLocalUserLoveHeartCount:Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FadeDialog)
    }

    private val compositeDisposable by lazy {
        CompositeDisposable()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window.setLayout((screenWidth() * 0.83f).toInt()+dip(30), wrapContent)
        dialog.window.setGravity(Gravity.CENTER)
        dialog.setCanceledOnTouchOutside(true)
    }

    override fun show(manager: FragmentManager?, tag: String?) {
        val ft = manager?.beginTransaction()
        ft?.add(this, tag)
        ft?.commitAllowingStateLoss()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.dialog_senddategift, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_action.setOnClickListener {
            dialogListener?.let {
                it.onClick(0,"ok")
            }
        }

        tv_redheart_gobuy.setOnClickListener {
            isBaseActivity {
                startActivity<MyPointsActivity>("fromType" to SENDLOVEHEART_DIALOG)
                dismissAllowingStateLoss()
            }
        }

        tv_close.setOnClickListener {
            dismissAllowingStateLoss()
        }


        if(TextUtils.equals(voicechatType,"2")){
            mLocalUserLoveHeartCount = voiceChat!!.iRemainPoint!!
            tv_redheart_count.text = "剩余 [img src=redheart_small/] 不足 (剩余${mLocalUserLoveHeartCount})"
        }

        gift_pic.setImageURI("")

        tv_gift_heartnums.text="900 [img src=redheart_small/]"

        getUserInfo()
//        var info = UserInfo("${voiceChat?.userid}","${voiceChat?.name}", Uri.parse("${voiceChat?.picUrl}"))
//        RongContext.getInstance().currentUserInfo = info
    }

    private fun getUserInfo() {
        Request.getUserInfo(getLocalUserId(), getLocalUserId()).request((context as BaseActivity),false,success= { msg, data ->
            data?.let {
                //                if(it.iLovePoint < mMinLoveHeart?.toInt() ?: 0){
//                    tv_action.background = ContextCompat.getDrawable(context,R.drawable.shape_radius_4r_33)
//                }
                mLocalUserLoveHeartCount = it.iLovePoint
                tv_redheart_count.text = "${mLocalUserLoveHeartCount} [img src=redheart_small/]"
                tv_redheart_balance.text = "还差30 "
            }
        })
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

    override fun dismissDialog() {

    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }
}