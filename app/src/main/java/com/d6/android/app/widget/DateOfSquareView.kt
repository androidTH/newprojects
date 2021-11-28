package com.d6.android.app.widget

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.d6.android.app.R
import com.d6.android.app.activities.UserInfoActivity
import com.d6.android.app.adapters.SelfReleaselmageAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.UnKnowInfoDialog
import com.d6.android.app.models.Square
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.CustomerServiceId
import com.d6.android.app.utils.Const.CustomerServiceWomenId
import kotlinx.android.synthetic.main.view_dateofsquare_view.view.*
import kotlinx.android.synthetic.main.view_dateofsquare_view.view.headView
import kotlinx.android.synthetic.main.view_dateofsquare_view.view.img_self_auther
import kotlinx.android.synthetic.main.view_dateofsquare_view.view.iv_date_timeout
import kotlinx.android.synthetic.main.view_dateofsquare_view.view.iv_self_servicesign
import kotlinx.android.synthetic.main.view_dateofsquare_view.view.rv_images
import kotlinx.android.synthetic.main.view_dateofsquare_view.view.tv_content
import kotlinx.android.synthetic.main.view_dateofsquare_view.view.tv_date_more
import kotlinx.android.synthetic.main.view_dateofsquare_view.view.tv_date_nums
import kotlinx.android.synthetic.main.view_dateofsquare_view.view.tv_date_user_age
import kotlinx.android.synthetic.main.view_dateofsquare_view.view.tv_date_user_sex
import kotlinx.android.synthetic.main.view_dateofsquare_view.view.tv_datetype_name
import kotlinx.android.synthetic.main.view_dateofsquare_view.view.tv_name
import kotlinx.android.synthetic.main.view_dateofsquare_view.view.tv_self_address
import kotlinx.android.synthetic.main.view_dateofsquare_view.view.tv_self_gift
import kotlinx.android.synthetic.main.view_dateofsquare_view.view.tv_self_money
import kotlinx.android.synthetic.main.view_dateofsquare_view.view.tv_send_date
import kotlinx.android.synthetic.main.view_dateofsquare_view.view.tv_time_long
import kotlinx.android.synthetic.main.view_self_release_view.view.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.dip
import org.jetbrains.anko.startActivity
import java.net.URLDecoder

/**
 * Created on 2017/12/17.
 */
class DateOfSquareView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    private var myDate: Square? = null
    private val mImages = ArrayList<String>()
    private val imageAdapter by lazy {
        SelfReleaselmageAdapter(mImages,1)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_dateofsquare_view, this, true)
        rv_images.setHasFixedSize(true)
//        rv_images.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv_images.adapter = imageAdapter
    }

    fun update(date: Square) {
        this.myDate = myDate
        headView.setImageURI(date.picUrl)
        tv_name.text = date.name
        tv_date_user_sex.isSelected = TextUtils.equals("0",date.sex)

        headView.setOnClickListener(OnClickListener {
            val id = date.userid
            isBaseActivity {
                if(date.iIsAnonymous==1){
                    var mUnknowDialog = UnKnowInfoDialog()
                    mUnknowDialog.arguments = bundleOf("otheruserId" to id.toString())
                    mUnknowDialog.show(it.supportFragmentManager,"unknowDialog")
                }else{
                    it.startActivity<UserInfoActivity>("id" to id.toString())
                }
            }
        })

        var index = 1
        if(!TextUtils.equals("null","${date.iAppointType}")){
            index = date.iAppointType!!.toInt()-1
            tv_datetype_name.text = Const.dateTypes[index]
        }else{
            tv_datetype_name.text = Const.dateTypes[0]
            index = 0
        }

        if(index!= Const.dateTypesBig.size){
            var drawable = ContextCompat.getDrawable(context,Const.dateTypesBig[index])
            drawable?.setBounds(0, 0, drawable?.getMinimumWidth(), drawable?.getMinimumHeight());// 设置边界
            tv_datetype_name.setCompoundDrawablePadding(dip(3))
            tv_datetype_name.setCompoundDrawables(drawable,null,null,null);
        }

        if(date.age.isNullOrEmpty()){
            tv_date_user_age.visibility = View.GONE
        }else{
            if(date.age!=null){
                tv_date_user_age.isSelected = TextUtils.equals("0",date.sex)
                tv_date_user_age.visibility = View.VISIBLE
                tv_date_user_age.text = "${date.age}岁"
            }else{
                tv_date_user_age.visibility = View.GONE
            }
        }

        var time  = converToDays(date.dEndtime)
        tv_time_long.visibility = View.VISIBLE
        tv_send_date.visibility = View.VISIBLE
        iv_date_timeout.visibility = View.GONE
        if(time[0]==1){
            tv_time_long.text="倒计时：${time[1]}天"
        }else if(time[0]==2){
            tv_time_long.text="倒计时：${time[1]}小时"
        }else if(time[0]==3){
            tv_time_long.text="倒计时：${time[1]}分钟"
        }else if(time[0]==-1){
            tv_time_long.visibility = View.GONE
            tv_send_date.visibility = View.GONE
            iv_date_timeout.visibility = View.VISIBLE
        }else{
            tv_time_long.visibility = View.GONE
            tv_send_date.visibility = View.GONE
            iv_date_timeout.visibility = View.VISIBLE
        }

        if(index!=5&&index!=6){
            tv_self_address.visibility = View.VISIBLE
            tv_self_address.text = "约会地点：${date.city}"
            tv_self_money.visibility  = View.VISIBLE
            if(date.iFeeType==1){
                tv_self_money.text = "全包"
            }else if(date.iFeeType==2){
                tv_self_money.text = "AA"
            }else{
                tv_self_money.visibility  = View.GONE
            }
        }else{
            tv_self_address.visibility = View.GONE
            tv_self_money.visibility  = View.GONE
        }

        tv_content.text = date.content

        if (date.imgUrl.isNullOrEmpty()) {
            rv_images.gone()
        } else {
            rv_images.visible()
        }

        mImages.clear()
        val images = date.imgUrl?.split(",")
        if (images != null) {
            mImages.addAll(images.toList())

            val d = rv_images.getItemDecorationAt(0)
            if (d != null) {
                rv_images.removeItemDecoration(d)
            }
            if (mImages.size == 1 || mImages.size == 2 || mImages.size == 4) {
                rv_images.layoutManager = GridLayoutManager(context, 2)
                rv_images.addItemDecoration(RxRecyclerViewDividerTool(dip(2)))//SpacesItemDecoration(dip(4),2)
            } else {
                rv_images.layoutManager = GridLayoutManager(context, 3)
                rv_images.addItemDecoration(RxRecyclerViewDividerTool(dip(2)))//SpacesItemDecoration(dip(4),3)
            }
        }

//        Log.i("fff",myAppointment.sSourceAppointPic)
        imageAdapter.notifyDataSetChanged()
        tv_send_date.setOnClickListener {
            mSendDateClick?.let {
                it.onDateClick(date)
            }
        }
        tv_date_more.setOnClickListener {
            deleteAction?.let {
                it.onDelete(date)
            }
        }

        tv_date_vip.backgroundDrawable = getLevelDrawable("${date.userclassesid}",context)

        if(TextUtils.equals(CustomerServiceId,"${date.userid}")||TextUtils.equals(CustomerServiceWomenId,"${date.userid}")){
            iv_self_servicesign.visibility = View.VISIBLE
            img_self_auther.visibility = View.GONE
        }else{
            iv_self_servicesign.visibility = View.GONE
            if(TextUtils.equals("0",date!!.screen)|| date!!.screen.isNullOrEmpty()){
                img_self_auther.visibility = View.GONE
            }else if(TextUtils.equals("1",date!!.screen)){
                img_self_auther.visibility = View.VISIBLE
                img_self_auther.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.video_small)
            }else if(TextUtils.equals("3",date!!.screen)){
                img_self_auther.visibility = View.GONE
                img_self_auther.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.renzheng_small)
            }else{
                img_self_auther.visibility = View.GONE
            }
        }

        date.iAppointmentSignupCount?.let {
            if(it>0){
                tv_date_nums.text = "累计${it}人邀约"
            }else{
                tv_date_nums.text = ""
            }
        }

        if(date.hasGift&&date.iAppointType!=6){
            tv_self_gift.visibility = View.VISIBLE
            date.giftLoveNum?.let {
                tv_self_gift.text = "邀约礼物·${date.giftName}(${it}颗 [img src=redheart_small/])" //x${myAppointment.giftNum}
            }
        }else{
            tv_self_gift.visibility = View.GONE
        }
    }

    public fun sendDateListener(action:(myAppointment: Square)->Unit) {
        mSendDateClick = object : sendDateClickListener {
            override fun onDateClick(myAppointment: Square) {
                action(myAppointment)
            }
        }
    }

    fun setDeleteClick(action:(myAppointment: Square)->Unit){
        this.deleteAction = object :DeleteClick {
            override fun onDelete(myAppointment: Square) {
                action(myAppointment)
            }
        }
    }

    private var mSendDateClick:sendDateClickListener?=null
    private var deleteAction: DeleteClick?=null

    interface sendDateClickListener{
        fun onDateClick(myAppointment: Square)
    }

    interface DeleteClick{
        fun onDelete(myAppointment: Square)
    }

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }
}