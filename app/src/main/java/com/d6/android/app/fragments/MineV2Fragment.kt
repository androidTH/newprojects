package com.d6.android.app.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.liaox.cachelib.CacheDbManager
import cn.liaox.cachelib.bean.UserBean
import com.alibaba.fastjson.JSONObject
import com.d6.android.app.R
import com.d6.android.app.activities.*
import com.d6.android.app.adapters.MyImageAdapter
import com.d6.android.app.adapters.MySquareAdapter
import com.d6.android.app.adapters.UserTagAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.BaseFragment
import com.d6.android.app.dialogs.*
import com.d6.android.app.extentions.request
import com.d6.android.app.extentions.showBlur
import com.d6.android.app.models.*
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.widget.CustomToast
import com.d6.android.app.widget.SwipeRefreshRecyclerLayout
import com.google.android.flexbox.FlexboxLayoutManager
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration
import io.reactivex.Flowable
import io.rong.imkit.RongIM
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.fragment_mine_v2.*
import kotlinx.android.synthetic.main.header_mine_layout.view.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.imageURI
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.startActivityForResult
import org.jetbrains.anko.support.v4.toast
import www.morefuntrip.cn.sticker.Bean.BLBeautifyParam

/**
 * 我的
 */
class MineV2Fragment : BaseFragment(), SwipeRefreshRecyclerLayout.OnRefreshListener {
    override fun contentViewId() = R.layout.fragment_mine_v2
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)//35598
    }
    private var mData: UserData? = null

    private val mSquares = ArrayList<Square>()
    private val squareAdapter by lazy {
        MySquareAdapter(mSquares, 0)
    }
    private val mImages = ArrayList<AddImage>()
    private val myImageAdapter by lazy {
        MyImageAdapter(mImages)
    }

    private val mTags = ArrayList<UserTag>()
    private val userTagAdapter by lazy {
        UserTagAdapter(mTags)//UserTagAdapter
    }
    private var pageNum = 1
    private val headerView by lazy {
        layoutInflater.inflate(R.layout.header_mine_layout, mSwipeRefreshLayout.mRecyclerView, false)
    }

    private val colorDrawable by lazy {
        ColorDrawable(Color.WHITE).mutate()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        immersionBar
                .fitsSystemWindows(false)
                .statusBarColor(R.color.trans_parent)
                .statusBarAlpha(0f)
                .titleBar(rl_title)
                .keyboardEnable(true)
                .init()
    }

    override fun onFirstVisibleToUser() {
        mSwipeRefreshLayout.setLayoutManager(LinearLayoutManager(context))
        squareAdapter.setHeaderView(headerView)
        mSwipeRefreshLayout.setAdapter(squareAdapter)
        mSwipeRefreshLayout.setOnRefreshListener(this)

        headerView.rv_my_images.setHasFixedSize(true)
        headerView.rv_my_images.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        headerView.rv_my_images.isNestedScrollingEnabled = false
        headerView.rv_my_images.adapter = myImageAdapter
        headerView.rv_my_images.addItemDecoration(VerticalDividerItemDecoration.Builder(context)
                .colorResId(android.R.color.transparent)
                .size(dip(2))
                .build())

        myImageAdapter.setOnItemClickListener { _, position ->

            val data = mImages[position]
            if (data.type != 1) {
                mData?.let {
                    //广场照片详情页面
                    val urls = mImages.filter { it.type != 1 }.map { it.imgUrl }
                    startActivityForResult<ImagePagerActivity>(22, "data" to it, ImagePagerActivity.URLS to urls, ImagePagerActivity.CURRENT_POSITION to position, "delete" to true)
                }
            } else {
                if (mImages.size >= 9) {
                    toast("最多上传8张图片")
                    return@setOnItemClickListener
                }
                startActivityForResult<SelectPhotoDialog>(8)
            }
        }
        headerView.rv_tags.setHasFixedSize(true)
        headerView.rv_tags.layoutManager = FlexboxLayoutManager(context)// GridLayoutManager(context,2)//FlexboxLayoutManager(context)
        headerView.rv_tags.isNestedScrollingEnabled = false
        headerView.rv_tags.adapter = userTagAdapter
        headerView.rel_add_square.setOnClickListener {
            //发布动态
            startActivityForResult<ReleaseNewTrendsActivity>(3)
        }

        headerView.rl_mypoints_count.setOnClickListener {
            startActivity<MyPointsActivity>("points" to headerView.tv_mypointscount.text)
        }

        headerView.headview.setOnClickListener {
            mData?.let {
                startActivityForResult<MyInfoActivity>(0, "data" to it, "images" to mImages)
            }
        }
        headerView.rl_fans_count.setOnClickListener(View.OnClickListener {
            startActivity<FansActivity>()
        })

        headerView.rl_follow_count.setOnClickListener(View.OnClickListener {
            startActivity<FollowActivity>()
        })

        headerView.rl_vistors_count.setOnClickListener(View.OnClickListener {
            Request.getVistorAuth(userId).request(this, false, success = { msg, data ->
                startActivity<VistorsActivity>()
            }) { code, msg ->
                if (code == 2) {
                    //积分充足
                    if (msg.isNotEmpty()) {
                        val jsonObject = JSONObject.parseObject(msg)
                        var point = jsonObject.getString("iAddPoint")
                        var sAddPointDesc = jsonObject.getString("sAddPointDesc")
                        val dateDialog = VistorPayPointDialog()
                        dateDialog.arguments = bundleOf("point" to point, "pointdesc" to sAddPointDesc, "type" to 0)
                        dateDialog.show((context as BaseActivity).supportFragmentManager, "vistor")
                    }
                } else if (code == 3) {
                    //积分不足
                    if (msg.isNotEmpty()) {
                        val jsonObject = JSONObject.parseObject(msg)
                        var sAddPointDesc = jsonObject.getString("sAddPointDesc")
//                        val dateDialog = OpenDatePointNoEnoughDialog()
//                        dateDialog.arguments= bundleOf("point" to point.toString(),"remainPoint" to remainPoint)
//                        dateDialog.show((context as BaseActivity).supportFragmentManager, "vistors")

                        var openErrorDialog = OpenDateErrorDialog()
                        openErrorDialog.arguments = bundleOf("code" to 1000, "msg" to sAddPointDesc)
                        openErrorDialog.show((context as BaseActivity).supportFragmentManager, "publishfindDateActivity")
                    }
                } else {
                    showToast(msg)
                }
            }
        })


        headerView.tv_auther_sign.setOnClickListener(View.OnClickListener {
            activity.isCheckOnLineAuthUser(this, userId) {
                startActivity<DateAuthSucessActivity>()
            }
        })

        tv_more.setOnClickListener {
            //startActivity<SettingActivity>()
            val mineActionDialog = MineActionDialog()
            mineActionDialog.show(childFragmentManager, "action")
            mineActionDialog.setDialogListener { p, s ->
                if (p == 0) {
                    mData?.let {
                        startActivityForResult<MyInfoActivity>(0, "data" to it, "images" to mImages)
                    }
                } else if (p == 1) {
                    startActivityForResult<SettingActivity>(5)
                }
            }
        }

        tv_setting.setOnClickListener {
            startActivityForResult<SettingActivity>(5)
        }

        squareAdapter.setOnItemClickListener { view, position ->
            val square = mSquares[position]
//            (activity as BaseActivity).getTrendDetail(square.id ?: "") {
//                startActivityForResult<TrendDetailActivity>(18, "data" to it)
//            }
            startActivity<SquareTrendDetailActivity>("id" to (square.id
                    ?: ""), "position" to position)
        }

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

        mImages.add(AddImage("res:///" + myImageAdapter.mRes, 1))
        showDialog()
    }

    private fun setTitleBgAlpha(alpha: Int) {
        colorDrawable.alpha = alpha
        rl_title.backgroundDrawable = colorDrawable
        tv_title_nick.alpha = alpha / 255f
        if (alpha > 128) {
//            tv_msg.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_my_msg_orange, 0)
            tv_setting.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_setting_orange, 0)
            tv_more.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_more_orange, 0)
            immersionBar.statusBarDarkFont(true).init()
        } else {
//            tv_msg.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_my_msg, 0)
            tv_setting.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_setting_white, 0)
            tv_more.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_more_white, 0)
            immersionBar.statusBarDarkFont(false).init()
        }
    }

    override fun onResume() {
        super.onResume()
        getUserInfo()
        getUserFollowAndFansandVistor()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            immersionBar.init()
        }
    }

    private fun updateCache(data: UserData) {
        val userBean = UserBean()
        userBean.userId = data.accountId
        val nick = data.name ?: ""
        userBean.nickName = nick
        val url = data.picUrl ?: ""
        userBean.headUrl = url
        userBean.status = 1//必须设置有效性>0的值
        CacheDbManager.getInstance().updateMemoryCache(data.accountId, userBean)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0) {
                onRefresh()
            } else if (requestCode == 8 && data != null) {//选择图片
                val path = data.getStringExtra(SelectPhotoDialog.PATH)
//               updateImages(path)
                var param: BLBeautifyParam = BLBeautifyParam()//data.imgUrl.replace("file://","")
                param.index = 0
                param.type = Const.User.SELECTIMAGE
                param.images.add(path)
                startActivityForResult<BLBeautifyImageActivity>(BLBeautifyParam.REQUEST_CODE_BEAUTIFY_IMAGE, BLBeautifyParam.KEY to param);
            } else if (requestCode == 22) {
                onRefresh()
            } else if (requestCode == BLBeautifyParam.REQUEST_CODE_BEAUTIFY_IMAGE && data != null) {
                var param = data.getParcelableExtra<BLBeautifyParam>(BLBeautifyParam.RESULT_KEY);
                updateImages(param.images[param.index])
            }
        }
    }

    private fun getUserInfo() {
        Request.getUserInfo("", userId).request(this, success = { _, data ->
            mSwipeRefreshLayout.isRefreshing = false
            mData = data
            activity?.saveUserInfo(data)
            data?.let {
                val info = UserInfo(data.accountId, data.name, Uri.parse("" + data.picUrl))
                RongIM.getInstance().refreshUserInfoCache(info)
                updateCache(it)
                headerView.iv_bg.showBlur(it.picUrl)
                headerView.headview.setImageURI(it.picUrl)
                headerView.tv_nick.text = it.name
                tv_title_nick.text = it.name
                if (!TextUtils.isEmpty(it.intro)) {
                    headerView.tv_signature.text = it.intro
                } else {
                    headerView.tv_signature.text = "一个好的自我介绍更受异性青睐~"
                }

                headerView.tv_sex.isSelected = TextUtils.equals("0", it.sex)
                it.age?.let {
                    if (it.toInt() <= 0) {
                        headerView.tv_sex.text = ""
                    } else {
                        headerView.tv_sex.text = it
                    }
                }

                SPUtils.instance().put(Const.User.USERPOINTS_NUMS, it.iPoint.toString()).apply()
                if (TextUtils.equals("0", mData!!.screen) || mData!!.screen.isNullOrEmpty()) {
                    if (TextUtils.equals("7", mData!!.userclassesid)) {
                        headerView.tv_auther_sign.visibility = View.VISIBLE
                    } else {
                        headerView.tv_auther_sign.visibility = View.GONE
                    }
                    headerView.img_auther.visibility = View.GONE
                } else if (TextUtils.equals("1", mData!!.screen)) {
                    headerView.img_auther.setImageResource(R.mipmap.video_big)
                    headerView.img_auther.visibility = View.VISIBLE
                    headerView.tv_auther_sign.visibility = View.GONE
                } else if (TextUtils.equals("3", mData!!.screen)) {
                    headerView.tv_auther_sign.visibility = View.GONE
                    headerView.img_auther.visibility = View.GONE
                    headerView.img_auther.setImageResource(R.mipmap.renzheng_big)
                } else {
                    headerView.tv_auther_sign.visibility = View.VISIBLE
                    headerView.img_auther.visibility = View.GONE
                }

                //27入门 28中级  29优质
                if (TextUtils.equals(it.userclassesid, "27")) {
                    headerView.tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.gril_cj)
                } else if (TextUtils.equals(it.userclassesid, "28")) {
                    headerView.tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.gril_zj)
                } else if (TextUtils.equals(it.userclassesid, "29")) {
                    headerView.tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.gril_gj)
                } else if (TextUtils.equals(it.userclassesid.toString(), "22")) {
                    headerView.tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_ordinary)
                } else if (TextUtils.equals(it.userclassesid, "23")) {
                    headerView.tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_silver)
                } else if (TextUtils.equals(it.userclassesid, "24")) {
                    headerView.tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_gold)
                } else if (TextUtils.equals(it.userclassesid, "25")) {
                    headerView.tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_zs)
                } else if (TextUtils.equals(it.userclassesid, "26")) {
                    headerView.tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_private)
                }

                mTags.clear()

                if (!it.height.isNullOrEmpty()) {
                    mTags.add(UserTag("身高 ${it.height}", R.mipmap.boy_stature_whiteicon))
                }

                if (!it.weight.isNullOrEmpty()) {
                    mTags.add(UserTag("体重 ${it.weight}", R.mipmap.boy_weight_whiteicon))
                }

                if (!it.constellation.isNullOrEmpty()) {
                    mTags.add(UserTag("星座 ${it.constellation}", R.mipmap.boy_constellation_whiteicon))
                }

                if (!it.city.isNullOrEmpty()) {
                    mTags.add(UserTag("地区 ${it.city}", R.mipmap.boy_area_whiteicon))
                }

                if (!it.job.isNullOrEmpty()) {
                    AppUtils.setUserInfoTvTag(context, "职业 ${it.job}", 0, 2, headerView.tv_job)
                } else {
                    headerView.tv_job.visibility = View.GONE
                }

                if (!it.zuojia.isNullOrEmpty()) {
                    AppUtils.setUserInfoTvTag(context, "座驾${it.zuojia}", 0, 2, headerView.tv_zuojia)
                } else {
                    headerView.tv_zuojia.visibility = View.GONE
                }
                if (!it.hobbit.isNullOrEmpty()) {
                    var mHobbies = it.hobbit?.replace("#", ",")?.split(",")
                    var sb = StringBuffer()
                    sb.append("爱好 ")
                    if (mHobbies != null) {
                        for (str in mHobbies) {
//                            mTags.add(UserTag(str, R.drawable.shape_tag_bg_6))
                            sb.append("${str} ")
                        }
//                        mTags.add(UserTag(sb.toString(), R.mipmap.boy_hobby_whiteicon))
                        AppUtils.setUserInfoTvTag(context, sb.toString(), 0, 2, headerView.tv_aihao)
                    }
                } else {
                    headerView.tv_aihao.visibility = View.GONE
                }

                userTagAdapter.notifyDataSetChanged()
                if (mTags.size == 0) {
                    headerView.rv_tags.visibility = View.GONE
                } else {
                    headerView.rv_tags.visibility = View.VISIBLE
                }
                refreshImages(it)
                getTrendData()
            }
        }) { _, _ ->
            mSwipeRefreshLayout.isRefreshing = false
        }
    }

    //关注粉丝访客
    fun getUserFollowAndFansandVistor() {
        Request.getUserFollowAndFansandVistor(userId).request(this, success = { s: String?, data: FollowFansVistor? ->
            //            toast("$s,${data?.iFansCount},${data?.iFansCountAll},${data?.iUserid}")
            data?.let {
                headerView.tv_fans_count.text = data.iFansCountAll.toString()
                headerView.tv_follow_count.text = data.iFollowCount.toString()
                headerView.tv_vistor_count.text = data.iVistorCountAll.toString()
                if (data.iFansCount!! > 0) {
                    headerView.tv_fcount.text = "+${data.iFansCount.toString()}"
                    headerView.tv_fcount.visibility = View.VISIBLE
                } else {
                    headerView.tv_fcount.visibility = View.GONE
                }

                if (it.iPointNew!!.toInt() > 0) {
                    headerView.tv_mypointscount.visibility = View.VISIBLE
                } else {
                    headerView.tv_mypointscount.visibility = View.GONE
                }

//                if(data.iFollowCount!! > 0){
//                    headerView.tv_fllcount.text = "+${data.iFollowCount.toString()}"
//                    headerView.tv_fllcount.visibility = View.VISIBLE
//                }else {
//                    headerView.tv_fllcount.visibility = View.GONE
//                }

                if (data.iVistorCount!! > 0) {
                    headerView.tv_vcount.text = "+${data.iVistorCount.toString()}"
                    headerView.tv_vcount.visibility = View.VISIBLE
                } else {
                    headerView.tv_vcount.visibility = View.GONE
                }
            }
        })
    }

    private fun refreshImages(userData: UserData) {
        mImages.clear()
        if (!TextUtils.equals("null", userData.userpics)) {
            if (!userData.userpics.isNullOrEmpty()) {
                userData.userpics?.let {
                    val images = it.split(",")
                    images.forEach {
                        mImages.add(AddImage(it))
                    }
                }
            }
        }
        mImages.add(AddImage("res:///" + R.mipmap.ic_add_v2bg, 1))
        myImageAdapter.notifyDataSetChanged()
    }

    private fun getTrendData() {

        Request.getMySquares(userId, userId, 0, pageNum).request(this, success = { _, data ->
            mSwipeRefreshLayout.isRefreshing = false
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
            }
            squareAdapter.notifyDataSetChanged()
        }) { _, _ ->
            mSwipeRefreshLayout.isRefreshing = false
        }
    }

    private fun updateImages(path: String) {
        val userData = mData ?: return
        showDialog()
        Flowable.just(path).flatMap {
            val file = BitmapUtils.compressImageFile(path)
            Request.uploadFile(file)
        }.flatMap {
            if (userData.userpics.isNullOrEmpty()) {
                userData.userpics = it
            } else {
                userData.userpics = userData.userpics + "," + it
            }
            mData = userData
            Request.updateUserInfo(userData)
        }.request(this) { _, _ ->
            refreshImages(userData)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar.destroy()
    }

    override fun onRefresh() {
        pageNum = 1
        getUserInfo()
        getUserFollowAndFansandVistor()
    }

    override fun onLoadMore() {
        pageNum++
        getTrendData()
    }
}