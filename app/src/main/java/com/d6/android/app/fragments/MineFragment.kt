package com.d6.android.app.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import cn.liaox.cachelib.CacheDbManager
import cn.liaox.cachelib.bean.UserBean
import com.alibaba.fastjson.JSONObject
import com.d6.android.app.R
import com.d6.android.app.activities.*
import com.d6.android.app.adapters.MyImageAdapter
import com.d6.android.app.adapters.UserTagAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.BaseFragment
import com.d6.android.app.dialogs.OpenDateErrorDialog
import com.d6.android.app.dialogs.VistorPayPointDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.*
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.USERINFO_PERCENT
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration
import io.reactivex.Flowable
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.fragment_myinfo.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.startActivityForResult
import www.morefuntrip.cn.sticker.Bean.BLBeautifyParam

/**
 * 我的
 */
class MineFragment : BaseFragment() {

    override fun contentViewId() = R.layout.fragment_myinfo

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)//35598
    }
    private var mData: UserData? = null

    private val mImages = ArrayList<AddImage>()
    private val mBigSquareImages = ArrayList<AddImage>()
    private val mPicsWall = ArrayList<AddImage>()

    private val myImageAdapter by lazy {
        MyImageAdapter(mImages)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        headview.setOnClickListener {
            mData?.let {
                it.picUrl?.let {
                    var url = it.replace(Const.Pic_Thumbnail_Size_wh200,"")
                    val urls = ArrayList<String>()
                    urls.add(url)
                    startActivityForResult<ImagePagerActivity>(22, ImagePagerActivity.URLS to urls, ImagePagerActivity.CURRENT_POSITION to 0)
                }
            }
        }

        rl_edituserinfo.setOnClickListener {
            mData?.let {
                startActivity<UserInfoActivity>("id" to it.accountId.toString())
//                startActivityForResult<MyInfoActivity>(0, "data" to it, "images" to mPicsWall)
            }
        }

        rl_fans_count.setOnClickListener(View.OnClickListener {
            startActivity<FansActivity>()
        })

        rl_follow_count.setOnClickListener(View.OnClickListener {
            startActivity<FollowActivity>()
        })

        ll_square.setOnClickListener {
            mData?.let {
                startActivity<UserInfoActivity>("id" to it.accountId.toString())
            }
        }

        myImageAdapter.setOnItemClickListener { _, position ->
            mData?.let {
                startActivity<UserInfoActivity>("id" to it.accountId.toString())
//                startActivityForResult<MyInfoActivity>(0, "data" to it, "images" to mPicsWall)
            }
//            val data = mImages[position]
////            if (data.type != 1) {
////                mData?.let {
////                    //广场照片详情页面
////                    val urls = mBigSquareImages.filter { it.type != 1 }.map { it.imgUrl }
////                    startActivityForResult<ImagePagerActivity>(22, "data" to it, ImagePagerActivity.URLS to urls, ImagePagerActivity.CURRENT_POSITION to position, "delete" to false)
////                }
////            }
        }

        rl_vistors_count.setOnClickListener(View.OnClickListener {
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

        rl_setting.setOnClickListener {
            startActivityForResult<SettingActivity>(5)
        }

        rl_blacklist.setOnClickListener {
            startActivity<BlackListActivity>()
//            startActivity<MemberActivity>()
        }

        rl_customerservice.setOnClickListener {
            (context as BaseActivity).pushCustomerMessage((context as BaseActivity), getLocalUserId(),5,"",next = {
                chatService((context as BaseActivity))
            })
        }

        rl_member_center.setOnClickListener {
            (context as BaseActivity).isAuthUser("mine") {
                startActivity<MemberActivity>()
            }
        }

        rl_wallet.setOnClickListener {
            startActivity<MyPointsActivity>()
        }

        iv_whiteclose.setOnClickListener {
            rl_warmuserinfo.visibility = View.GONE
            SPUtils.instance().put(USERINFO_PERCENT,System.currentTimeMillis()).apply()
        }

        tv_edituserinfo.setOnClickListener {
            mData?.let {
                startActivityForResult<MyInfoActivity>(0, "data" to it, "images" to mPicsWall)
            }
        }

        tv_squarewarm.setOnClickListener {
            startActivity<UserInfoActivity>("id" to userId)
        }

        rv_square_imgs.setHasFixedSize(true)
        rv_square_imgs.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv_square_imgs.isNestedScrollingEnabled = false
        rv_square_imgs.adapter = myImageAdapter
        rv_square_imgs.addItemDecoration(VerticalDividerItemDecoration.Builder(context)
                .colorResId(android.R.color.transparent)
                .size(dip(3))
                .build())
    }

    override fun onFirstVisibleToUser() {
        mPicsWall.add(AddImage("res:///" + R.mipmap.ic_add_v2bg, 1))
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
//                onRefresh()
            } else if (requestCode == 8 && data != null) {//选择图片
                val path = data.getStringExtra(SelectPhotoDialog.PATH)
//               updateImages(path)
                var param: BLBeautifyParam = BLBeautifyParam()//data.imgUrl.replace("file://","")
                param.index = 0
                param.type = Const.User.SELECTIMAGE
                param.images.add(path)
                startActivityForResult<BLBeautifyImageActivity>(BLBeautifyParam.REQUEST_CODE_BEAUTIFY_IMAGE, BLBeautifyParam.KEY to param);
            } else if (requestCode == 22) {
//                onRefresh()
            } else if (requestCode == BLBeautifyParam.REQUEST_CODE_BEAUTIFY_IMAGE && data != null) {
                var param = data.getParcelableExtra<BLBeautifyParam>(BLBeautifyParam.RESULT_KEY);
                updateImages(param.images[param.index])
            }
        }
    }

    private fun getUserInfo() {
        Request.getUserInfo("", userId).request(this, success = { _, data ->
            mData = data
            activity?.saveUserInfo(data)
            data?.let {
                val info = UserInfo(data.accountId, data.name, Uri.parse("" + data.picUrl))
                RongIM.getInstance().refreshUserInfoCache(info)
                updateCache(it)

                headview.setImageURI(it.picUrl)
                sv_service.setImageURI(it.sServicePicUrl)
                tv_nick.text = it.name
                if (!TextUtils.isEmpty(it.intro)) {
                    tv_signature.text = it.intro
                } else {
                    tv_signature.text = getString(R.string.string_info)
                }

                tv_sex.isSelected = TextUtils.equals("0", it.sex)
                it.age?.let {
                    if (it.toInt() <= 0) {
                        tv_sex.text = ""
                    } else {
                        tv_sex.text = it
                    }
                }

                SPUtils.instance().put(Const.User.USERPOINTS_NUMS, it.iPoint.toString()).apply()
                AppUtils.setUserWallet( context,"积分 ${it.iPoint.toString()}",0 ,2 ,tv_points)
                AppUtils.setUserWallet( context,"小红花 ${it.iFlowerCount.toString()}",0 ,3 ,tv_redflowernums)

                if(TextUtils.equals(userId, Const.CustomerServiceId)||TextUtils.equals(userId, Const.CustomerServiceWomenId)){
                    img_auther.visibility = View.GONE
                    img_mine_official.visibility = View.VISIBLE
                }else{
                    if (TextUtils.equals("0", mData!!.screen) || mData!!.screen.isNullOrEmpty()) {
                        img_auther.visibility = View.GONE
                    } else if (TextUtils.equals("1", mData!!.screen)) {
                        img_auther.setImageResource(R.mipmap.video_small)
                        img_auther.visibility = View.VISIBLE
                    } else if (TextUtils.equals("3", mData!!.screen)) {
                        img_auther.visibility = View.GONE
                        img_auther.setImageResource(R.mipmap.renzheng_small)
                    } else {
                        img_auther.visibility = View.GONE
                    }
                }

                if(TextUtils.equals("0",it.isValid)){
                    img_auther.visibility = View.VISIBLE
                    img_auther.setImageResource(R.mipmap.official_forbidden_icon)
                }

                var drawable: Drawable? = null
                //27入门 28中级  29优质
                tv_menber_center.text = ""
                if (TextUtils.equals(it.userclassesid, "27")) {
                    tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.gril_cj)
                    drawable = ContextCompat.getDrawable(context, R.mipmap.gril_cj)
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())//这句一定要加
                } else if (TextUtils.equals(it.userclassesid, "28")) {
                    tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.gril_zj)
                    drawable = ContextCompat.getDrawable(context, R.mipmap.gril_zj)
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())//这句一定要加
                } else if (TextUtils.equals(it.userclassesid, "29")) {
                    tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.gril_gj)
                    drawable = ContextCompat.getDrawable(context, R.mipmap.gril_gj)
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())
                } else if (TextUtils.equals(it.userclassesid.toString(), "22")) {
                    tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_ordinary)
                    drawable = ContextCompat.getDrawable(context, R.mipmap.vip_ordinary)
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())
                } else if (TextUtils.equals(it.userclassesid, "23")) {
                    tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_silver)
                    drawable = ContextCompat.getDrawable(context, R.mipmap.vip_silver)
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())
                } else if (TextUtils.equals(it.userclassesid, "24")) {
                    tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_gold)
                    drawable = ContextCompat.getDrawable(context, R.mipmap.vip_gold)
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())
                } else if (TextUtils.equals(it.userclassesid, "25")) {
                    tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_zs)
                    drawable = ContextCompat.getDrawable(context, R.mipmap.vip_zs)
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())
                } else if (TextUtils.equals(it.userclassesid, "26")) {
                    tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_private)
                    drawable = ContextCompat.getDrawable(context, R.mipmap.vip_private)
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())
                }else if(TextUtils.equals(it.userclassesid,"7")){
                    tv_vip.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.youke_icon)
                    drawable = null
                    tv_menber_center.text = getString(R.string.string_vip_tq)
                }else if(TextUtils.equals(it.userclassesid,"30")){
                    tv_vip.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.ruqun_icon)
                    drawable = ContextCompat.getDrawable(context, R.mipmap.youke_icon)
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())
                } else{
                    tv_vip.visibility = View.GONE
                    drawable = null
                    tv_menber_center.text = getString(R.string.string_vip_tq)
                }

                tv_menber_center.setCompoundDrawables(drawable, null, null, null)


                if(it.iDatacompletion < 60){
                    if(SPUtils.instance().getLong(USERINFO_PERCENT,System.currentTimeMillis())!=System.currentTimeMillis()){
                        if(getSevenDays(SPUtils.instance().getLong(USERINFO_PERCENT, System.currentTimeMillis()))){
                            rl_warmuserinfo.visibility = View.VISIBLE
                        }else{
                            rl_warmuserinfo.visibility = View.GONE
                        }
                    }else{
                        rl_warmuserinfo.visibility = View.VISIBLE
                    }
                }else{
                    rl_warmuserinfo.visibility = View.GONE
                }

                if (!TextUtils.equals("null", it.sSquarePicList.toString())) {
                    rv_square_imgs.visibility = View.VISIBLE
                    addSquareImages(it)
                }else{
                    rv_square_imgs.visibility = View.GONE
                }

                if(it.iSquareCount!!>0){
                    view_mine_square.visibility = View.VISIBLE
                    ll_square.visibility = View.VISIBLE
                    tv_squarewarm.text = "${it.iSquareCount}条动态"
                }else{
                    view_mine_square.visibility = View.GONE
                    ll_square.visibility = View.GONE
                    tv_squarewarm.text = getString(R.string.string_nosquare)
                }

//                setPicsWall(it)
            }
        }) { _, _ ->
//            mSwipeRefreshLayout.isRefreshing = false
        }
    }

    //关注粉丝访客
    fun getUserFollowAndFansandVistor() {
        Request.getUserFollowAndFansandVistor(userId).request(this, success = { s: String?, data: FollowFansVistor? ->
            //            toast("$s,${data?.iFansCount},${data?.iFansCountAll},${data?.iUserid}")
            data?.let {
                tv_fans_count.text = data.iFansCountAll.toString()
                tv_follow_count.text = data.iFollowCount.toString()
                tv_vistor_count.text = data.iVistorCountAll.toString()
                if (data.iFansCount!! > 0) {
                    tv_fcount.text = "+${data.iFansCount.toString()}"
                    tv_fcount.visibility = View.VISIBLE
                } else {
                    tv_fcount.visibility = View.GONE
                }

                if (it.iPointNew!!.toInt() > 0) {
                    iv_reddot.visibility = View.VISIBLE
                } else {
                    iv_reddot.visibility = View.GONE
                }

//                if(data.iFollowCount!! > 0){
//                    headerView.tv_fllcount.text = "+${data.iFollowCount.toString()}"
//                    headerView.tv_fllcount.visibility = View.VISIBLE
//                }else {
//                    headerView.tv_fllcount.visibility = View.GONE
//                }

                if (data.iVistorCount!! > 0) {
                    tv_vcount.text = "+${data.iVistorCount.toString()}"
                    tv_vcount.visibility = View.VISIBLE
                } else {
                    tv_vcount.visibility = View.GONE
                }
            }
        })
    }

    /**
     * 动态图片
     */
    private fun addSquareImages(userData: UserData) {
        mImages.clear()
        mBigSquareImages.clear()
        if (!TextUtils.equals("null", userData.sSquarePicList)) {
            if (!userData.sSquarePicList.isNullOrEmpty()) {
                userData.sSquarePicList?.let {
                    val images = it.split(",")
                    images.forEach {
                        mImages.add(AddImage(it))
                    }
                }

                userData.sSourceSquarePicList?.let {
                    var SourceSquares = it.split(",")
                    SourceSquares.forEach{
                        mBigSquareImages.add(AddImage(it))
                    }
                }
            }
        }else{
            rv_square_imgs.visibility = View.GONE
        }
        myImageAdapter.notifyDataSetChanged()
    }

    /**
     * 照片墙
     */
    private fun setPicsWall(userData: UserData) {
        mPicsWall.clear()
        if (!TextUtils.equals("null", userData.userpics)) {
            if (!userData.userpics.isNullOrEmpty()) {
                userData.userpics?.let {
                    val images = it.split(",")
                    images.forEach {
                        mPicsWall.add(AddImage(it))
                    }
                }
            }
        }
        mPicsWall.add(AddImage("res:///" + R.mipmap.ic_add_v2bg, 1))
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
            setPicsWall(userData)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar.destroy()
    }
}