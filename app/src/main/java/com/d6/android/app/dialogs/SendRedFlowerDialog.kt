package com.d6.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.GridLayoutManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.adapters.BuyFlowerAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.Comment
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.widget.CustomToast
import com.d6.android.app.widget.RxRecyclerViewDividerTool
import com.d6.android.app.widget.badge.DisplayUtil
import kotlinx.android.synthetic.main.dialog_send_redflower.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.dip
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent

/**
 * 我的页面操作弹窗
 */
class SendRedFlowerDialog : DialogFragment() {

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private var mBuyFlowerAdapter: BuyFlowerAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.Dialog)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window.setLayout(matchParent, wrapContent)
        dialog.window.setGravity(Gravity.BOTTOM)
        dialog.setCanceledOnTouchOutside(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.dialog_send_redflower, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var id =  arguments.getString("userId")
        iv_sendflower_close.setOnClickListener {
            dismissAllowingStateLoss()
        }

        tv_wx_pay_flower.setOnClickListener {
            dismissAllowingStateLoss()
            doBuyRedFlower(id)
        }

        var data = arrayListOf<String>("1","10","50","100","200","520")

        rv_send_redflower.setHasFixedSize(true)
        rv_send_redflower.layoutManager = GridLayoutManager(context,3)
        mBuyFlowerAdapter = BuyFlowerAdapter(data.toList())
        rv_send_redflower.addItemDecoration(RxRecyclerViewDividerTool(DisplayUtil.dpToPx(15)))
        rv_send_redflower.adapter = mBuyFlowerAdapter

        mBuyFlowerAdapter?.setOnItemClickListener() { adapter, view, position ->
                 mBuyFlowerAdapter?.selectedIndex = position
                 mBuyFlowerAdapter?.notifyDataSetChanged()
        }
        getUserInfo(id)
    }

    private fun getUserInfo(id: String) {
        Request.getUserInfo(userId, id).request((context as BaseActivity),false,success= { msg, data ->
            data?.let {
                iv_sendflower_headView.setImageURI(it.picUrl)
                tv_sendflower_name.text = it.name
            }
        })
    }

    private fun doBuyRedFlower(id:String){
        val dialogSendFlowerSuccess = DialogSendFlowerSuccess()
            dialogSendFlowerSuccess.arguments = bundleOf("userId" to id,"nums" to "30")
            dialogSendFlowerSuccess.show((context as BaseActivity).supportFragmentManager, "sendflower")
    }

    private var dialogListener: OnDialogListener? = null

    fun setDialogListener(l: (p: Int, s: String?) -> Unit) {
        dialogListener = object : OnDialogListener {
            override fun onClick(position: Int, data: String?) {
                l(position, data)
            }
        }
    }
}