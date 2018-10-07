package com.d6.android.app.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.activities.TrendDetailActivity
import com.d6.android.app.adapters.PointRuleAdapter
import com.d6.android.app.adapters.SquareDetailCommentAdapter
import com.d6.android.app.adapters.TrendCommentAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.interfaces.RequestManager
import com.d6.android.app.models.Comment
import com.d6.android.app.models.PointRule
import com.d6.android.app.models.Square
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.OnDialogListener
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.screenWidth
import com.d6.android.app.widget.SwipeRefreshRecyclerLayout
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_release_new_trends.*
import kotlinx.android.synthetic.main.dialog_pointslist_layout.*
import kotlinx.android.synthetic.main.dialog_select_sex_layout.*
import kotlinx.android.synthetic.main.dialog_trend_comments_layout.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.textColor
import org.jetbrains.anko.wrapContent

/**
 * 积分充值页面
 */
class PointsListDialog : DialogFragment(),RequestManager {
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


    private var mPointsRule =  ArrayList<PointRule>()
    private val mPRAdapter by lazy {
        PointRuleAdapter(mPointsRule)
    }

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
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
            inflater?.inflate(R.layout.dialog_pointslist_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swiprl_points_list.setHasFixedSize(true)
        swiprl_points_list.layoutManager = GridLayoutManager(context, 3)
        tv_points_close.setOnClickListener { dismissAllowingStateLoss() }
        swiprl_points_list.adapter = mPRAdapter
//        ll_jifen.setOnClickListener {
//            val payResultDialog = PayResultDialog()
////            payResultDialog.arguments = bundleOf("payresult" to payResultDialog.PAY_FAIL)
//            payResultDialog.show((context as BaseActivity).supportFragmentManager, "action")
//            payResultDialog.setDialogListener { p, s ->
//
//            }
//        }
        getData()
    }

    private fun getData() {
        Request.getPointsRule().request(this){_,data->
            if(data!=null){
                data.let {
                    mPointsRule.addAll(it)
                    mPRAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun loadData() {

    }

    private var dialogListener: OnDialogListener? = null

    fun setDialogListener(l: (p: Int, s: String?) -> Unit) {
        dialogListener = object : OnDialogListener {
            override fun onClick(position: Int, data: String?) {
                l(position, data)
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