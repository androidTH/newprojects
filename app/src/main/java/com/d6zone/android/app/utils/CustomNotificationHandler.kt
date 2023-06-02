package com.d6zone.android.app.utils

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import com.d6zone.android.app.activities.*
import com.d6zone.android.app.base.BaseActivity
import com.umeng.message.UmengNotificationClickHandler
import com.umeng.message.entity.UMessage
import org.jetbrains.anko.startActivity

// 使用自定义的NotificationHandler，来结合友盟统计处理消息通知
// 参考http://bbs.umeng.com/thread-11112-1-1.html
class  CustomNotificationHandler: UmengNotificationClickHandler() {

    override fun dismissNotification(context: Context?, msg: UMessage?) {
        super.dismissNotification(context, msg)
//        Toast.makeText(context, "dismission+${msg!!.extra["type"]}", Toast.LENGTH_LONG).show()
    }

    override fun launchApp(context: Context, msg: UMessage?) {
        super.launchApp(context, msg)
    }

    override fun openActivity(context: Context, msg: UMessage) {
        super.openActivity(context, msg)
        var type = msg.extra.get("type");
        if(type.equals("14")){
            context!!.sendBroadcast( Intent(Const.YOUMENG_MSG_NOTIFION_MINE));
        }
        Log.i(TAG,"success=${msg.extra.get("type")}");
    }

    fun open(context:Context,type:String){
        (context as BaseActivity).startActivity<MyDateActivity>("type" to type)
    }

    override fun openUrl(context: Context, msg: UMessage) {
        super.openUrl(context, msg)
    }

    override fun dealWithCustomAction(context: Context?, msg: UMessage?) {
        super.dealWithCustomAction(context, msg)
//        Toast.makeText(context, "dealWithCustomAction+${msg!!.extra["type"]}", Toast.LENGTH_LONG).show()
        handleCustomAction(context, msg)
    }

    override fun autoUpdate(context: Context, msg: UMessage?) {
        super.autoUpdate(context, msg)
    }

    private fun handleCustomAction(context: Context?, msg: UMessage?) {
        if (msg != null && !TextUtils.isEmpty(msg.custom)) {
            //                mMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//                            PushMessageResponse response = GsonHelper.GsonToBean(msg.custom, PushMessageResponse.class);
            //                PushMessageResponse response = mMapper.readValue(msg.custom, PushMessageResponse.class);
            //                int type = response.getCode();
            val type = msg.extra["type"]
            Log.i(TAG,"handleCustomAction=${msg.extra.get("type")}");
            if (TextUtils.equals(type,"1")) {
                (context as BaseActivity).startActivity<MyPointsActivity>()
            } else if (TextUtils.equals(type,"2")) {
                (context as BaseActivity).startActivity<MyDateActivity>()
            }else if (TextUtils.equals(type,"8")){

            }else if(TextUtils.equals(type,"9")){

            }else if(TextUtils.equals(type,"10")||TextUtils.equals(type,"12")){//邀请用户
                (context as BaseActivity).startActivity<InviteGoodFriendsActivity>()
            }else if(TextUtils.equals(type,"11")){
                (context as BaseActivity).startActivity<FansActivity>()
            }else if(TextUtils.equals(type,"13")){
                //跳转到动态详情页面
                (context as BaseActivity).startActivity<SquareTrendDetailActivity>("id" to "${13}")
            }
        }
    }

    companion object {

        private val TAG = CustomNotificationHandler::class.java.name
        val MSG_TYPE_HOME = 1001
        val MSG_TYPE_NEWS_DETAILS = 1101
        val MSG_TYPE_THEME = 1102
    }

    //    private Intent getNewsDetailsIntent(Context context, PushMessageResponse response) {
    //        Intent intent = new Intent(context, NewsDetailsSecActivity.class);
    //        intent.putExtra("video_id", response.getData().getVideo_id());
    //        intent.putExtra("push_id", response.getPush_id() + "");
    //        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    //        return intent;
    //    }
    //
    //    private Intent getHtmlNewsDetailsIntent(Context context, PushMessageResponse response) {
    //        Intent intent = new Intent(context, WebEventActivity.class);
    //        WebEventResponse.DataBean bean = new WebEventResponse.DataBean();
    //        bean.setPage_url(response.getData().getH5_url());
    //        bean.setPage_title(response.getData().getTitle());
    //        Bundle bundle = new Bundle();
    //        bundle.putSerializable(StaticParam.INTENT_EXTRAS, bean);
    //        bundle.putString(StaticParam.VIDEO_ID, response.getData().getVideo_id());
    //        intent.putExtras(bundle);
    //        return intent;
    //    }
    //
    //    private Intent getThemeIntent(Context context, PushMessageResponse response) {
    //        Intent intent = new Intent(context, ThemListActivity.class);
    //        intent.putExtra("them_id", response.getData().getSpecial_id());
    //        intent.putExtra("push_id", response.getPush_id() + "");
    //        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    //        return intent;
    //    }
    //
    //    private Intent getHomeIntent(Context context, PushMessageResponse response) {
    //        Intent intent = new Intent(context, HomeActivity.class);
    //        intent.putExtra("push_id", response.getPush_id() + "");
    //        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    //        return intent;
    //    }
}
