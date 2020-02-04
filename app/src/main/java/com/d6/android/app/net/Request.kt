package com.d6.android.app.net

import com.d6.android.app.models.Response
import com.d6.android.app.models.UserData
import com.d6.android.app.utils.*
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

    //0图片 1是视频 语音
    fun uploadFile(file: File,type:Int = 0): Flowable<String> {
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
                        if(type!=0){
//                            it.onNext("http://video.d6-zone.com/$key")
                            it.onNext("http://image.d6-zone.com/$key")
                        }else{
                            it.onNext("http://image.d6-zone.com/$key")
                        }
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

    fun addComment(userId: String, newsId: String, content: String, replyUid: String?,iIsAnonymous:Int,iReplyCommnetType:Int?) =
            RRetrofit.instance().create(ApiServices::class.java).addComment(userId, newsId, content, replyUid,iIsAnonymous,iReplyCommnetType)

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

    fun releaseSquare(userId: String, classesId: String?, city: String?, imgUrl: String?, content: String,sAppointUser:String,iIsAnonymous:Int
                      ,sTopicId:String,sVideoUrl:String,sVideoPicUrl:String,sVideoWidth:String,sVideoHeight:String,sVoiceUrl:String,sVoiceLength:String,sIfLovePics:String) =
            RRetrofit.instance().create(ApiServices::class.java).releaseSquare(userId, classesId, city, imgUrl, content,sAppointUser,iIsAnonymous,
                    sTopicId,sVideoUrl,sVideoPicUrl,sVideoWidth,sVideoHeight,sVoiceUrl,sVoiceLength,sIfLovePics)

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


    fun loginV2New(type: Int, vercode: String? = null, phone: String? = null, guoneiguowai: String? = null, openId: String? = null,devicetoken:String?="",sUnionid:String?="",sChannelId:String = "",sInviteCode:String) =
            RRetrofit.instance().create(ApiServices::class.java).loginV2New(type, vercode, phone, guoneiguowai, openId,devicetoken,sUnionid,sChannelId,sInviteCode)


    fun findDataDict(key: String? = "quhao") =
            RRetrofit.instance().create(ApiServices::class.java).findDataDict(key)

    fun addBlackList(userId: String,id:String,iType:Int) =
            RRetrofit.instance().create(ApiServices::class.java).addBlackList(userId,id,iType)

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
    fun releasePullDate(userid: String,sPlace: String?,sDesc: String?,iAppointType: Int?,iFeeType:Int?,beginTime: String?, endTime: String?, sAppointPic: String?,sAppointUser:String,iIsAnonymous:Int)=RRetrofit.instance().
            create(ApiServices::class.java).releasePullDate(userid,sPlace,sDesc,iAppointType,iFeeType,beginTime , endTime, sAppointPic,sAppointUser,iIsAnonymous)
    //自主约会
    fun findAppointmentList(userid: String,iAppointType:String?, sPlace:String?,sex:String,pageNum:Int)=RRetrofit.instance().create(ApiServices::class.java).findAppointmentList(userid,iAppointType ,sPlace,sex,pageNum)

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
    fun getOrderById(sOrderid:String,iOrdertype:Int)=RRetrofit.instance().create(ApiServices::class.java).getOrderById(sOrderid,iOrdertype)

    //添加查询约会扣除、退回、取消需要的积分接口
    fun queryAppointmentPoint(userId:String,iAppointUserid:String)=RRetrofit.instance().create(ApiServices::class.java).queryAppointmentPoint(userId,iAppointUserid)

    //更新地理未知
    fun updateUserPosition(iUserid:String,sProvince:String,sCountry:String,sPosition:String,lat:String,lon:String)=RRetrofit.instance().create(ApiServices::class.java).updateUserPosition(iUserid,sProvince,sCountry,sPosition,lat,lon)

    //发现约会
    fun findAccountCardListPage(userId:String,sCity:String,
                                sex:String,userclassesid:String,agemin:String,agemax:String,lat:String,lon:String
                                ,pageNum:Int)=RRetrofit.instance().create(ApiServices::class.java).findAccountCardListPage(userId,sCity,sex,userclassesid,agemin,agemax,lat,lon,pageNum)

    //绑定手机号
    fun bindPhone(phone:String,vercode:String,openid:String,sUnionid:String,devicetoken:String,sWxName:String,sWxpic:String,sChannelId:String?,sInviteCode:String)=RRetrofit.instance().create(ApiServices::class.java).bindPhone(phone,vercode,openid,sUnionid,devicetoken,sWxName,sWxpic,sChannelId,sInviteCode)

    //赠送积分
    fun loginForPoint(sLoginToken:String,iUserid: String)=RRetrofit.instance().create(ApiServices::class.java).loginForPointNew(sLoginToken,iUserid)


    //支付多少积分
    fun getUnlockTalkPoint(sLoginToken:String)=RRetrofit.instance().create(ApiServices::class.java).getUnlockTalkPoint(sLoginToken)

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
    fun doTalkJustifyNew(iFromUserid:String,iToUserid:String,iType:Int) = RRetrofit.instance().create(ApiServices::class.java).doTalkJustifyNew(iFromUserid,iToUserid,iType)

    //申请私聊接口
    fun doApplyPrivateChat(iFromUserid:String, iToUserId:String)=RRetrofit.instance().create(ApiServices::class.java).doApplyPrivateChat(iFromUserid,iToUserId)

    //同意或拒绝私聊接口
    fun doUpdatePrivateChatStatus(iFromUserid:String,iToUserid:String,iStatus:String)=RRetrofit.instance().create(ApiServices::class.java).doUpdatePrivateChatStatus(iFromUserid,iToUserid,iStatus)

    //获取与当前用户的私聊状态
    fun getApplyStatus(iFromUserid:String,iToUserid:String,iType:Int)=RRetrofit.instance().create(ApiServices::class.java).getApplyStatus(iFromUserid,iToUserid,iType)

    //获取用户信息接口
    fun getUserInfoDetail(iUserid:String)=RRetrofit.instance().create(ApiServices::class.java).getUserInfoDetail(iUserid)

    //判断是否允许发布约会接口
    fun getAppointmentAuth(iUserid:String,sLoginToken: String)=RRetrofit.instance().create(ApiServices::class.java).getAppointmentAuth(iUserid,sLoginToken)

    //判断是否有查看访客权限
    fun getVistorAuth(iUserid:String)=RRetrofit.instance().create(ApiServices::class.java).getVistorAuth(iUserid)

    //支付查看访客积分
    fun getVistorPayPoint(iUserid:String,sLoginToken:String)=RRetrofit.instance().create(ApiServices::class.java).getVistorPayPoint(iUserid,sLoginToken)

    //更新版本
    fun getByVersion(sVersion:String,iType:String)=RRetrofit.instance().create(ApiServices::class.java).getByVersion(sVersion,iType)

    //获取粉丝列表
    fun getFindMyBlackList(iUserId: String,pageNum:Int)=RRetrofit.instance().create(ApiServices::class.java).getFindMyBlacklist(iUserId,pageNum)

    //移除黑名单
    fun removeBlackList(sId:String?)=RRetrofit.instance().create(ApiServices::class.java).delBlackList(sId.toString())

    //更新微信登录的unionId
    fun updateUnionId(iUserid:String,sOpenId:String,sUnionid:String)=RRetrofit.instance().create(ApiServices::class.java).updateUnionId(iUserid,sOpenId,sUnionid)

    //把某人移除黑名单
    fun removeBlackList(userId: String,id:String,iType:Int)=RRetrofit.instance().create(ApiServices::class.java).removeBlackList(userId,id,iType)

    //查询好友列表
    fun findUserFriends(iUserId: String,sUserName:String,pageNum:Int)=RRetrofit.instance().create(ApiServices::class.java).findUserFriends(iUserId,sUserName,pageNum)

    //消息设置接口
    fun updateMessageSetting(iUserId:String,iMessageSetting:Int)=RRetrofit.instance().create(ApiServices::class.java).updateMessageSetting(iUserId,iMessageSetting)

    //消息设置接口
    fun updateInviteMessageSetting(iShowInviteMsg:Int)=RRetrofit.instance().create(ApiServices::class.java).updateInviteMessageSetting(iShowInviteMsg)

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

    fun getProvinceAllOfMember(sType:String) =
            RRetrofit.instance().create(ApiServices::class.java).getProvinceAllOfMember(sType)

    //查询约会和动态匿名剩余次数接口
    fun getAnonymouseAppointmentPoint(sLoginToken:String,iType:Int)=RRetrofit.instance().create(ApiServices::class.java).getAnonymouseAppointmentPoint(sLoginToken,iType)

    //查询是否开启匿名卡片
    fun getUserQueryAnonymous(sLoginToken:String)=RRetrofit.instance().create(ApiServices::class.java).getUserQueryAnonymous(sLoginToken)

    //查询匿名需要支付的积分
    fun getQueryAnonymous(sLoginToken:String) = RRetrofit.instance().create(ApiServices::class.java).getQueryAnonymous(sLoginToken)

    //开通匿名卡片支付积分
    fun getAnonymousPayPoint(sLoginToken:String) = RRetrofit.instance().create(ApiServices::class.java).getAnonymousPayPoint(sLoginToken)

    //获得匿名用户详情页
    fun getAnonymousAccountDetail(sLoginToken:String,iUserid:String)=RRetrofit.instance().create(ApiServices::class.java).getAnonymousAccountDetail(sLoginToken,iUserid)
    // 创建匿名组接口
    fun CreateGroupAdd(sLoginToken:String,iTalkUserid:String,iType:Int)=RRetrofit.instance().create(ApiServices::class.java).CreateGroupAdd(sLoginToken,iTalkUserid,iType)

    //跳转到匿名用户组（先判断是否已创建匿名组，没有创建则手动创建）
    fun doToUserAnonyMousGroup(sLoginToken:String,iTalkUserid:String,iType:Int)=RRetrofit.instance().create(ApiServices::class.java).doToUserAnonyMousGroup(sLoginToken,iTalkUserid,iType)

    //查询组的信息，返回组的名称和图片（已区分是否匿名）
    fun findGroupDescByGroupId(sLoginToken:String,sGroupId:String)=RRetrofit.instance().create(ApiServices::class.java).findGroupDescByGroupId(sLoginToken,sGroupId)

    fun getLookDateDetail(ids: String?) = RRetrofit.instance().create(ApiServices::class.java).getLookDateDetail(ids)

    //新的申请私聊接口
    fun doApplyNewPrivateChat(sLoginToken:String,iToUserId:String)=RRetrofit.instance().create(ApiServices::class.java).doApplyNewPrivateChat(sLoginToken,iToUserId)

    fun getAccountInviteLink(sLoginToken:String)=RRetrofit.instance().create(ApiServices::class.java).getAccountInviteLink(sLoginToken)

    //签到
    fun signPoint(sLoginToken:String)=RRetrofit.instance().create(ApiServices::class.java).signPoint(sLoginToken)

    //客服修改在线状态
    fun updateUserOnline(sLoginToken:String,iOnline:Int) = RRetrofit.instance().create(ApiServices::class.java).updateUserOnline(sLoginToken,iOnline)

    //检查客服是否在线
    fun checkUserOnline(sLoginToken:String,iUserid:String)= RRetrofit.instance().create(ApiServices::class.java).checkUserOnline(sLoginToken,iUserid)

    //查询当日任务和完成情况
    fun findUserPointStatus(sLoginToken: String)=RRetrofit.instance().create(ApiServices::class.java).findUserPointStatus(sLoginToken)

    //2.9.0
    fun findTopicListByPage(sLoginToken:String,pageNum:Int)=RRetrofit.instance().create(ApiServices::class.java).findTopicListByPage(sLoginToken,pageNum)

    fun findTopicBannerList(sLoginToken:String)=RRetrofit.instance().create(ApiServices::class.java).findTopicBannerList(sLoginToken)

    //查询动态
    fun getFindSquareList(accountId: String, classesid: String?, pageNum: Int,limit: Int = 0,sex: Int = 2,sTopicId:String,sCity:String)
            =RRetrofit.instance().create(ApiServices::class.java).getFindSquareList(accountId, classesid, pageNum,limit = limit,sex= sex,sTopicId = sTopicId,sCity = sCity)

    fun findYKUserClasses(ids:String,sLoginToken:String,sAreaName:String="")=RRetrofit.instance().create(ApiServices::class.java).findYKUserClasses(ids,sLoginToken,sAreaName)

    //2.11.0
    fun findUserLoveRule(sLoginToken:String)=RRetrofit.instance().create(ApiServices::class.java).findUserLoveRule(sLoginToken)

    fun doCashMoneyOfLoveHeart(iUserid:String,iLoveHeartCount:String)=RRetrofit.instance().create(ApiServices::class.java).doCashMoneyOfLoveHeart(iUserid,iLoveHeartCount)

    //赠送红心
    fun sendLovePoint(sLoginToken:String,iReceiveUserid:String,iLovePoint:Int,iType:Int,sResourceid:String,sPicUrl:String="")= RRetrofit.instance().create(ApiServices::class.java).sendLovePoint(sLoginToken,iReceiveUserid,iLovePoint,iType,sResourceid,sPicUrl)

    fun findSendLoveList(sLoginToken:String,pageNum:Int)=RRetrofit.instance().create(ApiServices::class.java).findSendLoveList(sLoginToken,pageNum)

    fun findReceiveLoveList(sLoginToken: String,pageNum: Int)=RRetrofit.instance().create(ApiServices::class.java).findReceiveLoveList(sLoginToken,pageNum)

    //查询打码用户资料需要支付的积分数量
    fun getLovePointQueryAuth(sLoginToken: String,iUserid:String)=RRetrofit.instance().create(ApiServices::class.java).getLovePointQueryAuth(sLoginToken,iUserid)

    //支付积分查看打码用户
    fun loveUserQueryPayPoint(sLoginToken: String,iUserid:String)=RRetrofit.instance().create(ApiServices::class.java).loveUserQueryPayPoint(sLoginToken,iUserid)

    //更新用户卡片是否允许发现中查看
    fun updateCardIsFind(sLoginToken:String, iIsFind:Int)=RRetrofit.instance().create(ApiServices::class.java).updateCardIsFind(sLoginToken,iIsFind)

    fun findReceiveLoveHeartList(iUserid:String,sLoginToken:String,pageNum:Int)=RRetrofit.instance().create(ApiServices::class.java).findReceiveLoveHeartList(iUserid,sLoginToken,pageNum)

    fun updateListSetting(sLoginToken:String,iListSetting:Int) = RRetrofit.instance().create(ApiServices::class.java).updateListSetting(sLoginToken,iListSetting)

    fun updateSendPointShow(sLoginToken:String,iSendPointShow:Int) = RRetrofit.instance().create(ApiServices::class.java).updateSendPointShow(sLoginToken,iSendPointShow)

    fun findLoveListing(sLoginToken:String,iSex:Int,pageNum: Int = 1)= RRetrofit.instance().create(ApiServices::class.java).findLoveListing(sLoginToken,iSex,pageNum)

    fun findSquareTop()=RRetrofit.instance().create(ApiServices::class.java).findSquareTop()

    fun addConnectVoice(userid: String,sDesc: String, iAppointType:Int,iVoiceConnectType:Int,iPrepayLovepoint:Int, iOncePayLovePoint:Int,sAppointPic: String?,beginTime: String,dEndTime:String)=RRetrofit.instance().create(ApiServices::class.java).addConnectVoice(userid,sDesc,iAppointType,iVoiceConnectType,iPrepayLovepoint,iOncePayLovePoint,sAppointPic,beginTime,dEndTime)

    fun getApplyVoiceSquareLovePoint(sAppointmentId:String,sLoginToken:String)=RRetrofit.instance().create(ApiServices::class.java).getApplyVoiceSquareLovePoint(sAppointmentId,sLoginToken)

    fun addVoiceChat(sAppointmentId:String,sLoginToken:String)=RRetrofit.instance().create(ApiServices::class.java).addVoiceChat(sAppointmentId,sLoginToken)

    fun updateSquareSignUp(sAppointmentSignupId:String,iStatus:String,iConnectVoiceLength:Long,sLoginToken:String)=RRetrofit.instance().create(ApiServices::class.java).updateSquareSignUp(sAppointmentSignupId,iStatus,iConnectVoiceLength, sLoginToken)

    fun getInviteFindByPage(pageNum: Int=1)=RRetrofit.instance().create(ApiServices::class.java).getInviteFindByPage(pageNum)

    fun findEventListByUserId(iUserid:String)=RRetrofit.instance().create(ApiServices::class.java).findEventListByUserId(iUserid)

    fun updatePhoneSetting(iPhoneSetting:Int)=RRetrofit.instance().create(ApiServices::class.java).updatePhoneSetting(iPhoneSetting)

    fun updateLookaboutSetting(iLookaboutSetting:Int)=RRetrofit.instance().create(ApiServices::class.java).updateLookaboutSetting(iLookaboutSetting)

    fun updateProgress(sAppointmentSignupId:String,iProgress:Int)=RRetrofit.instance().create(ApiServices::class.java).updateProgress(sAppointmentSignupId,iProgress)


    //3.4
    fun createRongCloudGroup(sGroupName:String,sGroupPic:String,sGroupDesc:String)=RRetrofit.instance().create(ApiServices::class.java).createRongCloudGroup(sGroupName,sGroupPic,sGroupDesc)

    fun dissGroup(sGroupId:String)=RRetrofit.instance().create(ApiServices::class.java).dissGroup(sGroupId)

    fun quiteGroup(sGroupId:String)=RRetrofit.instance().create(ApiServices::class.java).quiteGroup(sGroupId)

    fun kickGroup(sGroupId:String,iUserid:String)=RRetrofit.instance().create(ApiServices::class.java).kickGroup(sGroupId,iUserid)

    fun applyToGroup(sGroupId:String,sApplyDesc:String)=RRetrofit.instance().create(ApiServices::class.java).applyToGroup(sGroupId,sApplyDesc)

    //同意或拒绝加入群组
    fun confirmToGroup(sApplyId:String,iStatus:String)=RRetrofit.instance().create(ApiServices::class.java).confirmToGroup(sApplyId,iStatus)

    fun getGroupByGroupNum(iGroupNum:String)=RRetrofit.instance().create(ApiServices::class.java).getGroupByGroupNum(iGroupNum)

    fun getGroupByGroupId(sGroupId:String)=RRetrofit.instance().create(ApiServices::class.java).getGroupByGroupId(sGroupId)

    fun getMyGroupList(pageNum:Int=1)=RRetrofit.instance().create(ApiServices::class.java).getMyGroupList(pageNum)

    fun getGroupMemberListByGroupId(sGroupId:String,sUserName:String,pageNum:Int)=RRetrofit.instance().create(ApiServices::class.java).getGroupMemberListByGroupId(sGroupId,sUserName,pageNum)

    fun getGroupAllMemberListByGroupId(sGroupId:String,pageNum:Int)=RRetrofit.instance().create(ApiServices::class.java).getGroupAllMemberListByGroupId(sGroupId,pageNum)

    fun updateUserGroupManager(sGroupId:String,iUserid:String,iIsManager:Int)=RRetrofit.instance().create(ApiServices::class.java).updateUserGroupManager(sGroupId,iUserid,iIsManager)

    fun updateMemberNotification(sGroupId:String,iIsNotification:Int)=RRetrofit.instance().create(ApiServices::class.java).updateMemberNotification(sGroupId,iIsNotification)

    fun updateGroup(sGroupId:String,sGroupName:String,sGroupPic:String,sGroupDesc:String)=RRetrofit.instance().create(ApiServices::class.java).updateGroup(sGroupId,sGroupName,sGroupPic,sGroupDesc)

}