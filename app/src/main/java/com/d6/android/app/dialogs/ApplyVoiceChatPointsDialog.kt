package com.d6.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.activities.MyPointsActivity
import com.d6.android.app.adapters.BuyRedHeartAdapter
import com.d6.android.app.adapters.BuyRedHeartVoiceChatAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.interfaces.RequestManager
import com.d6.android.app.models.LoveHeartRule
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.SENDLOVEHEART_DIALOG
import com.d6.android.app.widget.RxRecyclerViewDividerTool
import com.d6.android.app.widget.badge.DisplayUtil
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_apply_voicechat_points.*
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent
import org.jetbrains.anko.support.v4.startActivity

/**
 * 申请连麦
 */
class ApplyVoiceChatPointsDialog : DialogFragment(),RequestManager {

    private var fromType = ""
    private var mLocalUserLoveHeartCount:Int? = -1

    private var mBuyRedHeartVoiceChatAdapter: BuyRedHeartVoiceChatAdapter?=null
    private var mLoveHeartList=ArrayList<LoveHeartRule>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FadeDialog)
    }

    private val compositeDisposable by lazy {
        CompositeDisposable()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window.setLayout((screenWidth() * 0.86f).toInt()+dip(30), wrapContent)
        dialog.window.setGravity(Gravity.CENTER)
        dialog.setCanceledOnTouchOutside(true)
    }

    override fun show(manager: FragmentManager?, tag: String?) {
        val ft = manager?.beginTransaction()
        ft?.add(this, tag)
        ft?.commitAllowingStateLoss()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.dialog_apply_voicechat_points, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_redheart_gobuy.setOnClickListener {
            isBaseActivity {
                startActivity<MyPointsActivity>("fromType" to SENDLOVEHEART_DIALOG)
                dismissAllowingStateLoss()
            }
        }

        tv_close.setOnClickListener {
            dismissAllowingStateLoss()
        }

        rv_voicechat_redheart.setHasFixedSize(true)
        rv_voicechat_redheart.layoutManager = GridLayoutManager(context,3) as RecyclerView.LayoutManager?
        mBuyRedHeartVoiceChatAdapter = BuyRedHeartVoiceChatAdapter(null)
        rv_voicechat_redheart.addItemDecoration(RxRecyclerViewDividerTool(DisplayUtil.dpToPx(10)))
        rv_voicechat_redheart.adapter = mBuyRedHeartVoiceChatAdapter

       if(arguments !=null){
//           var it = arguments.getParcelable("explain") as IntegralExplain
//           tv_preparepoints.text = "本次申请赴约将预付${it.iAppointPoint}积分,申请成功后先聊一聊再决定是否赴约哦"
//           tv_agree_points.text = "对方同意,预付${it.iAppointPoint}积分"
//           tv_noagree_points.text = "对方拒绝,返还${it.iAppointPointRefuse}积分"
//           tv_timeout_points.text = "3天内未回复,返还${it.iAppointPointCancel}积分"
        }

        getUserInfo()
        setLoveHeartData()
    }

    private fun setLoveHeartData(){
        var mLoveHeartRule1 = LoveHeartRule("1")
        mLoveHeartRule1.iLoveCount = 1

        var mLoveHeartRule2 = LoveHeartRule("1")
        mLoveHeartRule2.iLoveCount = 2

        var mLoveHeartRule3 = LoveHeartRule("1")
        mLoveHeartRule3.iLoveCount = 3

        var mLoveHeartRule4 = LoveHeartRule("1")
        mLoveHeartRule4.iLoveCount = 4

        var mLoveHeartRule5 = LoveHeartRule("1")
        mLoveHeartRule5.iLoveCount = 5

        var mLoveHeartRule6 = LoveHeartRule("1")
        mLoveHeartRule6.iLoveCount = 6

        mLoveHeartList.add(mLoveHeartRule1)
        mLoveHeartList.add(mLoveHeartRule2)
        mLoveHeartList.add(mLoveHeartRule3)
        mLoveHeartList.add(mLoveHeartRule4)
        mLoveHeartList.add(mLoveHeartRule5)
        mLoveHeartList.add(mLoveHeartRule6)
        mBuyRedHeartVoiceChatAdapter?.let {
            it.setNewData(mLoveHeartList)
        }
    }

    private fun getUserInfo() {
        Request.getUserInfo(getLocalUserId(), getLocalUserId()).request((context as BaseActivity),false,success= { msg, data ->
            data?.let {
                mLocalUserLoveHeartCount = it.iLovePoint
                tv_redheart_count.text = "剩余 [img src=redheart_small/] 不足 (剩余${mLocalUserLoveHeartCount})"
            }
        })
    }

    private fun getData() {
        dismissAllowingStateLoss()
        isBaseActivity {
            //194ecdb4-4809-4b2d-bf32-42a3342964df
        }
    }

    private fun queryPoints(){
//        isBaseActivity {
//            Request.queryAppointmentPoint(userId).request(it, success = {msg,data->
//                data?.let {
//                    tv_preparepoints.text = "本次约会将预付${it.iAppointPoint}积分"
//                    tv_agree_points.text = "对方同意,预付${it.iAppointPoint}积分"
//                    tv_noagree_points.text = "对方拒绝,返还${it.iAppointPointRefuse}积分"
//                    tv_timeout_points.text = "超时未回复,返还${it.iAppointPointCancel}积分"
//                }
//            }){code,msg->
//                if(code == 2){
//                    toast(msg)
//                    dismissAllowingStateLoss()
//                    var openErrorDialog = OpenDateErrorDialog()
//                    openErrorDialog.arguments= bundleOf("code" to code)
//                    openErrorDialog.show(it.supportFragmentManager, "d")
//                }
//            }
//        }
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