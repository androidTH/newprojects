package com.d6.android.app.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.TextUtils
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
import io.reactivex.Flowable
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.fragment_myinfo.*
import kotlinx.android.synthetic.main.header_mine_layout.view.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.bundleOf
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

    private val mSquares = ArrayList<Square>()

    private val mImages = ArrayList<AddImage>()
    private val myImageAdapter by lazy {
        MyImageAdapter(mImages)
    }

    private val mTags = ArrayList<UserTag>()
    private val userTagAdapter by lazy {
        UserTagAdapter(mTags)//UserTagAdapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        headview.setOnClickListener {
            mData?.let {
                startActivityForResult<MyInfoActivity>(0, "data" to it, "images" to mImages)
            }
        }
        rl_fans_count.setOnClickListener(View.OnClickListener {
            startActivity<FansActivity>()
        })

        rl_follow_count.setOnClickListener(View.OnClickListener {
            startActivity<FollowActivity>()
        })

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
//            startActivity<BlackListActivity>()
            startActivity<MemberActivity>()
        }

        rl_customerservice.setOnClickListener {
            (context as BaseActivity).pushCustomerMessage((context as BaseActivity), getLocalUserId(),5,"",next = {
                chatService((context as BaseActivity))
            })
        }

        rl_member_center.setOnClickListener {
            (context as BaseActivity).isVipUser {

            }
        }

        rl_wallet.setOnClickListener {
            startActivity<MyPointsActivity>()
        }
    }

    override fun onFirstVisibleToUser() {
        mImages.add(AddImage("res:///" + myImageAdapter.mRes, 1))
        showDialog()
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
                if(TextUtils.equals(userId, Const.CustomerServiceId)||TextUtils.equals(userId, Const.CustomerServiceWomenId)){
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
                        img_auther.setImageResource(R.mipmap.renzheng_big)
                    } else {
                        img_auther.visibility = View.GONE
                    }
                }

                //27入门 28中级  29优质
                if (TextUtils.equals(it.userclassesid, "27")) {
                    tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.gril_cj)
                } else if (TextUtils.equals(it.userclassesid, "28")) {
                    tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.gril_zj)
                } else if (TextUtils.equals(it.userclassesid, "29")) {
                    tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.gril_gj)
                } else if (TextUtils.equals(it.userclassesid.toString(), "22")) {
                    tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_ordinary)
                } else if (TextUtils.equals(it.userclassesid, "23")) {
                    tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_silver)
                } else if (TextUtils.equals(it.userclassesid, "24")) {
                    tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_gold)
                } else if (TextUtils.equals(it.userclassesid, "25")) {
                    tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_zs)
                } else if (TextUtils.equals(it.userclassesid, "26")) {
                    tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_private)
                }else if(TextUtils.equals(it.userclassesid,"7")){
                    tv_vip.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.youke_icon)
                }else{
                    tv_vip.visibility = View.GONE
                }
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

//                if (it.iPointNew!!.toInt() > 0) {
//                    headerView.tv_mypointscount.visibility = View.VISIBLE
//                } else {
//                    headerView.tv_mypointscount.visibility = View.GONE
//                }

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
}