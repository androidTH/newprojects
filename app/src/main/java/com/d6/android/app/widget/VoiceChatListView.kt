package com.d6.android.app.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.d6.android.app.R
import com.d6.android.app.activities.UserInfoActivity
import com.d6.android.app.adapters.SelfReleaselmageAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.UnKnowInfoDialog
import com.d6.android.app.models.MyAppointment
import com.d6.android.app.models.Square
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.CustomerServiceId
import com.d6.android.app.utils.Const.CustomerServiceWomenId
import kotlinx.android.synthetic.main.view_voicechat_view.view.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.dip
import org.jetbrains.anko.startActivity
import java.net.URLDecoder

/**
 * Created on 2017/12/17.
 */
class VoiceChatListView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    private val mImages = ArrayList<String>()
    private val imageAdapter by lazy {
        SelfReleaselmageAdapter(mImages,1)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_voicechat_view, this, true)
        rv_images.setHasFixedSize(true)
        rv_images.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv_images.adapter = imageAdapter
    }

    fun update(voiceChatData: Square) {
        headView.setImageURI(voiceChatData.picUrl)
        tv_name.text = voiceChatData.name
        tv_voicechat_user_sex.isSelected = TextUtils.equals("0","${voiceChatData.sex}")
        headView.setOnClickListener(OnClickListener {
            val id = voiceChatData.userid
            isBaseActivity {
                if(voiceChatData.iIsAnonymous==1){
                    var mUnknowDialog = UnKnowInfoDialog()
                    mUnknowDialog.arguments = bundleOf("otheruserId" to id.toString())
                    mUnknowDialog.show(it.supportFragmentManager,"unknowDialog")
                }else{
                    it.startActivity<UserInfoActivity>("id" to id.toString())
                }
            }
        })

        if(!voiceChatData.age.toString().isNullOrEmpty()){
            if(voiceChatData.age!=null){
                voiceChatData.age?.let {
                    tv_voicechat_user_sex.text = "${voiceChatData.age}"
                }
            }
        }

        var time  = converToDays(voiceChatData.dEndtime)

        if(time[0]==1){
            tv_time_long.text="倒计时：${time[1]}天"
        }else if(time[0]==2){
            tv_time_long.text="倒计时：${time[1]}小时"
        }else if(time[0]==3){
            tv_time_long.text="倒计时：${time[1]}分钟"
        }else if(time[0]==-1){
            tv_time_long.visibility = View.GONE
        }else{
            tv_time_long.visibility = View.GONE
        }

        if(voiceChatData.iStatus==3){
            tv_send_voicechat.visibility = View.GONE
            iv_voicechat_timeout.visibility = View.VISIBLE
        }else{
            tv_send_voicechat.visibility = View.VISIBLE
            iv_voicechat_timeout.visibility = View.GONE
        }
//        Log.i("voicechat","内容：${voiceChatData.content},状态：${voiceChatData.iStatus}")

        tv_voicechat_type.visibility = View.VISIBLE
        if(voiceChatData.iVoiceConnectType==2){
            tv_voicechat_type.text = "申请者需打赏喜欢 [img src=heart_gray/]，${voiceChatData.iOncePayLovePoint}喜欢/分钟"
        }else if(voiceChatData.iVoiceConnectType==3){
            tv_voicechat_type.text = "申请者将获得喜欢 [img src=heart_gray/]，${voiceChatData.iOncePayLovePoint}喜欢/分钟"
        }else{
            tv_voicechat_type.visibility = View.GONE
            tv_voicechat_type.text = "无需打赏"
        }

        tv_content.text = voiceChatData.content

        if (voiceChatData.imgUrl.isNullOrEmpty()) {
            rv_images.gone()
        } else {
            rv_images.visible()
        }

        mImages.clear()
        val images = voiceChatData.imgUrl?.split(",")
        if (images != null) {
            mImages.addAll(images.toList())
        }
//        Log.i("fff",myAppointment.sSourceAppointPic)
        imageAdapter.notifyDataSetChanged()
        tv_send_voicechat.setOnClickListener {
            mSendVoiceChatClick?.let {
                it.onVoiceChatClick(voiceChatData)
            }
        }

        tv_voicechat_more.setOnClickListener {
            deleteAction?.let {
                it.onDelete(voiceChatData)
            }
        }

        tv_voicechat_vip.backgroundDrawable = getLevelDrawable("${voiceChatData.userclassesid}",context)

        if(TextUtils.equals(CustomerServiceId,"${voiceChatData.userid}")||TextUtils.equals(CustomerServiceWomenId,"${voiceChatData.userid}")){
            iv_voicechat_servicesign.visibility = View.VISIBLE
            img_voicechat_auther.visibility = View.GONE
        }else{
            iv_voicechat_servicesign.visibility = View.GONE
            if(TextUtils.equals("0",voiceChatData!!.screen)|| voiceChatData!!.screen.isNullOrEmpty()){
                img_voicechat_auther.visibility = View.GONE
            }else if(TextUtils.equals("1",voiceChatData!!.screen)){
                img_voicechat_auther.visibility = View.VISIBLE
                img_voicechat_auther.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.video_small)
            }else if(TextUtils.equals("3",voiceChatData!!.screen)){
                img_voicechat_auther.visibility = View.GONE
                img_voicechat_auther.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.renzheng_small)
            }else{
                img_voicechat_auther.visibility = View.GONE
            }
        }

        voiceChatData.iVoiceConnectSignupCount?.let {
            if(it>0){
                tv_voicechat_nums.text = "已有${it}人申请连麦"
            }else{
                tv_voicechat_nums.text = ""
            }
        }
    }

    fun sendVoiceChatListener(action:(voiceChatData: Square)->Unit) {
        mSendVoiceChatClick = object : sendVoiceChatClickListener {
            override fun onVoiceChatClick(voiceChatData: Square) {
                action(voiceChatData)
            }
        }
    }

    fun setDeleteClick(action:(voiceChatData: Square)->Unit){
        this.deleteAction = object :DeleteClick {
            override fun onDelete(voiceChatData: Square) {
                action(voiceChatData)
            }
        }
    }

    private var mSendVoiceChatClick:sendVoiceChatClickListener?=null
    private var deleteAction: DeleteClick?=null

    interface sendVoiceChatClickListener{
        fun onVoiceChatClick(voiceChatData: Square)
    }

    interface DeleteClick{
        fun onDelete(voiceChatData: Square)
    }

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }
}