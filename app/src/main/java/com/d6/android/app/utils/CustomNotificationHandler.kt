package com.d6.android.app.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils

import com.umeng.analytics.MobclickAgent
import com.umeng.message.UmengNotificationClickHandler
import com.umeng.message.common.UmLog
import com.umeng.message.entity.UMessage

import java.util.HashMap

// 使用自定义的NotificationHandler，来结合友盟统计处理消息通知
// 参考http://bbs.umeng.com/thread-11112-1-1.html
class  CustomNotificationHandler: UmengNotificationClickHandler() {

    override fun dismissNotification(context: Context?, msg: UMessage?) {
        UmLog.d(TAG, "dismissNotification")
        super.dismissNotification(context, msg)
        MobclickAgent.onEvent(context, "dismiss_notification")
    }

    override fun launchApp(context: Context, msg: UMessage?) {
        UmLog.d(TAG, "launchApp")
        super.launchApp(context, msg)
        val map = HashMap<String, String>()
        map["action"] = "launch_app"
        MobclickAgent.onEvent(context, "click_notification", map)
    }

    override fun openActivity(context: Context, msg: UMessage) {
        UmLog.d(TAG, "openActivity")
        super.openActivity(context, msg)
        val map = HashMap<String, String>()
        map["action"] = "open_activity"
        MobclickAgent.onEvent(context, "click_notification", map)
    }

    override fun openUrl(context: Context, msg: UMessage) {
        UmLog.d(TAG, "openUrl")
        super.openUrl(context, msg)
        val map = HashMap<String, String>()
        map["action"] = "open_url"
        MobclickAgent.onEvent(context, "click_notification", map)
    }

    override fun dealWithCustomAction(context: Context?, msg: UMessage?) {
        UmLog.d(TAG, "dealWithCustomAction")
        handleCustomAction(context, msg)
        val map = HashMap<String, String>()
        map["action"] = "custom_action"
        MobclickAgent.onEvent(context, "click_notification", map)
    }

    override fun autoUpdate(context: Context, msg: UMessage?) {
        UmLog.d(TAG, "autoUpdate")
        super.autoUpdate(context, msg)
        val map = HashMap<String, String>()
        map["action"] = "auto_update"
        MobclickAgent.onEvent(context, "click_notification", map)
    }

    private fun handleCustomAction(context: Context?, msg: UMessage?) {
        if (msg != null && !TextUtils.isEmpty(msg.custom)) {
            //                mMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            //                PushMessageResponse response = GsonHelper.GsonToBean(msg.custom, PushMessageResponse.class);
            //                PushMessageResponse response = mMapper.readValue(msg.custom, PushMessageResponse.class);
            //                int type = response.getCode();
            val type = 0
            val intent: Intent? = null
            if (type == MSG_TYPE_NEWS_DETAILS) {
                //                    if(response.getData().getVideo_type() == 9){
                //                        intent = getHtmlNewsDetailsIntent(context, response);
                //                    }else {
                //                        intent = getNewsDetailsIntent(context, response);
                //                    }
            } else if (type == MSG_TYPE_THEME) {
                //                    intent = getThemeIntent(context, response);
            } else {
                //                    intent = getHomeIntent(context, response);
            }
            context!!.startActivity(intent)
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
