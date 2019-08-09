package com.d6.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.adapters.MemberShipAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.MemberBean
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import kotlinx.android.synthetic.main.dialog_slivermember_price.*
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent

/**
 * 开通会员
 */
class SilverMemberDialog : DialogFragment() {

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
            if(!TextUtils.equals(tv_choose_address.text,"地区")){
                dialogListener?.onClick(1000, tv_slivemember_price.text.toString())
            }
        }

        tv_wxpay_member.text = "微信支付"
    }

    fun setAddressd(address:String,currentIndex:Int){
        if(TextUtils.equals(address,"不限地区")){
            tv_choose_address.text = "地区"
            tv_wxpay_member.background = ContextCompat.getDrawable(context,R.drawable.shape_4r_8054)
            tv_wxpay_member.text = "微信支付"
        }else{
            tv_choose_address.text = address
            tv_wxpay_member.background = ContextCompat.getDrawable(context,R.drawable.shape_4r_54)
            getAreaNameMemberPriceList(address,currentIndex)
        }
    }


    private fun getAreaNameMemberPriceList(areaName:String,currentIndex:Int){
        isBaseActivity {
            Request.findUserClasses(getLoginToken(),areaName).request(it){ msg, data->
                data?.list?.let {
                    var memberBean = it.get(currentIndex)
                    tv_wxpay_member.text = "微信支付·¥${memberBean.iAndroidPrice}"
                    tv_slivemember_price.text = "${memberBean.iAndroidPrice}"
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