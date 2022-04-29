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
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import kotlinx.android.synthetic.main.dialog_privatemember_price.*
import kotlinx.android.synthetic.main.dialog_privatemember_price.tv_wxpay_member
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent

/**
 * 私人定制
 */
class PrivateMemberDialog : DialogFragment() {

    private var mCheckedType:String = "1"
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
            inflater?.inflate(R.layout.dialog_privatemember_price, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var yearprice = if(arguments.containsKey("yearprice")){
                arguments.getString("yearprice")
        }else{
            ""
        }

        var foreverprice = if(arguments.containsKey("foreverprice")){
            arguments.getString("foreverprice")
        }else{
            ""
        }

        var ids =  arguments.getString("ids")
//        tv_private_title.text= if(TextUtils.equals(ids,"22")){
//             "开通普通会员"
//        }else{
//             "开通白银会员"
//        }

        tv_privateyear_price.text = "${yearprice}"
        tv_privateforever_price.text = "${foreverprice}"

        rg_private.setOnCheckedChangeListener { group, checkedId ->
             if(checkedId==R.id.rb_private_forever){
                 mCheckedType = "1"
             }else if(checkedId==R.id.rb_private_year){
                 mCheckedType = "2"
             }
             tv_wxpay_member.background = ContextCompat.getDrawable(context,R.drawable.shape_4r_f7a)
        }

        iv_privateMembership_close.setOnClickListener {
            dismissAllowingStateLoss()
        }

//        tv_choose_address.setOnClickListener {
//            dialogListener?.onClick(2001,"地区")
//        }

        tv_wxpay_member.setOnClickListener {
            if(mCheckedType!="-1"){
                dialogListener?.onClick(1000, mCheckedType)
            }
        }

        tv_wxpay_member.text = "确定支付"
    }

    fun setAddressd(address:String,currentIndex:Int){
        if(TextUtils.equals(address,"不限地区")){
            tv_wxpay_member.background = ContextCompat.getDrawable(context,R.drawable.shape_4r_80_orange)
            tv_wxpay_member.text = "确定支付"
        }else{
            tv_wxpay_member.background = ContextCompat.getDrawable(context,R.drawable.shape_4r_f7a)
            getAreaNameMemberPriceList(address,currentIndex)
        }
    }


    private fun getAreaNameMemberPriceList(areaName:String,currentIndex:Int){
        isBaseActivity {
            Request.findUserClasses(getLoginToken(),areaName).request(it){ msg, data->
                data?.list?.let {
                    var memberBean = it.get(currentIndex)
                    tv_wxpay_member.text = "确定支付·¥${memberBean.iAndroidPrice}"
                    tv_privatemember_price.text = "${memberBean.iAndroidPrice}"
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