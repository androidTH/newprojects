package com.d6.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.d6.android.app.R
import com.d6.android.app.adapters.MemberShipAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.easypay.EasyPay
import com.d6.android.app.easypay.PayParams
import com.d6.android.app.easypay.callback.OnPayInfoRequestListener
import com.d6.android.app.easypay.callback.OnPayResultListener
import com.d6.android.app.easypay.enums.HttpType
import com.d6.android.app.easypay.enums.NetworkClientType
import com.d6.android.app.easypay.enums.PayWay
import com.d6.android.app.eventbus.FlowerMsgEvent
import com.d6.android.app.extentions.request
import com.d6.android.app.models.MemberBean
import com.d6.android.app.models.Square
import com.d6.android.app.net.API
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.widget.CustomToast
import com.d6.android.app.widget.RxRecyclerViewDividerTool
import com.d6.android.app.widget.badge.DisplayUtil
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration
import io.rong.eventbus.EventBus
import kotlinx.android.synthetic.main.dialog_slivermember_price.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.wrapContent

/**
 * 开通会员
 */
class SilverMemberDialog : DialogFragment() {

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private var mMemberPriceList = ArrayList<MemberBean>()
    var mMemberShipAdapter: MemberShipAdapter?=null

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
            inflater?.inflate(R.layout.dialog_slivermember_price, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var desc = if(arguments.containsKey("desc")){
                arguments.getString("desc")
        }else{
            ""
        }

        var ids =  arguments.getString("ids")
        tv_slive_title.text= if(TextUtils.equals(ids,"22")){
             "开通普通会员"
        }else{
             "开通白银会员"
        }

        tv_slivermember_des.text = desc

        iv_silverMembership_close.setOnClickListener {
            dismissAllowingStateLoss()
        }

        tv_choose_address.setOnClickListener {
            dialogListener?.onClick(2001,"地区")
        }

        tv_wxpay_member.setOnClickListener {
            dialogListener?.onClick(1000, "微信支付")
        }
    }

    fun setAddressd(address:String){
        if(TextUtils.equals(address,"不限地区")){
            tv_choose_address.text = "地区"
            tv_wxpay_member.background = ContextCompat.getDrawable(context,R.drawable.shape_4r_8054)
        }else{
            tv_choose_address.text = address
            tv_wxpay_member.background = ContextCompat.getDrawable(context,R.drawable.shape_4r_54)
        }
        getAreaNameMemberPriceList(address)
    }


    private fun getAreaNameMemberPriceList(areaName:String){
        isBaseActivity {
            Request.findUserClasses(getLoginToken(),areaName).request(it){ msg, data->
                data?.list?.let {
                    Log.i("mem","数量${it.size}")
                    mMemberPriceList = it
                    mMemberShipAdapter?.let {
                        it.setNewData(mMemberPriceList)
                    }
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

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }
}