package com.d6.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.adapters.AppMemberPriceAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.AppMemberPrice
import com.d6.android.app.models.MemberBean
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.dialog_appmember_price_list.*
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.wrapContent

/**
 * 开通会员
 */
class AppMemberDialog : DialogFragment() {

    var mAppMemberPriceAdapter: AppMemberPriceAdapter?=null
    private var mSelectedPriceIndex:Int = 0

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
            inflater?.inflate(R.layout.dialog_appmember_price_list, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var mMember = if(arguments.containsKey("bean")){
            arguments.getParcelable("bean") as MemberBean
        }else{
            null
        }

        var desc =if(arguments.containsKey("desc")){
            arguments.getString("desc")
        }else{
            ""
        }

        tv_appmember_des.text = desc

        iv_appmember_close.setOnClickListener {
            dismissAllowingStateLoss()
        }

        tv_wxpay_appmember.setOnClickListener {
            dialogListener?.onClick(mSelectedPriceIndex,"${tv_wxpay_price.text.toString()}")
        }

        rv_appmember_price_list.setHasFixedSize(true)
        rv_appmember_price_list.layoutManager = GridLayoutManager(context,3) as RecyclerView.LayoutManager?

        mAppMemberPriceAdapter = AppMemberPriceAdapter(mMember?.let{it.lstPrice})
        rv_appmember_price_list.adapter = mAppMemberPriceAdapter

        mAppMemberPriceAdapter?.setOnItemClickListener() { adapter, view, position ->
            mAppMemberPriceAdapter?.let {
                mSelectedPriceIndex = position
                if (it.selectedIndex == position) {
                    it.selectedIndex = -1
                } else {
                    it.selectedIndex = position
                }
                it.notifyDataSetChanged()

                if(it.selectedIndex!=-1){
                    tv_wxpay_appmember.background = ContextCompat.getDrawable(context,R.drawable.shape_4r_f7a)
                    var price =it.data[position].iAndroidPrice
                    tv_wxpay_appmember.text = "确定支付·¥${price}"
                    tv_wxpay_price.text = "${price}"
                }else{
                    tv_wxpay_appmember.background = ContextCompat.getDrawable(context,R.drawable.shape_4r_80_orange)
                    tv_wxpay_appmember.text = "确定支付"
                }
            }
        }

        mAppMemberPriceAdapter?.let {
            var mAppMemberPrice = it.data[0]
            tv_wxpay_appmember.background = ContextCompat.getDrawable(context, R.drawable.shape_4r_f7a)
            var price = mAppMemberPrice.iAndroidPrice
            tv_wxpay_appmember.text = "确定支付·¥${price}"
            tv_wxpay_price.text = "${price}"
        }
    }

//    private fun getAppMemberPrice(mMember:MemberBean):List<AppMemberPrice>{
//        var mList=ArrayList<AppMemberPrice>()
//        if (mMember != null) {
//            var mAppMemberPrice = AppMemberPrice()
//            mAppMemberPrice.timeLong = "1个月"
//            mAppMemberPrice.iAndroidPrice = mMember.iAndroidPrice
//            mAppMemberPrice.sAndroidPriceDiscount = mMember.sAndroidPriceDiscount
//            mAppMemberPrice.sAndroidPriceDiscountDesc = mMember.sAndroidPriceDiscountDesc
//            mList.add(mAppMemberPrice)
//            var mAppMemberAPrice = AppMemberPrice()
//            mAppMemberAPrice.timeLong = "3个月"
//            mAppMemberAPrice.iAndroidPrice = mMember.iAndroidAPrice
//            mAppMemberAPrice.sAndroidPriceDiscount = mMember.sAndroidAPriceDiscount
//            mAppMemberAPrice.sAndroidPriceDiscountDesc = mMember.sAndroidAPriceDiscountDesc
//            mList.add(mAppMemberAPrice)
//            var mAppMemberYPrice = AppMemberPrice()
//            mAppMemberYPrice.timeLong = "6个月"
//            mAppMemberYPrice.iAndroidPrice = mMember.iAndroidYPrice
//            mAppMemberYPrice.sAndroidPriceDiscount = mMember.sAndroidYPriceDiscount
//            mAppMemberYPrice.sAndroidPriceDiscountDesc = mMember.sAndroidYPriceDiscountDesc
//            mList.add(mAppMemberYPrice)
//        }
//        return mList
//    }

    fun setAddressd(address:String){
//        tv_choose_address.text = address
        getAreaNameMemberPriceList(address)
    }


    private fun getAreaNameMemberPriceList(areaName:String){
        isBaseActivity {
            Request.findUserClasses(getLoginToken(),areaName).request(it){ msg, data->
                data?.list?.let {
                    Log.i("mem","数量${it.size}")
//                    mMemberPriceList = it
                    mAppMemberPriceAdapter?.let {
//                        it.setNewData(mMemberPriceList)
                    }
                }
            }
        }
    }

    private fun getMemberPriceList() {
        Request.findUserClasses(getLoginToken()).request((context as BaseActivity)){msg,data->
            data?.list?.let {
//                mMemberPriceList = it
//                mAppMemberPriceAdapter?.setNewData(mMemberPriceList)
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