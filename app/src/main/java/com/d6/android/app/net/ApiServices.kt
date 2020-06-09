package com.d6.android.app.net

import bolts.Task
import com.d6.android.app.models.*
import com.d6.android.app.utils.getAppVersion
import com.d6.android.app.utils.getLoginToken
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import io.reactivex.Flowable
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created on 2017/12/27.
 */
interface ApiServices {

    @POST("backstage/login/system_login")
    fun login(@Query("phone") phone: String?, @Query("loginName") account: String?, @Query("password") pwd: String, @Query("logintype") loginType: Int,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<UserData>>

    @POST("backstage/account/add")
    fun register(@Query("phone") phone: String, @Query("password") pwd: String, @Query("vercode") code: String, @Query("guoneiguowai") phoneType: String, @Query("sex")sex:Int,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<JsonObject>>

    @POST("backstage/login/getVerifyCode")
    fun sendSMSCode(@Query("phone") phone: String, @Query("vercodetype") vercodetype: Int, @Query("guoneiguowai") guoneiguowai: Int,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<JsonObject>>

    @POST("backstage/account/resetPwd")
    fun resetPwd(@Query("phone") phone: String, @Query("password") password: String, @Query("logintype") logintype: String = "1",@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<JsonObject>>

    @POST("backstage/account/resetPwdFirstStep")
    fun resetPwdFirstStep(@Query("phone") phone: String, @Query("vercode") vercode: String, @Query("logintype") logintype: Int = 1,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<JsonObject>>

    @POST("backstage/account/find")
    fun getUserInfo(@Query("loginuserid") loginuserid:String,@Query("userid") accountId: String,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<UserData>>

    @POST("backstage/account/findAccountDetail")
    fun getUserInfoDetail(@Query("iUserid") accountId:String,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<UserData>>

    @POST("backstage/banner/findByPage")
    fun getBanners(@Query("pageNum") pageNum: Int = 1,@Query("pageSize") pageSize: Int = 10,@Query("bannerkey") bannerkey: String="home",@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<Page<Banner>>>

    //碎片接口
    @POST("backstage/pieces/find")
    fun getInfo(@Query("piecesMark") piecesMark: String = "1",@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<JsonObject>>

    @POST("backstage/account/update")
    fun updateUserInfo(@Body userData: UserData,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<UserData>>

    @POST("backstage/squareclasses/find")
    fun getSquareTags(@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<ArrayList<SquareTag>>>

    @POST("backstage/square/del")
    fun deleteSquare(@Query("userid") userid:String,@Query("ids") ids:String?,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<JsonObject>>

    @POST("backstage/imessage/del")
    fun deleteSysMsg(@Query("userid") userid:String,@Query("ids") ids:String?,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<JsonObject>>

    @POST("backstage/square/findByPage")
    fun getSquareList(@Query("userid") accountId: String, @Query("classesid") classesid: String?, @Query("pageNum") pageNum: Int, @Query("pageSize") pageSize: Int = Request.PAGE_SIZE, @Query("limit") limit: Int = 0, @Query("sex") sex: Int = 2,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<Page<Square>>>

    @POST("backstage/square/find")
    fun getSquareDetail(@Query("userid") userId: String, @Query("ids") ids: String?, @Query("limit") limit: Int = Request.PAGE_SIZE,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<Square>>

    @POST("backstage/comments/findByPage")
    fun getCommentList(@Query("userid") userId: String,@Query("newsId") newsId: String, @Query("pageNum") pageNum: Int, @Query("pageSize") pageSize: Int = Request.PAGE_SIZE,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<Page<Comment>>>

    @POST("backstage/opinions/add")
    fun feedback(@Query("userid") accountId: String, @Query("content") content: String,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<JsonObject>>

    @POST("backstage/comments/add") //1、回复的评论类型是匿名 2、回复的评论类型是非匿名
    fun addComment(@Query("userid") accountId: String, @Query("newsId") newsId: String, @Query("content") content: String, @Query("replyuserid") replyuserid: String?, @Query("iIsAnonymous") iIsAnonymous:Int, @Query("iReplyCommnetType") iReplyCommnetType:Int?,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<JsonObject>>

    @POST("backstage/comments/del")
    fun delComment(@Query("ids")ids:Int,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    @POST("backstage/tool/webuploader/getqiniutoken")
    fun getQiniuToken(@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<JsonPrimitive>>

    @POST("backstage/speedabout/findByPage")
    fun getSpeedDateList(@Query("speedtype") speedType: Int, @Query("pageNum") pageNum: Int, @Query("beginTime") beginTime: String? = null, @Query("endTime") endTime: String? = null
                         , @Query("classesid") classesid: String? = null, @Query("arrayspeedstate") arraySpeedState: String? = null, @Query("speedwhere") area: String? = null
                         , @Query("handspeedwhere") guowaiarea: String? = null, @Query("arrayuserclassesid") arrayUserClassesId: String? = null, @Query("speedhomepage") speedhomepage: String? = null
                         , @Query("pageSize") pageSize: Int = Request.PAGE_SIZE,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<Page<MyDate>>>

    @POST("backstage/speedabout/find")
    fun getSpeedDetail(@Query("ids") ids: String?,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<MyDate>>

    @POST("backstage/lookabout/findByPage")
    fun getFindDateList(@Query("looktype") looktype: Int, @Query("pageNum") pageNum: Int, @Query("beginTime") beginTime: String? = null, @Query("endTime") endTime: String? = null
                        , @Query("classesid") classesid: String? = null, @Query("arraylookstate") arrayLookState: String? = null, @Query("userlookwhere") guoneiarea: String? = null
                        , @Query("userhandlookwhere") guowaiarea: String? = null, @Query("arrayuserclassesid") arrayUserClassesId: String? = null
                        , @Query("pageSize") pageSize: Int = Request.PAGE_SIZE,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<Page<MyDate>>>

    @POST("backstage/wxkf/find")
    fun searchWeChatId(@Query("kfName") kfName: String,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<JsonObject>>

    @POST("backstage/square/findByPageMySquare")
    fun getMySquares(@Query("loginuserid") loginuserid:String,@Query("userid") userid: String, @Query("limit") limit: Int, @Query("pageNum") pageNum: Int, @Query("pageSize") pageSize: Int = Request.PAGE_SIZE,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<Page<Square>>>

    @POST("backstage/upvote/add")
    fun addPraise(@Query("userid") userid: String, @Query("newsId") newsId: String?,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<JsonObject>>

    @POST("backstage/upvote/del")
    fun cancelPraise(@Query("userid") userid: String, @Query("newsId") newsId: String?,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<JsonObject>>

    @POST("backstage/selfabout/findByPage")
    fun getSelfReleaseList(@Query("pageNum") pageNum: Int, @Query("beginTime") beginTime: String? = null, @Query("endTime") endTime: String? = null
                           , @Query("guoneiarea") guoneiarea: String? = null, @Query("guowaiarea") guowaiarea: String? = null, @Query("arrayuserclassesid") arrayUserClassesId: String? = null, @Query("pageSize") pageSize: Int = Request.PAGE_SIZE,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<Page<MyDate>>>

    @POST("backstage/square/add")
    fun releaseSquare(@Query("userid") userid: String, @Query("classesid") classesid: String?, @Query("squarecity") city: String?, @Query("coverurl") coverurl: String?, @Query("content") content: String, @Query("sAppointUser")sAppointUser:String, @Query("iIsAnonymous") iIsAnonymous:Int,
                      @Query("sTopicId") sTopicId:String, @Query("sVideoUrl") sVideoUrl:String, @Query("sVideoPicUrl") sVideoPicUrl:String, @Query("sVideoWidth") sVideoWidth:String, @Query("sVideoHeight") sVideoHeight:String, @Query("sVoiceUrl") sVoiceUrl:String, @Query("sVoiceLength") sVoiceLength:String
                      ,@Query("sIfLovePics") sIfLovePics:String,@Query("sIfSeePics") sIfSeePics:String, @Query("sLoginToken")sLoginToken:String = getLoginToken(), @Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<JsonObject>>

    @POST("backstage/selfabout/add")
    fun releaseSelfAbout(@Query("userid") userid: String, @Query("content") content: String?, @Query("handlookwhere") handlookwhere: String?
                         , @Query("lookwhere") lookwhere: String?, @Query("city") city: String?, @Query("beginTime") beginTime: String?
                         , @Query("endTime") endTime: String?, @Query("selfpicurl") selfpicurl: String?,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<JsonObject>>

    @POST("backstage/userclasses/findauto")
    fun getUserLevels(@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<ArrayList<UserLevel>>>

    @POST("backstage/sysDict/findauto")
    fun getCities(@Query("paramKey") paramKey: String,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<ArrayList<City>>>

    @POST("backstage/sysDict/findautoNew")
    fun getProvince(@Query("isShow") isShow:Int,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<ArrayList<Province>>>

    @POST("backstage/sysDict/findautoAll")
    fun getProvinceAll(@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<ArrayList<Province>>>

    @POST("backstage/comments/findByPageguangchangxiaoxi")
    fun getSquareMessages(@Query("userid") userid: String, @Query("pageNum") pageNum: Int, @Query("createTime") createTime: String? = null, @Query("pageSize") pageSize: Int = Request.PAGE_SIZE,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<Page<SquareMessage>>>

    /**
     * 广场动态消息
     */
    @POST("backstage/imessage/findSquareMessageByPage")
    fun getNewSquareMessages(@Query("userid") userid: String, @Query("pageNum") pageNum: Int,@Query("pageSize") pageSize: Int = Request.PAGE_SIZE,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<Page<SquareMessage>>>

    @POST("backstage/rongcloud/gettalkdetails")
    fun getTalkDetails(@Query("fromuserid") fromuserid: String, @Query("touserid") touserid: String, @Query("checkdate") checkdate: String,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<JsonObject>>

    @POST("backstage/imessage/findByPage")
    fun getSystemMsgs(@Query("userid") userid: String, @Query("pageNum") pageNum: Int, @Query("createTime") createTime: String? = "", @Query("pageSize") pageSize: Int = Request.PAGE_SIZE,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<Page<SysMessage>>>

    @POST("backstage/account/updateumengdevicetype")
    fun updateDeviceType(@Query("userid") userid: String, @Query("devicetype") devicetype:String = "android",@Query("sLoginToken")sLoginToken:String = getLoginToken()): Flowable<Response<JsonObject>>

    @POST("backstage/new_login/getVerifyCode")
    fun getVerifyCodeV2(@Query("phone") phone: String, @Query("vercodetype") vercodetype:Int,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<JsonObject>>

    @POST("backstage/new_login/system_login")
    fun loginV2(@Query("logintype") logintype: Int, @Query("vercode") vercode:String?,@Query("phone") phone:String?=null, @Query("guoneiguowai") guoneiguowai:String?=null, @Query("openid") openid:String?=null,@Query("devicetoken") devicetoken:String?,@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<UserData>>

    @POST("backstage/new_login/system_login_new")
    fun loginV2New(@Query("logintype") logintype: Int, @Query("vercode") vercode:String?, @Query("phone") phone:String?=null, @Query("guoneiguowai") guoneiguowai:String?=null, @Query("openid") openid:String?=null, @Query("devicetoken") devicetoken:String?, @Query("sUnionid") sUnionid:String?, @Query("sChannelId") sChannelId:String?, @Query("sInviteCode")sInviteCode:String,@Query("sImei") sImei:String,@Query("sOaid") sOaid:String,@Query("sAndroidId") sAndroidId:String,@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<UserData>>


    @POST("backstage/dataDict/find")
    fun findDataDict(@Query("dataKey") dataKey:String?="quhao"): Flowable<Response<JsonPrimitive>>

    @POST("backstage/tip/add")
    fun report(@Query("userid") userid:String,@Query("tipuserid") tipuserid:String,@Query("content") content:String,@Query("tiptype") tiptype:String,@Query("sLoginToken")sLoginToken:String= getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<JsonPrimitive>>

    @POST("backstage/engagementsaccount/update")
    fun updateSeeCount(@Query("userid") userid:String,@Query("seecount") seecount:String="1",@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<JsonPrimitive>>

    @POST("backstage/engagementsaccount/findseecount")
    fun findSeeCount(@Query("userid") userid:String,@Query("sLoginToken")sLoginToken:String= getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<JsonPrimitive>>

    @POST("backstage/engagements/leiji")
    fun myDateCount(@Query("userid") userid:String,@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<JsonPrimitive>>

    @POST("backstage/engagements/leiji")
    fun dateMeCount(@Query("engagementuserid") userid:String,@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<JsonPrimitive>>

    @POST("backstage/engagements/sasamieru")
    fun getDateSuccessCount(): Flowable<Response<JsonPrimitive>>

    @POST("backstage/engagementsaccount/update")
    fun updateDateInfo(@Query("userid") userid:String,@Query("egagementtype")egagementtype:String?,@Query("egagementtext")egagementtext:String?,@Query("userhandlookwhere")userhandlookwhere:String?,@Query("userlookwhere")userlookwhere:String?,@Query("phone")phone:String?,@Query("egagementwx")egagementwx:String?,@Query("openEgagementflag")openEgagementflag:String?,@Query("sLoginToken")sLoginToken:String= getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<JsonPrimitive>>

    @POST("backstage/engagements/update")
    fun updateDateState(@Query("ids")ids:String,@Query("state")state:String,@Query("sLoginToken")sLoginToken:String= getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<JsonPrimitive>>

    @POST("backstage/engagements/add")
    fun dateUser(@Query("userid")userid:String,@Query("engagementuserid")engagementuserid:String,@Query("sLoginToken")sLoginToken:String= getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<JsonObject>>

    @POST("backstage/engagementsaccount/findByPagewoyuebieren")
    fun findMyDatingList(@Query("userid")userid:String,@Query("pageNum")pageNum:Int,@Query("pageSize")pageSize:Int=Request.PAGE_SIZE,@Query("sLoginToken")sLoginToken:String= getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<Page<NewDate>>>

    @POST("backstage/engagementsaccount/findByPage")
    fun findDatingMeList(@Query("userid")userid:String,@Query("pageNum")pageNum:Int,@Query("pageSize")pageSize:Int=Request.PAGE_SIZE,@Query("sLoginToken")sLoginToken:String= getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<Page<NewDate>>>

    @POST("backstage/engagementsaccount/findcart")
    fun getHomeDateList(@Query("userid")userid:String,@Query("sex")sex:String,@Query("egagementtype")egagementtype:Int,@Query("userlookwhere")userlookwhere:String?,@Query("userhandlookwhere")userhandlookwhere:String?,@Query("sLoginToken")sLoginToken:String= getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<ArrayList<DateBean>>>

    @POST("backstage/engagementsaccount/percent")
    fun getAuthState(@Query("userid")userid:String,@Query("sLoginToken")sLoginToken:String= getLoginToken()): Flowable<Response<JsonObject>>

    //添加关注
    @POST("backstage/follow/add")
    fun getAddFollow(@Query("userid") userid:String,@Query("followuserid") followuserid:String?,@Query("sLoginToken")sLoginToken:String= getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //取消关注
    @POST("backstage/follow/del")
    fun getDelFollow(@Query("userid") userid:String,@Query("followuserid") followuserid:String?,@Query("sLoginToken")sLoginToken:String= getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //查询我的关注,粉丝访客个数
    @POST("backstage/statistic/find")//statistic
    fun getUserFollowAndFansandVistor(@Query("userid") userid:String?,@Query("sLoginToken")sLoginToken:String= getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<FollowFansVistor>>

    @POST("backstage/follow/findMyFans")
    fun getFindMyFans(@Query("userid")userid:String,@Query("pageNum")pageNum:Int,@Query("pageSize")pageSize:Int=Request.PAGE_SIZE,@Query("sLoginToken")sLoginToken:String= getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<Page<Fans>>>

    @POST("backstage/follow/find")
    fun getFindMyFollows(@Query("userid")userid:String,@Query("pageNum")pageNum:Int,@Query("pageSize")pageSize:Int=Request.PAGE_SIZE,@Query("sLoginToken")sLoginToken:String= getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<Page<Fans>>>

    //添加访客
    @POST("backstage/vistor/add")
    fun getAddVistor(@Query("userid") userid:String,@Query("vistorid") vistorid:String?,@Query("sLoginToken")sLoginToken:String= getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //删除访客
    @POST("backstage/vistor/del")
    fun getDelVistor(@Query("userid") userid:String,@Query("vistorid") vistorid:String?,@Query("sLoginToken")sLoginToken:String= getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //查询访客记录
    @POST("backstage/vistor/find")
    fun getFindVistors(@Query("userid")userid:String,@Query("pageNum")pageNum:Int,@Query("pageSize")pageSize:Int=Request.PAGE_SIZE,@Query("sLoginToken")sLoginToken:String= getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<Page<Fans>>>

    //积分充值列表
    @POST("backstage/userpointrule/find")
    fun getUserPointsRule(@Query("sVersion") sVersion:String = getAppVersion(),@Query("sLoginToken")sLoginToken:String= getLoginToken()):Flowable<Response<ArrayList<PointRule>>>

    //获取用户消费的积分
    @POST("backstage/userpoint/find")
    fun getUserPoints(@Query("iUserid")sUserId:String,@Query("pageNum")pageNum:Int,@Query("pageSize")pageSize:Int=Request.PAGE_SIZE,@Query("sLoginToken")sLoginToken:String= getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<Page<UserPoints>>>

    //创建订单
    @POST("backstage/order/add")
    fun createOrder(@Query("iUserid") iUserid:Int?,@Query("iOrdertype") iOrdertype:Int?,@Query("iPrice") iPrice:Int?,@Query("iPoint") iPoint:Int?,@Query("sLoginToken")sLoginToken:String= getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion())

    //获取订单支付状态
    @POST("backstage/order/getOrderById")
    fun getOrderById(@Query("sOrderid") sOrderid:String?, @Query("iOrdertype") iOrdertype:Int, @Query("sLoginToken")sLoginToken:String= getLoginToken(), @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //发布约会
    @POST("backstage/appointment/add")
    fun releasePullDate(@Query("iUserid") userid: String, @Query("sPlace") sPlace: String?, @Query("sDesc") sDesc: String?
                        , @Query("iAppointType") iAppointType: Int?, @Query("iFeeType") iFeeType:Int?, @Query("dStarttime") beginTime: String?
                        , @Query("dEndtime") endTime: String?, @Query("sAppointPic") sAppointPic: String?, @Query("sAppointUser")sAppointUser:String, @Query("iIsAnonymous") iIsAnonymous:Int, @Query("sLoginToken")sLoginToken:String= getLoginToken(), @Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<JsonObject>>

    //自主约会
    @POST("backstage/appointment/findAppointmentListByPage")
    fun findAppointmentList(@Query("iUserid") userid:String, @Query("iAppointType") iAppointType:String?, @Query("sPlace") sPlace:String?,@Query("sex") sex:String, @Query("pageNum")pageNum:Int, @Query("pageSize")pageSize:Int=Request.PAGE_SIZE,@Query("sLoginToken")sLoginToken:String= getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<Page<MyAppointment>>>;

    //我的约会
    @POST("backstage/appointment/findMyAppointmentListByPage")
    fun findMyAppointmentList(@Query("iUserid")sUserId:String,@Query("pageNum")pageNum:Int,@Query("pageSize")pageSize:Int=Request.PAGE_SIZE,@Query("sLoginToken")sLoginToken:String= getLoginToken(),@Query("sVersion") sVersion:String =getAppVersion()):Flowable<Response<Page<MyAppointment>>>

    //报名约会
    @POST("backstage/appointmentsignup/add")
    fun signUpdate(@Query("iUserid") userid:String,@Query("sAppointmentId") sAppointmentId:String,@Query("sDesc") sDesc:String,@Query("sLoginToken")sLoginToken:String= getLoginToken(),@Query("sVersion") sVersion:String =getAppVersion()):Flowable<Response<JsonObject>>

    //约会详情页
    @POST("backstage/appointment/queryAppointmentDetail")
    fun getAppoinmentIdDetail(@Query("iUserid") iUserid:String,@Query("sAppointmentSignupId") sAppointmentSignupId:String,@Query("sAppointmentId") sAppointmentId:String,@Query("iShareUserid") iShareUserid:String,@Query("sLoginToken")sLoginToken:String= getLoginToken(),@Query("sVersion") sVersion:String =getAppVersion()):Flowable<Response<MyAppointment>>

    //约会状态
    @POST("backstage/appointmentsignup/updateAppointment")
    fun updateDateStatus(@Query("sAppointmentSignupId") sAppointmentSignupId:String,@Query("iStatus") iStatus:Int,@Query("sRefuseDesc") sRefuseDesc:String,@Query("sLoginToken")sLoginToken:String= getLoginToken(),@Query("sVersion") sVersion:String =getAppVersion()):Flowable<Response<JsonObject>>

    //未读取消息
    @POST("backstage/appointmentsignup/getUnreadAppointmentCount")
    fun getUnreadAppointmentCount(@Query("iUserid") userid:String,@Query("sLoginToken")sLoginToken:String= getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<UnreadMsg>>

    //人工推荐
    @POST("backstage/lookabout/findLookAboutList")
    fun findLookAboutList(@Query("iUserid") iUserid:String,@Query("sLoginToken")sLoginToken:String= getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<ArrayList<MyDate>>>

    //查询全部人工推荐
    @POST("backstage/lookabout/findAllLookAboutList")
    fun findLookAllAboutList(@Query("iUserid") iUserid:String, @Query("iLookType") iLookType:String, @Query("sPlace") splace:String, @Query("pageNum")pageNum:Int, @Query("pageSize")pageSize:Int=Request.PAGE_SIZE,@Query("sLoginToken")sLoginToken:String= getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<Page<MyDate>>>

    //添加查询约会扣除、退回、取消需要的积分接口
    @POST("backstage/appointment/queryAppointmentPoint")
    fun queryAppointmentPoint(@Query("iUserid") iUserid:String, @Query("iAppointUserid") iAppointUserid:String, @Query("sLoginToken")sLoginToken:String= getLoginToken(), @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<IntegralExplain>>

    //同城
    @POST("backstage/account/updateUserPosition")
    fun updateUserPosition(@Query("iUserid") iUserid:String,@Query("sProvince") sProvince:String,@Query("sCountry") sCountry:String, @Query("sPosition") sPosition:String, @Query("lat") lat:String, @Query("lon") lon:String, @Query("sLoginToken")sLoginToken:String= getLoginToken(), @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //发现约会
    @POST("backstage/account/findAccountCardListPage")
    fun findAccountCardListPage(@Query("iUserid") iUserid:String, @Query("sCity") scity:String,
                                @Query("sex") sex:String, @Query("userclassesid") userclassesid:String, @Query("agemin") agemin:String, @Query("agemax") agemax:String,
                                @Query("lat") lat:String, @Query("lon") lon:String,
                                @Query("pageNum")pageNum:Int, @Query("pageSize")pageSize:Int=Request.PAGE_SIZE, @Query("sLoginToken")sLoginToken:String= getLoginToken(), @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<Page<FindDate>>>

    //绑定手机号
    @POST("backstage/account/bindPhone")
    fun bindPhone(@Query("phone") phone:String, @Query("vercode") vercode:String, @Query("openid") openid:String, @Query("sUnionid") sUnionid:String, @Query("devicetoken") devicetoken:String, @Query("sWxName")sWxName:String, @Query("sWxpic")sWxpic:String, @Query("sChannelId") sChannelId:String?,@Query("sInviteCode") sInviteCode:String,@Query("sImei") sImei:String,@Query("sOaid") sOaid:String,@Query("sAndroidId") sAndroidId:String,@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<UserData>>

    //赠送积分
    @POST("backstage/new_login/loginForPointNew")
    fun loginForPointNew(@Query("sLoginToken")sLoginToken:String,@Query("iUserid") iUserid:String,@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //解锁聊天支付多少积分
    //iTalkRefusePoint 拒绝邀请返还积分    iTalkOverDuePoint 过期返还积分
    @POST("backstage/rongcloud/getUnlockTalkPoint")
    fun getUnlockTalkPoint(@Query("sLoginToken") LoginToken:String,@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //是否能聊天
    @POST("backstage/rongcloud/unlockTalk")
    fun doUnlockTalk(@Query("iUserid") iUserid:String,@Query("iTalkUserId") iTalkUserId:String,@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //是否允许聊天
    @POST("backstage/rongcloud/getTalkJustify")
    fun doTalkJustify(@Query("iFromUserid") iUserid:String,@Query("iToUserid") iTalkUserId:String,@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    /*1.8.0接口*/
    //送小红花列表
    @POST("backstage/userflowerrule/find")
    fun getUserFlowerRule(@Query("sVersion") sVersion:String =getAppVersion()):Flowable<Response<ArrayList<FlowerRule>>>

    //绑定微信
    @POST("backstage/account/bindWxid")
    fun doBindWxId(@Query("iUserid") iUserid:String,@Query("wxid") wxId:String,@Query("sWxName") sWxName:String,@Query("sWxpic") sWxpic:String,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sUnionid")sUnionid:String?="",@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //大赏小红花
    @POST("backstage/userflowerrule/sendFlowerByOrderId")
    fun sendFlowerByOrderId(@Query("iUserid") iUserid:String,@Query("iReceiveUserid") iReceiveUserid:String,@Query("sOrderid") sOrderid:String,@Query("sResourceid") sResourceid:String,@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //提现接口
    @POST("backstage/userflowerrule/withDrawFlower")
    fun doCashMoney(@Query("iUserid") iUserid:String, @Query("iFlowerCount")iFlowerCount:String,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    /*1.8.5接口*/
    //修改聊天设置接口
    @POST("backstage/account/updateTalkSetting")
    fun updateTalkSetting(@Query("iUserid") iUserid:String,@Query("iTalkSetting") iTalkSetting:Int,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //新的私聊接口  1、私聊 2、匿名组
    @POST("backstage/rongcloud/getTalkJustifyNew")
    fun doTalkJustifyNew(@Query("iFromUserid") iUserid:String,@Query("iToUserid") iToUserid:String,@Query("iType") iType:Int,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //申请私聊接口
    @POST("backstage/talkapply/apply")
    fun doApplyPrivateChat(@Query("iFromUserid") iUserid:String,@Query("iToUserid") iToUserid:String,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //同意或拒绝私聊接口
    @POST("backstage/talkapply/update")
    fun doUpdatePrivateChatStatus(@Query("iFromUserid") iFromUserid:String,@Query("iToUserid") iToUserid:String,@Query("iStatus") iStatus:String,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //获取与当前用户的私聊状态
    @POST("backstage/talkapply/getApplyStatus")
    fun getApplyStatus(@Query("iFromUserid") iFromUserid:String,@Query("iToUserid") iToUserid:String,@Query("iType") iType:Int,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    /* 1.8.2接口*/
    //判断是否允许发布约会接口
    @POST("backstage/appointment/addAppointmentAuth")
    fun getAppointmentAuth(@Query("iUserid") iUserid:String,@Query("sLoginToken")sLoginToken:String,@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //判断是否有查看访客权限
    @POST("backstage/vistor/getVistorAuth")
    fun getVistorAuth(@Query("iUserid") iUserid:String,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //支付查看访客积分
    @POST("backstage/vistor/vistorPayPoint")
    fun getVistorPayPoint(@Query("iUserid") iUserid:String,@Query("sLoginToken")sLoginToken:String,@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    /*1.9.0接口*/
    @POST("backstage/version/getByVersion")
    fun getByVersion(@Query("sVersion") sVersion:String,@Query("iType") iType:String):Flowable<Response<VersionBean>>

    /*1.9.1接口*/
    @POST("backstage/blacklist/add") //1、匿名  2、非匿名状态
    fun addBlackList(@Query("iUserid") userid:String, @Query("iBlackUserid") blackuserid:String, @Query("iType") iType:Int,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<JsonPrimitive>>

    //获取黑名单列表
    @POST("backstage/blacklist/find")
    fun getFindMyBlacklist(@Query("iUserid")userid:String,@Query("pageNum")pageNum:Int,@Query("pageSize")pageSize:Int=Request.PAGE_SIZE,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<Page<BlackListBean>>>

    //移除黑名单
    @POST("backstage/blacklist/del")
    fun delBlackList(@Query("sId") sId:String,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<JsonPrimitive>>

    /*1.9.2接口*/
    @POST("backstage/account/updateUnionId")
    fun updateUnionId(@Query("iUserid") iUserid:String,@Query("sOpenId")sOpenId:String,@Query("sUnionid")sUnionid:String,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>


    /* 2.0接口*/
    @POST("backstage/blacklist/remove")
    fun removeBlackList(@Query("iUserid") userid:String,@Query("iBlackUserid") blackuserid:String,@Query("iType") iType:Int,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<JsonPrimitive>>

    //查询好友列表
    @POST("backstage/userfriend/findByPage")
    fun findUserFriends(@Query("iUserid")userid:String,@Query("sUserName") sUserName:String,@Query("pageNum")pageNum:Int,@Query("pageSize")pageSize:Int=Request.PAGE_SIZE,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<Page<FriendBean>>>

    @POST("backstage/userfriend/findByPageNew")
    fun findUserFriends_New(@Query("sUserName") sUserName:String,@Query("pageNum")pageNum:Int,@Query("pageSize")pageSize:Int=Request.PAGE_SIZE,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<Page<FriendBean>>>

    //动态消息设置
    @POST("backstage/account/updateMessageSetting")
    fun updateMessageSetting(@Query("iUserid") userid:String,@Query("iMessageSetting") iMessageSetting:Int,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonPrimitive>>

    //iShowInviteMsg 1、推送 2、不推送
    @POST("backstage/account/updateShowInviteMsg")
    fun updateInviteMessageSetting(@Query("iShowInviteMsg") iMessageSetting:Int,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonPrimitive>>

    //查询用户接口
    @POST("backstage/account/findAllByPage")
    fun findAllUserFriends(@Query("iUserid")userid:String,@Query("sUserName")sUserName:String,@Query("pageNum")pageNum:Int,@Query("pageSize")pageSize:Int=Request.PAGE_SIZE,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<Page<UserData>>>

    //我的好友个数
    @POST("backstage/userfriend/findFriendCount")
    fun findFriendCount(@Query("iUserid")userid:String,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //分享的接口
    @POST("backstage/share/shareMessage")
    fun shareMessage(@Query("iUserid")userid:String,@Query("iType") iType:Int,@Query("sResourceId") sResourceId:String,@Query("sAppointUser")sAppointUser:String,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonPrimitive>>

    //会话类型1、咨询会员 2、咨询认证 3、觅约人工推荐 4、速约人工推荐 5、在线客服 6、提现联系客服 7、游客离开会员页 8、会员联系客服 9、注册发送客服消息
    @POST("backstage/push/pushCustomerMessage")
    fun pushCustomerMessage(@Query("sLoginToken")sLoginToken:String,@Query("iUserid")iUserid:String,@Query("iType") iType:Int,@Query("sResourceId")sResourceId:String,@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonPrimitive>>

    /*2.1.0接口*/
    //获取等级接口
    @POST("backstage/userclasses/findUserClasses")
    fun findUserClasses(@Query("sLoginToken")sLoginToken:String,@Query("sAreaName") sAreaName:String, @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<ListMemeberBean<MemberBean>>>

    //删除约会
    @POST("backstage/appointment/delAppointment")
    fun delAppointment(@Query("sLoginToken")sLoginToken:String,@Query("sAppointmentId") sAppointmentId:String,@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonPrimitive>>

    //会员约会地区选择
    @POST("backstage/sysDict/findautoAll")
    fun getProvinceAllOfMember(@Query("sType") sType:String,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<ArrayList<Province>>>

    /*2.2.0接口*/
    //查询约会和动态匿名剩余次数接口 iType   类型 1、约会 2、动态
    @POST("backstage/appointment/anonymousAppointmentPoint")
    fun getAnonymouseAppointmentPoint(@Query("sLoginToken")sLoginToken:String,@Query("iType") iType:Int,@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //查询是否开启匿名卡片
    @POST("backstage/account/userQueryAnonymous")
    fun getUserQueryAnonymous(@Query("sLoginToken")sLoginToken:String,@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //查询匿名需要支付的积分
    @POST("backstage/account/queryAnonymousPoint")
    fun getQueryAnonymous(@Query("sLoginToken")sLoginToken:String,@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //开通匿名卡片支付积分
    @POST("backstage/account/anonymousPayPoint")
    fun getAnonymousPayPoint(@Query("sLoginToken")sLoginToken:String,@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //查询用户的匿名卡片详情
    @POST("backstage/account/getAnonymousAccountDetail")
    fun getAnonymousAccountDetail(@Query("sLoginToken")sLoginToken:String,@Query("iUserid")iUserid:String,@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<UserData>>

    //创建匿名组接口 iType 1、我是匿名  2、对方是匿名
    @POST("backstage/group/add")
    fun CreateGroupAdd(@Query("sLoginToken")sLoginToken:String,@Query("iTalkUserid") iTalkUserid:String,@Query("iType") iType:Int,@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //跳转到匿名用户组（先判断是否已创建匿名组，没有创建则手动创建 iType 1、我是匿名  2、对方是匿名
    @POST("backstage/group/toUserAnonymousGroup")
    fun doToUserAnonyMousGroup(@Query("sLoginToken")sLoginToken:String,@Query("iTalkUserid") iTalkUserid:String,@Query("iType") iType:Int,@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<GroupBean>>

    //获取当前组的成员
    @POST("backstage/group/findGroupMembersByGroupId")
    fun findGroupMembersByGroupId(@Query("sLoginToken")sLoginToken:String,@Query("sGroupId") sGroupId:String,@Query("sVersion") sVersion:String = getAppVersion())

    //查询组的信息，返回组的名称和图片（已区分是否匿名）
    @POST("backstage/group/findGroupByGroupid")
    fun findGroupDescByGroupId(@Query("sLoginToken")sLoginToken:String,@Query("sGroupId") sGroupId:String,@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<GroupBean>>

    //密约详情页
    @POST("backstage/lookabout/find")
    fun getLookDateDetail(@Query("ids") ids: String?,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<MyDate>>

    //2.5.0
    //申请私聊接口
    @POST("backstage/talkapply/applyNew")
    fun doApplyNewPrivateChat(@Query("sLoginToken")sLoginToken:String,@Query("iToUserid") iToUserid:String,@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //2.7.0
    //获取推荐页的信息
    @POST("backstage/account/getAccountInviteLink")
    fun getAccountInviteLink(@Query("sLoginToken")sLoginToken:String,@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<InviteLinkBean>>


    //2.8.0
    //签到
    @POST("backstage/new_login/signPoint")
    fun signPoint(@Query("sLoginToken")sLoginToken:String,@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //修改在线状态 1、不在线 2、在线
    @POST("backstage/account/updateUserOnline")
    fun updateUserOnline(@Query("sLoginToken")sLoginToken:String, @Query("iOnline") iOnline:Int, @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //1、检查客服是否在线
    @POST("backstage/account/checkUserOnline")
    fun checkUserOnline(@Query("sLoginToken")sLoginToken:String, @Query("iUserid") iUserid:String, @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //5、查询当日任务和完成情况
    @POST("backstage/userpoint/findTodayTask")
    fun findUserPointStatus(@Query("sLoginToken")sLoginToken:String,@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<TaskList<TaskBean>>>

    //2.9.0
    //话题列表
    @POST("backstage/basetopic/findTopicListByPage")
    fun findTopicListByPage(@Query("sLoginToken")sLoginToken:String,@Query("pageNum") pageNum: Int = 1,@Query("pageSize") pageSize: Int = 10,@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<Page<TopicBean>>>

    @POST("backstage/basetopic/findTopicBannerList")
    fun findTopicBannerList(@Query("sLoginToken")sLoginToken:String):Flowable<Response<TopicList<TopicBean>>>

    //动态查询
    @POST("backstage/square/findByPage")
    fun getFindSquareList(@Query("userid") accountId: String, @Query("classesid") classesid: String?, @Query("pageNum") pageNum: Int, @Query("pageSize") pageSize: Int = Request.PAGE_SIZE, @Query("limit") limit: Int = 0, @Query("sex") sex: Int = 2, @Query("sTopicId") sTopicId:String, @Query("sCity") sCity:String, @Query("sLoginToken")sLoginToken:String = getLoginToken(), @Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<Page<Square>>>

    //2.10.0接口
    @POST("backstage/userclasses/find")
    fun findYKUserClasses(@Query("ids") ids:String,@Query("sLoginToken")sLoginToken:String,@Query("sAreaName") sAreaName:String, @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<MemberBean>>

    //2.11.0接口
    @POST("backstage/userloverule/find")
    fun findUserLoveRule(@Query("sLoginToken")sLoginToken:String,@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<ArrayList<LoveHeartRule>>>

    //提现爱心接口
    @POST("backstage/userloverule/withDrawLovePoint")
    fun doCashMoneyOfLoveHeart(@Query("iUserid") iUserid:String, @Query("iLovePoint") iLovePoint:String,@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //赠送红心 iType：赠送的入口 1、动态 2、卡片 3、主页 4、聊天
    @POST("backstage/userloverule/sendLovePoint")
    fun sendLovePoint(@Query("sLoginToken")sLoginToken:String, @Query("iReceiveUserid") iReceiveUserid:String, @Query("iLovePoint") iLovePoint:Int, @Query("iType") iType:Int, @Query("sResourceid")sResourceid:String, @Query("sPicUrl") sPicUrl:String, @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //查询我发送的爱心列表
    @POST("backstage/userloverule/findSendLoveList")
    fun findSendLoveList(@Query("sLoginToken")sLoginToken:String,@Query("pageNum") pageNum: Int = 1,@Query("pageSize") pageSize: Int = 20,@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<Page<LoveHeartFans>>>

    //查询收到的爱心列表
    @POST("backstage/userloverule/findReceiveLoveList")
    fun findReceiveLoveList(@Query("sLoginToken")sLoginToken:String,@Query("pageNum") pageNum: Int = 1,@Query("pageSize") pageSize: Int = 20,@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<Page<LoveHeartFans>>>

    //查询打码用户资料需要支付的积分数量
    @POST("backstage/userloverule/getLovePointQueryAuth")
    fun getLovePointQueryAuth(@Query("sLoginToken")sLoginToken:String,@Query("iUserid") iUserid:String,@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //支付积分查看打码用户
    @POST("backstage/userloverule/loveUserQueryPayPoint")
    fun loveUserQueryPayPoint(@Query("sLoginToken")sLoginToken:String,@Query("iUserid") iUserid:String,@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //2.12.0
    //更新用户卡片是否允许发现中查看
    @POST("backstage/account/updateCardIsFind")
    fun updateCardIsFind(@Query("sLoginToken")sLoginToken:String, @Query("iIsFind")iIsFind:Int, @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //3.0.0
    @POST("backstage/userloverule/findReceiveLoveListByUserid")
    fun findReceiveLoveHeartList(@Query("iUserid") iUserid:String,@Query("sLoginToken")sLoginToken:String,@Query("pageNum") pageNum: Int = 1,@Query("pageSize") pageSize: Int = 20,@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<Page<LoveHeartFans>>>

    //更新用户是否显示在榜单中
    @POST("backstage/account/updateListSetting")
    fun updateListSetting(@Query("sLoginToken")sLoginToken:String, @Query("iListSetting") iListSetting:Int, @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    @POST("backstage/account/updateSendPointShow")
    fun updateSendPointShow(@Query("sLoginToken")sLoginToken:String, @Query("iSendPointShow") iSendPointShow:Int, @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    @POST("backstage/userloverule/findLoveListing")
    fun findLoveListing(@Query("sLoginToken")sLoginToken:String, @Query("iSex") iSex:Int,@Query("pageNum") pageNum: Int = 1,@Query("pageSize") pageSize: Int = 20,@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<Page<LoveHeartFans>>>

    //混合流广场顶部数据
    @POST("backstage/square/findSquareTop")
    fun findSquareTop(@Query("sLoginToken")sLoginToken:String = getLoginToken(),@Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //3.1.0
    // 连麦类型 1、无需打赏 2、申请者打赏 3、发布者打赏  iAppointType 8 连麦
    @POST("backstage/appointment/add")
    fun addConnectVoice(@Query("iUserid") userid: String,@Query("sDesc") sDesc: String, @Query("iAppointType") iAppointType:Int, @Query("iVoiceConnectType") iVoiceConnectType:Int, @Query("iPrepayLovepoint") iPrepayLovepoint:Int, @Query("iOncePayLovePoint") iOncePayLovePoint:Int,@Query("sAppointPic") sAppointPic: String?,@Query("dStarttime") beginTime: String, @Query("dEndtime") endTime: String?, @Query("sLoginToken")sLoginToken:String = getLoginToken(), @Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<JsonObject>>

    //查询是否允许连麦
    @POST("backstage/squaresignup/getApplyVoiceSquareLovePoint")
    fun getApplyVoiceSquareLovePoint(@Query("sAppointmentId") sAppointmentId:String,@Query("sLoginToken")sLoginToken:String = getLoginToken(), @Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<JsonObject>>

    //报名连麦
    @POST("backstage/squaresignup/add")
    fun addVoiceChat(@Query("sAppointmentId") sAppointmentId:String,@Query("sLoginToken")sLoginToken:String = getLoginToken(), @Query("sVersion") sVersion:String = getAppVersion()): Flowable<Response<JsonObject>>

    //连麦申请状态
    //状态 1、发起 2、同意 3、拒绝  4、主动取消  5、过期自动取消 6、完成 7,每分钟
    @POST("backstage/squaresignup/updateSquareSignUp")
    fun updateSquareSignUp(@Query("sAppointmentSignupId") sAppointmentSignupId:String,@Query("iStatus") iStatus:String,@Query("iConnectVoiceLength")iConnectVoiceLength:Long,@Query("sLoginToken")sLoginToken:String = getLoginToken(), @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    @POST("backstage/userevent/findByPage")
    fun getInviteFindByPage(@Query("pageNum")pageNum:Int,@Query("pageSize")pageSize:Int=Request.PAGE_SIZE,@Query("sLoginToken")sLoginToken:String = getLoginToken(), @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<Page<InviteUserBean>>>

    @POST("backstage/userevent/findEventListByUserId")
    fun findEventListByUserId(@Query("iUserid") iUserid:String,@Query("sLoginToken")sLoginToken:String = getLoginToken(), @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<InviteUserMemberInfo>>

    //重要消息短信提醒
    @POST("backstage/account/updatePhoneSetting")
    fun updatePhoneSetting(@Query("iPhoneSetting") iPhoneSetting:Int, @Query("sLoginToken")sLoginToken:String = getLoginToken(), @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    @POST("backstage/account/updateLookaboutSetting")
    fun updateLookaboutSetting(@Query("iLookaboutSetting") iPhoneSetting:Int, @Query("sLoginToken")sLoginToken:String = getLoginToken(), @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    @POST("backstage/appointmentsignup/updateProgress")
    fun updateProgress(@Query("sAppointmentSignupId") sAppointmentSignupId:String,@Query("iProgress")iProgress:Int,@Query("sLoginToken")sLoginToken:String = getLoginToken(), @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //3.4版本
    @POST("backstage/rongcloudgroup/createRongCloudGroup")
    fun createRongCloudGroup(@Query("sGroupName") sGroupName:String,@Query("sGroupPic")sGroupPic:String,@Query("sGroupDesc")sGroupDesc:String,@Query("sLoginToken")sLoginToken:String = getLoginToken(), @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<NewGroupBean>>

    //解散群
    @POST("backstage/rongcloudgroup/dissGroup")
    fun dissGroup(@Query("sGroupId") sGroupId:String,@Query("sLoginToken")sLoginToken:String = getLoginToken(), @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>
    //退出群组
    @POST("backstage/rongcloudgroup/quiteGroup")
    fun quiteGroup(@Query("sGroupId") sGroupId:String,@Query("sLoginToken")sLoginToken:String = getLoginToken(), @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //把用户踢出群组
    @POST("backstage/rongcloudgroup/kickGroup")
    fun kickGroup(@Query("sGroupId") sGroupId:String,@Query("iUserid") iUserid:String,@Query("sLoginToken")sLoginToken:String = getLoginToken(), @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //申请加入群
    @POST("backstage/rongcloudgroup/applyToGroup")
    fun applyToGroup(@Query("sGroupId") sGroupId:String,@Query("sApplyDesc") sApplyDesc:String,@Query("sLoginToken")sLoginToken:String = getLoginToken(), @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //同意或拒绝加入群组
    // sApplyId：申请的id
    //iStatus：状态 2、同意 3、拒绝
    @POST("backstage/rongcloudgroup/confirmToGroup")
    fun confirmToGroup(@Query("sApplyId") sApplyId:String, @Query("iStatus") iStatus:String, @Query("sLoginToken")sLoginToken:String = getLoginToken(), @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //根据群的编号查询群组
    @POST("backstage/rongcloudgroup/getGroupByGroupNum")
    fun getGroupByGroupNum(@Query("iGroupNum") iGroupNum:String, @Query("sLoginToken")sLoginToken:String = getLoginToken(), @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<NewGroupBean>>

    //根据组的id查询组的详细信息
    @POST("backstage/rongcloudgroup/getGroupByGroupId")
    fun getGroupByGroupId(@Query("sGroupId") iGroupNum:String, @Query("sLoginToken")sLoginToken:String = getLoginToken(), @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<NewGroupBean>>

    //查询我的群组
    @POST("backstage/rongcloudgroup/getMyGrouList")
    fun getMyGroupList(@Query("pageNum")pageNum:Int,@Query("pageSize")pageSize:Int=200,@Query("sLoginToken")sLoginToken:String = getLoginToken(), @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<Page<NewGroupBean>>>

    //根据组的id查询组的成员信息
    @POST("backstage/rongcloudgroup/getGroupMemberListByGroupId")
    fun getGroupMemberListByGroupId(@Query("sGroupId") sGroupId:String, @Query("sUserName") sUserName:String,@Query("pageNum")pageNum:Int, @Query("pageSize")pageSize:Int=Request.PAGE_SIZE, @Query("sLoginToken")sLoginToken:String = getLoginToken(), @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<Page<GroupUserBean>>>

    @POST("backstage/rongcloudgroup/getGroupMemberListByGroupId")
    fun getGroupAllMemberListByGroupId(@Query("sGroupId") sGroupId:String,@Query("pageNum")pageNum:Int,@Query("pageSize")pageSize:Int=200,@Query("sLoginToken")sLoginToken:String = getLoginToken(), @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<Page<GroupUserBean>>>

    @POST("backstage/rongcloudgroup/updateUserGroupManager")
    fun updateUserGroupManager(@Query("sGroupId") sGroupId:String,@Query("iUserid") iUserid:String,@Query("iIsManager")iIsManager:Int,@Query("sLoginToken")sLoginToken:String = getLoginToken(), @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    @POST("backstage/rongcloudgroup/updateMemberNotification")
    fun updateMemberNotification(@Query("sGroupId") sGroupId:String,@Query("iIsNotification")iIsNotification:Int,@Query("sLoginToken")sLoginToken:String = getLoginToken(), @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //修改群组的信息
    @POST("backstage/rongcloudgroup/updateGroup")
    fun updateGroup(@Query("sGroupId") sGroupId:String,@Query("sGroupName")sGroupName:String,@Query("sGroupPic")sGroupPic:String,@Query("sGroupDesc")sGroupDesc:String,@Query("sLoginToken")sLoginToken:String = getLoginToken(), @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //3.5版本
    //2、开启或关闭通讯录隐私保护
    @POST("backstage/account/updatePhonePrivacy")
    fun updatePhonePrivacy(@Query("iPhonePrivacy") iPhonePrivacy:String,@Query("sPhoneList") sPhoneList:String,@Query("sLoginToken")sLoginToken:String = getLoginToken(), @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //3.6版本
    @POST("backstage/userfriend/deleteFriend")
    fun deleteFriend(@Query("iFriendUserid") iFriendUserid:String,@Query("sLoginToken")sLoginToken:String = getLoginToken(), @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>

    //iType 1、群  2、好友
    //sId  群组id或好友的id
    //sGroupName 群组或好友的名称
    //sGroupPic 群组或好友的头像
    @POST("backstage/rongcloudgroup/getMyGrouListAndFriendList")
    fun getMyGrouListAndFriendList(@Query("pageNum")pageNum:Int,@Query("pageSize")pageSize:Int=10,@Query("sLoginToken")sLoginToken:String = getLoginToken(), @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<Page<NewGroupBean>>>

    //3.7版本
    @POST("backstage/square/insertUserVisitPic")
    fun insertUserVisitPic(@Query("sPicUrl") sPicUrl:String,@Query("sLoginToken")sLoginToken:String = getLoginToken(), @Query("sVersion") sVersion:String = getAppVersion()):Flowable<Response<JsonObject>>
}