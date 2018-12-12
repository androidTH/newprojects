package com.d6.android.app.activities

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.d6.android.app.R
import com.d6.android.app.adapters.MyImageAdapter
import com.d6.android.app.adapters.MySquareAdapter
import com.d6.android.app.adapters.SelfReleaselmageAdapter
import com.d6.android.app.adapters.UserTagAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.*
import com.d6.android.app.extentions.request
import com.d6.android.app.extentions.showBlur
import com.d6.android.app.models.*
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.AppUtils.Companion.context
import com.d6.android.app.widget.CustomToast
import com.d6.android.app.widget.SwipeRefreshRecyclerLayout
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.gson.JsonObject
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration
import io.rong.imkit.RongIM
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_user_info_v2.*
import kotlinx.android.synthetic.main.header_user_info_layout.view.*
import kotlinx.android.synthetic.main.layout_userinfo_date.view.*
import org.jetbrains.anko.*


/**
 *
 */
class UserInfoActivity : BaseActivity(), SwipeRefreshRecyclerLayout.OnRefreshListener {
    private val id by lazy {
        intent.getStringExtra("id")
    }
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private var mData: UserData? = null

    private var pageNum = 1
    private val headerView by lazy {
        layoutInflater.inflate(R.layout.header_user_info_layout, mSwipeRefreshLayout.mRecyclerView, false)
    }
    private val mSquares = ArrayList<Square>()
    private val squareAdapter by lazy {
        MySquareAdapter(mSquares, 0)
    }
    private val mImages = ArrayList<AddImage>()

    private val myImageAdapter by lazy {
        MyImageAdapter(mImages)
    }

    private val mDateImages = ArrayList<String>()
    private val mDateimageAdapter by lazy {
        SelfReleaselmageAdapter(mDateImages,1)
    }

    private val mTags = ArrayList<UserTag>()
    private val userTagAdapter by lazy {
        UserTagAdapter(mTags)
    }
    private val colorDrawable by lazy {
        ColorDrawable(Color.WHITE).mutate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info_v2)

        immersionBar
                .fitsSystemWindows(false)
                .statusBarColor(R.color.trans_parent)
                .titleBar(rl_title)
                .statusBarDarkFont(false)
                .keyboardEnable(true)
                .init()

        tv_back.setOnClickListener {
            finish()
        }

        mSwipeRefreshLayout.setLayoutManager(LinearLayoutManager(this))
        squareAdapter.setHeaderView(headerView)
        mSwipeRefreshLayout.setAdapter(squareAdapter)
        mSwipeRefreshLayout.setOnRefreshListener(this)

        headerView.rv_my_images.setHasFixedSize(true)
        headerView.rv_my_images.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        headerView.rv_my_images.isNestedScrollingEnabled = false
        headerView.rv_my_images.adapter = myImageAdapter
        headerView.rv_my_images.addItemDecoration(VerticalDividerItemDecoration.Builder(this)
                .colorResId(android.R.color.transparent)
                .size(dip(8))
                .build())

        tv_like.setOnClickListener(View.OnClickListener {
            if(mData?.iIsFollow != null){
                if(mData?.iIsFollow == 0){//mData?.iIsFollow
                    addFollow()
                }else{
                    delFollow()
                }
            }
        })
        myImageAdapter.setOnItemClickListener { _, position ->
            val data = mImages[position]
            if (data.type != 1) {
                val urls = mImages.filter { it.type !=1  }.map { it.imgUrl }
                startActivityForResult<ImagePagerActivity>(22,ImagePagerActivity.URLS to urls, ImagePagerActivity.CURRENT_POSITION to position)
            }
        }
        headerView.rv_tags.setHasFixedSize(true)
        headerView.rv_tags.layoutManager = GridLayoutManager(this, 2)//FlexboxLayoutManager(this)
        headerView.rv_tags.isNestedScrollingEnabled = false
        headerView.rv_tags.adapter = userTagAdapter

        mSwipeRefreshLayout.mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (recyclerView?.layoutManager is LinearLayoutManager) {
                    val l = (recyclerView.layoutManager as LinearLayoutManager)
                    val f = l.findFirstVisibleItemPosition()
                    if (f == 0) {
                        val view = l.findViewByPosition(0)
                        val h = view.top
                        sysErr("----------onScrolled-----$h-")
                        val alpha = if (-h > 255) {
                            255
                        } else {
                            -h
                        }
                        setTitleBgAlpha(alpha)
                    }
                }
            }
        })

        tv_siliao.setOnClickListener {
//            isNoAuthToChat("5") {
                mData?.let {
                    val name = it.name ?: ""
//                        checkChatCount(id) {
                            showDatePayPointDialog(name)
//                    }
                }
//            }
        }

        tv_more.setOnClickListener {
            val userActionDialog = UserActionDialog()
            userActionDialog.setDialogListener { p, s ->
                if (p == 0) {//举报
                    report()
                } else if (p == 1) {
                    addBlackList()
                }
            }
            userActionDialog.show(supportFragmentManager, "user")
        }

        squareAdapter.setOnItemClickListener { view, position ->
            val square = mSquares[position]
//            getTrendDetail(square.id?:""){
//                startActivityForResult<TrendDetailActivity>(18,"data" to it)
//            }
            startActivity<SquareTrendDetailActivity>("id" to (square.id?:""),"position" to position)
        }

        dialog()
        getUserInfo()
        addVistor()
        getUserFollowAndFansandVistor()
    }

    private fun setTitleBgAlpha(alpha:Int) {
        colorDrawable.alpha = alpha
        rl_title.backgroundDrawable = colorDrawable
        tv_title_nick.alpha = alpha/255f
        if (alpha > 128) {
            tv_msg.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_chat_orange, 0)
            tv_more.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_more_orange, 0)
            immersionBar.statusBarDarkFont(true).init()
        } else {
            tv_msg.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_chat_white1, 0)
            tv_more.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_more_white, 0)
            immersionBar.statusBarDarkFont(false).init()
        }
    }

    /**
     * 展示约会信息
     */
    private fun setDateInfo(myAppointment:MyAppointment?){
        headerView.tv_datetype_name.text = Const.dateTypes[myAppointment?.iAppointType!!.toInt()-1]
        if(myAppointment.iAppointType!!.toInt() == Const.dateTypesImg.size){
            var drawable = ContextCompat.getDrawable(context,R.mipmap.invitation_nolimit_small)
            headerView.tv_datetype_name.setCompoundDrawables(drawable,null,null,null)
            headerView.tv_datetype_name.setCompoundDrawablePadding(dip(3))
        }else{
            var drawable = ContextCompat.getDrawable(context,Const.dateTypesImg[myAppointment?.iAppointType!!.toInt()-1])
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// 设置边界
            headerView.tv_datetype_name.setCompoundDrawablePadding(dip(3));
            headerView.tv_datetype_name.setCompoundDrawables(drawable,null,null,null);
        }

        var sb = StringBuffer()
        if(!myAppointment.iAge.toString().isNullOrEmpty()){
            if(myAppointment.iAge!=null){
                sb.append("${myAppointment.iAge}岁")
            }
        }

        if(!myAppointment.iHeight.toString().isNullOrEmpty()){
            if(myAppointment.iHeight!!.toInt() > 0 ){
                sb.append("·${myAppointment.iHeight}cm")
            }
        }

        if(!myAppointment.iWeight.toString().isNullOrEmpty()){
            if(!myAppointment.iWeight.toString().equals("0")){
                sb.append("·${myAppointment.iWeight}kg")
            }
        }

        if(!sb.toString().isNullOrEmpty()){
            headerView.tv_sub_title.text = sb.toString()
            headerView.tv_sub_title.visibility = View.VISIBLE
        }else{
            headerView.tv_sub_title.visibility = View.GONE
        }

        var time = converTime(myAppointment.dEndtime)
        headerView.tv_time_long.text="倒计时:${time}"

        headerView.tv_self_address.text = myAppointment.sPlace

        headerView.tv_content.text = myAppointment.sDesc

        if (myAppointment.sAppointPic.isNullOrEmpty()) {
            headerView.rv_images.gone()
        } else {
            headerView.rv_images.visible()
        }

        mDateImages.clear()
        val images = myAppointment.sAppointPic?.split(",")
        if (images != null) {
            mDateImages.addAll(images.toList())
        }

        headerView.rv_images.setHasFixedSize(true)
        headerView.rv_images.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        headerView.rv_images.adapter = mDateimageAdapter

        headerView.tv_send_date.setOnClickListener {
            signUpDate(myAppointment)
        }
    }

    private fun getUserInfo() {

        Request.getUserInfo(userId,id).request(this, success = { _, data ->
            mSwipeRefreshLayout.isRefreshing = false
            this.mData = data
            data?.let {
                val info = UserInfo(data.accountId, data.name, Uri.parse("" + data.picUrl))
                RongIM.getInstance().refreshUserInfoCache(info)
                tv_title_nick.text = it.name
                headerView.iv_bg.showBlur(it.picUrl)
                headerView.headView.setImageURI(it.picUrl)
                headerView.tv_nick.text = it.name
                if(!TextUtils.isEmpty(it.intro)){
                    headerView.tv_signature.text = it.intro
                }else{
                    headerView.tv_signature.text = "本宝宝还没想到好的自我介绍~"
                }

                headerView.tv_sex.isSelected = TextUtils.equals("0", it.sex)
                headerView.tv_sex.text = it.age
                if (TextUtils.equals("0", it.sex)) {
                    headerView.tv_vip.invisible()
                } else {
                    headerView.tv_vip.visible()
                }
                headerView.tv_vip.text = String.format("%s", it.classesname)
                mTags.clear()
                if(!it.height.isNullOrEmpty()){
                    mTags.add(UserTag("身高 ${it.height}", R.mipmap.boy_stature_whiteicon))
                }
                if(!it.weight.isNullOrEmpty()){
                    mTags.add(UserTag("体重 ${it.weight}",R.mipmap.boy_weight_whiteicon))
                }

                if (!it.constellation.isNullOrEmpty()) {
                    mTags.add(UserTag("星座 ${it.constellation}", R.mipmap.boy_constellation_whiteicon))
                }

                if (!it.city.isNullOrEmpty()) {
                    mTags.add(UserTag("地区 ${it.city}", R.mipmap.boy_area_whiteicon))
                }

                userTagAdapter.notifyDataSetChanged()

                if (!it.job.isNullOrEmpty()) {
                    AppUtils.setUserInfoTvTag(this,"职业 ${it.job}",0,2,headerView.tv_job)
                }

//                AppUtils.setUserInfoTvTag(this,"座驾 Testla ModelX 迈巴赫",0,2,headerView.tv_zuojia)

                if (!it.hobbit.isNullOrEmpty()) {
                    var mHobbies = it.hobbit?.replace("#",",")?.split(",")
                    var sb = StringBuffer()
                    sb.append("爱好 ")
                    if (mHobbies != null) {
                        for(str in mHobbies){
//                            mTags.add(UserTag(str, R.drawable.shape_tag_bg_6))
                            sb.append("${str} ")
                        }
                        AppUtils.setUserInfoTvTag(this,sb.toString(),0,2,headerView.tv_aihao)
                    }
//                    mTags.add(UserTag(it.hobbit ?: "", R.drawable.shape_tag_bg_6))
                }

                refreshImages(it)

                squareAdapter.setUserInfo(mData!!)
                getTrendData()
//                headView.setImageURI(it.picUrl)
//                tv_name.text = it.name
//                tv_name.isSelected = TextUtils.equals("0",it.sex)
//                tv_signature.text = it.signature
//                tv_age.text = it.age
//                tv_height.text = it.height
//                tv_weight.text = it.weight
//                tv_job.text = it.job
//
//                tv_city.text = it.city
//                if (it.city.isNullOrEmpty()) {
//                    tv_city.invisible()
//                } else {
//                    tv_city.visible()
//                }
//                tv_hobbit.text = it.hobbit
//                if (it.hobbit.isNullOrEmpty()) {
//                    tv_hobbit.invisible()
//                } else {
//                    tv_hobbit.visible()
//                }
//                tv_constellation.text = it.constellation
//                if (it.constellation.isNullOrEmpty()) {
//                    tv_constellation.invisible()
//                } else {
//                    tv_constellation.visible()
//                }
//
//                tv5.text = if (TextUtils.equals(it.sex,"1")) "自我介绍" else "要求"
//                tv_content.text = it.intro
//                if (it.intro.isNullOrEmpty()) {
//                    tv_content.invisible()
//                } else {
//                    tv_content.visible()
//                }

                if(it.appointment!=null){
                    headerView.date_headView.setImageURI(it.picUrl)
                    headerView.tv_name.text =it.name
                    headerView.tv_name.isSelected =TextUtils.equals("0", it.sex)
                    setDateInfo(it.appointment)
                }else{
                    headerView.rl_userinfo_date.visibility = View.GONE
                }

                if(data.iIsFollow !=null){
                    if(data.iIsFollow==1){
//                        headerView.iv_isfollow.imageResource = R.mipmap.usercenter_liked_button

                        tv_like.setCompoundDrawables(null,null,null,null);
                        tv_like.text = resources.getString(R.string.string_liked)
                        tv_like.backgroundResource = R.drawable.shape_20r_white
                        tv_like.textColor = ContextCompat.getColor(context,R.color.color_666666)

                        tv_like.setPadding(resources.getDimensionPixelSize(R.dimen.margin_20),resources.getDimensionPixelSize(R.dimen.margin_10),resources.getDimensionPixelSize(R.dimen.margin_20),resources.getDimensionPixelSize(R.dimen.margin_10))
                        tv_siliao.setPadding(resources.getDimensionPixelSize(R.dimen.padding_75),resources.getDimensionPixelSize(R.dimen.margin_10),resources.getDimensionPixelSize(R.dimen.padding_75),resources.getDimensionPixelSize(R.dimen.margin_10))
                    }else{
                        tv_like.text= resources.getString(R.string.string_like)
                        tv_like.backgroundResource = R.drawable.shape_20r_ff6
                        tv_like.textColor = ContextCompat.getColor(context,R.color.white)
//                        headerView.iv_isfollow.imageResource = R.mipmap.usercenter_like_button
                    }
                }
            }
        }) { _, _ -> mSwipeRefreshLayout.isRefreshing = false }
    }

    private fun refreshImages(userData: UserData) {
        mImages.clear()
        if (!userData.userpics.isNullOrEmpty()) {
            val images = userData.userpics!!.split(",")
            images.forEach {
                mImages.add(AddImage(it))
            }
        }
        myImageAdapter.notifyDataSetChanged()
    }

    private fun getTrendData() {
        Request.getMySquares(userId,id, 0, pageNum).request(this, success = { _, data ->
            mSwipeRefreshLayout.isRefreshing = false//15717
            if (pageNum == 1) {
                mSquares.clear()
            }
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                if (pageNum > 1) {
                    mSwipeRefreshLayout.setLoadMoreText("没有更多了")
                    pageNum--
                } else {
                    mSwipeRefreshLayout.setLoadMoreText("暂无数据")
                }
            } else {
                mSquares.addAll(data.list.results)
                squareAdapter.notifyDataSetChanged()
            }
        }) { _, _ ->
            mSwipeRefreshLayout.isRefreshing = false
        }
    }

    private fun report() {
        startActivity<ReportActivity>("id" to id, "tiptype" to "1")
    }

    private fun addBlackList() {
        dialog()
        Request.addBlackList(userId,id).request(this){_,_->
            toast("已加入黑名单")
        }
    }

    //关注粉丝访客
    fun getUserFollowAndFansandVistor(){
        Request.getUserFollowAndFansandVistor(id).request(this,success = {s:String?,data: FollowFansVistor?->
            //            toast("$s,${data?.iFansCount},${data?.iFansCountAll},${data?.iUserid}")
            data?.let {
                headerView.tv_user_fans_count.text = data.iFansCountAll.toString()
                headerView.tv_user_follow_count.text = data.iFollowCount.toString()
//                headerView.tv_user_vistor_count.text = data.iVistorCountAll.toString()
            }
        })
    }

    private fun addFollow(){
        dialog()//35578
        Request.getAddFollow(userId, id).request(this){ s: String?, jsonObject: JsonObject? ->
//            toast("$s,$jsonObject")
//            headerView.iv_isfollow.imageResource = R.mipmap.usercenter_liked_button

            tv_like.setCompoundDrawables(null,null,null,null);

            tv_like.text = resources.getString(R.string.string_liked)
            tv_like.backgroundResource = R.drawable.shape_20r_white
            tv_like.textColor = ContextCompat.getColor(context,R.color.color_666666)

            tv_like.setPadding(resources.getDimensionPixelSize(R.dimen.margin_20),resources.getDimensionPixelSize(R.dimen.margin_10),resources.getDimensionPixelSize(R.dimen.margin_20),resources.getDimensionPixelSize(R.dimen.margin_10))
            tv_siliao.setPadding(resources.getDimensionPixelSize(R.dimen.padding_75),resources.getDimensionPixelSize(R.dimen.margin_10),resources.getDimensionPixelSize(R.dimen.padding_75),resources.getDimensionPixelSize(R.dimen.margin_10))

            mData?.iIsFollow = 1
            showTips(jsonObject,"","")
        }
    }

    private fun delFollow(){
        dialog()
        Request.getDelFollow(userId, id).request(this){ s: String?, jsonObject: JsonObject? ->
//            data.optDouble("wanshanziliao")
//            toast("$s,$jsonObject")
//            headerView.iv_isfollow.imageResource = R.mipmap.usercenter_like_button

            var drawable = ContextCompat.getDrawable(context,R.mipmap.icon_like_button)
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//这句一定要加
            tv_like.setCompoundDrawables(drawable,null,null,null);

            tv_like.text= resources.getString(R.string.string_like)
            tv_like.backgroundResource = R.drawable.shape_20r_ff6
            tv_like.textColor = ContextCompat.getColor(context,R.color.white)


            tv_like.setPadding(resources.getDimensionPixelSize(R.dimen.padding_30),resources.getDimensionPixelSize(R.dimen.margin_10),resources.getDimensionPixelSize(R.dimen.padding_30),resources.getDimensionPixelSize(R.dimen.margin_10))
            tv_siliao.setPadding(resources.getDimensionPixelSize(R.dimen.padding_30),resources.getDimensionPixelSize(R.dimen.margin_10),resources.getDimensionPixelSize(R.dimen.padding_30),resources.getDimensionPixelSize(R.dimen.margin_10))
            mData?.iIsFollow = 0
        }
    }

    //添加访客
    private fun addVistor(){
        Request.getAddVistor(id,userId).request(this){s: String?, jsonObject: JsonObject? ->
        }
    }

    private fun showDatePayPointDialog(name:String){
        Request.doTalkJustify(userId, id).request(this,false,success = {msg,data->
            if(data!=null){
                var code = data!!.optInt("code")
                if(code == 1){
                    var point = data!!.optString("iTalkPoint")
                    var remainPoint = data!!.optString("iRemainPoint")
                    if(point.toInt() > remainPoint.toInt()){
                        val dateDialog = OpenDatePointNoEnoughDialog()
                        var point = data!!.optString("iTalkPoint")
                        var remainPoint = data!!.optString("iRemainPoint")
                        dateDialog.arguments= bundleOf("point" to point,"remainPoint" to remainPoint)
                        dateDialog.show(supportFragmentManager, "d")
                    }else{
                        val dateDialog = OpenDatePayPointDialog()
                        dateDialog.arguments= bundleOf("point" to point,"remainPoint" to remainPoint,"username" to name,"chatUserId" to id)
                        dateDialog.show(supportFragmentManager, "d")
                    }
                } else if(code == 0){
                    RongIM.getInstance().startConversation(this, Conversation.ConversationType.PRIVATE, id, name)
                } else {
                    val dateDialog = OpenDatePointNoEnoughDialog()
                    var point = data!!.optString("iTalkPoint")
                    var remainPoint = data!!.optString("iRemainPoint")
                    dateDialog.arguments= bundleOf("point" to point,"remainPoint" to remainPoint)
                    dateDialog.show(supportFragmentManager, "d")
                }
            }else{
                RongIM.getInstance().startConversation(this, Conversation.ConversationType.PRIVATE, id, name)
            }
        }) { _, msg ->

        }
    }

    private fun signUpDate(myAppointment:MyAppointment) {
        Request.queryAppointmentPoint(userId).request(this, false, success = { msg, data ->
            val dateDialog = OpenDateDialog()
            dateDialog.arguments = bundleOf("data" to myAppointment, "explain" to data!!)
            dateDialog.show(supportFragmentManager, "d")
        }) { code, msg ->
            if (code == 2) {
                var openErrorDialog = OpenDateErrorDialog()
                openErrorDialog.arguments = bundleOf("code" to code, "msg" to msg)
                openErrorDialog.show(supportFragmentManager, "d")
            }
        }
    }

    override fun onRefresh() {
        pageNum = 1
        getUserInfo()
    }


    override fun showToast(msg: String) {
    }

    override fun onLoadMore() {
        pageNum++
        getTrendData()
    }
}
