package com.d6.android.app.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.GridLayoutManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.adapters.PointRuleAdapter
import com.d6.android.app.adapters.RedHeartRuleAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.interfaces.RequestManager
import com.d6.android.app.models.LoveHeartRule
import com.d6.android.app.models.PointRule
import com.d6.android.app.net.Request
import com.d6.android.app.utils.OnDialogListener
import com.d6.android.app.utils.getLocalUserId
import com.d6.android.app.utils.getLoginToken
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_redheartlist_layout.*
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.toast

/**
 * 红心充值页面
 */
class RedHeartListDialog : DialogFragment(),RequestManager {
    override fun showToast(msg: String) {
        toast(msg)
    }

    override fun dismissDialog() {
        if (activity is BaseActivity) {
            (activity as BaseActivity).dismissDialog()
        }
    }

    override fun onBind(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    private val compositeDisposable = CompositeDisposable()


    private var mLoveHeartRules =  ArrayList<LoveHeartRule>()
    private val mPRAdapter by lazy {
        RedHeartRuleAdapter(mLoveHeartRules)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.Dialog)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window.setLayout(matchParent, resources.getDimensionPixelSize(R.dimen.height_300))
        dialog.window.setGravity(Gravity.BOTTOM)
        dialog.setCanceledOnTouchOutside(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.dialog_redheartlist_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swiprl_redheart_list.setHasFixedSize(true)
        swiprl_redheart_list.layoutManager = GridLayoutManager(context, 3)
        tv_redheart_close.setOnClickListener { dismissAllowingStateLoss() }
        swiprl_redheart_list.adapter = mPRAdapter
        mPRAdapter.setOnItemClickListener { view, position ->
            var loveHeartRule = mLoveHeartRules.get(position)
            if(mOnPayListerner!=null&&loveHeartRule!=null){
                mOnPayListerner.onPayClick(position, loveHeartRule)
            }
        }
        getData()
    }

    private fun getData() {
        Request.findUserLoveRule(getLoginToken()).request(this){ _, data->
            if(data!=null){
                data.let {
                    mLoveHeartRules.addAll(it)
                    mPRAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private var dialogListener: OnDialogListener? = null

    fun setDialogListener(l: (p: Int, s: String?) -> Unit) {
        dialogListener = object : OnDialogListener {
            override fun onClick(position: Int, data: String?) {
                l(position, data)
            }
        }
    }

    lateinit var mOnPayListerner:OnPayListener

    interface OnPayListener{
        fun onPayClick(position: Int,loveHeartRule:LoveHeartRule)
    }

    fun setOnPayListener(l: (p: Int, data: LoveHeartRule) -> Unit){
        this.mOnPayListerner = object :OnPayListener{
            override fun onPayClick(position: Int, loveHeartRule: LoveHeartRule) {
                l(position,loveHeartRule)
            }
        }

    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        dismissDialog()
    }
    override fun onDetach() {
        super.onDetach()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }
}