package com.d6.android.app.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import cn.liaox.cachelib.CacheDbManager
import cn.liaox.cachelib.bean.UserBean
import com.d6.android.app.R
import com.d6.android.app.activities.*
import com.d6.android.app.adapters.MyImageAdapter
import com.d6.android.app.adapters.MySquareAdapter
import com.d6.android.app.adapters.UserTagAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.BaseFragment
import com.d6.android.app.dialogs.MineActionDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.extentions.showBlur
import com.d6.android.app.models.*
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.widget.SwipeRefreshRecyclerLayout
import com.google.android.flexbox.FlexboxLayoutManager
import com.gyf.barlibrary.ImmersionBar
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration
import io.reactivex.Flowable
import io.rong.imkit.RongIM
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.fragment_mine_v2.*
import kotlinx.android.synthetic.main.header_mine_layout.view.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.startActivityForResult
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
        UserTagAdapter(mTags)
    }
    private var pageNum = 1
    private val headerView by lazy {
        layoutInflater.inflate(R.layout.header_mine_layout, mSwipeRefreshLayout.mRecyclerView, false)
    }

    private val colorDrawable by lazy {
        ColorDrawable(Color.WHITE).mutate()
    }

    override fun onFirstVisibleToUser() {

        immersionBar
                .fitsSystemWindows(false)
                .statusBarAlpha(0f)
                .titleBar(rl_title)
                .keyboardEnable(true)
                .init()

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
        headerView.rv_tags.layoutManager = FlexboxLayoutManager(context)
        headerView.rv_tags.isNestedScrollingEnabled = false
        headerView.rv_tags.adapter = userTagAdapter
        headerView.rel_add_square.setOnClickListener {
            startActivityForResult<ReleaseNewTrendsActivity>(3)
        }

        headerView.headView.setOnClickListener {
            mData?.let {
                startActivityForResult<MyInfoActivity>(0, "data" to it, "images" to mImages)
            }
        }
        headerView.ll_fans_count.setOnClickListener(View.OnClickListener {
//            startActivity<FansActivity>()
        })

        headerView.ll_follow_count.setOnClickListener(View.OnClickListener {
//            startActivity<FollowActivity>()
        })

        headerView.ll_vistors_count.setOnClickListener(View.OnClickListener {
//            startActivity<VistorsActivity>()
        })

        tv_more.setOnClickListener {
            //            startActivity<SettingActivity>()
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
        tv_msg.setOnClickListener {
            startActivity<MessagesActivity>()
        }
        tv_setting.setOnClickListener {
            startActivityForResult<SettingActivity>(5)
        }
        squareAdapter.setOnItemClickListener { view, position ->
            val square = mSquares[position]
//            (activity as BaseActivity).getTrendDetail(square.id ?: "") {
//                startActivityForResult<TrendDetailActivity>(18, "data" to it)
//            }
            startActivity<SquareTrendDetailActivity>("id" to (square.id?:""))
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

    private fun setTitleBgAlpha(alpha:Int) {
        colorDrawable.alpha = alpha
        rl_title.backgroundDrawable = colorDrawable
        tv_title_nick.alpha = alpha/255f
        if (alpha > 128) {
            tv_msg.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_my_msg_orange, 0)
            tv_setting.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_setting_orange, 0)
            tv_more.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_more_orange, 0)
            immersionBar.statusBarDarkFont(true).init()
        } else {
            tv_msg.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_my_msg, 0)
            tv_setting.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_setting_white, 0)
            tv_more.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_more_white, 0)
            immersionBar.statusBarDarkFont(false).init()
        }
    }

    override fun onResume() {
        super.onResume()
        getUserInfo()
        getUnReadCount()
        getUserFollowAndFansandVistor()
    }
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            immersionBar.init()
        }
    }

    private fun getUnReadCount() {
        RongIM.getInstance().getUnreadCount(object : RongIMClient.ResultCallback<Int>() {
            override fun onSuccess(p0: Int?) {
                p0?.let {
                    if (p0 > 0) {
                        tv_msg_count1.visible()
                    } else {
                        tv_msg_count1.gone()
                        getSysLastOne()
                    }
                }
            }

            override fun onError(p0: RongIMClient.ErrorCode?) {

            }

        }, Conversation.ConversationType.PRIVATE)
    }


    private fun getSysLastOne() {
        val time = SPUtils.instance().getLong(Const.LAST_TIME)
        val userId = SPUtils.instance().getString(Const.User.USER_ID)
        Request.getSystemMessages(userId, 1, time.toString(), pageSize = 1).request(this, false, success = { _, data ->
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                //无数据
                tv_msg_count1.gone()
                getSquareMsg()
            } else {
                if ((data.count ?:0)> 0) {
                    tv_msg_count1.visible()
                } else {
                    getSquareMsg()
                }

            }

        }) { _, _ ->
            getSquareMsg()
        }
    }

    private fun getSquareMsg() {
        val time = SPUtils.instance().getLong(Const.LAST_TIME)
        val userId = SPUtils.instance().getString(Const.User.USER_ID)
        Request.getSquareMessages(userId, 1, time.toString(), pageSize = 1).request(this, false, success = { _, data ->
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                //无数据
                tv_msg_count1.gone()
            } else {
                if ((data.count ?:0)> 0) {
                    tv_msg_count1.visible()
                }
            }
        }) { _, _ ->

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
               updateImages(path)
//                var param: BLBeautifyParam = BLBeautifyParam()//data.imgUrl.replace("file://","")
//                param.index = 0
//                param.type = Const.User.SELECTIMAGE
//                param.images.add(path)
//                startActivityForResult<BLBeautifyImageActivity>(BLBeautifyParam.REQUEST_CODE_BEAUTIFY_IMAGE, BLBeautifyParam.KEY to param);
            } else if (requestCode == 22) {
                onRefresh()
            }else if(requestCode == BLBeautifyParam.REQUEST_CODE_BEAUTIFY_IMAGE&& data != null){
                var param = data.getParcelableExtra<BLBeautifyParam>(BLBeautifyParam.RESULT_KEY);
                updateImages(param.images[param.index])
            }
        }
    }

    private fun getUserInfo() {
        Request.getUserInfo("",userId).request(this, success = { _, data ->
            mSwipeRefreshLayout.isRefreshing = false
            mData = data
            activity?.saveUserInfo(data)
            data?.let {
                val info = UserInfo(data.accountId, data.name, Uri.parse("" + data.picUrl))
                RongIM.getInstance().refreshUserInfoCache(info)
                updateCache(it)
                headerView.iv_bg.showBlur(it.picUrl)
                headerView.headView.setImageURI(it.picUrl)
                headerView.tv_nick.text = it.name
                tv_title_nick.text = it.name
                if(!TextUtils.isEmpty(it.intro)){
                    headerView.tv_signature.text = it.intro
                }else{
                    headerView.tv_signature.text = "一个好的自我介绍更受异性青睐~"
                }

                headerView.tv_sex.isSelected = TextUtils.equals("0", it.sex)
                headerView.tv_sex.text = it.age
//                if (TextUtils.equals("0", it.sex)) {
//                    headerView.tv_vip.invisible()
//                } else {
//                    headerView.tv_vip.visible()
//                }

                if(TextUtils.equals("0",mData!!.screen) || mData!!.screen.isNullOrEmpty()){
                    headerView.tv_auther_sign.visibility = View.VISIBLE
                    headerView.img_auther.visibility = View.GONE
                }else{
                    headerView.tv_auther_sign.visibility = View.GONE
                    headerView.img_auther.visibility = View.VISIBLE
                }
                headerView.tv_vip.text = String.format("%s", it.classesname)

                mTags.clear()
                mTags.add(UserTag("身高:${it.height}", R.drawable.shape_tag_bg_1))
                mTags.add(UserTag("体重:${it.weight}", R.drawable.shape_tag_bg_2))
                if (!it.job.isNullOrEmpty()) {
                    mTags.add(UserTag(it.job ?: "", R.drawable.shape_tag_bg_3))
                }
                if (!it.city.isNullOrEmpty()) {
                    mTags.add(UserTag(it.job ?: "", R.drawable.shape_tag_bg_4))
                }
                if (!it.constellation.isNullOrEmpty()) {
                    mTags.add(UserTag(it.constellation ?: "", R.drawable.shape_tag_bg_5))
                }
                if (!it.hobbit.isNullOrEmpty()) {
                    mTags.add(UserTag(it.hobbit ?: "", R.drawable.shape_tag_bg_6))
                }

                userTagAdapter.notifyDataSetChanged()
                refreshImages(it)
                getTrendData()
            }
        }) { _, _ ->
            mSwipeRefreshLayout.isRefreshing = false
        }
    }

    //关注粉丝访客
    fun getUserFollowAndFansandVistor(){
        Request.getUserFollowAndFansandVistor(userId).request(this,success = {s:String?,data:FollowFansVistor?->
//            toast("$s,${data?.iFansCount},${data?.iFansCountAll},${data?.iUserid}")
            data?.let {
                headerView.tv_fans_count.text = data.iFansCountAll.toString()
                headerView.tv_follow_count.text = data.iFollowCount.toString()
                headerView.tv_vistor_count.text = data.iVistorCountAll.toString()
            }
        })
    }

    private fun refreshImages(userData: UserData) {
        mImages.clear()
        if (!userData.userpics.isNullOrEmpty()) {
            val images = userData.userpics!!.split(",")
            images.forEach {
                mImages.add(AddImage(it))
            }
        }
        mImages.add(AddImage("res:///" + R.mipmap.ic_add_bg, 1))
        myImageAdapter.notifyDataSetChanged()
    }

    private fun getTrendData() {

        Request.getMySquares(userId, 0, pageNum).request(this, success = { _, data ->
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