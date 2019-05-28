package com.d6.android.app.net

import com.d6.android.app.models.*
import com.d6.android.app.utils.AppUtils
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.qiniu.android.utils.Json
import com.vector.update_app.utils.AppUpdateUtils
import io.reactivex.Flowable
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Created on 2017/12/27.
 */
interface ApiServices {

    @POST("backstage/login/system_login")
    fun login(@Query("phone") phone: String?, @Query("loginName") account: String?, @Query("password") pwd: String, @Query("logintype") loginType: Int,@Query("sVersion") sVersion:String = AppUpdateUtils.getVersionName(AppUtils.context)): Flowable<Response<UserData>>

    @POST("backstage/account/add")
    fun register(@Query("phone") phone: String, @Query("password") pwd: String, @Query("vercode") code: String, @Query("guoneiguowai") phoneType: String, @Query("sex")sex:Int): Flowable<Response<JsonObject>>

    @POST("backstage/login/getVerifyCode")
    fun sendSMSCode(@Query("phone") phone: String, @Query("vercodetype") vercodetype: Int, @Query("guoneiguowai") guoneiguowai: Int): Flowable<Response<JsonObject>>

    @POST("backstage/account/resetPwd")
    fun resetPwd(@Query("phone") phone: String, @Query("password") password: String, @Query("logintype") logintype: String = "1"): Flowable<Response<JsonObject>>

    @POST("backstage/account/resetPwdFirstStep")
    fun resetPwdFirstStep(@Query("phone") phone: String, @Query("vercode") vercode: String, @Query("logintype") logintype: Int = 1): Flowable<Response<JsonObject>>

    @POST("backstage/account/find")
    fun getUserInfo(@Query("loginuserid") loginuserid:String,@Query("userid") accountId: String): Flowable<Response<UserData>>

    @POST("backstage/account/findAccountDetail")
    fun getUserInfoDetail(@Query("iUserid") accountId:String): Flowable<Response<UserData>>

    @POST("backstage/banner/findByPage")
    fun getBanners(@Query("pageNum") pageNum: Int = 1,@Query("pageSize") pageSize: Int = 10,@Query("bannerkey") bannerkey: String="home"): Flowable<Response<Page<Banner>>>

    //碎片接口
    @POST("backstage/pieces/find")
    fun getInfo(@Query("piecesMark") piecesMark: String = "1"): Flowable<Response<JsonObject>>

    @POST("backstage/account/update")
    fun updateUserInfo(@Body userData: UserData): Flowable<Response<UserData>>

    @POST("backstage/squareclasses/find")
    fun getSquareTags(): Flowable<Response<ArrayList<SquareTag>>>

    @POST("backstage/square/del")
    fun deleteSquare(@Query("userid") userid:String,@Query("ids") ids:String?): Flowable<Response<JsonObject>>

    @POST("backstage/imessage/del")
    fun deleteSysMsg(@Query("userid") userid:String,@Query("ids") ids:String?): Flowable<Response<JsonObject>>

    @POST("backstage/square/findByPage")
    fun getSquareList(@Query("userid") accountId: String, @Query("classesid") classesid: String?, @Query("pageNum") pageNum: Int, @Query("pageSize") pageSize: Int = Request.PAGE_SIZE, @Query("limit") limit: Int = 0, @Query("sex") sex: Int = 2): Flowable<Response<Page<Square>>>

    @POST("backstage/square/find")
    fun getSquareDetail(@Query("userid") userId: String, @Query("ids") ids: String?, @Query("limit") limit: Int = Request.PAGE_SIZE): Flowable<Response<Square>>

    @POST("backstage/comments/findByPage")
    fun getCommentList(@Query("userid") userId: String,@Query("newsId") newsId: String, @Query("pageNum") pageNum: Int, @Query("pageSize") pageSize: Int = Request.PAGE_SIZE): Flowable<Response<Page<Comment>>>

    @POST("backstage/opinions/add")
    fun feedback(@Query("userid") accountId: String, @Query("content") content: String): Flowable<Response<JsonObject>>

    @POST("backstage/comments/add")
    fun addComment(@Query("userid") accountId: String, @Query("newsId") newsId: String, @Query("content") content: String, @Query("replyuserid") replyuserid: String?): Flowable<Response<JsonObject>>

    @POST("backstage/comments/del")
    fun delComment(@Query("ids")ids:Int):Flowable<Response<JsonObject>>

    @POST("backstage/tool/webuploader/getqiniutoken")
    fun getQiniuToken(): Flowable<Response<JsonPrimitive>>

    @POST("backstage/speedabout/findByPage")
    fun getSpeedDateList(@Query("speedtype") speedType: Int, @Query("pageNum") pageNum: Int, @Query("beginTime") beginTime: String? = null, @Query("endTime") endTime: String? = null
                         , @Query("classesid") classesid: String? = null, @Query("arrayspeedstate") arraySpeedState: String? = null, @Query("speedwhere") area: String? = null
                         , @Query("handspeedwhere") guowaiarea: String? = null, @Query("arrayuserclassesid") arrayUserClassesId: String? = null, @Query("speedhomepage") speedhomepage: String? = null
                         , @Query("pageSize") pageSize: Int = Request.PAGE_SIZE): Flowable<Response<Page<MyDate>>>

    @POST("backstage/speedabout/find")
    fun getSpeedDetail(@Query("ids") ids: String?):Flowable<Response<MyDate>>

    @POST("backstage/lookabout/findByPage")
    fun getFindDateList(@Query("looktype") looktype: Int, @Query("pageNum") pageNum: Int, @Query("beginTime") beginTime: String? = null, @Query("endTime") endTime: String? = null
                        , @Query("classesid") classesid: String? = null, @Query("arraylookstate") arrayLookState: String? = null, @Query("userlookwhere") guoneiarea: String? = null
                        , @Query("userhandlookwhere") guowaiarea: String? = null, @Query("arrayuserclassesid") arrayUserClassesId: String? = null
                        , @Query("pageSize") pageSize: Int = Request.PAGE_SIZE): Flowable<Response<Page<MyDate>>>

    @POST("backstage/wxkf/find")
    fun searchWeChatId(@Query("kfName") kfName: String): Flowable<Response<JsonObject>>

    @POST("backstage/square/findByPageMySquare")
    fun getMySquares(@Query("loginuserid") loginuserid:String,@Query("userid") userid: String, @Query("limit") limit: Int, @Query("pageNum") pageNum: Int, @Query("pageSize") pageSize: Int = Request.PAGE_SIZE): Flowable<Response<Page<Square>>>

    @POST("backstage/upvote/add")
    fun addPraise(@Query("userid") userid: String, @Query("newsId") newsId: String?): Flowable<Response<JsonObject>>

    @POST("backstage/upvote/del")
    fun cancelPraise(@Query("userid") userid: String, @Query("newsId") newsId: String?): Flowable<Response<JsonObject>>

    @POST("backstage/selfabout/findByPage")
    fun getSelfReleaseList(@Query("pageNum") pageNum: Int, @Query("beginTime") beginTime: String? = null, @Query("endTime") endTime: String? = null
                           , @Query("guoneiarea") guoneiarea: String? = null, @Query("guowaiarea") guowaiarea: String? = null, @Query("arrayuserclassesid") arrayUserClassesId: String? = null, @Query("pageSize") pageSize: Int = Request.PAGE_SIZE): Flowable<Response<Page<MyDate>>>

    @POST("backstage/square/add")
    fun releaseSquare(@Query("userid") userid: String, @Query("classesid") classesid: String?, @Query("squarecity") city: String?, @Query("coverurl") coverurl: String?, @Query("content") content: String,@Query("sAppointUser")sAppointUser:String): Flowable<Response<JsonObject>>

    @POST("backstage/selfabout/add")
    fun releaseSelfAbout(@Query("userid") userid: String, @Query("content") content: String?, @Query("handlookwhere") handlookwhere: String?
                         , @Query("lookwhere") lookwhere: String?, @Query("city") city: String?, @Query("beginTime") beginTime: String?
                         , @Query("endTime") endTime: String?, @Query("selfpicurl") selfpicurl: String?): Flowable<Response<JsonObject>>

    @POST("backstage/userclasses/findauto")
    fun getUserLevels(): Flowable<Response<ArrayList<UserLevel>>>

    @POST("backstage/sysDict/findauto")
    fun getCities(@Query("paramKey") paramKey: String): Flowable<Response<ArrayList<City>>>

    @POST("backstage/sysDict/findautoNew")
    fun getProvince(@Query("isShow") isShow:Int): Flowable<Response<ArrayList<Province>>>

    @POST("backstage/sysDict/findautoAll")
    fun getProvinceAll(): Flowable<Response<ArrayList<Province>>>

    @POST("backstage/comments/findByPageguangchangxiaoxi")
    fun getSquareMessages(@Query("userid") userid: String, @Query("pageNum") pageNum: Int, @Query("createTime") createTime: String? = null, @Query("pageSize") pageSize: Int = Request.PAGE_SIZE): Flowable<Response<Page<SquareMessage>>>

    /**
     * 广场动态消息
     */
    @POST("backstage/imessage/findSquareMessageByPage")
    fun getNewSquareMessages(@Query("userid") userid: String, @Query("pageNum") pageNum: Int,@Query("pageSize") pageSize: Int = Request.PAGE_SIZE): Flowable<Response<Page<SquareMessage>>>


    @POST("backstage/rongcloud/gettalkdetails")
    fun getTalkDetails(@Query("fromuserid") fromuserid: String, @Query("touserid") touserid: String, @Query("checkdate") checkdate: String): Flowable<Response<JsonObject>>

    @POST("backstage/imessage/findByPage")
    fun getSystemMsgs(@Query("userid") userid: String, @Query("pageNum") pageNum: Int, @Query("createTime") createTime: String? = "", @Query("pageSize") pageSize: Int = Request.PAGE_SIZE): Flowable<Response<Page<SysMessage>>>

    @POST("backstage/account/updateumengdevicetype")
    fun updateDeviceType(@Query("userid") userid: String, @Query("devicetype") devicetype:String = "android"): Flowable<Response<JsonObject>>

    @POST("backstage/new_login/getVerifyCode")
    fun getVerifyCodeV2(@Query("phone") phone: String, @Query("vercodetype") vercodetype:Int): Flowable<Response<JsonObject>>

    @POST("backstage/new_login/system_login")
    fun loginV2(@Query("logintype") logintype: Int, @Query("vercode") vercode:String?,@Query("phone") phone:String?=null, @Query("guoneiguowai") guoneiguowai:String?=null, @Query("openid") openid:String?=null,@Query("devicetoken") devicetoken:String?): Flowable<Response<UserData>>

    @POST("backstage/new_login/system_login_new")
    fun loginV2New(@Query("logintype") logintype: Int, @Query("vercode") vercode:String?,@Query("phone") phone:String?=null, @Query("guoneiguowai") guoneiguowai:String?=null, @Query("openid") openid:String?=null,@Query("devicetoken") devicetoken:String?,@Query("sUnionid") sUnionid:String?,@Query("sChannelId") sChannelId:String?): Flowable<Response<UserData>>


    @POST("backstage/dataDict/find")
    fun findDataDict(@Query("dataKey") dataKey:String?="quhao"): Flowable<Response<JsonPrimitive>>

    @POST("backstage/tip/add")
    fun report(@Query("userid") userid:String,@Query("tipuserid") tipuserid:String,@Query("content") content:String,@Query("tiptype") tiptype:String): Flowable<Response<JsonPrimitive>>

    @POST("backstage/engagementsaccount/update")
    fun updateSeeCount(@Query("userid") userid:String,@Query("seecount") seecount:String="1"): Flowable<Response<JsonPrimitive>>

    @POST("backstage/engagementsaccount/findseecount")
    fun findSeeCount(@Query("userid") userid:String): Flowable<Response<JsonPrimitive>>

    @POST("backstage/engagements/leiji")
    fun myDateCount(@Query("userid") userid:String): Flowable<Response<JsonPrimitive>>

    @POST("backstage/engagements/leiji")
    fun dateMeCount(@Query("engagementuserid") userid:String): Flowable<Response<JsonPrimitive>>

    @POST("backstage/engagements/sasamieru")
    fun getDateSuccessCount(): Flowable<Response<JsonPrimitive>>

    @POST("backstage/engagementsaccount/update")
    fun updateDateInfo(@Query("userid") userid:String,@Query("egagementtype")egagementtype:String?,@Query("egagementtext")egagementtext:String?,@Query("userhandlookwhere")userhandlookwhere:String?,@Query("userlookwhere")userlookwhere:String?,@Query("phone")phone:String?,@Query("egagementwx")egagementwx:String?,@Query("openEgagementflag")openEgagementflag:String?): Flowable<Response<JsonPrimitive>>

    @POST("backstage/engagements/update")
    fun updateDateState(@Query("ids")ids:String,@Query("state")state:String): Flowable<Response<JsonPrimitive>>

    @POST("backstage/engagements/add")
    fun dateUser(@Query("userid")userid:String,@Query("engagementuserid")engagementuserid:String): Flowable<Response<JsonObject>>

    @POST("backstage/engagementsaccount/findByPagewoyuebieren")
    fun findMyDatingList(@Query("userid")userid:String,@Query("pageNum")pageNum:Int,@Query("pageSize")pageSize:Int=Request.PAGE_SIZE): Flowable<Response<Page<NewDate>>>

    @POST("backstage/engagementsaccount/findByPage")
    fun findDatingMeList(@Query("userid")userid:String,@Query("pageNum")pageNum:Int,@Query("pageSize")pageSize:Int=Request.PAGE_SIZE): Flowable<Response<Page<NewDate>>>

    @POST("backstage/engagementsaccount/findcart")
    fun getHomeDateList(@Query("userid")userid:String,@Query("sex")sex:String,@Query("egagementtype")egagementtype:Int,@Query("userlookwhere")userlookwhere:String?,@Query("userhandlookwhere")userhandlookwhere:String?): Flowable<Response<ArrayList<DateBean>>>

    @POST("backstage/engagementsaccount/percent")
    fun getAuthState(@Query("userid")userid:String): Flowable<Response<JsonObject>>

    //添加关注
    @POST("backstage/follow/add")
    fun getAddFollow(@Query("userid") userid:String,@Query("followuserid") followuserid:String?):Flowable<Response<JsonObject>>

    //取消关注
    @POST("backstage/follow/del")
    fun getDelFollow(@Query("userid") userid:String,@Query("followuserid") followuserid:String?):Flowable<Response<JsonObject>>

    //查询我的关注,粉丝访客个数
    @POST("backstage/statistic/find")
    fun getUserFollowAndFansandVistor(@Query("userid") userid:String?):Flowable<Response<FollowFansVistor>>

    @POST("backstage/follow/findMyFans")
    fun getFindMyFans(@Query("userid")userid:String,@Query("pageNum")pageNum:Int,@Query("pageSize")pageSize:Int=Request.PAGE_SIZE):Flowable<Response<Page<Fans>>>

    @POST("backstage/follow/find")
    fun getFindMyFollows(@Query("userid")userid:String,@Query("pageNum")pageNum:Int,@Query("pageSize")pageSize:Int=Request.PAGE_SIZE):Flowable<Response<Page<Fans>>>

    //添加访客
    @POST("backstage/vistor/add")
    fun getAddVistor(@Query("userid") userid:String,@Query("vistorid") vistorid:String?):Flowable<Response<JsonObject>>

    //删除访客
    @POST("backstage/vistor/del")
    fun getDelVistor(@Query("userid") userid:String,@Query("vistorid") vistorid:String?):Flowable<Response<JsonObject>>

    //查询访客记录
    @POST("backstage/vistor/find")
    fun getFindVistors(@Query("userid")userid:String,@Query("pageNum")pageNum:Int,@Query("pageSize")pageSize:Int=Request.PAGE_SIZE):Flowable<Response<Page<Fans>>>

    //积分充值列表
    @POST("backstage/userpointrule/find")
    fun getUserPointsRule():Flowable<Response<ArrayList<PointRule>>>

    //获取用户消费的积分
    @POST("backstage/userpoint/find")
    fun getUserPoints(@Query("iUserid")sUserId:String,@Query("pageNum")pageNum:Int,@Query("pageSize")pageSize:Int=Request.PAGE_SIZE):Flowable<Response<Page<UserPoints>>>

    //创建订单
    @POST("backstage/order/add")
    fun createOrder(@Query("iUserid") iUserid:Int?,@Query("iOrdertype") iOrdertype:Int?,@Query("iPrice") iPrice:Int?,@Query("iPoint") iPoint:Int?)

    //获取订单支付状态
    @POST("backstage/order/getOrderById")
    fun getOrderById(@Query("sOrderid") sOrderid:String?):Flowable<Response<JsonObject>>

    //发布约会
    @POST("backstage/appointment/add")
    fun releasePullDate(@Query("iUserid") userid: String, @Query("sPlace") sPlace: String?, @Query("sDesc") sDesc: String?
                         , @Query("iAppointType") iAppointType: Int?, @Query("dStarttime") beginTime: String?
                         , @Query("dEndtime") endTime: String?, @Query("sAppointPic") sAppointPic: String?,@Query("sAppointUser")sAppointUser:String): Flowable<Response<JsonObject>>

    //自主约会
    @POST("backstage/appointment/findAppointmentListByPage")
    fun findAppointmentList(@Query("iUserid") userid:String, @Query("iAppointType") iAppointType:String?, @Query("sPlace") sPlace:String?, @Query("pageNum")pageNum:Int, @Query("pageSize")pageSize:Int=Request.PAGE_SIZE):Flowable<Response<Page<MyAppointment>>>;

    //我的约会
    @POST("backstage/appointment/findMyAppointmentListByPage")
    fun findMyAppointmentList(@Query("iUserid")sUserId:String,@Query("pageNum")pageNum:Int,@Query("pageSize")pageSize:Int=Request.PAGE_SIZE):Flowable<Response<Page<MyAppointment>>>

    //报名约会
    @POST("backstage/appointmentsignup/add")
    fun signUpdate(@Query("iUserid") userid:String,@Query("sAppointmentId") sAppointmentId:String,@Query("sDesc") sDesc:String):Flowable<Response<JsonObject>>

   //约会详情页
    @POST("backstage/appointment/queryAppointmentDetail")
    fun getAppoinmentIdDetail(@Query("iUserid") iUserid:String,@Query("sAppointmentSignupId") sAppointmentSignupId:String,@Query("sAppointmentId") sAppointmentId:String,@Query("iShareUserid") iShareUserid:String):Flowable<Response<MyAppointment>>

    //约会状态
    @POST("backstage/appointmentsignup/updateAppointment")
    fun updateDateStatus(@Query("sAppointmentSignupId") sAppointmentSignupId:String,@Query("iStatus") iStatus:Int,@Query("sRefuseDesc") sRefuseDesc:String):Flowable<Response<JsonObject>>

    //未读取消息
    @POST("backstage/appointmentsignup/getUnreadAppointmentCount")
    fun getUnreadAppointmentCount(@Query("iUserid") userid:String):Flowable<Response<UnreadMsg>>

    //人工推荐
    @POST("backstage/lookabout/findLookAboutList")
    fun findLookAboutList(@Query("iUserid") iUserid:String,@Query("sVersion") sVersion:String = AppUpdateUtils.getVersionName(AppUtils.context)):Flowable<Response<ArrayList<MyDate>>>

    //查询全部人工推荐
    @POST("backstage/lookabout/findAllLookAboutList")
    fun findLookAllAboutList(@Query("iUserid") iUserid:String, @Query("iLookType") iLookType:String, @Query("sPlace") splace:String, @Query("pageNum")pageNum:Int, @Query("pageSize")pageSize:Int=Request.PAGE_SIZE):Flowable<Response<Page<MyDate>>>

    //添加查询约会扣除、退回、取消需要的积分接口
    @POST("backstage/appointment/queryAppointmentPoint")
    fun queryAppointmentPoint(@Query("iUserid") iUserid:String,@Query("sVersion") sVersion:String = AppUpdateUtils.getVersionName(AppUtils.context)):Flowable<Response<IntegralExplain>>

    //同城
    @POST("backstage/account/updateUserPosition")
    fun updateUserPosition(@Query("iUserid") iUserid:String,@Query("sPosition") sPosition:String,@Query("sVersion") sVersion:String = AppUpdateUtils.getVersionName(AppUtils.context)):Flowable<Response<JsonObject>>

    //发现约会
    @POST("backstage/account/findAccountCardListPage")
    fun findAccountCardListPage(@Query("iUserid") iUserid:String, @Query("sCity") scity:String,
                                @Query("sex") sex:String,@Query("xingzuo") xingzuo:String, @Query("agemin") agemin:String, @Query("agemax") agemax:String,
                                @Query("lat") lat:String, @Query("lon") lon:String,
                                @Query("pageNum")pageNum:Int, @Query("pageSize")pageSize:Int=Request.PAGE_SIZE):Flowable<Response<Page<FindDate>>>

    //绑定手机号
    @POST("backstage/account/bindPhone")
    fun bindPhone(@Query("phone") phone:String, @Query("vercode") vercode:String,@Query("openid") openid:String,@Query("sUnionid") sUnionid:String,@Query("devicetoken") devicetoken:String,@Query("sWxName")sWxName:String,@Query("sWxpic")sWxpic:String,@Query("sChannelId") sChannelId:String?):Flowable<Response<UserData>>

    //赠送积分
    @POST("backstage/new_login/loginForPointNew")
    fun loginForPointNew(@Query("sLoginToken")sLoginToken:String,@Query("iUserid") iUserid:String,@Query("sVersion") sVersion:String = AppUpdateUtils.getVersionName(AppUtils.context)):Flowable<Response<JsonObject>>

    //解锁聊天支付多少积分
    @POST("backstage/rongcloud/getUnlockTalkPoint")
    fun getUnlockTalkPoint(@Query("sVersion") sVersion:String = AppUpdateUtils.getVersionName(AppUtils.context)):Flowable<Response<JsonObject>>

    //是否能聊天
    @POST("backstage/rongcloud/unlockTalk")
    fun doUnlockTalk(@Query("iUserid") iUserid:String,@Query("iTalkUserId") iTalkUserId:String,@Query("sVersion") sVersion:String = AppUpdateUtils.getVersionName(AppUtils.context)):Flowable<Response<JsonObject>>

    //是否允许聊天
    @POST("backstage/rongcloud/getTalkJustify")
    fun doTalkJustify(@Query("iFromUserid") iUserid:String,@Query("iToUserid") iTalkUserId:String,@Query("sVersion") sVersion:String = AppUpdateUtils.getVersionName(AppUtils.context)):Flowable<Response<JsonObject>>

    /*1.8.0接口*/
    //送小红花列表
    @POST("backstage/userflowerrule/find")
    fun getUserFlowerRule(@Query("sVersion") sVersion:String = AppUpdateUtils.getVersionName(AppUtils.context)):Flowable<Response<ArrayList<FlowerRule>>>

    //绑定微信
    @POST("backstage/account/bindWxid")
    fun doBindWxId(@Query("iUserid") iUserid:String,@Query("wxid") wxId:String,@Query("sWxName") sWxName:String,@Query("sWxpic") sWxpic:String,@Query("sUnionid")sUnionid:String?=""):Flowable<Response<JsonObject>>

    //大赏小红花
    @POST("backstage/userflowerrule/sendFlowerByOrderId")
    fun sendFlowerByOrderId(@Query("iUserid") iUserid:String,@Query("iReceiveUserid") iReceiveUserid:String,@Query("sOrderid") sOrderid:String,@Query("sResourceid") sResourceid:String):Flowable<Response<JsonObject>>

    //提现接口
    @POST("backstage/userflowerrule/withDrawFlower")
    fun doCashMoney(@Query("iUserid") iUserid:String,@Query("iFlowerCount")iFlowerCount:String,@Query("sVersion") sVersion:String = AppUpdateUtils.getVersionName(AppUtils.context)):Flowable<Response<JsonObject>>

     /*1.8.5接口*/
    //修改聊天设置接口
    @POST("backstage/account/updateTalkSetting")
    fun updateTalkSetting(@Query("iUserid") iUserid:String,@Query("iTalkSetting") iTalkSetting:Int,@Query("sVersion") sVersion:String = AppUpdateUtils.getVersionName(AppUtils.context)):Flowable<Response<JsonObject>>

    //新的私聊接口
    @POST("backstage/rongcloud/getTalkJustifyNew")
    fun doTalkJustifyNew(@Query("iFromUserid") iUserid:String,@Query("iToUserid") iToUserid:String,@Query("sVersion") sVersion:String = AppUpdateUtils.getVersionName(AppUtils.context)):Flowable<Response<JsonObject>>

    //申请私聊接口
    @POST("backstage/talkapply/apply")
    fun doApplyPrivateChat(@Query("iFromUserid") iUserid:String,@Query("iToUserid") iToUserid:String,@Query("sVersion") sVersion:String = AppUpdateUtils.getVersionName(AppUtils.context)):Flowable<Response<JsonObject>>

    //同意或拒绝私聊接口
    @POST("backstage/talkapply/update")
    fun doUpdatePrivateChatStatus(@Query("iFromUserid") iFromUserid:String,@Query("iToUserid") iToUserid:String,@Query("iStatus") iStatus:String,@Query("sVersion") sVersion:String = AppUpdateUtils.getVersionName(AppUtils.context)):Flowable<Response<JsonObject>>

    //获取与当前用户的私聊状态
    @POST("backstage/talkapply/getApplyStatus")
    fun getApplyStatus(@Query("iFromUserid") iFromUserid:String,@Query("iToUserid") iToUserid:String,@Query("sVersion") sVersion:String = AppUpdateUtils.getVersionName(AppUtils.context)):Flowable<Response<JsonObject>>

    /* 1.8.2接口*/
    //判断是否允许发布约会接口
    @POST("backstage/appointment/addAppointmentAuth")
    fun getAppointmentAuth(@Query("iUserid") iUserid:String,@Query("sVersion") sVersion:String = AppUpdateUtils.getVersionName(AppUtils.context)):Flowable<Response<JsonObject>>

    //判断是否有查看访客权限
    @POST("backstage/vistor/getVistorAuth")
    fun getVistorAuth(@Query("iUserid") iUserid:String,@Query("sVersion") sVersion:String = AppUpdateUtils.getVersionName(AppUtils.context)):Flowable<Response<JsonObject>>

    //支付查看访客积分
    @POST("backstage/vistor/vistorPayPoint")
    fun getVistorPayPoint(@Query("iUserid") iUserid:String,@Query("sVersion") sVersion:String = AppUpdateUtils.getVersionName(AppUtils.context)):Flowable<Response<JsonObject>>

    /*1.9.0接口*/
    @POST("backstage/version/getByVersion")
    fun getByVersion(@Query("sVersion") sVersion:String,@Query("iType") iType:String):Flowable<Response<VersionBean>>

    /*1.9.1接口*/
    @POST("backstage/blacklist/add")
    fun addBlackList(@Query("iUserid") userid:String,@Query("iBlackUserid") blackuserid:String,@Query("sVersion") sVersion:String = AppUpdateUtils.getVersionName(AppUtils.context)): Flowable<Response<JsonPrimitive>>

    //获取黑名单列表
    @POST("backstage/blacklist/find")
    fun getFindMyBlacklist(@Query("iUserid")userid:String,@Query("pageNum")pageNum:Int,@Query("pageSize")pageSize:Int=Request.PAGE_SIZE,@Query("sVersion") sVersion:String = AppUpdateUtils.getVersionName(AppUtils.context)):Flowable<Response<Page<BlackListBean>>>

    //移除黑名单
    @POST("backstage/blacklist/del")
    fun removeBlackList(@Query("sId") sId:String): Flowable<Response<JsonPrimitive>>

    /*1.9.2接口*/
    @POST("backstage/account/updateUnionId")
    fun updateUnionId(@Query("iUserid") iUserid:String,@Query("sOpenId")sOpenId:String,@Query("sUnionid")sUnionid:String):Flowable<Response<JsonObject>>


   /* 2.0接口*/
   @POST("backstage/blacklist/remove")
   fun removeBlackList(@Query("iUserid") userid:String,@Query("iBlackUserid") blackuserid:String,@Query("sVersion") sVersion:String = AppUpdateUtils.getVersionName(AppUtils.context)): Flowable<Response<JsonPrimitive>>

    //查询好友列表
   @POST("backstage/userfriend/findByPage")
   fun findUserFriends(@Query("iUserid")userid:String,@Query("sUserName") sUserName:String,@Query("pageNum")pageNum:Int,@Query("pageSize")pageSize:Int=Request.PAGE_SIZE,@Query("sVersion") sVersion:String = AppUpdateUtils.getVersionName(AppUtils.context)):Flowable<Response<Page<FriendBean>>>

   //动态消息设置
   @POST("backstage/account/updateMessageSetting")
   fun updateMessageSetting(@Query("iUserid") userid:String,@Query("iMessageSetting") iMessageSetting:Int,@Query("sVersion") sVersion:String = AppUpdateUtils.getVersionName(AppUtils.context)):Flowable<Response<JsonPrimitive>>

   //查询用户接口
   @POST("backstage/account/findAllByPage")
   fun findAllUserFriends(@Query("iUserid")userid:String,@Query("sUserName")sUserName:String,@Query("pageNum")pageNum:Int,@Query("pageSize")pageSize:Int=Request.PAGE_SIZE,@Query("sVersion") sVersion:String = AppUpdateUtils.getVersionName(AppUtils.context)):Flowable<Response<Page<FriendBean>>>

   //我的好友个数
   @POST("backstage/userfriend/findFriendCount")
   fun findFriendCount(@Query("iUserid")userid:String,@Query("sVersion") sVersion:String = AppUpdateUtils.getVersionName(AppUtils.context)):Flowable<Response<JsonObject>>

   //分享的接口
   @POST("backstage/share/shareMessage")
   fun shareMessage(@Query("iUserid")userid:String,@Query("iType") iType:Int,@Query("sResourceId") sResourceId:String,@Query("sAppointUser")sAppointUser:String,@Query("sVersion") sVersion:String = AppUpdateUtils.getVersionName(AppUtils.context)):Flowable<Response<JsonPrimitive>>

   @POST("backstage/push/pushCustomerMessage")
   fun pushCustomerMessage(@Query("sLoginToken")sLoginToken:String,@Query("iUserid")iUserid:String,@Query("iType") iType:Int,@Query("sResourceId")sResourceId:String,@Query("sVersion") sVersion:String = AppUpdateUtils.getVersionName(AppUtils.context)):Flowable<Response<JsonPrimitive>>

   /*2.1.0接口*/
   //获取等级接口
   @POST("backstage/userclasses/findUserClasses")
   fun findUserClasses(@Query("sLoginToken")sLoginToken:String,@Query("sAreaName") sAreaName:String, @Query("sVersion") sVersion:String = AppUpdateUtils.getVersionName(AppUtils.context)):Flowable<Response<ListMemeberBean<MemberBean>>>

   //删除约会
  @POST("backstage/appointment/delAppointment")
  fun delAppointment(@Query("sLoginToken")sLoginToken:String,@Query("sAppointmentId") sAppointmentId:String,@Query("sVersion") sVersion:String = AppUpdateUtils.getVersionName(AppUtils.context)):Flowable<Response<JsonPrimitive>>

}