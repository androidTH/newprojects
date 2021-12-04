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
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.BaseFragment
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
import org.jetbrains.anko.support.v4.toast
import www.morefuntrip.cn.sticker.Bean.BLBeautifyParam


/**
 * 我的
 */
class MineFragment : BaseFragment() {

    override fun contentViewId() = R.layout.fragment_myinfo

    private var mData: UserData? = null

    private val mImages = ArrayList<AddImage>()
    private val mBigSquareImages = ArrayList<AddImage>()
    private val mPicsWall = ArrayList<AddImage>()
    private var isShowWarm:Boolean = false

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
            }
        }

        rl_vistors_count.setOnClickListener(View.OnClickListener {
            startActivity<VistorsActivity>("count" to "${tv_vistor_count.text}")
//            Request.getVistorAuth(getLocalUserId()).request(this, false, success = { msg, data ->
//                startActivity<VistorsActivity>("count" to "${tv_vistor_count.text}")
//            }) { code, msg ->
//                if (code == 2) {
//                    //积分充足
//                    if (msg.isNotEmpty()) {
//                        val jsonObject = JSONObject.parseObject(msg)
//                        var point = jsonObject.getString("iAddPoint")
//                        var sAddPointDesc = jsonObject.getString("sAddPointDesc")
//                        val dateDialog = VistorPayPointDialog()
//                        dateDialog.arguments = bundleOf("point" to point, "pointdesc" to sAddPointDesc, "type" to 0)
//                        dateDialog.show((context as BaseActivity).supportFragmentManager, "vistor")
//                    }
//                } else if (code == 3) {
//                    //积分不足
//                    if (msg.isNotEmpty()) {
//                        val jsonObject = JSONObject.parseObject(msg)
//                        var sAddPointDesc = jsonObject.getString("sAddPointDesc")
////                        val dateDialog = OpenDatePointNoEnoughDialog()
////                        dateDialog.arguments= bundleOf("point" to point.toString(),"remainPoint" to remainPoint)
////                        dateDialog.show((context as BaseActivity).supportFragmentManager, "vistors")
//
//                        var openErrorDialog = OpenDateErrorDialog()
//                        openErrorDialog.arguments = bundleOf("code" to 1000, "msg" to sAddPointDesc)
//                        openErrorDialog.show((context as BaseActivity).supportFragmentManager, "publishfindDateActivity")
//                    }
//                } else if(code==0){
//                    var sex = SPUtils.instance().getString(Const.User.USER_SEX)
//                    if(TextUtils.equals("1",sex)){
//                        startActivity<AuthMenStateActivity>()
//                    }else{
//                        startActivity<AuthWomenStateActivity>()
//                    }
//                } else {
//                    showToast("$msg")
//                }
//            }
        })

        rl_setting.setOnClickListener {
            startActivityForResult<SettingActivity>(5)
        }

        rl_yinsi.setOnClickListener {
            startActivity<PrivacySettingActivity>()
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
            iv_reddot.visibility = View.GONE
        }

        ll_mine_points.setOnClickListener {
            startActivity<MyPointsActivity>()
        }

        iv_whiteclose.setOnClickListener {
            rl_warmuserinfo.visibility = View.GONE
            SPUtils.instance().put(USERINFO_PERCENT+ getLocalUserId(),System.currentTimeMillis()).apply()
        }

        tv_edituserinfo.setOnClickListener {
            mData?.let {
                startActivityForResult<MyInfoActivity>(0, "data" to it, "images" to mPicsWall)
            }
        }

        tv_squarewarm.setOnClickListener {
            startActivity<UserInfoActivity>("id" to getLocalUserId())
        }

        rl_unknow.setOnClickListener {
//            startActivity<UnKnownActivity>()
            startActivity<PrivacySettingActivity>()
            iv_unknow_reddot.visibility = View.GONE
            SPUtils.instance().put(Const.IS_FIRST_SHOWUNKNOW_TIPS, true).apply()
        }

        rl_inviate.setOnClickListener {
            startActivity<InviteGoodFriendsActivity>()
        }

        sw_mine_off.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                updateUserOnline(2)//1 不在线
            }else{
                updateUserOnline(1)//2 在线
            }
        }

        rv_square_imgs.setHasFixedSize(true)
        rv_square_imgs.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv_square_imgs.isNestedScrollingEnabled = false
        rv_square_imgs.adapter = myImageAdapter
        rv_square_imgs.addItemDecoration(VerticalDividerItemDecoration.Builder(context)
                .colorResId(android.R.color.transparent)
                .size(dip(3))
                .build())

//        headview.hierarchy = getHierarchy()
        if(!SPUtils.instance().getBoolean(Const.IS_FIRST_SHOWUNKNOW_TIPS, false)){
            iv_unknow_reddot.visibility = View.VISIBLE
        }else{
            iv_unknow_reddot.visibility = View.GONE
        }

        if(TextUtils.equals(getLocalUserId(), Const.CustomerServiceId) || TextUtils.equals(getLocalUserId(), Const.CustomerServiceWomenId)) {
            tv_service_arrow.visibility = View.VISIBLE
            sv_service.visibility = View.GONE
            sw_mine_off.visibility = View.VISIBLE
        }else{
            tv_service_arrow.visibility = View.VISIBLE
            sv_service.visibility = View.VISIBLE
            sw_mine_off.visibility = View.GONE
        }


    }

    override fun onFirstVisibleToUser() {
        mPicsWall.add(AddImage("res:///" + R.mipmap.ic_add_v2bg, 1))
    }

    override fun onResume() {
        super.onResume()
        getUserInfo(true)
        getUserFollowAndFansandVistor()

        Log.i("minefragment","onresume")
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            immersionBar.init()
            if(!isShowWarm){
                getUserFollowAndFansandVistor()
            }
            getUserInfo(false)
            Log.i("minefragment","onHiddenChanged--${isShowWarm}")
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

    private fun getUserInfo(showDataInfo: Boolean) {
        Request.getUserInfo("", getLocalUserId()).request(this, success = { _, data ->
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
                tv_age.isSelected = TextUtils.equals("0", it.sex)
//                it.age?.let {
//                    if (it.toInt() <= 0) {
//                        tv_age.visibility = View.GONE
//                        tv_age.text = ""
//                    } else {
//                        tv_age.visibility = View.VISIBLE
//                        tv_age.text = it
//                    }
//                }
                if(it.age.isNullOrEmpty()){
                    tv_age.visibility = View.GONE
                    tv_age.text = ""
                }else{
                    tv_age.visibility = View.VISIBLE
                    tv_age.text = "${it.age}岁"
                }

//                SPUtils.instance().put(Const.User.USERPOINTS_NUMS, it.iPoint.toString()).apply()
                AppUtils.setUserWallet(context,"积分 ${it.iPoint.toString()}",0 ,2 ,tv_points)
                AppUtils.setUserWallet(context,"喜欢 ${it.iLovePoint}",0 ,3 ,tv_redflowernums)

                tv_fans_count.text = "${data.iReceiveLovePoint} [img src=redheart_small/]"
                tv_follow_count.text = "${data.iSendLovePoint} [img src=redheart_small/]"

                if(TextUtils.equals(getLocalUserId(), Const.CustomerServiceId)||TextUtils.equals(getLocalUserId(), Const.CustomerServiceWomenId)){
                    img_auther.visibility = View.GONE
                    img_mine_official.visibility = View.VISIBLE
                }else{
                    if (TextUtils.equals("0", mData!!.screen) || mData!!.screen.isNullOrEmpty()) {
                        img_auther.visibility = View.GONE
                    } else if (TextUtils.equals("1", mData!!.screen)) {
                        img_auther.setImageResource(R.mipmap.video_big)
                        img_auther.visibility = View.VISIBLE
                    } else if (TextUtils.equals("3", mData!!.screen)) {
                        img_auther.visibility = View.GONE
                        img_auther.setImageResource(R.mipmap.renzheng_small)
                    } else {
                        img_auther.visibility = View.GONE
                    }
                }

                if(TextUtils.equals("0",it.isValid)){
                    img_auther.visibility = View.GONE
                    img_mine_official.visibility = View.VISIBLE
                    img_mine_official.setImageResource(R.mipmap.official_forbidden_icon)
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
                    drawable = ContextCompat.getDrawable(context, R.mipmap.ruqun_icon)
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())
                } else if(TextUtils.equals(it.userclassesid,"31")){
                    tv_vip.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.app_vip)
                    drawable = ContextCompat.getDrawable(context, R.mipmap.app_vip)
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())
                }else{
                    tv_vip.visibility = View.GONE
                    drawable = null
                    tv_menber_center.text = getString(R.string.string_vip_tq)
                }

//                if(!TextUtils.equals(it.userclassesid, "7")){
//                    tv_goodfirends.text = "邀请好友入会赢现金奖励"
//                    tv_goodfirends.visibility = View.VISIBLE
//                }else{
//                    tv_goodfirends.visibility = View.GONE
//                }

                tv_goodfirends.text = "邀请好友入会赢现金奖励"
                tv_goodfirends.visibility = View.VISIBLE

                tv_menber_center.setCompoundDrawables(drawable, null, null, null)

                if(showDataInfo){
                    if(it.iDatacompletion < 60){
                        if(SPUtils.instance().getLong(USERINFO_PERCENT+ getLocalUserId(),System.currentTimeMillis())!=System.currentTimeMillis()){
                            if(getSevenDays(SPUtils.instance().getLong(USERINFO_PERCENT+getLocalUserId(), System.currentTimeMillis()))){
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
                }

                ll_square.visibility = View.VISIBLE
                if (!TextUtils.equals("null", it.sSquarePicList.toString())) {
                    rv_square_imgs.visibility = View.GONE
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
                setPicsWall(it)

                //2.5移除
/*              if (TextUtils.equals("7",it.userclassesid)) {
                    if(TextUtils.equals("1",it.sex)){
                        rl_customerservice.visibility = View.GONE
                        view_kf.visibility = View.GONE
                    }else{
                        rl_customerservice.visibility = View.VISIBLE
                        view_kf.visibility = View.VISIBLE
                    }
                }else{
                    rl_customerservice.visibility = View.VISIBLE
                    view_kf.visibility = View.VISIBLE
                }
                var intent = Intent(Const.MINE_MANSERVICE_YOUKE)
                context.sendBroadcast(intent)*/

                Const.iLovePointShow = it.iLovePointShow
            }
        }) { _, _ ->
//            mSwipeRefreshLayout.isRefreshing = false
        }
    }

    //关注粉丝访客
    fun getUserFollowAndFansandVistor() {
        Request.getUserFollowAndFansandVistor(getLocalUserId()).request(this, success = { s: String?, data: FollowFansVistor? ->
            data?.let {
//                tv_fans_count.text = data.iFansCountAll.toString()
//                tv_follow_count.text = data.iFollowCount.toString()
//                showLikeWarm(false,0,it.iPointNew!!.toInt(),data.iVistorCount!!.toInt())
                var point = it.iPointNew!!.toInt()
                var iVistorCount = it.iVistorCount!!.toInt()
                tv_vistor_count.text = "${it.iVistorCountAll}"
                Request.getUserInfo(getLocalUserId(), getLocalUserId()).request(this,false,success= { msg, data ->
                    data?.let {
                        showLikeWarm(false,data.iReceiveNewLovePoint,point,iVistorCount)
                    }
                })
            }
        })
    }

    //客服修改在线状态
    fun updateUserOnline(iOnline:Int){
        Request.updateUserOnline(getLoginToken(),iOnline).request(this,success={_,data->
             data?.let {
                 toast("成功")
             }
        }){code,msg->
            toast(msg)
        }
    }

    fun showLikeWarm(showWarm:Boolean,fansCount:Int,iPoint:Int,vistorCount:Int){
        if (fansCount > 0) {
            tv_fcount.text = "+${fansCount}"
            tv_fcount.visibility = View.VISIBLE
        } else {
            tv_fcount.visibility = View.GONE
        }

        if (iPoint> 0) {
            iv_reddot.visibility = View.VISIBLE
        } else {
            iv_reddot.visibility = View.GONE
        }

        if (vistorCount > 0) {
            tv_vcount.text = "+${vistorCount}"
            tv_vcount.visibility = View.VISIBLE
        } else {
            tv_vcount.visibility = View.GONE
        }

        var intent = Intent(Const.MINE_MESSAGE)
        if(fansCount>0||vistorCount>0){
            intent.putExtra("showwarm",true)
            isShowWarm = true
        }else{
            intent.putExtra("showwarm",false)
            isShowWarm = false
        }
        context.sendBroadcast(intent)
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
        }.request(this,success={ _, _ ->
            dismissDialog()
            setPicsWall(userData)
        }){msg,code->
            dismissDialog()
            toast(msg)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar.destroy()
    }
    
}