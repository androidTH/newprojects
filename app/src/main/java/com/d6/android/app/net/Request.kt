package com.d6.android.app.net

import com.d6.android.app.models.Response
import com.d6.android.app.models.UserData
import com.d6.android.app.utils.getFileSuffix
import com.d6.android.app.utils.ioScheduler
import com.d6.android.app.utils.sysErr
import com.google.gson.JsonPrimitive
import com.qiniu.android.storage.UploadManager
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.File


/**
 * 服务端接口相关。。
 */
object Request {

    const val PAGE_SIZE = 20

    fun uploadFile(file: File): Flowable<String> {
        return RRetrofit.instance().create(ApiServices::class.java).getQiniuToken().ioScheduler().flatMap {
            if (it.res == 1) {
                val upload = UploadManager()
                val objectKey = System.currentTimeMillis().toString() + "." + file.getFileSuffix()
                val token = it.resMsg
                Flowable.create<String>({
                    val info = upload.syncPut(file, objectKey, token, null)
                    sysErr("--->$info")
                    sysErr("--->" + info.response)
                    if (info.isOK) {
                        val key = info.response.optString("key")
                        it.onNext("http://p22l7xdxa.bkt.clouddn.com/$key")
                        it.onComplete()
                    } else {
                        it.onError(ResultException("上传失败！"))
                    }
                }, BackpressureStrategy.DROP).subscribeOn(Schedulers.io())
            } else {
                Flowable.error {
                    ResultException(it.resMsg)
                }
            }
        }
    }

    /**
     * 登陆
     */
    fun login(type: Int, account: String, pwd: String): Flowable<Response<UserData>> {
//        val type = if (account.length >= 11) {//是手机号
//            1
//        } else {
//            0
//        }
        val phone = if (type == 1) {
            account
        } else {
            null
        }

        val loginName = if (type == 1) {
            null
        } else {
            account
        }
        return RRetrofit.instance().create(ApiServices::class.java).login(phone, loginName, pwd, type)
    }

    /**
     * 注册
     */
    fun register(phone: String, pwd: String, code: String, phoneType: String, sex: Int) =
            RRetrofit.instance().create(ApiServices::class.java).register(phone, pwd, code, phoneType, sex)

    fun sendSMSCode(phone: String, type: Int, phoneType: Int) =
            RRetrofit.instance().create(ApiServices::class.java).sendSMSCode(phone, type, phoneType)

    /**
     * 获取
     */
    fun getBanners() =
            RRetrofit.instance().create(ApiServices::class.java).getBanners()

    fun getInfo(piecesMark: String) =
            RRetrofit.instance().create(ApiServices::class.java).getInfo(piecesMark)

    /**
     * 广场分类
     */
    fun getSquareTags() =
            RRetrofit.instance().create(ApiServices::class.java).getSquareTags()

    /**
     * 广场列表
     * @param limit 广场对应的评论信息，初始加载的时候要显示前几条，这里就传入数字几，不显示，则传入0即可，如果是下拉滚动条刷新获取最新数据，请调用/backstage/comments/findByPage分页查询
     * sex 1男0女2全部
     */
    fun getSquareList(accountId: String, classesid: String, pageNum: Int, limit: Int = 0,sex:Int =2) =
            RRetrofit.instance().create(ApiServices::class.java).getSquareList(accountId, classesid, pageNum, limit = limit,sex= sex)

    /**
     * 广场详情
     */
    fun getSquareDetail(accountId: String, id: String) =
            RRetrofit.instance().create(ApiServices::class.java).getSquareDetail(accountId, id)

    /**
     * 广场评论
     */
    fun getCommentList(userId: String,id: String, pageNum: Int) =
            RRetrofit.instance().create(ApiServices::class.java).getCommentList(userId,id, pageNum)

    /**
     * 获取个人信息
     */
    fun getUserInfo(loginuserid:String,id: String) =
            RRetrofit.instance().create(ApiServices::class.java).getUserInfo(loginuserid,id)

    /**
     * SHANCHU
     */
    fun deleteSquare(userId: String, id: String?) =
            RRetrofit.instance().create(ApiServices::class.java).deleteSquare(userId, id)

    fun deleteSysMsg(userId: String, id: String?) =
            RRetrofit.instance().create(ApiServices::class.java).deleteSysMsg(userId, id)

    /**
     * 更新个人信息
     */
    fun updateUserInfo(userData: UserData) =
            RRetrofit.instance().create(ApiServices::class.java).updateUserInfo(userData)

    /**
     * 反馈
     */
    fun feedback(userId: String, content: String) =
            RRetrofit.instance().create(ApiServices::class.java).feedback(userId, content)

    fun addComment(userId: String, newsId: String, content: String, replyUid: String?) =
            RRetrofit.instance().create(ApiServices::class.java).addComment(userId, newsId, content, replyUid)

    /**
     * 速约
     */
    fun getSpeedDateList(type: Int, pageNum: Int, beginTime: String? = null, endTime: String? = null, classesId: String? = null
                         , arraySpeedState: String? = null, area: String? = null, outArea: String? = null, arrayUserClassesId: String? = null, speedhomepage: String? = null, pageSize: Int = PAGE_SIZE) =
            RRetrofit.instance().create(ApiServices::class.java).getSpeedDateList(type, pageNum, beginTime, endTime, classesId, arraySpeedState, area, outArea, arrayUserClassesId, speedhomepage, pageSize)

    fun getSpeedDetail(ids: String?) = RRetrofit.instance().create(ApiServices::class.java).getSpeedDetail(ids)

    /**
     * 觅约
     */
    fun getFindDateList(type: Int, pageNum: Int, beginTime: String? = null, endTime: String? = null, classesId: String? = null
                        , arrayLookState: String? = null, area: String? = null, outArea: String? = null, arrayUserClassesId: String? = null, pageSize: Int = PAGE_SIZE) =
            RRetrofit.instance().create(ApiServices::class.java).getFindDateList(type, pageNum, beginTime, endTime, classesId, arrayLookState, area, outArea, arrayUserClassesId, pageSize)

    /**
     *
     */
    fun searchWeChatId(kfName: String) =
            RRetrofit.instance().create(ApiServices::class.java).searchWeChatId(kfName)

    fun addPraise(userId: String, newsId: String?) =
            RRetrofit.instance().create(ApiServices::class.java).addPraise(userId, newsId)

    fun cancelPraise(userId: String, newsId: String?) =
            RRetrofit.instance().create(ApiServices::class.java).cancelPraise(userId, newsId)

    /**
     * 我的广场
     * @param limit 为0的时候查询我发布的,1的时候我点赞的,2的时候我评论的
     */
    fun getMySquares(loginuserid:String,userId: String, limit: Int, pageNum: Int) =
            RRetrofit.instance().create(ApiServices::class.java).getMySquares(loginuserid,userId, limit, pageNum)

    fun getSelfReleaseList(userId: String, pageNum: Int, beginTime: String? = null, endTime: String? = null
                           , area: String? = null, outArea: String? = null, arrayUserClassesId: String? = null, pageSize: Int = PAGE_SIZE) =
            RRetrofit.instance().create(ApiServices::class.java).getSelfReleaseList(pageNum, beginTime, endTime, area, outArea, arrayUserClassesId, pageSize)

    fun releaseSquare(userId: String, classesId: String?, city: String?, imgUrl: String?, content: String,sAppointUser:String) =
            RRetrofit.instance().create(ApiServices::class.java).releaseSquare(userId, classesId, city, imgUrl, content,sAppointUser)

    fun releaseSelfAbout(userId: String, outArea: String?, area: String?, city: String?, beginTime: String?, endTime: String?, content: String, imgUrl: String?) =
            RRetrofit.instance().create(ApiServices::class.java).releaseSelfAbout(userId, content, outArea, area, city, beginTime, endTime, imgUrl)

    fun getUserLevels() =
            RRetrofit.instance().create(ApiServices::class.java).getUserLevels()

    fun getCities(paramKey: String) =
            RRetrofit.instance().create(ApiServices::class.java).getCities(paramKey)

    fun getProvince(isShow:Int=1) =
            RRetrofit.instance().create(ApiServices::class.java).getProvince(isShow)

    fun getProvinceAll() =
            RRetrofit.instance().create(ApiServices::class.java).getProvinceAll()

    fun resetPwdFirstStep(phone: String, code: String) =
            RRetrofit.instance().create(ApiServices::class.java).resetPwdFirstStep(phone, code)

    fun resetPwd(phone: String, password: String) =
            RRetrofit.instance().create(ApiServices::class.java).resetPwd(phone, password)

    fun getSquareMessages(userId: String, pageNum: Int, time: String? = null, pageSize: Int = PAGE_SIZE) =
            RRetrofit.instance().create(ApiServices::class.java).getSquareMessages(userId, pageNum, time, pageSize)

    /**
     * 获取广场消息
     */
    fun getNewSquareMessages(userId: String, pageNum: Int,pageSize: Int = PAGE_SIZE) =
            RRetrofit.instance().create(ApiServices::class.java).getNewSquareMessages(userId, pageNum, pageSize)

    fun getSystemMessages(userId: String, pageNum: Int, time: String? = null, pageSize: Int = PAGE_SIZE) =
            RRetrofit.instance().create(ApiServices::class.java).getSystemMsgs(userId, pageNum, time, pageSize)

    fun getTalkDetails(fromUserId: String, toUserId: String, date: String) =
            RRetrofit.instance().create(ApiServices::class.java).getTalkDetails(fromUserId, toUserId, date)


    fun updateDeviceType(userId: String) =
            RRetrofit.instance().create(ApiServices::class.java).updateDeviceType(userId)

    fun getVerifyCodeV2(phone: String, verifyCodeType: Int) =
            RRetrofit.instance().create(ApiServices::class.java).getVerifyCodeV2(phone, verifyCodeType)


    fun loginV2(type: Int, vercode: String? = null, phone: String? = null, guoneiguowai: String? = null, openId: String? = null,devicetoken:String?="") =
            RRetrofit.instance().create(ApiServices::class.java).loginV2(type, vercode, phone, guoneiguowai, openId,devicetoken)


    fun loginV2New(type: Int, vercode: String? = null, phone: String? = null, guoneiguowai: String? = null, openId: String? = null,devicetoken:String?="",sUnionid:String?="",sChannelId:String = "") =
            RRetrofit.instance().create(ApiServices::class.java).loginV2New(type, vercode, phone, guoneiguowai, openId,devicetoken,sUnionid,sChannelId)


    fun findDataDict(key: String? = "quhao") =
            RRetrofit.instance().create(ApiServices::class.java).findDataDict(key)

    fun addBlackList(userId: String,id:String) =
            RRetrofit.instance().create(ApiServices::class.java).addBlackList(userId,id)

    fun report(userId: String,id:String,content:String,tiptype:String) =
            RRetrofit.instance().create(ApiServices::class.java).report(userId,id,content,tiptype)

    fun updateSeeCount(userId: String) =
            RRetrofit.instance().create(ApiServices::class.java).updateSeeCount(userId)

    fun findSeeCount(userId: String) =
            RRetrofit.instance().create(ApiServices::class.java).findSeeCount(userId)

    fun myDateCount(userId: String) =
            RRetrofit.instance().create(ApiServices::class.java).myDateCount(userId)

    fun dateMeCount(userId: String) =
            RRetrofit.instance().create(ApiServices::class.java).dateMeCount(userId)

    fun getDateSuccessCount() =
            RRetrofit.instance().create(ApiServices::class.java).getDateSuccessCount()

    fun updateDateInfo(userId: String,egagementtype:String?=null,egagementtext:String?=null,userhandlookwhere:String?=null,userlookwhere:String?=null,phone:String?=null,egagementwx:String?=null,openEgagementflag:String?=null) =
            RRetrofit.instance().create(ApiServices::class.java).updateDateInfo(userId,egagementtype,egagementtext,userhandlookwhere,userlookwhere,phone,egagementwx,openEgagementflag)

    fun updateDateState(userId: String,state:String) =
            RRetrofit.instance().create(ApiServices::class.java).updateDateState(userId,state)

    fun dateUser(userId: String,engagementuserid:String) =
            RRetrofit.instance().create(ApiServices::class.java).dateUser(userId,engagementuserid)

    fun findMyDatingList(userId: String,pageNum:Int) =
            RRetrofit.instance().create(ApiServices::class.java).findMyDatingList(userId,pageNum)

    fun findDatingMeList(userId: String,pageNum:Int) =
            RRetrofit.instance().create(ApiServices::class.java).findDatingMeList(userId,pageNum)

    fun getHomeDateList(userId: String,sex:String,egagementtype:Int,userlookwhere:String?=null,userhandlookwhere:String?=null) =
            RRetrofit.instance().create(ApiServices::class.java).getHomeDateList(userId,sex,egagementtype,userlookwhere,userhandlookwhere)

    fun getAuthState(userId: String) =
            RRetrofit.instance().create(ApiServices::class.java).getAuthState(userId)

    //添加关注
    fun getAddFollow(userid:String,followuserid:String)=RRetrofit.instance().create(ApiServices::class.java).getAddFollow(userid, followuserid)

    //取消关注
    fun getDelFollow(userid:String,followuserid:String)=RRetrofit.instance().create(ApiServices::class.java).getDelFollow(userid, followuserid)

    fun getUserFollowAndFansandVistor(userid:String)=RRetrofit.instance().create(ApiServices::class.java).getUserFollowAndFansandVistor(userid)

    fun getFindMyFans(userId: String,pageNum:Int)=RRetrofit.instance().create(ApiServices::class.java).getFindMyFans(userId,pageNum)

    fun getFindMyFollows(userId: String,pageNum:Int)=RRetrofit.instance().create(ApiServices::class.java).getFindMyFollows(userId,pageNum)

    //添加访客
    fun getAddVistor(userid:String,vistorid:String)=RRetrofit.instance().create(ApiServices::class.java).getAddVistor(userid, vistorid)

    //删除访客
    fun getDelVistor(userid:String,vistorid:String)=RRetrofit.instance().create(ApiServices::class.java).getDelVistor(userid, vistorid)

    //查询用户的访客
    fun getFindVistors(userid: String,pageNum:Int)=RRetrofit.instance().create(ApiServices::class.java).getFindVistors(userid , pageNum)

    fun delComments(ids:Int)=RRetrofit.instance().create(ApiServices::class.java).delComment(ids)

    //获取充值列表
    fun getPointsRule()=RRetrofit.instance().create(ApiServices::class.java).getUserPointsRule()

    //获取用户消费的积分
    fun getUserPoints(userid: String,pageNum:Int)=RRetrofit.instance().create(ApiServices::class.java).getUserPoints(userid , pageNum)

    //发布约会
    fun releasePullDate(userid: String,sPlace: String?,sDesc: String?,iAppointType: Int?,beginTime: String?, endTime: String?, sAppointPic: String?,sAppointUser:String)=RRetrofit.instance().
            create(ApiServices::class.java).releasePullDate(userid,sPlace,sDesc,iAppointType,beginTime , endTime, sAppointPic,sAppointUser)
    //自主约会
    fun findAppointmentList(userid: String,iAppointType:String?, sPlace:String?,pageNum:Int)=RRetrofit.instance().create(ApiServices::class.java).findAppointmentList(userid,iAppointType ,sPlace,pageNum)

    //我的约会
    fun findMyAppointmentList(userid: String,pageNum:Int)=RRetrofit.instance().create(ApiServices::class.java).findMyAppointmentList(userid , pageNum)

    //报名约会
    fun signUpdate(userid:String,sAppointmentId:String,sDesc:String)=RRetrofit.instance().create(ApiServices::class.java).signUpdate(userid, sAppointmentId,sDesc)

    //约会详情页
    fun getAppointDetails(userId:String,sAppointmentSignupId:String,sAppointmentId:String,iShareUserid:String)=RRetrofit.instance().create(ApiServices::class.java).getAppoinmentIdDetail(userId,sAppointmentSignupId,sAppointmentId,iShareUserid)

    //更新约会状态
    fun updateDateStatus(sAppointmentSignupId:String,iStatus:Int,sRefuseDesc:String)=RRetrofit.instance().create(ApiServices::class.java).updateDateStatus(sAppointmentSignupId,iStatus,sRefuseDesc)

    //为读消息
    fun getUnreadAppointmentCount(userid:String)=RRetrofit.instance().create(ApiServices::class.java).getUnreadAppointmentCount(userid)

    //人工推荐
    fun findLookAboutList(userId:String)=RRetrofit.instance().create(ApiServices::class.java).findLookAboutList(userId)

    //查询全部人工推荐
    fun findLookAllAboutList(userId:String,iLookType:String,splace:String,pageNum:Int)=RRetrofit.instance().create(ApiServices::class.java).findLookAllAboutList(userId,iLookType,splace,pageNum)

    //获取支付后的订单状态
    fun getOrderById(sOrderid:String)=RRetrofit.instance().create(ApiServices::class.java).getOrderById(sOrderid)

    //添加查询约会扣除、退回、取消需要的积分接口
    fun queryAppointmentPoint(userId:String)=RRetrofit.instance().create(ApiServices::class.java).queryAppointmentPoint(userId)

    //更新地理未知
    fun updateUserPosition(iUserid:String,sPosition:String)=RRetrofit.instance().create(ApiServices::class.java).updateUserPosition(iUserid,sPosition)

    //发现约会
    fun findAccountCardListPage(userId:String,sCity:String,
                                sex:String,xingzuo:String,agemin:String,agemax:String,lat:String,lon:String
                                ,pageNum:Int)=RRetrofit.instance().create(ApiServices::class.java).findAccountCardListPage(userId,sCity,sex,xingzuo,agemin,agemax,lat,lon,pageNum)

    //绑定手机号
    fun bindPhone(phone:String,vercode:String,openid:String,sUnionid:String,devicetoken:String,sWxName:String,sWxpic:String,sChannelId:String?)=RRetrofit.instance().create(ApiServices::class.java).bindPhone(phone,vercode,openid,sUnionid,devicetoken,sWxName,sWxpic,sChannelId)

    //赠送积分
    fun loginForPoint(sLoginToken:String,iUserid: String)=RRetrofit.instance().create(ApiServices::class.java).loginForPointNew(sLoginToken,iUserid)


    //支付多少积分
    fun getUnlockTalkPoint()=RRetrofit.instance().create(ApiServices::class.java).getUnlockTalkPoint()

    //能否聊天
    fun doUnlockTalk(iUserid:String,iTalkUserId:String)=RRetrofit.instance().create(ApiServices::class.java).doUnlockTalk(iUserid, iTalkUserId)

    //是否允许聊天
    fun doTalkJustify(iFromUserid:String,iToUserid:String) = RRetrofit.instance().create(ApiServices::class.java).doTalkJustify(iFromUserid,iToUserid)
    //获取小红花列表
    fun getUserFlower()=RRetrofit.instance().create(ApiServices::class.java).getUserFlowerRule()

    //绑定微信号
    fun doBindWxId(iUserid: String,wxid:String,sWxName:String,sWxpic:String,sUnionid:String)=RRetrofit.instance().create(ApiServices::class.java).doBindWxId(iUserid,wxid,sWxName,sWxpic,sUnionid)

    //大赏用户红花
    fun sendFlowerByOrderId(iUserid:String,iReceiveUserid:String,sOrderid:String,sResourceid:String)=RRetrofit.instance().create(ApiServices::class.java).sendFlowerByOrderId(iUserid,iReceiveUserid,sOrderid,sResourceid)

    //提现
    fun doCashMoney(iUserid:String,iFlowerCount:String)=RRetrofit.instance().create(ApiServices::class.java).doCashMoney(iUserid,iFlowerCount)

    //修改聊天设置接口
    fun updateTalkSetting(iUserid:String,iTalkSetting:Int)=RRetrofit.instance().create(ApiServices::class.java).updateTalkSetting(iUserid,iTalkSetting)

    //新的私聊接口
    fun doTalkJustifyNew(iFromUserid:String,iToUserid:String) = RRetrofit.instance().create(ApiServices::class.java).doTalkJustifyNew(iFromUserid,iToUserid)

    //申请私聊接口
    fun doApplyPrivateChat(iFromUserid:String, iToUserId:String)=RRetrofit.instance().create(ApiServices::class.java).doApplyPrivateChat(iFromUserid,iToUserId)

    //同意或拒绝私聊接口
    fun doUpdatePrivateChatStatus(iFromUserid:String,iToUserid:String,iStatus:String)=RRetrofit.instance().create(ApiServices::class.java).doUpdatePrivateChatStatus(iFromUserid,iToUserid,iStatus)

     //获取与当前用户的私聊状态
    fun getApplyStatus(iFromUserid:String,iToUserid:String)=RRetrofit.instance().create(ApiServices::class.java).getApplyStatus(iFromUserid,iToUserid)

    //获取用户信息接口
    fun getUserInfoDetail(iUserid:String)=RRetrofit.instance().create(ApiServices::class.java).getUserInfoDetail(iUserid)

    //判断是否允许发布约会接口
    fun getAppointmentAuth(iUserid:String)=RRetrofit.instance().create(ApiServices::class.java).getAppointmentAuth(iUserid)

    //判断是否有查看访客权限
    fun getVistorAuth(iUserid:String)=RRetrofit.instance().create(ApiServices::class.java).getVistorAuth(iUserid)

    //支付查看访客积分
    fun getVistorPayPoint(iUserid:String)=RRetrofit.instance().create(ApiServices::class.java).getVistorPayPoint(iUserid)

    //更新版本
    fun getByVersion(sVersion:String,iType:String)=RRetrofit.instance().create(ApiServices::class.java).getByVersion(sVersion,iType)

    //获取粉丝列表
    fun getFindMyBlackList(iUserId: String,pageNum:Int)=RRetrofit.instance().create(ApiServices::class.java).getFindMyBlacklist(iUserId,pageNum)

    //移除黑名单
    fun removeBlackList(sId:String?)=RRetrofit.instance().create(ApiServices::class.java).removeBlackList(sId.toString())

    //更新微信登录的unionId
    fun updateUnionId(iUserid:String,sOpenId:String,sUnionid:String)=RRetrofit.instance().create(ApiServices::class.java).updateUnionId(iUserid,sOpenId,sUnionid)

    //把某人移除黑名单
    fun removeBlackList(userId: String,id:String)=RRetrofit.instance().create(ApiServices::class.java).removeBlackList(userId,id)

    //查询好友列表
    fun findUserFriends(iUserId: String,sUserName:String,pageNum:Int)=RRetrofit.instance().create(ApiServices::class.java).findUserFriends(iUserId,sUserName,pageNum)

    //消息设置接口
    fun updateMessageSetting(iUserId:String,iMessageSetting:Int)=RRetrofit.instance().create(ApiServices::class.java).updateMessageSetting(iUserId,iMessageSetting)

    //客服查询用户
    fun findAllUserFriends(iUserId:String,sUserName:String,pageNum:Int)=RRetrofit.instance().create(ApiServices::class.java).findAllUserFriends(iUserId,sUserName,pageNum)

    //查询好友个数
    fun findFriendCount(iUserId:String)=RRetrofit.instance().create(ApiServices::class.java).findFriendCount(iUserId)

    //分享给好友
    fun shareMessage(userid:String,iType:Int,sResourceId:String,sAppointUser:String)=RRetrofit.instance().create(ApiServices::class.java).shareMessage(userid,iType,sResourceId,sAppointUser)

    //推送消息
    fun pushCustomerMessage(sLoginToken:String,iUserid:String,iType:Int,sResourceId:String)=RRetrofit.instance().create(ApiServices::class.java).pushCustomerMessage(sLoginToken,iUserid,iType,sResourceId)

    //获取会员列表
    fun findUserClasses(sLoginToken:String,sAreaName:String="")=RRetrofit.instance().create(ApiServices::class.java).findUserClasses(sLoginToken,sAreaName)

    //删除约会列表
    fun delAppointment(sLoginToken:String,sAppointmentId:String)=RRetrofit.instance().create(ApiServices::class.java).delAppointment(sLoginToken,sAppointmentId)
}